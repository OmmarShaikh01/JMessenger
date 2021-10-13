package org.jam.jmessenger.ui.chats

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.room.Room
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.ktx.toObject
import org.jam.jmessenger.R
import org.jam.jmessenger.data.db.Result
import org.jam.jmessenger.data.db.entity.RoomMessage
import org.jam.jmessenger.data.db.entity.RoomUser
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.repository.AuthenticationRepository
import org.jam.jmessenger.databinding.ChatsHomeFragmentBinding
import java.lang.ref.WeakReference


class ChatsHomeFragment: Fragment(), View.OnClickListener {
    private var TAG = "ContactsHomeFragment"
    private var authdUser = AuthenticationRepository().getValidUser()!!

    private lateinit var bindings: ChatsHomeFragmentBinding
    private lateinit var viewModel: ChatsHomeViewModel
    private lateinit var parentNavController: NavController
    private lateinit var recyclerViewAdapter: ChatsRecyclerViewAdapter

    // START REGION: ViewModel Related Methods
    private fun initViewModel() {
        // Declaring Fragment ViewModel
        viewModel = ViewModelProvider(
            requireActivity(),
            ChatsHomeViewModelFactory(WeakReference(requireContext()), authdUser.uid)
        ).get(ChatsHomeViewModel::class.java)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.unreadCount.addSnapshotListener(MetadataChanges.EXCLUDE){ snap, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (snap != null && snap.exists()) {
                viewModel.receiveMessages()
            }
        }

        viewModel.userList.observe(this.viewLifecycleOwner, { data ->
            val dataHashMap = HashMap<String, RoomUser>()
            data.forEach {
                dataHashMap[it.user] = it
            }
            updateRecyclerView(dataHashMap)
        })
    }
    // END REGION


    // START REGION
    private fun initRecyclerView() {
        recyclerViewAdapter = ChatsRecyclerViewAdapter(HashMap(), viewModel, parentNavController)
        bindings.chatsHomeRecyclerView.adapter = recyclerViewAdapter
        viewModel.userList.value?.let { data ->
            val dataHashMap = HashMap<String, RoomUser>()
            data.forEach {
                dataHashMap[it.user] = it
            }
            updateRecyclerView(dataHashMap)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView(chats: HashMap<String, RoomUser>) {
        recyclerViewAdapter.updateList(chats)
        recyclerViewAdapter.notifyDataSetChanged()
    }
    // END REGION


    // START REGION
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        bindings = ChatsHomeFragmentBinding.inflate(inflater)
        initViewModel()
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentNavController = Navigation.findNavController(requireActivity(), R.id.main_NavHost)
        initRecyclerView()
    }

    override fun onClick(v: View?) { }

    override fun onDestroyView() {
        bindings.chatsHomeRecyclerView.adapter = null
        super.onDestroyView()
    }
    // END REGION
}