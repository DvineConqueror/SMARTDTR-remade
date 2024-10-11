package com.example.smartdtr_remade.models

data class Login(
    val teacher_id: String,
    val student_id: String,
    val password: String

)
class LoginResponse(
    val token: String,
    val user: User
)

data class User(
    val id: Int,
    val username: String,
    val student_id: String,
    val teacher_id: String
)
