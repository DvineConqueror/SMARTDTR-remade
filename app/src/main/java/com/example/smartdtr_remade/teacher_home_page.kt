package com.example.smartdtr_remade

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smartdtr_remade.activityTeachers.teacher_student_list
import com.example.smartdtr_remade.databinding.FragmentTeacherHomePageBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [teacher_home_page.newInstance] factory method to
 * create an instance of this fragment.
 */
class teacher_home_page : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentTeacherHomePageBinding? = null
    private val binding get() = _binding!!
    private var param1: String? = null
    private var param2: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTeacherHomePageBinding.bind(view)

        // In your Fragment where the button is located, e.g., teacher_home_page
        binding.appointmentButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, teacher_create_appointment.newInstance("Value1", "Value2")) // Passing null for params if not needed
                .addToBackStack(null)
                .commit()
        }

        binding.btUpcomingStudentList.setOnClickListener(){
            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, teacher_student_list.newInstance())
                .addToBackStack(null)
                .commit()
        }


        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_home_page, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment teacher_home_page.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            teacher_home_page().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}