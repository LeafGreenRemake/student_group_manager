package com.example.student_group_manager.Adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.student_group_manager.R
import com.example.student_group_manager.data.Student
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.database

class StudentAdapter(private val students: MutableList<Student>, private val classroomId: String?, private val subjectId: String?) : RecyclerView.Adapter<StudentAdapter.ViewHolder>() {

    private lateinit var auth: FirebaseAuth

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTv: TextView = view.findViewById(R.id.student_name_tv)
        val removeButton: Button = view.findViewById(R.id.remove_student)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.student_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = students[position]
        holder.nameTv.text = student.name

        auth = Firebase.auth
        val currentUser = auth.currentUser
        val uid = currentUser?.uid ?: return
        val studentId = student.id
        val database = Firebase.database


        holder.removeButton.setOnClickListener {
            val context = holder.itemView.context

            AlertDialog.Builder(context)
                .setMessage("למחוק את התלמיד מהכיתה?")
                .setPositiveButton("כן") { _, _ ->
                    val classroomOfStudentRef =
                        classroomId?.let { pathString -> database.getReference("students").child(studentId).child("classrooms").child(pathString) }
                    classroomOfStudentRef?.removeValue()

                    if (subjectId != null) {
                        val studentRef =
                            (classroomId)?.let { pathString -> database.getReference("teachers").child(uid).child("subjects").child(subjectId).child("subjectClassrooms").child(classroomId).child("students").child(studentId)
                            }
                        studentRef?.removeValue()
                    }
                }

                .setNegativeButton("לא", null)
                .show()
        }
    }

    override fun getItemCount(): Int = students.size
}