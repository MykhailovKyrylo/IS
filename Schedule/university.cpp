//
// Created by Kyrylo Mykhailov on 14.04.2020.
//

#include "university.h"

university::university(std::string &&name_)
	: univerversity_name(name_)
{};

void university::add_teacher(const name &teacher_name, teacher_data &&teacher_data)
{
  teachers.insert({teacher_name, teacher_data});
}

void university::add_discipline(const name &discipline_name, discipline_data &&discipline_data)
{
  disciplines.insert({discipline_name, discipline_data});
}

void university::add_classroom(const name &classroom_name, classroom_data &&classroom_data)
{
  classrooms.insert({classroom_name, classroom_data});
}

void university::add_group(const name &group_name, group_data &&group_data)
{
  groups.insert({group_name, group_data});
}

const teachers &university::getTeachers() const
{
  return teachers;
}

const disciplines &university::getDisciplines() const
{
  return disciplines;
}

const classrooms &university::getClassrooms() const
{
  return classrooms;
}

const groups &university::getGroups() const
{
  return groups;
}

size_t discipline_data::get_lessons_count() const
{
  return lectures_count_per_week + practice_count_per_week;
}

size_t group_data::get_lessons_count() const
{
  size_t lessong_count = 0;
  for (const auto &discipline : disciplines)
	{
	  lessong_count += discipline->second.get_lessons_count();
	}
}
