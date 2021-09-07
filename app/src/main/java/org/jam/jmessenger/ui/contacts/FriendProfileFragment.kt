package org.jam.jmessenger.ui.contacts

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jam.jmessenger.R

class FriendProfileFragment : Fragment() {

    companion object {
        fun newInstance() = FriendProfileFragment()
    }

    private lateinit var viewModel: FriendProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.friend_profile_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FriendProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }

}