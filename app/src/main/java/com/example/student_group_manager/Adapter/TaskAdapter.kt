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
import com.example.student_group_manager.data.Task


class TaskAdapter(private val tasks: MutableList<Task>) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTv: TextView = view.findViewById(R.id.task_name_tv)
        val descTv: TextView = view.findViewById(R.id.description_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.taks_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]
        holder.nameTv.text = task.name
        holder.descTv.text = task.description
    }

    override fun getItemCount(): Int = tasks.size
}