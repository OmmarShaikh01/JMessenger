package org.jam.jmessenger.data.db.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.ktx.toObject
import org.jam.jmessenger.data.db.Result
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.entity.UserFriend
import org.jam.jmessenger.data.db.remote.FirebaseDatabaseSource


/**
 * Database repository
 *
 * @constructor Create empty Database repository
 */
class DatabaseRepository {
    private val firebaseDatabaseService = FirebaseDatabaseSource()

    // Search Functions ----------------------------------------------------------------------------
    /**
     * Search friend email
     *
     * @param email: Email to search
     * @param infix
     * @receiver
     */
    fun searchFriendEmail(email: String, infix: ((Result<User>) -> Unit)) {
        firebaseDatabaseService.searchFriendEmail(email)
            .addOnSuccessListener { query_snap ->
                if (!query_snap.isEmpty) {
                    val user = query_snap.documents[0].toObject<User>()
                    if (user != null) {
                        infix.invoke(Result.Success(data = user))
                    }
                }
            }
            .addOnFailureListener { exception -> infix.invoke(Result.Error(exception)) }
    }
    // END REGION


    // Misc Functions ------------------------------------------------------------------------------
    /**
     * On user data change listener
     *
     * @param uid: String - user uid to attach listener to
     * @param infix
     * @receiver
     */
    fun onUserDataChangeListener(uid: String, infix: ((Result<User>) -> Unit)) {
        firebaseDatabaseService.onUserDataChangeListener(uid)
            .addSnapshotListener(MetadataChanges.EXCLUDE){ snap, e ->
                if (e != null) {
                    infix.invoke(Result.Error(e))
                    return@addSnapshotListener
                }
                if (snap != null && snap.exists()) {
                    val user = snap.toObject<User>()
                    if (user != null) {
                        if (snap.metadata.hasPendingWrites()) {
                            infix.invoke(Result.Success(data = user)) // Local Loader
                        } else {
                            infix.invoke(Result.Success(data = user)) // Server Loader
                        }
                    }
                }
            }
    }
    // END REGION


    // Create Functions ----------------------------------------------------------------------------
    /**
     * Create new user
     *
     * @param user: String - User to add to the dat
     * @return: Task<Void>
     */
    fun createNewUser(user: User): Task<Void> {
        return firebaseDatabaseService.createNewUser(user)
    }
    // END REGION


    // Loader Functions ----------------------------------------------------------------------------
    /**
     * Load user
     *
     * @param uid: String - User uid to load data for
     * @param infix
     * @receiver
     */
    fun loadUser(uid: String, infix: ((Result<User>) -> Unit)) {
        firebaseDatabaseService.loadUser(uid)
            .addOnSuccessListener { document_snap ->
                val user = document_snap!!.toObject<User>()
                if (user != null) { infix.invoke(Result.Success(data = user)) }
            }
            .addOnFailureListener { exception -> infix.invoke(Result.Error(exception)) }
    }

    /**
     * Load user friends
     *
     * @param uid: String - User uid to load friend data for
     * @param infix
     * @receiver
     */
    fun loadUserFriends(uid: String, infix: ((Result<HashMap<String, UserFriend>>) -> Unit)) {
        firebaseDatabaseService.loadUserFriends(uid)
            .addOnSuccessListener { document_snap ->
                val userFriends = document_snap!!.toObject<User>()!!.friends
                infix.invoke(Result.Success(data = userFriends))
            }
            .addOnFailureListener { exception -> infix.invoke(Result.Error(exception)) }
    }

    fun getFriendIsMute(uid: String, frienduid: String, infix: ((Result<Boolean>) -> Unit)) {
        firebaseDatabaseService.loadUserFriends(uid)
                .addOnSuccessListener { document_snap ->
                    val mute = document_snap?.toObject<User>()?.friends?.get(frienduid)?.isMute
                    if (mute != null) {
                        infix.invoke(Result.Success(data = mute))
                    }
                }
                .addOnFailureListener { exception -> infix.invoke(Result.Error(exception)) }
    }
    // END REGION


    // Update Functions ----------------------------------------------------------------------------
    fun addFriend(user: User, friend: User): Task<Void> {
        return firebaseDatabaseService.addFriend(user, friend)
    }

    fun blockFriend(user: User, friend: User): Task<Void> {
        return firebaseDatabaseService.blockFriend(user, friend)
    }

    fun unblockFriend(user: User, friend: User): Task<Void> {
        return firebaseDatabaseService.unblockFriend(user, friend)
    }

    fun muteFriend(user: User, friend: User, mute: Boolean = true): Task<Void> {
        return firebaseDatabaseService.muteFriend(user, friend, mute)
    }

    fun rejectFriend(user: User, friend: User): Task<Void> {
        return firebaseDatabaseService.rejectFriend(user, friend)
    }

    fun acceptFriend(user: User, friend: User): Task<Void> {
        return firebaseDatabaseService.acceptFriend(user, friend)
    }

    fun updateUserData(user: User, friend: User): Task<Void> {
        return firebaseDatabaseService.acceptFriend(user, friend)
    }

    fun updateUserLastSeen(uid: String): Task<DocumentSnapshot> {
        return firebaseDatabaseService.updateUserLastSeen(uid)
    }

    fun updateUserOnlineStatus(uid: String, isonline: Boolean): Task<DocumentSnapshot> {
        return firebaseDatabaseService.updateUserOnlineStatus(uid, isonline)
    }
    // END REGION


    // Delete Functions ----------------------------------------------------------------------------

    // END REGION
}