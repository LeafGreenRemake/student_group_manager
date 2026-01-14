package com.example.student_group_manager

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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

        groupsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (groupSnap in snapshot.children) {
                    val group = groupSnap.getValue(Group::class.java) ?: continue

                    if (group.groupStudent.containsValue(studentId)) {
                        applyGroupUI(group)
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    private fun applyGroupUI(group: Group) {
        val rootLayout = findViewById<View>(R.id.rootLayout)
        val groupText = findViewById<TextView>(R.id.groupNumberText)

        try {
            rootLayout.setBackgroundColor(Color.parseColor(group.groupColor))
        } catch (e: IllegalArgumentException) {
            rootLayout.setBackgroundColor(Color.WHITE)
        }

        groupText.text = "Group ${group.groupNumber}"
    }

}