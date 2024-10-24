package com.example.smartdtr_remade.models

import java.io.Serializable

data class Student(
    val id: Int,
    val email: String,
    val firstname: String,
    val lastname: String,
    val mobile_number: String,
    val password: String,
    val sex: String,
    val student_id: String,
    val year_level: String,
    var isChecked: Boolean = false
): Serializable