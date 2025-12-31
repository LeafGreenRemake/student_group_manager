package com.example.student_group_manager

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.student_group_manager.R
import com.example.student_group_manager.data.Student
import com.example.student_group_manager.data.Teacher
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import kotlinx.coroutines.MainScope
import kotlin.math.sign

private lateinit var auth: FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.sign_up_screen)

        val goBackButton: Button = findViewById(R.id.go_back_button)
        val nameEditText: EditText = findViewById(R.id.name_edittext)
        val emailEditText: EditText = findViewById(R.id.email_edittext)
        val passwordEditText: EditText = findViewById(R.id.password_edittext)
        val signUpButton: Button = findViewById(R.id.sign_up_button)
        val toggleButton: ToggleButton = findViewById(R.id.toggleButton)
        auth = Firebase.auth

        var isAStudent: Boolean = true

        goBackButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isAStudent = false
            } else {
                isAStudent = true
            }
        }

        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty())
            {
                Toast.makeText(this, "אנא תמלאו את כל הפרטים", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid

                        if (isAStudent) {
                            saveStudentToDatabase(userId, name, email)
                        } else {
                            saveTeacherToDatabase(userId, name, email)
                        }
                    } else {
                        Toast.makeText(this, "Signup failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

    }


    private fun saveStudentToDatabase(userId: String?, name: String, email: String) {
        if (userId == null) {
            Toast.makeText(this, "Error: Student ID not found", Toast.LENGTH_LONG).show()
            return
        }

        val database = Firebase.database
        val studentRef = database.getReference("students").child(userId)
        val student = Student(userId, name, mapOf())

        studentRef.setValue(student)
            .addOnSuccessListener {
                Toast.makeText(this, "נרשמת בהצלחה, מוזמנים להשתמש באפליקציה!", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save user: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }


    private fun saveTeacherToDatabase(userId: String?, name: String, email: String) {
        if (userId == null) {
            Toast.makeText(this, "Error: Teacher ID not found", Toast.LENGTH_LONG).show()
            return
        }

        val database = Firebase.database
        val teacherRef = database.getReference("teachers").child(userId)
        val teacher = Teacher(name, email)

        teacherRef.setValue(teacher)
            .addOnSuccessListener {
                Toast.makeText(this, "נרשמת בהצלחה, מוזמנים להשתמש באפליקציה!", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save user: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
