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
import com.example.student_group_manager.Adapter.StudentAdapter
import com.example.student_group_manager.data.Student
import com.example.student_group_manager.data.Teacher
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlin.random.Random

private lateinit var auth: FirebaseAuth
private var studentsList = mutableListOf<Student>()
private lateinit var adapter: StudentAdapter

class StudentsScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.students_screen)

        val logoutButton: Button = findViewById(R.id.logout_button)
        val addButton: Button = findViewById(R.id.add_button)
        auth = Firebase.auth
        val currentUser = auth.currentUser
        val nameEditText: EditText = findViewById(R.id.teacher_name)

        logoutButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
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
                    Log.e("StudentsScreenActivity", "Teacher data not found")
                    Toast.makeText(this@StudentsScreenActivity, "Failed to load teacher name", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("StudentsScreenActivity", "Database error: ${error.message}")
                Toast.makeText(this@StudentsScreenActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StudentAdapter(studentsList)
        recyclerView.adapter = adapter

        val subjectId = intent.getStringExtra("subject_id")
        if (subjectId.isNullOrEmpty()) {
            Toast.makeText(this, "No subject ID provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val classroomId = intent.getStringExtra("classroom_id")
        if (classroomId.isNullOrEmpty()) {
            Toast.makeText(this, "No classroom ID provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        addButton.setOnClickListener {
            generateJoinCode(subjectId, classroomId)
        }

        val studentsRef = database.getReference("teachers").child(uid).child("subjects").child(subjectId).child("subjectClassrooms").child(classroomId).child("classroomStudents")
        studentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studentsList.clear()
                for (childSnapshot in snapshot.children) {
                    val student = childSnapshot.getValue(Student::class.java)
                    student?.let {
                        it.id = childSnapshot.key ?: ""
                        studentsList.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("StudentsScreenActivity", "Students fetch error: ${error.message}")
                Toast.makeText(this@StudentsScreenActivity, "Failed to load students", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun generateJoinCode(subjectId: String, classroomId: String) {
        val database = Firebase.database
        val joinCodesRef = database.getReference("join_codes")

        var code = Random.nextInt(1000000, 9999999).toString()  // 7-digit random

        // Check if code exists; if yes, regenerate (recursive, but low collision risk)
        joinCodesRef.child(code).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Code taken, regenerate
                    generateJoinCode(subjectId, classroomId)
                } else {
                    // Save code -> classroom path
                    val classroomPath = "teachers/${auth.uid}/subjects/$subjectId/subjectClassrooms/$classroomId"
                    joinCodesRef.child(code).setValue(classroomPath)
                        .addOnSuccessListener {
                            Toast.makeText(this@StudentsScreenActivity, "Join code generated: $code", Toast.LENGTH_LONG).show()
                            // Optional: Copy to clipboard or show dialog
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this@StudentsScreenActivity, "Failed to generate code: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@StudentsScreenActivity, "Error checking code: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}