package com.example.student_group_manager.data

data class Classroom(
    var id: String = "",
    val name: String = "",
    val students: Map<String, Boolean> = mapOf(),
    val classroomGroups: Map<Int, String> = mapOf()
)
