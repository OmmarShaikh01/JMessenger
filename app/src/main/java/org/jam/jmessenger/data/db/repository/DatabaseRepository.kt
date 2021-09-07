package org.jam.jmessenger.data.db.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
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
    // END REGION


    // Update Functions ----------------------------------------------------------------------------
    fun addFriend(user: User, friend: User): Task<Void> {
        return firebaseDatabaseService.addFriend(user, friend)
    }

    // Delete Functions ----------------------------------------------------------------------------
}