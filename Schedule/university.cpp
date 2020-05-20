//
// Created by Kyrylo Mykhailov on 14.04.2020.
//

#include "university.h"
#include "random_utils.h"

#include <algorithm>

university::university(std::string&& univerversity_name) : name(univerversity_name) {};

void university::add_teacher(id teacher_id, teacher_data&& teacher_data) {
    teachers.insert({teacher_id, std::move(teacher_data)});
}

void university::add_discipline(id discipline_id, discipline_data&& discipline_data) {
    disciplines.insert({discipline_id, std::move(discipline_data)});
}

void university::add_classroom(id classroom_id, classroom_data&& classroom_data) {
    classrooms.insert({classroom_id, classroom_data});
}

void university::add_group(id group_id, group_data&& group_data) {
    groups.insert({group_id, std::move(group_data)});
}

const teacher_data& university::get_teacher(id teacher_id) const {
    return teachers.at(teacher_id);
}

const discipline_data& university::get_discipline(id discipline_id) const {
    return disciplines.at(discipline_id);
}

const classroom_data& university::get_classroom(id classroom_id) const {
    return classrooms.at(classroom_id);
}

const group_data& university::get_group(id group_id) const {
    return groups.at(group_id);
}

id university::get_random_classroom(uint16_t capacity) const {
    id random_classroom_id = -1;
    for (const auto&[classroom_id, classroom_data] : classrooms) {
        if (classroom_data.capacity < capacity) continue;

        if (random_classroom_id == -1) {
            random_classroom_id = classroom_id;
            continue;
        }

        if (random_bool()) {
            random_classroom_id = classroom_id;
        }
    }

    assert(random_classroom_id != -1);

    return random_classroom_id;
}

id university::get_random_practice_teacher(id discipline_id) const {
    const auto& practice_teachers = disciplines.at(discipline_id).practice_teachers;
    return *select_randomly(practice_teachers.begin(), practice_teachers.end());
}

id university::get_lecturer(id discipline_id) const {
    return disciplines.at(discipline_id).lecturer;
}

id university::get_teacher_rank(id teacher_id) const {
    return teachers.at(teacher_id).rank_id;
}

lesson_data university::get_lesson(size_t lesson_idx) const {
    return lessons[lesson_idx];
}

void university::construct_lessons() {
    for (const auto&[group_id, group_data] : groups) {
        for (auto discipline_id : group_data.disciplines) {

            const auto discipline_data = get_discipline(discipline_id);

            for (size_t lecture_idx = 0; lecture_idx < discipline_data.lectures_count_per_week; lecture_idx++) {
                lesson_data lecture = {group_id, discipline_id, false};
                lessons.push_back(lecture);
            }

            for (size_t practice_idx = 0; practice_idx < discipline_data.practice_count_per_week; practice_idx++) {
                lesson_data practice = {group_id, discipline_id, true};
                lessons.push_back(practice);
            }
        }
    }

    // TODO(me) : check if I need shuffle here
    std::random_device rd;
    std::mt19937 g(rd());

    std::shuffle(lessons.begin(), lessons.end(), g);
}

size_t university::get_lessons_count() const {
    return lessons.size();
}
