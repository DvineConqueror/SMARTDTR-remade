import android.adservices.adid.AdId
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartdtr_remade.R
import com.example.smartdtr_remade.models.Student

class StudentListCreateAdapter(private var students: MutableList<Student>) :
    RecyclerView.Adapter<StudentListCreateAdapter.StudentViewHolder>() {

    private val selectedStudentIds = mutableSetOf<Int>() // Store selected student IDs
    private var studentSelectionChangeListener: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_teacher_list_student_create_card, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.tvNameValue.text = "${student.firstname} ${student.lastname}"
        holder.tvStudentIdValue.text = student.student_id

        // remove listener before updating checked state
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = student.isChecked

        // Update the checked state when the checkbox is toggled
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            student.isChecked = isChecked
            studentSelectionChangeListener?.invoke()//notify listener of the change
        }
    }

    override fun getItemCount(): Int {
        return students.size
    }

    fun selectStudent(studentId: Int, isSelected: Boolean){
        if (isSelected){
            selectedStudentIds.add(studentId)
        } else {
            selectedStudentIds.remove(studentId)
        }
        notifyDataSetChanged()
    }

    // Function to select/deselect all students
    fun selectAllStudents(isChecked: Boolean) {
        for (student in students) {
            student.isChecked = isChecked
        }
        notifyDataSetChanged()
        studentSelectionChangeListener?.invoke()
    }

    fun areAllStudentsSelected(): Boolean {
        return students.all { it.isChecked }
    }

    fun setOnStudentSelectionChangeListener(listener: () -> Unit){
        studentSelectionChangeListener = listener
    }

    // Get a list of selected students
    fun getSelectedStudentIds(): List<Int> {
        return students.filter { it.isChecked }.map { it.id }
    }

    fun updateStudents(newStudents: List<Student>) {
        students.clear()
        students.addAll(newStudents)
        notifyDataSetChanged()
    }

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNameValue: TextView = itemView.findViewById(R.id.tvNameValue)
        val tvStudentIdValue: TextView = itemView.findViewById(R.id.tvStudentIdValue)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
    }
}
