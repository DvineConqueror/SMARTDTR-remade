package com.example.smartdtr_remade.activityTeachers

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartdtr_remade.R
import com.example.smartdtr_remade.activityStudents.student_login

class teacher_create_account : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_teacher_create_account)

        val signupButton: Button = findViewById(R.id.btnNext)
        val spinnerSex: Spinner = findViewById(R.id.spinnerSex)
        val adapterSex: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.sex_options,
            android.R.layout.simple_spinner_item
        )
        adapterSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSex.adapter = adapterSex

        signupButton.setOnClickListener{
            startActivity(
                Intent(
                    this@teacher_create_account,
                    student_login::class.java
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