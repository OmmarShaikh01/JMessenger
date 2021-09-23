package org.jam.jmessenger.ui.contacts

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.jam.jmessenger.R
import org.jam.jmessenger.data.db.entity.FriendState
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.entity.UserFriend
import org.jam.jmessenger.data.db.repository.AuthenticationRepository
import org.jam.jmessenger.databinding.ContactsHomeFragmentBinding
import org.jam.jmessenger.ui.main.HomeFragmentDirections


class ContactsHomeFragment :  Fragment(), View.OnClickListener {
    private var TAG = "ContactsHomeFragment"
    private var authdUser = AuthenticationRepository().getValidUser()!!

    private lateinit var viewModel: ContactsHomeViewModel
    private lateinit var bindings:ContactsHomeFragmentBinding
    private lateinit var parentNavController: NavController
    private lateinit var recyclerViewAdapter: ContactsRecyclerViewAdapter

    // START REGION: ViewModel Related Methods
    private fun initViewModel() {
        // Declaring Fragment ViewModel
        viewModel = ViewModelProvider(
            requireActivity(),
            ContactsHomeViewModelFactory(authdUser.uid)
        ).get(ContactsHomeViewModel::class.java)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.userInfo.observe(this.viewLifecycleOwner, { data: User ->
            updateRecyclerView(data.friends)
            updateRequestFABBadgeCount(data.friends)
        })
    }

    private fun updateRequestFABBadgeCount(friends: HashMap<String, UserFriend>) {
        var count = 0
        for ((k, v) in friends.iterator()) {
            if (v.state?.name.toString() == FriendState.INREQUESTED.name) count++
        }

        if (count == 0) {
            bindings.contactsHomeTextViewRequestBadge.visibility = View.INVISIBLE
            bindings.contactsHomeTextViewRequestBadge.text = count.toString()
        } else if (count > 99){
            bindings.contactsHomeTextViewRequestBadge.visibility = View.VISIBLE
            bindings.contactsHomeTextViewRequestBadge.text = ""
        } else {
            bindings.contactsHomeTextViewRequestBadge.visibility = View.VISIBLE
            bindings.contactsHomeTextViewRequestBadge.text = count.toString()
        }
    }
    // END REGION


    // START REGION
    private fun initRecyclerView() {
        recyclerViewAdapter = ContactsRecyclerViewAdapter(HashMap(), viewModel, parentNavController)
        bindings.contactsHomeRecyclerView.adapter = recyclerViewAdapter
        val userFriends = viewModel.userInfo.value
        if (userFriends!= null) {
            updateRecyclerView(userFriends.friends)
            updateRequestFABBadgeCount(userFriends.friends)
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
        bindings = ContactsHomeFragmentBinding.inflate(inflater)
        initViewModel()
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindings.contactsHomeFABAddFriends.setOnClickListener(this)
        bindings.contactsHomeFABFriendRequests.setOnClickListener(this)
        parentNavController = Navigation.findNavController(requireActivity(), R.id.main_NavHost)
        initRecyclerView()
    }

    override fun onClick(v: View?) {
        when(v!!) {
            bindings.contactsHomeFABAddFriends -> {
                parentNavController.navigate(HomeFragmentDirections.actionHomeFragmentToFriendSearchFragment())
            }
            bindings.contactsHomeFABFriendRequests -> {
                parentNavController.navigate(HomeFragmentDirections.actionHomeFragmentToFriendRequestsFragment())
            }
        }
    }

    override fun onDestroyView() {
        bindings.contactsHomeRecyclerView.adapter = null
        super.onDestroyView()
    }
    // END REGION
}
