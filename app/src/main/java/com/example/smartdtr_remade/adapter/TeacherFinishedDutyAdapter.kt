import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.smartdtr_remade.R
import com.example.smartdtr_remade.models.Duty
import java.text.SimpleDateFormat
import java.util.Locale

class TeacherFinishedDutyAdapter(
    private var duties: MutableList<Duty>,
    private val fragmentActivity: FragmentActivity, // Pass FragmentActivity for navigation
    private val onItemClick: (Duty) -> Unit // Callback for item click
) : RecyclerView.Adapter<TeacherFinishedDutyAdapter.DutyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DutyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_teacher_finished_card, parent, false)
        return DutyViewHolder(view)
    }

    override fun onBindViewHolder(holder: DutyViewHolder, position: Int) {
        val duty = duties[position]

        // Set item views
        holder.statusTextView.text = duty.status
        holder.dateTextView.text = duty.date
        holder.startTimeTextView.text = formatTime(duty.start_time)
        holder.endTimeTextView.text = formatTime(duty.end_time)
        holder.teacherTextView.text = duty.teacher_name.toString()
        holder.subjectTextView.text = duty.subject
        holder.roomTextView.text = duty.room

        // Set item click listener
        holder.itemView.setOnClickListener {
            onItemClick(duty) // Call the callback with the clicked Duty item
            // Navigate to detailed view or another fragment here if needed
        }
    }

    override fun getItemCount(): Int {
        return duties.size
    }

    fun updateDuties(newDuties: List<Duty>) {
        duties.clear()
        duties.addAll(newDuties)
        notifyDataSetChanged()
    }

    class DutyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val statusTextView: TextView = itemView.findViewById(R.id.finished_status)
        val dateTextView: TextView = itemView.findViewById(R.id.Finished_date)
        val startTimeTextView: TextView = itemView.findViewById(R.id.finished_start_time)
        val endTimeTextView: TextView = itemView.findViewById(R.id.finished_end_time)
        val teacherTextView: TextView = itemView.findViewById(R.id.finished_teacher)
        val subjectTextView: TextView = itemView.findViewById(R.id.finished_subject)
        val roomTextView: TextView = itemView.findViewById(R.id.finished_room)
    }

    // Navigation method (optional: if you want to navigate to a specific fragment)
    private fun navigateToFragment(fragment: Fragment) {
        val transaction = fragmentActivity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.recycler_dutySchedule, fragment) // Check this container ID
        transaction.addToBackStack(null)
        transaction.commit()
    }

    // Function to format time from hh:mm:ss to hh:mm
    private fun formatTime(time: String): String {
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val date = inputFormat.parse(time)
        return outputFormat.format(date!!)
    }
}
