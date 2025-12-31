package com.example.student_group_manager.data

data class Group(
    var id: String = "",
    val groupSize: Int = 0,
    val groupColor: String = "#FF000000",
    val groupStudent: Map<Int, String> = mapOf(),   // Storing Students and Tasks by their ID.
    val groupTasks: Map<Int, String> = mapOf()
)
