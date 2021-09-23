package org.jam.jmessenger.ui.contacts

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.jam.jmessenger.R
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.entity.UserFriend
import org.jam.jmessenger.data.db.repository.AuthenticationRepository
import org.jam.jmessenger.databinding.FriendRequestsFragmentBinding
import org.jam.jmessenger.ui.main.HomeFragmentDirections


class FriendRequestsFragment : Fragment() {
    private var TAG = "ContactsHomeFragment"
    private var authdUser = AuthenticationRepository().getValidUser()!!

    private lateinit var viewModel: FriendRequestsViewModel
    private lateinit var bindings: FriendRequestsFragmentBinding
    private lateinit var parentNavController: NavController
    private lateinit var recyclerViewAdapter: FriendRequestsRecyclerViewAdapter


    // START REGION: ViewModel Related Methods
    private fun initViewModel() {
        // Declaring Fragment ViewModel
        viewModel = ViewModelProvider(
            requireActivity(),
            FriendRequestsViewModelFactory(authdUser.uid)
        ).get(FriendRequestsViewModel::class.java)
        observeViewModel()
    }

    private fun observeViewModel() {
        // Observers Data Changes
        viewModel.userInfo.observe(this.viewLifecycleOwner, Observer { data: User ->
            updateRecyclerView(data.friends)
        })
    }
    // END REGION


    // START REGION
    private fun initRecyclerView() {
        recyclerViewAdapter = FriendRequestsRecyclerViewAdapter(HashMap(), viewModel)
        bindings.friendRequestsRecyclerView.adapter = recyclerViewAdapter
        val friends = viewModel.userInfo.value?.friends
        if (friends!= null) {
            updateRecyclerView(friends)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView(friends: HashMap<String, UserFriend>) {
        recyclerViewAdapter.updateList(friends)
        recyclerViewAdapter.notifyDataSetChanged()
    }
    // END REGION

    // START REGION: Overrides
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindings = FriendRequestsFragmentBinding.inflate(inflater)
        initViewModel()
        initRecyclerView()
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentNavController = Navigation.findNavController(requireActivity(), R.id.main_NavHost)
    }
// END REGION
}