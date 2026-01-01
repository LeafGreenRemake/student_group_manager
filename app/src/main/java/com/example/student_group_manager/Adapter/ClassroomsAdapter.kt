package com.example.student_group_manager.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.student_group_manager.GroupsScreenActivity
import com.example.student_group_manager.R
import com.example.student_group_manager.StudentsScreenActivity
import com.example.student_group_manager.data.Classroom

class ClassroomsAdapter(
    private val classrooms: MutableList<Classroom>,
    private val subjectId: String  // Add this
) : RecyclerView.Adapter<ClassroomsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTv: TextView = view.findViewById(R.id.classroom_name_tv)
        val groupsButton: Button = view.findViewById(R.id.groups_button)
        val studentsButton: Button = view.findViewById(R.id.students_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.classroom_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val classroom = classrooms[position]
        holder.nameTv.text = classroom.name

        holder.groupsButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, GroupsScreenActivity::class.java)
            intent.putExtra("subject_id", subjectId)
            intent.putExtra("classroom_id", classroom.id)
            intent.putExtra("classroom_name", classroom.name)
            context.startActivity(intent)
        }

        holder.studentsButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, StudentsScreenActivity::class.java)
            intent.putExtra("subject_id", subjectId)  // Pass subject ID
            intent.putExtra("classroom_id", classroom.id)  // Pass classroom ID
            intent.putExtra("classroom_name", classroom.name)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = classrooms.size
}