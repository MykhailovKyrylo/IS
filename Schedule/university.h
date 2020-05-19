//
// Created by Kyrylo Mykhailov on 14.04.2020.
//

#pragma once

#include <string>
#include <set>
#include <list>
#include <map>
#include <iterator>
#include <vector>
#include <unordered_map>
#include <cstdint>

using name = std::string;

struct teacher_data{
    std::string rank;
};
using teachers = std::map<name, teacher_data>;
using teacher_it = teachers::const_iterator;

struct discipline_data {
    uint16_t lectures_count_per_week;
    uint16_t practice_count_per_week;
    teacher_it lecturer;
    std::set<teacher_it> practice_teachers;
    size_t get_lessons_count() const;
};
using disciplines = std::map<name, discipline_data>;
using discipline_it = disciplines::const_iterator;

struct classroom_data {
    uint16_t capacity;
};
using classrooms = std::map<name, classroom_data>;
using classroom_it = classrooms::const_iterator;

struct group_data {
    uint16_t students_count;
    std::set<discipline_it> disciplines;
	size_t get_lessons_count() const;
};
using groups = std::map<name, group_data>;
using group_it = groups::const_iterator;

class university {
  public:
    explicit university(std::string&& name_);

    void add_teacher(const name& teacher_name, teacher_data&& teacher_data);
    void add_discipline(const name& discipline_name, discipline_data&& discipline_data);
    void add_classroom(const name& classroom_name, classroom_data&& classroom_data);
    void add_group(const name& group_name, group_data&& group_data);

    const teachers& getTeachers() const;
    const disciplines& getDisciplines() const;
    const classrooms& getClassrooms() const;
    const groups& getGroups() const;
  private:
    name univerversity_name;
    teachers teachers;
    disciplines disciplines;
    classrooms classrooms;
    groups groups;
};





