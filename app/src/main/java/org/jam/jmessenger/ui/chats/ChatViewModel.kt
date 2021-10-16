package org.jam.jmessenger.ui.chats

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jam.jmessenger.data.db.Result
import org.jam.jmessenger.data.db.entity.RoomMessage
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.repository.ChatsRepository
import org.jam.jmessenger.data.db.repository.DatabaseRepository
import org.jam.jmessenger.data.db.repository.StorageRepository
import org.jam.jmessenger.widgets.ProfileRoundImageView
import java.lang.ref.WeakReference


class ChatViewModelFactory(
    private val context: WeakReference<Context>,
    private val userUID: String,
    private val friendUID: String
    ): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatViewModel(context, userUID, friendUID) as T
    }
}


class ChatViewModel(
    private val context: WeakReference<Context>,
    private var userUID: String,
    private var friendUID: String
): ViewModel() {

    private val TAG = "ChatViewModel"
    private val database_repository = DatabaseRepository()
    private val storage_repository = StorageRepository()
    private val chats_repository = ChatsRepository(context.get()!!)

    // LiveData
    private var _userInfo = MutableLiveData<User>()
    var userInfo: LiveData<User> = _userInfo
    private var _friendInfo = MutableLiveData<User>()
    var friendInfo: LiveData<User> = _friendInfo

    fun readMessages(senderUID: String, receiverUID: String): Flow<PagingData<RoomMessage>> {
        updateReadCount(senderUID, receiverUID)
        return Pager(PagingConfig(pageSize = 10, maxSize = 100, prefetchDistance = 25)) {
            chats_repository.messageRoomDAO.readConversationPaged(userUID, friendUID)
        }.flow.cachedIn(viewModelScope)
    }

    fun updateReadCount(senderUID: String, receiverUID: String) {
        CoroutineScope(Dispatchers.IO).launch {
            chats_repository.messageRoomDAO.updateReadState(senderUID, receiverUID)
        }
    }

    fun sendMessages(senderUID: String, receiverUID: String, content: String) {
        chats_repository.sendMessage(senderUID, receiverUID, content)
    }

    fun loadUser(uid: String) {
        userUID = uid
        database_repository.loadUser(uid) { result ->
            if (result is Result.Success) result.data.let { user -> _userInfo.value = user }
        }
    }

    fun loadFriend(uid: String) {
        friendUID = uid
        database_repository.loadUser(uid) { result ->
            run {
                if (result is Result.Success) result.data.let { user -> _friendInfo.value = user }
            }
        }
    }

    fun loadUserFriendProfileImage(uid: String, imageView: Any) {
        _friendInfo.value?.let { user ->
            storage_repository.loadUserProfileImage(user.info.uid) { result ->
            run {
                if (result is Result.Success) {
                    result.data.let { profile ->
                        val bmp = BitmapFactory.decodeByteArray(profile.data, 0, profile.data.size)
                        when (imageView) {
                            is ImageView -> imageView.setImageBitmap(bmp)
                            is ProfileRoundImageView -> imageView.setImageBitmap(bmp)
                            }
                        }
                    }
                }
            }
        }
    }
}