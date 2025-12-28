package com.focusapp.data.dao

import androidx.room.*
import com.focusapp.data.model.AppUsage
import com.focusapp.data.model.AppUsageSummary
import kotlinx.coroutines.flow.Flow

@Dao
interface AppUsageDao {
    @Query("SELECT * FROM app_usage WHERE sessionId = :sessionId")
    fun getUsageBySession(sessionId: Long): Flow<List<AppUsage>>

    @Query("SELECT packageName, SUM(usageTime) as totalTime FROM app_usage WHERE date >= :startDate GROUP BY packageName ORDER BY totalTime DESC")
    suspend fun getUsageByDateRange(startDate: Long): List<AppUsageSummary>

    @Query("SELECT * FROM app_usage WHERE date = :date")
    suspend fun getUsageByDate(date: Long): List<AppUsage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsage(usage: AppUsage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllUsage(usage: List<AppUsage>)

    @Query("DELETE FROM app_usage WHERE sessionId = :sessionId")
    suspend fun deleteUsageBySession(sessionId: Long)
}



