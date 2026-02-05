package com.example.student_group_manager.data

data class SymbolGroup (
    var id: String = "",
    val groupStudent: List<String> = listOf(),
    val groupImageResId: Int = 0
)