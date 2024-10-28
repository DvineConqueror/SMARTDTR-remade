package com.example.smartdtr_remade.activityTeachers

import LoginResponse
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.example.smartdtr_remade.Api.ApiService
import com.example.smartdtr_remade.R
import com.example.smartdtr_remade.activityStudents.Main_student
import com.example.smartdtr_remade.activityTeachers.Main_teacher
import com.example.smartdtr_remade.models.SignUpRequest
import com.example.smartdtr_remade.models.SignupResponse
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
    private lateinit var etIDTextField: TextInputLayout
    private lateinit var etPasswordTextField: TextInputLayout
    private lateinit var etPasswordConfirmTextField: TextInputLayout
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
        etIDTextField = findViewById(R.id.etTextFieldStudentID)
        etPasswordTextField = findViewById(R.id.etTextFieldPassword)
        etPasswordConfirmTextField = findViewById(R.id.etTextFieldPasswordConfirm)

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
        spinnerSex.adapter = adapter2

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnYearLevel.adapter = adapter1

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

        //Input filter to block symbols etc.
        val blockedChars = " .,/;'\":{}[]|\\_=+!@#$%^&*()?<> "
        val symbolTextFilter = InputFilter { source, _, _, _, _, _  ->
            if (source.any { it in blockedChars }){
                return@InputFilter ""
            }
            null
        }
        //Because space filter overrides the other filters, we need to set the max lengths of our text fields here
        val maxLengthUserId = InputFilter.LengthFilter(7)
        val maxLengthPassword = InputFilter.LengthFilter(16)

        etIDTextField.editText?.filters = arrayOf(symbolTextFilter, maxLengthUserId)
        etPasswordTextField.editText?.filters = arrayOf(symbolTextFilter, maxLengthPassword)
        etPasswordConfirmTextField.editText?.filters = arrayOf(symbolTextFilter, maxLengthPassword)

        etMobileNumber.apply {
            setText("63") // Set initial text as "63"
            setSelection(2) // Set the cursor to start after "63"

            // Add TextWatcher to prevent user from modifying the first two characters
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    // Ensure "63" is always at the start and can't be modified
                    if (!s.toString().startsWith("63")) {
                        etMobileNumber.setText("63")
                        etMobileNumber.setSelection(2) // Set cursor after "63"
                    }
                }
            })
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
                etMobileNumber.error = validateMobileNum()
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

        return when {
            email.isEmpty() -> "Email is required."
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Please enter a valid email address."
            else -> null
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
            "ID must be T-***** or S-*****"
        } else {
            null
        }
    }

    private fun validateMobileNum(): String? {
        val mobileNumber = etMobileNumber.text.toString().trim()
        return if (mobileNumber.length < 12) {
            "Mobile number must be at least 12 digits."
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
        val sex = spinnerSex.selectedItem.toString().trim()
        val yearLevel = if (id.startsWith("S")) {
            spnYearLevel.selectedItem.toString().trim() // Get the selected year level if ID starts with "S"
        } else {
            null // Handle accordingly if ID doesn't start with "S"
        }

        // Validate fields
        if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() ||
            password.isEmpty() || mobileNumber.isEmpty() || id.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != passwordConfirm) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
            return
        }

        if (mobileNumber.length < 12){
            Toast.makeText(this, "Mobile numbers must be at least 12 digits.", Toast.LENGTH_SHORT).show()
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
