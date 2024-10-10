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
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.app.Activity
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartdtr_remade.R
import com.example.smartdtr_remade.activityTeachers.Main_teacher
import com.example.smartdtr_remade.activityTeachers.teacher_create_account
import com.example.smartdtr_remade.choose_user_role
import com.example.smartdtr_remade.databinding.ActivityTeacherLoginBinding
import org.w3c.dom.Text

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

        // Set the icon for the right side of edit text
        etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_remove_red_eye_24, 0) // Set the initial drawable (eye)
        binding.etPassword.setOnTouchListener { v, event ->
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

        btLogin.setOnClickListener {
            val etEmail = etEmailAddress.text.toString()
            val etPasswordText = etPassword.text.toString()

            if (!etEmail.contains("@")) {
                Toast.makeText(this, "Please enter a valid email address!", Toast.LENGTH_SHORT).show()
            } else {
                if (etEmail == "testteacher@gmail.com") {
                    if (etPasswordText == "adminteacher") {
                        Toast.makeText(this, "Login Successful!", Toast.LENGTH_LONG).show()
                        // Optionally navigate to another activity
                        Handler().postDelayed({
                            startActivity(
                                Intent(this@teacher_login, Main_teacher::class.java)
                            )
                        }, 1000)
                    } else {
                        Toast.makeText(this, "Password incorrect!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show()
                }
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
            startActivity(
                Intent(
                    this@teacher_login,
                    choose_user_role::class.java
                )
            )
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
            binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            // Change the drawable to eye-off
            binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0)
            binding.etPassword.typeface = ResourcesCompat.getFont(this, R.font.lilita_one)
        } else {
            binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_remove_red_eye_24, 0)
            binding.etPassword.typeface = ResourcesCompat.getFont(this, R.font.lilita_one)
        }

        // Set the font for the EditText
        binding.etPassword.typeface = ResourcesCompat.getFont(this, R.font.lilita_one)
        // Move cursor to the end
        binding.etPassword.setSelection(binding.etPassword.text.length)
    }


}