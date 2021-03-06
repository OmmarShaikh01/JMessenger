package org.jam.jmessenger.data.db.remote

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import org.jam.jmessenger.data.db.entity.FriendState
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.entity.UserFriend


/**
 * Firebase database source
 *
 * @constructor Create empty Firebase database source
 */
@Suppress("PrivatePropertyName")
class FirebaseDatabaseSource {
    private val TAG = "FirebaseDatabaseSource"
    private val firebaseDatabase = FirebaseFirestore.getInstance()


    // Search Functions ----------------------------------------------------------------------------
    /**
     * Search friend email
     *
     * @param email: String - User email of the friend to query
     * @return: Task<QuerySnapshot>
     */
    fun searchFriendEmail(email: String): Task<QuerySnapshot> {
        return getUserCollection().whereEqualTo("info.email", email).limit(1).get()
    }
    // END REGION


    // Persist Functions ---------------------------------------------------------------------------

    /**
     * On user data change listener, adds a listener to the user document for change events
     *
     * @param uid: String - Users Uid
     * @return: DocumentReference
     */
    fun onUserDataChangeListener(uid: String): DocumentReference = getUserDocument(uid)
    // END REGION


    // Create Functions ----------------------------------------------------------------------------
    /**
     * Create new user
     *
     * @param user: User - Adds a new user document to the DB
     * @return: Task<Void>
     */
    fun createNewUser(user: User): Task<Void> = getUserDocument(user.info.uid).set(user)
    // END REGION


    // Update Functions ----------------------------------------------------------------------------
    /**
     * Add friend
     *
     * @param user: User - user entity
     * @param friend: User - user Friend entity
     * @return: Task<Void>
     */
    fun addFriend(user: User, friend: User): Task<Void> {
        val friendRequest = UserFriend(friend.info.uid, friend.info.name, FriendState.OUTREQUESTED, friend.info.profileuri)
        val userRequest = UserFriend(user.info.uid, user.info.name, FriendState.INREQUESTED, user.info.profileuri)

        // Update Outgoing request to the friends
        return Tasks.whenAll(
            getUserDocument(userRequest.uid).update("friends.${friendRequest.uid}", friendRequest),
            getUserDocument(friendRequest.uid).update("friends.${userRequest.uid}", userRequest)
        )
    }

    /**
     * Block friend
     *
     * @param user: User - user entity
     * @param friend: User - user Friend entity
     * @return: Task<Void>
     */
    fun blockFriend(user: User, friend: User): Task<Void>{
        val friendRequest = UserFriend(friend.info.uid, friend.info.name, FriendState.BLOCKED, friend.info.profileuri)
        val userRequest = UserFriend(user.info.uid, user.info.name, FriendState.BLOCKER, user.info.profileuri)

        // Update Outgoing request to the friends
        return Tasks.whenAll(
            getUserDocument(userRequest.uid).update("friends.${friendRequest.uid}", friendRequest),
            getUserDocument(friendRequest.uid).update("friends.${userRequest.uid}", userRequest)
        )
    }

    /**
     * Unblock friend
     *
     * @param user: User - user entity
     * @param friend: User - user Friend entity
     * @return: Task<Void>
     */
    fun unblockFriend(user: User, friend: User): Task<Void>{
        val friendRequest = UserFriend(friend.info.uid, friend.info.name, FriendState.FRIEND, friend.info.profileuri)
        val userRequest = UserFriend(user.info.uid, user.info.name, FriendState.FRIEND, user.info.profileuri)

        // Update Outgoing request to the friends
        return Tasks.whenAll(
            getUserDocument(userRequest.uid).update("friends.${friendRequest.uid}", friendRequest),
            getUserDocument(friendRequest.uid).update("friends.${userRequest.uid}", userRequest)
        )
    }

    fun muteFriend(user: User, friend: User, mute: Boolean = true): Task<Void> {
        return getUserCollection().document(user.info.uid).update("friends.${friend.info.uid}.isMute", mute)
    }

    /**
     * Accept friend
     *
     * @param user: User - user entity
     * @param friend: User - user Friend entity
     * @return: Task<Void>
     */

    fun acceptFriend(user: User, friend: User): Task<Void> {
        val friendRequest = UserFriend(friend.info.uid, friend.info.name, FriendState.FRIEND, friend.info.profileuri)
        val userRequest = UserFriend(user.info.uid, user.info.name, FriendState.FRIEND, user.info.profileuri)
        return Tasks.whenAll(
            getUserDocument(userRequest.uid).update("friends.${friendRequest.uid}", friendRequest),
            getUserDocument(friendRequest.uid).update("friends.${userRequest.uid}", userRequest)
        )
    }

    /**
     * Reject friend
     *
     * @param user: User - user entity
     * @param friend: User - user Friend entity
     * @return: Task<Void>
     */
    fun rejectFriend(user: User, friend: User): Task<Void> {
        return Tasks.whenAll(
            // deletes the friend entity from user
            getUserDocument(user.info.uid).update(
                hashMapOf<String, Any>(
                    "friends.${friend.info.uid}.uid" to FieldValue.delete(),
                    "friends.${friend.info.uid}.name" to FieldValue.delete(),
                    "friends.${friend.info.uid}.state" to FieldValue.delete(),
                    "friends.${friend.info.uid}" to FieldValue.delete(),
                )
            ),
            // deletes the user entity from friend
            getUserDocument(friend.info.uid).update(
                hashMapOf<String, Any>(
                    "friends.${user.info.uid}.uid" to FieldValue.delete(),
                    "friends.${user.info.uid}.name" to FieldValue.delete(),
                    "friends.${user.info.uid}.state" to FieldValue.delete(),
                    "friends.${user.info.uid}" to FieldValue.delete(),
                )
            )
        )
    }

    /**
     * Update user data
     *
     * @param user: User - user entity with updated data
     * @return: Task<Void>
     */
    fun updateUserData(user: User): Task<Void> = getUserDocument(user.info.uid).set(user)

    /**
     * Update user last seen
     *
     * @param uid: users uid
     * @return
     */
    fun updateUserLastSeen(uid: String): Task<DocumentSnapshot> {
        return getUserDocument(uid).get().addOnSuccessListener { doc ->
            val user = doc.toObject<User>()
            if (user != null) {
                user.info.last_seen = null
                user.info.isOnline = false
                getUserDocument(user.info.uid).set(user)
            }
        }
    }

    fun updateUserOnlineStatus(uid: String, isonline: Boolean): Task<DocumentSnapshot> {
        return getUserDocument(uid).get().addOnSuccessListener { doc ->
            val user = doc.toObject<User>()
            if (user != null) {
                user.info.isOnline = isonline
                getUserDocument(user.info.uid).set(user)
            }
        }
    }
    // END REGION


    // Loader Functions ----------------------------------------------------------------------------
    /**
     * Load user
     *
     * @param uid: user uid to load data for
     * @return: Task<DocumentSnapshot>
     */
    fun loadUser(uid: String): Task<DocumentSnapshot> = getUserDocument(uid).get()

    /**
     * Load user friends
     *
     * @param uid: user uid to load friends data for
     * @return: Task<DocumentSnapshot>
     */
    fun loadUserFriends(uid: String): Task<DocumentSnapshot> = getUserDocument(uid).get()

    fun getFriendListRef(userUID: String): DocumentReference {
        return firebaseDatabase.collection("users").document(userUID)
    }
    // END REGION

    
    // Delete Functions ----------------------------------------------------------------------------
    /**
     * Delete user
     *
     * @param uid: String - user uid to delete document refrence for
     * @return
     */
    fun deleteUser(uid: String): Task<Void> {
        return getUserDocument(uid).delete()
    }


    // END REGION


    // Misc Functions ------------------------------------------------------------------------------
    /** TEST ONLY FUNCTION
     * Use emudlator
     *
     * @param host: host ip address
     * @param port: host port to connect to
     */
    fun useEmulator(host: String, port: Int) {
        firebaseDatabase.useEmulator(host, port)
        firebaseDatabase.clearPersistence()
    }

    private fun getUserCollection(): CollectionReference {
        return firebaseDatabase.collection("users")
    }

    private fun getUserDocument(userUID: String): DocumentReference {
        return firebaseDatabase.collection("users").document(userUID)
    }
}
