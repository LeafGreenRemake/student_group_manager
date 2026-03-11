package com.example.student_group_manager.data

data class SymbolGroup (
    var id: String = "",
    var groupStudent: List<String> = listOf(),
    val groupImageResId: Int = 0
)