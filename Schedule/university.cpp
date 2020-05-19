//
// Created by Kyrylo Mykhailov on 14.04.2020.
//

#include "university.h"

#include <algorithm>
#include <random>

university::university(std::string&& name_)
	: univerversity_name(name_)
{};

void university::add_teacher(const name& teacher_name, teacher_data&& teacher_data)
{
  teachers.insert({teacher_name, teacher_data});
}

void university::add_discipline(const name& discipline_name, discipline_data&& discipline_data)
{
  disciplines.insert({discipline_name, discipline_data});
}

void university::add_classroom(const name& classroom_name, classroom_data&& classroom_data)
{
  classrooms.insert({classroom_name, classroom_data});
}

void university::add_group(const name& group_name, group_data&& group_data)
{
  groups.insert({group_name, group_data});
}

const teachers& university::getTeachers() const
{
  return teachers;
}

const disciplines& university::getDisciplines() const
{
  return disciplines;
}

const classrooms& university::getClassrooms() const
{
  return classrooms;
}

const groups& university::getGroups() const
{
  return groups;
}

const std::vector<Lesson>& university::getLessons() const
{
  return lessons;
}

void university::construct_lessons()
{
  lessons.reserve(get_lessons_count());

  for (const auto&[group_name, group_data] : groups)
  {
	for (const auto& discipline_it : group_data.disciplines)
	{
	  const std::string discipline_name = discipline_it->first;
	  const auto discipline_data = discipline_it->second;

	  for (size_t lecture_number = 0; lecture_number < discipline_data.lectures_count_per_week; lecture_number++)
	  {
		Lesson lecture = {group_name, discipline_name, false};
		lessons.push_back(std::move(lecture));
	  }

	  for (size_t practice_number = 0; practice_number < discipline_data.practice_count_per_week; practice_number++)
	  {
		Lesson practice = {group_name, discipline_name, true};
		lessons.push_back(std::move(practice));
	  }
	}
  }

  std::random_device rd;
  std::mt19937 g(rd());

  std::shuffle(lessons.begin(), lessons.end(), g);
}

size_t university::get_lessons_count() const
{
  size_t lessons_count = 0;
  for (const auto&[_, discipline_data] : disciplines)
  {
	lessons_count += discipline_data.get_lessons_count();
  }

  return lessons_count;
}

size_t discipline_data::get_lessons_count() const
{
  return lectures_count_per_week + practice_count_per_week;
}

size_t group_data::get_lessons_count() const
{
  size_t lessong_count = 0;
  for (const auto& discipline : disciplines)
  {
	lessong_count += discipline->second.get_lessons_count();
  }
}
