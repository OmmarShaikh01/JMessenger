package org.jam.jmessenger.ui.chats

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.room.Room
import org.jam.jmessenger.data.db.entity.RoomMessage
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.databinding.ChatFragmentBinding
import java.lang.ref.WeakReference

class ChatFragment: Fragment() {

    private lateinit var viewModel: ChatViewModel
    private lateinit var bindings: ChatFragmentBinding
    private lateinit var recyclerViewAdapter: ChatRecycleViewAdapter

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
        viewModel.userInfo.observe(this.viewLifecycleOwner, { data: User ->

        })
        viewModel.friendInfo.observe(this.viewLifecycleOwner, { data: User ->
            updateUI(data)
        })
        viewModel.messages.observe(this.viewLifecycleOwner, { data: List<RoomMessage> ->

        })
    }

    private fun updateUI(data: User) {
        bindings.chatsTextViewName.text = data.info.name
        bindings.chatsIsOnline.visibility = if (data.info.isOnline) View.VISIBLE else View.INVISIBLE
    }
    // END REGION


    // START REGION
    private fun initRecyclerView() {
        recyclerViewAdapter = ChatRecycleViewAdapter(userUID, viewModel, HashMap())
        bindings.chatsRecyclerView.adapter = recyclerViewAdapter
        updateRecyclerView(viewModel.emulateMessage(userUID, friendUID, 10))
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView(chats: List<RoomMessage>) {
        val dataHashMap = HashMap<String, RoomMessage>()
        chats.forEach {
            dataHashMap[it.hashString()] = it
        }
        recyclerViewAdapter.updateList(dataHashMap)
        recyclerViewAdapter.notifyDataSetChanged()
    }
    // END REGION


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

    override fun onDestroyView() {
        bindings.chatsRecyclerView.adapter = null
        super.onDestroyView()
    }
}