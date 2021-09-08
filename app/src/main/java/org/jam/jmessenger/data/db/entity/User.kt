package org.jam.jmessenger.data.db.entity

data class User(
    var info: UserInfo = UserInfo(), // user info
    var friends: HashMap<String, UserFriend> = HashMap(), // hashmap for friends
    var groups: HashMap<String, UserGroup> = HashMap(), // hashmap for group
    var requestedGroups: HashMap<String, UserGroup> = HashMap(),// hashmap for group
) {
    override fun toString(): String {
        return "${info.name} ${info.email}"
    }
}

data class UserInfo(
    var uid: String = "", // unique user id given by the authenticator api
    var name: String = "", // name given by user
    var email: String = "", // email given by user
    var status: String = "New User", // user status
    var last_seen: String = "", // last seen time stamp, changes on each logout
    var isOnline: Boolean = true, // is online status
)

data class UserFriend(
    var uid: String = "", // unique user id given by the authenticator api
    var name: String = "", // user name
    var state: FriendState? = null
)

enum class FriendState {
    INREQUESTED, OUTREQUESTED, FRIEND, BLOCKED, BLOCKER
}

data class UserGroup(
    var gid: String = "", // unique group id given by the authenticator api
    var name: String = "" // group name
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