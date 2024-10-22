package com.example.smartdtr_remade

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.smartdtr_remade.Api.ApiService
import com.example.smartdtr_remade.activityTeachers.activity_login
import com.example.smartdtr_remade.databinding.FragmentChangePasswordBinding
import com.example.smartdtr_remade.models.ResetPasswordRequest
import com.example.smartdtr_remade.models.ResetPasswordResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class change_password : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)

        // Initialize PreferencesManager
        preferencesManager = PreferencesManager(requireContext())

        setupFocusListeners()

        binding.btResetPassword.setOnClickListener {
            if (validateForm()) {
                showLogoutConfirmationDialog()
            } else {
                Toast.makeText(requireContext(), "Ensure that all details entered are accurate!", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
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
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleApiResponse(response: Response<ResetPasswordResponse>) {
        if (response.isSuccessful) {
            Log.d("ResetPasswordResponse", "Successful: ${response.body()}")
            showPasswordChangedDialog()
        } else {
            Log.e("ResetPasswordResponse", "Failed: ${response.message()}")
            val errorMessage = response.errorBody()?.string() ?: "Password change failed."
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLogoutConfirmationDialog() {
        showAlertDialog(
            title = "Proceed with Password Change?",
            message = "You will be logged out of your account if you proceed. Do you want to continue?",
            positiveText = "Yes",
            positiveAction = {
                resetPassword() // Call the password reset method
            },
            negativeText = "No",
            negativeAction = {
                Toast.makeText(requireContext(), "Password Change Aborted", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun showPasswordChangedDialog() {
        showAlertDialog(
            title = "Password Changed",
            message = "Your password has been successfully changed. You will now be logged out.",
            positiveText = "OK",
            positiveAction = {
                // Clear the stored token
                preferencesManager.clearToken() // Call the new method to clear the token

                startActivity(Intent(requireContext(), activity_login::class.java))
                activity?.finish() // Close current fragment
            }
        )
    }

    private fun showAlertDialog(
        title: String,
        message: String,
        positiveText: String,
        positiveAction: () -> Unit,
        negativeText: String? = null,
        negativeAction: (() -> Unit)? = null
    ) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveText) { dialog, _ ->
                dialog.dismiss()
                positiveAction()
            }

        // Show the negative button if text and action are provided
        negativeText?.let {
            builder.setNegativeButton(it) { dialog, _ ->
                dialog.dismiss()
                negativeAction?.invoke()
            }
        }

        builder.setCancelable(false) // Prevent clicking outside to dismiss
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
