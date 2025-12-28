package com.focusapp.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.focusapp.data.repository.FocusRepository
import com.focusapp.ui.overlay.BlockingOverlayManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AppBlockingService : AccessibilityService() {
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var repository: FocusRepository? = null
    private var overlayManager: BlockingOverlayManager? = null
    
    companion object {
        var instance: AppBlockingService? = null
        private var blockingMode: BlockingMode = BlockingMode.OFF
        private var selectedApps: List<String> = emptyList()
        private var currentSessionId: Long? = null
        
        enum class BlockingMode {
            FULL,           // Block all apps except system/phone/our app
            SELECTIVE_BLOCK, // Block only selected 3 apps
            SELECTIVE_ALLOW, // Block everything except selected 3 apps
            OFF             // No blocking, tracking only
        }
    }
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        overlayManager = BlockingOverlayManager(applicationContext)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        instance = null
        overlayManager?.dismiss()
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return
            
            if (shouldBlockApp(packageName)) {
                serviceScope.launch {
                    blockApp(packageName)
                }
            }
        }
    }
    
    override fun onInterrupt() {}
    
    private fun shouldBlockApp(packageName: String): Boolean {
        // Always allow system apps
        if (isSystemApp(packageName)) return false
        
        // Always allow our app
        if (packageName == applicationContext.packageName) return false
        
        // Always allow phone, emergency, settings
        if (isAlwaysAllowed(packageName)) return false
        
        return when (blockingMode) {
            BlockingMode.FULL -> true
            BlockingMode.SELECTIVE_BLOCK -> selectedApps.contains(packageName)
            BlockingMode.SELECTIVE_ALLOW -> !selectedApps.contains(packageName)
            BlockingMode.OFF -> false
        }
    }
    
    private fun isSystemApp(packageName: String): Boolean {
        return packageName.startsWith("com.android.") ||
               packageName.startsWith("android.") ||
               packageName == "com.google.android.gms" ||
               packageName == "com.google.android.apps.nexuslauncher"
    }
    
    private fun isAlwaysAllowed(packageName: String): Boolean {
        val allowedApps = listOf(
            "com.android.phone",
            "com.android.dialer",
            "com.android.contacts",
            "com.android.settings",
            "com.android.server.telecom",
            "com.samsung.android.dialer",
            "com.samsung.android.contacts"
        )
        return allowedApps.contains(packageName)
    }
    
    private suspend fun blockApp(packageName: String) {
        // Show blocking overlay
        overlayManager?.show(packageName) { quitSession ->
            if (quitSession) {
                endFocusSession()
            }
        }
        
        // Go back to home screen
        performGlobalAction(GLOBAL_ACTION_BACK)
        performGlobalAction(GLOBAL_ACTION_HOME)
        
        // Increment unblock attempts if user tries to access blocked app
        currentSessionId?.let { sessionId ->
            repository?.incrementUnblockAttempts(sessionId)
        }
    }
    
    fun setBlockingMode(mode: BlockingMode, apps: List<String>, sessionId: Long, repo: FocusRepository) {
        blockingMode = mode
        selectedApps = apps
        currentSessionId = sessionId
        repository = repo
    }
    
    fun clearBlocking() {
        blockingMode = BlockingMode.OFF
        selectedApps = emptyList()
        currentSessionId = null
        overlayManager?.dismiss()
    }
    
    private suspend fun endFocusSession() {
        currentSessionId?.let { sessionId ->
            repository?.endSession(sessionId)
        }
        clearBlocking()
    }
}



