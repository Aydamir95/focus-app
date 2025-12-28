package com.focusapp.data.dao

import androidx.room.*
import com.focusapp.data.model.DailyStats
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyStatsDao {
    @Query("SELECT * FROM daily_stats ORDER BY date DESC")
    fun getAllStats(): Flow<List<DailyStats>>

    @Query("SELECT * FROM daily_stats WHERE date = :date")
    suspend fun getStatsByDate(date: String): DailyStats?

    @Query("SELECT * FROM daily_stats WHERE date >= :startDate AND date <= :endDate")
    suspend fun getStatsByDateRange(startDate: String, endDate: String): List<DailyStats>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStats(stats: DailyStats)

    @Update
    suspend fun updateStats(stats: DailyStats)

    @Query("SELECT * FROM daily_stats ORDER BY date DESC LIMIT 7")
    suspend fun getWeeklyStats(): List<DailyStats>
}



