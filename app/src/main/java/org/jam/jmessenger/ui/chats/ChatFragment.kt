package org.jam.jmessenger.ui.chats

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.room.Room
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jam.jmessenger.data.db.entity.RoomMessage
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.databinding.ChatFragmentBinding
import org.jam.jmessenger.ui.hideKeyboard
import java.lang.ref.WeakReference

class ChatFragment: Fragment(), View.OnClickListener {
    private val TAG = "ChatFragment"

    private lateinit var viewModel: ChatViewModel
    private lateinit var bindings: ChatFragmentBinding
    private lateinit var recyclerViewAdapter: ChatRecycleViewAdapter
    private lateinit var mUnreceivedMessageListner: ListenerRegistration

    private val args: ChatFragmentArgs by navArgs()
    private val userUID: String by lazy { args.userUID }
    private val friendUID: String by lazy { args.friendUID }

    // START REGION: ViewModel Related Methods
    private fun initViewModel() {
        // Declaring Fragment ViewModel
        viewModel = ViewModelProvider(
            requireActivity(),
            ChatViewModelFactory(WeakReference(requireContext()), userUID, friendUID)
        ).get(ChatViewModel::class.java)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.friendInfo.observe(this.viewLifecycleOwner, { data: User ->
            updateUI(data)
        })
    }

    private fun updateUI(data: User) {
        bindings.chatsTextViewName.text = data.info.name
        bindings.chatsIsOnline.visibility = if (data.info.isOnline) View.VISIBLE else View.INVISIBLE
    }
    // END REGION


    // START REGION
    private fun initRecyclerView() {
        recyclerViewAdapter = ChatRecycleViewAdapter(userUID, viewModel)
        bindings.chatsRecyclerView.adapter = recyclerViewAdapter
        lifecycleScope.launch {
            viewModel.readMessages(userUID, friendUID).collectLatest { data ->
                recyclerViewAdapter.submitData(data)
            }
        }
    }
    // END REGION

    private fun sendMessage() {
        Log.i(TAG, "SENT")
        val message = bindings.chatMessageTextEdit.text.toString()

        if (message.length >= 3500) {
            Toast.makeText(requireContext(), "Max Word Length is 3500", Toast.LENGTH_SHORT).show()
        }
        if (message.isNotEmpty()) {
            viewModel.sendMessages(userUID, friendUID, message.trim())
            bindings.chatMessageTextEdit.text = null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindings = ChatFragmentBinding.inflate(inflater)
        initViewModel()
        viewModel.loadUser(userUID)
        viewModel.loadFriend(friendUID)
        viewModel.loadUserFriendProfileImage(friendUID, bindings.chatsImageViewProfile)
        initRecyclerView()
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bindings.chatsButtonSend.setOnClickListener(this)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        bindings.chatsRecyclerView.adapter = null
        viewModel.updateReadCount(userUID, friendUID)
        super.onDestroyView()
    }

    override fun onClick(v: View?) {
        when(v) {
            bindings.chatsButtonSend -> {
                sendMessage()
            }
        }
    }
}