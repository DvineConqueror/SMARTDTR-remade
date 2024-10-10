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
import com.example.smartdtr_remade.Api.RetrofitInstance
import com.example.smartdtr_remade.models.Duty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class teacher_appointment : Fragment() {

    private lateinit var teacherUpcomingDutyAdapter: TeacherUpcomingDutyAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_teacher_appointment, container, false)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_dutySchedule)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set up adapter
        teacherUpcomingDutyAdapter = TeacherUpcomingDutyAdapter(mutableListOf())
        recyclerView.adapter = teacherUpcomingDutyAdapter

        // Fetch the upcoming duties
        fetchUpcomingDuties()

        return view
    }

    private fun fetchUpcomingDuties() {
        RetrofitInstance.dutyApi.getUpcomingDuties().enqueue(object : Callback<List<Duty>> {
            override fun onResponse(call: Call<List<Duty>>, response: Response<List<Duty>>) {
                if (response.isSuccessful) {
                    val duties = response.body()
                    duties?.let {
                        Log.d("DutyResponse", "Number of duties: ${it.size}") // Log the size of the duties list
                        teacherUpcomingDutyAdapter.updateDuties(it)
                    } ?: Log.e("DutyResponse", "Response body is null")
                } else {
                    Log.e("DutyResponse", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Duty>>, t: Throwable) {
                Log.e("DutyResponse", "Failure: ${t.message}")
            }
        })
    }
}
