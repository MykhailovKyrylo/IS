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

enum PARA {
    first,
    second,
    third,
    fourth,
    fifth,
    PARA_COUNT
};

const std::string DAYS_NAMES[DAYS_COUNT] {
    "monday",
    "tuesday",
    "wednesday",
    "thursday",
    "friday"
};

const std::string PARA_NAMES[PARA_COUNT] {
    "first",
    "second",
    "third",
    "fourth",
    "fifth"
};

namespace university_data {

    enum TEACHERS {
        KEK,
        LOL,
        HEH,
        AHAH,
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
        OTHER_0,
        OTHER_1,
        OTHER_2,
        DISCIPLINES_COUNT
    };

    enum GROUPS {
        TTP_42,
        MI_4,
        TK4,
        GROUPS_COUNT
    };

    const std::string TEACHERS_NAMES[TEACHERS_COUNT] {
        "KEK",
        "LOL",
        "HEH",
        "AHAH"
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
        "OTHER_0",
        "OTHER_1",
        "OTHER_2"
    };

    const std::string GROUPS_NAMES[GROUPS_COUNT] {
        "TTP_42",
        "MI_4",
        "TK4"
    };

} // university_data
