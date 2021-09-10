package org.jam.jmessenger.data.db.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.ktx.toObject
import org.jam.jmessenger.data.db.Result
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.entity.UserFriend
import org.jam.jmessenger.data.db.remote.FirebaseDatabaseSource

class DatabaseRepository {
    private val firebaseDatabaseService = FirebaseDatabaseSource()


    // Search Functions ----------------------------------------------------------------------------
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
    fun createNewUser(user: User): Task<Void> {
        return firebaseDatabaseService.createNewUser(user)
    }
    // END REGION


    // Loader Functions ----------------------------------------------------------------------------
    fun loadUser(uid: String, infix: ((Result<User>) -> Unit)) {
        firebaseDatabaseService.loadUser(uid)
            .addOnSuccessListener { document_snap ->
                val user = document_snap!!.toObject<User>()
                if (user != null) { infix.invoke(Result.Success(data = user)) }
            }
            .addOnFailureListener { exception -> infix.invoke(Result.Error(exception)) }
    }

    fun loadUserFriends(uid: String, infix: ((Result<HashMap<String, UserFriend>>) -> Unit)) {
        firebaseDatabaseService.loadUserFriends(uid)
            .addOnSuccessListener { document_snap ->
                val userFriends = document_snap!!.toObject<User>()!!.friends
                infix.invoke(Result.Success(data = userFriends))
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

    fun rejectFriend(user: User, friend: User): Task<Void> {
        return firebaseDatabaseService.rejectFriend(user, friend)
    }

    fun acceptFriend(user: User, friend: User): Task<Void> {
        return firebaseDatabaseService.acceptFriend(user, friend)
    }

    fun updateUserData(user: User, friend: User): Task<Void> {
        return firebaseDatabaseService.acceptFriend(user, friend)
    }
    // END REGION


    // Delete Functions ----------------------------------------------------------------------------

    // END REGION
}