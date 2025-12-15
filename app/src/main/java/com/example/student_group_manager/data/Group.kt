package com.example.student_group_manager.data

data class Group(
    val groupSize: Int = 0,
    val groupStudent: MutableList<Student> = mutableListOf(),
    val groupTasks: MutableList<TaskInstance> = mutableListOf()
)
