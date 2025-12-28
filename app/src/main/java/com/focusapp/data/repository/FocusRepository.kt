package com.focusapp.data.repository

import com.focusapp.data.dao.AppUsageDao
import com.focusapp.data.dao.DailyStatsDao
import com.focusapp.data.dao.FocusSessionDao
import com.focusapp.data.model.AppUsage
import com.focusapp.data.model.AppUsageSummary
import com.focusapp.data.model.DailyStats
import com.focusapp.data.model.FocusSession
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class FocusRepository(
    private val sessionDao: FocusSessionDao,
    private val appUsageDao: AppUsageDao,
    private val dailyStatsDao: DailyStatsDao
) {
    fun getAllSessions(): Flow<List<FocusSession>> = sessionDao.getAllSessions()
    
    suspend fun getActiveSession(): FocusSession? = sessionDao.getActiveSession()
    
    suspend fun startSession(
        blockingMode: String,
        selectedApps: List<String> = emptyList()
    ): Long {
        val session = FocusSession(
            startTime = System.currentTimeMillis(),
            blockingMode = blockingMode,
            selectedApps = selectedApps,
            isActive = true
        )
        return sessionDao.insertSession(session)
    }
    
    suspend fun endSession(sessionId: Long) {
        sessionDao.endSession(sessionId)
        updateDailyStats()
    }
    
    suspend fun updateSession(session: FocusSession) {
        sessionDao.updateSession(session)
    }
    
    suspend fun incrementUnblockAttempts(sessionId: Long) {
        val session = sessionDao.getSessionById(sessionId)
        session?.let {
            val updated = it.copy(unblockAttempts = it.unblockAttempts + 1)
            sessionDao.updateSession(updated)
        }
    }
    
    suspend fun updateScreenTime(sessionId: Long, screenTime: Long) {
        val session = sessionDao.getSessionById(sessionId)
        session?.let {
            val updated = it.copy(totalScreenTime = screenTime)
            sessionDao.updateSession(updated)
        }
    }
    
    suspend fun recordAppUsage(sessionId: Long, packageName: String, usageTime: Long) {
        val usage = AppUsage(
            sessionId = sessionId,
            packageName = packageName,
            usageTime = usageTime
        )
        appUsageDao.insertUsage(usage)
    }
    
    suspend fun getUsageByDateRange(startDate: Long): Map<String, Long> {
        return appUsageDao.getUsageByDateRange(startDate).associate { it.packageName to it.totalTime }
    }
    
    suspend fun getWeeklyStats(): List<DailyStats> {
        return dailyStatsDao.getWeeklyStats()
    }
    
    suspend fun getTodayStats(): DailyStats? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())
        return dailyStatsDao.getStatsByDate(today)
    }
    
    private suspend fun updateDailyStats() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        val endOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
        
        val sessions = sessionDao.getSessionsByDateRange(startOfDay, endOfDay)
        val totalFocusTime = sessions.sumOf { it.totalScreenTime }
        val totalUnblockAttempts = sessions.sumOf { it.unblockAttempts.toLong() }.toInt()
        val totalScreenTime = sessions.sumOf { it.totalScreenTime }
        
        val stats = DailyStats(
            date = today,
            totalFocusTime = totalFocusTime,
            totalUnblockAttempts = totalUnblockAttempts,
            totalScreenTime = totalScreenTime,
            sessionCount = sessions.size
        )
        
        dailyStatsDao.insertStats(stats)
    }
}



