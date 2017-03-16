#ifndef __lib_service_test_ServiceTest_h__
#define __lib_service_test_ServiceTest_h__

#include "lib-constants-service/Registry.h"
#include "lib-constants-service/Service.h"
#include "lib-service/Service.h"
#include <condition_variable>
#include <mutex>

using namespace lib::constants::Registry;
using namespace lib::constants::Service;


int argc;
char** argv;
unsigned int timeout = 5; // HARD-CODED
auto timeoutms = std::chrono::milliseconds( timeout * 1000 );
std::condition_variable cv;
std::mutex mtx;

std::string endpoint;


// helpers for making a client since we can't reuse a client becase client->stop() doesn't reset its io_service
const std::string success = "success";
const std::string failure = "failure";

std::shared_ptr<Controller> make_controller() {
#  ifndef LIB_SERVICE_SECURE_CONTROLLER
   return std::make_shared<Controller>();
#  else
   std::string crt{ __FILE__}, key{ __FILE__};

   crt.replace( crt.find( "ServiceTest.h" ), crt.length(), "../../lib-ws/test/WebSocketTest.crt" ); // HARD-CODED
   key.replace( key.find( "ServiceTest.h" ), key.length(), "../../lib-ws/test/WebSocketTest.key" ); // HARD-CODED

   return std::make_shared<Controller>( crt, key );
#  endif // LIB_SERVICE_SECURE_CONTROLLER
}


std::string json( std::string action ) {
   lib::json::JSON o;

   o[ACTION]  = action;
   o[SUCCESS] = success;
   o[FAILURE] = failure;

   return o.dump();
}


std::string onopen( std::shared_ptr<Client> client, std::string action, std::string invalid = "" ) {
   std::string outgoing = json( action ) + invalid;

   client->onopen = [client, outgoing]() { // the send within onopen is kinda weird; it follows the Simple-WebSocket-Server example
      auto send_stream = std::make_shared<SendStream>();

      *send_stream << outgoing;

      client->send( send_stream );
   };

   return outgoing;
}


void test( std::shared_ptr<Client> client, std::function<void( std::shared_ptr<Message> message )> onmessage ) {
   client->onmessage = onmessage;

   std::unique_lock<std::mutex> lck{ mtx };
   auto thread = std::make_shared<std::thread>( [&]() {
      try {
         client->start();
      } catch ( const std::exception& e ) {
         cv.notify_all();

         FAIL() << e.what();
      }
    } );

    if ( cv.wait_for( lck, timeoutms ) == std::cv_status::timeout ) FAIL() << "timed out";

    thread->join();
}

#endif // __lib_service_test_ServiceTest_h__
