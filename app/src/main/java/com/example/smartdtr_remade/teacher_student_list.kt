package com.example.smartdtr_remade.activityTeachers

import StudentListAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartdtr_remade.Api.RetrofitInstance
import com.example.smartdtr_remade.R
import com.example.smartdtr_remade.models.Student
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class teacher_student_list : Fragment() { // Changed to Fragment

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentListAdapter
    private val studentList = mutableListOf<Student>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_student_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_student_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = StudentListAdapter(studentList)
        recyclerView.adapter = adapter

        // Fetch student data from API
        RetrofitInstance.studentApi.getAllStudents().enqueue(object : Callback<List<Student>> {
            override fun onResponse(call: Call<List<Student>>, response: Response<List<Student>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        adapter.updateStudents(it)
                    }
                }
            }

            override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                Log.e("TeacherStudentListFragment", "Error fetching data", t)
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = teacher_student_list() // New instance method
    }
}
