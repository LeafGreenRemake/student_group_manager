package com.example.student_group_manager.data

data class Classroom(
    val classroom: String = "",
    val students: MutableList<Student> = mutableListOf(),
    val classroomGroups: MutableList<Group> = mutableListOf()
)
