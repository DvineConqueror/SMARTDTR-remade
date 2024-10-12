package com.example.smartdtr_remade.activityTeachers

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.smartdtr_remade.R
import com.example.smartdtr_remade.databinding.FragmentTeacherStudentListBinding
import com.example.smartdtr_remade.databinding.RvTeacherDutyCardBinding
// Change this to your actual binding class name
import com.example.smartdtr_remade.teacher_create_appointment
import com.example.smartdtr_remade.teacher_student_list

class teacher_duty_card : AppCompatActivity() {

    private lateinit var binding: RvTeacherDutyCardBinding // Use the correct binding class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RvTeacherDutyCardBinding.inflate(layoutInflater) // Inflate the binding layout
        setContentView(binding.root) // Set the content view to the root of the binding

        // Set up the ImageButton
        binding.editButton.setOnClickListener {
            // Replace the current activity with teacher_create_appointment
            val intent = Intent(this, teacher_create_appointment::class.java)
            startActivity(intent)
        }

        binding.btStudentList.setOnClickListener {
            // Replace the current activity with teacher_student_list
            val intent = Intent(this, teacher_student_list::class.java)
            startActivity(intent)
        }
    }
}
