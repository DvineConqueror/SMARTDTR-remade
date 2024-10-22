package com.example.smartdtr_remade.activityTeachers

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartdtr_remade.databinding.RvTeacherDutyCardBinding
// Change this to your actual binding class name
import com.example.smartdtr_remade.teacher_create_appointment
import com.example.smartdtr_remade.teacher_student_list_create

class teacher_duty_card : AppCompatActivity() {

    private lateinit var binding: RvTeacherDutyCardBinding // Use the correct binding class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RvTeacherDutyCardBinding.inflate(layoutInflater) // Inflate the binding layout
        setContentView(binding.root) // Set the content view to the root of the binding
    }
}
