#ifndef __lib_service_Service_h__
#define __lib_service_Service_h__

#include "fmt/format.h"
#include "lib-common/Host.h"
#include "lib-common/Logger.h"
#include "lib-json/JSON.h"
#include "lib-ws/WebSocket.h"
#include <memory>
#include <zmq.hpp>

#undef CALLBACK // NOTE: preprocessor name collision
#undef ECHO     // NOTE: preprocessor name collision

#ifndef LIB_SERVICE_SECURE_CONTROLLER
using Client = lib::ws::WSC;
using Controller = lib::ws::WSS;
using Message = lib::ws::WSC::Message;
using SendStream = lib::ws::WSC::SendStream;
#else
using Client = lib::ws::WSSC;
using Controller = lib::ws::WSSS;
using Message = lib::ws::WSSC::Message;
using SendStream = lib::ws::WSSC::SendStream;
#endif // LIB_SERVICE_SECURE_CONTROLLER

namespace lib {
namespace service {


//! Base class for all microservices that are controlled by a websocket.
template<typename T>
class Service {
public:
   Service( std::shared_ptr<lib::common::Logger> logger, std::shared_ptr<T> controller, std::string proc_git_sha1 );

   virtual ~Service();

   virtual void run( std::function<void()> onaccept = [](){} );

protected:
   static lib::json::JSON& findMandatoryProperty( std::shared_ptr<lib::json::JSON> o, const std::string& property );

   virtual void _register(); //!< Registers this Service with the (shared memory as of 2016.08.15) registry service.
   virtual void _unregister(); //!< Unregisters this Service with the (shared memory as of 2016.08.15) registry service.
   virtual void onClose( std::shared_ptr<typename T::Connection> connection, int status, const std::string& reason );
   virtual void onError( std::shared_ptr<typename T::Connection> connection, const boost::system::error_code& ec );
   virtual void onError( std::shared_ptr<typename T::Connection> connection, const std::string& input, const char* format, fmt::ArgList args = fmt::ArgList{} );
   FMT_VARIADIC( void, onError, std::shared_ptr<typename T::Connection>, const std::string&, const char* )
   virtual void onMessage( std::shared_ptr<typename T::Connection> connection, std::string& js, std::shared_ptr<lib::json::JSON> o );
   virtual void onOpen( std::shared_ptr<typename T::Connection> connection );
   virtual void send( std::shared_ptr<typename T::Connection> connection, const std::string& message );
   virtual void shutdown();

   lib::json::JSON info_;

   std::shared_ptr<T> controller_; //!< Websocket interface that drives the service.

   std::shared_ptr<lib::common::Logger> log_;

   zmq::context_t context_;

private:
   static Service<T>* SIGSERVICE_; //!< Instance for sighandler(); set to this in the constructor and set to nullptr in shutdown().
};

template<typename T> Service<T>* Service<T>::SIGSERVICE_;


} // namespace service
} // namespace lib

#endif // __lib_service_Service_h__
