#include "fmt/format.h"
#include "gtest/gtest.h"
#include "ServiceTest.h"


const std::string NOT_PONG = PONG + " not";
const std::string NOT_SUCCESS = "fail";
const std::string FAIL_WITH_ERROR = "fail with error";


class DerivedService : public lib::service::Service<Controller> {
public:
   DerivedService( std::shared_ptr<lib::common::Logger> logger, std::shared_ptr<Controller> controller )
   : Service<Controller>( logger, controller, __FILE__ )
   {
   }


   void onMessage( std::shared_ptr<Controller::Connection> connection, std::string& js, std::shared_ptr<lib::json::JSON> o ) {
      std::string action = (*o)[ACTION];
      lib::json::JSON json;

      if ( action == PING ) {
         json[CALLBACK] = (*o)[SUCCESS];
         json[PING] = NOT_PONG;
      } else if ( action == NOT_SUCCESS ) {
         json[CALLBACK] = (*o)[FAILURE];
      } else if ( action == FAIL_WITH_ERROR ) {
          json[CALLBACK] = (*o)[FAILURE];
          json[ERROR] = FAIL_WITH_ERROR;
      } else {
         return Service<Controller>::onMessage( connection, js, o ); // short-circuit
      }

      send( connection, json.dump() );
   }
};


TEST( DerivedServiceTest, args ) {
   ASSERT_EQ( 1, argc );
}


std::shared_ptr<Controller> controller;

TEST( DerivedServiceTest, controller ) {
   controller = make_controller();

   ASSERT_TRUE( controller != nullptr );
}


std::shared_ptr<lib::common::Logger> logger = std::make_shared<lib::common::Logger>( "" );
std::shared_ptr<DerivedService> service;
std::shared_ptr<std::thread> server;

TEST( DerivedServiceTest, run ) {
   if ( !controller ) FAIL() << "no controller";

   service = std::make_shared<DerivedService>( logger, controller );

   ASSERT_TRUE( service != nullptr );

   std::unique_lock<std::mutex> lck{ mtx };

   server = std::make_shared<std::thread>( []() {
      service->run( []() {
         cv.notify_all();
      } );
   } );

   cv.wait( lck, []() { return controller->port(); } );

   endpoint = fmt::format( "localhost:{}/", controller->port() ); // HARD-CODED in conjunction with Service::run()
}


TEST( DerivedServiceTest, ping ) {
   if ( !service ) FAIL() << "no service";

   auto client = std::make_shared<Client>( endpoint );
   auto outgoing = onopen( client, PING );
   auto onmessage = [client, outgoing]( std::shared_ptr<Message> message ) {
      std::string incoming = message->string();

      client->send_close( 1000, "Network success!" );

      ASSERT_TRUE( incoming.find( success ) != std::string::npos );
      ASSERT_TRUE( incoming.find( NOT_PONG ) != std::string::npos );

      cv.notify_all();
   };

   test( client, onmessage );
}


TEST( DerivedServiceTest, fail ) {
   if ( !service ) FAIL() << "no service";

   auto client = std::make_shared<Client>( endpoint );
   auto outgoing = onopen( client, NOT_SUCCESS );
   auto onmessage = [client, outgoing]( std::shared_ptr<Message> message ) {
      std::string incoming = message->string();

      client->send_close( 1000, "Network success!" );

      ASSERT_TRUE( incoming.find( CALLBACK ) != std::string::npos );
      ASSERT_TRUE( incoming.find( FAILURE ) != std::string::npos );
      ASSERT_TRUE( incoming.find( failure ) != std::string::npos );

      cv.notify_all();
   };

   test( client, onmessage );
}


TEST( DerivedServiceTest, failWithError ) {
   if ( !service ) FAIL() << "no service";

   auto client = std::make_shared<Client>( endpoint );
   auto outgoing = onopen( client, FAIL_WITH_ERROR );
   auto onmessage = [client, outgoing]( std::shared_ptr<Message> message ) {
      std::string incoming = message->string();

      client->send_close( 1000, "Network success!" );

      ASSERT_TRUE( incoming.find( CALLBACK ) != std::string::npos );
      ASSERT_TRUE( incoming.find( FAILURE ) != std::string::npos );
      ASSERT_TRUE( incoming.find( failure ) != std::string::npos );
      ASSERT_TRUE( incoming.find( ERROR ) != std::string::npos );

      cv.notify_all();
   };

   test( client, onmessage );
}


TEST( ServiceTest, join ) {
   service = nullptr;

   server->join();
}


int main( int argc, char **argv ) {
  testing::InitGoogleTest( &argc, argv );

  ::argc = argc;
  ::argv = argv;

  return RUN_ALL_TESTS();
}
