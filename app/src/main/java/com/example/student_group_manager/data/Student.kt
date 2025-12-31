package com.example.student_group_manager.data

data class Student (
    var id: String = "",
    val name: String = "",
    val classrooms: Map<String, String> = mapOf()
)