package org.jam.jmessenger.data.db.entity


/**
 * User
 *
 * @property info
 * @property friends
 * @property groups
 * @constructor Create empty User
 */
data class User(
    var info: UserInfo = UserInfo(), // user info
    var friends: HashMap<String, UserFriend> = HashMap(), // hashmap for friends
    var groups: HashMap<String, UserGroup> = HashMap(), // hashmap for group
) {
    override fun toString(): String {
        return "${info.email}: ${info.name} "
    }
}


/**
 * User info
 *
 * @property uid: String - unique user id given by the authenticator api
 * @property name: String - name given by user
 * @property email: String - email given by user
 * @property status: String - user status
 * @property last_seen: String - last seen time stamp, changes on each logout
 * @property isOnline: String - is online status
 * @constructor Create empty User info
 */
data class UserInfo(
    var uid: String = "",
    var name: String = "",
    var email: String = "",
    var status: String = "New User",
    var last_seen: String = "",
    var isOnline: Boolean = true,
)

/**
 * User friend
 *
 * @property uid: String - unique user id given by the authenticator api
 * @property name: String - user name
 * @property state: FriendState - Relationship with user
 * @constructor Create empty User friend
 */
data class UserFriend(
    var uid: String = "", // unique user id given by the authenticator api
    var name: String = "", // user name
    var state: FriendState? = null
)

/**
 * Friend state
 *
 * @constructor Create empty Friend state
 */
enum class FriendState {
    INREQUESTED, // A Foregien User has requested to follow
    OUTREQUESTED, // User has requested to follow a friend
    FRIEND, // user and the foreign user have a friend relationship
    BLOCKED, // user has been blocked by a friend
    BLOCKER, // user is the blocker
    ACCEPTED, // user friend request has been accepted
    UNFRIENDED // user friend request has been declined
}

/**
 * User group
 *
 * @property gid: String - Group unique uid
 * @property name: String - Group name
 * @constructor Create empty User group
 */
data class UserGroup(
    var gid: String = "", // unique group id given by the authenticator api
    var name: String = "" // group name
)

/**
 * Profile
 *
 * @property url: String - firebase url that the data is stored at
 * @property data: ByteArray - Data fetched and stored into the object
 * @constructor Create empty Profile
 */
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