#include "lib-common/Host.h"
#include "lib-constants-service/Registry.h"
#include "lib-constants-service/Service.h"
#include "lib-service/Registry.h"
#include "gtest/gtest.h"
#include <unistd.h> // for getpid()

using namespace lib::constants::Registry;
using namespace lib::constants::Service;


lib::json::JSON json;

TEST( RegistryTest, instance ) {
   EXPECT_THROW( lib::service::Registry::instance( json ), std::invalid_argument );

   try {
      lib::service::Registry::instance( json );
   } catch( std::invalid_argument& e ) {
      std::string what{ e.what() };

      ASSERT_TRUE( what.find( "Registry::HOST" ) != std::string::npos );
   }

   json[HOST] = lib::common::Host::name();

   try {
      lib::service::Registry::instance( json );
   } catch( std::invalid_argument& e ) {
      std::string what{ e.what() };

      ASSERT_TRUE( what.find( "Registry::PID" ) != std::string::npos );
   }
}


TEST( RegistryTest, service ) {
   EXPECT_THROW( lib::service::Registry::service( json ), std::invalid_argument );

   try {
      lib::service::Registry::service( json );
   } catch( std::invalid_argument& e ) {
      std::string what{ e.what() };

      ASSERT_TRUE( what.find( "Registry::PROC" ) != std::string::npos );
   }
}


std::shared_ptr<lib::service::Registry> registry;

TEST( RegistryTest, open_or_create ) {
   registry = std::make_shared<lib::service::Registry>();

   ASSERT_TRUE( registry != nullptr );
}


TEST( RegistryTest, set ) {
   if ( !registry ) FAIL() << "no registry";

   json[HOST] = lib::common::Host::name();
   json[PROC] = lib::common::Host::proc();
   json[PID]  = getpid();
   json[CONTROLLER] = "ws://localhost:14001";
   json["endpoints"] = { "ipc://test", "tcp://test.service.com:1234" };

   registry->set( json );

   auto info = registry->get( json );

   ASSERT_STREQ( json.dump().c_str(), info.c_str() );
}


TEST( RegistryTest, get ) {
   if ( !registry ) FAIL() << "no registry";

   auto info = registry->get( json );

   ASSERT_STREQ( json.dump().c_str(), info.c_str() );
}


TEST( RegistryTest, instances ) {
   if ( !registry ) FAIL() << "no registry";

   auto copy = json;
   auto before = copy.dump();

   copy.erase( copy.find( HOST ) );

   auto info = registry->get( copy );
   auto o = lib::json::JSON::parse( info );

   ASSERT_LE( 1, o.size() ); // the registry could be polluted, so don't use EQ 1
   ASSERT_EQ( '[', info[0] );
   ASSERT_EQ( ']', info[info.length() - 1] );
   ASSERT_TRUE( info.find( before ) != std::string::npos );
}


TEST( RegistryTest, unset ) {
   if ( !registry ) FAIL() << "no registry";

   registry->unset( json );

   auto info = registry->get( json );

   ASSERT_STREQ( "{}", info.c_str() );
}


int main( int argc, char **argv ) {
  testing::InitGoogleTest( &argc, argv );

  return RUN_ALL_TESTS();
}
