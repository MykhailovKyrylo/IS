# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.15

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list


# Suppress display of executed commands.
$(VERBOSE).SILENT:


# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /Applications/CLion.app/Contents/bin/cmake/mac/bin/cmake

# The command to remove a file.
RM = /Applications/CLion.app/Contents/bin/cmake/mac/bin/cmake -E remove -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /Users/kyrylomykhailov/IS/Schedule

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /Users/kyrylomykhailov/IS/Schedule/cmake-build-debug

# Include any dependencies generated for this target.
include CMakeFiles/Schedule.dir/depend.make

# Include the progress variables for this target.
include CMakeFiles/Schedule.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/Schedule.dir/flags.make

CMakeFiles/Schedule.dir/main.cpp.o: CMakeFiles/Schedule.dir/flags.make
CMakeFiles/Schedule.dir/main.cpp.o: ../main.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/Users/kyrylomykhailov/IS/Schedule/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object CMakeFiles/Schedule.dir/main.cpp.o"
	/Library/Developer/CommandLineTools/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/Schedule.dir/main.cpp.o -c /Users/kyrylomykhailov/IS/Schedule/main.cpp

CMakeFiles/Schedule.dir/main.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/Schedule.dir/main.cpp.i"
	/Library/Developer/CommandLineTools/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /Users/kyrylomykhailov/IS/Schedule/main.cpp > CMakeFiles/Schedule.dir/main.cpp.i

CMakeFiles/Schedule.dir/main.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/Schedule.dir/main.cpp.s"
	/Library/Developer/CommandLineTools/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /Users/kyrylomykhailov/IS/Schedule/main.cpp -o CMakeFiles/Schedule.dir/main.cpp.s

# Object files for target Schedule
Schedule_OBJECTS = \
"CMakeFiles/Schedule.dir/main.cpp.o"

# External object files for target Schedule
Schedule_EXTERNAL_OBJECTS =

Schedule: CMakeFiles/Schedule.dir/main.cpp.o
Schedule: CMakeFiles/Schedule.dir/build.make
Schedule: CMakeFiles/Schedule.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/Users/kyrylomykhailov/IS/Schedule/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Linking CXX executable Schedule"
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/Schedule.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/Schedule.dir/build: Schedule

.PHONY : CMakeFiles/Schedule.dir/build

CMakeFiles/Schedule.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles/Schedule.dir/cmake_clean.cmake
.PHONY : CMakeFiles/Schedule.dir/clean

CMakeFiles/Schedule.dir/depend:
	cd /Users/kyrylomykhailov/IS/Schedule/cmake-build-debug && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /Users/kyrylomykhailov/IS/Schedule /Users/kyrylomykhailov/IS/Schedule /Users/kyrylomykhailov/IS/Schedule/cmake-build-debug /Users/kyrylomykhailov/IS/Schedule/cmake-build-debug /Users/kyrylomykhailov/IS/Schedule/cmake-build-debug/CMakeFiles/Schedule.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : CMakeFiles/Schedule.dir/depend

