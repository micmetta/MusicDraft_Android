package com.example.musicdraft.data.tables.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.musicdraft.data.tables.user.User
import kotlinx.coroutines.flow.Flow


@Dao
interface UserDao {

    @Query("SELECT * FROM User")
    fun getAllUser(): Flow<List<User>>

    @Insert
    suspend fun insertUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    // con il Flow qui sotto, ogni volta che ci sarà un aggiornamento
    // nella tabella utenti (ad esempio viene inserito un nuovo utente,
    // il flow qui sotto emetterà una nuova lista di utenti che conterrà
    // anche l'utente appena inserito nel DB:
    @Query("SELECT * FROM user ORDER BY email ASC")
    fun getUserOrderedByEmail(): Flow<List<User>>

    @Query("SELECT * FROM user ORDER BY nickname ASC")
    fun getUserOrderedByNickname(): Flow<List<User>>

    @Query("SELECT * FROM user ORDER BY points ASC")
    fun getUserOrderedByPoints(): Flow<List<User>>
}