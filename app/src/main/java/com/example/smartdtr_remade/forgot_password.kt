package com.example.smartdtr_remade

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.smartdtr_remade.activityTeachers.activity_login
import com.example.smartdtr_remade.databinding.ActivityForgotPasswordBinding

class forgot_password : AppCompatActivity() {
    // Declare the binding variable
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inflate the layout using binding
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        IDFocusListener()

        val backBtn: Button = binding.btnBack
        val forgotPassButton: Button = binding.btResetPassword

        backBtn.setOnClickListener {
            startActivity(
                Intent(
                    this@forgot_password,
                    activity_login::class.java
                )
            )
        }

        forgotPassButton.setOnClickListener {
            startActivity(
                Intent(
                    this@forgot_password,
                    activity_login::class.java
                )
            )
        }
    }

    private fun IDFocusListener() {
        binding.etTextFieldUserID.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.etTextFieldUserID.helperText = validID()
            }
        }
    }

    private fun validID(): String? {
        val etUserID = binding.etTextFieldUserID.editText?.text.toString()
        val teacherPattern = "^T-\\d{5}$".toRegex()
        val studentPattern = "^S-\\d{5}$".toRegex()

        return if (!teacherPattern.matches(etUserID) && !studentPattern.matches(etUserID)) {
            "Please enter a valid ID Number!"
        } else {
            null
        }
    }
}
