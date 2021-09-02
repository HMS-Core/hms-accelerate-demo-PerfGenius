cmake_minimum_required(VERSION 3.4.1)
# add head file of libPerfgeniusApi.so
include_directories(src/main/cpp/include/)
# set the source file of libmain-lib.so
add_library(
        acckitjni-lib
        SHARED
        source/PerfGeniusJNILib.cpp)
# add 64 bit libc++_shared.so, needed by libPerfgeniusApi.so
add_library(
        c++_shared
        SHARED
        IMPORTED)
set_target_properties(
        c++_shared
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../../../libs/${ANDROID_ABI}/libc++_shared.so)
# add libPerfgeniusApi.so
add_library(
        PerfgeniusApi
        SHARED
        IMPORTED)
set_target_properties(
        PerfgeniusApi
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../../../libs/${ANDROID_ABI}/libPerfgeniusApi.so)
find_library(
        log-lib
        log)
target_link_libraries(
        acckitjni-lib
        PerfgeniusApi
        ${log-lib})