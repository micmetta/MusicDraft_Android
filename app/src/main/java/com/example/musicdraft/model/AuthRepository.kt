package com.example.musicdraft.model

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.musicdraft.data.tables.user.User
import com.example.musicdraft.data.tables.user.UserDao
import com.example.musicdraft.utility.Resource
import com.example.musicdraft.viewModel.LoginViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Repository per gestire l'autenticazione utente tramite Firebase e interazioni con il database locale.
 *
 * @property viewModel ViewModel che gestisce la logica dell'interfaccia utente correlata all'autenticazione.
 * @property dao Oggetto DAO per accedere al database locale e gestire le operazioni CRUD sugli utenti.
 */
class AuthRepository(val viewModel: LoginViewModel, val dao: UserDao){

    private val firebaseAuth = Firebase.auth
    //var users = dao.getAllUser()
    val users: List<User>? = null
    val userLoggedInfo: MutableStateFlow<User?> = MutableStateFlow(null)
    val allUsersFriendsOfCurrentUser: MutableStateFlow<List<User>?> = MutableStateFlow(users)
    val allUsersrReceivedRequestByCurrentUser: MutableStateFlow<List<User>?> = MutableStateFlow(users)


    /**
     * Esegue l'accesso dell'utente utilizzando le credenziali di autenticazione fornite.
     *
     * @param credential Credenziali di autenticazione ottenute da Google SignIn.
     * @return Flow di risorsa che emette lo stato di caricamento, successo o errore dell'operazione di accesso.
     */
    fun googleSignIn(credential: AuthCredential): Flow<Resource<AuthResult>>{
        return flow{
            emit(Resource.Loading())
            val result = firebaseAuth.signInWithCredential(credential).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }
    /**
     * Inserisce un nuovo utente nel database locale.
     *
     * @param user Oggetto User da inserire nel database.
     */
    fun insertNewUser(user: User){
        viewModel.viewModelScope.launch {
            dao.insertUser(user)
        }
    }
    /**
     * Imposta lo stato online/offline di un utente nel database locale.
     *
     * @param email Email dell'utente di cui impostare lo stato online/offline.
     * @param isOnline Booleano che indica lo stato online/offline da impostare.
     */
    fun setisOnlineUser(email: String, isOnline: Boolean){
        viewModel.viewModelScope.launch {
            dao.updateIsOnlineUser(email, isOnline)
        }
    }
    /**
     * Ottiene le informazioni dell'utente dal database locale per l'email specificata.
     *
     * @param email Email dell'utente di cui ottenere le informazioni.
     */
    fun getUserByEmail(email: String) {
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // mi faccio restituire dalla funzione "getUserByEmail(email)" del DAO
                // il flow:
                val userFlow = dao.getUserByEmail(email)
                userFlow.collect { user ->
                    userLoggedInfo.value = user
                    Log.i("TG", "info utente prese dal DB updated: ${userLoggedInfo.value}")
                }

            }
        }
    }

    /**
     * Resetta le informazioni dell'utente correntemente loggato.
     *
     * @param email Email dell'utente da disconnettere (logout).
     */
    fun LogoutUserLoggedInfo(email: String){
        userLoggedInfo.value?.email = ""
        userLoggedInfo.value?.nickname = ""
        //userLoggedInfo.value?.password = ""
        userLoggedInfo.value?.isOnline = false
        userLoggedInfo.value?.points = 0

        // setto nel DB che l'utente che ha l'email passata in input è andato offline:
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO){
                dao.updateIsOnlineUser(email, false)
                Log.i("TG", "Ho settato a false lo stato isOnline dell'utente con questa email: ${email}")
            }
        }
    }
    /**
     * Ottiene tutti gli amici dell'utente correntemente loggato dal database locale.
     *
     * @param emails Lista di email degli amici di cui ottenere le informazioni.
     */
    fun getAllUsersFriendsOfCurrentUser(emails: List<String>){
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val friends = dao.getNicknamesByEmails(emails)
                friends.collect { response ->
                    allUsersFriendsOfCurrentUser.value = response
                    Log.i("TG", "Tutti gli amici dell'utente corrente (tutte le loro info): ${allUsersFriendsOfCurrentUser.value}")
                }
            }
        }
    }
    /**
     * Ottiene tutti gli utenti che hanno ricevuto richieste dall'utente correntemente loggato dal database locale.
     *
     * @param emails Lista di email degli utenti di cui ottenere le informazioni.
     */
    fun getallUsersrReceivedRequestByCurrentUser(emails: List<String>){
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val friends = dao.getNicknamesByEmails(emails)
                friends.collect { response ->
                    allUsersrReceivedRequestByCurrentUser.value = response
                    Log.i("TG", "Tutti gli utenti che hanno ricevuto la richiesta dall'utente corrente (tutte le loro info): ${allUsersrReceivedRequestByCurrentUser.value}")
                }
            }
        }
    }

    /**
     * Verifica se esiste un utente nel database locale con l'email specificata.
     *
     * @param email Email dell'utente da verificare.
     * @return true se esiste un utente con l'email specificata, altrimenti false.
     */
    suspend fun doesUserExistWithEmail(email: String): Boolean {
        return dao.doesUserExistWithEmail(email)
    }
    /**
     * Verifica se esiste un utente nel database locale con il nickname specificato.
     *
     * @param nickname Nickname dell'utente da verificare.
     * @return true se esiste un utente con il nickname specificato, altrimenti false.
     */
    suspend fun doesUserExistWithNickname(nickname: String): Boolean {
        return dao.doesUserExistWithNickname(nickname)
    }
}