//
// Created by Kyrylo Mykhailov on 20.05.2020.
//

#pragma once

#include "university_data.h"
#include "university.h"

struct scheduled_lesson {
    static scheduled_lesson crossover(const scheduled_lesson& a, const scheduled_lesson& b);

    size_t day_idx;
    size_t para_idx;
    size_t teacher_id;
    size_t classroom_id;
};

struct chromosome {
    static chromosome construct_randomly(const university& university);

    static chromosome crossover(const chromosome& a, const chromosome& b);

    std::vector<scheduled_lesson> scheduled_lessons;
//    std::map<std::string, std::vector<size_t>> teachers_lessons;
//    std::map<std::string, std::vector<size_t>> classroom_lessons;
};

struct population {
    static population construct_randomly(const university& university);

    static chromosome mutation(chromosome&&);

    population();

    void add(chromosome&& chromosome);

    void mutate();

    const std::vector<chromosome> get_chromosomes;

    std::vector<chromosome> chromosomes;
};


