package com.example.musicdraft.data.tables.handleFriends

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HandleFriendsDao {

    @Insert
    suspend fun insertRequest(handleFriends: HandleFriends)

    // Quando una richiesta di amicizia da parte dell'utente con email2 viene
    // rifiutata (oppure anche dopo che questa era stata accettata uno dei due
    // utenti decide di cancellarla), allora tale richiesta verrà eliminata direttamente dal DB:
    @Query("DELETE FROM HandleFriends WHERE email1 = :email1 AND email2 = :email2")
    suspend fun deleteRequest(email1: String, email2: String)

    // restituisce la lista di utenti che hanno mandato richieste di amicizia
    // all'utente con email2 e quindi permette ad un utente di vedere queste
    // richieste nella schermata Friends.RequestsReceived:
    @Query("SELECT * FROM HandleFriends WHERE email2 = :email2 AND state = 'pending'")
    fun getRequestReceivedByUser(email2: String): Flow<List<HandleFriends>>

    // restituisce la lista di utenti a cui l'utente corrente ha mandato
    // la richiesta e quindi permette ad un utente di vedere a quali utenti
    // ha mandato lui stesso la richiesta nella schermata Friends.RequestsSent:
    @Query("SELECT * FROM HandleFriends WHERE email1 = :email1")
    fun getRequestSent(email1: String): List<HandleFriends>

    // Richiesta di amicizia accettata dall'utente con 'email2':
    @Query("UPDATE HandleFriends SET state = :state WHERE email1 = :email1 AND email2 = :email2")
    suspend fun acceptRequest(email1:String, email2: String, state: String)

    // Prendo tutti gli amici dell'utente con 'email_user':
    @Query("SELECT * FROM HandleFriends WHERE email1 = :email_user OR email2 = :email_user")
    fun getAllFriendsByUser(email_user: String): Flow<List<HandleFriends>>

}