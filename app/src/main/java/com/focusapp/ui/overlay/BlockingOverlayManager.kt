package com.focusapp.ui.overlay

import android.app.Service
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.focusapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BlockingOverlayManager(private val context: Context) {
    
    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private val overlayScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var onQuitCallback: ((Boolean) -> Unit)? = null
    
    init {
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
    }
    
    fun show(blockedAppName: String, onQuit: (Boolean) -> Unit) {
        dismiss() // Remove any existing overlay
        
        onQuitCallback = onQuit
        
        val inflater = LayoutInflater.from(context)
        overlayView = inflater.inflate(R.layout.overlay_blocking, null)
        
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }
        
        overlayView?.let { view ->
            val messageText = view.findViewById<TextView>(R.id.blockingMessage)
            val timerText = view.findViewById<TextView>(R.id.blockingTimer)
            val quitButton = view.findViewById<Button>(R.id.quitButton)
            
            messageText.text = "This app is blocked during your focus session: $blockedAppName"
            
            quitButton.setOnClickListener {
                onQuitCallback?.invoke(true)
                dismiss()
            }
            
            // Start timer
            overlayScope.launch {
                var seconds = 0
                while (overlayView != null) {
                    val minutes = seconds / 60
                    val secs = seconds % 60
                    timerText.text = String.format("%02d:%02d", minutes, secs)
                    delay(1000)
                    seconds++
                }
            }
            
            windowManager?.addView(view, params)
        }
    }
    
    fun dismiss() {
        overlayView?.let { view ->
            windowManager?.removeView(view)
            overlayView = null
        }
        overlayScope.cancel()
        onQuitCallback = null
    }
}



