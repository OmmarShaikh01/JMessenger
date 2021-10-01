package org.jam.jmessenger.data.db.entity

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable


data class User (
    var info: UserInfo = UserInfo(),
    var friends: HashMap<String, UserFriend> = HashMap(),
    var groups: HashMap<String, UserGroup> = HashMap()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as User
        if (this.info.uid != other.info.uid) return false
        return true
    }
    override fun hashCode(): Int = TODO()
    override fun toString(): String {
        return "${this.info.uid}: ${this.info.name}"
    }
}


data class UserInfo(
    var uid: String = "",
    var name: String = "",
    var email: String = "",
    var profileuri: String = "profile_images/User-Profile-PNG.png",
    var status: String = "New JMessenger User",
    var isOnline: Boolean = true,
    @ServerTimestamp
    var last_seen: Timestamp? = null,
): Serializable {
    override fun toString(): String = "${this.uid}: ${this.name}"
}


data class UserFriend (
    var uid: String = "",
    var name: String = "",
    var state: FriendState? = null,
    var profileuri: String = "",
    var isMute: Boolean = false,
)


data class UserGroup (
    var uid: String = "",
    var name: String = "",
    var profileuri: String = "",
    var state: GroupRole? = null,
    var isMute: Boolean = false,
)


data class Group (
    var info: UserInfo = UserInfo(),
    var users: HashMap<String, UserFriend> = HashMap(),
    var pGroupchats: HashMap<String, Message> = HashMap()
)


data class Profile(
    var url: String = "",
    var data: ByteArray = ByteArray(1000 * 1024) // defaults to a 1000 KB array
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Profile
        if (url != other.url) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}


enum class FriendState {
    INREQUESTED, // A Foreign User has requested to follow
    OUTREQUESTED, // User has requested to follow a friend
    FRIEND, // user and the foreign user have a friend relationship
    BLOCKED, // user has been blocked by a friend
    BLOCKER, // user is the blocker
    ACCEPTED, // user friend request has been accepted
    UNFRIENDED // user friend request has been declined
}


enum class GroupRole {
    INREQUESTED, // A Foreign User has requested to follow
    OUTREQUESTED, // User has requested to follow a friend
    FRIEND, // user and the foreign user have a friend relationship
    BLOCKED, // user has been blocked by a friend
    BLOCKER, // user is the blocker
    ACCEPTED, // user friend request has been accepted
    UNFRIENDED // user friend request has been declined
}