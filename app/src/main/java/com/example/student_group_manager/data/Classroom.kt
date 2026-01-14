package com.example.student_group_manager.data

data class Classroom(
    var id: String = "",
    var teacherId: String = "",
    var subjectId: String = "",
    val name: String = "",
    val students: Map<String, Boolean> = mapOf(),
    val classroomGroups: Map<Int, String> = mapOf()
)
