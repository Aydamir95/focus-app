package com.focusapp.ui.focus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.focusapp.R
import com.focusapp.service.AppBlockingService
import com.focusapp.ui.viewmodel.FocusViewModel
import java.text.SimpleDateFormat
import java.util.*

class FocusFragment : Fragment() {
    
    private val viewModel: FocusViewModel by viewModels()
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var timerText: TextView
    private lateinit var screenTimeText: TextView
    private lateinit var unblockAttemptsText: TextView
    private lateinit var modeRadioGroup: RadioGroup
    
    private var timerRunnable: Runnable? = null
    private var startTime: Long = 0
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_focus, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        startButton = view.findViewById(R.id.startButton)
        stopButton = view.findViewById(R.id.stopButton)
        timerText = view.findViewById(R.id.timerText)
        screenTimeText = view.findViewById(R.id.screenTimeText)
        unblockAttemptsText = view.findViewById(R.id.unblockAttemptsText)
        modeRadioGroup = view.findViewById(R.id.modeRadioGroup)
        
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupObservers() {
        viewModel.isSessionActive.observe(viewLifecycleOwner, Observer { isActive ->
            startButton.isEnabled = !isActive
            stopButton.isEnabled = isActive
            modeRadioGroup.isEnabled = !isActive
            
            if (isActive) {
                startTimer()
            } else {
                stopTimer()
            }
        })
        
        viewModel.screenTime.observe(viewLifecycleOwner, Observer { time ->
            screenTimeText.text = formatTime(time)
        })
        
        viewModel.unblockAttempts.observe(viewLifecycleOwner, Observer { attempts ->
            unblockAttemptsText.text = "Unblock Attempts: $attempts"
        })
    }
    
    private fun setupClickListeners() {
        startButton.setOnClickListener {
            val selectedMode = when (modeRadioGroup.checkedRadioButtonId) {
                R.id.radioFull -> AppBlockingService.BlockingMode.FULL
                R.id.radioSelectiveBlock -> AppBlockingService.BlockingMode.SELECTIVE_BLOCK
                R.id.radioSelectiveAllow -> AppBlockingService.BlockingMode.SELECTIVE_ALLOW
                R.id.radioOff -> AppBlockingService.BlockingMode.OFF
                else -> AppBlockingService.BlockingMode.OFF
            }
            
            // For now, empty list - can be extended to select apps
            viewModel.startSession(selectedMode, emptyList())
            startTime = System.currentTimeMillis()
        }
        
        stopButton.setOnClickListener {
            viewModel.stopSession()
        }
    }
    
    private fun startTimer() {
        timerRunnable = object : Runnable {
            override fun run() {
                val elapsed = System.currentTimeMillis() - startTime
                timerText.text = formatTime(elapsed)
                viewModel.updateScreenTime(elapsed)
                timerText.postDelayed(this, 1000)
            }
        }
        timerText.post(timerRunnable!!)
    }
    
    private fun stopTimer() {
        timerRunnable?.let {
            timerText.removeCallbacks(it)
        }
        timerRunnable = null
        timerText.text = "00:00:00"
    }
    
    private fun formatTime(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / 60000) % 60
        val hours = millis / 3600000
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        stopTimer()
    }
}



