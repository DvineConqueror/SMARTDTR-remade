package com.example.smartdtr_remade.activityTeachers

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.smartdtr_remade.R
import com.example.smartdtr_remade.teacher_student_list

class teacher_duty_card : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rv_teacher_duty_card) // Use your actual layout file name

        // Set up the ImageButton
        val btStudentList: ImageButton = findViewById(R.id.btStudentList)

        btStudentList.setOnClickListener {
            // Navigate to the StudentListActivity
            openStudentListActivity()
        }
    }

    private fun openStudentListActivity() {
        val intent = Intent(this, teacher_student_list::class.java)
        startActivity(intent)
    }
}
