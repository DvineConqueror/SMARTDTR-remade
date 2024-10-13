package com.example.smartdtr_remade.activityTeachers

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.smartdtr_remade.R
import com.example.smartdtr_remade.change_password
import com.example.smartdtr_remade.teacher_account_details
import com.example.smartdtr_remade.teacher_appointment
import com.example.smartdtr_remade.teacher_history
import com.example.smartdtr_remade.teacher_home_page
import com.example.smartdtr_remade.databinding.ActivityMainTeacherBinding
import com.example.smartdtr_remade.teacher_create_appointment
import com.example.smartdtr_remade.PreferencesManager
import com.example.smartdtr_remade.Api.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.material.bottomsheet.BottomSheetDialog

class Main_teacher : AppCompatActivity() {

    private lateinit var binding: ActivityMainTeacherBinding
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainTeacherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesManager = PreferencesManager(this) // Initialize PreferencesManager
        replaceFragment(teacher_home_page())

        binding.btmNavView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home_btn -> replaceFragment(teacher_home_page())
                R.id.appointment_btn -> replaceFragment(teacher_appointment())
                R.id.menu_btn -> showBottomSheetDialog()
                else -> {
                    // Handle other cases if needed
                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.activity_bottom_sheet_layout, null)

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()

        val button1 = view.findViewById<Button>(R.id.btn_change_password)
        val button2 = view.findViewById<Button>(R.id.btn_user_details)
        val button3 = view.findViewById<Button>(R.id.btn_history)
        val button4 = view.findViewById<Button>(R.id.btn_log_out)

        button1.setOnClickListener {
            replaceFragment(change_password())
            bottomSheetDialog.dismiss()
        }
        button2.setOnClickListener {
            replaceFragment(teacher_account_details())
            bottomSheetDialog.dismiss()
        }
        button3.setOnClickListener {
            replaceFragment(teacher_history())
            bottomSheetDialog.dismiss()
        }
        button4.setOnClickListener {
            showLogoutConfirmationDialog(bottomSheetDialog)
        }
    }

    private fun showLogoutConfirmationDialog(bottomSheetDialog: BottomSheetDialog) {
        val dialogView = layoutInflater.inflate(R.layout.activity_alert_dialog2, null)

        val builder = AlertDialog.Builder(this@Main_teacher, R.style.CustomAlertDialog)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.show()

        val btnCancel = dialogView.findViewById<Button>(R.id.btnDialogCancel)
        val btnLogOut = dialogView.findViewById<Button>(R.id.btnDialogLogOut)

        // Cancel button function
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        // Logout button function
        btnLogOut.setOnClickListener {
            val token = preferencesManager.getToken() // Get the stored token

            if (token != null) {
                logoutUser(token)
            } else {
                Toast.makeText(this, "No token found. Please log in again.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@Main_teacher, activity_login::class.java))
                finish() // Close current activity
            }

            dialog.dismiss()
            bottomSheetDialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun logoutUser(token: String) {
        val apiService = ApiService.create() // Create Retrofit instance

        apiService.logout("Bearer $token").enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    preferencesManager.clear() // Clear the token on successful logout
                    Toast.makeText(this@Main_teacher, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@Main_teacher, activity_login::class.java))
                    finish() // Close current activity
                } else {
                    Toast.makeText(this@Main_teacher, "Failed to log out", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@Main_teacher, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
