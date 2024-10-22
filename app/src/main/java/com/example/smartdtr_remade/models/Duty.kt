package com.example.smartdtr_remade.models

import java.io.Serializable

data class Duty(
    val id: Int,
    val teacher_id: Int,
    val teacher_name: String?, // Teacher's name as a nullable string
    val student_ids: List<Int>, // List of student names or IDs
    val subject: String,
    val room: String,
    val date: String,
    val start_time: String,
    val end_time: String,
    val status: String
): Serializable

