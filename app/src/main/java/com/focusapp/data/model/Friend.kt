package com.focusapp.data.model

data class Friend(
    val id: String,
    val name: String,
    val isFocusing: Boolean = false,
    val todayFocusTime: Long = 0,
    val currentStreak: Int = 0,
    val unblockAttempts: Int = 0
)



