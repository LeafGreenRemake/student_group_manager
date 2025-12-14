package com.example.student_group_manager

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.student_group_manager.data.Teacher
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

private lateinit var nameEditText: EditText
private lateinit var auth: FirebaseAuth

class SubjectsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.subjects_screen)

        auth = Firebase.auth
        val currentUser = auth.currentUser
        nameEditText = findViewById(R.id.teacher_name)

        if (currentUser != null) {
            fetchUserName(currentUser.uid)
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show()
        }
    }


    private fun fetchUserName(uid: String) {
        val database = Firebase.database
        val userRef = database.getReference("teachers").child(uid)  // Adjust node to "users" if needed

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val teacher = snapshot.getValue(Teacher::class.java)
                if (teacher != null) {
                    val name = teacher.name
                    nameEditText.setText(name)  // Set the name in the EditText
                } else {
                    Toast.makeText(this@SubjectsActivity, "No data found for user", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SubjectsActivity, "Error fetching data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}