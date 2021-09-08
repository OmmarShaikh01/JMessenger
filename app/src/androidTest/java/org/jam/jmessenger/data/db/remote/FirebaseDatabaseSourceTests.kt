package org.jam.jmessenger.data.db.remote


import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is
import org.jam.jmessenger.data.db.entity.FriendState
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.entity.UserFriend
import org.jam.jmessenger.data.db.entity.UserInfo
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters


@RunWith(AndroidJUnit4::class)
@LargeTest  // need network
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class FirebaseDatabaseSourceTests {
    // START REGION: fixtures
    companion object {
        private var database: FirebaseDatabaseSource = FirebaseDatabaseSource()
        private var adminDatabase = Firebase.firestore
        private var auth: FirebaseAuth = Firebase.auth
        private var authUser = User()
        private var friendUser = User()

        // CONSTANTS
        private val TAG = "FirebaseDatabaseSourceTests"
        private var EMUHOST = "10.0.2.2"
        private var TESTEMAIL_1: String = "testuser11@gmail.com"
        private var TESTPASS_1: String = "1234567890"
        private var TESTNAME_1: String = "testuser_1"

        private var TESTEMAIL_2: String = "testuser22@gmail.com"
        private var TESTPASS_2: String = "1234567890"
        private var TESTNAME_2: String = "testuser_2"

        private var TESTEMAIL_3: String = "testuser33@gmail.com"
        private var TESTPASS_3: String = "1234567890"
        private var TESTNAME_3: String = "testuser_3"

        init{
            auth.useEmulator(EMUHOST, 9099)
            database.useEmulator(EMUHOST, 8080)
            adminDatabase.useEmulator(EMUHOST, 8080)
            Tasks.await(adminDatabase.clearPersistence())
        }

        @BeforeClass
        @JvmStatic
        fun createAuthTestUser() {
            auth.signOut()
            createUserFireBase(TESTEMAIL_1, TESTPASS_1, TESTNAME_1) // creation
            createUserFireBase(TESTEMAIL_3, TESTPASS_3, TESTNAME_3) // creation
            friendUser = createUserFireBase(TESTEMAIL_2, TESTPASS_2, TESTNAME_2)!! // creation

            Tasks.await(auth.signInWithEmailAndPassword(TESTEMAIL_1, TESTPASS_1)) // signup
            authUser = Tasks.await(database.loadUser(auth.uid.toString()))!!.toObject<User>()!!
            assertThat(auth.currentUser, Is(notNullValue()))
        }

        @AfterClass
        @JvmStatic
        fun deleteAuthTestUser() {
            auth.signOut()
            assertThat(auth.currentUser, Is(nullValue()))
        }

        fun createUserFireBase(email: String, password: String, name: String): User? {
            // checks if user exists
            val query = Tasks.await(adminDatabase.collection("users")
                .whereEqualTo("info.email", email)
                .limit(1).get()
            )

            // Yes
            if (query.documents.isNotEmpty()) {
                // Checks if the loaded data is a cache
                // True
                if (query.documents[0].metadata.isFromCache) {
                    Log.e(TAG, query.documents.toString())
                    throw Exception("Data From Cache")
                // False
                } else if(!query.documents[0].metadata.isFromCache) {
                    val docRef = query.documents[0]
                    return docRef!!.toObject<User>()
                // Default
                } else {
                    return null
                }
            // No
            } else if (query.documents.isEmpty()) {
                // Auth creats user
                val result = Tasks.await(auth.createUserWithEmailAndPassword(email, password))
                val createUser = User(UserInfo(
                    uid = result.user!!.uid,
                    name = name,
                    email = result.user!!.email.toString()
                ))
                // Firestore creates user
                Tasks.await(database.createNewUser(createUser))
                return createUser
            // Default
            } else {
                Log.e(TAG, query.documents.toString())
                throw Exception("User Not Created")
            }
        }
    }
    // END REGION

    @Test
    fun test_createUser_loadUser() {
        val testUser = User(UserInfo(
            uid = "TESTID",
            name = "TESTUSER",
            email = "TESTEMAIL@GMAIL.COM",
        ))
        Tasks.await(database.createNewUser(testUser))
        val docRef = Tasks.await(database.loadUser("TESTID"))
        val loadedUser = docRef!!.toObject<User>()
        assertThat("Loaded data is invalid and user creation failed", (testUser == loadedUser))
        Tasks.await(adminDatabase.collection("users").document("TESTID").delete())
    }

    @Test
    fun test_updateUser() {
        val testUser = User(UserInfo(
            uid = "TESTID",
            name = "TESTUSER",
            email = "TESTEMAIL@GMAIL.COM",
        ))
        Tasks.await(database.createNewUser(testUser))
        testUser.info.email = "TESTEMAILNEW@GMAIL.COM"
        Tasks.await(database.updateUserData(testUser))
        val docRef = Tasks.await(database.loadUser("TESTID"))
        val loadedUser = docRef!!.toObject<User>()
        assertThat("user info update questy failed to update email", ("TESTEMAILNEW@GMAIL.COM" == loadedUser!!.info.email))
        Tasks.await(adminDatabase.collection("users").document("TESTID").delete())
    }

    @Test
    fun test_searchFriend_validSearch() {
        val querySnap: QuerySnapshot = Tasks.await(database.searchFriendEmail(TESTEMAIL_1))
        for (docref in querySnap.documents) {
            val loadedUser = docref!!.toObject<User>()
            assertThat("Failed to find valid email ID", (loadedUser!!.info.email == TESTEMAIL_1))
        }
    }

    @Test
    fun test_searchFriend_invalidSearch() {
        val querySnap: QuerySnapshot = Tasks.await(database.searchFriendEmail("FalseEmail@gmail.com"))
        assertThat("invalid user was found", querySnap.documents.isEmpty())
    }

    @Test
    fun test_addFriend() {
        Tasks.await(database.addFriend(authUser, friendUser))
        val docRef = Tasks.await(database.loadUser(auth.uid!!.toString()))
        val loadedUser = docRef!!.toObject<User>()
        assertThat("Failed to add friend", (loadedUser!!.friends.containsKey(friendUser.info.uid)))
    }

    @Test
    fun test_acceptFriend() {
        // Setup
        Tasks.await(database.addFriend(authUser, friendUser))

        // TEST
        Tasks.await(database.acceptFriend(authUser, friendUser))
        var docRef = Tasks.await(database.loadUser(auth.uid!!.toString()))
        val loadedUser = docRef!!.toObject<User>()!!

        docRef = Tasks.await(database.loadUser(friendUser.info.uid))
        val loadedFriend = docRef!!.toObject<User>()!!

        assertThat(
            "User failed to accept friend request",
            (loadedUser.friends[loadedFriend.info.uid]!!.state!!.name == FriendState.FRIEND.name)
        )
        assertThat(
            "Friend failed to accept user friend request",
            (loadedFriend.friends[loadedUser.info.uid]!!.state!!.name == FriendState.FRIEND.name)
        )
    }

    @Test
    fun test_rejectFriend() {
        Tasks.await(database.addFriend(authUser, friendUser))

        // TEST
        Tasks.await(database.rejectFriend(authUser, friendUser))
        var docRef = Tasks.await(database.loadUser(auth.uid!!.toString()))
        val loadedUser = docRef!!.toObject<User>()!!

        docRef = Tasks.await(database.loadUser(friendUser.info.uid))
        val loadedFriend = docRef!!.toObject<User>()!!

        assertThat("User failed to reject friend request", (!loadedUser.friends.containsKey(loadedFriend.info.uid)))
        assertThat("friend failed to reject user friend request", (!loadedFriend.friends.containsKey(loadedUser.info.uid)))
    }

    @Test
    fun test_blockFriend() {
        Tasks.await(database.addFriend(authUser, friendUser))
        Tasks.await(database.acceptFriend(authUser, friendUser))

        // TEST
        Tasks.await(database.blockFriend(authUser, friendUser))
        var docRef = Tasks.await(database.loadUser(auth.uid!!.toString()))
        val loadedUser = docRef!!.toObject<User>()!!

        docRef = Tasks.await(database.loadUser(friendUser.info.uid))
        val loadedFriend = docRef!!.toObject<User>()!!

        assertThat("user failed to block friend request", (loadedUser.friends[loadedFriend.info.uid]!!.state!!.name == FriendState.BLOCKED.name))
        assertThat("friend failed to block user friend request", (loadedFriend.friends[loadedUser.info.uid]!!.state!!.name == FriendState.BLOCKER.name))
    }

    @Test
    fun test_unblockFriend() {
        Tasks.await(database.addFriend(authUser, friendUser))
        Tasks.await(database.acceptFriend(authUser, friendUser))

        // TEST
        Tasks.await(database.unblockFriend(authUser, friendUser))
        var docRef = Tasks.await(database.loadUser(auth.uid!!.toString()))
        val loadedUser = docRef!!.toObject<User>()!!

        docRef = Tasks.await(database.loadUser(friendUser.info.uid))
        val loadedFriend = docRef!!.toObject<User>()!!

        assertThat("User failed to unblock friend request", (loadedUser.friends[loadedFriend.info.uid]!!.state!!.name == FriendState.FRIEND.name))
        assertThat("User failed to unblock user friend request", (loadedFriend.friends[loadedUser.info.uid]!!.state!!.name == FriendState.FRIEND.name))
    }

    @Test
    fun test_loadUserFriends() {
        // TEST
        Tasks.await(database.addFriend(authUser, friendUser))
        var docRef = Tasks.await(database.loadUserFriends(auth.uid!!.toString()))
        val data = docRef.data
        var loadedUserFriends = docRef!!.toObject<HashMap<String, UserFriend>>()!!
        assertThat("Friend Loading Failed after addFriend", true)

        // TEST
        Tasks.await(database.acceptFriend(authUser, friendUser))
        docRef = Tasks.await(database.loadUserFriends(auth.uid!!.toString()))
        loadedUserFriends = docRef!!.toObject<HashMap<String, UserFriend>>()!!
        assertThat("Friend Loading Failed after acceptFriend", true)

        // TEST
        Tasks.await(database.blockFriend(authUser, friendUser))
        docRef = Tasks.await(database.loadUserFriends(auth.uid!!.toString()))
        loadedUserFriends = docRef!!.toObject<HashMap<String, UserFriend>>()!!
        assertThat("Friend Loading Failed after blockFriend", true)

        // TEST
        Tasks.await(database.unblockFriend(authUser, friendUser))
        docRef = Tasks.await(database.loadUserFriends(auth.uid!!.toString()))
        loadedUserFriends = docRef!!.toObject<HashMap<String, UserFriend>>()!!
        assertThat("Friend Loading Failed after unblockFriend", true)

        // TEST
        Tasks.await(database.rejectFriend(authUser, friendUser))
        docRef = Tasks.await(database.loadUserFriends(auth.uid!!.toString()))
        loadedUserFriends = docRef!!.toObject<HashMap<String, UserFriend>>()!!
        assertThat("Friend Loading Failed after rejectFriend", true)
    }
}