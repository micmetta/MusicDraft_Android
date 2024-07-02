package com.example.musicdraft.data.tables.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface UserDao {

    @Query("SELECT * FROM User")
    fun getAllUser(): Flow<List<User>>

    @Query("SELECT * FROM User WHERE email LIKE '%' || :email || '%'")
    fun getAllUsersFilterEmail(email: String): Flow<List<User>?>


    @Query("SELECT * FROM User WHERE nickname LIKE '%' || :nickname || '%'")
    fun getAllUsersFilterNickname(nickname: String): Flow<List<User>?>

    @Insert
    suspend fun insertUser(user: User)

    @Query("UPDATE User SET nickname = :newNickname WHERE nickname = :currentNickname")
    suspend fun updateNicknameUser(currentNickname: String, newNickname: String)

    /*
    - Query per aggiornare il campo 'isOnline' in base ai parametro 'email' e 'isOnline' (true o false) passati in input.
    */
    @Query("UPDATE User SET isOnline = :isOnline WHERE email = :email")
    suspend fun updateIsOnlineUser(email: String, isOnline: Boolean)

    @Query("UPDATE User SET points = :points WHERE email =:email")
    suspend fun updatePoints(points:Int, email:String)


    @Query("UPDATE User SET points = points + :addPoints WHERE email = :email")
    suspend fun addPoints(addPoints: Int, email: String)

    @Query("UPDATE User SET points = points - :subtractPoints WHERE email = :email")
    suspend fun subtractPoints(subtractPoints: Int, email: String)

    @Query("SELECT * FROM User WHERE email = :email")
    fun getUserByEmail(email: String): Flow<User?>

    @Query("SELECT * FROM User WHERE nickname = :nickname")
    fun getUserByNickname(nickname: String): Flow<User?>

    @Query("SELECT * FROM User WHERE nickname = :nickname")
    fun getOpponentByNickname(nickname: String): Flow<User?>

    @Query("SELECT * FROM User WHERE email IN (:emails)")
    fun getNicknamesByEmails(emails: List<String>): Flow<List<User>>


    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE email = :email)")
    suspend fun doesUserExistWithEmail(email: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE nickname = :nickname)")
    suspend fun doesUserExistWithNickname(nickname: String): Boolean

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