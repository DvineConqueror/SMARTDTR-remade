    package com.example.smartdtr_remade

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
    import com.example.smartdtr_remade.PreferencesManager // Make sure you import your PreferencesManager
    import com.example.smartdtr_remade.adapter.StudentUpcomingDutyAdapter
    import retrofit2.Call
    import retrofit2.Callback
    import retrofit2.Response

    class student_appointment : Fragment() {

        private lateinit var studentUpcomingDutyAdapter: StudentUpcomingDutyAdapter
        private lateinit var recyclerView: RecyclerView
        private lateinit var preferencesManager: PreferencesManager

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_student_appointment, container, false)

            preferencesManager = PreferencesManager(requireContext())
            recyclerView = view.findViewById(R.id.recycler_dutySchedule)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            studentUpcomingDutyAdapter = StudentUpcomingDutyAdapter(mutableListOf())
            recyclerView.adapter = studentUpcomingDutyAdapter

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
                            duties?.let {
                                Log.d("DutyResponse", "Duties received: ${it.joinToString { it.subject }}")
                                studentUpcomingDutyAdapter.updateDuties(it) // Update adapter
                            } ?: Log.e("DutyResponse", "Response body is null")
                        } else {
                            Log.e("DutyResponse", "Error: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<List<Duty>>, t: Throwable) {
                        Log.e("DutyResponse", "Failure: ${t.message}")
                    }
                })
            } else {
                Log.e("StudentDuties", "Student ID is null or invalid")
            }
        }
    }



