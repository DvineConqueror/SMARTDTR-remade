package com.example.smartdtr_remade.activityTeachers

import LoginResponse
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.smartdtr_remade.Api.ApiService
import com.example.smartdtr_remade.Api.RetrofitInstance
import com.example.smartdtr_remade.R
import com.example.smartdtr_remade.models.SignUpRequest
import com.google.android.material.textfield.TextInputLayout
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
    private lateinit var btnNext: Button
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        // Initialize UI components
        etFirstname = findViewById(R.id.etFirstname)
        etLastname = findViewById(R.id.etLastname)
        etEmail = findViewById(R.id.etEmail)
        etFirstPassword = findViewById(R.id.etFirstPassword)
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm)
        etID = findViewById(R.id.etID)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        spinnerSex = findViewById(R.id.spinnerSex)
        btnNext = findViewById(R.id.btnNext)
        btnBack = findViewById(R.id.btnBack)

        // Set onClickListener for the Sign Up button
        btnNext.setOnClickListener {
            performSignUp()
        }

        btnBack.setOnClickListener{
            startActivity(
                Intent(
                    this@activity_create_account,
                    activity_login::class.java
                )
            )
        }
    }



    private fun performSignUp() {
        val firstname = etFirstname.text.toString().trim()
        val lastname = etLastname.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etFirstPassword.text.toString().trim()
        val passwordConfirm = etPasswordConfirm.text.toString().trim()
        val ID = etID.text.toString().trim()
        val mobileNumber = etMobileNumber.text.toString().trim()
        val sex = spinnerSex.selectedItem.toString()

        // Validate inputs
        if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() ||
            password.isEmpty() || passwordConfirm.isEmpty() || ID.isEmpty() ||
            mobileNumber.isEmpty() || sex.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if passwords match
        if (password != passwordConfirm) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Create SignUpRequest object
        val signUpRequest = SignUpRequest(firstname, lastname, email, password, mobileNumber, ID, sex)
        val apiService = ApiService.create()

        // Make API call for signup
        apiService.signup(signUpRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@activity_create_account, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                    // Redirect to another activity or clear input fields
                } else {
                    Toast.makeText(this@activity_create_account, "Sign Up Failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@activity_create_account, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("SignUpError", "Error: ${t.message}", t)
            }
        })
    }
}
