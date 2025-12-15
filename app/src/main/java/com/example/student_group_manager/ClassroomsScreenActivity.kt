package com.example.student_group_manager

import android.content.Intent
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.student_group_manager.Adapter.SubjectAdapter
import com.example.student_group_manager.R  // Add this import if missing
import com.example.student_group_manager.data.Subject
import com.example.student_group_manager.data.Teacher
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.student_group_manager.Adapter.ClassroomsAdapter
import com.example.student_group_manager.data.Classroom


private lateinit var auth: FirebaseAuth
private var classroomsList = mutableListOf<Classroom>()
private lateinit var adapter: ClassroomsAdapter


class ClassroomsScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.classes_screen)

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
                    Log.e("ClassroomsScreenActivity", "Teacher data not found")
                    Toast.makeText(this@ClassroomsScreenActivity, "Failed to load teacher name", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ClassroomsScreenActivity", "Database error: ${error.message}")
                Toast.makeText(this@ClassroomsScreenActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })


        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ClassroomsAdapter(classroomsList)
        recyclerView.adapter = adapter



        val subjectId = intent.getStringExtra("subject_id")
        if (subjectId.isNullOrEmpty()) {
            Toast.makeText(this, "No subject ID provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        addButton.setOnClickListener {
            AddClassroomFragment.newInstance(subjectId).show(supportFragmentManager, "add_classroom_dialog")
        }

        val classroomsRef = database.getReference("teachers").child(uid).child("subjects").child(subjectId).child("subjectClassrooms")
        classroomsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                classroomsList.clear()
                for (childSnapshot in snapshot.children) {
                    val classroom = childSnapshot.getValue(Classroom::class.java)
                    classroom?.let {
                        it.id = childSnapshot.key ?: ""
                        classroomsList.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ClassroomsScreenActivity", "Classrooms fetch error: ${error.message}")
                Toast.makeText(this@ClassroomsScreenActivity, "Failed to load classrooms", Toast.LENGTH_SHORT).show()
            }
        })
    }
}