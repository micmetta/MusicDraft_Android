package com.example.musicdraft.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.musicdraft.data.tables.handleFriends.HandleFriends
import com.example.musicdraft.data.tables.handleFriends.HandleFriendsDao
import com.example.musicdraft.data.tables.user.User
import com.example.musicdraft.data.tables.user.UserDao

@Database(
    entities = [User::class, HandleFriends::class],
    version = 1
)
abstract class MusicDraftDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao?
    abstract fun handleFriendsDao(): HandleFriendsDao?

    companion object {
        // marking the instance as volatile to ensure atomic access to the variable
        @Volatile
        private var INSTANCE: MusicDraftDatabase? = null


        fun getDatabase(context: Context): MusicDraftDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MusicDraftDatabase::class.java,
                    "musicDraftDB"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}