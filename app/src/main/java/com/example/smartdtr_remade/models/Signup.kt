package com.example.smartdtr_remade.models

import User

data class SignUpRequest(
    val firstname: String,
    val lastname: String,
    val email: String,
    val password: String,
    val mobile_number: String,
    val ID: String,
    val sex: String,
    val date_of_birth: String,
    val year_level: String? = null // Optional for teachers
)

data class SignupResponse(
    val message: String,
    val token: String,
    val user: User,
    val userId: String
)

data class User(
    val userId: String,           // Unique identifier for the user
    val name: String,         // Full name of the user
    val student_id: String?,  // Optional student ID (if applicable)
    val teacher_id: String?,   // Optional teacher ID (if applicable)
    val id: String,
    val firstname: String,
    val lastname: String,
    val email: String,
    val mobile_number: String,
    val date_of_birth: String,
    val sex: String,
    val year_level: String?
)

