package org.jam.jmessenger.ui.chats

import android.content.Context
import android.graphics.BitmapFactory
import android.media.Image
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.*
import org.jam.jmessenger.data.db.Result
import org.jam.jmessenger.data.db.entity.RoomMessage
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.entity.UserFriend
import org.jam.jmessenger.data.db.entity.UserInfo
import org.jam.jmessenger.data.db.repository.ChatsRepository
import org.jam.jmessenger.data.db.repository.DatabaseRepository
import org.jam.jmessenger.data.db.repository.StorageRepository
import org.jam.jmessenger.ui.contacts.ContactsHomeViewModel
import org.jam.jmessenger.widgets.ProfileRoundImageView
import java.lang.ref.WeakReference


class ChatsHomeViewModelFactory(
    private val context: WeakReference<Context>,
    private val uid: String): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatsHomeViewModel(context, uid) as T
    }
}

class ChatsHomeViewModel(private val context: WeakReference<Context>, private val uid: String) : ViewModel() {

    private val TAG = "ChatsHomeViewModel"
    private val database_repository = DatabaseRepository()
    private val storage_repository = StorageRepository()
    private val chats_repository = ChatsRepository(context.get()!!)

    // LiveData
    private var _userInfo = MutableLiveData<User>()
    var userInfo: LiveData<User> = _userInfo
    var conversationList = chats_repository.getConversationList()
    var userList = chats_repository.getUserList(excludeUID = uid)
    init {
        chats_repository.receiveMessage(uid)
        updateUserData()
    }

    private fun updateUserData() {
        database_repository.loadUser(uid) { result ->
            run {
                if (result is Result.Success) result.data.let { user -> _userInfo.value = user }
                else { throw ExceptionInInitializerError() }
            }
        }
    }

    fun getRecentMessage(userUID: String): RoomMessage {
        return chats_repository.getRecentMessage(userUID, uid)
    }

    fun getUnreadCount(userUID: String): Int {
        return chats_repository.getUnreadCount(userUID, uid)
    }

    fun loadUserFriendInfo(userUID: String, name: TextView) {
        database_repository.loadUser(userUID) { result ->
            run {
                if (result is Result.Success) result.data.let { user -> name.text = user.info.name }
            }
        }
    }

    fun loadUserFriendInfo(userUID: String, infix: ((Result<User>) -> Unit)) {
        return database_repository.loadUser(userUID, infix)
    }

    fun muteUser(friend: UserFriend) {
        val userfriend = User(UserInfo(uid = friend.uid, name = friend.name))
        val user = userInfo.value ?: return
        if (user.info.uid.isNotEmpty() && userfriend.info.uid.isNotEmpty() && userfriend.info.name.isNotEmpty()) {
            database_repository.muteFriend(user, userfriend)
            Toast.makeText(context.get()!!, "Operation Successful.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context.get()!!, "Operation Failed.", Toast.LENGTH_SHORT).show()
        }
    }

    fun blockUser(friend: UserFriend) {
        val userfriend = User(UserInfo(uid = friend.uid, name = friend.name))
        val user = userInfo.value ?: return
        if (user.info.uid.isNotEmpty() && userfriend.info.uid.isNotEmpty() && userfriend.info.name.isNotEmpty()) {
            database_repository.blockFriend(user, userfriend)
            Toast.makeText(context.get()!!, "Operation Successful.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context.get()!!, "Operation Failed.", Toast.LENGTH_SHORT).show()
        }
    }

    fun unfriendUser(friend: UserFriend) {
        val userfriend = User(UserInfo(uid = friend.uid, name = friend.name))
        val user = userInfo.value ?: return
        if (user.info.uid.isNotEmpty() && userfriend.info.uid.isNotEmpty() && userfriend.info.name.isNotEmpty()) {
            database_repository.rejectFriend(user, userfriend)
            Toast.makeText(context.get()!!, "Operation Successful.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context.get()!!, "Operation Failed.", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkIsMute(userUID: String, mute: ImageView) {
        database_repository.getFriendIsMute(userUID, uid){ result ->
            run {
                if (result is Result.Success) result.data.let { user ->
                    if (!user) {
                        mute.visibility = View.INVISIBLE
                    } else {
                        mute.visibility = View.VISIBLE
                    }
                } else {
                    mute.visibility = View.INVISIBLE
                }
            }
        }
    }

    fun loadUserFriendProfileImage(userUID: String, imageView: Any) {
        storage_repository.loadUserProfileImage(userUID) { result ->
            run {
                if (result is Result.Success) {
                    result.data.let { profile ->
                        val bmp = BitmapFactory.decodeByteArray(profile.data, 0, profile.data.size)
                        when(imageView) {
                            is ImageView -> imageView.setImageBitmap(bmp)
                            is ProfileRoundImageView -> imageView.setImageBitmap(bmp)
                        }
                    }
                }
            }
        }
    }
}