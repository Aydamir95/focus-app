package com.focusapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: Long,
    val endTime: Long? = null,
    val totalScreenTime: Long = 0, // in milliseconds
    val unblockAttempts: Int = 0,
    val blockingMode: String, // FULL, SELECTIVE_BLOCK, SELECTIVE_ALLOW, OFF
    val selectedApps: List<String> = emptyList(), // package names - stored as comma-separated string
    val isActive: Boolean = true
)

@Entity(tableName = "app_usage")
data class AppUsage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val packageName: String,
    val usageTime: Long, // in milliseconds
    val date: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_stats")
data class DailyStats(
    @PrimaryKey
    val date: String, // YYYY-MM-DD format
    val totalFocusTime: Long = 0, // in milliseconds
    val totalUnblockAttempts: Int = 0,
    val totalScreenTime: Long = 0,
    val sessionCount: Int = 0
)



