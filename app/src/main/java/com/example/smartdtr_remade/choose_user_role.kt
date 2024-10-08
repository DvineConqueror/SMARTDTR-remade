package com.example.smartdtr_remade

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartdtr_remade.activityStudents.student_login
import com.example.smartdtr_remade.activityTeachers.teacher_login

class choose_user_role : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_choose_user_role)

        // Using findViewById to initialize buttons
        val btnStudent: Button = findViewById(R.id.btnStudent)
        val btnTeacher: Button = findViewById(R.id.btnTeacher)

        btnStudent.setOnClickListener {
            startActivity(
                Intent(
                    this@choose_user_role,
                    student_login::class.java
                )
            )
        }

        // Redirects for Teacher button
        btnTeacher.setOnClickListener{
            startActivity(
                Intent(
                    this@choose_user_role,
                    teacher_login::class.java
                )
            )
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}