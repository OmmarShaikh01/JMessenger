package org.jam.jmessenger.data.db.entity

import androidx.lifecycle.LiveData
import androidx.room.*
import org.jetbrains.annotations.NotNull
import java.util.*


@DatabaseView(value = "SELECT user FROM (SELECT sender as user FROM messages UNION SELECT receiver as user FROM messages) GROUP BY user", viewName = "chatting_users")
data class RoomUser(
    @ColumnInfo(name = "user") var user: String = "",
)

@Dao
interface RoomUserDAO {
    @Query("SELECT * FROM chatting_users")
    fun getUserList(): LiveData<List<RoomUser>>

    @Query("SELECT * FROM chatting_users WHERE NOT(user LIKE :excludeUID)")
    fun getUserList(excludeUID: String): LiveData<List<RoomUser>>
}