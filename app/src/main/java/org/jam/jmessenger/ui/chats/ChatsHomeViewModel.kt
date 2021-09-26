package org.jam.jmessenger.ui.chats

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.repository.ChatsRepository
import org.jam.jmessenger.data.db.repository.DatabaseRepository
import org.jam.jmessenger.data.db.repository.StorageRepository
import org.jam.jmessenger.ui.contacts.ContactsHomeViewModel
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
}