package com.example.smartdtr_remade

import TeacherFinishedDutyAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smartdtr_remade.Api.RetrofitInstance
import com.example.smartdtr_remade.models.Duty
import com.example.smartdtr_remade.PreferencesManager // Make sure to import PreferencesManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class teacher_history : Fragment() {

    private lateinit var teacherFinishedDutyAdapter: TeacherFinishedDutyAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var preferencesManager: PreferencesManager // Declare PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_teacher_history, container, false)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_dutySchedule)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set up adapter
        teacherFinishedDutyAdapter = TeacherFinishedDutyAdapter(mutableListOf())
        recyclerView.adapter = teacherFinishedDutyAdapter

        // Initialize PreferencesManager
        preferencesManager = PreferencesManager(requireContext())

        // Fetch the finished duties
        fetchFinishedDuties()

        return view
    }

    private fun fetchFinishedDuties() {
        // Get the logged-in teacher's ID
        val loggedInTeacherId = preferencesManager.getUserId() // Adjust method if needed to get teacher ID

        Log.d("TeacherHistory", "Logged-in Teacher ID: $loggedInTeacherId")

        if (loggedInTeacherId != null) {
            // Call the API to get completed duties for the logged-in teacher
            RetrofitInstance.dutyApi.getCompletedDutiesTeacher(loggedInTeacherId).enqueue(object : Callback<List<Duty>> {
                override fun onResponse(call: Call<List<Duty>>, response: Response<List<Duty>>) {
                    if (response.isSuccessful) {
                        val duties = response.body()
                        duties?.let {
                            Log.d("DutyResponse", "Number of duties: ${it.size}") // Log the size of the duties list
                            teacherFinishedDutyAdapter.updateDuties(it)
                        } ?: Log.e("DutyResponse", "Response body is null")
                    } else {
                        Log.e("DutyResponse", "Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<Duty>>, t: Throwable) {
                    Log.e("DutyResponse", "Failure: ${t.message}")
                }
            })
        } else {
            Log.e("TeacherHistory", "Teacher ID is null or invalid")
        }
    }
}
