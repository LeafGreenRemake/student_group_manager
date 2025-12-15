package com.example.student_group_manager.data

data class Subject(
    var id: String = "",
    val subject_name: String = "",
    val description: String = "",
    val subjectTasks: MutableList<Task> = mutableListOf(),
    val subjectClassrooms: MutableList<Classroom> = mutableListOf()
)