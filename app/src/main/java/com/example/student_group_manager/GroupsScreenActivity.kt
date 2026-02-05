package com.example.student_group_manager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.student_group_manager.data.Classroom
import com.example.student_group_manager.data.ColorGroup
import com.example.student_group_manager.data.Group
import com.example.student_group_manager.data.Student
import com.example.student_group_manager.data.SymbolGroup
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

            // Inflate custom layout
            val customView = layoutInflater.inflate(R.layout.dialog_group_input, null)
            val input = customView.findViewById<EditText>(R.id.edit_group_num)
            val toggle1 = customView.findViewById<ToggleButton>(R.id.toggle1)
            val toggle2 = customView.findViewById<ToggleButton>(R.id.toggle2)

            AlertDialog.Builder(this)
                .setTitle("אפשרויות יצירת קבוצות")
                .setView(customView)  // Use the custom view instead of just the EditText
                .setPositiveButton("OK") { _, _ ->
                    val groupNumStr = input.text.toString()
                    if (groupNumStr.isEmpty()) {
                        Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    val groupNum = groupNumStr.toIntOrNull()
                    if (groupNum == null || groupNum <= 0) {
                        Toast.makeText(this, "Group number must be positive", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    // Access toggle states here
                    val isToggle1Enabled = toggle1.isChecked
                    val isToggle2Enabled = toggle2.isChecked

                    // Example: Conditionally shuffle based on toggle1
                    if (isToggle1Enabled) {
                        //studentsList.shuffle()
                    } else {
                        // Optionally sort or handle differently, e.g., by name
                        //studentsList.sortBy { it.name }  // Assuming Student has a 'name' property
                    }

                    // Example: Allow uneven groups if toggle2 is enabled
                    if (!isToggle2Enabled && studentsList.size % groupNum != 0) {
                        Toast.makeText(this, "Group number must divide total students (${studentsList.size}) evenly", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    // Proceed with group creation (rest of your code remains the same)
                    val groupSize = studentsList.size / groupNum
                    val classroomRef = database.getReference("teachers").child(uid).child("subjects").child(subjectId).child("subjectClassrooms").child(classroomId)
                    classroomRef.get().addOnSuccessListener { classroomSnapshot ->
                        val classroom = classroomSnapshot.getValue(Classroom::class.java) ?: return@addOnSuccessListener
                        val existingGroups = classroom.classroomGroups.toMutableMap()

                        val groupsRef = classroomRef.child("groups")
                        val colorGroupsRef = classroomRef.child("colorGroups")
                        val symbolGroupsRef = classroomRef.child("symbolGroups")

                        val groupIcons = listOf(
                            R.drawable.outline_cookie_24,
                            R.drawable.outline_cruelty_free_24,
                            R.drawable.outline_dark_mode_24,
                            R.drawable.outline_deceased_24,
                            R.drawable.outline_light_mode_24
                        )

                        for (i in 0 until groupNum) {
                            val groupStudents = mutableListOf<String>()
                            val startIndex = i * groupSize
                            val endIndex = if (i == groupNum - 1) studentsList.size else startIndex + groupSize
                            for (j in startIndex until endIndex) {
                                groupStudents.add(studentsList[j].id)
                            }

                            val randomIcon = groupIcons[Random.nextInt(groupIcons.size)]

                            val newGroup = Group(
                                id = "",
                                groupNumber = i + 1,
                                groupSize = groupStudents.size,  // Use actual size to handle uneven
                                groupStudent = groupStudents
                            )

                            val newColorGroup = ColorGroup(
                                id = "",
                                groupColor = String.format("#%06X", Random.nextInt(0xFFFFFF + 1)),
                                groupStudent = groupStudents
                            )

                            val newSymbolGroup = SymbolGroup(
                                id = "",
                                groupImageResId = randomIcon,
                                groupStudent = groupStudents
                            )

                            val newGroupRef = groupsRef.push()
                            newGroup.id = newGroupRef.key ?: continue
                            newGroupRef.setValue(newGroup)

                            val newColorGroupRef = colorGroupsRef.push()
                            newColorGroup.id = newColorGroupRef.key ?: continue
                            newColorGroupRef.setValue(newColorGroup)

                            val newSymbolGroupRef = symbolGroupsRef.push()
                            newSymbolGroup.id = newSymbolGroupRef.key ?: continue
                            newSymbolGroupRef.setValue(newSymbolGroup)
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