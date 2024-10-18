package com.example.smartdtr_remade

import StudentListCreateAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartdtr_remade.Api.RetrofitInstance
import com.example.smartdtr_remade.models.Student
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class teacher_student_list_create : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentListCreateAdapter
    private lateinit var selectAllCheckBox: CheckBox
    private val studentList = mutableListOf<Student>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_teacher_student_list_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_student_list)
        selectAllCheckBox = view.findViewById(R.id.checkBox)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = StudentListCreateAdapter(studentList)
        recyclerView.adapter = adapter

        // Fetch student data from API
        RetrofitInstance.studentlistApi.getAllStudents().enqueue(object : Callback<List<Student>> {
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

        // Handle "Select All" checkbox behavior
        selectAllCheckBox.setOnCheckedChangeListener { _, isChecked ->
            adapter.selectAllStudents(isChecked)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = teacher_student_list_create()
    }
}

