package com.example.smartdtr_remade.models

data class ResetPasswordRequest(
    val userId: String,
    val oldPassword: String,
    val newPassword: String
)
