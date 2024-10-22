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
import android.view.ViewStub // Import ViewStub
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
    private lateinit var viewStub: ViewStub // Declare ViewStub

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_student_history, container, false)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_dutySchedule)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize ViewStub
        viewStub = view.findViewById(R.id.nodata_viewstub) // Make sure the ID matches your layout

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
                        if (duties.isNullOrEmpty()) {
                            showNoDataView() // Show "No Data" view if duties list is empty
                        } else {
                            recyclerView.visibility = View.VISIBLE
                            studentFinishedDutyAdapter.updateDuties(duties)

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
                    showNoDataView() // Show "No Data" view on failure
                }
            })
        }
    }

    private fun showNoDataView() {
        // Inflate the ViewStub and display the no data layout
        recyclerView.visibility = View.GONE // Hide RecyclerView
        viewStub.visibility = View.VISIBLE  // Inflate and show the no data view
    }

    private fun updateTimerOnHomePage(remainingHours: Int) {
        // Assuming you have a reference to the home fragment or activity
        val homeFragment = parentFragmentManager.findFragmentByTag("homeFragment") as? student_home_page
        homeFragment?.updateTimerTextView(remainingHours)
    }
}
