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

private lateinit var nameEditText: EditText
private lateinit var auth: FirebaseAuth
private var subjectsList = mutableListOf<Subject>()
private lateinit var adapter: SubjectAdapter



class SubjectsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.subjects_screen)

        auth = Firebase.auth
        val currentUser = auth.currentUser
        nameEditText = findViewById(R.id.teacher_name)
        val logoutButton: Button = findViewById(R.id.logout_button)  // Fixed: Use logout_button, not login_button
        val addButton: Button = findViewById(R.id.add_button)


        logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "יצאת מהמערכת בהצלחה", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
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
                    Log.e("SubjectsActivity", "Teacher data not found")
                    Toast.makeText(this@SubjectsActivity, "Failed to load teacher name", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SubjectsActivity", "Database error: ${error.message}")
                Toast.makeText(this@SubjectsActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })


        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SubjectAdapter(subjectsList)
        recyclerView.adapter = adapter

        addButton.setOnClickListener {
            AddSubjectFragment.newInstance().show(supportFragmentManager, "add_subject_dialog")
        }

        val subjectsRef = database.getReference("teachers").child(uid).child("subjects")
        subjectsRef.addValueEventListener(object : ValueEventListener {
            // In the subjectsRef.addValueEventListener block:
            override fun onDataChange(snapshot: DataSnapshot) {
                subjectsList.clear()
                for (childSnapshot in snapshot.children) {
                    val subject = childSnapshot.getValue(Subject::class.java)
                    subject?.let {
                        it.id = childSnapshot.key ?: ""  // Set the Firebase key as id
                        subjectsList.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SubjectsActivity", "Subjects fetch error: ${error.message}")
                Toast.makeText(this@SubjectsActivity, "Failed to load subjects", Toast.LENGTH_SHORT).show()
            }
        })
    }
}