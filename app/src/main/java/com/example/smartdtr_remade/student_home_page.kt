package com.example.smartdtr_remade

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var refreshPrompt: TextView
    private var newDutiesAvailable = false
    private var previousUpcomingDuties: List<Duty> = emptyList()


    private val handler = Handler()
    private val refreshRunnable = object : Runnable {
        override fun run() {
            checkForNewDuties()
            handler.postDelayed(this, 3000) // Check every 5 seconds
        }
    }

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
        swipeRefreshLayout = view.findViewById(R.id.refreshLayout)
        refreshPrompt = view.findViewById(R.id.refreshPrompt)

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

        swipeRefreshLayout.setOnRefreshListener {
            refreshPrompt.visibility = View.GONE
            fetchUpcomingDuties() // Refresh duties
        }


        return view
    }

    override fun onResume() {
        super.onResume()
        handler.post(refreshRunnable) // Start periodic check when fragment is active
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(refreshRunnable) // Stop periodic check when fragment is not active
    }

    private fun checkForNewDuties() {
        val loggedInStudentId = preferencesManager.getUserId()
        if (loggedInStudentId != null) {
            RetrofitInstance.dutyApi.getUpcomingDutiesStudent(loggedInStudentId).enqueue(object : Callback<List<Duty>> {
                override fun onResponse(call: Call<List<Duty>>, response: Response<List<Duty>>) {
                    if (response.isSuccessful) {
                        val latestDuties = response.body() ?: emptyList()
                        if (latestDuties != previousUpcomingDuties) {
                            newDutiesAvailable = true // Set flag to indicate new duties are available
                            refreshPrompt.visibility = View.VISIBLE // Show the refresh prompt
                        }
                    }
                }

                override fun onFailure(call: Call<List<Duty>>, t: Throwable) {
                    Log.e("DutyResponse", "Failure: ${t.message}")
                }
            })
        }
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
        swipeRefreshLayout.isRefreshing = true // Start the loading animation

        if (loggedInStudentId != null) {
            RetrofitInstance.dutyApi.getUpcomingDutiesStudent(loggedInStudentId).enqueue(object : Callback<List<Duty>> {
                override fun onResponse(call: Call<List<Duty>>, response: Response<List<Duty>>) {
                    if (response.isSuccessful) {
                        val newDuties = response.body() ?: emptyList()

                        if (newDuties != previousUpcomingDuties) { // Show prompt if there's an update
                            previousUpcomingDuties = newDuties // Update previous duties
                            studentUpcomingDutyAdapter.updateDuties(newDuties)
                            refreshPrompt.visibility = View.VISIBLE // Show the prompt
                            newDutiesAvailable = true // Set the flag for new duties
                        } else {
                            refreshPrompt.visibility = View.GONE // Hide if no updates
                        }
                        //fetch duties to update the timer
                        fetchFinishedDuties()

                    } else {
                        refreshPrompt.visibility = View.GONE // Hide the prompt on unsuccessful response
                    }
                    swipeRefreshLayout.isRefreshing = false // Stop loading animation

                    // Hide the refresh prompt after refreshing
                    if (newDutiesAvailable) {
                        refreshPrompt.visibility = View.GONE // Hide the prompt after refreshing
                        newDutiesAvailable = false // Reset the flag
                    }
                }

                override fun onFailure(call: Call<List<Duty>>, t: Throwable) {
                    Log.e("DutyResponse", "Failure: ${t.message}")
                    swipeRefreshLayout.isRefreshing = false // Stop loading animation
                }
            })
        } else {
            swipeRefreshLayout.isRefreshing = false // Stop loading animation
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
        return duration.toHours().toInt() // Returns only the hour part of the duration
    }
}
