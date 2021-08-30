package org.jam.jmessenger.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.jam.jmessenger.R
import org.jam.jmessenger.databinding.ContactsHomeFragmentBinding
import org.jam.jmessenger.databinding.HomeFragmentBinding
import org.jam.jmessenger.ui.chats.ChatsHomeFragment
import org.jam.jmessenger.ui.contacts.ContactsHomeFragment
import org.jam.jmessenger.ui.groups.GroupsHomeFragment


/**
 * Home fragment, home fragment for the application
 *
 * @constructor Create empty Home fragment
 */
class HomeFragment : Fragment() {
    private lateinit var bindings: HomeFragmentBinding
    private lateinit var navController: NavController
    private lateinit var viewpager: ViewPager2


    /**
     * Sets tool bar option menu and implements the listener for item click events
     *
     */
    private fun setToolBarOptionMenu() {
        setHasOptionsMenu(true) // enables the option menu
        bindings.homeToolbar.inflateMenu(R.menu.menu_home)

        // item click handler
        bindings.homeToolbar.setOnMenuItemClickListener{ item ->
            var menuclick = false
            when(item.toString()) {
                "Logout" -> {
                    Firebase.auth.signOut()
                    // TODO: Update Last seen
                    menuclick = true
                    navController.navigate(HomeFragmentDirections.actionHomeFragmentToNavUserAuth())
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
}


class HomeFragmentAdapter(fragment: Fragment): FragmentStateAdapter(fragment){

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                ChatsHomeFragment()
            }
            1 -> {
                GroupsHomeFragment()
            }
            2 -> {
                ContactsHomeFragment()
            }
            else -> {
                throw IndexOutOfBoundsException()
            }
        }
    }
}

