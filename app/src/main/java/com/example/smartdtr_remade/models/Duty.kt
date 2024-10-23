package com.example.smartdtr_remade.models

import java.io.Serializable

data class Duty(
    val id: Int,
    val teacher_id: Int,
    val teacher_name: String?,
    val student_ids: List<Int>,
    val subject: String,
    val room: String,
    val date: String,
    val start_time: String,
    val end_time: String,
    val status: String
) : Serializable


data class GetDuty(
    val id: Int,
    val teacher_id: Int,
    val teacher_name: String?, // Teacher's name as a nullable string
    val student_ids: List<Int>, // List of student IDs
    val students: List<Students>, // List of students
    val subject: String,
    val room: String,
    val date: String,
    val start_time: String,
    val end_time: String,
    val status: String,
) : Serializable

data class Students(
    val id: Int,
    val firstname: String,
    val lastname: String,
    val student_id: String,
) : Serializable
