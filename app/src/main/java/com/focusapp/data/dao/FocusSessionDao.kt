package com.focusapp.data.dao

import androidx.room.*
import com.focusapp.data.model.FocusSession
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {
    @Query("SELECT * FROM focus_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<FocusSession>>

    @Query("SELECT * FROM focus_sessions WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveSession(): FocusSession?

    @Query("SELECT * FROM focus_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): FocusSession?

    @Query("SELECT * FROM focus_sessions WHERE startTime >= :startDate AND startTime <= :endDate")
    suspend fun getSessionsByDateRange(startDate: Long, endDate: Long): List<FocusSession>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSession): Long

    @Update
    suspend fun updateSession(session: FocusSession)

    @Query("UPDATE focus_sessions SET isActive = 0 WHERE id = :sessionId")
    suspend fun endSession(sessionId: Long)

    @Delete
    suspend fun deleteSession(session: FocusSession)
}



