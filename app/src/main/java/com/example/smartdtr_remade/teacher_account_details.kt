package com.example.smartdtr_remade

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.smartdtr_remade.Api.RetrofitInstance
import com.example.smartdtr_remade.R
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
        val teacherId = preferencesManager.getTeacherId()  // Retrieve the teacher ID

        if (teacherId != null) {
            fetchTeacherAccountDetails(teacherId, view)
        } else {
            // Handle the case when the teacher ID is not found
            // You can show an error message or redirect to the login screen
        }
    }

    private fun fetchTeacherAccountDetails(teacherId: String, view: View) {
        val call = RetrofitInstance.teacherApi.getTeacherAccountDetails(teacherId)

        call.enqueue(object : Callback<Teacher> {
            override fun onResponse(call: Call<Teacher>, response: Response<Teacher>) {
                if (response.isSuccessful) {
                    val accountDetails = response.body()

                    // Update the UI using the view from onViewCreated
                    view.findViewById<TextView>(R.id.name_value)?.text = "${accountDetails?.firstname} ${accountDetails?.lastname}"
                    view.findViewById<TextView>(R.id.teacher_id_value).text = accountDetails?.teacher_id
                    view.findViewById<TextView>(R.id.email_value).text = accountDetails?.email
                    view.findViewById<TextView>(R.id.mobile_number_value).text = accountDetails?.mobile_number
                    view.findViewById<TextView>(R.id.date_of_birth_value).text = accountDetails?.date_of_birth
                    view.findViewById<TextView>(R.id.sex_value).text = accountDetails?.sex
                } else {
                    // Handle the case when the response is not successful
                }
            }

            override fun onFailure(call: Call<Teacher>, t: Throwable) {
                // Handle failure
            }
        })
    }
}
