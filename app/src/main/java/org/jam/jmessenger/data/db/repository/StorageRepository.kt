package org.jam.jmessenger.data.db.repository

import com.google.firebase.storage.UploadTask
import org.jam.jmessenger.data.db.Result
import org.jam.jmessenger.data.db.entity.Profile
import org.jam.jmessenger.data.db.remote.FirebaseStorageSource

class StorageRepository {
    private val firebaseStorageSource = FirebaseStorageSource()

    // Create Functions ----------------------------------------------------------------------------
    fun uploadUserProfileImage(uid: String, profile: Profile): UploadTask {
        return firebaseStorageSource.uploadUserProfileImage(uid, profile)
    }
    // END REGION


    // Update Functions ----------------------------------------------------------------------------


    // Loader Functions ----------------------------------------------------------------------------
    fun loadUserProfileImage(uid: String, infix: ((Result<Profile>) -> Unit)) {
        firebaseStorageSource.loadUserProfileImage(uid).addOnSuccessListener { bytes ->
                val profile = Profile(data = bytes, url = "")
                if (bytes != null) { infix.invoke(Result.Success(data = profile)) }
            }
            .addOnFailureListener { exception -> infix.invoke(Result.Error(exception)) }
    }
    // END REGION
}