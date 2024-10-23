package com.example.smartdtr_remade

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartdtr_remade.Api.RetrofitInstance
import com.example.smartdtr_remade.adapter.HomeStudentFinishedDutyAdapter
import com.example.smartdtr_remade.adapter.HomeStudentUpcomingDutyAdapter
import com.example.smartdtr_remade.models.Duty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class student_home_page : Fragment() {
    private lateinit var tvTimer: TextView
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var upcomingDutyRecyclerView: RecyclerView
    private lateinit var finishedDutyRecyclerView: RecyclerView
    private lateinit var studentUpcomingDutyAdapter: HomeStudentUpcomingDutyAdapter
    private lateinit var studentFinishedDutyAdapter: HomeStudentFinishedDutyAdapter // Declare finished duty adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesManager = PreferencesManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_home_page, container, false)
        tvTimer = view.findViewById(R.id.timer)
        upcomingDutyRecyclerView = view.findViewById(R.id.recyclerView_duties)
        finishedDutyRecyclerView = view.findViewById(R.id.recyclerView_finished_duties)

        // Initialize RecyclerViews with adapters
        upcomingDutyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        studentUpcomingDutyAdapter = HomeStudentUpcomingDutyAdapter(emptyList())
        upcomingDutyRecyclerView.adapter = studentUpcomingDutyAdapter

        finishedDutyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        studentFinishedDutyAdapter = HomeStudentFinishedDutyAdapter(emptyList()) // Initialize finished duty adapter
        finishedDutyRecyclerView.adapter = studentFinishedDutyAdapter

        // Fetch finished and upcoming duties when the view is created
        fetchFinishedDuties()
        fetchUpcomingDuties()

        return view
    }

    private fun fetchFinishedDuties() {
        val loggedInStudentId = preferencesManager.getUserId()

        if (loggedInStudentId != null) {
            RetrofitInstance.dutyApi.getCompletedDutiesStudent(loggedInStudentId).enqueue(object : Callback<List<Duty>> {
                @RequiresApi(Build.VERSION_CODES.S)
                override fun onResponse(call: Call<List<Duty>>, response: Response<List<Duty>>) {
                    if (response.isSuccessful) {
                        val duties = response.body()
                        if (!duties.isNullOrEmpty()) {
                            // Update the finished duties RecyclerView
                            studentFinishedDutyAdapter.updateDuties(duties)

                            // Calculate total hours worked
                            val totalHoursWorked = calculateTotalHoursWorked(duties)
                            val remainingHours = 90 - totalHoursWorked
                            updateTimerTextView(remainingHours)
                        } else {
                            // Handle case where no duties are found
                            updateTimerTextView(90) // Default to 90 hours if no duties are found
                        }
                    } else {
                        Log.e("DutyResponse", "Response was unsuccessful: ${response.message()}")
                        updateTimerTextView(90) // Default to 90 hours if API call fails
                    }
                }

                override fun onFailure(call: Call<List<Duty>>, t: Throwable) {
                    Log.e("DutyResponse", "Failure: ${t.message}")
                    updateTimerTextView(90) // Default to 90 hours if API call fails
                }
            })
        }
    }

    private fun fetchUpcomingDuties() {
        val loggedInStudentId = preferencesManager.getUserId()

        if (loggedInStudentId != null) {
            RetrofitInstance.dutyApi.getUpcomingDutiesStudent(loggedInStudentId).enqueue(object : Callback<List<Duty>> {
                override fun onResponse(call: Call<List<Duty>>, response: Response<List<Duty>>) {
                    if (response.isSuccessful) {
                        val duties = response.body()
                        if (!duties.isNullOrEmpty()) {
                            // Update the upcoming duties RecyclerView
                            studentUpcomingDutyAdapter.updateDuties(duties)
                        }
                    } else {
                        Log.e("DutyResponse", "Response was unsuccessful: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<List<Duty>>, t: Throwable) {
                    Log.e("DutyResponse", "Failure: ${t.message}")
                }
            })
        }
    }

    fun updateTimerTextView(remainingHours: Int) {
        val timerText = "$remainingHours Hours Left"
        tvTimer.text = timerText
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun calculateTotalHoursWorked(duties: List<Duty>): Int {
        var totalHours = 0
        for (duty in duties) {
            totalHours += calculateHoursFromDuty(duty)
        }
        return totalHours
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun calculateHoursFromDuty(duty: Duty): Int {
        // Assuming start_time and end_time are in "HH:mm" format
        val startTime = java.time.LocalTime.parse(duty.start_time) // Parse the start time
        val endTime = java.time.LocalTime.parse(duty.end_time) // Parse the end time

        // Calculate the duration between start and end time
        val duration = java.time.Duration.between(startTime, endTime)

        // Return the total hours worked as an integer
        return duration.toHoursPart() // Returns only the hour part of the duration
    }
}
