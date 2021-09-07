package org.jam.jmessenger.data.db.remote


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

        // CONSTANTS
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
        }

        @BeforeClass
        @JvmStatic
        fun createAuthTestUser() {
            auth.signOut()
            createUserFireBase(TESTEMAIL_1, TESTPASS_1, TESTNAME_1) // creation
            createUserFireBase(TESTEMAIL_2, TESTPASS_2, TESTNAME_2) // creation
            createUserFireBase(TESTEMAIL_3, TESTPASS_3, TESTNAME_3) // creation
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
            val query = Tasks.await(adminDatabase.collection("users").whereEqualTo("info.email", email).limit(1).get())
            if (query.documents.isEmpty()) {
                val result = Tasks.await(auth.createUserWithEmailAndPassword(email, password))
                val createUser = User(UserInfo(
                    uid = result.user!!.uid,
                    name = name,
                    email = result.user!!.email.toString(),
                    status = "",
                    last_seen = "",
                    isOnline = true
                ))
                Tasks.await(database.createNewUser(createUser))
                return createUser
            }
            return null
        }
    }
    // END REGION

    @Test
    fun test_createUser_loadUser() {
        val testUser = User(UserInfo(
            uid = "TESTID",
            name = "TESTUSER",
            email = "TESTEMAIL@GMAIL.COM",
            status = "",
            last_seen = "",
            isOnline = true
        ))
        Tasks.await(database.createNewUser(testUser))
        val docRef = Tasks.await(database.loadUser("TESTID"))
        val loadedUser = docRef!!.toObject<User>()
        assertThat("Loaded data is valid", (testUser == loadedUser))
        Tasks.await(adminDatabase.collection("users").document("TESTID").delete())
    }

    @Test
    fun test_updateUser() {
        val testUser = User(UserInfo(
            uid = "TESTID",
            name = "TESTUSER",
            email = "TESTEMAIL@GMAIL.COM",
            status = "",
            last_seen = "",
            isOnline = true
        ))
        Tasks.await(database.createNewUser(testUser))
        testUser.info.email = "TESTEMAILNEW@GMAIL.COM"
        Tasks.await(database.updateUserData(testUser))
        val docRef = Tasks.await(database.loadUser("TESTID"))
        val loadedUser = docRef!!.toObject<User>()
        assertThat("Loaded data is valid", ("TESTEMAILNEW@GMAIL.COM" == loadedUser!!.info.email))
        Tasks.await(adminDatabase.collection("users").document("TESTID").delete())
    }

    @Test
    fun test_searchFriend_validSearch() {
        val querySnap: QuerySnapshot = Tasks.await(database.searchFriendEmail(TESTEMAIL_1))
        for (docref in querySnap.documents) {
            val loadedUser = docref!!.toObject<User>()
            assertThat("Found valid email ID", (loadedUser!!.info.email == TESTEMAIL_1))
        }
    }

    @Test
    fun test_searchFriend_invalidSearch() {
        val querySnap: QuerySnapshot = Tasks.await(database.searchFriendEmail("FalseEmail@gmail.com"))
        assertThat("invalid user was not found", querySnap.documents.isEmpty())
    }

    @Test
    fun test_addFriend() {
        val querySnap: QuerySnapshot = Tasks.await(database.searchFriendEmail(TESTEMAIL_2))
        for (docref in querySnap.documents) {
            val friendUser = docref!!.toObject<User>()
            if (friendUser != null) {
                Tasks.await(database.addFriend(authUser, friendUser))
                val docRef = Tasks.await(database.loadUser(auth.uid!!.toString()))
                val loadedUser = docRef!!.toObject<User>()
                assertThat("Loaded data is valid", (loadedUser!!.friends.containsKey(friendUser.info.uid)))
            }
        }
    }

    @Test
    fun test_acceptFriend() {
        // Setup
        val querySnap: QuerySnapshot = Tasks.await(database.searchFriendEmail(TESTEMAIL_2))
        for (docref in querySnap.documents) {
            val friendUser = docref!!.toObject<User>()
            if (friendUser != null) {
                Tasks.await(database.addFriend(authUser, friendUser))

                // TEST
                Tasks.await(database.acceptFriend(authUser, friendUser))
                var docRef = Tasks.await(database.loadUser(auth.uid!!.toString()))
                val loadedUser = docRef!!.toObject<User>()!!

                docRef = Tasks.await(database.loadUser(friendUser.info.uid))
                val loadedFriend = docRef!!.toObject<User>()!!

                assertThat("Loaded user data is valid", (loadedUser.friends[loadedFriend.info.uid]!!.state!!.name == FriendState.FRIEND.name))
                assertThat("Loaded friend data is valid", (loadedFriend.friends[loadedUser.info.uid]!!.state!!.name == FriendState.FRIEND.name))
            }
        }
    }

}