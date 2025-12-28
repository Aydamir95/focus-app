package com.focusapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.focusapp.data.database.FocusDatabase
import com.focusapp.data.model.FocusSession
import com.focusapp.data.repository.FocusRepository
import com.focusapp.service.AppBlockingService
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class FocusViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = FocusDatabase.getDatabase(application)
    private val repository = FocusRepository(
        database.focusSessionDao(),
        database.appUsageDao(),
        database.dailyStatsDao()
    )
    
    private val _activeSession = MutableLiveData<FocusSession?>()
    val activeSession: LiveData<FocusSession?> = _activeSession
    
    private val _screenTime = MutableLiveData<Long>(0)
    val screenTime: LiveData<Long> = _screenTime
    
    private val _unblockAttempts = MutableLiveData<Int>(0)
    val unblockAttempts: LiveData<Int> = _unblockAttempts
    
    private val _isSessionActive = MutableLiveData<Boolean>(false)
    val isSessionActive: LiveData<Boolean> = _isSessionActive
    
    init {
        loadActiveSession()
    }
    
    private fun loadActiveSession() {
        viewModelScope.launch {
            val session = repository.getActiveSession()
            _activeSession.value = session
            _isSessionActive.value = session != null
            session?.let {
                _screenTime.value = it.totalScreenTime
                _unblockAttempts.value = it.unblockAttempts
            }
        }
    }
    
    fun startSession(
        blockingMode: AppBlockingService.BlockingMode,
        selectedApps: List<String>
    ) {
        viewModelScope.launch {
            val modeString = when (blockingMode) {
                AppBlockingService.BlockingMode.FULL -> "FULL"
                AppBlockingService.BlockingMode.SELECTIVE_BLOCK -> "SELECTIVE_BLOCK"
                AppBlockingService.BlockingMode.SELECTIVE_ALLOW -> "SELECTIVE_ALLOW"
                AppBlockingService.BlockingMode.OFF -> "OFF"
            }
            
            val sessionId = repository.startSession(modeString, selectedApps)
            
            // Set blocking service
            AppBlockingService.instance?.setBlockingMode(
                blockingMode,
                selectedApps,
                sessionId,
                repository
            )
            
            loadActiveSession()
        }
    }
    
    fun stopSession() {
        viewModelScope.launch {
            val session = _activeSession.value
            session?.let {
                repository.endSession(it.id)
                AppBlockingService.instance?.clearBlocking()
                _activeSession.value = null
                _isSessionActive.value = false
                _screenTime.value = 0
                _unblockAttempts.value = 0
            }
        }
    }
    
    fun updateScreenTime(time: Long) {
        viewModelScope.launch {
            val session = _activeSession.value
            session?.let {
                repository.updateScreenTime(it.id, time)
                _screenTime.value = time
            }
        }
    }
    
    fun recordAppUsage(packageName: String, usageTime: Long) {
        viewModelScope.launch {
            val session = _activeSession.value
            session?.let {
                repository.recordAppUsage(it.id, packageName, usageTime)
            }
        }
    }
}



