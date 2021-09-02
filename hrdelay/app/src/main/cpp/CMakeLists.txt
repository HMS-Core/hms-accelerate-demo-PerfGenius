cmake_minimum_required(VERSION 3.4.1)
include_directories(src/main/cpp/include/)
# set the source file of libnative-lib.so
add_library(
        native-lib
        SHARED
        native-lib.cpp)

# add libhrdelay.so
add_library(
        hrdelay
        SHARED
        IMPORTED)
set_target_properties(
        hrdelay
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../../../libs/${ANDROID_ABI}/libhrdelay.so)

# add libhrdelay_stat.so
add_library(
        hrdelay_stat
        SHARED
        IMPORTED)
set_target_properties(
        hrdelay_stat
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../../../libs/${ANDROID_ABI}/libhrdelay_stat.so)

find_library(
        log-lib
        log)
target_link_libraries(
        native-lib
        hrdelay
        hrdelay_stat
        ${log-lib})