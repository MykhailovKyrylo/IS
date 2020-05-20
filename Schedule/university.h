//
// Created by Kyrylo Mykhailov on 14.04.2020.
//

#pragma once

#include <string>
#include <unordered_map>
#include <vector>
#include <cstdint>

using id = size_t;

struct teacher_data {
    id rank_id;
};

struct discipline_data {
    uint16_t lectures_count_per_week;
    uint16_t practice_count_per_week;
    id lecturer;
    std::vector<id> practice_teachers;
};

struct classroom_data {
    uint16_t capacity;
};

struct group_data {
    uint16_t students_count;
    std::vector<id> disciplines;
};

struct lesson_data {
    id group_id;
    id discipline_id;
    bool is_practice;
};

class university {
  public:
    explicit university(std::string&& university_name);

    void construct_lessons();

    void add_teacher(id teacher_id, teacher_data&& teacher_data);
    void add_discipline(id discipline_id, discipline_data&& discipline_data);
    void add_classroom(id classroom_id, classroom_data&& classroom_data);
    void add_group(id group_id, group_data&& group_data);

    const teacher_data& get_teacher(id teacher_id) const;
    const discipline_data& get_discipline(id discipline_id) const;
    const classroom_data& get_classroom(id classroom_id) const;
    const group_data& get_group(id group_id) const;

    id get_random_classroom(uint16_t capacity) const;
    id get_random_practice_teacher(id discipline_id) const;
    id get_lecturer(id discipline_id) const;
    id get_teacher_rank(id teacher_id) const;

    lesson_data get_lesson(size_t lesson_idx) const;
    size_t get_lessons_count() const;
  private:
    std::string name;
    std::unordered_map<id, teacher_data> teachers;
    std::unordered_map<id, discipline_data> disciplines;
    std::unordered_map<id, classroom_data> classrooms;
    std::unordered_map<id, group_data> groups;

    std::vector<lesson_data> lessons;
};





