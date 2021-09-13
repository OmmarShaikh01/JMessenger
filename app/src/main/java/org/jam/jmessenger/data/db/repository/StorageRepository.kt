package org.jam.jmessenger.data.db.repository

import com.google.firebase.storage.UploadTask
import org.jam.jmessenger.data.db.Result
import org.jam.jmessenger.data.db.entity.Profile
import org.jam.jmessenger.data.db.remote.FirebaseStorageSource


/**
 * Storage repository
 *
 * @constructor Create empty Storage repository
 */
class StorageRepository {
    private val firebaseStorageSource = FirebaseStorageSource()

    // Create Functions ----------------------------------------------------------------------------
    /**
     * Upload user profile image
     *
     * @param uid: String - User uid to upload the data to
     * @param profile: Profile - data to be uploaded
     * @return: UploadTask
     */
    fun uploadUserProfileImage(uid: String, profile: Profile): UploadTask {
        return firebaseStorageSource.uploadUserProfileImage(uid, profile)
    }
    // END REGION


    // Update Functions ----------------------------------------------------------------------------


    // Loader Functions ----------------------------------------------------------------------------
    /**
     * Load user profile image
     *
     * @param uid: String - User uid to get data from
     * @param infix: inline function to invoke on success
     * @receiver
     */
    fun loadUserProfileImage(uid: String, infix: ((Result<Profile>) -> Unit)) {
        firebaseStorageSource.loadUserProfileImage(uid).addOnSuccessListener { bytes ->
                val profile = Profile(data = bytes, url = "")
                if (bytes != null) { infix.invoke(Result.Success(data = profile)) }
            }
            .addOnFailureListener { exception -> infix.invoke(Result.Error(exception)) }
    }
    // END REGION
}