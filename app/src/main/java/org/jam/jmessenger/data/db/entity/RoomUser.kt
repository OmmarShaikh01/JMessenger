package org.jam.jmessenger.data.db.entity

import androidx.room.*
import org.jetbrains.annotations.NotNull
import java.util.*


@Entity(tableName = "users")
data class RoomUser(
    @PrimaryKey(autoGenerate = false) var uid: String = "",
    @ColumnInfo(name = "unreadCount") var unreadCount: Int = 0,
    @ColumnInfo(name = "lastMesseged") var lastMesseged: Long? = null,
    @ColumnInfo(name = "isMute") var isMute: Boolean = false
)

@Dao
interface RoomUserDAO {
    @Insert(entity = RoomUser::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg user: RoomUser)

    @Update(entity = RoomUser::class, onConflict = OnConflictStrategy.REPLACE)
    fun updateAll(vararg user: RoomUser)

    @Delete(entity = RoomUser::class)
    fun deleteUser(user: RoomUser)
}