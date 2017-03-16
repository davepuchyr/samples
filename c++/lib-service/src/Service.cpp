#include "fmt/format.h"
#include "lib-common/git_sha1.h"
#include "lib-constants-service/git_sha1.h"
#include "lib-constants-service/Registry.h"
#include "lib-constants-service/Service.h"
#include "lib-json/git_sha1.h"
#include "lib-service/git_sha1.h"
#include "lib-service/Registry.h"
#include "lib-service/Service.h"
#include "lib-ws/git_sha1.h"
#include <condition_variable>
#include <csignal>
#include <iomanip>

#undef CALLBACK // NOTE: avoid preprocessor name collision
#undef ECHO     // NOTE: avoid preprocessor name collision

#if 0
#  define NOW     std::chrono::high_resolution_clock::now
#  define MARK_T0 auto __t0 = NOW()
#  define MARK_T( s )  std::chrono::duration<double, std::milli> __dt = NOW() - __t0; log_->debug( fmt::format( "{} __dt == {}ms", s, __dt.count() ) )
#  define TRACE( s ) log_->trace( s, __FILE__, __LINE__ )
#else
#  define NOW
#  define MARK_T0
#  define MARK_T
#  define TRACE
#endif

namespace lib {
namespace service {

using namespace lib::constants::Registry;
using namespace lib::constants::Service;


template<typename T>
Service<T>::Service( std::shared_ptr<lib::common::Logger> logger, std::shared_ptr<T> controller, std::string proc_git_sha1 )
: controller_{ controller }
, log_{ logger }
, context_{ 1 } // HARD-CODED
{
   SIGSERVICE_ = this;

   static std::map<int, __sighandler_t> handlers;

   auto sighandler = []( int signum ) {
      std::thread( [signum]() { // shutdown on a thread other than main's thread since controller_ uses main's thread
         auto handler = handlers[signum];

         if ( handler ) handler( signum );

         if ( SIGSERVICE_ ) SIGSERVICE_->shutdown();
      } ).detach();
   };

   for ( auto signum : { SIGHUP, SIGINT, SIGKILL, SIGQUIT, SIGTERM } ) {
      auto prev_handler = std::signal( signum, sighandler );

      if ( prev_handler == SIG_ERR ) {
         log_->error( fmt::format( "Got SIG_ERR on trying to trap {0} - don't use kill -{0} or the registry will be polluted!", signum ) ); //! NOTE: SIGKILL will always leave this in the registry, so emit the error as a reminder not to try to stop this with SIGKILL.
      } else if ( prev_handler ) {
         handlers[signum] = prev_handler;
      }
   }

   info_[HOST] = lib::common::Host::name();
   info_[PROC] = lib::common::Host::proc();
   info_[PID]  = getpid();

   // HARD-CODED
   info_[PROC + "::git_sha1"]                 = proc_git_sha1;
   info_["lib::common::git_sha1"]             = lib::common::git_sha1();
   info_["lib::constants::service::git_sha1"] = lib::constants::service::git_sha1();
   info_["lib::json::git_sha1"]               = lib::json::git_sha1();
   info_["lib::service::git_sha1"]            = lib::service::git_sha1();
   info_["lib::ws::git_sha1"]                 = lib::ws::git_sha1();
}


template<typename T>
Service<T>::~Service() {
   if ( SIGSERVICE_ ) shutdown();
}


template<typename T>
void Service<T>::onClose( std::shared_ptr<typename T::Connection> connection, int status, const std::string& reason ) {
   log_->info( fmt::format( "Connection {} closed; status == {}{}.", (size_t) connection.get(), status, reason.length() ? fmt::format( "; reason == '{}'", reason ) : "" ) );
}


//!
//! Handles a network error.
//!
template<typename T>
void Service<T>::onError( std::shared_ptr<typename T::Connection> connection, const boost::system::error_code& ec ) {
   log_->error( fmt::format( "Connection {} error; ec.message == {}.", (size_t) connection.get(), ec.message() ) );
}


//!
//! Handles (parsed) JSON that was sent to controller_.
//!
//! \param[in] connection controller_'s connection
//! \param[in] js a JSON object with at least lib::service::ACTION and lib::service::{FAILURE,SUCCESS} properties
//! \param[in] o lib::json::JSON representation of js
//!
template<typename T>
void Service<T>::onMessage( std::shared_ptr<typename T::Connection> connection, std::string& js, std::shared_ptr<lib::json::JSON> o ) {
   std::string response;

   try {
      std::string action = (*o)[ACTION];

      if ( action == ECHO ) {
         response = js;
      } else if ( action == INFO ) {
         lib::json::JSON json = info_;

         json[CALLBACK] = (*o)[SUCCESS];

         response = json.dump();
      } else if ( action == PING ) {
         lib::json::JSON json;

         json[CALLBACK] = (*o)[SUCCESS];
         json[PING] = PONG;

         response = json.dump();
      } else if ( action == SHUTDOWN ) {
         int pid = findMandatoryProperty( o, PID );

         lib::json::JSON json;

         if ( pid == info_[PID] ) {
            json[CALLBACK] = (*o)[SUCCESS];

            // this is caught by sighandler, but makes debugging a pain since boost's io also apparently handles the signal: std::raise( SIGINT );
            std::thread( [this]() { // shutdown on a thread other than main's thread since controller_ uses main's thread
               shutdown();
            } ).detach();
         } else {
            json[CALLBACK] = (*o)[FAILURE];
            json[ERROR] = fmt::format( "Supplied pid {} does not match service's pid.", pid );
         }

         response = json.dump();
      } else {
         throw std::runtime_error( fmt::format( "Unknown {} '{}'", ACTION, action ) );
      }
   } catch ( std::exception& e ) {
      return onError( connection, js, e.what() ); // short-circuit
   }

   send( connection, response );
}


template<typename T>
void Service<T>::onOpen( std::shared_ptr<typename T::Connection> connection ) {
  log_->info( fmt::format( "Connection {} opened.", (size_t) connection.get() ) );
}


//!
//! Starts controller_, which blocks, using a HARD-CODED endpoint for controller_.  controller_ expects messages each of a single JSON
//! object with properties ACTION and CALLBACK.  It returns a JSON object with CALLBACK equal to the incoming message's CALLBACK.
//!
template<typename T>
void Service<T>::run( std::function<void()> onaccept ) {
   auto& endpoint = controller_->endpoint["^/$"]; //! HARD-CODED endpoint "/", ie not "/echo" or similar.

   endpoint.onmessage = [this]( std::shared_ptr<typename T::Connection> connection, std::shared_ptr<typename T::Message> message ) {
      auto js = message->string();

      try {
         if ( js[0] != '{' || js[js.size() - 1] != '}' ) {
            throw std::invalid_argument( fmt::format( "{}:{} expects a JSON object without leading/trailing whitespace", __FILE__, __LINE__ ) ); // HARD-CODED in conjunction with onMessage.
         }

         if ( js.find( ACTION ) == std::string::npos || js.find( SUCCESS ) == std::string::npos || js.find( FAILURE ) == std::string::npos ) {
            throw std::invalid_argument( fmt::format( "{}:{} expects properties Service::ACTION ({}) and Service::SUCCESS ({}) and Service::FAILURE ({})", __FILE__, __LINE__, ACTION, SUCCESS , FAILURE) ); // HARD-CODED in conjunction with onMessage.
         }

         std::shared_ptr<lib::json::JSON> o = std::make_shared<lib::json::JSON>( lib::json::JSON::parse( js ) );

         onMessage( connection, js, o );
      } catch ( const std::exception& e ) {
         onError( connection, js, e.what() );
      }
   };

   endpoint.onopen = [this]( std::shared_ptr<typename T::Connection> connection ) {
      onOpen( connection );
   };

   endpoint.onclose = [this]( std::shared_ptr<typename T::Connection> connection, int status, const std::string& reason ) {
      onClose( connection, status, reason );
   };

   endpoint.onerror = [this]( std::shared_ptr<typename T::Connection> connection, const boost::system::error_code& ec ) {
      onError( connection, ec );
   };

   auto accepted = [onaccept, this]() {
      _register();

      onaccept();
   };

   controller_->start( accepted ); //! Blocks; stop()ped in shutdown().
}


template<typename T>
void Service<T>::onError( std::shared_ptr<typename T::Connection> connection, const std::string& input, const char* format, fmt::ArgList args ) {
   lib::json::JSON json;

   json[ERROR] = fmt::format( format, args );
   json[ECHO] = input;

   try {
      auto o = lib::json::JSON::parse( input );
      auto it = o.find( FAILURE );

      if ( it != o.end() ) json[CALLBACK] = *it;
   } catch ( std::exception& ) {
      // no-op; input was not valid json
   }

   auto message = json.dump();
   TRACE( message );

   send( connection, message );
}


template<typename T>
void Service<T>::send( std::shared_ptr<typename T::Connection> connection, const std::string& message ) {
   auto send_stream = std::make_shared<typename T::SendStream>();

   *send_stream << message;

   size_t conn = (size_t) connection.get();

   controller_->send( connection, send_stream, [conn, this]( const boost::system::error_code& ec ) {
      if ( !ec ) return; // short-circuit

      log_->error( fmt::format( "Connection {} send error; ec.message == {}.", conn, ec.message() ) );
   } );
}


template<typename T>
void Service<T>::shutdown() {
   SIGSERVICE_ = nullptr;

   _unregister();

   std::string reason = fmt::format( "Shutting down {} at {}", info_[PROC].get<std::string>(), info_[CONTROLLER].get<std::string>() );
   std::condition_variable cv;
   std::mutex mtx;

   for ( auto connection : controller_->get_connections() ) {
      size_t conn = (size_t) connection.get();

      controller_->send_close( connection, 1000, reason, [conn, &cv, this]( const boost::system::error_code& ec ) {
         if ( ec ) log_->warning( fmt::format( "Connection {} send error while shutting down; ec.message == {}.", conn, ec.message() ) );

         cv.notify_all();
      } );

      {
         std::unique_lock<std::mutex> lck{ mtx };

         if ( cv.wait_for( lck, std::chrono::seconds{ 3 } ) == std::cv_status::timeout ) { // HARD-CODED
            log_->warning( fmt::format( "Timed out on send of shutting down to connection {}.", conn ) );
         }
      }
   }

   controller_->stop();

   context_.close();
}


template<typename T>
void Service<T>::_register() {
   std::time_t t = std::time( nullptr );
   std::tm* tm = std::localtime( &t );
   std::ostringstream oss;

   oss << std::put_time( tm, "%F %T %Z" ); // HARD-CODED

   info_[T0] = { t, oss.str() };

   std::string controller = fmt::format( "{}://{}:{}", controller_->scheme(), lib::common::Host::name(), controller_->port() ); // HARD-CODED
   info_[CONTROLLER] = controller;

   MARK_T0;
   Registry registry;

   registry.set( info_ );
   MARK_T( "Service::_register()" );

   log_->info( fmt::format( "{}'s controller endpoint == {}.", lib::common::Host::proc(), controller ) );
}


template<typename T>
void Service<T>::_unregister() {
   lib::json::JSON o;

   o[HOST] = lib::common::Host::name();
   o[PROC] = lib::common::Host::proc();
   o[PID]  = getpid();

   MARK_T0;
   Registry registry;

   registry.unset( o );
   MARK_T( "Service::_unregister()" );
}


//!
//! Returns an iterator to the given property and emits an error message if the property is not found.
//!
template<typename T>
lib::json::JSON& Service<T>::findMandatoryProperty( std::shared_ptr<lib::json::JSON> o, const std::string& property ) {
   auto it = o->find( property );

   if ( it == o->end() ) throw std::invalid_argument{ fmt::format( "Missing mandatory key '{}'.", property ) };

   return *it;
}


template class Service<Controller>; // http://stackoverflow.com/a/8752879/5887599


} // namespace service
} // namespace lib
