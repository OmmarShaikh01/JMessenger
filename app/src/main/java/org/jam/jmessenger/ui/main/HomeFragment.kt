package org.jam.jmessenger.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jam.jmessenger.R
import org.jam.jmessenger.data.db.Result
import org.jam.jmessenger.data.db.entity.RoomUser
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.repository.AuthenticationRepository
import org.jam.jmessenger.data.db.repository.ChatsRepository
import org.jam.jmessenger.data.db.repository.DatabaseRepository
import org.jam.jmessenger.databinding.HomeFragmentBinding
import org.jam.jmessenger.ui.chats.ChatsHomeFragment
import org.jam.jmessenger.ui.chats.ChatsHomeViewModel
import org.jam.jmessenger.ui.chats.ChatsHomeViewModelFactory
import org.jam.jmessenger.ui.contacts.ContactsHomeFragment
import org.jam.jmessenger.ui.groups.GroupsHomeFragment
import org.jam.jmessenger.ui.unregisterUser
import java.lang.ref.WeakReference


/**
 * Home fragment, home fragment for the application
 *
 * @constructor Create empty Home fragment
 */
class HomeFragment : Fragment() {
    private lateinit var bindings: HomeFragmentBinding
    private lateinit var navController: NavController
    private lateinit var viewpager: ViewPager2
    private lateinit var viewModel: HomeViewModel
    private var TAG = "HomeFragment"

    private var authRepository = AuthenticationRepository()


    // START REGION: ViewModel Related Methods
    private fun initViewModel() {
        // Declaring Fragment ViewModel
        viewModel = ViewModelProvider(
            requireActivity(),
            HomeViewModelFactory(Firebase.auth.currentUser!!.uid, WeakReference(requireContext()))
        ).get(HomeViewModel::class.java)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.unreadCount.observe(this.viewLifecycleOwner, { data: Int ->
            viewModel.notifyNewMessage(data)
        })
    }
    // END REGION

    /**
     * Sets tool bar option menu and implements the listener for item click events
     *
     */
    private fun setToolBarOptionMenu() {
        setHasOptionsMenu(true) // enables the option menu
        bindings.homeToolbar.inflateMenu(R.menu.menu_home)

        // item click handler
        bindings.homeToolbar.setOnMenuItemClickListener { item ->
            var menuclick = false
            when(item.toString()) {
                "Logout" -> {
                    menuclick = true
                    navController.navigate(HomeFragmentDirections.actionHomeFragmentToNavUserAuth())
                    authRepository.getValidUser()?.uid?.let {
                        viewModel.databaseRepository.updateUserLastSeen(it)
                    }
                    authRepository.signout()
                    unregisterUser()
                }
                "Settings" -> {
                    menuclick = true
                    navController.navigate(HomeFragmentDirections.actionHomeFragmentToSettingsFragment())
                }
                "Profile" -> {
                    menuclick = true
                    navController.navigate(HomeFragmentDirections.actionHomeFragmentToUserProfileFragment())
                }
            }
            menuclick
        }
    }

    /**
     * On create view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return ConstraintLayout
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        bindings = HomeFragmentBinding.inflate(inflater)
        viewpager = bindings.homeViewPager2
        initViewModel()
        authRepository.getValidUser()?.uid?.let {
            viewModel.databaseRepository.updateUserOnlineStatus(it, true)
        }

        // Sets main homepage action menu
        setToolBarOptionMenu()
        return bindings.root
    }

    /**
     * On view created
     *
     * @param view
     * @param savedInstanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        viewpager.adapter = HomeFragmentAdapter(this)
        TabLayoutMediator(bindings.homeTabLayout, viewpager){ tab, pos ->
            when (pos) {
                0 -> {
                    tab.text = "Chats"
                }
                1 -> {
                    tab.text = "Groups"
                }
                2 -> {
                    tab.text = "Contacts"
                }
            }
        }.attach()
    }

    override fun onDestroyView() { // TODO: LOGOUT BUG
        super.onDestroyView()
    }
}


class HomeFragmentAdapter(fragment: Fragment): FragmentStateAdapter(fragment){

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                return ChatsHomeFragment()
            }
            1 -> {
                return GroupsHomeFragment()
            }
            2 -> {
                return ContactsHomeFragment()
            }
            else -> {
                throw IndexOutOfBoundsException()
            }
        }
    }
}

