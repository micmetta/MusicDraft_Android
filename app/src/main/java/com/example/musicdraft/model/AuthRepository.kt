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

/*
- Questo è il model che si preoccupa di gestire in login con Google tramite l'utilizzo di Firebase.
- Firebase viene inizializzata all'avvio dell'applicazione. (la sua inizializzazione si trova nella classe "LoginMusicDraft.kt")
*/
class AuthRepository(val viewModel: LoginViewModel, val dao: UserDao){

    private val firebaseAuth = Firebase.auth
    //var users = dao.getAllUser()
    val users: List<User>? = null
    val userLoggedInfo: MutableStateFlow<User?> = MutableStateFlow(null)
    val friendRequestCard: MutableStateFlow<User?> = MutableStateFlow(null)
    val allUsersFriendsOfCurrentUser: MutableStateFlow<List<User>?> = MutableStateFlow(users)
    val allUsersrReceivedRequestByCurrentUser: MutableStateFlow<List<User>?> = MutableStateFlow(users)


    /*
    - Questa funzione verrà invocata dal loginViewModel nel momento in cui l'utente cliccherà sull'interfaccia
      sul button per eseguire il login con Google.
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

    fun insertNewUser(user: User){
        viewModel.viewModelScope.launch {
            dao.insertUser(user)
        }
    }

    fun updateNicknameUser(currentEmail: String, currentNickname: String, newNickname: String){
        viewModel.viewModelScope.launch {
            dao.updateNicknameUser(currentNickname, newNickname)
            // adesso riaggiorno le info dell'utente:
            dao.getUserByEmail(currentEmail)
        }
    }

    fun setisOnlineUser(email: String, isOnline: Boolean){
        viewModel.viewModelScope.launch {
            dao.updateIsOnlineUser(email, isOnline)
        }
    }

    fun getUserByEmail(email: String) {
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // mi faccio restituire dalla funzione "getUserByEmail(email)" del DAO
                // il flow:
                val userFlow = dao.getUserByEmail(email)
                userFlow.collect { user ->
                    userLoggedInfo.value = user
                    Log.i("AuthRepository", "info utente prese dal DB updated: ${userLoggedInfo.value}")
                }

            }
        }
    }


    fun getUserByNickname(nickname: String){
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // mi faccio restituire dalla funzione "getUserByEmail(email)" del DAO
                // il flow:
                val userFlow = dao.getUserByNickname(nickname)
                userFlow.collect { user ->
                    friendRequestCard.value = user
                    Log.i("AuthRepository", "info friend al quale verrà fatta l'offerta: ${friendRequestCard.value}")
                }

            }
        }
    }



    /*
    - Resetto i dati dell'utente corrente.
      (Questo metodo verrà invocato quando l'utente eseguirà il logout dall'applicazione.
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
                Log.i("AuthRepository", "Ho settato a false lo stato isOnline dell'utente con questa email: ${email}")
            }
        }
    }

    fun getAllUsersFriendsOfCurrentUser(emails: List<String>){
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val friends = dao.getNicknamesByEmails(emails)
                friends.collect { response ->
                    allUsersFriendsOfCurrentUser.value = response
                    Log.i("AuthRepository", "Tutti gli amici dell'utente corrente (tutte le loro info): ${allUsersFriendsOfCurrentUser.value}")
                }
            }
        }
    }

    fun getallUsersrReceivedRequestByCurrentUser(emails: List<String>){
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val friends = dao.getNicknamesByEmails(emails)
                friends.collect { response ->
                    allUsersrReceivedRequestByCurrentUser.value = response
                    Log.i("AuthRepository", "Tutti gli utenti che hanno ricevuto la richiesta dall'utente corrente (tutte le loro info): ${allUsersrReceivedRequestByCurrentUser.value}")
                }
            }
        }
    }


    suspend fun doesUserExistWithEmail(email: String): Boolean {
        return dao.doesUserExistWithEmail(email)
    }

    suspend fun doesUserExistWithNickname(nickname: String): Boolean {
        return dao.doesUserExistWithNickname(nickname)
    }
}