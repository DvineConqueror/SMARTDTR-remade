package com.example.smartdtr_remade.models

class Dutydata class Duty(
    val id: Int,
    val teacher_id: Int,
    val teacher_name: String?,
    val student_id: Int?,
    val student_name: String?,
    val subject: String,
    val room: String,
    val date: String,
    val start_time: String,
    val end_time: String,
    val status: String
)
