package org.jam.jmessenger.data.db.remote

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import org.jam.jmessenger.data.db.entity.Profile


/**
 * Firebase storage source
 *
 * @constructor Create empty Firebase storage source
 */
class FirebaseStorageSource {
    val TAG = "FirebaseStorageSource"
    private val firebase_storage = FirebaseStorage.getInstance()
    private val storage_ref = firebase_storage.reference


    // Create Functions ----------------------------------------------------------------------------
    /**
     * Upload user profile image
     *
     * @param uid: String - users uid
     * @param profile: Profile - Users Profile object that holds the data
     * @return
     */
    fun uploadUserProfileImage(uid: String, profile: Profile): UploadTask {
        val ref = "profile_images/$uid.png"
        return storage_ref.child(ref).putBytes(profile.data)
    }
    // END REGION


    // Update Functions ----------------------------------------------------------------------------


    // Loader Functions ----------------------------------------------------------------------------
    /**
     * Load user profile image
     *
     * @param uid: users uid
     * @return
     */
    fun loadURIImage(ref: String): Task<ByteArray> {
        return storage_ref.child(ref).getBytes(1048576L)
    }
    // END REGION


    // Delete Functions ----------------------------------------------------------------------------

    /**
     * Delete user profile image
     *
     * @param uid: String - User uid
     */
    fun deleteUserProfileImage(uid: String) {
        val ref = "profile_images/$uid.png"
        storage_ref.child(ref).delete()
    }

    // Misc Functions ------------------------------------------------------------------------------
    /** TEST ONLY FUNCTION
     * Use emulator
     *
     * @param host: host ip address
     * @param port: host port to connect to
     */
    fun useEmulator(host: String, port: Int) {
        firebase_storage.useEmulator(host, port)
    }
}