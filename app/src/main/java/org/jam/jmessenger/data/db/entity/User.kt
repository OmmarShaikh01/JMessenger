package org.jam.jmessenger.data.db.entity

data class User(
    var info: UserInfo = UserInfo(), // user info
    var friends:  HashMap<String, UserFriend> = HashMap(), // hashmap for friends
    var groups:  HashMap<String, UserGroup> = HashMap() // hashmap for group
)

data class UserInfo(
    var uid: String = "", // unique user id given by the authenticator api
    var name: String = "", // name given by user
    var email: String = "", // email given by user
    var profile_img: String = "", // profile image url
    var status: String = "New User", // user status
    var last_seen: String = "", // last seen time stamp, changes on each logout
    var isOnline: Boolean = true, // is online status
)

data class UserFriend(
    var uid: String = "" // unique user id given by the authenticator api
)

data class UserGroup(
    var gid: String = ""  // unique group id given by the authenticator api
)
