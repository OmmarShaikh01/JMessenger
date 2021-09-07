package org.jam.jmessenger.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.storage.UploadTask
import org.jam.jmessenger.data.db.Result
import org.jam.jmessenger.data.db.entity.Profile
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.repository.DatabaseRepository
import org.jam.jmessenger.data.db.repository.StorageRepository
import org.jam.jmessenger.ui.DefaultViewModel


class UserProfileViewModelFactory(private val uid: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T { return UserProfileViewModel(uid) as T }
}


class UserProfileViewModel(private val uid: String) : DefaultViewModel() {
    private val database_repository = DatabaseRepository()
    private val storage_repository = StorageRepository()

    private val _user = MutableLiveData<User>()
    var user: LiveData<User> = _user
    private val _profile = MutableLiveData<Profile>()
    var profile: LiveData<Profile> = _profile

    init {
        updateUIData(uid)
    }

    private fun updateUIData(uid: String) {
        loadUserInfo(uid)
        loadUserProfile(uid)
    }

    fun loadUserInfo(uid: String) {
        database_repository.loadUser(uid) { result: Result<User> ->
            onResult(_user, result)
        }
    }

    fun updateUserInfo(user: User) {
        database_repository.createNewUser(user).addOnSuccessListener {  }
    }

    fun loadUserProfile(uid: String) {
        storage_repository.loadUserProfileImage(uid) { result: Result<Profile> ->
            onResult(_profile, result)
        }
    }

    fun updateUserProfile(uid: String, profile: Profile): UploadTask {
        return storage_repository.uploadUserProfileImage(uid, profile)
    }
}