package com.example.smartdtr_remade

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.smartdtr_remade.Api.ApiService
import com.example.smartdtr_remade.activityTeachers.activity_login
import com.example.smartdtr_remade.databinding.ActivityForgotPasswordBinding
import com.example.smartdtr_remade.models.ResetPasswordRequest
import com.example.smartdtr_remade.models.ResetPasswordResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class forgot_password : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        etFocusListeners()

        val backBtn: Button = binding.btnBack
        val forgotPassButton: Button = binding.btResetPassword

        backBtn.setOnClickListener {
            startActivity(Intent(this, activity_login::class.java))
        }

        forgotPassButton.setOnClickListener {
            if (validateForm()) {
                resetPassword()
            } else {
                Toast.makeText(this, "Please fix the errors above", Toast.LENGTH_SHORT).show()
            }
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

    private fun validateForm(): Boolean {
        return validID() == null && validateOldPassword() == null &&
                validateNewPassword() == null && validateConfirmPassword() == null
    }

    private fun resetPassword() {
        val userId = binding.etTextFieldUserID.editText?.text.toString().trim()
        val oldPassword = binding.etTextFieldOldPass.editText?.text.toString().trim()
        val newPassword = binding.etTextFieldNewPassword.editText?.text.toString().trim()

        val resetPassword = ResetPasswordRequest(
            userId = userId,
            oldPassword = oldPassword,
            newPassword = newPassword
        )

        val apiService = ApiService.create()

        // Determine if the user is a student or teacher and call the appropriate API method
        if (userId.startsWith("S-", true)) { // If ID starts with "S-", it's a student
            apiService.updateStudent(userId, resetPassword).enqueue(object : Callback<ResetPasswordResponse> {
                override fun onResponse(call: Call<ResetPasswordResponse>, response: Response<ResetPasswordResponse>) {
                    handleApiResponse(response)
                }

                override fun onFailure(call: Call<ResetPasswordResponse>, t: Throwable) {
                    Toast.makeText(this@forgot_password, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else if (userId.startsWith("T-", true)) { // If ID starts with "T-", it's a teacher
            apiService.updateTeacher(userId, resetPassword).enqueue(object : Callback<ResetPasswordResponse> {
                override fun onResponse(call: Call<ResetPasswordResponse>, response: Response<ResetPasswordResponse>) {
                    handleApiResponse(response)
                }

                override fun onFailure(call: Call<ResetPasswordResponse>, t: Throwable) {
                    Toast.makeText(this@forgot_password, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "Invalid ID format", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleApiResponse(response: Response<ResetPasswordResponse>) {
        if (response.isSuccessful) {
            Log.d("ResetPasswordResponse", "Successful: ${response.body()}")
            Toast.makeText(this@forgot_password, "Password reset successfully!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@forgot_password, activity_login::class.java))
            finish() // Close current activity
        } else {
            Log.e("ResetPasswordResponse", "Failed: ${response.message()}")
            Toast.makeText(this@forgot_password, "Password reset failed: ${response.message()}", Toast.LENGTH_SHORT).show()
        }
    }
}
