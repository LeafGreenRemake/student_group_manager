package com.example.student_group_manager

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database


private lateinit var auth: FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_screen)

        auth = Firebase.auth

        val navigationButton: Button = findViewById(R.id.sign_up_button)
        val loginButton: Button = findViewById(R.id.login_button)
        emailText = findViewById(R.id.email_edittext_login)
        passwordText = findViewById(R.id.password_edittext_login)

        navigationButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val email = emailText.text.toString().trim()
            val password = passwordText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Attempt login
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Login success
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            reload(currentUser)  // Navigate or handle post-login
                        }
                    } else {
                        // Login failure (e.g., wrong credentials)
                        Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload(currentUser)
        }
    }

    private fun reload(currentUser: FirebaseUser) {
        val database = Firebase.database
        val uid = currentUser.uid
        val teacherRef = database.getReference("teachers").child(uid)
        val studentRef = database.getReference("students").child(uid)

        teacherRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val intent = Intent(this@MainActivity, SubjectsActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    checkIfStudent(studentRef)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error checking user type: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkIfStudent(studentRef: DatabaseReference) {
        studentRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val intent = Intent(this@MainActivity, ClassroomsOfStudents::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@MainActivity, "User not found in database", Toast.LENGTH_SHORT).show()
                    auth.signOut()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error checking user type: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}