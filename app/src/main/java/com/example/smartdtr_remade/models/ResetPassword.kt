package com.example.smartdtr_remade.models

data class ResetPasswordRequest(
    val userId: String,
    val oldPassword: String,
    val newPassword: String
)

data class ResetPasswordResponse(
    val success: Boolean,
    val message: String,
    val data: StudentData? // Assuming you might want to return student details after a successful update
)

data class StudentData(
    val id: Int,
    val firstname: String,
    val lastname: String,
    val email: String,
    val student_id: String,
    val mobile_number: String,
    val year_level: Int,
    val sex: String,
    val created_at: String,
    val updated_at: String
)

data class TeacherData(
    val id: Int,
    val firstname: String,
    val lastname: String,
    val email: String,
    val teacher_id: String,
    val mobile_number: String,
    val sex: String,
    val created_at: String,
    val updated_at: String
)
