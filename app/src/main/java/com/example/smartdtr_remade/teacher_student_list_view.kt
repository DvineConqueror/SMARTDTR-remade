package com.example.smartdtr_remade

import StudentListViewAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartdtr_remade.Api.RetrofitInstance
import com.example.smartdtr_remade.databinding.FragmentDutyViewBinding
import com.example.smartdtr_remade.models.Duty
import com.example.smartdtr_remade.models.Student
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class teacher_student_list_view : Fragment() {

    private var _binding: FragmentDutyViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: StudentListViewAdapter
    private lateinit var duty: Duty

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDutyViewBinding.inflate(inflater, container, false)

        // Initialize RecyclerView with an empty list initially
        adapter = StudentListViewAdapter(mutableListOf())
        binding.recyclerStudentList.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerStudentList.adapter = adapter

        // Get Duty object passed via arguments (if applicable)
        arguments?.let {
            duty = it.getSerializable("duty") as Duty
            populateDutyDetails(duty)
            fetchStudentsForDuty(duty.student_ids)
        }

        return binding.root
    }

    private fun populateDutyDetails(duty: Duty) {
        binding.tvSubjectValue.text = duty.subject
        binding.tvRoomValue.text = duty.room
        binding.textStartTimeValue.text = duty.start_time
        binding.textEndTimeValue.text = duty.end_time
    }

    private fun fetchStudentsForDuty(studentIds: List<Int>) {
        RetrofitInstance.studentApi.getStudentsByIds(studentIds).enqueue(object : Callback<List<Student>> {
            override fun onResponse(call: Call<List<Student>>, response: Response<List<Student>>) {
                if (response.isSuccessful) {
                    val students = response.body() ?: emptyList()
                    adapter.updateStudents(students)
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch students", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
