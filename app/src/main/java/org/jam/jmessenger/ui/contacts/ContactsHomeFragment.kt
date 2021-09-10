package org.jam.jmessenger.ui.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import org.jam.jmessenger.R
import org.jam.jmessenger.databinding.ContactsHomeFragmentBinding
import org.jam.jmessenger.ui.main.HomeFragmentDirections

class ContactsHomeFragment : Fragment(), View.OnClickListener {
    private lateinit var viewModel: ContactsHomeViewModel
    private lateinit var bindings:ContactsHomeFragmentBinding
    private lateinit var parentNavController: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        bindings = ContactsHomeFragmentBinding.inflate(inflater)
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)
        bindings.contactsHomeFABAddFriends.setOnClickListener(this)
        parentNavController = Navigation.findNavController(requireActivity(), R.id.main_NavHost)
    }

    override fun onClick(v: View?) {
        when(v) {
            null -> {
                return
            }
            bindings.contactsHomeFABAddFriends -> {
                parentNavController.navigate(HomeFragmentDirections.actionHomeFragmentToFriendSearchFragment())
            }
        }
    }
}
