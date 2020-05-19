//
// Created by Kyrylo Mykhailov on 20.05.2020.
//

#pragma once

#include <vector>
#include <string>
#include <map>

constexpr size_t POPULATION_SIZE = 10;

template<typename Iter, typename RandomGenerator>
Iter select_randomly(Iter start, Iter end, RandomGenerator& g)
{
  std::uniform_int_distribution<> dis(0, std::distance(start, end) - 1);
  std::advance(start, dis(g));
  return start;
}

template<typename Iter>
Iter select_randomly(Iter start, Iter end)
{
  static std::random_device rd;
  static std::mt19937 gen(rd());
  return select_randomly(start, end, gen);
}

enum DAYS {
	monday,
	tuesday,
	wednesday,
	thursday,
	friday,
	DAYS_COUNT
};

enum PARA {
	first,
	second,
	third,
	fourth,
	fifth,
	PARA_COUNT
};

const size_t DAYS_AND_PARA_COUNT = DAYS_COUNT * PARA_COUNT;

struct DayAndTime {
	uint day_idx;
	uint para_idx;
};

struct TeacherAndClassroom {
	const std::string& teacher;
	const std::string& classroom;
};

struct SchedulePerDayAndTime {
	void add(uint lesson_idx, const std::string& teacher_name, const std::string& classroom_name) {
	  TeacherAndClassroom teacher_and_classroom{teacher_name, classroom_name};
	  lessons_info.insert({lesson_idx, std::move(teacher_and_classroom)});

	  classroom_lessons[classroom_name].push_back(lesson_idx);
	  teachers_lessons[teacher_name].push_back(lesson_idx);
	}
	std::map<std::string, std::vector<size_t>> classroom_lessons;
	std::map<std::string, std::vector<size_t>> teachers_lessons;
	std::map<uint, TeacherAndClassroom> lessons_info;
};

struct Chromosome {
	Chromosome()
	{
	  schedule.resize(DAYS_AND_PARA_COUNT);
	}
	std::vector<SchedulePerDayAndTime> schedule;

	static Chromosome ConstructRandom(const university& university)
	{
	  Chromosome random_chromosome;

	  const size_t lessons_count = university.get_lessons_count();
	  const size_t lessons_per_day = (lessons_count % DAYS_COUNT == 0) ? lessons_count / DAYS_COUNT : (lessons_count / DAYS_COUNT + 1);

	  uint lesson_idx = 0;
	  for (uint day = DAYS::monday; day <= DAYS::friday && lesson_idx < lessons_count; day++)
	  {
		for (uint para = PARA::first; para < lessons_per_day && lesson_idx < lessons_count; para++, lesson_idx++)
		{
		  SchedulePerDayAndTime schedule_per_day_and_time;
		  uint day_and_time_idx = day * PARA_COUNT + para;

		  const Lesson& lesson = university.getLessons()[lesson_idx];
		  const std::string group_name = lesson.group;
		  const std::string discipline_name = lesson.discipline;

		  const auto& practice_teachers = university.getDisciplines().at(discipline_name).practice_teachers;
		  if (lesson.is_practice)
		  {
			auto teacher_it = *select_randomly(practice_teachers.begin(), practice_teachers.end());
			const auto& teacher_name = teacher_it->first;
			schedule_per_day_and_time.add(lesson_idx, teacher_name, teacher_it->first);
		  }
		  else
		  {
			auto teacher_it = university.getDisciplines().at(discipline_name).lecturer;
			const auto& teacher_name = teacher_it->first;
			schedule_per_day_and_time.add(lesson_idx, teacher_name, teacher_it->first);
		  }

		  random_chromosome.schedule[day_and_time_idx] = std::move(schedule_per_day_and_time);
		}
	  }
	}
};

struct Population {
	Population(university university)
	{
	  population.resize(POPULATION_SIZE);

	  for (auto& chromosome : population)
	  {
		chromosome = Chromosome::ConstructRandom(university);
	  }
	}
	std::vector<DayAndTime> day_and_time;
	std::vector<Chromosome> population;
};
