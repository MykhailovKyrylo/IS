//
// Created by Kyrylo Mykhailov on 14.04.2020.
//

#include <iostream>
#include <vector>
#include <map>
#include <numeric>

#include "university_data.h"

const int INF = std::numeric_limits<int>::max();

university createTarasShevchenkoNationUniversity()
{
  university university("Taras Shevchenko Nation University");

  { // adding teachers
	using namespace university_data::teachers;

	university.add_teacher(names::KEK, {rank::PROFESSOR});
	university.add_teacher(names::LOL, {rank::DOCTOR});
	university.add_teacher(names::HEH, {rank::ASSOCIATE_PROFESSOR});
	university.add_teacher(names::AHAH, {rank::GRADUATE_STUDENT});
  }

  { // adding classrooms
	using namespace university_data::classrooms;

	university.add_classroom(N_101, {15});
	university.add_classroom(N_102, {15});
	university.add_classroom(N_103, {15});
	university.add_classroom(N_201, {30});
	university.add_classroom(N_202, {30});
	university.add_classroom(N_203, {30});
	university.add_classroom(N_301, {120});
	university.add_classroom(N_302, {120});
	university.add_classroom(N_303, {120});
  }

  { // adding disciplines
	using namespace university_data::teachers;
	using namespace university_data::disciplines;

	{
	  discipline_data discipline;
	  discipline.lectures_count_per_week = 1;
	  discipline.practice_count_per_week = 2;
	  discipline.lecturer = university.getTeachers().find(names::KEK);
	  discipline.practice_teachers = {
		  university.getTeachers().find(names::KEK),
		  university.getTeachers().find(names::LOL)
	  };

	  university.add_discipline(PROGRAMMING_0, std::move(discipline));
	}

	{
	  discipline_data discipline;
	  discipline.lectures_count_per_week = 1;
	  discipline.practice_count_per_week = 2;
	  discipline.lecturer = university.getTeachers().find(names::HEH);
	  discipline.practice_teachers = {
		  university.getTeachers().find(names::HEH),
		  university.getTeachers().find(names::AHAH)
	  };

	  university.add_discipline(PROGRAMMING_1, std::move(discipline));
	}
  }

  { // adding groups
	using namespace university_data::groups;
	using namespace university_data::disciplines;

	{
	  group_data group;
	  group.students_count = 32;
	  group.disciplines = {
		  university.getDisciplines().find(PROGRAMMING_0),
		  university.getDisciplines().find(PROGRAMMING_1)
	  };

	  university.add_group(TTP_42, std::move(group));
	};

	{
	  group_data group;
	  group.students_count = 20;
	  group.disciplines = {
		  university.getDisciplines().find(PROGRAMMING_0),
		  university.getDisciplines().find(PROGRAMMING_1)
	  };

	  university.add_group(MI_4, std::move(group));
	};

  }

  return university;
}

int main()
{

  auto university = createTarasShevchenkoNationUniversity();

  enum DAYS{
	  monday,
	  tuesday,
	  wednesday,
	  thursday,
	  friday,
	  DAYS_COUNT
  };

  enum LESSON{
	  first,
	  second,
	  third,
	  fourth,
	  fifth,
	  LESSONS_COUNT
  };

  return 0;
}
