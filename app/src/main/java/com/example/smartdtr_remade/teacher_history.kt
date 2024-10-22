package com.example.smartdtr_remade

import TeacherFinishedDutyAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub // Import ViewStub
import com.example.smartdtr_remade.Api.RetrofitInstance
import com.example.smartdtr_remade.models.Duty
import com.example.smartdtr_remade.PreferencesManager
import com.example.smartdtr_remade.activityTeachers.Main_teacher
import com.example.smartdtr_remade.activityTeachers.teacher_list_student_card
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class teacher_history : Fragment() {

    private lateinit var teacherFinishedDutyAdapter: TeacherFinishedDutyAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var viewStub: ViewStub // Declare ViewStub

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_teacher_history, container, false)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_dutySchedule)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize ViewStub
        viewStub = view.findViewById(R.id.nodata_viewstub)

        // Set up adapter with the click listener
        teacherFinishedDutyAdapter = TeacherFinishedDutyAdapter(mutableListOf()) { duty ->
            // Handle item click to replace the current fragment with DutyDetailFragment
            val dutyDetailFragment = teacher_create_appointment().apply {
                arguments = Bundle().apply {
                    putSerializable("DUTY_DETAILS", duty) // Pass the clicked Duty object
                }
            }

            // Replace the current fragment with DutyDetailFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, dutyDetailFragment) // Change to your actual container ID
                .addToBackStack(null) // Optional: adds the transaction to the back stack
                .commit()
        }

        recyclerView.adapter = teacherFinishedDutyAdapter

        // Initialize PreferencesManager
        preferencesManager = PreferencesManager(requireContext())

        // Fetch the finished duties
        fetchFinishedDuties()

        return view
    }

    private fun fetchFinishedDuties() {
        // Get the logged-in teacher's ID
        val loggedInTeacherId = preferencesManager.getUserId()

        Log.d("TeacherHistory", "Logged-in Teacher ID: $loggedInTeacherId")

        if (loggedInTeacherId != null) {
            // Call the API to get completed duties for the logged-in teacher
            RetrofitInstance.dutyApi.getCompletedDutiesTeacher(loggedInTeacherId).enqueue(object : Callback<List<Duty>> {
                override fun onResponse(call: Call<List<Duty>>, response: Response<List<Duty>>) {
                    if (response.isSuccessful) {
                        val duties = response.body()
                        if (duties.isNullOrEmpty()) {
                            showNoDataView() // Show "No Data" view if duties list is empty
                        } else {
                            recyclerView.visibility = View.VISIBLE
                            teacherFinishedDutyAdapter.updateDuties(duties)
                        }
                    } else {
                        Log.e("DutyResponse", "Error: ${response.code()}")
                        showNoDataView() // Show "No Data" view on error
                    }
                }

                override fun onFailure(call: Call<List<Duty>>, t: Throwable) {
                    Log.e("DutyResponse", "Failure: ${t.message}")
                    showNoDataView() // Show "No Data" view on failure
                }
            })
        } else {
            Log.e("TeacherHistory", "Teacher ID is null or invalid")
            showNoDataView() // Show "No Data" view if no teacher ID
        }
    }

    private fun showNoDataView() {
        // Inflate the ViewStub and display the no data layout
        recyclerView.visibility = View.GONE // Hide RecyclerView
        viewStub.visibility = View.VISIBLE  // Inflate and show the no data view
    }
}
