package org.jam.jmessenger.data.db.repository

import android.content.Context
import android.net.wifi.WifiManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.jam.jmessenger.data.db.entity.MessageState
import org.jam.jmessenger.data.db.entity.RoomMessage
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.remote.FirebaseDatabaseSource
import org.jam.jmessenger.data.db.room.RoomChatDatabaseSources
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
@LargeTest  // need network
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ChatsRepositoryTest{
    companion object {
        private lateinit var repository: ChatsRepository
        private var databaseSources = FirebaseDatabaseSource()
        private var adminDatabase = Firebase.firestore
        private var auth: FirebaseAuth = Firebase.auth
        val context = ApplicationProvider.getApplicationContext<Context>()

        private lateinit var sender: User
        private lateinit var reciever: User

        // CONSTANTS
        private val TAG = "InstrumentedTestsSetup"
        private var EMUHOST = "10.0.2.2"


        init{
            repository = ChatsRepository(context, RoomChatDatabaseSources.RoomChatDatabaseType.MEMORY)
            repository.useEmulator(EMUHOST, 8080)
            auth.useEmulator(EMUHOST, 9099)
            adminDatabase.useEmulator(EMUHOST, 8080)
            databaseSources.useEmulator(EMUHOST, 8080)
            Tasks.await(adminDatabase.clearPersistence())
        }

        @BeforeClass
        @JvmStatic
        fun runBeforeClass() {
            auth.signOut()
            sender = Tasks.await(databaseSources.searchFriendEmail("testuser1@gmail.com")).documents[0].toObject<User>()!!
            reciever = Tasks.await(databaseSources.searchFriendEmail("testuser2@gmail.com")).documents[0].toObject<User>()!!
        }

        @AfterClass
        @JvmStatic
        fun runAfterClass() {
            auth.signOut()
            repository.closeRoomDB()
        }
    }

    @Ignore("q")
    @Test
    fun test01_ChatSendMessage_seccessful() = runBlocking {
        Tasks.await(repository.sendMessage(sender.info.uid, reciever.info.uid, "Hello World"))
        delay(4000)
        val msg = repository.readMessages(sender.info.uid,reciever.info.uid, MessageState.SENT)
        repository.deleteMessageFromSenderTo(sender.info.uid, reciever.info.uid)
        assertTrue("return is empty: no message sent and read", msg.isNotEmpty())
    }

    @Ignore("q")
    @Test
    fun test02_ChatSendMessage_unseccessful() = runBlocking {
        Tasks.await(repository.sendMessage(sender.info.uid, reciever.info.uid, "Hello World"))
        delay(4000)
        val msg = repository.readMessages(sender.info.uid,reciever.info.uid, MessageState.PENDING)
        repository.deleteMessageFromSenderTo(sender.info.uid, reciever.info.uid)
        assertTrue("return is empty: no message sent and read", msg.isNotEmpty())
    }

    @Test
    fun test03_ChatResendPendingMessage() = runBlocking {
        val message = repository.createUserMessage(sender.info.uid, reciever.info.uid, "Hello World")
        message.msgState = MessageState.PENDING
        repository.roomChatsSource.messageDao()?.insertAll(message)
        repository.roomChatsSource.messageDao()?.insertAll(message)
        repository.roomChatsSource.messageDao()?.insertAll(message)
        Tasks.await(repository.resendPendingMessage())
        delay(10000)
        val msgS = repository.readMessages(sender.info.uid,reciever.info.uid, MessageState.SENT)
        repository.deleteMessageFromSenderTo(sender.info.uid, reciever.info.uid)
        assertTrue("return is empty: no message sent and read", msgS.isNotEmpty())
    }
}
