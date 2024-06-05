package com.example.musicdraft.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.musicdraft.data.tables.user.User
import com.example.musicdraft.data.tables.user.UserDao

@Database(
    entities = [User::class],
    version = 1
)
abstract class MusicDraftDatabase: RoomDatabase() {
//    //    abstract val dao: UserDao
//    //abstract fun dao(): UserDao?
    abstract fun userDao(): UserDao?

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

        ////////////////////////////////////////////////////////////////////////////////////
        // funzione che mi restituisce l'istanza del DB qualora già esistesse, altrimenti
        // se la crea e poi me la restituisce:
//        fun getDatabase(context: Context): MusicDraftDatabase? {
//            if (INSTANCE == null) {
//                synchronized(MusicDraftDatabase::class.java) {
//                    if (INSTANCE == null) {
//                        INSTANCE = androidx.room.Room.databaseBuilder(
//                            context.applicationContext,
//                            MusicDraftDatabase::class.java, "mobility_database"
//                        )
//                            // how to add a migration
//                            .addMigrations(
//
//                            )
//                            // Wipes and rebuilds instead of migrating if no Migration object.
//                            .fallbackToDestructiveMigration()
//                            .addCallback(roomDatabaseCallback)
//                            .build()
//                    }
//                }
//            }
//            return INSTANCE
//        }
//        ////////////////////////////////////////////////////////////////////////////////////
//
//
//        /**
//         * Override the onOpen method to populate the database.
//         * For this sample, we clear the database every time it is created or opened.
//         * If you want to populate the database only when the database is created for the 1st time,
//         * override MyRoomDatabase.Callback()#onCreate
//         */
//        private val roomDatabaseCallback: Callback =
//            object : Callback() {
//            }
//    }

//    abstract fun userDao(): UserDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: MusicDraftDatabase? = null
//
//        fun getInstance(context: Context): MusicDraftDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    MusicDraftDatabase::class.java,
//                    "music_draft_database"
//                ).build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }

}