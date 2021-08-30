package org.jam.jmessenger.data.db.repository

import com.google.firebase.firestore.ktx.toObject
import org.jam.jmessenger.data.db.Result
import org.jam.jmessenger.data.db.entity.Profile
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.remote.FirebaseStorageSource

class StorageRepository {
    private val firebaseStorageSource = FirebaseStorageSource()

    // Create Functions ----------------------------------------------------------------------------
    fun uploadUserProfileImage(user: User, image: String) {
        firebaseStorageSource.uploadUserProfileImage(user, image)
    }

    // Update Functions ----------------------------------------------------------------------------

    // Loader Functions ----------------------------------------------------------------------------
    fun loadUserProfileImage(uid: String, infix: ((Result<Profile>) -> Unit)) {
        firebaseStorageSource.loadUserProfileImage(uid).addOnSuccessListener { bytes ->
                val profile = Profile(data = bytes, url = "")
                if (bytes != null) { infix.invoke(Result.Success(data = profile)) }
            }
            .addOnFailureListener { exception -> infix.invoke(Result.Error(exception)) }
    }
}