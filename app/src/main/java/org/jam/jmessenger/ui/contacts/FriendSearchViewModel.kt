package org.jam.jmessenger.ui.contacts

import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.jam.jmessenger.data.db.Result
import org.jam.jmessenger.data.db.entity.FriendState
import org.jam.jmessenger.data.db.entity.Profile
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.entity.UserFriend
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
    // END REGION

    fun addFriend(): FriendState? {
        val loadedUser = userInfo.value ?: return null
        val loadedFriend = friendInfo.value?: return null
        if ((loadedUser.info.uid.isNotEmpty()) and (loadedFriend.info.uid.isNotEmpty())) {
            if (loadedUser.friends.keys.contains(loadedFriend.info.uid)) {
                val state = loadedUser.friends[loadedFriend.info.uid]!!.state
                Log.i(TAG, state?.name.toString())
                return state
            } else {
                database_repository.addFriend(loadedUser, loadedFriend).addOnCompleteListener() {
                    loadUserInfo(loadedUser.info.uid)
                }
                Log.i(TAG, FriendState.OUTREQUESTED.name)
                return FriendState.OUTREQUESTED
            }
        } else {
            return null
        }
    }

    fun searchFriendEmail(email: String){
        database_repository.searchFriendEmail(email) { result: Result<User> ->
            onResult(_friendInfo, result)
            if (result is Result.Success){
                val uid = _friendInfo.value!!.info.uid
                storage_repository.loadUserProfileImage(uid) { profileResult: Result<Profile> ->
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

    companion object{
        enum class RequestState { PENDING, ALREADY_FRIEND, SENT, FAILED }
    }
}