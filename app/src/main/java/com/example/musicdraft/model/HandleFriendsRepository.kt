package com.example.musicdraft.model

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.musicdraft.data.tables.handleFriends.HandleFriends
import com.example.musicdraft.data.tables.handleFriends.HandleFriendsDao
import com.example.musicdraft.data.tables.user.User
import com.example.musicdraft.data.tables.user.UserDao
import com.example.musicdraft.viewModel.HandleFriendsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Repository per gestire le interazioni degli amici (richieste, accettazioni, rifiuti, cancellazioni) tra gli utenti.
 *
 * @property handleFriendsViewModel ViewModel che gestisce la logica dell'interfaccia utente per la gestione degli amici.
 * @property HandleFriendsdao Oggetto DAO per accedere alle operazioni di gestione degli amici nel database locale.
 * @property userDao Oggetto DAO per accedere alle operazioni di gestione degli utenti nel database locale.
 */
class HandleFriendsRepository(
    val handleFriendsViewModel: HandleFriendsViewModel,
    val HandleFriendsdao: HandleFriendsDao,
    val userDao: UserDao
) {

    val users: List<User>? = null
    val usersFilter: MutableStateFlow<List<User>?> = MutableStateFlow(users)
    val usersFilterbyNickname: MutableStateFlow<List<User>?> = MutableStateFlow(users)

    // Inizializzazione a null perché il valore sarà assegnato in un secondo momento
    val reqReceivedCurrentUser: MutableStateFlow<List<HandleFriends>?> = MutableStateFlow(null)
    val reqSentFromCurrentUser: MutableStateFlow<List<HandleFriends>?> = MutableStateFlow(null)
    val allFriendsCurrentUser: MutableStateFlow<List<HandleFriends>?> = MutableStateFlow(null)
    val allPendingRequest: MutableStateFlow<List<HandleFriends>?> = MutableStateFlow(null)

    /**
     * Inserisce una nuova richiesta di amicizia nel database locale.
     *
     * @param email1 Email del primo utente coinvolto nella richiesta di amicizia.
     * @param email2 Email del secondo utente coinvolto nella richiesta di amicizia.
     */
    fun insertNewRequest(email1: String, email2: String) {
        handleFriendsViewModel.viewModelScope.launch {
            val handleFriends = HandleFriends(
                email1 = email1,
                email2 = email2,
                state = "pending"
            )
            HandleFriendsdao.insertRequest(handleFriends)
        }
    }

    /**
     * Ottiene tutte le richieste di amicizia ricevute da un utente specifico dal database locale.
     *
     * @param email2 Email dell'utente di cui ottenere le richieste ricevute.
     */
    fun getRequestReceivedByUser(email2: String) {
        handleFriendsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val requestsReceived = HandleFriendsdao.getRequestReceivedByUser(email2)
                requestsReceived.collect { response ->
                    reqReceivedCurrentUser.value = response
                    Log.i("HandleFriendsRepository", "Richieste ricevute aggiornate: ${reqReceivedCurrentUser.value}")
                }
            }
        }
    }

    /**
     * Ottiene tutte le richieste di amicizia inviate da un utente specifico dal database locale.
     *
     * @param email1 Email dell'utente di cui ottenere le richieste inviate.
     */
    fun getRequestSent(email1: String) {
        handleFriendsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val requestsSent = HandleFriendsdao.getRequestSent(email1)
                requestsSent.collect { response ->
                    reqSentFromCurrentUser.value = response
                    Log.i("HandleFriendsRepository", "Richieste inviate aggiornate: ${reqSentFromCurrentUser.value}")
                }
            }
        }
    }

    /**
     * Ottiene tutti gli amici di un utente specifico dal database locale.
     *
     * @param email_user Email dell'utente di cui ottenere gli amici.
     */
    fun getAllFriendsByUser(email_user: String) {
        handleFriendsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val friends = HandleFriendsdao.getAllFriendsByUser(email_user)
                friends.collect { response ->
                    allFriendsCurrentUser.value = response
                    Log.i("HandleFriendsRepository", "Tutti gli amici dell'utente corrente: ${allFriendsCurrentUser.value}")
                }
            }
        }
    }

    /**
     * Ottiene tutte le richieste di amicizia in attesa per un utente specifico dal database locale.
     *
     * @param email_user Email dell'utente di cui ottenere le richieste in attesa.
     */
    fun getAllPendingRequestByUser(email_user: String) {
        handleFriendsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val pendingRequest = HandleFriendsdao.getAllPendingRequestByUser(email_user)
                pendingRequest.collect { response ->
                    allPendingRequest.value = response
                    Log.i("HandleFriendsRepository", "Richieste in attesa aggiornate: ${allPendingRequest.value}")
                }
            }
        }
    }

    /**
     * Filtra gli utenti per email e aggiorna lo stato `usersFilter`.
     *
     * @param filter Filtro per l'email degli utenti da visualizzare.
     */
    fun getUsersByFilter(filter: String) {
        handleFriendsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (filter.isEmpty()) {
                    usersFilter.value = emptyList()
                } else {
                    val usersFilternew = userDao.getAllUsersFilterEmail(filter)
                    usersFilternew.collect { users ->
                        usersFilter.value = users
                        Log.i("HandleFriendsRepository", "Utenti filtrati per email: ${usersFilter.value}")
                    }
                }
            }
        }
    }

    /**
     * Filtra gli utenti per nickname e aggiorna lo stato `usersFilterbyNickname`.
     *
     * @param filter Filtro per il nickname degli utenti da visualizzare.
     */
    fun getUsersByFilterNickname(filter: String) {
        handleFriendsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (filter.isEmpty()) {
                    usersFilterbyNickname.value = emptyList()
                } else {
                    val usersFilternew = userDao.getAllUsersFilterNickname(filter)
                    usersFilternew.collect { users ->
                        usersFilterbyNickname.value = users
                        Log.i("HandleFriendsRepository", "Utenti filtrati per nickname: ${usersFilterbyNickname.value}")
                    }
                }
            }
        }
    }

    /**
     * Accetta una richiesta di amicizia nel database locale.
     *
     * @param email1 Email del primo utente coinvolto nella richiesta di amicizia.
     * @param email2 Email del secondo utente coinvolto nella richiesta di amicizia.
     */
    fun acceptRequest(email1: String, email2: String) {
        handleFriendsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                HandleFriendsdao.acceptRequest(email1, email2, "accepted")
            }
        }
    }

    /**
     * Rifiuta una richiesta di amicizia nel database locale.
     *
     * @param email1 Email del primo utente coinvolto nella richiesta di amicizia.
     * @param email2 Email del secondo utente coinvolto nella richiesta di amicizia.
     */
    fun refuseRequest(email1: String, email2: String) {
        handleFriendsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                HandleFriendsdao.deleteRequest(email1, email2)
            }
        }
    }

    /**
     * Cancella una relazione di amicizia nel database locale.
     *
     * @param email1 Email del primo utente coinvolto nella relazione di amicizia.
     * @param email2 Email del secondo utente coinvolto nella relazione di amicizia.
     */
    fun deleteFriendship(email1: String, email2: String) {
        handleFriendsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                HandleFriendsdao.deleteFriendship(email1, email2)
            }
        }
    }
}