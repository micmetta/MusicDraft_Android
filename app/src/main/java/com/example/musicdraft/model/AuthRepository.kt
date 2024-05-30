package com.example.musicdraft.model

import com.example.musicdraft.utility.Resource
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

/*
- Questo è il model che si preoccupa di gestire in login con Google tramite l'utilizzo di Firebase.
- Firebase viene inizializzata all'avvio dell'applicazione. (la sua inizializzazione si trova nella classe "LoginMusicDraft.kt")
*/
class AuthRepository{
    private val firebaseAuth = Firebase.auth

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
}