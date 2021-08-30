package org.jam.jmessenger.data.db.remote

import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.jam.jmessenger.data.db.entity.User


class FirebaseStorageSource {
    val TAG = "FirebaseStorageSource"
    private val firebase_storage = Firebase.storage
    private val storage_ref = firebase_storage.reference

    // Create Functions ----------------------------------------------------------------------------
    fun uploadUserProfileImage(user: User, image: String) {}

    // Update Functions ----------------------------------------------------------------------------

    // Loader Functions ----------------------------------------------------------------------------
    fun loadUserProfileImage(uid: String): Task<ByteArray> {
        // val ref = "profile_images/$uid.jpg"
        val ref = "profile_images/User-Profile-Test.png"
        return storage_ref.child(ref).getBytes(1048576L)
    }

    // Delete Functions ----------------------------------------------------------------------------
}