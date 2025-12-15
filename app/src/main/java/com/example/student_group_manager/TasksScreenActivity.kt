package com.example.student_group_manager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.student_group_manager.Adapter.TaskAdapter
import com.example.student_group_manager.data.Task
import com.example.student_group_manager.data.Teacher
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database


private lateinit var auth: FirebaseAuth
private var tasksList = mutableListOf<Task>()
private lateinit var adapter: TaskAdapter

class TasksScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.tasks_screen)

        val logoutButton: Button = findViewById(R.id.logout_button)  // Fixed: Use logout_button, not login_button
        val addButton: Button = findViewById(R.id.add_button)
        auth = Firebase.auth
        val currentUser = auth.currentUser
        val nameEditText: EditText = findViewById(R.id.teacher_name)

        logoutButton.setOnClickListener {
            val intent = Intent(this, SubjectsActivity::class.java)
            startActivity(intent)
        }


        val database = Firebase.database
        val uid = currentUser?.uid ?: return
        val teacherRef = database.getReference("teachers").child(uid)


        teacherRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val teacher = snapshot.getValue(Teacher::class.java)
                if (teacher != null) {
                    nameEditText.setText(teacher.name)
                } else {
                    Log.e("TasksScreenActivity", "Teacher data not found")
                    Toast.makeText(this@TasksScreenActivity, "Failed to load teacher name", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TasksScreenActivity", "Database error: ${error.message}")
                Toast.makeText(this@TasksScreenActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })


        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TaskAdapter(tasksList)
        recyclerView.adapter = adapter



        val subjectId = intent.getStringExtra("subject_id")
        if (subjectId.isNullOrEmpty()) {
            Toast.makeText(this, "No subject ID provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        addButton.setOnClickListener {
            AddTaskFragment.newInstance(subjectId).show(supportFragmentManager, "add_task_dialog")
        }

        val tasksRef = database.getReference("teachers").child(uid).child("subjects").child(subjectId).child("subjectTasks")
        tasksRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tasksList.clear()
                for (childSnapshot in snapshot.children) {
                    val task = childSnapshot.getValue(Task::class.java)?.let {
                        it.id = childSnapshot.key ?: ""
                        tasksList.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TasksScreenActivity", "Tasks fetch error: ${error.message}")
                Toast.makeText(this@TasksScreenActivity, "Failed to load tasks", Toast.LENGTH_SHORT).show()
            }
        })
    }
}