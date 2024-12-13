package com.example.smartdtr_remade.activitySplashScreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.smartdtr_remade.activityTeachers.Main_teacher
import com.example.smartdtr_remade.activityStudents.Main_student
import com.example.smartdtr_remade.activityTeachers.activity_login
import com.example.smartdtr_remade.PreferencesManager
import com.example.smartdtr_remade.R

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferencesManager = PreferencesManager(this)

        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            val token = preferencesManager.getToken()
            val userType = preferencesManager.getUserType() // Get the user type from PreferencesManager
            Log.d("SplashScreenActivity", "Token: $token, UserType: $userType") // Debugging log

            if (token != null) {
                // Navigate based on user type
                when (userType) {
                    "teacher" -> startActivity(Intent(this, Main_teacher::class.java))
                    "student" -> startActivity(Intent(this, Main_student::class.java))
                    else -> startActivity(Intent(this, activity_login::class.java)) // Fallback if user type is not recognized
                }
            } else {
                startActivity(Intent(this, activity_login::class.java))
            }

            finish()
        }, 1500)
    }
}

