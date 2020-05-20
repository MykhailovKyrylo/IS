//
// Created by Kyrylo Mykhailov on 14.04.2020.
//

#include <iostream>

#include "university_data.h"
#include "solution.h"

using namespace university_data;

constexpr size_t MAX_ITERATION_COUNT = 200; // 1'000'000

university create_university() {
    university knu("Taras Shevchenko Nation University");

    { // adding teachers
        knu.add_teacher(TEACHERS::KEK, {RANKS::PROFESSOR});
        knu.add_teacher(TEACHERS::LOL, {RANKS::DOCTOR});
        knu.add_teacher(TEACHERS::HEH, {RANKS::ASSOCIATE_PROFESSOR});
        knu.add_teacher(TEACHERS::AHAH, {RANKS::GRADUATE_STUDENT});
    }

    { // adding classrooms
        knu.add_classroom(CLASSROOMS::N_101, {15});
        knu.add_classroom(CLASSROOMS::N_102, {15});
        knu.add_classroom(CLASSROOMS::N_103, {15});
        knu.add_classroom(CLASSROOMS::N_201, {30});
        knu.add_classroom(CLASSROOMS::N_202, {30});
        knu.add_classroom(CLASSROOMS::N_203, {30});
        knu.add_classroom(CLASSROOMS::N_301, {120});
        knu.add_classroom(CLASSROOMS::N_302, {120});
        knu.add_classroom(CLASSROOMS::N_303, {120});
    }

    { // adding disciplines
        {
            discipline_data discipline;
            discipline.lectures_count_per_week = 1;
            discipline.practice_count_per_week = 2;
            discipline.lecturer = TEACHERS::KEK;
            discipline.practice_teachers = {
                TEACHERS::KEK,
                TEACHERS::LOL
            };

            knu.add_discipline(DISCIPLINES::PROGRAMMING_0, std::move(discipline));
        }

        {
            discipline_data discipline;
            discipline.lectures_count_per_week = 1;
            discipline.practice_count_per_week = 2;
            discipline.lecturer = TEACHERS::HEH;
            discipline.practice_teachers = {
                TEACHERS::HEH,
                TEACHERS::AHAH
            };

            knu.add_discipline(DISCIPLINES::PROGRAMMING_1, std::move(discipline));
        }
    }

    { // adding groups
        {
            group_data group;
            group.students_count = 32;
            group.disciplines = {
                DISCIPLINES::PROGRAMMING_0,
                DISCIPLINES::PROGRAMMING_1
            };

            knu.add_group(GROUPS::TTP_42, std::move(group));
        };

        {
            group_data group;
            group.students_count = 20;
            group.disciplines = {
                DISCIPLINES::PROGRAMMING_0,
                DISCIPLINES::PROGRAMMING_1
            };

            knu.add_group(GROUPS::MI_4, std::move(group));
        };

    }

    knu.construct_lessons();

    return knu;
}

void print_chromosome(const chromosome& chromosome, const university& university) {
    for (size_t lesson_idx = 0; lesson_idx < chromosome.scheduled_lessons.size(); lesson_idx++) {
        auto lesson_data = university.get_lesson(lesson_idx);
        auto scheduled_lesson = chromosome.scheduled_lessons[lesson_idx];
        auto teacher_id = scheduled_lesson.teacher_id;
        auto classroom_id = scheduled_lesson.classroom_id;
        auto lesson_type = (lesson_data.is_practice) ? "Practice" : "Lecture";

        std::cout << "Scheduled lesson #" << lesson_idx << '\n';
        std::cout << GROUPS_NAMES[lesson_data.group_id];
        std::cout << " (" << university.get_group(lesson_data.group_id).students_count << " students) ";
        std::cout << DISCIPLINES_NAMES[lesson_data.discipline_id] << '\n';
        std::cout << lesson_type << " with " << RANKS_NAMES[university.get_teacher_rank(teacher_id)] << ' ';
        std::cout << TEACHERS_NAMES[teacher_id] << '\n';
        std::cout << DAYS_NAMES[scheduled_lesson.day_idx] << ' ' << PARA_NAMES[scheduled_lesson.para_idx] << " lesson\n";
        std::cout << "In " << CLASSROOMS_NAMES[classroom_id];
        std::cout <<" (capacity = " << university.get_classroom(classroom_id).capacity << ")\n";
        std::cout << '\n';
    }
}

void print_population(const population& population, const university& university) {
    size_t chromosomes_count = 0;
    for (const auto& chromosome : population.chromosomes) {
        std::cout << "Chromosome #" << chromosomes_count++ << '\n';
        print_chromosome(chromosome, university);
        std::cout << '\n';
    }
}

int main() {

    auto university = create_university();

    auto population = population::construct_randomly(university);

    print_population(population, university);

//  int iteration_count = 0;
//  while (!population.is_valid() && iteration_count < MAX_ITERATION_COUNT) {
//    population.make_step();
//  }

    return 0;
}
