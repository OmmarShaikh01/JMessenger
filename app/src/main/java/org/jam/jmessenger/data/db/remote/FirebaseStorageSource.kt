package org.jam.jmessenger.data.db.remote

import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import org.jam.jmessenger.data.db.entity.Profile


class FirebaseStorageSource {
    val TAG = "FirebaseStorageSource"
    private val firebase_storage = Firebase.storage
    private val storage_ref = firebase_storage.reference

    // Create Functions ----------------------------------------------------------------------------
    fun uploadUserProfileImage(uid: String, profile: Profile): UploadTask {
        val ref = "profile_images/$uid.png"
        return storage_ref.child(ref).putBytes(profile.data)
    }
    // END REGION


    // Update Functions ----------------------------------------------------------------------------


    // Loader Functions ----------------------------------------------------------------------------
    fun loadUserProfileImage(uid: String): Task<ByteArray> {
        val ref = "profile_images/$uid.png"
        // val ref = "profile_images/User-Profile-Test.png"
        return storage_ref.child(ref).getBytes(1048576L)
    }
    // END REGION


    // Delete Functions ----------------------------------------------------------------------------
}