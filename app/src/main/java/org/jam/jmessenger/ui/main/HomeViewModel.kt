package org.jam.jmessenger.ui.main

import android.app.NotificationManager
import android.content.Context
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.jam.jmessenger.R
import org.jam.jmessenger.data.db.entity.FriendState
import org.jam.jmessenger.data.db.entity.UserFriend
import org.jam.jmessenger.data.db.repository.AuthenticationRepository
import org.jam.jmessenger.data.db.repository.ChatsRepository
import org.jam.jmessenger.data.db.repository.DatabaseRepository
import org.jam.jmessenger.ui.contacts.ContactsHomeViewModel
import java.lang.ref.WeakReference


class HomeViewModelFactory(
    private val uid: String,
    private val context: WeakReference<Context>
    ) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(uid, context) as T
    }
}

class HomeViewModel(
    private val uid: String,
    private val context: WeakReference<Context>
) : ViewModel() {

    val databaseRepository = DatabaseRepository()
    val authRepository = AuthenticationRepository()
    val chatsRepository = ChatsRepository(context.get()!!)

    val unreadCount = chatsRepository.getAllUnreadCount()
    val friendList = databaseRepository.getFriendList(uid)

    init {
        chatsRepository.receiveMessage(uid)
        chatsRepository.addOnChatChangedListener(uid)
    }

    override fun onCleared() {
        if (authRepository.checkUserAvailable()) {
            authRepository.getValidUser()?.uid?.let {
                databaseRepository.updateUserLastSeen(it)
            }
        }
        super.onCleared()
    }

    fun notifyNewMessage(unreadCount: Int) {
        var count = unreadCount
        val mContext = context.get()
        if (mContext != null && count > 0) {
            val builder = NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.jabmessenger_appicon)
                .setContentTitle("Unread Messages")
                .setContentText("There are $count unread messages")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
            val notificationManager =
                mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            notificationManager?.notify(10202, builder.build())
        }
    }

    fun notifyIncommingFriendRequest(friends: HashMap<String, UserFriend>) {
        var count = 0
        for ((k, v) in friends.iterator()) {
            if (v.state?.name.toString() == FriendState.INREQUESTED.name) count++
        }
        val mContext = context.get()
        if (mContext != null && count > 0) {
            val builder = NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.jabmessenger_appicon)
                .setContentTitle("Incoming Friend Request")
                .setContentText("There are $count friend requests")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
            val notificationManager =
                mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            notificationManager?.notify(10200, builder.build())
        }
    }
}