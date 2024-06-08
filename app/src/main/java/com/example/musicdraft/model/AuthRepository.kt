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
    val allUsersFriendsOfCurrentUser: MutableStateFlow<List<User>?> = MutableStateFlow(users)



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



    suspend fun doesUserExistWithEmail(email: String): Boolean {
        return dao.doesUserExistWithEmail(email)
    }

    suspend fun doesUserExistWithNickname(nickname: String): Boolean {
        return dao.doesUserExistWithNickname(nickname)
    }
}