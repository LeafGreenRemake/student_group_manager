package com.example.student_group_manager

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
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
import com.example.student_group_manager.data.ColorGroup
import com.example.student_group_manager.data.SymbolGroup

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

        val colorGroupsRef = Firebase.database
            .getReference("teachers")
            .child(teacherId)
            .child("subjects")
            .child(subjectId)
            .child("subjectClassrooms")
            .child(classroomId)
            .child("colorGroups")

        val symbolGroupsRef = Firebase.database
            .getReference("teachers")
            .child(teacherId)
            .child("subjects")
            .child(subjectId)
            .child("subjectClassrooms")
            .child(classroomId)
            .child("symbolGroups")


        groupsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("ClassGroupOfStudentActivity", "onDataChange triggered. Groups count: ${snapshot.childrenCount}")
                var found = false
                for (groupSnap in snapshot.children) {
                    val group = groupSnap.getValue(Group::class.java) ?: continue

                    if (group.groupStudent.contains(studentId)) {  // 👈 Use contains() for List
                        applyGroupUI(group)
                        Log.d("ClassGroupOfStudentActivity", "Found student in group ${group.groupNumber}")
                        found = true
                        break
                    }
                }
                if (!found) {  // 👈 Moved "not found" outside the loop
                    Log.d("ClassGroupOfStudentActivity", "The student was not found in any group")
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



        colorGroupsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("ClassGroupOfStudentActivity", "onDataChange triggered. Groups count: ${snapshot.childrenCount}")
                var found = false
                for (groupSnap in snapshot.children) {
                    val group = groupSnap.getValue(ColorGroup::class.java) ?: continue

                    if (group.groupStudent.contains(studentId)) {  // 👈 Use contains() for List
                        applyColorUI(group)
                        Log.d("ClassGroupOfStudentActivity", "Found student in a group.")
                        found = true
                        break
                    }
                }
                if (!found) {  // 👈 Moved "not found" outside the loop
                    Log.d("ClassGroupOfStudentActivity", "The student was not found in any group")
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



        symbolGroupsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("ClassGroupOfStudentActivity", "onDataChange triggered. Groups count: ${snapshot.childrenCount}")
                var found = false
                for (groupSnap in snapshot.children) {
                    val group = groupSnap.getValue(SymbolGroup::class.java) ?: continue

                    if (group.groupStudent.contains(studentId)) {  // 👈 Use contains() for List
                        applySymbolUI(group)
                        Log.d("ClassGroupOfStudentActivity", "Found student in a group.")
                        found = true
                        break
                    }
                }
                if (!found) {  // 👈 Moved "not found" outside the loop
                    Log.d("ClassGroupOfStudentActivity", "The student was not found in any group")
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
        val groupText = findViewById<TextView>(R.id.groupNumberText)

        groupText.setText("${group.groupNumber}")
    }


    private fun applyColorUI(colorGroup: ColorGroup) {
        val rootLayout = findViewById<View>(R.id.rootLayout)

        try {
            rootLayout.setBackgroundColor(colorGroup.groupColor.toColorInt())
        } catch (e: IllegalArgumentException) {
            rootLayout.setBackgroundColor(Color.WHITE)
        }
    }


    private fun applySymbolUI(symbolGroup: SymbolGroup) {
        val groupIconImageView = findViewById<ImageView>(R.id.group_icon_imageview)

        if (symbolGroup.groupImageResId != 0) {
            groupIconImageView.setImageResource(symbolGroup.groupImageResId)
        } else {
            // Fallback (e.g., hide or set a default icon)
            groupIconImageView.visibility = View.GONE
        }
    }
}