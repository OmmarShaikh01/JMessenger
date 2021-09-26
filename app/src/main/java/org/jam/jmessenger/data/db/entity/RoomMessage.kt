package org.jam.jmessenger.data.db.entity

import androidx.room.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@Entity(tableName = "messages")
data class RoomMessage (
    @PrimaryKey(autoGenerate = true) var mid: Long = 0L,
    @ColumnInfo(name = "sender") var sender: String = "",
    @ColumnInfo(name = "receiver")var receiver: String = "",
    @ColumnInfo(name = "text")var text: String? = null,
    @ColumnInfo(name = "msgType")var msgType: MessageType? = null,
    @ColumnInfo(name = "msgState")var msgState: MessageState? = null,
    @ColumnInfo(name = "sendtime")var sendtime: Long? = null,
) {
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

    fun asyncInsertAll(vararg message: RoomMessage) {
        CoroutineScope(Dispatchers.IO).launch { suspendInsertAll(*message) }
    }

    @Update(entity = RoomMessage::class, onConflict = OnConflictStrategy.REPLACE)
    fun updateAll(vararg message: RoomMessage)

    @Query("UPDATE messages SET msgState = :readFLAG WHERE msgState = :unreadFLAG")
    fun updateReadState(
        readFLAG: MessageState = MessageState.READ,
        unreadFLAG: MessageState = MessageState.UNREAD,
    )

    @Query("SELECT * FROM messages WHERE (sender LIKE :senderUID AND receiver LIKE :receiverUID AND msgState LIKE :stateFLAG)")
    fun readReadMessage(
        senderUID: String,
        receiverUID: String,
        stateFLAG: MessageState = MessageState.READ
    ): List<RoomMessage>

    @Query("SELECT * FROM messages WHERE msgState LIKE :stateFLAG")
    fun readPendingMessage(
        stateFLAG: MessageState = MessageState.PENDING
    ): List<RoomMessage>

    @Query("SELECT * FROM messages WHERE (sender LIKE :senderUID AND receiver LIKE :receiverUID AND msgState LIKE :stateFLAG)")
    fun readUnreadMessages(
        senderUID: String,
        receiverUID: String,
        stateFLAG: MessageState = MessageState.UNREAD
    ): List<RoomMessage>

    @Delete(entity = RoomMessage::class)
    fun deleteMessage(message: RoomMessage): Int

    @Query("DELETE FROM messages WHERE sender LIKE :senderUID AND receiver LIKE :receiverUID")
    fun deleteMessageFromSenderTo(senderUID: String, receiverUID: String)
}
