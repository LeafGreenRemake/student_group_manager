package com.example.student_group_manager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.student_group_manager.data.Student
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

private lateinit var auth: FirebaseAuth

class ClassroomsOfStudents: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.classes_screen)

        val logoutButton: Button = findViewById(R.id.logout_button)
        auth = Firebase.auth
        val currentUser = auth.currentUser
        val nameEditText: EditText = findViewById(R.id.teacher_name)

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


        logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "יצאת מהמערכת בהצלחה", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}


