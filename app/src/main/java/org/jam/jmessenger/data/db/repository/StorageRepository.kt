package org.jam.jmessenger.data.db.repository

import android.util.Log
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.UploadTask.TaskSnapshot
import org.jam.jmessenger.data.db.Result
import org.jam.jmessenger.data.db.entity.Profile
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.remote.FirebaseDatabaseSource
import org.jam.jmessenger.data.db.remote.FirebaseStorageSource


/**
 * Storage repository
 *
 * @constructor Create empty Storage repository
 */
class StorageRepository {
    private val TAG = "StorageRepository"
    private val firebaseStorageSource = FirebaseStorageSource()
    private var firebaseDatabaseSource = FirebaseDatabaseSource()

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
        firebaseDatabaseSource.loadUser(uid).addOnSuccessListener { doc ->
            val user = doc?.toObject<User>()
            if (user != null) {
                firebaseStorageSource.loadURIImage(user.info.profileuri).addOnSuccessListener { bytes ->
                    val profile = Profile(data = bytes, url = user.info.profileuri)
                    if (bytes != null) {
                        infix.invoke(Result.Success(data = profile))
                    }
                }.addOnFailureListener { exception -> infix.invoke(Result.Error(exception)) }
            }
        }.addOnFailureListener { exception -> infix.invoke(Result.Error(exception)) }
    }

    /**
     * Load user profile image
     *
     * @param uid: String - User uid to get data from
     * @param infix: inline function to invoke on success
     * @receiver
     */
    fun loadURIImage(ref: String, infix: ((Result<Profile>) -> Unit)) {
        firebaseStorageSource.loadURIImage(ref).addOnSuccessListener { bytes ->
            val profile = Profile(data = bytes, url = ref)
            if (bytes != null) {
                infix.invoke(Result.Success(data = profile))
            }
        }.addOnFailureListener { exception -> infix.invoke(Result.Error(exception)) }
    }
    // END REGION
}