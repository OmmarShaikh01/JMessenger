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
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.jam.jmessenger.R
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.entity.UserFriend
import org.jam.jmessenger.data.db.repository.AuthenticationRepository
import org.jam.jmessenger.databinding.ChatsHomeFragmentBinding
import org.jam.jmessenger.ui.main.HomeFragmentDirections
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
        viewModel.userInfo.observe(this.viewLifecycleOwner, { data: User ->
            updateRecyclerView(data.friends)
        })
    }
    // END REGION


    // START REGION
    private fun initRecyclerView() {
        recyclerViewAdapter = ChatsRecyclerViewAdapter(HashMap(), viewModel, parentNavController)
        bindings.chatsHomeRecyclerView.adapter = recyclerViewAdapter
        val userFriends = viewModel.userInfo.value
        if (userFriends!= null) {
            updateRecyclerView(userFriends.friends)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView(friends: HashMap<String, UserFriend>) {
        recyclerViewAdapter.updateList(friends)
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