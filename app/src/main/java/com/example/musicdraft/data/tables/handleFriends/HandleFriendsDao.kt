package com.example.musicdraft.data.tables.handleFriends

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Interfaccia DAO per gestire le operazioni di accesso ai dati della tabella HandleFriends nel database Room.
 * Contiene metodi per inserire, cancellare e recuperare dati relativi alle relazioni di amicizia tra utenti.
 */
@Dao
interface HandleFriendsDao {
    /**
     * Inserisce una nuova richiesta di amicizia nel database.
     *
     * @param handleFriends Oggetto HandleFriends da inserire nel database.
     */
    @Insert
    suspend fun insertRequest(handleFriends: HandleFriends)


    /**
     * Cancella una richiesta di amicizia specificata dal database.
     *
     *  Quando una richiesta di amicizia da parte dell'utente con email2 viene
     *  rifiutata, allora tale richiesta verrà eliminata direttamente dal DB; questa query verrà eseguita
     *  anche quando ad esempio l'utente con 'email1'
     *  decide di cancellare la richiesta di amicizia che aveva mandato all'utente con 'email2':
     *
     * @param email1 Email dell'utente che ha inviato la richiesta.
     * @param email2 Email dell'utente a cui è stata inviata la richiesta.
     */

    @Query("DELETE FROM HandleFriends WHERE email1 = :email1 AND email2 = :email2")
    suspend fun deleteRequest(email1: String, email2: String)

    /**
     * Cancella un'amicizia tra due utenti specificati dal database.
     *
     * Data una certa coppia che rappresenta un'amicizia tra
     * utente1-utente2, uno dei due utenti decide di rimuovere l'altro dalla sua lista amici e quindi
     * questa riga del DB viene cancellata:
     *
     * @param email1 Email del primo utente coinvolto nell'amicizia.
     * @param email2 Email del secondo utente coinvolto nell'amicizia.
     */
   
    @Query("DELETE FROM HandleFriends WHERE (email1 = :email1 AND email2 = :email2) OR (email2 = :email1 AND email1 = :email2)")
    suspend fun deleteFriendship(email1: String, email2: String)

    /**
     * Ottiene tutte le richieste di amicizia ricevute da un utente specificato, in stato 'pending'.
     *
     * restituisce la lista di utenti che hanno mandato richieste di amicizia
     * all'utente con email2 e quindi permette ad un utente di vedere queste
     * richieste nella schermata Friends.RequestsReceived:
     *      
     * @param email2 Email dell'utente a cui sono state inviate le richieste di amicizia.
     * @return Flow di lista di HandleFriends rappresentanti le richieste ricevute.
     */
     
    @Query("SELECT * FROM HandleFriends WHERE email2 = :email2 AND state = 'pending'")
    fun getRequestReceivedByUser(email2: String): Flow<List<HandleFriends>>

    /**
     * Ottiene tutte le richieste di amicizia inviate da un utente specificato, in stato 'pending'.
     *
     * restituisce la lista di utenti a cui l'utente corrente ha mandato
     * la richiesta e quindi permette ad un utente di vedere a quali utenti
     * ha mandato lui stesso la richiesta nella schermata Friends.RequestsSent per le quali non ha
     * ancora ricevuto risposta:
     *
     * @param email1 Email dell'utente che ha inviato le richieste di amicizia.
     * @return Flow di lista di HandleFriends rappresentanti le richieste inviate.
     */
    @Query("SELECT * FROM HandleFriends WHERE email1 = :email1 AND state = 'pending'")
    fun getRequestSent(email1: String): Flow<List<HandleFriends>>

    /**
     * Accetta una richiesta di amicizia specificata nel database, impostando lo stato come specificato.
     *
     * @param email1 Email dell'utente che ha ricevuto la richiesta di amicizia.
     * @param email2 Email dell'utente che ha inviato la richiesta di amicizia.
     * @param state Nuovo stato da assegnare alla relazione di amicizia ('accepted').
     */    @Query("UPDATE HandleFriends SET state = :state WHERE email1 = :email1 AND email2 = :email2")
    suspend fun acceptRequest(email1:String, email2: String, state: String)

    /**
     * Ottiene tutte le relazioni di amicizia attive di un utente specificato, in stato 'accepted'.
     *
     * @param email_user Email dell'utente di cui recuperare gli amici.
     * @return Flow di lista di HandleFriends rappresentanti gli amici dell'utente.
     */    @Query("SELECT * FROM HandleFriends WHERE (email1 = :email_user OR email2 = :email_user) AND state = 'accepted'")
    fun getAllFriendsByUser(email_user: String): Flow<List<HandleFriends>>

    /**
     * Ottiene tutte le richieste di amicizia pendenti di un utente specificato, in stato 'pending'.
     *
     * @param email_user Email dell'utente di cui recuperare le richieste pendenti.
     * @return Flow di lista di HandleFriends rappresentanti le richieste pendenti dell'utente.
     */    @Query("SELECT * FROM HandleFriends WHERE (email1 = :email_user OR email2 = :email_user) AND state = 'pending'")
    fun getAllPendingRequestByUser(email_user: String): Flow<List<HandleFriends>>

}