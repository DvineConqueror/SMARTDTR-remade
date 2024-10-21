package com.example.smartdtr_remade.activityTeachers

import LoginResponse
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartdtr_remade.Api.ApiService
import com.example.smartdtr_remade.R
import com.example.smartdtr_remade.activityStudents.Main_student
import com.example.smartdtr_remade.activityTeachers.Main_teacher
import com.example.smartdtr_remade.models.SignUpRequest
import com.example.smartdtr_remade.models.SignupResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class activity_create_account : AppCompatActivity() {

    private lateinit var etFirstname: EditText
    private lateinit var etLastname: EditText
    private lateinit var etEmail: EditText
    private lateinit var etFirstPassword: EditText
    private lateinit var etPasswordConfirm: EditText
    private lateinit var etID: EditText
    private lateinit var etMobileNumber: EditText
    private lateinit var spinnerSex: Spinner
    private lateinit var spnYearLevel: Spinner
    private lateinit var btnNext: Button
    private lateinit var btnBack: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account) // Use your actual layout file name

        // Initialize UI elements
        etFirstname = findViewById(R.id.etFirstname)
        etLastname = findViewById(R.id.etLastname)
        etEmail = findViewById(R.id.etEmail)
        etFirstPassword = findViewById(R.id.etFirstPassword)
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm)
        etID = findViewById(R.id.etID)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        spinnerSex = findViewById(R.id.spinnerSex)
        spnYearLevel = findViewById(R.id.spnYearLevel)
        btnNext = findViewById(R.id.btnNext)
        btnBack = findViewById(R.id.btnBack)

        //Set spinner font to app font
        val adapter1 = ArrayAdapter.createFromResource(
            this,
            R.array.year_level,
            R.layout.spinner_text_edit
        )

        val adapter2 = ArrayAdapter.createFromResource(
            this,
            R.array.sex_options,
            R.layout.spinner_text_edit
        )

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSex.adapter = adapter1

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnYearLevel.adapter = adapter2

        etFocusListeners()

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        // Set onClick listener for the signup button
        btnNext.setOnClickListener {
            signUp()
        }

        btnBack.setOnClickListener {
            startActivity(
                Intent(
                    this@activity_create_account,
                    activity_login::class.java
                )
            )
        }
    }

    private fun etFocusListeners() {
        etFirstname.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                etFirstname.error = validateField(etFirstname.text.toString().trim(), "First name")
            }
        }

        etLastname.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                etLastname.error = validateField(etLastname.text.toString().trim(), "Last name")
            }
        }

        etEmail.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                etEmail.error = validateEmail()
            }
        }

        etFirstPassword.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                val errorMsg = validatePassword()
                if (errorMsg != null) {
                    etFirstPassword.error = null // Hide error icon
                    etFirstPassword.setError(errorMsg, null) // Show only error text
                }
            }
        }

        etPasswordConfirm.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                val errorMsg = validateConfirmPassword()
                if (errorMsg != null) {
                    etPasswordConfirm.error = null // Hide error icon
                    etPasswordConfirm.setError(errorMsg, null) // Show only error text
                }
            }
        }

        etID.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                etID.error = validID()
            }
        }

        etMobileNumber.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                etMobileNumber.error = validateField(etMobileNumber.text.toString().trim(), "Mobile number")
            }
        }

    }

    private fun validateField(value: String, fieldName: String): String? {
        return if (value.isEmpty()) {
            "$fieldName is required."
        } else {
            null
        }
    }

    private fun validateEmail(): String? {
        val email = etEmail.text.toString().trim()
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
        return if (email.isEmpty()) {
            "Email is required."
        } else if (!email.matches(emailPattern)) {
            "Please enter a valid email address."
        } else {
            null
        }
    }

    private fun validatePassword(): String? {
        val password = etFirstPassword.text.toString().trim()
        return when {
            password.length < 8 -> "Password must be at least 8 characters."
            !password.matches(Regex(".*[A-Z].*")) -> "Password must contain at least one uppercase letter."
            !password.matches(Regex(".*\\d.*")) -> "Password must contain at least one number."
            else -> null
        }
    }

    private fun validateConfirmPassword(): String? {
        val password = etFirstPassword.text.toString().trim()
        val confirmPassword = etPasswordConfirm.text.toString().trim()
        return if (confirmPassword != password) {
            "Passwords do not match."
        } else {
            null
        }
    }

    private fun validID(): String? {
        val id = etID.text.toString().trim()
        val teacherPattern = "^T-\\d{5}$".toRegex(RegexOption.IGNORE_CASE)
        val studentPattern = "^S-\\d{5}$".toRegex(RegexOption.IGNORE_CASE)

        return if (!teacherPattern.matches(id) && !studentPattern.matches(id)) {
            "Please enter a valid ID number."
        } else {
            null
        }
    }


    private fun validateForm(): Boolean {
        val fields = listOf(
            etFirstname, etLastname, etEmail, etFirstPassword, etPasswordConfirm,
            etID, etMobileNumber
        )
        return fields.all { it.error == null }
    }



    private fun signUp() {
        val firstname = etFirstname.text.toString().trim()
        val lastname = etLastname.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etFirstPassword.text.toString().trim()
        val passwordConfirm = etPasswordConfirm.text.toString().trim()
        val mobileNumber = etMobileNumber.text.toString().trim()
        val id = etID.text.toString().trim()
        val sex = spinnerSex.selectedItem.toString()
        val yearLevel = if (id.startsWith("S")) {
            spnYearLevel.selectedItem.toString() // Get the selected year level if ID starts with "S"
        } else {
            null // Handle accordingly if ID doesn't start with "S"
        }

        // Validate fields
        if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() ||
            password.isEmpty() || mobileNumber.isEmpty() || id.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != passwordConfirm) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Create SignUpRequest object
        val signUpRequest = SignUpRequest(
            firstname = firstname,
            lastname = lastname,
            email = email,
            password = password,
            mobile_number = mobileNumber,
            ID = id,
            sex = sex,
            year_level = yearLevel
        )

        // Make API call
        val apiService = ApiService.create()
        apiService.signup(signUpRequest).enqueue(object : Callback<SignupResponse> {
            override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
                if (response.isSuccessful) {
                    Log.d("SignupResponse", "Successful: ${response.body()}")
                    response.body()?.let {
                        // Save token and userId in SharedPreferences
                        with(sharedPreferences.edit()) {
                            putString("token", it.token)
                            putString("userId", it.userId)// Ensure userId is saved as Int
                            apply()
                        }

                        // Show a success message
                        Toast.makeText(this@activity_create_account, "Account created successfully!", Toast.LENGTH_SHORT).show()

                        // Redirect to the login activity
                        startActivity(Intent(this@activity_create_account, activity_login::class.java))
                        finish() // Close the current activity
                    }
                } else {
                    Log.e("SignupResponse", "Failed: ${response.message()}")
                    Toast.makeText(this@activity_create_account, "Signup failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                Toast.makeText(
                    this@activity_create_account,
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
