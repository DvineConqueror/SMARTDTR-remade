package com.example.smartdtr_remade

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartdtr_remade.activityTeachers.activity_login

class forgot_password : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)

        val backBtn: Button = findViewById(R.id.btnBack)
        val forgotPassButton:Button = findViewById(R.id.btResetPassword)

        backBtn.setOnClickListener{
            startActivity(
                Intent(
                    this@forgot_password,
                    activity_login::class.java
                )
            )
        }

        forgotPassButton.setOnClickListener{
            startActivity(
                Intent(
                    this@forgot_password,
                    activity_login::class.java
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