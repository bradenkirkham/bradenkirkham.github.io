cmake_minimum_required(VERSION 3.21)
project(MSD_Script)

set(CMAKE_CXX_STANDARD 14)

add_executable(MSD_Script main.cpp cmdline.cpp Expr.cpp catch.hpp Expr.hpp cmdline.hpp parse.cpp parse.h val.cpp val.h pointer.h Env.cpp Env.h Step.cpp Step.h Cont.cpp Cont.h)
add_executable(Test MSDTest/test_msdscript.cpp MSDTest/testGeneratorFunctions.cpp MSDTest/exec.cpp)
