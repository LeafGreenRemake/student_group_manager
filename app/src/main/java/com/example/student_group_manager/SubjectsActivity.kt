package com.example.student_group_manager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.student_group_manager.R  // Add this import if missing
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
        val logoutButton: Button = findViewById(R.id.logout_button)  // Fixed: Use logout_button, not login_button

        logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "יצאת מהמערכת בהצלחה", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}