//
// Created by Kyrylo Mykhailov on 20.05.2020.
//

#include "solution.h"
#include "random_utils.h"

constexpr size_t POPULATION_SIZE = 200;
constexpr size_t CHROMOSOME_GENERATION_COUNT = 10;
constexpr double MUTATION_PROBABILITY = 0.1;

size_t generate_random_day() {
    return random_unsigned_int(DAYS::monday, DAYS::friday);
}

size_t generate_random_lesson() {
    return random_unsigned_int(LESSONS::first, LESSONS::fifth);
}

scheduled_lesson scheduled_lesson::crossover(const scheduled_lesson& a, const scheduled_lesson& b) {
    scheduled_lesson crossed_scheduled_lesson {a.data};

    crossed_scheduled_lesson.day_idx = random_bool() ? a.day_idx : b.day_idx;
    crossed_scheduled_lesson.para_idx = random_bool() ? a.para_idx : b.para_idx;
    crossed_scheduled_lesson.teacher_id = random_bool() ? a.teacher_id : b.teacher_id;
    crossed_scheduled_lesson.classroom_id = random_bool() ? a.classroom_id : b.classroom_id;

    return crossed_scheduled_lesson;
}

scheduled_lesson::scheduled_lesson(lesson_data data_) : data(data_) {}

chromosome chromosome::construct_randomly(const university& university) {
    chromosome random_chromosome;

    const size_t lessons_count = university.get_lessons_count();
    random_chromosome.scheduled_lessons.reserve(lessons_count);

    for (size_t lesson_idx = 0; lesson_idx < lessons_count; lesson_idx++) {
        auto lesson_data = university.get_lesson(lesson_idx);
        auto students_count = university.get_group(lesson_data.group_id).students_count;

        scheduled_lesson random_scheduled_lesson {lesson_data};

        random_scheduled_lesson.day_idx = generate_random_day();
        random_scheduled_lesson.para_idx = generate_random_lesson();

        random_scheduled_lesson.teacher_id =
            (lesson_data.is_practice) ? university.get_random_practice_teacher(lesson_data.discipline_id) :
            university.get_lecturer(lesson_data.discipline_id);

        random_scheduled_lesson.classroom_id = university.get_random_classroom(students_count);

        random_chromosome.scheduled_lessons.push_back(random_scheduled_lesson);
        random_chromosome.add(random_scheduled_lesson);
    }

    return random_chromosome;
}

chromosome chromosome::crossover(const chromosome& a, const chromosome& b) {
    std::vector<chromosome> crossed_chromosomes;
    crossed_chromosomes.reserve(CHROMOSOME_GENERATION_COUNT);

    for (int i = 0; i < CHROMOSOME_GENERATION_COUNT; i++) {
        chromosome crossed_chromosome;
        crossed_chromosome.scheduled_lessons.reserve(a.scheduled_lessons.size());

        for (size_t lesson_idx = 0; lesson_idx < a.scheduled_lessons.size(); lesson_idx++) {
            crossed_chromosome.scheduled_lessons.push_back(scheduled_lesson::crossover(a.scheduled_lessons[lesson_idx],
                                                                                       b.scheduled_lessons[lesson_idx]));
        }

        crossed_chromosomes.push_back(std::move(crossed_chromosome));
    }

    auto cmp = [](const chromosome& lhs, const chromosome& rhs){
        return lhs.fitness() > rhs.fitness();
    };
    std::sort(crossed_chromosomes.begin(), crossed_chromosomes.end(), cmp);

    return crossed_chromosomes[0];
}

void chromosome::add(scheduled_lesson scheduled_lesson) {
    const size_t day_and_para_idx = scheduled_lesson.day_idx * LESSONS_COUNT + scheduled_lesson.para_idx;

    auto teacher_id = scheduled_lesson.teacher_id;
    if (!teachers_lessons.count(teacher_id)) {
        teachers_lessons.insert({teacher_id, std::vector<int>(SLOTS_COUNT, 0)});
    }
    if (teachers_lessons[teacher_id][day_and_para_idx]) {
        mismatches_count++;
    }
    teachers_lessons[teacher_id][day_and_para_idx]++;

    auto classroom_id = scheduled_lesson.classroom_id;
    if (!classroom_lessons.count(classroom_id)) {
        classroom_lessons.insert({classroom_id, std::vector<int>(SLOTS_COUNT, 0)});
    }
    if (classroom_lessons[classroom_id][day_and_para_idx]) {
        mismatches_count++;
    }
    classroom_lessons[classroom_id][day_and_para_idx]++;

    auto group_id = scheduled_lesson.data.group_id;
    if (!groups_lessons.count(group_id)) {
        groups_lessons.insert({group_id, std::vector<int>(SLOTS_COUNT, 0)});
    }
    if (groups_lessons[group_id][day_and_para_idx]) {
        mismatches_count++;
    }
    groups_lessons[group_id][day_and_para_idx]++;
}

void chromosome::remove(scheduled_lesson scheduled_lesson) {
    const size_t day_and_para_idx = scheduled_lesson.day_idx * LESSONS_COUNT + scheduled_lesson.para_idx;

    auto teacher_id = scheduled_lesson.teacher_id;
    teachers_lessons[teacher_id][day_and_para_idx]--;
    if (teachers_lessons[teacher_id][day_and_para_idx] > 0) {
        mismatches_count--;
    }

    auto classroom_id = scheduled_lesson.classroom_id;
    classroom_lessons[classroom_id][day_and_para_idx]--;
    if (classroom_lessons[classroom_id][day_and_para_idx] > 0) {
        mismatches_count--;
    }

    auto group_id = scheduled_lesson.data.group_id;
    groups_lessons[group_id][day_and_para_idx]++;
    if (groups_lessons[group_id][day_and_para_idx] > 0) {
        mismatches_count++;
    }
}

bool chromosome::is_valid() const {
    return (mismatches_count == 0);
}

double chromosome::fitness() const {
    if (mismatches_count == 0) {
        return VALID;
    }
    return 1. / mismatches_count;
}

population population::construct_randomly(const university& university) {
    population random_population;

    for (size_t i = 0; i < POPULATION_SIZE; i++) {
        random_population.add(chromosome::construct_randomly(university));
    }

    return random_population;
}

chromosome population::mutation(chromosome&& chromosome, const university& university) {
    if (chromosome.is_valid()) {
        return chromosome;
    }

    for (size_t lesson_idx = 0; lesson_idx < university.get_lessons_count(); lesson_idx++) {
        auto lesson_data = university.get_lesson(lesson_idx);
        auto mutated_scheduled_lesson = chromosome.scheduled_lessons[lesson_idx];

        bool mutated = false;

        if (random_probability() < MUTATION_PROBABILITY) {
            mutated_scheduled_lesson.day_idx = generate_random_day();
            mutated = true;
        }

        if (random_probability() < MUTATION_PROBABILITY) {
            mutated_scheduled_lesson.para_idx = generate_random_lesson();
            mutated = true;
        }

        if (random_probability() < MUTATION_PROBABILITY) {
            auto students_count = university.get_group(lesson_data.group_id).students_count;
            mutated_scheduled_lesson.classroom_id = university.get_random_classroom(students_count);
            mutated = true;
        }

        if (random_probability() < MUTATION_PROBABILITY) {
            if (lesson_data.is_practice) {
                mutated_scheduled_lesson.teacher_id = university.get_random_practice_teacher(lesson_data.discipline_id);
            } else {
                mutated_scheduled_lesson.teacher_id = university.get_lecturer(lesson_data.discipline_id);
            }
            mutated = true;
        }

        if (mutated) {
            chromosome.remove(chromosome.scheduled_lessons[lesson_idx]);
            chromosome.add(mutated_scheduled_lesson);
        }
    }

    return chromosome;
}

population::population() {
    chromosomes.reserve(POPULATION_SIZE);
}

population population::selection(const university& university) {
    population new_population;

    chromosome a = *select_randomly(chromosomes.begin(), chromosomes.end());
    chromosome b = *select_randomly(chromosomes.begin(), chromosomes.end());

    new_population.add(mutation(chromosome::crossover(a, b), university));
    return new_population;
}

bool population::is_valid() const {
    for (const auto& chromosome : chromosomes) {
        if (chromosome.fitness() == chromosome::VALID) {
            return true;
        }
    }

    return false;
}

const chromosome & population::get_valid_chromosome() const {
    for (const auto& chromosome : chromosomes) {
        if (chromosome.fitness() == chromosome::VALID) {
            return chromosome;
        }
    }
}

void population::add(chromosome&& chromosome) {
    chromosomes.push_back(std::move(chromosome));
}

int population::get_mismatches_count() const {
    int mismatches_count = 0;
    for (const auto& chromosome : chromosomes) {
        mismatches_count += chromosome.mismatches_count;
    }

    return mismatches_count;
}