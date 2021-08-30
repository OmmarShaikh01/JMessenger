package org.jam.jmessenger.data.db.remote

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import org.jam.jmessenger.data.db.entity.User

class FirebaseDatabaseSource {
    val TAG = "FirebaseDatabaseSource"
    private val firebase_database = Firebase.firestore

    // Create Functions ----------------------------------------------------------------------------
    fun createNewUser(user: User): Task<Void> {
        return firebase_database.collection("users").document(user.info.uid).set(user)
    }

    // Update Functions ----------------------------------------------------------------------------

    // Loader Functions ----------------------------------------------------------------------------
    fun loadUser(uid: String): Task<DocumentSnapshot> {
        return firebase_database.collection("users").document(uid).get()
    }

    // Delete Functions ----------------------------------------------------------------------------
}
