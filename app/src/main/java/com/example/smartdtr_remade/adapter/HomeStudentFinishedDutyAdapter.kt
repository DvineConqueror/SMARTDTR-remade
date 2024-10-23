package com.example.smartdtr_remade.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartdtr_remade.R
import com.example.smartdtr_remade.models.Duty
import java.text.SimpleDateFormat
import java.util.Locale

class HomeStudentFinishedDutyAdapter(private var duties: List<Duty>) : RecyclerView.Adapter<HomeStudentFinishedDutyAdapter.DutyViewHolder>() {

    class DutyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val statusTextView: TextView = itemView.findViewById(R.id.status)
        val dateTextView: TextView = itemView.findViewById(R.id.upcoming_date)
        val startTimeTextView: TextView = itemView.findViewById(R.id.upcoming_start_time)
        val endTimeTextView: TextView = itemView.findViewById(R.id.upcoming_end_time)
        val teacherTextView: TextView = itemView.findViewById(R.id.upcoming_teacher)
        val subjectTextView: TextView = itemView.findViewById(R.id.upcoming_subject)
        val roomTextView: TextView = itemView.findViewById(R.id.upcoming_room)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DutyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_student_duty_card, parent, false) // Make sure the layout resource is correct
        return DutyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DutyViewHolder, position: Int) {
        val duty = duties[position]

        // Formatting the time
        holder.statusTextView.text = duty.status
        holder.dateTextView.text = duty.date
        holder.startTimeTextView.text = formatTime(duty.start_time)
        holder.endTimeTextView.text = formatTime(duty.end_time)

        // Display teacher's name, use "N/A" if null
        holder.teacherTextView.text = duty.teacher_name ?: "N/A"

        holder.subjectTextView.text = duty.subject
        holder.roomTextView.text = duty.room
    }

    override fun getItemCount(): Int {
        return if (duties.isNotEmpty()) 1 else 0 // Limit to 1 item
    }

    fun updateDuties(newDuties: List<Duty>) {
        duties = newDuties
        notifyDataSetChanged() // Notify the adapter to refresh the view
    }

    // Format time function
    private fun formatTime(time: String): String {
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val date = inputFormat.parse(time)
        return outputFormat.format(date!!)
    }
}

