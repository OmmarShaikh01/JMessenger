package org.jam.jmessenger.data.db.remote

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import org.jam.jmessenger.data.db.entity.Message
import org.jam.jmessenger.data.db.entity.MessageType
import org.jam.jmessenger.data.db.entity.RoomMessage
import java.util.*

class FirebaseChatDatabaseSources {
    private val TAG = "ChatDatabaseSources"
    private val firebaseDatabase = FirebaseFirestore.getInstance()


    // Search Functions ----------------------------------------------------------------------------


    // Persist Functions ---------------------------------------------------------------------------


    // Create Functions ----------------------------------------------------------------------------
    fun sendMessage(senderUID: String, receiverUID: String, text: String): Task<Void> {
        val message = createUserMessage(senderUID, receiverUID, text)
        return getRefrence(receiverUID).set((hashMapOf(message.mid to message) as Map<String, Any>), SetOptions.merge())
    }

    fun sendMessage(messages: List<RoomMessage>): Task<Void> {
        return firebaseDatabase.runBatch {
            for (message in messages) {
                val firebaseMessage = createUserMessage(message.sender, message.receiver, message.text.toString())
                it.set(
                    getRefrence(message.receiver),
                    (hashMapOf(firebaseMessage.mid to firebaseMessage) as Map<String, Any>),
                    SetOptions.merge()
                )
            }
            return@runBatch
        }
    }

    // Update Functions ----------------------------------------------------------------------------


    // Loader Functions ----------------------------------------------------------------------------
    fun getPendingMessage(userUID: String): Task<DocumentSnapshot> {
        return getRefrence(userUID).get()
    }

    // Delete Functions ----------------------------------------------------------------------------
    fun clearChatsPendingQueue(userUID: String): Task<Void> {
        return getRefrence(userUID).delete()
    }

    // Misc Functions ------------------------------------------------------------------------------
    fun createUserMessage(senderUID: String, receiverUID: String, contents: String): Message {
        return Message(
            sender = senderUID,
            receiver = receiverUID,
            text = contents,
            msgType = MessageType.USER,
        )
    }

    fun createGroupMessage(senderUID: String, receiverUID: String, contents: String): Message {
        return Message(
            sender = senderUID,
            receiver = receiverUID,
            text = contents,
            msgType = MessageType.GROUP,
        )
    }

    private fun getRefrence(uid: String): DocumentReference {
        return firebaseDatabase.collection("users").document(uid).collection("chats").document("users")
    }

    fun useEmulator(host: String, port: Int) {
        firebaseDatabase.useEmulator(host, port)
        firebaseDatabase.clearPersistence()
    }
}