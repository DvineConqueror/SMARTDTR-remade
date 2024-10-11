package com.example.smartdtr_remade.activityTeachers

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartdtr_remade.R
import com.example.smartdtr_remade.activityStudents.Main_student
import com.example.smartdtr_remade.databinding.ActivityLoginBinding

class activity_login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var isPasswordVisible: Boolean = false

    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root) // Set content view to the binding root

        // Initialize your views
        val etIDNumber = binding.etIDNumber
        val etPassword = binding.etPassword
        val btLoginButton = binding.btLogin // Use binding to access login button
        val textViewButton = binding.tvClickableSignUp // Use binding for the TextView (SignUp)

        // Set the icon for the right side of edit text
        etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_remove_red_eye_24, 0) // Set the initial drawable (eye)

        // Toggle password visibility when clicking on the eye icon
        etPassword.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2 // This is the right drawable
                val drawableRight = etPassword.compoundDrawables[drawableEnd]
                if (drawableRight != null && event.rawX >= (etPassword.right - drawableRight.bounds.width())) {
                    // Toggle password visibility
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }

        // Handle login button click
        btLoginButton.setOnClickListener {
            val etID = etIDNumber.text.toString()
            val etPasswordText = etPassword.text.toString()
            // Patterns for the student and teacher ID's
            val teacherPattern = "^UP-\\d{5}$".toRegex()
            val studentPattern = "^03-\\d{5}$".toRegex()

            if (!teacherPattern.matches(etID) && !studentPattern.matches(etID)) {
                Toast.makeText(this, "Please enter a valid ID Number!", Toast.LENGTH_SHORT).show()
            } else {
                if (teacherPattern.matches(etID)) {
                    if (etID == "UP-01111" && etPasswordText == "adminteacher"){
                        Toast.makeText(this, "Login Successful!", Toast.LENGTH_LONG).show()
                        // Navigate to Main_teacher activity after delay
                        Handler().postDelayed({
                            val intent = Intent(this@activity_login, Main_teacher::class.java)
                            val options = ActivityOptions.makeSceneTransitionAnimation(this) // Create options for transition
                            startActivity(intent, options.toBundle()) // Start activity with options
                        }, 1000)
                    } else {
                        Toast.makeText(this, "Password incorrect!", Toast.LENGTH_SHORT).show()
                    }
                } else if (studentPattern.matches(etID)) {
                    if (etID == "03-01111" && etPasswordText == "adminstudent"){
                        Toast.makeText(this, "Login Successful!", Toast.LENGTH_LONG).show()
                        // Navigate to Main_teacher activity after delay
                        Handler().postDelayed({
                            val intent = Intent(this@activity_login, Main_student::class.java)
                            val options = ActivityOptions.makeSceneTransitionAnimation(this) // Create options for transition
                            startActivity(intent, options.toBundle()) // Start activity with options
                        }, 1000)
                    } else {
                        Toast.makeText(this, "Password incorrect!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Handle Signup button click
        textViewButton.setOnClickListener {
            startActivity(
                Intent(this@activity_login, activity_create_account::class.java)
            )
        }

        // Apply window insets for padding
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
            binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            // Change the drawable to eye-off
            binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0)
        } else {
            binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_remove_red_eye_24, 0)
        }

        // Set the font for the EditText
        binding.etPassword.typeface = ResourcesCompat.getFont(this, R.font.lilita_one)
        // Move cursor to the end
        binding.etPassword.setSelection(binding.etPassword.text.length)
    }
}
