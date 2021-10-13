package org.jam.jmessenger.ui.contacts

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import org.jam.jmessenger.R
import org.jam.jmessenger.data.db.entity.FriendState
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.entity.UserFriend
import org.jam.jmessenger.data.db.repository.AuthenticationRepository
import org.jam.jmessenger.databinding.ContactsHomeFragmentBinding
import org.jam.jmessenger.ui.main.HomeFragmentDirections
import java.lang.Math.random


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
            updateRequestFABBadgeCount(data.friends, true)
        })
    }

    private fun updateRequestFABBadgeCount(friends: HashMap<String, UserFriend>, notify: Boolean = false) {
        var count = 0
        for ((k, v) in friends.iterator()) {
            if (v.state?.name.toString() == FriendState.INREQUESTED.name) count++
        }

        val builder = NotificationCompat.Builder(requireContext())
            .setSmallIcon(R.drawable.jabmessenger_appicon)
            .setContentTitle("Incoming Friend Request")
            .setContentText("There are $count friend requests")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        if (count == 0) {
            bindings.contactsHomeTextViewRequestBadge.visibility = View.INVISIBLE
            bindings.contactsHomeTextViewRequestBadge.text = count.toString()
        } else if (count > 99){
            bindings.contactsHomeTextViewRequestBadge.visibility = View.VISIBLE
            bindings.contactsHomeTextViewRequestBadge.text = ""
            if (notify) {
                notificationManager?.notify(random().toInt(), builder.build())
            }
        } else {
            bindings.contactsHomeTextViewRequestBadge.visibility = View.VISIBLE
            bindings.contactsHomeTextViewRequestBadge.text = count.toString()
            if (notify) {
                notificationManager?.notify(random().toInt(), builder.build())
            }
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
