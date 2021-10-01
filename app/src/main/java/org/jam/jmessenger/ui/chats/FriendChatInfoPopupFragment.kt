package org.jam.jmessenger.ui.chats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import org.jam.jmessenger.R
import org.jam.jmessenger.data.db.Result
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.entity.UserFriend
import org.jam.jmessenger.databinding.FragmentFriendChatInfoPopupBinding
import org.jam.jmessenger.databinding.FragmentFriendInfoPopupBinding
import org.jam.jmessenger.ui.contacts.ContactsHomeViewModel
import java.lang.ref.WeakReference


class FriendChatInfoPopupFragment(
        private var parentNavController: WeakReference<NavController>,
        private var parentViewModel: WeakReference<ChatsHomeViewModel>
) : DialogFragment() {

    private lateinit var bindings: FragmentFriendChatInfoPopupBinding
    var userFriend = UserFriend()

    private fun muteUser(friend: UserFriend) {
        if (friend.uid.isNotEmpty()) {
            parentViewModel.get()?.muteUser(friend)
            dismiss()
        }
    }
    private fun blockUser(friend: UserFriend) {
        if (friend.uid.isNotEmpty()) {
            parentViewModel.get()?.blockUser(friend)
            dismiss()
        }
    }
    private fun unfriendUser(friend: UserFriend) {
        if (friend.uid.isNotEmpty()) {
            parentViewModel.get()?.unfriendUser(friend)
            dismiss()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        bindings = FragmentFriendChatInfoPopupBinding.inflate(inflater)
        bindings.friendChatInfoPopButtonMute.setOnClickListener { muteUser(userFriend) }
        bindings.friendChatInfoPopButtonBlock.setOnClickListener { blockUser(userFriend) }
        bindings.friendChatInfoPopButtonUnfriend.setOnClickListener { unfriendUser(userFriend) }
        updateUI(userFriend)
        return bindings.root
    }

    private fun updateUI(friend: UserFriend) {
        if (friend.uid.isNotEmpty()) {
            parentViewModel.get()?.loadUserFriendInfo(friend.uid) { result: Result<User> ->
                run {
                    if (result is Result.Success) result.data.let { user ->
                        userFriend = UserFriend(user.info.uid, user.info.name)
                    }
                }
            }
            parentViewModel.get()?.loadUserFriendInfo(friend.uid, bindings.friendChatInfoPopTextViewName)
            parentViewModel.get()?.loadUserFriendProfileImage(friend.uid, bindings.friendChatInfoPopImageView)
        }
    }

    fun show(manager: FragmentManager, friend: UserFriend) {
        userFriend = friend
        super.show(manager, "")
    }
}