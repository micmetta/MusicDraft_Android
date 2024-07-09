package com.example.musicdraft.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.musicdraft.database.MusicDraftDatabase
import com.example.musicdraft.model.HandleFriendsRepository

/**
 * ViewModel per gestire le operazioni di amicizia.
 *
 * @property application L'istanza dell'applicazione.
 */
class HandleFriendsViewModel(application: Application) : AndroidViewModel(application) {

    // Istanziazione del repository 'handleFriendsRepository' e DAO 'handleFriendsDao' e 'userDao'
    private val database = MusicDraftDatabase.getDatabase(application)
    private val handleFriendsDao = database.handleFriendsDao()
    private val userDao = database.userDao()

    // Istanzia il repository utilizzando i DAO
    private val handleFriendsRepository: HandleFriendsRepository = HandleFriendsRepository(this, handleFriendsDao!!, userDao!!)

    // Variabili per il filtraggio degli utenti
    var usersFilter = handleFriendsRepository.usersFilter
    var usersFilterbyNickname = handleFriendsRepository.usersFilterbyNickname

    // Variabili per le richieste di amicizia ricevute dall'utente corrente
    var reqReceivedCurrentUser = handleFriendsRepository.reqReceivedCurrentUser

    // Variabili per le richieste di amicizia inviate dall'utente corrente
    var reqSentFromCurrentUser = handleFriendsRepository.reqSentFromCurrentUser

    // Variabili per gli amici dell'utente corrente
    var allFriendsCurrentUser = handleFriendsRepository.allFriendsCurrentUser

    // Variabili per tutte le richieste di amicizia in sospeso
    var allPendingRequest = handleFriendsRepository.allPendingRequest

    // ViewModel per la gestione del login
    private val loginViewModel: LoginViewModel = LoginViewModel(application)

    /**
     * Inserisce una nuova richiesta di amicizia.
     *
     * @param email1 Email dell'utente che invia la richiesta.
     * @param email2 Email dell'utente che riceve la richiesta.
     */
    fun insertNewRequest(email1: String, email2: String) {
        handleFriendsRepository.insertNewRequest(email1, email2)
    }

    /**
     * Cerca un utente nel database utilizzando un filtro email.
     *
     * @param filter Il filtro per la ricerca.
     */
    fun onSearchTextChange(filter: String) {
        handleFriendsRepository.getUsersByFilter(filter)
    }

    /**
     * Cerca un utente nel database utilizzando un filtro nickname.
     *
     * @param filter Il filtro per la ricerca.
     */
    fun onSearchTextChangeByNickname(filter: String) {
        handleFriendsRepository.getUsersByFilterNickname(filter)
    }

    /**
     * Ottiene le richieste di amicizia ricevute da un utente.
     *
     * @param email2 Email dell'utente che riceve le richieste.
     */
    fun getRequestReceivedByUser(email2: String) {
        handleFriendsRepository.getRequestReceivedByUser(email2)
    }

    /**
     * Ottiene le richieste di amicizia inviate da un utente.
     *
     * @param email1 Email dell'utente che invia le richieste.
     */
    fun getRequestSent(email1: String) {
        handleFriendsRepository.getRequestSent(email1)
    }

    /**
     * Ottiene tutti gli amici di un utente.
     *
     * @param email_user Email dell'utente di cui ottenere gli amici.
     */
    fun getAllFriendsByUser(email_user: String) {
        handleFriendsRepository.getAllFriendsByUser(email_user)
    }

    /**
     * Ottiene tutte le richieste di amicizia in sospeso per un utente.
     *
     * @param email_user Email dell'utente di cui ottenere le richieste in sospeso.
     */
    fun getAllPendingRequestByUser(email_user: String) {
        handleFriendsRepository.getAllPendingRequestByUser(email_user)
    }

    /**
     * Accetta una richiesta di amicizia.
     *
     * @param email1 Email dell'utente che ha inviato la richiesta.
     * @param email2 Email dell'utente che accetta la richiesta.
     */
    fun acceptRequest(email1: String, email2: String) {
        handleFriendsRepository.acceptRequest(email1, email2)
    }

    /**
     * Rifiuta una richiesta di amicizia.
     *
     * @param email1 Email dell'utente che ha inviato la richiesta.
     * @param email2 Email dell'utente che rifiuta la richiesta.
     */
    fun refuseRequest(email1: String, email2: String) {
        handleFriendsRepository.refuseRequest(email1, email2)
    }

    /**
     * Elimina un'amicizia.
     *
     * @param email1 Email del primo utente.
     * @param email2 Email del secondo utente.
     */
    fun deleteFriendship(email1: String, email2: String) {
        handleFriendsRepository.deleteFriendship(email1, email2)
        handleFriendsRepository.deleteFriendship(email2, email1)
    }
}
