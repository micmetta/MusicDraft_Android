package com.example.musicdraft.data.tables.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


/**
 * Data Access Object (DAO) per gestire le operazioni CRUD sulla tabella degli utenti.
 */
@Dao
interface UserDao {

    /**
     * Ottiene tutti gli utenti presenti nel database.
     */
    @Query("SELECT * FROM User")
    fun getAllUser(): Flow<List<User>>

    /**
     * Ottiene gli utenti filtrati per indirizzo email.
     *
     * @param email Indirizzo email da cercare.
     */
    @Query("SELECT * FROM User WHERE email LIKE '%' || :email || '%'")
    fun getAllUsersFilterEmail(email: String): Flow<List<User>?>

    /**
     * Ottiene gli utenti filtrati per nickname.
     *
     * @param nickname Nickname da cercare.
     */
    @Query("SELECT * FROM User WHERE nickname LIKE '%' || :nickname || '%'")
    fun getAllUsersFilterNickname(nickname: String): Flow<List<User>?>

    /**
     * Inserisce un nuovo utente nel database.
     *
     * @param user Utente da inserire.
     */
    @Insert
    suspend fun insertUser(user: User)

    /**
     * Aggiorna lo stato online di un utente.
     *
     * @param email Indirizzo email dell'utente da aggiornare.
     * @param isOnline Nuovo stato di online (true o false).
     */
    @Query("UPDATE User SET isOnline = :isOnline WHERE email = :email")
    suspend fun updateIsOnlineUser(email: String, isOnline: Boolean)

    /**
     * Aggiorna i punti di un utente.
     *
     * @param points Nuovo valore dei punti.
     * @param email Indirizzo email dell'utente da aggiornare.
     */
    @Query("UPDATE User SET points = :points WHERE email =:email")
    suspend fun updatePoints(points:Int, email:String)

    /**
     * Ottiene un utente specifico per indirizzo email.
     *
     * @param email Indirizzo email dell'utente da cercare.
     */
    @Query("SELECT * FROM User WHERE email = :email")
    fun getUserByEmail(email: String): Flow<User?>

    /**
     * Ottiene una lista di utenti per una lista di indirizzi email.
     *
     * @param emails Lista di indirizzi email da cercare.
     */
    @Query("SELECT * FROM User WHERE email IN (:emails)")
    fun getNicknamesByEmails(emails: List<String>): Flow<List<User>>

    /**
     * Verifica se esiste un utente con un dato indirizzo email nel database.
     *
     * @param email Indirizzo email da cercare.
     * @return True se esiste un utente con l'indirizzo email specificato, altrimenti False.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE email = :email)")
    suspend fun doesUserExistWithEmail(email: String): Boolean

    /**
     * Verifica se esiste un utente con un dato nickname nel database.
     *
     * @param nickname Nickname da cercare.
     * @return True se esiste un utente con il nickname specificato, altrimenti False.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE nickname = :nickname)")
    suspend fun doesUserExistWithNickname(nickname: String): Boolean

    /**
     * Elimina un utente dal database.
     *
     * @param user Utente da eliminare.
     */
    @Delete
    suspend fun deleteUser(user: User)

    /**
     * Ottiene tutti gli utenti ordinati per indirizzo email in ordine ascendente.
     */
    @Query("SELECT * FROM user ORDER BY email ASC")
    fun getUserOrderedByEmail(): Flow<List<User>>

    /**
     * Ottiene tutti gli utenti ordinati per nickname in ordine ascendente.
     */
    @Query("SELECT * FROM user ORDER BY nickname ASC")
    fun getUserOrderedByNickname(): Flow<List<User>>

    /**
     * Ottiene tutti gli utenti ordinati per punti in ordine ascendente.
     */
    @Query("SELECT * FROM user ORDER BY points ASC")
    fun getUserOrderedByPoints(): Flow<List<User>>
}
