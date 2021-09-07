package org.jam.jmessenger.data.db.remote

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.jam.jmessenger.data.db.entity.FriendState
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.entity.UserFriend


/*
* TODO:
* */
@Suppress("PrivatePropertyName")
class FirebaseDatabaseSource {
    private val TAG = "FirebaseDatabaseSource"
    private val firebaseDatabase = Firebase.firestore


    // Search Functions ----------------------------------------------------------------------------
    fun searchFriendEmail(email: String): Task<QuerySnapshot> {
        val query = firebaseDatabase.collection("users").whereEqualTo("info.email", email).limit(1)
        return query.get()
    }
    // END REGION


    // Persist Functions ---------------------------------------------------------------------------
    // TODO: add on edit update function
    // END REGION


    // Create Functions ----------------------------------------------------------------------------
    fun createNewUser(user: User): Task<Void> {
        return firebaseDatabase.collection("users").document(user.info.uid).set(user)
    }
    // END REGION


    // Update Functions ----------------------------------------------------------------------------
    fun addFriend(user: User, friend: User): Task<Void> {
        val friendRequest = UserFriend(friend.info.uid, friend.info.name, FriendState.OUTREQUESTED)
        val userRequest = UserFriend(user.info.uid, user.info.name, FriendState.INREQUESTED)

        // Update Outgoing request to the friends
        return Tasks.whenAll(firebaseDatabase.collection("users").document(userRequest.uid)
            .update("friends.${friendRequest.uid}", friendRequest),
            firebaseDatabase.collection("users").document(friendRequest.uid)
            .update("friends.${userRequest.uid}", userRequest)
        )
    }

    fun blockFriend(user: User, friend: UserFriend){ }

    fun acceptFriend(user: User, friend: User): Task<Void> {
        val friendRequest = UserFriend(friend.info.uid, friend.info.name, FriendState.FRIEND)
        val userRequest = UserFriend(user.info.uid, user.info.name, FriendState.FRIEND)
        return Tasks.whenAll(
            firebaseDatabase.collection("users").document(userRequest.uid)
            .update("friends.${friendRequest.uid}", friendRequest),
            firebaseDatabase.collection("users").document(friendRequest.uid)
            .update("friends.${userRequest.uid}", userRequest)
        )
    }

    fun rejectFriend(user: User, friend: UserFriend){ }

    fun updateUserData(user: User): Task<Void> {
        return firebaseDatabase.collection("users").document(user.info.uid).set(user)
    }

    // END REGION


    // Loader Functions ----------------------------------------------------------------------------
    fun loadUser(uid: String): Task<DocumentSnapshot> {
        return firebaseDatabase.collection("users").document(uid).get()
    }
    // END REGION

    
    // Delete Functions ----------------------------------------------------------------------------
    // TODO: Delete user
    // TODO: Delete user friends
    // END REGION


    // Misc Functions
    fun useEmulator(host: String, port: Int) {
        firebaseDatabase.useEmulator(host, port)
    }
}
