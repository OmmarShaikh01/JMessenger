package org.jam.jmessenger.ui.chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.repository.DatabaseRepository
import org.jam.jmessenger.data.db.repository.StorageRepository
import org.jam.jmessenger.ui.contacts.ContactsHomeViewModel


class ChatsHomeViewModelFactory(private val uid: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatsHomeViewModel(uid) as T
    }
}

class ChatsHomeViewModel(private val uid: String) : ViewModel() {
    private val TAG = "ChatsHomeViewModel"
    private val database_repository = DatabaseRepository()
    private val storage_repository = StorageRepository()

    // LiveData
    private var _userInfo = MutableLiveData<User>()
    var userInfo: LiveData<User> = _userInfo

}