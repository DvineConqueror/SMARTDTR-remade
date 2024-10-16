package com.example.smartdtr_remade

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.smartdtr_remade.Api.RetrofitInstance
import com.example.smartdtr_remade.activityTeachers.Main_teacher
import com.example.smartdtr_remade.models.Teacher
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class teacher_account_details : Fragment() {

    private lateinit var preferencesManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_account_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferencesManager = PreferencesManager(requireContext())
        val userId = preferencesManager.getUserId() // Retrieve the user ID

        if (userId != null) {
            fetchTeacherAccountDetails(userId, view)
        } else {
            Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show()
            redirectToLogin()
        }
    }

    private fun fetchTeacherAccountDetails(userId: String, view: View) {
        val call = RetrofitInstance.teacherApi.getTeacherAccountDetails(userId)

        call.enqueue(object : Callback<Teacher> {
            override fun onResponse(call: Call<Teacher>, response: Response<Teacher>) {
                if (response.isSuccessful && response.body() != null) {
                    val accountDetails = response.body()

                    // Update the UI using the view from onViewCreated
                    view.findViewById<TextView>(R.id.name_value)?.text = "${accountDetails?.firstname} ${accountDetails?.lastname}"
                    view.findViewById<TextView>(R.id.teacher_id_value)?.text = accountDetails?.teacher_id
                    view.findViewById<TextView>(R.id.email_value)?.text = accountDetails?.email
                    view.findViewById<TextView>(R.id.mobile_number_value)?.text = accountDetails?.mobile_number
                    view.findViewById<TextView>(R.id.date_of_birth_value)?.text = accountDetails?.date_of_birth
                    view.findViewById<TextView>(R.id.sex_value)?.text = accountDetails?.sex
                } else {
                    Toast.makeText(requireContext(), "Failed to load teacher details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Teacher>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun redirectToLogin() {
        val intent = Intent(requireContext(), Main_teacher::class.java)
        startActivity(intent)
        activity?.finish()
    }
}
