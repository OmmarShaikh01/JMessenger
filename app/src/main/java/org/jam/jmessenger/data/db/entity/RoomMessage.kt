package org.jam.jmessenger.data.db.entity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import kotlin.random.Random

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

    override fun equals(other: Any?): Boolean {
        if (other is RoomMessage) {
            return this.mid == other.mid
        } else {
            return false
        }
    }

    fun hashString(): String {
        val seed = Random(3).nextInt()
        val input = "$sender$receiver$text$sendtime$seed"
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

    @Query("UPDATE messages SET msgState = :readFLAG WHERE sender IN (:user1UID, :user2UID) AND receiver IN (:user1UID, :user2UID) AND msgState = :unreadFLAG")
    fun updateReadState(user1UID: String, user2UID: String, readFLAG: MessageState = MessageState.READ, unreadFLAG: MessageState = MessageState.UNREAD)

    @Query("SELECT * FROM chatting_users")
    fun getConversationList(): LiveData<List<RoomUser>>

    @Query("SELECT * FROM messages WHERE (sender IN (:user1UID, :user2UID) AND receiver IN (:user1UID, :user2UID)) ORDER BY sendtime DESC LIMIT 1")
    fun getMostRecentMessage(user1UID: String, user2UID: String): RoomMessage

    @Query("SELECT COUNT(mid) FROM messages WHERE (sender IN (:user1UID, :user2UID) AND receiver IN (:user1UID, :user2UID) AND msgState LIKE :stateFLAG) LIMIT 1")
    fun getUnreadCount(user1UID: String, user2UID: String, stateFLAG: MessageState = MessageState.UNREAD): Int

    @Query("SELECT COUNT(mid) FROM messages WHERE (msgState LIKE :stateFLAG) LIMIT 1")
    fun getAllUnreadCount(stateFLAG: MessageState = MessageState.UNREAD): LiveData<Int>

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

    @Query("SELECT * FROM messages WHERE (sender IN (:senderUID, :receiverUID) AND receiver IN (:senderUID, :receiverUID)) ORDER BY sendtime")
    fun readConversationPaged(senderUID: String, receiverUID: String): PagingSource<Int, RoomMessage>

    @Delete(entity = RoomMessage::class)
    fun deleteMessage(message: RoomMessage): Int

    @Query("DELETE FROM messages WHERE (sender IN (:senderUID, :receiverUID) AND receiver IN (:senderUID, :receiverUID))")
    fun deleteMessageFromSenderTo(senderUID: String, receiverUID: String)

    fun asyncInsertAll(vararg message: RoomMessage) {
        CoroutineScope(Dispatchers.IO).launch { suspendInsertAll(*message) }
    }
}