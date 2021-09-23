package org.jam.jmessenger.ui.contacts

import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.jam.jmessenger.data.db.Result.*
import org.jam.jmessenger.data.db.Result.Success
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.entity.UserFriend
import org.jam.jmessenger.data.db.entity.UserInfo
import org.jam.jmessenger.data.db.repository.DatabaseRepository
import org.jam.jmessenger.data.db.repository.StorageRepository
import org.jam.jmessenger.ui.DefaultViewModel
import org.jam.jmessenger.widgets.ProfileRoundImageView


class FriendRequestsViewModelFactory(private val uid: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FriendRequestsViewModel(uid) as T
    }
}


class FriendRequestsViewModel(private val uid: String) : DefaultViewModel() {
    private val database_repository = DatabaseRepository()
    private val storage_repository = StorageRepository()
    private val TAG = "FriendRequestsViewModel"

    // LiveData declarations
    private val _userInfo = MutableLiveData<User>()
    var userInfo: LiveData<User> = _userInfo

    init {
        database_repository.loadUser(uid) { result ->
            onResult(_userInfo, result)
        }
        setuserInfoSnapShot(uid)
    }

    fun loadUserProfileImage(uid: String, imageView: ProfileRoundImageView) {
        storage_repository.loadUserProfileImage(uid) { result ->
            run {
                if (result is Success ) {
                    result.data.let { profile ->
                        val bmp = BitmapFactory.decodeByteArray(profile.data, 0, profile.data.size)
                        imageView.setImageBitmap(bmp)
                    }
                }
            }
        }
    }

    fun acceptRequest(userFriend: UserFriend) {
        val friendUser = User(UserInfo(uid = userFriend.uid, name = userFriend.name))
        val user = userInfo.value
        if (user != null) database_repository.acceptFriend(user, friendUser)
    }

    fun rejectRequest(userFriend: UserFriend) {
        val friendUser = User(UserInfo(uid = userFriend.uid, name = userFriend.name))
        val user = userInfo.value
        if (user != null) database_repository.rejectFriend(user, friendUser)
    }

    fun setuserInfoSnapShot(uid: String) {
        database_repository.onUserDataChangeListener(uid) { result ->
            onResult(_userInfo, result)
        }
    }
}