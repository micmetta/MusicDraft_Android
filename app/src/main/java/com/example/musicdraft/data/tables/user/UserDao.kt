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

    /*
    - Query per aggiornare il campo 'nickname' in base ai parametro 'currentNickname' passati in input.
    */
    /**
     * Aggiorna lo stato online di un utente.
     *
     * @param nickname nickname dell'utente da aggiornare.
     * @param newNickname nuovo nickname utente.
     */
    @Query("UPDATE User SET nickname = :newNickname WHERE nickname = :currentNickname")
    suspend fun updateNicknameUser(currentNickname: String, newNickname: String)

    /*
    - Query per aggiornare il campo 'isOnline' in base ai parametro 'email' e 'isOnline' (true o false) passati in input.
    */
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
     * Aggiunge un certo numero di points ad un utente.
     *
     * @param email Indirizzo email dell'utente da cercare.
     * @param addPoints Points da aggiungere.
     */
    @Query("UPDATE User SET points = points + :addPoints WHERE email = :email")
    suspend fun addPoints(addPoints: Int, email: String)

    /**
     * Sottrae un certo numero di points ad un utente.
     *
     * @param email Indirizzo email dell'utente da cercare.
     * @param addPoints Points da sottrarre.
     */
    @Query("UPDATE User SET points = points - :subtractPoints WHERE email = :email")
    suspend fun subtractPoints(subtractPoints: Int, email: String)

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
    @Query("SELECT * FROM User WHERE nickname = :nickname")
    fun getUserByNickname(nickname: String): Flow<User?>

    /**
     * Ottiene le info sull'utente avversario.
     *
     * @param nickname nickname utente avversario.
     */
    @Query("SELECT * FROM User WHERE nickname = :nickname")
    fun getOpponentByNickname(nickname: String): Flow<User?>


    /**
     * Ottiene i nicknames di un insieme di utente
     * partendo dalle loro emails.
     *
     * @param emails Lista emails utenti per i quali si vuole ottenere il nickname.
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

    // con il Flow qui sotto, ogni volta che ci sarà un aggiornamento
    // nella tabella utenti (ad esempio viene inserito un nuovo utente,
    // il flow qui sotto emetterà una nuova lista di utenti che conterrà
    // anche l'utente appena inserito nel DB:
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
