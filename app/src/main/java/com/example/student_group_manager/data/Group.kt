package com.example.student_group_manager.data

data class Group(
    var id: String = "",
    val groupSize: Int = 0,
    val groupColor: String = "#FF000000",
    val groupStudent: Map<String, String> = mapOf(),  // Changed to String keys
    val groupTasks: Map<String, String> = mapOf()     // Changed to String keys if needed
)
