import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.smartdtr_remade.R
import com.example.smartdtr_remade.models.Duty
import com.example.smartdtr_remade.teacher_create_appointment
import com.example.smartdtr_remade.teacher_student_list_create
import com.example.smartdtr_remade.teacher_student_list_view
import java.text.SimpleDateFormat
import java.util.Locale

class TeacherUpcomingDutyAdapter(
    private var duties: MutableList<Duty>,
    private val fragmentActivity: FragmentActivity,
    private val onItemClick: (Duty) -> Unit // Callback for item click
) : RecyclerView.Adapter<TeacherUpcomingDutyAdapter.DutyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DutyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_teacher_duty_card, parent, false)
        return DutyViewHolder(view)
    }

    override fun onBindViewHolder(holder: DutyViewHolder, position: Int) {
        val duty = duties[position]

        // Set data in ViewHolder
        holder.statusTextView.text = duty.status
        holder.dateTextView.text = duty.date
        holder.startTimeTextView.text = formatTime(duty.start_time)
        holder.endTimeTextView.text = formatTime(duty.end_time)
        holder.teacherTextView.text = duty.teacher_name
        holder.subjectTextView.text = duty.subject
        holder.roomTextView.text = duty.room

        /// Set item click listener
        holder.itemView.setOnClickListener {
            onItemClick(duty) // Call the callback with the clicked Duty item
        }
    }

    fun updateDuties(newDuties: List<Duty>) {
        duties.clear()
        duties.addAll(newDuties)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return duties.size
    }

    // ViewHolder class
    class DutyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val statusTextView: TextView = itemView.findViewById(R.id.status)
        val dateTextView: TextView = itemView.findViewById(R.id.upcoming_date)
        val startTimeTextView: TextView = itemView.findViewById(R.id.upcoming_start_time)
        val endTimeTextView: TextView = itemView.findViewById(R.id.upcoming_end_time)
        val teacherTextView: TextView = itemView.findViewById(R.id.upcoming_teacher)
        val subjectTextView: TextView = itemView.findViewById(R.id.upcoming_subject)
        val roomTextView: TextView = itemView.findViewById(R.id.upcoming_room)
    }

    // Navigation method
    private fun navigateToFragment(fragment: Fragment) {
        val transaction = fragmentActivity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.recycler_dutySchedule, fragment) // Check this container ID
        transaction.addToBackStack(null)
        transaction.commit()
    }

    // Helper to format time
    private fun formatTime(time: String): String {
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = inputFormat.parse(time)
        return outputFormat.format(date!!)
    }
}

