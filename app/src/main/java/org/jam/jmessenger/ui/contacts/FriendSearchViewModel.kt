package org.jam.jmessenger.ui.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.jam.jmessenger.data.db.Result
import org.jam.jmessenger.data.db.entity.FriendState
import org.jam.jmessenger.data.db.entity.Profile
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.repository.DatabaseRepository
import org.jam.jmessenger.data.db.repository.StorageRepository
import org.jam.jmessenger.ui.DefaultViewModel


class FriendSearchViewModelFactory(private val uid: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T { return FriendSearchViewModel(uid) as T }
}


class FriendSearchViewModel(private val uid: String) : DefaultViewModel() {
    private val database_repository = DatabaseRepository()
    private val storage_repository = StorageRepository()
    private var TAG = "FriendSearchViewModel"

    // START REGION: live data declaration
    private val _userInfo = MutableLiveData<User>()
    var userInfo: LiveData<User> = _userInfo
    private val _friendInfo = MutableLiveData<User>()
    var friendInfo: LiveData<User> = _friendInfo
    private val _friendProfile = MutableLiveData<Profile>()
    var friendProfile: LiveData<Profile> = _friendProfile
    private val _friendRequestState = MutableLiveData<FriendState>()
    var friendRequestState: LiveData<FriendState> = _friendRequestState
    // END REGION

    init {
        loadUserInfo(uid)
        // set snapshot listener for the user data
        database_repository.onUserDataChangeListener(uid) { result: Result<User> ->
            onResult(_userInfo, result)
        }
    }

    fun addFriend() {
        val loadedUser = userInfo.value ?: return
        val loadedFriend = friendInfo.value ?: return
        if ((loadedUser.info.uid.isNotEmpty()) and (loadedFriend.info.uid.isNotEmpty())) {
            if (loadedUser.friends.keys.contains(loadedFriend.info.uid)) {
                _friendRequestState.value = loadedUser.friends[loadedFriend.info.uid]!!.state!!
            } else {
                database_repository.addFriend(loadedUser, loadedFriend).addOnSuccessListener {
                    _friendRequestState.value = FriendState.OUTREQUESTED
                }
            }
        }
    }

    fun removeFriend() {
        val loadedUser = userInfo.value ?: return
        val loadedFriend = friendInfo.value ?: return
        if ((loadedUser.info.uid.isNotEmpty()) and (loadedFriend.info.uid.isNotEmpty())) {
            if (loadedUser.friends.keys.contains(loadedFriend.info.uid)) {
                database_repository.rejectFriend(loadedUser, loadedFriend).addOnSuccessListener {
                    _friendRequestState.value = FriendState.UNFRIENDED
                }
            }
        }
    }

    fun unblockFriend() {
        val loadedUser = userInfo.value ?: return
        val loadedFriend = friendInfo.value ?: return
        if ((loadedUser.info.uid.isNotEmpty()) and (loadedFriend.info.uid.isNotEmpty())) {
            if (loadedUser.friends.keys.contains(loadedFriend.info.uid)) {
                database_repository.unblockFriend(loadedUser, loadedFriend).addOnSuccessListener {
                    _friendRequestState.value = FriendState.ACCEPTED
                }
            }
        }
    }

    fun acceptFriend() {
        val loadedUser = userInfo.value ?: return
        val loadedFriend = friendInfo.value ?: return
        if ((loadedUser.info.uid.isNotEmpty()) and (loadedFriend.info.uid.isNotEmpty())) {
            if (loadedUser.friends.keys.contains(loadedFriend.info.uid)) {
                database_repository.acceptFriend(loadedUser, loadedFriend).addOnSuccessListener {
                    _friendRequestState.value = FriendState.ACCEPTED
                }
            }
        }
    }

    fun searchFriendEmail(email: String){
        database_repository.searchFriendEmail(email) { result: Result<User> ->
            onResult(_friendInfo, result)
            if (result is Result.Success){
                val uri = _friendInfo.value!!.info.profileuri
                storage_repository.loadURIImage(uri) { profileResult: Result<Profile> ->
                    onResult(_friendProfile, profileResult)
                }
            }
        }
    }

    fun loadUserInfo(uid: String) {
        database_repository.loadUser(uid) { result: Result<User> ->
            onResult(_userInfo, result)
        }
    }
}