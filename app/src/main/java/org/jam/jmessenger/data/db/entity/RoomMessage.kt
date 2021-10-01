package org.jam.jmessenger.data.db.entity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

@Entity(tableName = "messages")
data class RoomMessage (
    @PrimaryKey var mid: String = "",
    @ColumnInfo(name = "sender") var sender: String = "",
    @ColumnInfo(name = "receiver")var receiver: String = "",
    @ColumnInfo(name = "text")var text: String? = null,
    @ColumnInfo(name = "msgType")var msgType: MessageType? = null,
    @ColumnInfo(name = "msgState")var msgState: MessageState? = null,
    @ColumnInfo(name = "sendtime")var sendtime: Long? = null,
) {
    init {
        mid = hashString()
    }

    private fun hashString(): String {
        val input = "$sender$receiver$text"
        val bytes = MessageDigest
                .getInstance("MD5")
                .digest(input.toByteArray())
        return  BigInteger(1, bytes).toString(16).padStart(32, '0')
    }

    override fun toString(): String {
        return "$sender:Sent: $text"
    }
}


@Dao
interface RoomMessageDAO {

    @Insert(entity = RoomMessage::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg message: RoomMessage)

    @Insert(entity = RoomMessage::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun suspendInsertAll(vararg message: RoomMessage)

    @Update(entity = RoomMessage::class, onConflict = OnConflictStrategy.REPLACE)
    fun updateAll(vararg message: RoomMessage)

    @Query("UPDATE messages SET msgState = :readFLAG WHERE msgState = :unreadFLAG")
    fun updateReadState(readFLAG: MessageState = MessageState.READ, unreadFLAG: MessageState = MessageState.UNREAD)

    @Query("SELECT * FROM chatting_users")
    fun getConversationList(): LiveData<List<RoomUser>>

    @Query("SELECT * FROM messages WHERE (sender IN (:user1UID, :user2UID) AND receiver IN (:user1UID, :user2UID)) ORDER BY sendtime DESC LIMIT 1")
    fun getMostRecentMessage(user1UID: String, user2UID: String): RoomMessage

    @Query("SELECT COUNT(mid) FROM messages WHERE (sender IN (:user1UID, :user2UID) AND receiver IN (:user1UID, :user2UID)) ORDER BY sendtime DESC LIMIT 1")
    fun getUnreadCount(user1UID: String, user2UID: String): Int

    @Query("SELECT * FROM messages WHERE (sender IN (:senderUID, :receiverUID) AND receiver IN (:senderUID, :receiverUID) AND msgState LIKE :stateFLAG)")
    fun readReadMessage(senderUID: String, receiverUID: String, stateFLAG: MessageState = MessageState.READ): List<RoomMessage>

    @Query("SELECT * FROM messages WHERE msgState LIKE :stateFLAG")
    fun readPendingMessage(stateFLAG: MessageState = MessageState.PENDING): List<RoomMessage>

    @Query("SELECT * FROM messages WHERE (sender IN (:senderUID, :receiverUID) AND receiver IN (:senderUID, :receiverUID) AND msgState LIKE :stateFLAG)")
    fun readUnreadMessages(senderUID: String, receiverUID: String, stateFLAG: MessageState = MessageState.UNREAD): List<RoomMessage>

    @Query("SELECT * FROM messages WHERE (msgState LIKE :stateFLAG)")
    fun readUnreadMessages(stateFLAG: MessageState = MessageState.UNREAD): List<RoomMessage>

    @Query("SELECT * FROM messages WHERE (sender IN (:senderUID, :receiverUID) AND receiver IN (:senderUID, :receiverUID)) ORDER BY sendtime DESC")
    fun readConversation(senderUID: String, receiverUID: String): List<RoomMessage>

    @Delete(entity = RoomMessage::class)
    fun deleteMessage(message: RoomMessage): Int

    @Query("DELETE FROM messages WHERE (sender IN (:senderUID, :receiverUID) AND receiver IN (:senderUID, :receiverUID))")
    fun deleteMessageFromSenderTo(senderUID: String, receiverUID: String)

    fun asyncInsertAll(vararg message: RoomMessage) {
        CoroutineScope(Dispatchers.IO).launch { suspendInsertAll(*message) }
    }
}
