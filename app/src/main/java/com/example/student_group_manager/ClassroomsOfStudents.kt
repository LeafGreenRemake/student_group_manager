package com.example.student_group_manager

import android.app.AlertDialog
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
import com.example.student_group_manager.Adapter.ClassroomsOfStudentAdapter
import com.example.student_group_manager.data.Classroom
import com.example.student_group_manager.data.Student
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

private lateinit var auth: FirebaseAuth
private var classroomsList = mutableListOf<Classroom>()
private lateinit var adapter: ClassroomsOfStudentAdapter

class ClassroomsOfStudents : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.classes_screen)

        val logoutButton: Button = findViewById(R.id.logout_button)
        val addButton: Button = findViewById(R.id.add_button)  // Assume this exists in layout
        auth = Firebase.auth
        val currentUser = auth.currentUser
        val nameEditText: EditText = findViewById(R.id.teacher_name)  // Reuse ID, but for student name

        val database = Firebase.database
        val uid = currentUser?.uid ?: return
        val studentRef = database.getReference("students").child(uid)

        studentRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val student = snapshot.getValue(Student::class.java)
                if (student != null) {
                    nameEditText.setText(student.name)
                } else {
                    Log.e("ClassroomsOfStudents", "Student data not found")
                    Toast.makeText(this@ClassroomsOfStudents, "Failed to load student name", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ClassroomsOfStudents", "Database error: ${error.message}")
                Toast.makeText(this@ClassroomsOfStudents, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // Set up RecyclerView for classrooms
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)  // Assume ID in layout
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ClassroomsOfStudentAdapter(classroomsList)
        recyclerView.adapter = adapter

        loadClassrooms(uid)  // Load joined classrooms

        logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "יצאת מהמערכת בהצלחה", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        addButton.setOnClickListener {
            showEnterCodeDialog(uid)
        }
    }

    private fun loadClassrooms(studentId: String) {
        val database = Firebase.database
        val studentRef = database.getReference("students").child(studentId)

        studentRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val student = snapshot.getValue(Student::class.java)
                if (student != null) {
                    classroomsList.clear()
                    student.classrooms?.forEach { (classroomId, classroomName) ->
                        // Fetch full Classroom (assuming you store ID to name; fetch more if needed)
                        val classroom = Classroom(id = classroomId, name = classroomName)
                        classroomsList.add(classroom)
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ClassroomsOfStudents, "Failed to load classrooms: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showEnterCodeDialog(studentId: String) {
        val input = EditText(this)
        input.hint = "Enter 7-digit code"
        AlertDialog.Builder(this)
            .setTitle("Join Classroom")
            .setView(input)
            .setPositiveButton("הצטרפות") { _, _ ->
                val code = input.text.toString().trim()
                if (code.length == 7 && code.all { it.isDigit() }) {
                    joinClassroomWithCode(studentId, code)
                } else {
                    Toast.makeText(this, "Invalid code format", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("ביטול") { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun joinClassroomWithCode(studentId: String, code: String) {
        val database = Firebase.database
        val joinCodesRef = database.getReference("join_codes").child(code)

        joinCodesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val classroomPath = snapshot.value as? String
                    if (classroomPath != null) {
                        // Parse classroomId from path (assuming path ends with /classroomId)
                        val classroomId = classroomPath.substringAfterLast("/")
                        // Fetch classroom details (for name)
                        val classroomRef = database.getReference(classroomPath)
                        classroomRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(classroomSnapshot: DataSnapshot) {
                                val classroom = classroomSnapshot.getValue(Classroom::class.java)
                                if (classroom != null) {
                                    // Fetch student details
                                    val studentRef = database.getReference("students").child(studentId)
                                    studentRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(studentSnapshot: DataSnapshot) {
                                            val student = studentSnapshot.getValue(Student::class.java)
                                            if (student != null) {
                                                // Update both sides atomically
                                                val updates = mutableMapOf<String, Any>()

                                                // Add only student ID to classroom.students (as true)
                                                updates["$classroomPath/students/$studentId"] = true
                                                // Add classroom to student.classrooms (id to name)

                                                updates["students/$studentId/classrooms/$classroomId"] = classroom.name
                                                database.reference.updateChildren(updates)
                                                    .addOnSuccessListener {
                                                        Toast.makeText(this@ClassroomsOfStudents, "Joined classroom successfully!", Toast.LENGTH_SHORT).show()
                                                        // Optional: Remove code if single-use
                                                        //joinCodesRef.removeValue()
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Toast.makeText(this@ClassroomsOfStudents, "Failed to join: ${e.message}", Toast.LENGTH_SHORT).show()
                                                    }
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Toast.makeText(this@ClassroomsOfStudents, "Error fetching student: ${error.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    })
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(this@ClassroomsOfStudents, "Error fetching classroom: ${error.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                } else {
                    Toast.makeText(this@ClassroomsOfStudents, "Invalid or expired code", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ClassroomsOfStudents, "Error checking code: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}