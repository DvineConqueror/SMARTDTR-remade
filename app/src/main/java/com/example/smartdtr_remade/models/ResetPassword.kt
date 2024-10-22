package com.example.smartdtr_remade.models

data class ResetPasswordRequest(
    val userId: String,
    val new_password: String,
    val new_password_confirmation: String // Added for confirmation
)

data class ResetPasswordResponse(
    val message: String
)
