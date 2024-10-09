package com.example.smartdtr_remade.activityTeachers

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.smartdtr_remade.R
import com.example.smartdtr_remade.change_password
import com.example.smartdtr_remade.teacher_account_details
import com.example.smartdtr_remade.teacher_appointment
import com.example.smartdtr_remade.teacher_history
import com.example.smartdtr_remade.teacher_home_page
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.example.smartdtr_remade.databinding.ActivityMainTeacherBinding

class Main_teacher : AppCompatActivity() {

    private lateinit var binding: ActivityMainTeacherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainTeacherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(teacher_home_page())

        binding.btmNavView.setOnItemSelectedListener {

            when (it.itemId) {

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
            replaceFragment(teacher_account_details())
            bottomSheetDialog.dismiss()
        }
        button3.setOnClickListener {
            replaceFragment(teacher_history())
            bottomSheetDialog.dismiss()
        }
        button4.setOnClickListener {
            // Handle Button 4 click
            bottomSheetDialog.dismiss()
        }
    }


}