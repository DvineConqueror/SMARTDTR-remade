package com.example.smartdtr_remade

import StudentListViewAdapter
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartdtr_remade.Api.RetrofitInstance
import com.example.smartdtr_remade.models.GetDuty
import com.example.smartdtr_remade.models.Student
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class finished_duty_view : Fragment() {

    private lateinit var studentsAdapter: StudentListViewAdapter
    private lateinit var recyclerStudentList: RecyclerView  // RecyclerView for the student list

    // Lazy property to get duty ID from the arguments
    private val dutyId: Int by lazy { arguments?.getInt("DUTY_ID") ?: 0 }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_finished_duty_view, container, false)

        val backButton: MaterialButton = view.findViewById(R.id.btnBack)

        // Back button click listener
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        // Initialize RecyclerView
        recyclerStudentList = view.findViewById(R.id.recycler_student_list)

        // Fetch the duty details using the duty ID
        fetchDutyDetails(dutyId)

        return view
    }

    private fun fetchDutyDetails(dutyId: Int) {
        RetrofitInstance.dutyApi.getDuty(dutyId).enqueue(object : Callback<GetDuty> {
            override fun onResponse(call: Call<GetDuty>, response: Response<GetDuty>) {
                if (response.isSuccessful) {
                    val getDuty = response.body()
                    getDuty?.let {
                        // Populate the UI with duty details
                        populateDutyDetails(it)

                        // Initialize the adapter with student IDs and set it to the RecyclerView
                        setupStudentList(it.student_ids)
                    }
                } else {
                    Log.e("DutyView", "Failed to fetch duty details: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<GetDuty>, t: Throwable) {
                Log.e("DutyView", "Error fetching duty details: ${t.message}")
            }
        })
    }

    private fun populateDutyDetails(duty: GetDuty) {
        val startTimeTextView: TextView = view?.findViewById(R.id.textStartTimeValue) ?: return
        val endTimeTextView: TextView = view?.findViewById(R.id.textEndTimeValue) ?: return
        val roomTextView: TextView = view?.findViewById(R.id.tvRoomValue) ?: return
        val subjectTextView: TextView = view?.findViewById(R.id.tvSubjectValue) ?: return

        startTimeTextView.text = formatTime(duty.start_time)
        endTimeTextView.text = formatTime(duty.end_time)
        roomTextView.text = duty.room
        subjectTextView.text = duty.subject
    }

    private fun setupStudentList(studentIds: List<Int>) {
        // Initialize the adapter with an empty student list
        studentsAdapter = StudentListViewAdapter(mutableListOf())
        recyclerStudentList.layoutManager = LinearLayoutManager(requireContext())
        recyclerStudentList.adapter = studentsAdapter

        // Fetch students for this duty (you no longer need to pass studentIds)
        fetchStudentsForDuty()
    }

    private fun fetchStudentsForDuty() {
        // Call the API using the dutyId
        RetrofitInstance.studentApi.getStudentsFromDuty(dutyId).enqueue(object : Callback<List<Student>> {
            override fun onResponse(call: Call<List<Student>>, response: Response<List<Student>>) {
                if (response.isSuccessful) {
                    val students = response.body()
                    students?.let {
                        studentsAdapter.updateStudents(it)  // Update the adapter with fetched students
                    }
                } else {
                    Log.e("StudentList", "Failed to fetch students: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                Log.e("StudentList", "Error fetching students: ${t.message}")
            }
        })
    }

    private fun formatTime(time: String): String {
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = inputFormat.parse(time)
        return outputFormat.format(date!!)
    }
}
