cmake_minimum_required(VERSION 3.15)
project(Schedule)

set(CMAKE_CXX_STANDARD 17)

add_executable(Schedule
        main.cpp
        university.cpp      university.h
        solution.cpp        solution.h
        random_utils.cpp    random_utils.h)