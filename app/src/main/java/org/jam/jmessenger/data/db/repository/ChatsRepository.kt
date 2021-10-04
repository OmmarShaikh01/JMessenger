package org.jam.jmessenger.data.db.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jam.jmessenger.data.db.Result
import org.jam.jmessenger.data.db.entity.*
import org.jam.jmessenger.data.db.remote.FirebaseChatDatabaseSources
import org.jam.jmessenger.data.db.remote.FirebaseDatabaseSource
import org.jam.jmessenger.data.db.room.RoomChatDatabaseSources
import java.util.*
import org.jam.jmessenger.data.db.room.RoomChatDatabaseSources.RoomChatDatabaseType as aliasRoomChatDatabaseType


class ChatsRepository(
    context: Context,
    type: aliasRoomChatDatabaseType = aliasRoomChatDatabaseType.STORAGE
) {
    private val TAG = "ChatsRepository"
    private val firebaseChatsSource = FirebaseChatDatabaseSources()
    private val roomChatsSource = RoomChatDatabaseSources.getDatabase(context, type) ?:
        throw Exception("Room Failed to Initialize")

    private val userRoomDAO = roomChatsSource.userDAO()!!
    private val messageRoomDAO = roomChatsSource.messageDao()!!


    // Create Functions ----------------------------------------------------------------------------
    fun sendMessage(senderUID: String, receiverUID: String, contents: String): Task<Void> {
        return firebaseChatsSource.sendMessage(senderUID, receiverUID, contents).addOnSuccessListener {
            val message = createUserMessage(senderUID, receiverUID, contents)
            message.msgState = MessageState.SENT
            messageRoomDAO.asyncInsertAll(message)
            Log.i(TAG, message.toString())
        }.addOnFailureListener {
            val message = createUserMessage(senderUID, receiverUID, contents)
            message.msgState = MessageState.PENDING
            messageRoomDAO.asyncInsertAll(message)
            Log.e(TAG, message.toString())
        }
    }

    fun resendPendingMessage(): Task<Void> {
        val pendingMessagesList = messageRoomDAO.readPendingMessage()
        return firebaseChatsSource.sendMessage(pendingMessagesList).addOnSuccessListener {
            val messageList = mutableListOf<RoomMessage>()
            pendingMessagesList.forEach { message ->
                message.msgState = MessageState.SENT
                messageList.add(message)
            }
            messageRoomDAO.asyncInsertAll(*messageList.toTypedArray())
            Log.i(TAG,"resendPendingMessage")
        }.addOnFailureListener {
            Log.e(TAG, "resendPendingMessage")
        }
    }


    // Read Functions ------------------------------------------------------------------------------
    fun readMessages(senderUID: String, receiverUID: String, state: MessageState = MessageState.UNREAD): List<RoomMessage> {
        return messageRoomDAO.readUnreadMessages(senderUID, receiverUID, state)
    }

    fun readUnreadMessages(): List<RoomMessage> {
        return messageRoomDAO.readUnreadMessages()
    }

    fun readConversation(senderUID: String, receiverUID: String, infix: ((Result<List<RoomMessage>>) -> Unit)) {
        CoroutineScope(Dispatchers.IO).launch{
            val messageList = messageRoomDAO.readConversation(senderUID, receiverUID)
            infix.invoke(Result.Success(data = messageList))
        }
    }

    fun receiveMessage(userUID: String): Task<DocumentSnapshot> {
        return firebaseChatsSource.getPendingMessage(userUID).addOnSuccessListener { doc ->
            val messageList = mutableListOf<RoomMessage>()
            doc.data?.entries?.forEach { item ->
                val value = (item.value as HashMap<*, *>)
                if (value.size == 6) {
                    value.let {
                        val message = createUserMessage(it["sender"] as String,
                            it["receiver"] as String,
                            it["text"] as String)
                        message.msgState = MessageState.UNREAD
                        messageList.add(message)
                    }
                }
            }
            if (messageList.isNotEmpty()) {
                messageRoomDAO.asyncInsertAll(*messageList.toTypedArray())
                // TODO: ENABLE IN PRODUCTION
                // firebaseChatsSource.clearChatsPendingQueue(userUID)
            }
        }
    }

    fun getUnreadCount(senderUID: String, receiverUID: String): Int {
        return messageRoomDAO.getUnreadCount(senderUID, receiverUID)
    }

    fun getConversationList(): LiveData<List<RoomUser>> {
        return messageRoomDAO.getConversationList()
    }

    fun getUserList(): LiveData<List<RoomUser>> {
        return userRoomDAO.getUserList()
    }

    fun getUserList(excludeUID: String): LiveData<List<RoomUser>> {
        return userRoomDAO.getUserList(excludeUID)
    }

    fun getRecentMessage(userUID_1: String, userUID_2: String): RoomMessage {
        return messageRoomDAO.getMostRecentMessage(userUID_1, userUID_2)
    }

    // Delete Functions ----------------------------------------------------------------------------
    fun deleteMessageFromSenderTo(senderUID: String, receiverUID: String) {
        return messageRoomDAO.deleteMessageFromSenderTo(senderUID, receiverUID)
    }


    // Misc Functions ------------------------------------------------------------------------------
    fun createUserMessage(senderUID: String, receiverUID: String, contents: String): RoomMessage {
        return RoomMessage(
            sender = senderUID,
            receiver = receiverUID,
            text = contents,
            msgType = MessageType.USER,
            sendtime = Calendar.getInstance().time.time
        )
    }

    fun closeRoomDB() {
        roomChatsSource.close()
    }

    fun useEmulator(host: String, port: Int) {
        firebaseChatsSource.useEmulator(host, port)
    }
}