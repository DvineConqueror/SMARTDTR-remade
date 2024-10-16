package com.example.smartdtr_remade.activityTeachers

import LoginResponse
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
    private lateinit var etDateOfBirth: EditText // New field for date of birth
    private lateinit var spinnerSex: Spinner
    private lateinit var spnYearLevel: Spinner
    private lateinit var btnNext: Button
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
        etMobileNumber = findViewById(R.id.etMobileNumber)// Initialize date of birth field
        spinnerSex = findViewById(R.id.spinnerSex)
        spnYearLevel = findViewById(R.id.spnYearLevel)
        etDateOfBirth = findViewById(R.id.etDateOfBirth)
        btnNext = findViewById(R.id.btnNext)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        // Set onClick listener for the signup button
        btnNext.setOnClickListener {
            signUp()
        }
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
        val dateOfBirth = etDateOfBirth.text.toString().trim() // Get the date of birth
        val yearLevel = if (id.startsWith("S")) {
            spnYearLevel.selectedItem.toString() // Get the selected year level if ID starts with "S"
        } else {
            null // Handle accordingly if ID doesn't start with "S"
        }

        // Validate fields
        if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() ||
            password.isEmpty() || mobileNumber.isEmpty() || id.isEmpty() || dateOfBirth.isEmpty()) {
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
            date_of_birth = dateOfBirth, // Include date of birth
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
