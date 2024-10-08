package com.example.smartdtr_remade.activityTeachers

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartdtr_remade.R
import com.example.smartdtr_remade.activityStudents.Main_student
import com.example.smartdtr_remade.activityStudents.Student_create_account
import com.example.smartdtr_remade.databinding.ActivityTeacherLoginBinding

class teacher_login : AppCompatActivity() {
    private lateinit var binding: ActivityTeacherLoginBinding
    private var isPasswordVisible: Boolean = false

    // Declare your EditText and Button here
    private lateinit var etEmailAddress: EditText
    private lateinit var etPassword: EditText
    private lateinit var btLogin: Button

    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTeacherLoginBinding.inflate(layoutInflater)
        setContentView(binding.root) // Set content view to the binding root

        // Initialize your views
        val etEmailAddress = binding.etEmailAddress
        val etPassword = binding.etPassword
        val btLogin = binding.btLogin
        val textViewButton: TextView = findViewById(R.id.tvClickableSignUp)
        val backButton: Button = findViewById(R.id.btnBack)
        val isPasswordVisible = false

        binding.etPassword.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = binding.etPassword.compoundDrawables[2] // End drawable (right)
                if (drawableEnd != null && event.rawX >= (binding.etPassword.right - drawableEnd.bounds.width())) {
                    togglePasswordVisibility()
                    // Call performClick() for accessibility support
                    binding.etPassword.performClick()
                    return@setOnTouchListener true
                }
            }
            false
        }


        btLogin.setOnClickListener {
            if (etEmailAddress.text.toString() == "testteacher" && etPassword.text.toString() == "admin") {
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                // Optionally navigate to another activity
                startActivity(
                    Intent(
                        this@teacher_login,
                        Main_student::class.java
                    )
                )
            } else {
                Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show()
            }
        }

        //Signup Button
        textViewButton.setOnClickListener{
            startActivity(
                Intent(
                    this@teacher_login,
                    teacher_create_account::class.java
                )
            )
        }

        binding.btnBack.setOnClickListener {
            onBackPressed() // This will take the user to the previous screen
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    // Toggle password visibility and change drawable
    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible

        if (isPasswordVisible) {
            // Set the input type to visible password
            binding.etPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            // Change the drawable to eye-off
            binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.baseline_visibility_off_24,
                0
            )
            binding.etPassword.typeface = ResourcesCompat.getFont(this, R.font.lilita_one)
        } else {
            binding.etPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.baseline_remove_red_eye_24,
                0
            )
            binding.etPassword.typeface = ResourcesCompat.getFont(this, R.font.lilita_one)
        }
    }
}