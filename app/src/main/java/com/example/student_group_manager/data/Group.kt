package com.example.student_group_manager.data

data class Group(
    var id: String = "",
    val groupNumber: Int = 0,
    val groupSize: Int = 0,
    val groupColor: String = "#FFFFFF",
    val groupStudent: Map<String, String> = mapOf()
)