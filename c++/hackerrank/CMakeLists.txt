cmake_minimum_required( VERSION 3.4.3 )

set( PROJECT_NAME hackerrank )

project( ${PROJECT_NAME} LANGUAGES CXX )

add_executable( ${PROJECT_NAME} dummy.cpp )

# debug build by default
if( NOT CMAKE_BUILD_TYPE )
  set( CMAKE_BUILD_TYPE "Debug" CACHE STRING "Choose the type of build" FORCE )
endif( NOT CMAKE_BUILD_TYPE )

option( ctci-find-the-running-median "Build and run ctci-find-the-running-median." ON)
option( simple-array-sum "Build and run simple-array-sum." ON)

if( ctci-find-the-running-median )
   add_executable( ctci-find-the-running-median ctci-find-the-running-median.cpp )
endif( ctci-find-the-running-median )

if( simple-array-sum )
   add_executable( simple-array-sum simple-array-sum.cpp )
endif( simple-array-sum )
