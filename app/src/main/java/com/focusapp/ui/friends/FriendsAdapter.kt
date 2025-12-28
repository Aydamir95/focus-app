package com.focusapp.ui.friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.focusapp.R
import com.focusapp.data.model.Friend

class FriendsAdapter(private val friends: List<Friend>) : RecyclerView.Adapter<FriendsAdapter.FriendViewHolder>() {
    
    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.friendName)
        val statusText: TextView = itemView.findViewById(R.id.friendStatus)
        val focusTimeText: TextView = itemView.findViewById(R.id.friendFocusTime)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend, parent, false)
        return FriendViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friends[position]
        holder.nameText.text = friend.name
        holder.statusText.text = if (friend.isFocusing) "Currently Focusing" else "Not Focusing"
        holder.focusTimeText.text = "Today: ${formatTime(friend.todayFocusTime)}"
    }
    
    override fun getItemCount() = friends.size
    
    private fun formatTime(millis: Long): String {
        val hours = millis / 3600000
        val minutes = (millis % 3600000) / 60000
        return "${hours}h ${minutes}m"
    }
}



