package com.example.smartdtr_remade.activityTeachers

import LoginRequest
import LoginResponse
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartdtr_remade.Api.ApiService
import com.example.smartdtr_remade.PreferencesManager
import com.example.smartdtr_remade.R
import com.example.smartdtr_remade.activityStudents.Main_student
import com.example.smartdtr_remade.databinding.ActivityLoginBinding
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
        etFocusListeners()

        preferencesManager = PreferencesManager(this)

        val etIDNumber = binding.etTextFieldUserID
        val etPassword = binding.etTextFieldPass
        val btLoginButton = binding.btLogin
        val tvSignUpButton = binding.tvClickableSignUp
        val tvForgotPass = binding.tvClickableForgotPassword

        btLoginButton.setOnClickListener {
            val etID = binding.etTextFieldUserID.editText?.text.toString().trim()
            val etPasswordText = binding.etTextFieldPass.editText?.text.toString().trim()

            val teacherPattern = "^T-\\d{5}$".toRegex(RegexOption.IGNORE_CASE)
            val studentPattern = "^S-\\d{5}$".toRegex(RegexOption.IGNORE_CASE)

            when {
                !teacherPattern.matches(etID) && !studentPattern.matches(etID) -> {
                    Toast.makeText(this, "Please enter a valid ID Number!", Toast.LENGTH_SHORT).show()
                }
                etPasswordText.length < 8 -> {
                    Toast.makeText(this, "Password must be at least 8 characters long!", Toast.LENGTH_SHORT).show()
                }
                !etPasswordText.matches(Regex(".*[A-Z].*")) -> {
                    Toast.makeText(this, "Password must contain at least one uppercase letter!", Toast.LENGTH_SHORT).show()
                }
                !etPasswordText.matches(Regex(".*\\d.*")) -> {
                    Toast.makeText(this, "Password must contain at least one number!", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    loginUser(etID, etPasswordText)
                }
            }
        }

        tvForgotPass.setOnClickListener{
            startActivity(
                Intent(
                    this@activity_login, forgot_password::class.java
                )
            )
        }

        tvSignUpButton.setOnClickListener {
            startActivity(Intent(this@activity_login, activity_create_account::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun etFocusListeners() {
        binding.etTextFieldUserID.editText?.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                val validationMessage = validateID()
                Log.d("FocusListener", "Validation triggered. Message: $validationMessage")
                binding.etTextFieldUserID.helperText = validationMessage
            }
        }

        binding.etTextFieldPass.editText?.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                val validationMessage = validatePassword()
                binding.etTextFieldPass.helperText = validationMessage
            }
        }

    }

    private fun validateID(): String? {
        val userIDNumber = binding.etTextFieldUserID.editText?.text.toString().trim()
        val teacherPattern = "^T-\\d{5}$".toRegex(RegexOption.IGNORE_CASE)
        val studentPattern = "^S-\\d{5}$".toRegex(RegexOption.IGNORE_CASE)
        return when {
            userIDNumber.isEmpty() -> {
                "Please enter a valid ID Number!"
            }
            !userIDNumber.matches(teacherPattern) && !userIDNumber.matches(studentPattern) -> {
                "ID Number must be in the format T-***** or S-*****"
            }
            else -> {
                null
            }
        }
    }

    private fun validatePassword(): String? {
        val password = binding.etTextFieldPass.editText?.text.toString().trim()
        return when {
            password.length < 8 -> "Password must be at least 8 characters."
            !password.matches(Regex(".*[A-Z].*")) -> "Password must contain at least one uppercase letter."
            !password.matches(Regex(".*\\d.*")) -> "Password must contain at least one number."
            else -> null
        }
    }

    private fun loginUser(id: String, password: String) {
        val loginRequest = LoginRequest(id, password)
        val apiService = ApiService.create()

        apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    val userId = response.body()?.userId
                    val user = response.body()?.user

                    if (token != null && user != null) {
                        preferencesManager.saveToken(token)
                        preferencesManager.saveUserId(userId ?: "")

                        // Save the appropriate ID based on user type
                        if (user.teacher_id != null) {
                            preferencesManager.saveTeacherId(user.teacher_id)
                            startActivity(Intent(this@activity_login, Main_teacher::class.java))
                            Toast.makeText(this@activity_login, "Login Successful!", Toast.LENGTH_SHORT).show()
                        } else if (user.student_id != null) {
                            preferencesManager.saveStudentId(user.student_id)
                            startActivity(Intent(this@activity_login, Main_student::class.java))
                            Toast.makeText(this@activity_login, "Login Successful!", Toast.LENGTH_SHORT).show()
                        }
                        finish()
                    }
                } else {
                    Toast.makeText(this@activity_login, "Login failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@activity_login, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
