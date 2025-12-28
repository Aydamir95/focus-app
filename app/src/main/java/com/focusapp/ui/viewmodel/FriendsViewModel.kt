package com.focusapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.focusapp.data.model.Friend
import com.focusapp.service.FriendsService
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class FriendsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val friendsService = FriendsService()
    
    private val _friends = MutableLiveData<List<Friend>>(emptyList())
    val friends: LiveData<List<Friend>> = _friends
    
    init {
        startPeriodicUpdates()
    }
    
    private fun startPeriodicUpdates() {
        viewModelScope.launch {
            while (true) {
                loadFriends()
                delay(5 * 60 * 1000) // Update every 5 minutes
            }
        }
    }
    
    private suspend fun loadFriends() {
        val friendsList = friendsService.getFriends()
        _friends.value = friendsList
    }
    
    fun addFriend(friendId: String) {
        viewModelScope.launch {
            friendsService.addFriend(friendId)
            loadFriends()
        }
    }
}



