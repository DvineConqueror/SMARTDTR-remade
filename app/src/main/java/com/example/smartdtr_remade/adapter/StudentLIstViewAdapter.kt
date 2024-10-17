import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartdtr_remade.R
import com.example.smartdtr_remade.models.Student

class StudentListViewAdapter(private var students: MutableList<Student>) :
    RecyclerView.Adapter<StudentListViewAdapter.StudentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_teacher_list_student_view, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.tvNameValue.text = "${student.firstname} ${student.lastname}"
        holder.tvStudentIdValue.text = student.student_id
    }

    override fun getItemCount(): Int {
        return students.size
    }

    fun updateStudents(newStudents: List<Student>) {
        students.clear()
        students.addAll(newStudents)
        notifyDataSetChanged()
    }

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNameValue: TextView = itemView.findViewById(R.id.tvNameValue)
        val tvStudentIdValue: TextView = itemView.findViewById(R.id.tvStudentIdValue)
    }
}
