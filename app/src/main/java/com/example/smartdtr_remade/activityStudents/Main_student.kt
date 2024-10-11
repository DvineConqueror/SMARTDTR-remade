package com.example.smartdtr_remade.activityStudents

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.example.smartdtr_remade.R
import com.example.smartdtr_remade.activityTeachers.activity_login
import com.example.smartdtr_remade.change_password
import com.example.smartdtr_remade.databinding.ActivityMainStudentBinding
import com.example.smartdtr_remade.student_account_details
import com.example.smartdtr_remade.student_appointment
import com.example.smartdtr_remade.student_history
import com.example.smartdtr_remade.student_home_page
import com.google.android.material.bottomsheet.BottomSheetDialog

class Main_student : AppCompatActivity() {

    private lateinit var binding: ActivityMainStudentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(student_home_page())

        binding.btmNavView.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.home_btn -> replaceFragment(student_home_page())
                R.id.appointment_btn -> replaceFragment(student_appointment())
                R.id.menu_btn -> showBottomSheetDialog()

                else -> {


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

        // Optionally, handle button clicks in the Bottom Sheet
        val button1 = view.findViewById<Button>(R.id.btn_change_password)
        val button2 = view.findViewById<Button>(R.id.btn_user_details)
        val button3 = view.findViewById<Button>(R.id.btn_history)
        val button4 = view.findViewById<Button>(R.id.btn_log_out)

        button1.setOnClickListener {
            // Handle Button 1 click
            replaceFragment(change_password())
            bottomSheetDialog.dismiss()
        }
        button2.setOnClickListener {
            // Handle Button 2 click
            replaceFragment(student_account_details())
            bottomSheetDialog.dismiss()
        }
        button3.setOnClickListener {
            replaceFragment(student_history())
            bottomSheetDialog.dismiss()
        }
        button4.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.activity_alert_dialog2, null)

            // Create an AlertDialog and set the custom view
            val builder = AlertDialog.Builder(this@Main_student, R.style.CustomAlertDialog)
            builder.setView(dialogView)

            // Create and show the AlertDialog
            val dialog = builder.create()
            dialog.show()

            // Get references to the buttons in the custom layout
            val btnCancel = dialogView.findViewById<Button>(R.id.btnDialogCancel)
            val btnLogOut = dialogView.findViewById<Button>(R.id.btnDialogLogOut)

            //Cancel button function
            btnCancel.setOnClickListener{
                dialog.dismiss()
            }

            btnLogOut.setOnClickListener{
                bottomSheetDialog.dismiss()
                startActivity(
                    Intent(
                        this@Main_student,
                        activity_login::class.java
                    )
                )
            }
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.show()
        }
    }
}