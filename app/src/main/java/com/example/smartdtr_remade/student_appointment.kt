package com.example.smartdtr_remade

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
import com.example.smartdtr_remade.adapter.StudentUpcomingDutyAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class student_appointment : Fragment() {

    private lateinit var studentUpcomingDutyAdapter: StudentUpcomingDutyAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var viewStub: ViewStub // Declare ViewStub

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_appointment, container, false)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_dutySchedule)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize ViewStub
        viewStub = view.findViewById(R.id.nodata_viewstub)

        // Set up adapter
        studentUpcomingDutyAdapter = StudentUpcomingDutyAdapter(mutableListOf())
        recyclerView.adapter = studentUpcomingDutyAdapter

        // Initialize PreferencesManager
        preferencesManager = PreferencesManager(requireContext())

        // Fetch the upcoming duties
        fetchStudentUpcomingDuties()

        return view
    }

    private fun fetchStudentUpcomingDuties() {
        val loggedInStudentId = preferencesManager.getUserId()

        if (loggedInStudentId != null) {
            RetrofitInstance.dutyApi.getUpcomingDutiesStudent(loggedInStudentId).enqueue(object : Callback<List<Duty>> {
                override fun onResponse(call: Call<List<Duty>>, response: Response<List<Duty>>) {
                    if (response.isSuccessful) {
                        val duties = response.body()
                        if (duties.isNullOrEmpty()) {
                            showNoDataView()  // Show "No Data" view if no duties
                        } else {
                            recyclerView.visibility = View.VISIBLE
                            studentUpcomingDutyAdapter.updateDuties(duties)
                        }
                    } else {
                        Log.e("DutyResponse", "Error: ${response.code()}")
                        showNoDataView()  // Show "No Data" view on error
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
        recyclerView.visibility = View.GONE // Hide RecyclerView
        viewStub.visibility = View.VISIBLE  // Inflate and show the no data view
    }
}
