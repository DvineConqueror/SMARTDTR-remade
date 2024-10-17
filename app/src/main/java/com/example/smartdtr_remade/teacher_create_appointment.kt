package com.example.smartdtr_remade

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.google.android.material.button.MaterialButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [teacher_create_appointment.newInstance] factory method to
 * create an instance of this fragment.
 */
class teacher_create_appointment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backButton: MaterialButton = view.findViewById(R.id.btnBack)
        val spnrStartTime: Spinner = view.findViewById(R.id.spnrStartTime)
        val spnrEndTime: Spinner = view.findViewById(R.id.spnrEndTime)
        val etSubject: EditText = view.findViewById(R.id.etSubject)
        val etRoom: EditText = view.findViewById(R.id.etRoom)

        val adapterTimeHour: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.Hour,
            android.R.layout.simple_spinner_item
        )
        adapterTimeHour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnrStartTime.adapter = adapterTimeHour

        val adapterTimeMin: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.Hour,
            android.R.layout.simple_spinner_item
        )
        adapterTimeMin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnrEndTime.adapter = adapterTimeMin

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_create_appointment, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment teacher_create_appointment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            teacher_create_appointment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}