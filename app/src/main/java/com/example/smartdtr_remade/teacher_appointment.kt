package com.example.smartdtr_remade

import TeacherUpcomingDutyAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub // Add this import
import com.example.smartdtr_remade.Api.RetrofitInstance
import com.example.smartdtr_remade.models.Duty
import com.example.smartdtr_remade.PreferencesManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class teacher_appointment : Fragment() {

    private lateinit var teacherUpcomingDutyAdapter: TeacherUpcomingDutyAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var viewStub: ViewStub // ViewStub declaration

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_teacher_appointment, container, false)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_dutySchedule)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize ViewStub
        viewStub = view.findViewById(R.id.nodata_viewstub)

        // Set up adapter with the click listener
        teacherUpcomingDutyAdapter = TeacherUpcomingDutyAdapter(mutableListOf(), requireActivity()) { duty ->
            // Handle item click to replace the current fragment with DutyDetailFragment
            val dutyDetailFragment = duty_view().apply {
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

        recyclerView.adapter = teacherUpcomingDutyAdapter

        // Initialize PreferencesManager
        preferencesManager = PreferencesManager(requireContext())

        // Fetch the upcoming duties
        fetchUpcomingDuties()

        return view
    }

    private fun fetchUpcomingDuties() {
        val loggedInTeacherId = preferencesManager.getUserId()

        if (loggedInTeacherId != null) {
            RetrofitInstance.dutyApi.getUpcomingDutiesTeacher(loggedInTeacherId).enqueue(object : Callback<List<Duty>> {
                override fun onResponse(call: Call<List<Duty>>, response: Response<List<Duty>>) {
                    if (response.isSuccessful) {
                        val duties = response.body()
                        if (duties.isNullOrEmpty()) {
                            showNoDataView()  // Show "No Data" view
                        } else {
                            recyclerView.visibility = View.VISIBLE
                            teacherUpcomingDutyAdapter.updateDuties(duties)
                        }
                    }
                }

                override fun onFailure(call: Call<List<Duty>>, t: Throwable) {
                    Log.e("DutyResponse", "Failure: ${t.message}")
                    showNoDataView()  // Show "No Data" view on failure
                }
            })
        }
    }

    private fun showNoDataView() {
        // Inflate the ViewStub and display the no data layout
        recyclerView.visibility = View.GONE // Hide RecyclerView
        viewStub.visibility = View.VISIBLE  // Inflate and show the no data view
    }
}

