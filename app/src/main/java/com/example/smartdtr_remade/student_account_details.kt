package com.example.smartdtr_remade

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.smartdtr_remade.Api.RetrofitInstance
import com.example.smartdtr_remade.models.Student
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class student_account_details : Fragment() {

    private lateinit var preferencesManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_account_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferencesManager = PreferencesManager(requireContext())
        val userId = preferencesManager.getUserId() // Retrieve the user ID

        if (userId != null) {
            fetchStudentAccountDetails(userId, view)
        } else {
            Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show()
            // Optionally, redirect to the login screen or another appropriate action
        }
    }

    private fun fetchStudentAccountDetails(userId: String, view: View) {
        val call = RetrofitInstance.studentApi.getStudentAccountDetails(userId)

        call.enqueue(object : Callback<Student> {
            override fun onResponse(call: Call<Student>, response: Response<Student>) {
                if (response.isSuccessful && response.body() != null) {
                    val accountDetails = response.body()

                    // Update the UI using the view from onViewCreated
                    view.findViewById<TextView>(R.id.name_value)?.text = "${accountDetails?.firstname} ${accountDetails?.lastname}"
                    view.findViewById<TextView>(R.id.student_id_value)?.text = accountDetails?.student_id
                    view.findViewById<TextView>(R.id.email_value)?.text = accountDetails?.email
                    view.findViewById<TextView>(R.id.mobile_number_value)?.text = accountDetails?.mobile_number
                    view.findViewById<TextView>(R.id.Year_level_value)?.text = accountDetails?.year_level
                    view.findViewById<TextView>(R.id.sex_value)?.text = accountDetails?.sex
                } else {
                    Toast.makeText(requireContext(), "Failed to load student details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Student>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
