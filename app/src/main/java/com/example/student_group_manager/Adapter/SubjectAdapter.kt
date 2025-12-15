package com.example.student_group_manager.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.student_group_manager.ClassroomsScreenActivity
import com.example.student_group_manager.data.Subject
import com.example.student_group_manager.R
import com.example.student_group_manager.TasksScreenActivity

class SubjectAdapter(private val subjects: MutableList<Subject>) : RecyclerView.Adapter<SubjectAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTv: TextView = view.findViewById(R.id.subject_name_tv)
        val descTv: TextView = view.findViewById(R.id.description_tv)
        val classroomsButton: Button = view.findViewById(R.id.classrooms_button)
        val tasksButton: Button = view.findViewById(R.id.tasks_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.subject_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val subject = subjects[position]
        holder.nameTv.text = subject.subject_name
        holder.descTv.text = subject.description

        holder.classroomsButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ClassroomsScreenActivity::class.java)
            intent.putExtra("subject_id", subject.id)  // Pass ID for Firebase queries
            intent.putExtra("subject_name", subject.subject_name)  // Pass name for display
            // Optionally pass more data, e.g., intent.putExtra("subject_description", subject.description)
            context.startActivity(intent)
        }

        holder.tasksButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, TasksScreenActivity::class.java)
            intent.putExtra("subject_id", subject.id)  // Pass ID for Firebase queries
            intent.putExtra("subject_name", subject.subject_name)  // Pass name for display
            // Optionally pass more data, e.g., intent.putExtra("subject_description", subject.description)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = subjects.size
}