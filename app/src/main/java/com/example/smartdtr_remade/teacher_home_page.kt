package com.example.smartdtr_remade

import HomeTeacherFinishedAdapter
import HomeTeacherUpcomingAdapter
import TeacherFinishedDutyAdapter
import TeacherUpcomingDutyAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartdtr_remade.R
import com.example.smartdtr_remade.Api.RetrofitInstance
import com.example.smartdtr_remade.models.Duty
import com.example.smartdtr_remade.PreferencesManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class teacher_home_page : Fragment() {

    private lateinit var recyclerViewUpcoming: RecyclerView
    private lateinit var recyclerViewCompleted: RecyclerView
    private lateinit var teacherUpcomingDutyAdapter: HomeTeacherUpcomingAdapter
    private lateinit var teacherFinishedDutyAdapter: HomeTeacherFinishedAdapter
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var makeAppointmentButton: Button
    private lateinit var viewStub: ViewStub

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_teacher_home_page, container, false)

        recyclerViewUpcoming = view.findViewById(R.id.recyclerView_duties)
        recyclerViewUpcoming.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewCompleted = view.findViewById(R.id.recyclerView_finished_duties)
        recyclerViewCompleted.layoutManager = LinearLayoutManager(requireContext())
        makeAppointmentButton = view.findViewById(R.id.appointmentButton)

        preferencesManager = PreferencesManager(requireContext())

        /// Set up adapter with the click listener
        teacherUpcomingDutyAdapter = HomeTeacherUpcomingAdapter(mutableListOf(), requireActivity()) { duty ->
            val dutyDetailFragment = duty_view().apply {
                arguments = Bundle().apply {
                    putSerializable("DUTY_DETAILS", duty) // Pass the clicked Duty object
                    putInt("DUTY_ID", duty.id) // Pass the clicked Duty ID
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, dutyDetailFragment) // Change to your actual container ID
                .addToBackStack(null) // Optional: adds the transaction to the back stack
                .commit()
        }
        recyclerViewUpcoming.adapter = teacherUpcomingDutyAdapter

        // Set up adapter with the click listener
        teacherFinishedDutyAdapter = HomeTeacherFinishedAdapter(mutableListOf(), requireActivity()) { duty ->
            // Handle item click to replace the current fragment with FinishedDutyView
            val finishedDutyDetailFragment = finished_duty_view().apply {
                arguments = Bundle().apply {
                    putSerializable("DUTY_DETAILS", duty) // Pass the clicked Duty object
                    putInt("DUTY_ID", duty.id)
                }
            }

            // Replace the current fragment with FinishedDutyView
            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, finishedDutyDetailFragment) // Change to your actual container ID
                .addToBackStack(null) // Optional: adds the transaction to the back stack
                .commit()
        }
        recyclerViewCompleted.adapter = teacherFinishedDutyAdapter


        // Fetch duties
        fetchUpcomingDuties()
        fetchFinishedDuties()

        // Set up button listener
        makeAppointmentButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, teacher_create_appointment()) // Passing null for params if not needed
                .addToBackStack(null)
                .commit()
        }

        preferencesManager = PreferencesManager(requireContext())

        return view
    }

    private fun fetchUpcomingDuties() {
        val teacherId = preferencesManager.getUserId()

        if (teacherId != null) {
            RetrofitInstance.dutyApi.getUpcomingDutiesTeacher(teacherId).enqueue(object : Callback<List<Duty>> {
                override fun onResponse(call: Call<List<Duty>>, response: Response<List<Duty>>) {
                    if (response.isSuccessful) {
                        val duties = response.body()
                        if (!duties.isNullOrEmpty()) {
                            teacherUpcomingDutyAdapter.updateDuties(duties)
                        } else {
                            showNoDataView(recyclerViewUpcoming)
                        }
                    }
                }

                override fun onFailure(call: Call<List<Duty>>, t: Throwable) {
                    Log.e("API Error", "Error fetching upcoming duties: ${t.message}")
                    showNoDataView(recyclerViewUpcoming)
                }
            })
        }
    }

    private fun fetchFinishedDuties() {
        val teacherId = preferencesManager.getUserId()

        if (teacherId != null) {
            RetrofitInstance.dutyApi.getCompletedDutiesTeacher(teacherId).enqueue(object : Callback<List<Duty>> {
                override fun onResponse(call: Call<List<Duty>>, response: Response<List<Duty>>) {
                    if (response.isSuccessful) {
                        val duties = response.body()
                        if (!duties.isNullOrEmpty()) {
                            teacherFinishedDutyAdapter.updateDuties(duties)
                        } else {
                            showNoDataView(recyclerViewCompleted)
                        }
                    }
                }

                override fun onFailure(call: Call<List<Duty>>, t: Throwable) {
                    Log.e("API Error", "Error fetching finished duties: ${t.message}")
                    showNoDataView(recyclerViewCompleted)
                }
            })
        }
    }

    private fun showNoDataView(recyclerView: RecyclerView) {
        recyclerView.visibility = View.GONE
        // You can also show a TextView or another UI element to indicate no data is available
        // Example:
        // noDataTextView.visibility = View.VISIBLE
    }
}
