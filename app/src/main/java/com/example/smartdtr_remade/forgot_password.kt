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
        // Add focus listeners to validate inputs
        binding.etTextFieldUserID.editText?.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.etTextFieldUserID.helperText = validID()
            }
        }

        binding.etTextFieldOldPass.editText?.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.etTextFieldOldPass.helperText = validateOldPassword()
            }
        }

        binding.etTextFieldNewPassword.editText?.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.etTextFieldNewPassword.helperText = validateNewPassword()
            }
        }

        binding.etTextFieldNewPasswordConfirm.editText?.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.etTextFieldNewPasswordConfirm.helperText = validateConfirmPassword()
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
        val old_password = binding.etTextFieldOldPass.editText?.text.toString().trim()
        val new_password = binding.etTextFieldNewPassword.editText?.text.toString().trim()

        val resetPassword = ResetPasswordRequest(
            userId = userId,
            old_password = old_password,
            new_password = new_password,
            new_password_confirmation = new_password // Pass new password as confirmation
        )

        val apiService = ApiService.create()

        // Unified API call for both students and teachers
        apiService.changePassword(resetPassword).enqueue(object : Callback<ResetPasswordResponse> {
            override fun onResponse(call: Call<ResetPasswordResponse>, response: Response<ResetPasswordResponse>) {
                handleApiResponse(response)
            }

            override fun onFailure(call: Call<ResetPasswordResponse>, t: Throwable) {
                Toast.makeText(this@forgot_password, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun handleApiResponse(response: Response<ResetPasswordResponse>) {
        if (response.isSuccessful) {
            Log.d("ResetPasswordResponse", "Successful: ${response.body()}")
            Toast.makeText(this@forgot_password, response.body()?.message ?: "Password reset successfully!", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this@forgot_password, activity_login::class.java))
            finish() // Close current activity
        } else {
            Log.e("ResetPasswordResponse", "Failed: ${response.message()}")
            // Handle error message from the response body if available
            val errorMessage = response.errorBody()?.string() ?: "Password reset failed."
            Toast.makeText(this@forgot_password, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

}
