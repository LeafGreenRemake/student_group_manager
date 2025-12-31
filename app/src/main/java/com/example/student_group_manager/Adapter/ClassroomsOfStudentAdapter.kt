package com.example.student_group_manager.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.student_group_manager.ClassGroupOfStudentActivity
import com.example.student_group_manager.R
import com.example.student_group_manager.data.Classroom

class ClassroomsOfStudentAdapter (
    private val classrooms: MutableList<Classroom>
) : RecyclerView.Adapter<ClassroomsOfStudentAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTv: TextView = view.findViewById(R.id.classroom_name_tv)
        val groupButton: Button = view.findViewById(R.id.group_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.classroom_item_of_students, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val classroom = classrooms[position]
        holder.nameTv.text = classroom.name

        holder.groupButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ClassGroupOfStudentActivity::class.java)
            intent.putExtra("classroom_id", classroom.id)
            intent.putExtra("classroom_name", classroom.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = classrooms.size
}