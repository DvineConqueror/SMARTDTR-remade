import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartdtr_remade.R
import com.example.smartdtr_remade.models.Duty
import java.text.SimpleDateFormat
import java.util.Locale

class TeacherUpcomingDutyAdapter(private var duties: MutableList<Duty>) : RecyclerView.Adapter<TeacherUpcomingDutyAdapter.DutyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DutyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_teacher_duty_card, parent, false)
        return DutyViewHolder(view)
    }

    override fun onBindViewHolder(holder: DutyViewHolder, position: Int) {
        val duty = duties[position]

        // Formatting the time
        holder.statusTextView.text = duty.status
        holder.dateTextView.text = duty.date
        holder.startTimeTextView.text = formatTime(duty.start_time)
        holder.endTimeTextView.text = formatTime(duty.end_time)
        holder.teacherTextView.text = duty.teacher_name.toString()
        holder.subjectTextView.text = duty.subject
        holder.roomTextView.text = duty.room
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
        val statusTextView: TextView = itemView.findViewById(R.id.status)
        val dateTextView: TextView = itemView.findViewById(R.id.upcoming_date)
        val startTimeTextView: TextView = itemView.findViewById(R.id.upcoming_start_time)
        val endTimeTextView: TextView = itemView.findViewById(R.id.upcoming_end_time)
        val teacherTextView: TextView = itemView.findViewById(R.id.upcoming_teacher)
        val subjectTextView: TextView = itemView.findViewById(R.id.upcoming_subject)
        val roomTextView: TextView = itemView.findViewById(R.id.upcoming_room)
    }

    // Function to format time from hh:mm:ss to hh:mm
    private fun formatTime(time: String): String {
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val date = inputFormat.parse(time)
        return outputFormat.format(date!!)
    }
}
