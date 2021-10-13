package org.jam.jmessenger.ui.misc

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import org.jam.jmessenger.R
import org.jam.jmessenger.data.db.entity.UserFriend
import org.jam.jmessenger.databinding.FragmentFriendInfoPopupBinding
import org.jam.jmessenger.ui.contacts.ContactsHomeViewModel
import org.jam.jmessenger.ui.main.HomeFragmentDirections
import org.jam.jmessenger.widgets.ProfileRoundImageView
import java.lang.ref.WeakReference


class FriendInfoPopupFragment(
    private var parentNavController: WeakReference<NavController>,
    private var parentViewModel: WeakReference<ContactsHomeViewModel>
    ) : DialogFragment() {

    private lateinit var bindings: FragmentFriendInfoPopupBinding
    var userFriend: UserFriend = UserFriend()

    private fun chatNavigate() {
        val user = parentViewModel.get()?.userInfo?.value?.info?.uid
        if (userFriend.uid.isNotEmpty() && user != null) {
            parentNavController.get()?.navigate(
                HomeFragmentDirections.actionHomeFragmentToChatFragment(user, userFriend.uid)
            )
            dismiss()
        }
    }

    private fun blockUser(userFriend: UserFriend) {
        if (userFriend.uid.isNotEmpty()) {
            parentViewModel.get()?.blockUser(userFriend)
            dismiss()
        }
    }
    private fun unfriendUser(userFriend: UserFriend) {
        if (userFriend.uid.isNotEmpty()) {
            parentViewModel.get()?.unfriendUser(userFriend)
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        bindings = FragmentFriendInfoPopupBinding.inflate(inflater)
        updateUI()
        bindings.friendInfoPopButtonChat.setOnClickListener { chatNavigate() }
        bindings.friendInfoPopButtonBlock.setOnClickListener { blockUser(userFriend) }
        bindings.friendInfoPopButtonUnfriend.setOnClickListener { unfriendUser(userFriend) }
        return bindings.root
    }

    private fun updateUI() {
        if (userFriend.uid.isNotEmpty()) {
            bindings.friendInfoPopTextViewName.text = userFriend.name
            parentViewModel.get()?.loadUserFriendProfileImage(userFriend.uid, bindings.friendInfoPopImageView)
        }
    }
}