cmake_minimum_required(VERSION 3.21)
project(MSDTest)

set(CMAKE_CXX_STANDARD 14)

add_executable(MSDTest test_msdscript.cpp testGeneratorFunctions.cpp exec.cpp)
