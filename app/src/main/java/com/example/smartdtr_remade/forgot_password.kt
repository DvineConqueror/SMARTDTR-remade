package com.example.smartdtr_remade

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
        etFocusListeners()

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

    private fun etFocusListeners() {
        binding.etTextFieldUserID.editText?.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                val validationMessage = validID()
                Log.d("FocusListener", "Validation triggered. Message: $validationMessage")
                binding.etTextFieldUserID.helperText = validationMessage
            }
        }

        binding.etTextFieldOldPass.editText?.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                val validationMessage = validateOldPassword()
                binding.etTextFieldOldPass.helperText = validationMessage
            }
        }

        binding.etTextFieldNewPassword.editText?.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                val validationMessage = validateNewPassword()
                binding.etTextFieldNewPassword.helperText = validationMessage
            }
        }

        binding.etTextFieldNewPasswordConfirm.editText?.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                val validationMessage = validateConfirmPassword()
                binding.etTextFieldNewPasswordConfirm.helperText = validationMessage
            }
        }
    }


    private fun validID(): String? {
        val etUserID = binding.etTextFieldUserID.editText?.text.toString().trim()
        val teacherPattern = "^T-\\d{5}$".toRegex(RegexOption.IGNORE_CASE)
        val studentPattern = "^S-\\d{5}$".toRegex(RegexOption.IGNORE_CASE)

        return if (!teacherPattern.matches(etUserID) && !studentPattern.matches(etUserID)) {
            "Please enter a valid ID Number!"
        } else {
            null
        }
    }

    private fun validateOldPassword(): String? {
        val oldPassword = binding.etTextFieldOldPass.editText?.text.toString().trim()
        return if (oldPassword.isEmpty()) {
            "Old password is required."
        } else {
            null
        }
    }

    private fun validateNewPassword(): String? {
        val newPassword = binding.etTextFieldNewPassword.editText?.text.toString().trim()
        return when {
            newPassword.length < 8 -> "Password must be at least 8 characters."
            !newPassword.matches(Regex(".*[A-Z].*")) -> "Password must contain at least one uppercase letter."
            !newPassword.matches(Regex(".*\\d.*")) -> "Password must contain at least one number."
            else -> null
        }
    }

    private fun validateConfirmPassword(): String? {
        val newPassword = binding.etTextFieldNewPassword.editText?.text.toString().trim()
        val confirmPassword = binding.etTextFieldNewPasswordConfirm.editText?.text.toString().trim()
        return if (confirmPassword != newPassword) {
            "Passwords do not match."
        } else {
            null
        }
    }
}
