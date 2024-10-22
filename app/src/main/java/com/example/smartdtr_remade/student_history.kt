package com.example.smartdtr_remade

import StudentFinishedDutyAdapter
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

class student_history : Fragment() {

    private lateinit var studentFinishedDutyAdapter: StudentFinishedDutyAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var preferencesManager: PreferencesManager // Declare PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_student_history, container, false)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_dutySchedule)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set up adapter
        studentFinishedDutyAdapter = StudentFinishedDutyAdapter(mutableListOf())
        recyclerView.adapter = studentFinishedDutyAdapter

        // Initialize PreferencesManager
        preferencesManager = PreferencesManager(requireContext())

        // Fetch the finished duties
        fetchFinishedDuties()

        return view
    }

    private fun fetchFinishedDuties() {
        val loggedInStudentId = preferencesManager.getUserId()

        if (loggedInStudentId != null) {
            RetrofitInstance.dutyApi.getCompletedDutiesStudent(loggedInStudentId).enqueue(object : Callback<List<Duty>> {
                override fun onResponse(call: Call<List<Duty>>, response: Response<List<Duty>>) {
                    if (response.isSuccessful) {
                        val duties = response.body()
                        duties?.let {
                            studentFinishedDutyAdapter.updateDuties(it)

                            // Calculate total hours worked
                            val totalHoursWorked = studentFinishedDutyAdapter.calculateTotalHoursWorked()
                            val remainingHours = 90 - totalHoursWorked

                            // Update the timer on the home page
                            updateTimerOnHomePage(remainingHours)
                        }
                    }
                }

                override fun onFailure(call: Call<List<Duty>>, t: Throwable) {
                    Log.e("DutyResponse", "Failure: ${t.message}")
                }
            })
        }
    }

    private fun updateTimerOnHomePage(remainingHours: Int) {
        // Assuming you have a reference to the home fragment or activity
        val homeFragment = parentFragmentManager.findFragmentByTag("homeFragment") as? student_home_page
        homeFragment?.updateTimerTextView(remainingHours)
    }
}
