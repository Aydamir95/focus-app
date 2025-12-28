package com.focusapp.service

import com.focusapp.data.model.Friend
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class FriendsService {
    
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    
    suspend fun getFriends(): List<Friend> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        val friendsRef = database.getReference("users/$userId/friends")
        
        return try {
            val snapshot = friendsRef.get().await()
            val friendsList = mutableListOf<Friend>()
            
            snapshot.children.forEach { child ->
                val friendId = child.key ?: return@forEach
                val friendData = child.value as? Map<*, *> ?: return@forEach
                
                val friend = Friend(
                    id = friendId,
                    name = friendData["name"] as? String ?: "Unknown",
                    isFocusing = friendData["isFocusing"] as? Boolean ?: false,
                    todayFocusTime = (friendData["todayFocusTime"] as? Long) ?: 0,
                    currentStreak = (friendData["currentStreak"] as? Int) ?: 0,
                    unblockAttempts = (friendData["unblockAttempts"] as? Int) ?: 0
                )
                friendsList.add(friend)
            }
            
            friendsList
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun addFriend(friendId: String) {
        val userId = auth.currentUser?.uid ?: return
        val friendsRef = database.getReference("users/$userId/friends/$friendId")
        friendsRef.setValue(true).await()
    }
    
    suspend fun updateUserStats(
        focusTime: Long,
        isFocusing: Boolean,
        streak: Int,
        unblockAttempts: Int
    ) {
        val userId = auth.currentUser?.uid ?: return
        val userRef = database.getReference("users/$userId")
        
        userRef.updateChildren(mapOf(
            "todayFocusTime" to focusTime,
            "isFocusing" to isFocusing,
            "currentStreak" to streak,
            "unblockAttempts" to unblockAttempts,
            "lastUpdate" to System.currentTimeMillis()
        )).await()
    }
}



