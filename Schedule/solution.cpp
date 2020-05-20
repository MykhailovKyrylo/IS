//
// Created by Kyrylo Mykhailov on 20.05.2020.
//

#include "solution.h"
#include "random_utils.h"

constexpr size_t POPULATION_SIZE = 2;

scheduled_lesson scheduled_lesson::crossover(const scheduled_lesson& a, const scheduled_lesson& b) {
    scheduled_lesson crossed_scheduled_lesson;

    crossed_scheduled_lesson.day_idx = random_bool() ? a.day_idx : b.day_idx;
    crossed_scheduled_lesson.para_idx = random_bool() ? a.para_idx : b.para_idx;
    crossed_scheduled_lesson.teacher_id = random_bool() ? a.teacher_id : b.teacher_id;
    crossed_scheduled_lesson.classroom_id = random_bool() ? a.classroom_id : b.classroom_id;

    return crossed_scheduled_lesson;
}

chromosome chromosome::construct_randomly(const university& university) {
    chromosome random_chromosome;

    const size_t lessons_count = university.get_lessons_count();
    random_chromosome.scheduled_lessons.reserve(lessons_count);

    for (size_t lesson_idx = 0; lesson_idx < lessons_count; lesson_idx++) {
        const auto& lesson = university.get_lesson(lesson_idx);
        auto students_count = university.get_group(lesson.group_id).students_count;

        auto& scheduled_lesson = random_chromosome.scheduled_lessons.emplace_back();

        scheduled_lesson.day_idx = random_unsigned_int(DAYS::monday, DAYS::friday);
        scheduled_lesson.para_idx = random_unsigned_int(PARA::first, PARA::fifth);

        scheduled_lesson.teacher_id =
            (lesson.is_practice) ? university.get_random_practice_teacher(lesson.discipline_id) :
            university.get_lecturer(lesson.discipline_id);

        scheduled_lesson.classroom_id = university.get_random_classroom(students_count);
    }

    return random_chromosome;
}

chromosome chromosome::crossover(const chromosome& a, const chromosome& b) {
    chromosome crossed_chromosome;
    crossed_chromosome.scheduled_lessons.reserve(a.scheduled_lessons.size());

    for (size_t lesson_idx = 0; lesson_idx < a.scheduled_lessons.size(); lesson_idx++) {
        crossed_chromosome.scheduled_lessons.push_back(scheduled_lesson::crossover(a.scheduled_lessons[lesson_idx],
                                                                                   b.scheduled_lessons[lesson_idx]));
    }

    return crossed_chromosome;
}

population population::construct_randomly(const university& university) {
    population random_population;

    for (size_t i = 0; i < POPULATION_SIZE; i++) {
        random_population.add(chromosome::construct_randomly(university));
    }

    return random_population;
}

chromosome population::mutation(chromosome&&) {
    // simple logic
}

population::population() {
    chromosomes.reserve(POPULATION_SIZE);
}

void population::mutate() {
    population new_population;

    chromosome a = *select_randomly(chromosomes.begin(), chromosomes.end());
    chromosome b = *select_randomly(chromosomes.begin(), chromosomes.end());

    new_population.add(mutation(chromosome::crossover(a, b)));
}

void population::add(chromosome&& chromosome) {
    chromosomes.push_back(std::move(chromosome));
}