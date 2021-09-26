package org.jam.jmessenger.data.db.repository

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Task
import org.jam.jmessenger.data.db.entity.MessageState
import org.jam.jmessenger.data.db.entity.MessageType
import org.jam.jmessenger.data.db.entity.RoomMessage
import org.jam.jmessenger.data.db.remote.FirebaseChatDatabaseSources
import org.jam.jmessenger.data.db.room.RoomChatDatabaseSources
import java.util.*
import org.jam.jmessenger.data.db.room.RoomChatDatabaseSources.RoomChatDatabaseType as aliasRoomChatDatabaseType

class ChatsRepository(
    context: Context,
    type: aliasRoomChatDatabaseType = aliasRoomChatDatabaseType.STORAGE
) {
    private val TAG = "ChatsRepository"
    val firebaseChatsSource = FirebaseChatDatabaseSources()
    val roomChatsSource = RoomChatDatabaseSources.getDatabase(context, type) ?: throw Exception("Room Failed to Initilize")
    private val userRoomDAO = roomChatsSource.userDAO()!!
    private val messageRoomDAO = roomChatsSource.messageDao()!!


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

    fun readMessages(senderUID: String, receiverUID: String, state: MessageState = MessageState.UNREAD): List<RoomMessage> {
        return messageRoomDAO.readUnreadMessages(senderUID, receiverUID, state)
    }

    fun receiveMessage(senderUID: String, receiverUID: String, contents: String) {
        return messageRoomDAO.insertAll()
    }

    fun deleteMessageFromSenderTo(senderUID: String, receiverUID: String) {
        return messageRoomDAO.deleteMessageFromSenderTo(senderUID, receiverUID)
    }

    fun createUserMessage(senderUID: String, receiverUID: String, contents: String): RoomMessage {
        return RoomMessage(
            sender = senderUID,
            receiver = receiverUID,
            text = contents,
            msgType = MessageType.USER,
            sendtime = Calendar.getInstance().time.time
        )
    }

    fun createGroupMessage(senderUID: String, receiverUID: String, contents: String): RoomMessage {
        return RoomMessage(
            sender = senderUID,
            receiver = receiverUID,
            text = contents,
            msgType = MessageType.GROUP,
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