package com.example.smartdtr_remade.activityTeachers

import LoginRequest
import LoginResponse
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartdtr_remade.Api.ApiService
import com.example.smartdtr_remade.PreferencesManager
import com.example.smartdtr_remade.R
import com.example.smartdtr_remade.activityStudents.Main_student
import com.example.smartdtr_remade.databinding.ActivityLoginBinding
import com.example.smartdtr_remade.activityTeachers.Main_teacher
import com.example.smartdtr_remade.forgot_password
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class activity_login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var preferencesManager: PreferencesManager
    private var isPasswordVisible: Boolean = false

    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize PreferencesManager
        preferencesManager = PreferencesManager(this)

        // Initialize your views
        val etIDNumber = binding.etIDNumber
        val etPassword = binding.etPassword
        val btLoginButton = binding.btLogin // Use binding to access login button
        // val tvForgotPassButton = binding.tvClickableForgotPass // Use binding for the TextView (SignUp)
        val tvSignUpButton = binding.tvClickableSignUp // Use binding for the TextView (SignUp)

        // Set the icon for the right side of edit text
        etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_remove_red_eye_24, 0)

        // Toggle password visibility when clicking on the eye icon
        etPassword.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2 // This is the right drawable
                val drawableRight = etPassword.compoundDrawables[drawableEnd]
                if (drawableRight != null && event.rawX >= (etPassword.right - drawableRight.bounds.width())) {
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
            val teacherPattern = "^T-\\d{5}$".toRegex()
            val studentPattern = "^S-\\d{5}$".toRegex()

            if (!teacherPattern.matches(etID) && !studentPattern.matches(etID)) {
                Toast.makeText(this, "Please enter a valid ID Number!", Toast.LENGTH_SHORT).show()
            } else {
                // Call the login function
                loginUser(etID, etPasswordText)
            }
        }

        // Handle Forgot Pass button click
        //tvForgotPassButton.setOnClickListener {
            //startActivity(Intent(this@activity_login, forgot_password::class.java))
        //}

        // Handle Signup button click
        tvSignUpButton.setOnClickListener {
            startActivity(Intent(this@activity_login, activity_create_account::class.java))
        }

        // Apply window insets for padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Function to call the login API
    private fun loginUser(id: String, password: String) {
        val loginRequest = LoginRequest(id, password)
        val apiService = ApiService.create() // Assuming you have set up Retrofit

        apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    val user = response.body()?.user // Get the user object

                    if (token != null && user != null) {
                        preferencesManager.saveToken(token)

                        // Save the appropriate ID based on user type
                        if (user.teacher_id != null) {
                            preferencesManager.saveTeacherId(user.teacher_id) // Store the teacher ID
                            startActivity(Intent(this@activity_login, Main_teacher::class.java))
                        } else if (user.student_id != null) {
                            preferencesManager.saveStudentId(user.student_id) // Store the student ID
                            startActivity(Intent(this@activity_login, Main_student::class.java))
                        }
                        finish()
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@activity_login, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
