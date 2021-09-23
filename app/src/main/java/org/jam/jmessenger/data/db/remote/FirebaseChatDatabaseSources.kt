package org.jam.jmessenger.data.db.remote

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseChatDatabaseSources {
    private val TAG = "FirebaseChatDatabaseSources"
    private val firebaseDatabase = FirebaseFirestore.getInstance()

    // Search Functions ----------------------------------------------------------------------------
    // Persist Functions ---------------------------------------------------------------------------
    // Create Functions ----------------------------------------------------------------------------
    fun sendMessage(uid: String, message: Int) {}
    // Update Functions ----------------------------------------------------------------------------
    // Loader Functions ----------------------------------------------------------------------------
    fun getPendingMessage(uid: String, message: Int) {}
    // Delete Functions ----------------------------------------------------------------------------
    // Misc Functions ------------------------------------------------------------------------------
    fun useEmulator(host: String, port: Int) {
        firebaseDatabase.useEmulator(host, port)
        firebaseDatabase.clearPersistence()
    }
}