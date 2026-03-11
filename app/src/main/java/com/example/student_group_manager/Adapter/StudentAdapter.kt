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

class StudentAdapter(private val students: MutableList<Student>) : RecyclerView.Adapter<StudentAdapter.ViewHolder>() {

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

        holder.removeButton.setOnClickListener {
            val context = holder.itemView.context

            AlertDialog.Builder(context)
                .setMessage("למחוק את התלמיד מהכיתה?")
                .setPositiveButton("כן") { _, _ ->

                }

                .setNegativeButton("לא", null)
                .show()
        }
    }

    override fun getItemCount(): Int = students.size
}