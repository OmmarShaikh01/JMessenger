package org.jam.jmessenger

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.entity.UserInfo
import org.jam.jmessenger.data.db.remote.FirebaseDatabaseSource
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters


@RunWith(AndroidJUnit4::class)
@LargeTest  // need network
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class InstrumentedTestsSetup {
    companion object {
        private var database: FirebaseDatabaseSource = FirebaseDatabaseSource()
        private var adminDatabase = Firebase.firestore
        private var auth: FirebaseAuth = Firebase.auth

        // CONSTANTS
        private val TAG = "InstrumentedTestsSetup"
        private var EMUHOST = "10.0.2.2"

        init{
            auth.useEmulator(EMUHOST, 9099)
            database.useEmulator(EMUHOST, 8080)
            adminDatabase.useEmulator(EMUHOST, 8080)
            Tasks.await(adminDatabase.clearPersistence())
        }

        @BeforeClass
        @JvmStatic
        fun runBeforeClass() {
            auth.signOut()
        }

        @AfterClass
        @JvmStatic
        fun runAfterClass() {
            auth.signOut()
            MatcherAssert.assertThat(auth.currentUser, Is(CoreMatchers.nullValue()))
        }

        fun createUserFireBase(email: String, password: String, name: String) {
            try {
                // Auth creats user
                val result = Tasks.await(auth.createUserWithEmailAndPassword(email, password))
                val createUser = User(UserInfo(
                    uid = result.user!!.uid,
                    name = name,
                    email = result.user!!.email.toString()
                ))
                // Firestore creates user
                Tasks.await(database.createNewUser(createUser))

                // Default
            } catch(e: Exception) {
                Log.e(TAG, e.toString())
            }
        }

        fun addFriend(userEmail: String, friendEmail: String){
            var user: User? = null
            var friend: User? = null
            for (doc in Tasks.await(database.searchFriendEmail(userEmail))) {
                user = doc.toObject()
            }
            for (doc in Tasks.await(database.searchFriendEmail(friendEmail))) {
                friend = doc.toObject()
            }
            if (user != null && friend != null) {
                Tasks.whenAll(database.addFriend(user, friend),
                    database.acceptFriend(user, friend))
            }
        }
    }

    @Test
    fun test1_createUsers() {
        for (index in 1..10) {
            Log.i(TAG, "Created User $index")
            createUserFireBase("testuser$index@gmail.com", "123456789", "testuser_$index")
        }
    }

    @Test
    fun test2_createFriendRelationships() {
        for (index in 1..10) {
            for (friendindex in 1..10) {
                if (friendindex == index) continue
                addFriend("testuser$index@gmail.com", "testuser$friendindex@gmail.com")
            }
            Log.i(TAG, "Created User Friend $index")
        }
    }
}