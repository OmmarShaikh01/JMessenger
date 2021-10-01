package org.jam.jmessenger.ui.contacts

import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.jam.jmessenger.data.db.Result
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.entity.UserFriend
import org.jam.jmessenger.data.db.entity.UserInfo
import org.jam.jmessenger.data.db.repository.DatabaseRepository
import org.jam.jmessenger.data.db.repository.StorageRepository
import org.jam.jmessenger.ui.DefaultViewModel
import org.jam.jmessenger.widgets.ProfileRoundImageView


class ContactsHomeViewModelFactory(private val uid: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ContactsHomeViewModel(uid) as T
    }
}

class ContactsHomeViewModel(private val uid: String) : DefaultViewModel() {
    private val TAG = "ContactsHomeViewModel"
    private val database_repository = DatabaseRepository()
    private val storage_repository = StorageRepository()

    // LiveData
    private var _userInfo = MutableLiveData<User>()
    var userInfo: LiveData<User> = _userInfo


    init{
        database_repository.loadUser(uid) { result ->
            onResult(_userInfo, result)
        }
        setuserInfoSnapShot(uid)
    }

    fun setuserInfoSnapShot(uid: String) {
        database_repository.onUserDataChangeListener(uid) { result ->
            onResult(_userInfo, result)
        }
    }

    fun loadUserFriendProfileImage(uid: String, imageView: Any) {
        storage_repository.loadUserProfileImage(uid) { result ->
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

    fun loadUserFriendInfo(uid: String, name: TextView, status: TextView) {
        database_repository.loadUser(uid) { result ->
            run {
                if (result is Result.Success) {
                    result.data.let { user ->
                        name.text = user.info.name
                        status.text = user.info.status
                    }
                }
            }
        }
    }

    fun unfriendUser(userFriend: UserFriend) {
        val friend = User(UserInfo(uid = userFriend.uid, name = userFriend.name))
        val user = userInfo.value ?: return
        if (user.info.uid.isNotEmpty() && friend.info.uid.isNotEmpty()) {
            database_repository.rejectFriend(user, friend)
        }
    }

    fun blockUser(userFriend: UserFriend) {
        val friend = User(UserInfo(uid = userFriend.uid, name = userFriend.name))
        val user = userInfo.value ?: return
        if (user.info.uid.isNotEmpty() && friend.info.uid.isNotEmpty()) {
            database_repository.blockFriend(user, friend)
        }
    }
}