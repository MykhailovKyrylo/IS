//
// Created by Kyrylo Mykhailov on 25.04.2020.
//

#pragma once

#include "university.h"

#include <string>

enum DAYS {
    monday,
    tuesday,
    wednesday,
    thursday,
    friday,
    DAYS_COUNT
};

enum LESSONS {
    first,
    second,
    third,
    fourth,
    fifth,
    LESSONS_COUNT
};

const size_t SLOTS_COUNT = DAYS_COUNT * LESSONS_COUNT;

const std::string DAYS_NAMES[DAYS_COUNT] {
    "monday",
    "tuesday",
    "wednesday",
    "thursday",
    "friday"
};

const std::string PARA_NAMES[LESSONS_COUNT] {
    "first",
    "second",
    "third",
    "fourth",
    "fifth"
};

namespace university_data {

    enum TEACHERS {
        PUPSEN,
        VUPSEN,
        LUNTIK,
        KUZYA,
        MYLA,
        PCHELONOK,
        TEACHERS_COUNT
    };

    enum RANKS {
        PROFESSOR,
        DOCTOR,
        ASSOCIATE_PROFESSOR,
        GRADUATE_STUDENT,
        RANKS_COUNT
    };

    enum CLASSROOMS {
        N_101,
        N_102,
        N_103,
        N_201,
        N_202,
        N_203,
        N_301,
        N_302,
        N_303,
        CLASSROOMS_COUNT
    };

    enum DISCIPLINES {
        PROGRAMMING_0,
        PROGRAMMING_1,
        PROGRAMMING_2,
        PROGRAMMING_3,
        OTHER_0,
        OTHER_1,
        DISCIPLINES_COUNT
    };

    enum GROUPS {
        TTP_42,
        MI_4,
        GROUPS_COUNT
    };

    const std::string TEACHERS_NAMES[TEACHERS_COUNT] {
        "PUPSEN",
        "VUPSEN",
        "LUNTIK",
        "KUZYA",
        "MYLA",
        "PCHELONOK"
    };

    const std::string RANKS_NAMES[RANKS_COUNT] {
        "PROFESSOR",
        "DOCTOR",
        "ASSOCIATE_PROFESSOR",
        "GRADUATE_STUDENT"
    };

    const std::string CLASSROOMS_NAMES[CLASSROOMS_COUNT] {
        "N_101",
        "N_102",
        "N_103",
        "N_201",
        "N_202",
        "N_203",
        "N_301",
        "N_302",
        "N_303"
    };

    const std::string DISCIPLINES_NAMES[DISCIPLINES_COUNT] {
        "PROGRAMMING_0",
        "PROGRAMMING_1",
        "PROGRAMMING_2",
        "PROGRAMMING_3",
        "OTHER_0",
        "OTHER_1"
    };

    const std::string GROUPS_NAMES[GROUPS_COUNT] {
        "TTP_42",
        "MI_4"
    };

} // university_data
