package com.example.student_group_manager.data

data class Classroom(
    var id: String = "",
    val name: String = "",
    val students: Map<String, Student> = mapOf(),
    val classroomGroups: Map<String, Group> = mapOf()
)
