//
// Created by Kyrylo Mykhailov on 20.05.2020.
//

#pragma once

#include "university_data.h"
#include "university.h"

struct scheduled_lesson {
    static scheduled_lesson crossover(const scheduled_lesson& a, const scheduled_lesson& b);

    scheduled_lesson(lesson_data lesson_data);

    const lesson_data data;
    size_t day_idx;
    size_t para_idx;
    size_t teacher_id;
    size_t classroom_id;
};

struct chromosome {
    static constexpr double VALID = 777;
    static chromosome construct_randomly(const university& university);
    static chromosome crossover(const chromosome& a, const chromosome& b);

    void add(scheduled_lesson scheduled_lesson);
    void remove(scheduled_lesson scheduled_lesson);
    bool is_valid() const;
    double fitness() const;

    int mismatches_count{0};
    std::vector<scheduled_lesson> scheduled_lessons;
    std::unordered_map<size_t, std::vector<int>> teachers_lessons;
    std::unordered_map<size_t, std::vector<int>> classroom_lessons;
    std::unordered_map<size_t, std::vector<int>> groups_lessons;
};

struct population {
    static population construct_randomly(const university& university);
    static chromosome mutation(chromosome&& chromosome, const university& university);

    population();

    bool is_valid() const;
    int get_mismatches_count() const;
    const chromosome& get_valid_chromosome() const;

    void add(chromosome&& chromosome);
    population selection(const university& university);

    std::vector<chromosome> chromosomes;
};
