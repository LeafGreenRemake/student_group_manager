package com.example.student_group_manager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.student_group_manager.data.Classroom
import com.example.student_group_manager.data.Group
import com.example.student_group_manager.data.Student
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

class GroupsScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.student_group_screen)

        val logoutButton: Button = findViewById(R.id.logout_button)
        val addButton: Button = findViewById(R.id.add_button)
        auth = Firebase.auth
        val currentUser = auth.currentUser

        logoutButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
975        }

        val database = Firebase.database
        val uid = currentUser?.uid ?: return
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

        // Load students (similar to StudentsScreenActivity)
        val studentsRef = database.getReference("teachers").child(uid).child("subjects").child(subjectId).child("subjectClassrooms").child(classroomId).child("students")
        studentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studentsList.clear()
                if (!snapshot.exists()) {
                    Log.d("GroupsScreenActivity", "No students found for classroom")
                    return
                }
                for (childSnapshot in snapshot.children) {
                    val studentId = childSnapshot.key ?: continue
                    database.getReference("students").child(studentId).get()
                        .addOnSuccessListener { studentSnapshot ->
                            val student = studentSnapshot.getValue(Student::class.java)
                            if (student != null) {
                                student.id = studentId
                                studentsList.add(student)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("GroupsScreenActivity", "Failed to fetch student $studentId: ${e.message}")
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("GroupsScreenActivity", "Students fetch error: ${error.message}")
                Toast.makeText(this@GroupsScreenActivity, "Failed to load students", Toast.LENGTH_SHORT).show()
            }
        })

        addButton.setOnClickListener {
            if (studentsList.isEmpty()) {
                Toast.makeText(this, "No students in classroom", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show dialog to input group_num
            val input = android.widget.EditText(this)
            input.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            AlertDialog.Builder(this)
                .setTitle("Enter Number of Groups")
                .setView(input)
                .setPositiveButton("OK") { _, _ ->
                    val groupNumStr = input.text.toString()
                    if (groupNumStr.isEmpty()) {
                        Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    val groupNum = groupNumStr.toIntOrNull()
                    if (groupNum == null || groupNum <= 0 || studentsList.size % groupNum != 0) {
                        Toast.makeText(this, "Group number must divide total students (${studentsList.size}) evenly", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    // Randomly shuffle students and divide into groups of equal size
                    studentsList.shuffle()
                    val groupSize = studentsList.size / groupNum
                    val classroomRef = database.getReference("teachers").child(uid).child("subjects").child(subjectId).child("subjectClassrooms").child(classroomId)
                    classroomRef.get().addOnSuccessListener { classroomSnapshot ->
                        val classroom = classroomSnapshot.getValue(Classroom::class.java) ?: return@addOnSuccessListener
                        val existingGroups = classroom.classroomGroups.toMutableMap()
                        val groupsRef = classroomRef.child("groups")

                        val groupIcons = listOf(
                            R.drawable.outline_cookie_24,
                            R.drawable.outline_cruelty_free_24,
                            R.drawable.outline_dark_mode_24,
                            R.drawable.outline_deceased_24,
                            R.drawable.outline_light_mode_24
                        )

                        for (i in 0 until groupNum) {

                            val groupStudents = mutableListOf<String>()  // 👈 Changed to MutableList<String>

                            for (j in 0 until groupSize) {
                                val studentIndex = i * groupSize + j
                                groupStudents.add(studentsList[studentIndex].id)  // 👈 Add directly to list
                            }

                            val randomIcon = groupIcons[Random.nextInt(groupIcons.size)]

                            val newGroup = Group(
                                id = "",
                                groupNumber = i + 1,
                                groupSize = groupSize,
                                groupColor = String.format("#%06X", Random.nextInt(0xFFFFFF + 1)),
                                groupStudent = groupStudents,
                                groupImageResId = randomIcon
                            )

                            val newGroupRef = groupsRef.push()
                            newGroup.id = newGroupRef.key ?: continue
                            newGroupRef.setValue(newGroup)
                        }


                        // Update classroom with new groups map
                        classroomRef.child("classroomGroups").setValue(existingGroups)
                            .addOnSuccessListener {
                                Toast.makeText(this, "$groupNum groups created successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to update classroom: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}