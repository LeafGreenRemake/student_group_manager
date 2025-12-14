package com.example.student_group_manager.data

data class Classroom(
    val classroom: String = "",
    val students: MutableList<Student>,
    val classroomGroups: MutableList<Group>
)
