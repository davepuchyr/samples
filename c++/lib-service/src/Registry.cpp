#include "fmt/format.h"
#include "lib-constants-service/Registry.h"
#include "lib-json/JSON.h"
#include "lib-service/Registry.h"
#include <boost/interprocess/sync/scoped_lock.hpp>

namespace lib {
namespace service {

using namespace lib::constants::Registry;

#if 1
#  define TRACE( s ) fmt::format( "{}:{} {}", __FILE__, __LINE__, s )
#  define DUMP( s2s ) dump( s2s )

static void dump( Shared::Service2Instances& service2instances ) {
   lib::json::JSON json;

   for ( auto service : service2instances ) {
      std::string sname = service.first.c_str();

      for ( auto instance : service.second ) {
         std::string iname = instance.first.c_str();

         json[sname][iname] = lib::json::JSON::parse( instance.second.c_str() );
      }
   }

   std::cout << "Service2Instances == " << json.dump( 3 ) << std::endl;
}
#else
#  define DUMP
#  define TRACE
#endif


std::string Registry::instance( lib::json::JSON& info ) {
   if ( info.find( HOST ) == info.end() ) throw std::invalid_argument( "Registry::HOST key required in JSON info object." );
   if ( info.find( PID ) == info.end() ) throw std::invalid_argument( "Registry::PID key required in JSON info object." );

   std::string host = info[HOST];
   int pid = info[PID];

   return fmt::format( "{}:{}", host, pid ); //! HARD-CODED host:pid delimiter.
}


std::string Registry::service( lib::json::JSON& info ) {
   if ( info.find( PROC ) == info.end() ) throw std::invalid_argument( "Registry::PROC key required in JSON info object." );

   std::string proc = info[PROC];

   return proc.substr( proc.find_last_of( '/' ) + 1 ); //! HARD-CODED directory delimiter.
}


Registry::Registry( std::string segment_prefix, const std::size_t segment_size )
: service2instances_{ make_shm_map( segment_prefix, segment_size ) }
, key_{ make_shm_string_key( segment_prefix, segment_size ) }
, value_{ make_shm_string_value( segment_prefix, segment_size ) }
, mtx_{ bip::open_or_create, "59fe70f2-6551-11e6-8250-0090f5b59930" } // HARD-CODED uuid.
{
}


Registry::~Registry() {
}


//!
//! \brief Registers a service given a JSON info object; "register" is a reserved word, hence the name "set".
//! \param info
//!
void Registry::set( lib::json::JSON& info ) {
   _set( service( info ), instance( info ), info.dump() );
}


//!
//! \brief Unregisters a service given a JSON object usable by service() and instance().
//! \param json
//!
void Registry::unset( lib::json::JSON& json ) {
   _set( service( json ), instance( json ), "{}" ); //! HARD-CODED empty js object.
}


//!
//! \brief Writes to the registry's shared memory in an interprocess-safe manner.
//! \param service
//! \param instance
//! \param info
//!
void Registry::_set( const std::string& service, const std::string& instance, const std::string& info ) {
   TRACE( fmt::format( "service == {}; instance == {}; info == {}", service, instance, info ) );

   bip::scoped_lock<bip::named_mutex> lck{ mtx_ };

   key_ = service.c_str();

   auto& instances2info = service2instances_[key_];
   auto  erase = info == "{}"; //! HARD-CODED empty js object.

   if ( erase ) value_ = key_; // use value_ as the service key in erase block

   key_ = instance.c_str();

   if ( erase ) {
      auto it = instances2info.find( key_ );

      if ( it != instances2info.end() ) {
         if ( instances2info.size() == 1 ) {
            service2instances_.erase( value_ ); // nuke the service
         } else {
            instances2info.erase( it ); // nuke the instance
         }
      } else { // instance not found
         if ( service2instances_.size() == 1 ) {
            service2instances_.erase( value_ ); // nuke the empty Instances2Info map that we created with auto& instances2info = ... above
         }
      }
   } else {
      value_ = info.c_str();

      instances2info[key_] = value_;
   }

   DUMP( service2instances_ );
}


//!
//! \brief Gets the JSON info for a service specified in the given json object.  If there's no HOST key in json then all instances of the service are returned.
//! \param json - must include HOST and PID for instance specific info; otherwise just PROC for all instances of a given service
//! \return JSON - {...} on a found instance; [{...},{...}...] on a found service; {} on not found
//!
std::string Registry::get( lib::json::JSON& json ) {
   return ( json.find( HOST ) == json.end() ) ? _get( service( json ), "" ) : _get( service( json ), instance( json ) );
}


//!
//! \brief Reads from the registry's shared memory in an interprocess-safe manner.
//! \param service
//! \param instance
//! \return JSON info or "{}" if the service or service + instance combo is not found; in the case of the service only query the result is [{...}] if the service is found
//!
std::string Registry::_get( const std::string& service, const std::string& instance ) {
   bip::scoped_lock<bip::named_mutex> lck{ mtx_ };

   key_ = service.c_str();

   auto outer = service2instances_.find( key_ );

   if ( outer == service2instances_.end() ) return "{}"; //! HARD-CODED empty js object.

   auto& instances2info = service2instances_[key_];

   if ( instance.size() ) {
      key_ = instance.c_str();

      auto inner = instances2info.find( key_ );

      if ( inner == instances2info.end() ) return "{}"; //! HARD-CODED empty js object.

      return instances2info[key_].c_str(); // short-circuit
   }

   // provide all instances of service
   lib::json::JSON json;

   for ( auto it : instances2info ) {
      json.push_back( lib::json::JSON::parse( it.second.c_str() ) );
   }

   return json.dump();
}


} // namespace service
} // namespace lib

