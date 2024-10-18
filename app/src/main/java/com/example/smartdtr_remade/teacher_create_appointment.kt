package com.example.smartdtr_remade

import StudentListCreateAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartdtr_remade.Api.RetrofitInstance
import com.example.smartdtr_remade.models.Student
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class teacher_create_appointment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentListCreateAdapter
    private lateinit var selectAllCheckBox: CheckBox
    private val studentList = mutableListOf<Student>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_create_appointment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        val backButton: MaterialButton = view.findViewById(R.id.btnBack)
        val spnrStartTime: Spinner = view.findViewById(R.id.spnrStartTime)
        val spnrEndTime: Spinner = view.findViewById(R.id.spnrEndTime)
        val etSubject: EditText = view.findViewById(R.id.etSubject)
        val etRoom: EditText = view.findViewById(R.id.etRoom)

        // Setup RecyclerView and adapter
        recyclerView = view.findViewById(R.id.recycler_student_list)
        selectAllCheckBox = view.findViewById(R.id.checkBox)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = StudentListCreateAdapter(studentList)
        recyclerView.adapter = adapter

        // Fetch student data from API
        fetchStudentData()

        // Handle "Select All" checkbox behavior
        selectAllCheckBox.setOnCheckedChangeListener { _, isChecked ->
            adapter.selectAllStudents(isChecked)
        }

        // Back button click listener
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Setup spinners for time selection
        setupTimeSpinners(spnrStartTime, spnrEndTime)
    }

    private fun fetchStudentData() {
        RetrofitInstance.studentlistApi.getAllStudents().enqueue(object : Callback<List<Student>> {
            override fun onResponse(call: Call<List<Student>>, response: Response<List<Student>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        studentList.clear()  // Clear existing list
                        studentList.addAll(it)  // Add new students
                        adapter.notifyDataSetChanged()  // Notify adapter of data changes
                    }
                }
            }

            override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                Log.e("TeacherCreateAppointment", "Error fetching data", t)
            }
        })
    }

    private fun setupTimeSpinners(startSpinner: Spinner, endSpinner: Spinner) {
        val adapterTimeHour: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.Hour,
            android.R.layout.simple_spinner_item
        )
        adapterTimeHour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        startSpinner.adapter = adapterTimeHour
        endSpinner.adapter = adapterTimeHour // Assuming both use the same array; adjust as needed
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String?, param2: String?) =
            teacher_create_appointment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
