package com.focusapp.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.focusapp.R
import com.focusapp.ui.viewmodel.StatsViewModel
import java.text.SimpleDateFormat
import java.util.*

class StatsFragment : Fragment() {
    
    private val viewModel: StatsViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val todayFocusTime = view.findViewById<TextView>(R.id.todayFocusTime)
        val todayUnblockAttempts = view.findViewById<TextView>(R.id.todayUnblockAttempts)
        val currentStreak = view.findViewById<TextView>(R.id.currentStreak)
        
        viewModel.todayStats.observe(viewLifecycleOwner, Observer { stats ->
            stats?.let {
                todayFocusTime.text = "Today's Focus Time: ${formatTime(it.totalFocusTime)}"
                todayUnblockAttempts.text = "Unblock Attempts: ${it.totalUnblockAttempts}"
            }
        })
        
        viewModel.currentStreak.observe(viewLifecycleOwner, Observer { streak ->
            currentStreak.text = "Current Streak: $streak days"
        })
    }
    
    private fun formatTime(millis: Long): String {
        val hours = millis / 3600000
        val minutes = (millis % 3600000) / 60000
        return "${hours}h ${minutes}m"
    }
}



