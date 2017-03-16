cmake_minimum_required( VERSION 3.4.3 )

# Set the all important PROJECT_NAME.  HARD-CODED in conjuction with lib/lib-cmake functions.
set( PROJECT_NAME lib-service )

project( ${PROJECT_NAME} LANGUAGES CXX )

# Put dependencies first...
include( lib/lib-cmake/common.cmake ) # sets PANDA_PROJECT_ROOT via is_cmake_root.cmake, cmake_minimum_required, etc

find_package( OpenSSL REQUIRED )
find_package( PkgConfig REQUIRED )

pkg_search_module( ZEROMQ REQUIRED libzmq )

add_subproject_if_not_exists( fmt                   lib/lib-fmt )
add_subproject_if_not_exists( gtest                 lib/lib-googletest )
add_subproject_if_not_exists( lib-common            lib/lib-common )
add_subproject_if_not_exists( lib-constants-service lib/lib-constants-service )
add_subproject_if_not_exists( lib-json              lib/lib-json )
add_subproject_if_not_exists( lib-ws                lib/lib-ws )

set( LIBRARYS_LIB_SERVICE
   ${ZEROMQ_LIBRARIES}
   fmt
   lib-common
   lib-constants-service
   lib-json
   lib-ws
   nlohmann_json
)

set( SOURCES_LIB_SERVICE
   src/Registry.cpp
   src/Service.cpp
   src/Version.cpp
)

# ...then targets...
add_library_shared_and_static( "${SOURCES_LIB_SERVICE}" )

# ...then build instructions.
add_git_sha1()
generate_export_header( ${PROJECT_NAME} )

if( ${IS_PANDA_PROJECT_ROOT} )
   target_compile_options( fmt PUBLIC -fPIC ) # NOTE: -fPIC is needed to create libfmt.a

   set( EXPORT_TARGETS fmt )
endif()

foreach( TARGET_LIB ${TARGET_LIBS} ) # HARD-CODED in conjunction with add_library_shared_and_static()
   # Add include directories.
   target_include_directories( ${TARGET_LIB} PUBLIC
      $<BUILD_INTERFACE:${CMAKE_CURRENT_BINARY_DIR}> # for export header and GitSha1.cpp
      $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/include>
      $<BUILD_INTERFACE:${lib-common_SOURCE_DIR}/include>
      $<BUILD_INTERFACE:${lib-constants-service_SOURCE_DIR}/include>
      $<BUILD_INTERFACE:${lib-json_SOURCE_DIR}/include>
      $<BUILD_INTERFACE:${lib-ws_SOURCE_DIR}/include>
      $<BUILD_INTERFACE:${Boost_INCLUDE_DIRS}>
      $<INSTALL_INTERFACE:include>
      PRIVATE src
   )

   # Add link libraries.
   target_link_libraries( ${TARGET_LIB} PUBLIC ${LIBRARYS_LIB_SERVICE} )

   # If we have compiler requirements for this project, list them here.
   #target_compile_features( ${TARGET_LIB}
   #   PUBLIC cxx_auto_type
   #   PRIVATE cxx_variadic_templates
   #)
endforeach()

# Every project has unit tests, of course.
if( TEST_${PROJECT_NAME} )
   add_executable( test_lib_service         test/ServiceTest.cpp )
   add_executable( test_lib_service_derived test/DerivedServiceTest.cpp )
   add_executable( test_lib_registry        test/RegistryTest.cpp )

   target_link_libraries( test_lib_service         ${PROJECT_NAME} gtest ${OPENSSL_LIBRARIES} )
   target_link_libraries( test_lib_service_derived ${PROJECT_NAME} gtest ${OPENSSL_LIBRARIES} )
   target_link_libraries( test_lib_registry        ${PROJECT_NAME} gtest ${OPENSSL_LIBRARIES} )

   add_test( test_lib_service         test_lib_service         )
   add_test( test_lib_service_derived test_lib_service_derived )
   add_test( test_lib_registry        test_lib_registry )
endif()

# Conditionally install ${PROJECT_NAME} and export targets based on an option from common.cmake.
install_if( "${EXPORT_TARGETS}" )
