package com.example.musicdraft.data

/**
 * Stato dell'interfaccia utente per la schermata di login.
 *
 * @property email Indirizzo email inserito dall'utente.
 * @property password Password inserita dall'utente.
 * @property emailError Indica se c'è un errore nell'indirizzo email.
 * @property passwordError Indica se c'è un errore nella password.
 */
data class LoginUIState (
    var email :String = "",
    var password :String = "",

    // mi permettono di capire se qualcuno di questi 4 campi non è valido (l'utente ha inserito dei dati errati)
    var emailError :Boolean = false,
    var passwordError :Boolean = false
)


