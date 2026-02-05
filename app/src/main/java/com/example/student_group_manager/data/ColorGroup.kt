package com.example.student_group_manager.data

data class ColorGroup(
    var id: String = "",
    val groupColor: String = "#FFFFFF",
    var groupStudent: List<String> = listOf()
)
