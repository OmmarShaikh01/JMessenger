package org.jam.jmessenger.ui.contacts

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.jam.jmessenger.R
import org.jam.jmessenger.data.db.entity.FriendState
import org.jam.jmessenger.data.db.entity.Profile
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.databinding.FriendSearchFragmentBinding
import org.jam.jmessenger.ui.hideKeyboard


class FriendSearchFragment : Fragment(), View.OnClickListener, SearchView.OnQueryTextListener {
    private var authdUser = Firebase.auth.currentUser
    private var TAG = "FriendSearchFragment"

    private lateinit var viewModel: FriendSearchViewModel
    private lateinit var bindings: FriendSearchFragmentBinding
    private lateinit var defaultProfile: Bitmap

    // START REGION: VIewModel Related Methods
    private fun initViewModel() {
        // Declaring Fragment ViewModel
        viewModel = ViewModelProvider(
            requireActivity(),
            FriendSearchViewModelFactory(authdUser?.uid.toString())
        ).get(FriendSearchViewModel::class.java)
        observeViewModel()
    }

    private fun observeViewModel() {
        // Observers Data Changes
        viewModel.friendInfo.observe(this.viewLifecycleOwner, Observer { data: User ->
            updateFriendInfo(data)
        })
        viewModel.friendProfile.observe(this.viewLifecycleOwner, Observer { data: Profile ->
            updateFriendProfile(data)
        })
        viewModel.friendRequestState.observe(this.viewLifecycleOwner, Observer { data: FriendState ->
            raiseToastOnFriendStateChange(data)
        })
    }
    // END REGION


    // START REGION:
    private fun raiseToastOnFriendStateChange(state: FriendState) {
        when(state) {
            FriendState.OUTREQUESTED -> {
                Toast.makeText(requireContext(), "Request Sent", Toast.LENGTH_SHORT).show()
            }
            FriendState.INREQUESTED -> {
                Toast.makeText(requireContext(), "Friend Request Present", Toast.LENGTH_SHORT).show()
                // TODO: raise a dialogue to accept request
                activity?.let {
                    val builder = AlertDialog.Builder(it)
                    builder.setMessage("Unblock Friend")
                    builder.setPositiveButton(
                        "Unblock"
                    ) { dialog, id ->
                        viewModel.acceptFriend()
                    }
                    builder.setNegativeButton(
                        "Cancel"
                    ) { dialog, id -> }
                    builder.show()
                } ?: throw IllegalStateException("Activity cannot be null")
            }
            FriendState.FRIEND -> {
                Toast.makeText(requireContext(), "Already Friend", Toast.LENGTH_SHORT).show()
            }
            FriendState.BLOCKED -> {
                // TODO: raise a dialogue to unblock
                activity?.let {
                    val builder = AlertDialog.Builder(it)
                    builder.setMessage("Unblock Friend")
                    builder.setPositiveButton(
                        "Unblock"
                    ) { dialog, id ->
                        viewModel.unblockFriend()
                    }
                    builder.setNegativeButton(
                        "Cancel"
                    ) { dialog, id -> }
                    builder.show()
                } ?: throw IllegalStateException("Activity cannot be null")
            }
            FriendState.BLOCKER -> {
                Toast.makeText(requireContext(), "Fail To Add Friend", Toast.LENGTH_SHORT).show()
            }
            FriendState.ACCEPTED -> {
                Toast.makeText(requireContext(), "Added As Friend", Toast.LENGTH_SHORT).show()
            }
            FriendState.UNFRIENDED -> {
                Toast.makeText(requireContext(), "Removed Friend", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // END REGION:

    // START REGION: UI updates
    private fun updateFriendInfo(user: User) {
        bindings.friendSearchTextViewName.text = user.info.name
        bindings.friendSearchTextViewStatus.text = user.info.status
    }

    private fun updateFriendProfile(profile: Profile) {
        if (profile.data.isNotEmpty()) {
            val bmp = BitmapFactory.decodeByteArray(profile.data, 0, profile.data.size)
            bindings.friendSearchImageViewProfile.setImageBitmap(bmp)
        }
    }
    // END REGION


    // START REGION: Friends Requests
    private fun removeFriend(uid: String){
        TODO()
    }

    private fun searchFriends(email: String){
        viewModel.searchFriendEmail(email)
    }
    // END REGION


    // START REGION: UI setup
    private fun setupUI(){
        defaultProfile = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_user_default_profile)!!.toBitmap()

        // clears ui when the view is created
        bindings.friendSearchTextViewName.text = ""
        bindings.friendSearchTextViewStatus.text = ""
    }

    private fun setToolBarOptionMenu() {
        setHasOptionsMenu(true) // enables the option menu
        val searchItem = bindings.friendSearchToolBar.menu.findItem(R.id.action_searchfriend)
        val searchView = (searchItem.actionView) as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.queryHint = "Friend Email"
        searchView.setOnQueryTextListener(this)
    }
    // END REGION


    // START REGION: overrides
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        bindings = FriendSearchFragmentBinding.inflate(inflater)
        initViewModel()
        setupUI()
        setToolBarOptionMenu()
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindings.friendSearchButtonAddFriend.setOnClickListener(this)
        bindings.friendSearchButtonRemoveFriend.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!) {
            bindings.friendSearchButtonAddFriend -> {
                viewModel.addFriend()
            }
            bindings.friendSearchButtonRemoveFriend -> {
                viewModel.removeFriend()
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            if ((query != authdUser?.email.toString()) and (query.isNotEmpty())) {
                Log.i(TAG, query)
                searchFriends(query)
                hideKeyboard()
            }
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null){
            if (newText.isEmpty()) {
                bindings.friendSearchTextViewName.text = ""
                bindings.friendSearchTextViewStatus.text = ""
                bindings.friendSearchImageViewProfile.setImageBitmap(defaultProfile)
            }
        }
        return true
    }
    // END REGION
}