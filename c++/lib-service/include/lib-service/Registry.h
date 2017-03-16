#ifndef __lib_service_Registry_h__
#define __lib_service_Registry_h__

#include "lib-json/JSON.h"
#include <boost/container/scoped_allocator.hpp>
#include <boost/interprocess/containers/string.hpp>
#include <boost/interprocess/managed_shared_memory.hpp>
#include <boost/interprocess/sync/named_mutex.hpp>
#include <boost/unordered_map.hpp>


namespace lib {
namespace service {

// http://stackoverflow.com/a/33913753
namespace bip = boost::interprocess;

namespace Shared {
   using Segment = bip::managed_shared_memory;

   template <typename T>
   using Alloc   = bip::allocator<T, Segment::segment_manager>;
   using Scoped  = boost::container::scoped_allocator_adaptor<Alloc<char>>;

   using String  = boost::container::basic_string<char, std::char_traits<char>, Scoped>;
   using KeyType = String;

   using Instances2Info    = boost::unordered_map<KeyType, String,        boost::hash<KeyType>, std::equal_to<KeyType>, Scoped>; //!< InMap in http://stackoverflow.com/a/33913753.
   using Service2Instances = boost::unordered_map<KeyType, Instances2Info, boost::hash<KeyType>, std::equal_to<KeyType>, Scoped>; //!< OutMap in http://stackoverflow.com/a/33913753.
}


//! Shared memory service registry that maps service names to instance names to instance info, ie the registry is a shared memory map to a map to JSON.
class Registry {
public:
   static std::string instance( lib::json::JSON& info ); //!< Builds the instance name string given a JSON info object.
   static std::string service( lib::json::JSON& info ); //!< Builds the service name string given a JSON info object.

   Registry( std::string segment_prefix = std::string{ "lib::service::Registry::" }, const std::size_t segment_size = 65536 ); //!< HARD-CODED prefix and size.

   virtual ~Registry();

   virtual std::string get( lib::json::JSON& info );

   virtual void set( lib::json::JSON& info );
   virtual void unset( lib::json::JSON& info );


protected:
   virtual std::string _get( const std::string& service, const std::string& instance ); //!< Thread-safe and interprocess-safe getter.

   virtual void _set( const std::string& service, const std::string& instance, const std::string& info ); //!< Thread-safe and interprocess-safe setter.

   bip::named_mutex mtx_; //!< Mutex for _get() and _set().

   Shared::Service2Instances& service2instances_; //!< Shared memory map of service names to instance names to instance info.

   Shared::String& key_;   //!< Reusable reference to a Shared::String to reduce shared allocations as they are costly (in terms of fragmentation/overhead).
   Shared::String& value_; //!< Reusable reference to a Shared::String to reduce shared allocations as they are costly (in terms of fragmentation/overhead).


private:
   //! Provides a Shared::Segment.
   //!
   //! \param[in] segment_prefix (defaulted to "lib::service::Registry::" as of 2016.08.17)
   //! \param[in] segment_size (defaulted to 65536 as of 2016.08.17)
   static Shared::Segment& getSegment( std::string segment_prefix, const std::size_t segment_size ) {
      static Shared::Segment segment( bip::open_or_create, ( segment_prefix + "c18ef85e-6353-11e6-8a7c-0090f5b59930" ).c_str(), segment_size ); //! HARD-CODED uuid.

      return segment;
   }


   //! Provides a Shared::service2instance reference for data memeber service2instance_.
   static Shared::Service2Instances& make_shm_map( std::string segment_prefix, const std::size_t segment_size ) {
      auto& segment = getSegment( segment_prefix, segment_size );
      static Shared::Service2Instances* map = segment.find_or_construct<Shared::Service2Instances>( "d7fe8b04-6353-11e6-8320-0090f5b59930" )( segment.get_segment_manager() ); //! HARD-CODED uuid.

      return *map;
   }


   //! Provides a Shared::String reference for data memeber key_.
   static Shared::String& make_shm_string_key( std::string segment_prefix, const std::size_t segment_size ) {
      static Shared::String shm_string( getSegment( segment_prefix, segment_size ).get_segment_manager() );

      return shm_string;
   }

   //! Provides a Shared::String reference for data memeber value_.
   static Shared::String& make_shm_string_value( std::string segment_prefix, const std::size_t segment_size ) {
      static Shared::String shm_string( getSegment( segment_prefix, segment_size ).get_segment_manager() );

      return shm_string;
   }
};


} // namespace service
} // namespace lib

#endif // __lib_service_Registry_h__
