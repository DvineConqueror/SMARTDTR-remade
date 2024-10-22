package com.example.smartdtr_remade

import StudentListViewAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import android.widget.Button
import android.widget.TextView
import com.example.smartdtr_remade.Api.RetrofitInstance
import com.example.smartdtr_remade.models.Duty
import com.example.smartdtr_remade.models.Student
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class duty_view : Fragment() {

    private lateinit var btnBack: MaterialButton
    private lateinit var btnEdit: Button
    private lateinit var btnMarkAsDone: Button
    private lateinit var tvSubjectValue: TextView
    private lateinit var tvRoomValue: TextView
    private lateinit var textStartTimeValue: TextView
    private lateinit var textEndTimeValue: TextView
    private lateinit var recyclerStudentList: RecyclerView
    private lateinit var studentsAdapter: StudentListViewAdapter // Updated to your adapter
    private var duty: Duty? = null // Initialize the duty object

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_duty_view, container, false)

        // Retrieve the Duty object from the arguments
        duty = arguments?.getSerializable("DUTY_DETAILS") as? Duty

        // Initialize the views
        btnBack = view.findViewById(R.id.btnBack)
        btnEdit = view.findViewById(R.id.btnCreate)
        btnMarkAsDone = view.findViewById(R.id.btnMarkAsDone)
        tvSubjectValue = view.findViewById(R.id.tvSubjectValue)
        tvRoomValue = view.findViewById(R.id.tvRoomValue)
        textStartTimeValue = view.findViewById(R.id.textStartTimeValue)
        textEndTimeValue = view.findViewById(R.id.textEndTimeValue)
        recyclerStudentList = view.findViewById(R.id.recycler_student_list)

        // Set up the student list RecyclerView
        setupStudentList()

        // Set the duty details to the UI
        setDutyDetails()

        // Set up listeners for buttons
        setupListeners()

        return view
    }

    private fun setupStudentList() {
        studentsAdapter = StudentListViewAdapter(mutableListOf()) // Initialize adapter
        recyclerStudentList.layoutManager = LinearLayoutManager(requireContext())
        recyclerStudentList.adapter = studentsAdapter
    }

    private fun setDutyDetails() {
        duty?.let {
            tvSubjectValue.text = it.subject
            tvRoomValue.text = it.room
            textStartTimeValue.text = formatTime(it.start_time)
            textEndTimeValue.text = formatTime(it.end_time)

            // Fetch students related to this duty
            fetchStudentsForDuty(it.id)
        }
    }

    private fun fetchStudentsForDuty(dutyId: Int) {
        RetrofitInstance.studentApi.getStudentsForDuty(dutyId).enqueue(object : Callback<List<Student>> {
            override fun onResponse(call: Call<List<Student>>, response: Response<List<Student>>) {
                if (response.isSuccessful) {
                    val students = response.body()
                    students?.let {
                        studentsAdapter.updateStudents(it) // Assuming your adapter has an update method
                    }
                } else {
                    Log.e("StudentList", "Failed to fetch students")
                }
            }

            override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                Log.e("StudentList", "Error fetching students: ${t.message}")
            }
        })
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { requireActivity().onBackPressed() }
        btnEdit.setOnClickListener { editDuty() }
        btnMarkAsDone.setOnClickListener { markDutyAsDone() }
    }

    private fun editDuty() {
        // Implement logic to navigate to edit duty screen
    }

    private fun markDutyAsDone() {
        // Implement logic to mark the duty as done
    }

    private fun formatTime(time: String): String {
        return time // Implement your time formatting logic here
    }
}


