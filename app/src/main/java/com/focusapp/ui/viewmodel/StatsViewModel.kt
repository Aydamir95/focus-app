package com.focusapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.focusapp.data.database.FocusDatabase
import com.focusapp.data.model.DailyStats
import com.focusapp.data.repository.FocusRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class StatsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = FocusRepository(
        FocusDatabase.getDatabase(application).focusSessionDao(),
        FocusDatabase.getDatabase(application).appUsageDao(),
        FocusDatabase.getDatabase(application).dailyStatsDao()
    )
    
    private val _todayStats = MutableLiveData<DailyStats?>()
    val todayStats: LiveData<DailyStats?> = _todayStats
    
    private val _currentStreak = MutableLiveData<Int>(0)
    val currentStreak: LiveData<Int> = _currentStreak
    
    init {
        loadStats()
    }
    
    private fun loadStats() {
        viewModelScope.launch {
            _todayStats.value = repository.getTodayStats()
            calculateStreak()
        }
    }
    
    private suspend fun calculateStreak() {
        val weeklyStats = repository.getWeeklyStats()
        var streak = 0
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        for (i in weeklyStats.indices) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val dateStr = dateFormat.format(calendar.time)
            
            val stats = weeklyStats.find { it.date == dateStr }
            if (stats != null && stats.totalFocusTime > 0) {
                streak++
            } else {
                break
            }
        }
        
        _currentStreak.value = streak
    }
}



