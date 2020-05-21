//
// Created by Kyrylo Mykhailov on 21.05.2020.
//

#include "random_utils.h"

bool random_bool() {
    static auto gen = std::bind(std::uniform_int_distribution<>(0, 1), std::default_random_engine());
    return gen();
}

double random_probability() {
    std::random_device randomDevice;
    std::mt19937 generator(randomDevice());
    std::uniform_real_distribution<double> distribution(0.0, 1.0);
    return distribution(generator);
}

size_t random_unsigned_int(size_t from, size_t to) {
    return from + static_cast<size_t>((to - from) * random_probability());
}


