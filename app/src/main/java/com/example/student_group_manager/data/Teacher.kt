package com.example.student_group_manager.data

data class Teacher(
    val name: String = "",
    val email: String = "",
    val subjects: Map<String, Subject>? = null
)
