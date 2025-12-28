package com.focusapp.ui.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.focusapp.R
import com.focusapp.ui.viewmodel.FriendsViewModel

class FriendsFragment : Fragment() {
    
    private val viewModel: FriendsViewModel by viewModels()
    private lateinit var friendsRecyclerView: RecyclerView
    private lateinit var addFriendButton: Button
    private lateinit var emptyStateText: TextView
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        friendsRecyclerView = view.findViewById(R.id.friendsRecyclerView)
        addFriendButton = view.findViewById(R.id.addFriendButton)
        emptyStateText = view.findViewById(R.id.emptyStateText)
        
        friendsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        viewModel.friends.observe(viewLifecycleOwner, Observer { friends ->
            if (friends.isEmpty()) {
                emptyStateText.visibility = View.VISIBLE
                friendsRecyclerView.visibility = View.GONE
            } else {
                emptyStateText.visibility = View.GONE
                friendsRecyclerView.visibility = View.VISIBLE
                friendsRecyclerView.adapter = FriendsAdapter(friends)
            }
        })
        
        addFriendButton.setOnClickListener {
            // TODO: Implement add friend dialog
        }
    }
}



