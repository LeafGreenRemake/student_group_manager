package com.example.student_group_manager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import com.example.student_group_manager.R

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

private lateinit var auth: FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContentView(R.layout.login_screen)

        auth = Firebase.auth

        val navigationButton: Button = findViewById(R.id.sign_up_button)
        val loginButton: Button = findViewById(R.id.login_button)

        navigationButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val intent = Intent(this, SubjectsActivity::class.java)
            startActivity(intent)
        }
    }


    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }

    fun reload() {

    }
}