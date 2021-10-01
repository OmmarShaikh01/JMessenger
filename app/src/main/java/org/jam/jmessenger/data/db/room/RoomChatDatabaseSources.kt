package org.jam.jmessenger.data.db.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.jam.jmessenger.data.db.entity.RoomMessage
import org.jam.jmessenger.data.db.entity.RoomMessageDAO
import org.jam.jmessenger.data.db.entity.RoomUser
import org.jam.jmessenger.data.db.entity.RoomUserDAO


@Database(entities = [RoomMessage::class], views = [RoomUser::class], version = 1)
abstract class RoomChatDatabaseSources: RoomDatabase() {
    abstract fun userDAO(): RoomUserDAO?
    abstract fun messageDao(): RoomMessageDAO?

    companion object {

        @Volatile private var INSTANCE: RoomChatDatabaseSources? = null

        fun getDatabase(context: Context, type: RoomChatDatabaseType): RoomChatDatabaseSources? {
            if (INSTANCE == null) {
                synchronized(RoomChatDatabaseSources::class.java) {
                    if ((INSTANCE == null) and (type == RoomChatDatabaseType.MEMORY)) {
                        INSTANCE = Room.inMemoryDatabaseBuilder(context.applicationContext,
                                RoomChatDatabaseSources::class.java)
                                .fallbackToDestructiveMigration()
                                .build()
                    }
                    if ((INSTANCE == null) and (type == RoomChatDatabaseType.STORAGE)) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                                RoomChatDatabaseSources::class.java, "chats_database.db")
                                .fallbackToDestructiveMigration()
                                .build()
                    }
                }
            }
            return INSTANCE
        }
    }
    enum class RoomChatDatabaseType {
        MEMORY, STORAGE
    }
}