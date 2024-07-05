package com.example.musicdraft.login

import com.google.firebase.auth.AuthResult

/**
- Data class che contiene le variabili che verranno aggiornate in base
  a se il login viene effettuato con successo o meno.
 */
data class GoogleSignInState(
    val success: AuthResult? = null,
    val loading: Boolean = false,
    val error: String = ""
)
