package com.example.smartdtr_remade.ApiService.model

class DutyRequest(// Getters and setters
    var id: Int,
    var teacherId: Int, // Nullable
    var studentId: Int,
    var subject: String,
    var room: String,
    var date: String,
    var startTime: String,
    var endTime: String,
    var status: String
)
