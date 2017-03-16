#include "fmt/format.h"
#include "gtest/gtest.h"
#include "lib-service/Registry.h"
#include "ServiceTest.h"


TEST( ServiceTest, args ) {
   ASSERT_EQ( 1, argc );
}


std::shared_ptr<Controller> controller;

TEST( ServiceTest, controller ) {
   controller = make_controller();

   ASSERT_TRUE( controller != nullptr );
}


std::shared_ptr<lib::common::Logger> logger = std::make_shared<lib::common::Logger>( "" );
std::shared_ptr<lib::service::Service<Controller>> service;
std::shared_ptr<std::thread> server;

TEST( ServiceTest, start ) {
   if ( !controller ) FAIL() << "no controller";

   service = std::make_shared<lib::service::Service<Controller>>( logger, controller, __FILE__ );

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


TEST( ServiceTest, echo ) {
   if ( !service ) FAIL() << "no service";

   auto client = std::make_shared<Client>( endpoint );
   auto outgoing = onopen( client, ECHO );
   auto onmessage = [client, outgoing]( std::shared_ptr<Message> message ) {
      std::string incoming = message->string();

      client->send_close( 1000 );

      ASSERT_TRUE( incoming.find( success ) != std::string::npos );
      ASSERT_TRUE( incoming.find( outgoing ) != std::string::npos );

      cv.notify_all();
   };

   test( client, onmessage );
}


TEST( ServiceTest, invalid ) {
   if ( !service ) FAIL() << "no service";

   auto client = std::make_shared<Client>( endpoint );
   auto outgoing = onopen( client, ECHO, "invalid json" );
   auto onmessage = [client, outgoing]( std::shared_ptr<Message> message ) {
      std::string incoming = message->string();

      client->send_close( 1000 );

      auto o = lib::json::JSON::parse( incoming );

      ASSERT_TRUE( o.find( ERROR ) != o.end() );
      ASSERT_TRUE( o.find( ECHO ) != o.end() );
      ASSERT_STREQ( o[ECHO].get<std::string>().c_str(), outgoing.c_str() );

      cv.notify_all();
   };

   test( client, onmessage );
}


TEST( ServiceTest, ping ) {
   if ( !service ) FAIL() << "no service";

   auto client = std::make_shared<Client>( endpoint );
   auto outgoing = onopen( client, PING );
   auto onmessage = [client, outgoing]( std::shared_ptr<Message> message ) {
      std::string incoming = message->string();

      client->send_close( 1000 );

      ASSERT_TRUE( incoming.find( success ) != std::string::npos );
      ASSERT_TRUE( incoming.find( PONG ) != std::string::npos );

      cv.notify_all();
   };

   test( client, onmessage );
}


TEST( ServiceTest, info ) {
   if ( !service ) FAIL() << "no service";

   auto client = std::make_shared<Client>( endpoint );
   auto outgoing = onopen( client, INFO );
   auto onmessage = [client, outgoing]( std::shared_ptr<Message> message ) {
      std::string incoming = message->string();

      client->send_close( 1000 );

      ASSERT_TRUE( incoming.find( success ) != std::string::npos );

      ASSERT_TRUE( incoming.find( HOST ) != std::string::npos );
      ASSERT_TRUE( incoming.find( PROC ) != std::string::npos );
      ASSERT_TRUE( incoming.find( PID  ) != std::string::npos );

      ASSERT_TRUE( incoming.find( "lib::common::git_sha1" )  != std::string::npos );
      ASSERT_TRUE( incoming.find( "lib::json::git_sha1" )    != std::string::npos );
      ASSERT_TRUE( incoming.find( "lib::service::git_sha1" ) != std::string::npos );
      ASSERT_TRUE( incoming.find( "lib::ws::git_sha1" )      != std::string::npos );

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
