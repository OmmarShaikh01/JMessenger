package org.jam.jmessenger.data.db.remote

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.jam.jmessenger.data.db.entity.User

class FirebaseDatabaseSource {

    companion object {
        val firebase_database = FirebaseDatabase.getInstance()
    }

    private fun getReference(path: String): DatabaseReference {
        return firebase_database.reference.child(path)
    }

    // Create Functions ---------------------------------------------------q-------------------------
    fun createNewUser(user: User) {
        val uid = user.info.uid
        print("users/$uid")
    }

    // Update Functions ----------------------------------------------------------------------------
    fun updateUser() {
        TODO("update user info of the logged in user")
    }

    // Select Functions ----------------------------------------------------------------------------
    fun selectUser() {
        TODO("get user info and declare related attributes of the logged in user")
    }

    // Delete Functions ----------------------------------------------------------------------------
    fun deleteUser() {
        TODO("delete user info and declare related attributes of the user, Notify friends of the Deletion")
    }
}