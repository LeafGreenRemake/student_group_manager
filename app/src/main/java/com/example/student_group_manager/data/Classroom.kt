package com.example.student_group_manager.data

data class Classroom(
    var id: String = "",
    val name: String = "",
    val students: MutableList<Student> = mutableListOf(),
    val classroomGroups: MutableList<Group> = mutableListOf()
)
