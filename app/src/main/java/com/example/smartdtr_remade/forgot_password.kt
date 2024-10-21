package com.example.smartdtr_remade

import android.app.AlertDialog
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

        setupFocusListeners()

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, activity_login::class.java))
        }

        binding.btResetPassword.setOnClickListener {
            if (validateForm()) {
                showLogoutConfirmationDialog()
            } else {
                Toast.makeText(this, "Please double-check the information provided!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupFocusListeners() {
        binding.etTextFieldUserID.editText?.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.etTextFieldUserID.helperText = validID()
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
        return validID() == null && validateNewPassword() == null && validateConfirmPassword() == null
    }

    private fun resetPassword() {
        val userId = binding.etTextFieldUserID.editText?.text.toString().trim()
        val newPassword = binding.etTextFieldNewPassword.editText?.text.toString().trim()

        val resetPasswordRequest = ResetPasswordRequest(
            userId = userId,
            new_password = newPassword,
            new_password_confirmation = newPassword // Pass new password as confirmation
        )

        val apiService = ApiService.create()
        apiService.changePassword(resetPasswordRequest).enqueue(object : Callback<ResetPasswordResponse> {
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
            showSuccessDialog(response.body()?.message ?: "Password reset successfully!")
        } else {
            Log.e("ResetPasswordResponse", "Failed: ${response.message()}")
            val errorMessage = response.errorBody()?.string() ?: "Password reset failed."
            Toast.makeText(this@forgot_password, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Proceed with Password Reset?")
            .setMessage("Do you wish to proceed with resetting your password?")
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                resetPassword() // Call the password reset method
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "Password Reset Aborted", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false)
            .show()
    }

    private fun showSuccessDialog(successMessage: String) {
        AlertDialog.Builder(this)
            .setTitle("Password Reset")
            .setMessage(successMessage)
            .setPositiveButton("OK") { innerDialog, _ ->
                innerDialog.dismiss()
                startActivity(Intent(this, activity_login::class.java))
                finish() // Close current activity
            }
            .setCancelable(false)
            .show()
    }
}
