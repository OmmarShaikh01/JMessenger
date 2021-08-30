package org.jam.jmessenger.data.db.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObject
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.remote.FirebaseDatabaseSource
import org.jam.jmessenger.data.db.Result

class DatabaseRepository {
    private val firebaseDatabaseService = FirebaseDatabaseSource()

    // Create Functions ----------------------------------------------------------------------------
    fun createNewUser(user: User): Task<Void> {
        return firebaseDatabaseService.createNewUser(user)
    }

    // Loader Functions ----------------------------------------------------------------------------
    fun loadUser(uid: String, infix: ((Result<User>) -> Unit)) {
        firebaseDatabaseService.loadUser(uid)
            .addOnSuccessListener { document_snap ->
                val user = document_snap!!.toObject<User>()
                if (user != null) { infix.invoke(Result.Success(data = user)) }
            }
            .addOnFailureListener { exception -> infix.invoke(Result.Error(exception)) }
    }

    // Update Functions ----------------------------------------------------------------------------

    // Delete Functions ----------------------------------------------------------------------------
}