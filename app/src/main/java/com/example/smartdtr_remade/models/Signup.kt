package com.example.smartdtr_remade.models

data class SignUpRequest(
    val firstname: String,
    val lastname: String,
    val email: String,
    val password: String,
    val mobileNumber: String,
    val ID: String,
    val sex: String
)
