package com.example.student_group_manager

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.student_group_manager.data.Group
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import androidx.core.graphics.toColorInt

private lateinit var auth: FirebaseAuth
private lateinit var teacherId: String
private lateinit var subjectId: String
private lateinit var classroomId: String

class ClassGroupOfStudentActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.class_group_of_student)
        //enableEdgeToEdge()

        val database = Firebase.database
        auth = Firebase.auth
        teacherId = intent.getStringExtra("teacher_id") ?: return
        subjectId = intent.getStringExtra("subject_id") ?: return
        classroomId = intent.getStringExtra("classroom_id") ?: return
        val logoutButton: Button = findViewById(R.id.logout_button)


        logoutButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        val studentId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        loadGroup(studentId)
    }


    private fun loadGroup(studentId: String) {
        val groupsRef = Firebase.database
            .getReference("teachers")
            .child(teacherId)
            .child("subjects")
            .child(subjectId)
            .child("subjectClassrooms")
            .child(classroomId)
            .child("groups")

        groupsRef.addValueEventListener(object : ValueEventListener {  // 👈 Changed to addValueEventListener for real-time updates
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("ClassGroupOfStudentActivity", "onDataChange triggered. Groups count: ${snapshot.childrenCount}")
                var found = false
                for (groupSnap in snapshot.children) {
                    val group = groupSnap.getValue(Group::class.java) ?: continue

                    if (group.groupStudent.contains(studentId)) {  // 👈 Use contains() for List
                        applyGroupUI(group)
                        Log.d("ClassGroupOfStudentActivity", "Found student in group ${group.groupNumber}")
                        Toast.makeText(this@ClassGroupOfStudentActivity, "Found the student in a group", Toast.LENGTH_SHORT).show()
                        found = true
                        break
                    }
                }
                if (!found) {  // 👈 Moved "not found" outside the loop
                    Log.d("ClassGroupOfStudentActivity", "The student was not found in any group")
                    Toast.makeText(this@ClassGroupOfStudentActivity, "The student was not found in any group", Toast.LENGTH_SHORT).show()
                    // Optional: Reset UI to defaults if needed
                    val rootLayout = findViewById<View>(R.id.rootLayout)
                    rootLayout.setBackgroundColor(Color.WHITE)
                    val groupText = findViewById<TextView>(R.id.groupNumberText)
                    groupText.text = "No Group"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ClassGroupOfStudentActivity", "Database error: ${error.message}")
                Toast.makeText(this@ClassGroupOfStudentActivity, "Failed to load groups: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun applyGroupUI(group: Group) {
        val rootLayout = findViewById<View>(R.id.rootLayout)
        val groupText = findViewById<TextView>(R.id.groupNumberText)

        try {
            rootLayout.setBackgroundColor(group.groupColor.toColorInt())
        } catch (e: IllegalArgumentException) {
            rootLayout.setBackgroundColor(Color.WHITE)
        }

        groupText.setText("Group ${group.groupNumber}")
    }

}