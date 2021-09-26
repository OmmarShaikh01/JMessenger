package org.jam.jmessenger.data.db.entity

import com.google.common.io.ByteStreams.toByteArray
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.math.BigInteger
import java.security.MessageDigest


data class Message (
    var mid: String = "",
    var sender: String = "",
    var receiver: String = "",
    var text: String? = null,
    var msgType: MessageType? = null,
    @ServerTimestamp
    var sendtime: Timestamp? = null
): Serializable {
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

enum class MessageType { USER, GROUP }
enum class MessageState { PENDING, READ, UNREAD, SENT }