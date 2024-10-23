    package com.example.smartdtr_remade

    import StudentListCreateAdapter
    import android.icu.util.Calendar
    import android.os.Bundle
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.*
    import androidx.fragment.app.Fragment
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.example.smartdtr_remade.Api.RetrofitInstance
    import com.example.smartdtr_remade.models.Duty
    import com.example.smartdtr_remade.models.GetDuty
    import com.example.smartdtr_remade.models.Student
    import com.google.android.material.button.MaterialButton
    import retrofit2.Call
    import retrofit2.Callback
    import retrofit2.Response

    class edit_appointment : Fragment() {

        private lateinit var recyclerView: RecyclerView
        private lateinit var adapter: StudentListCreateAdapter
        private lateinit var selectAllCheckBox: CheckBox
        private lateinit var calendarView: CalendarView
        private val studentList = mutableListOf<Student>()
        private lateinit var selectedDate: String
        private var teacherId: Int? = null
        private var dutyId: Int? = null // Variable to hold the duty ID

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // Retrieve duty ID from arguments
            arguments?.let {
                dutyId = it.getInt("DUTY_ID", 0) // Default to 0 if not found
            }
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.fragment_edit_appointment, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            // Initialize views
            val backButton: MaterialButton = view.findViewById(R.id.btnBack)
            val createButton: MaterialButton = view.findViewById(R.id.btnCreate)
            val spnrStartTime: Spinner = view.findViewById(R.id.spnrStartTime)
            val spnrEndTime: Spinner = view.findViewById(R.id.spnrEndTime)
            val etSubject: EditText = view.findViewById(R.id.etSubject)
            val etRoom: EditText = view.findViewById(R.id.etRoom)

            // Retrieve teacher ID from PreferencesManager
            teacherId = PreferencesManager(requireContext()).getUserId()?.toIntOrNull()

            // Setup RecyclerView and adapter
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
                            studentList.clear()
                            studentList.addAll(it)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }

                override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                    Log.e("TeacherCreateAppointment", "Error fetching data", t)
                }
            })

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

            // Initialize CalendarView
            calendarView = view.findViewById(R.id.calendarView)

            // Set the minimum date to today
            calendarView.minDate = System.currentTimeMillis()

            // Set a listener to capture the selected date
            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, dayOfMonth)

                // Check if the selected day is Sunday
                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    Toast.makeText(requireContext(), "Sundays are not selectable", Toast.LENGTH_SHORT).show()
                    return@setOnDateChangeListener // Exit if it's Sunday
                }

                selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth) // Format date as YYYY-MM-DD
            }

            createButton.setOnClickListener {
                // Check if a date has been selected
                if (!::selectedDate.isInitialized) {
                    Toast.makeText(requireContext(), "Please select a date", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener // Stay in the fragment without exiting
                }

                // Proceed with duty creation if a date is selected
                uploadDutyData(
                    etSubject.text.toString(),
                    etRoom.text.toString(),
                    spnrStartTime.selectedItem.toString(),
                    spnrEndTime.selectedItem.toString()
                )
            }

            // Prevent text from disappearing in the subject EditText
            etSubject.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && etSubject.text.toString() == "Enter a subject") {
                    etSubject.setText("") // Clear if default text is shown
                }
            }

            // Prevent text from disappearing in the room EditText
            etRoom.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && etRoom.text.toString() == "Enter a room") {
                    etRoom.setText("") // Clear if default text is shown
                }
            }

            // Fetch duty details to populate fields if updating
            dutyId?.let { fetchDutyDetails(it) }
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

        private fun fetchDutyDetails(dutyId: Int) {
            RetrofitInstance.dutyApi.getDuty(dutyId).enqueue(object : Callback<GetDuty> {
                override fun onResponse(call: Call<GetDuty>, response: Response<GetDuty>) {
                    if (response.isSuccessful) {
                        val getDuty = response.body()
                        getDuty?.let {
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

        private fun getTimePosition(time: String): Int {
            // Assuming time format is "HH:mm" or similar, find the position in the spinner
            // Implement your logic here to return the correct position
            // Example: return array of hours and find index
            return 0 // Replace with actual logic to find the correct position
        }

        private fun uploadDutyData(subject: String, room: String, startTime: String, endTime: String) {
            if (dutyId == null) {
                Toast.makeText(requireContext(), "Duty ID is not valid", Toast.LENGTH_SHORT).show()
                return
            }

            val selectedStudentsIds = adapter.getSelectedStudentIds()
            val duty = Duty(
                id = dutyId!!,
                teacher_id = teacherId ?: 0,
                teacher_name = "Teacher Name",
                student_ids = selectedStudentsIds,
                subject = subject,
                room = room,
                date = selectedDate,
                start_time = startTime,
                end_time = endTime,
                status = "Pending"
            )

            RetrofitInstance.dutyApi.updateDuty(dutyId!!, duty).enqueue(object : Callback<Duty> {
                override fun onResponse(call: Call<Duty>, response: Response<Duty>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Duty updated successfully", Toast.LENGTH_SHORT).show()
                        // Refresh the duties to see the updated data
                    } else {
                        Log.e("UpdateDuty", "Error: ${response.code()} - ${response.message()}")
                        Toast.makeText(requireContext(), "Failed to update duty: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Duty>, t: Throwable) {
                    Log.e("UpdateDuty", "Error: ${t.message}", t)
                    Toast.makeText(requireContext(), "Error occurred: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

    }
