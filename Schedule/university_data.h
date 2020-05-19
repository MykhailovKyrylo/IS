//
// Created by Kyrylo Mykhailov on 25.04.2020.
//

#pragma once

#include <string>
#include <map>

#include "university.h"

namespace university_data
{

namespace teachers
{

namespace names
{

const std::string KEK = "Kek";
const std::string LOL = "Lol";
const std::string HEH = "Heh";
const std::string AHAH = "Ahah";

} // names

namespace rank
{

const std::string PROFESSOR = "professor";
const std::string DOCTOR = "doctor";
const std::string ASSOCIATE_PROFESSOR = "associate professor";
const std::string GRADUATE_STUDENT = "graduate student";

} // rank

} // teachers

namespace classrooms
{

const std::string N_101 = "101";
const std::string N_102 = "102";
const std::string N_103 = "103";

const std::string N_201 = "201";
const std::string N_202 = "202";
const std::string N_203 = "203";

const std::string N_301 = "301";
const std::string N_302 = "302";
const std::string N_303 = "303";

} // classrooms

namespace disciplines
{

const std::string PROGRAMMING_0 = "programming0";
const std::string PROGRAMMING_1 = "programming1";

const std::string OTHER_0 = "other0";
const std::string OTHER_1 = "other1";
const std::string OTHER_2 = "other2";

} // disciplines

namespace groups
{

const std::string TTP_42 = "TTP42";
const std::string MI_4 = "MI4";
const std::string TK4 = "TK4";

}

} // university_data
