package com.example.musicdraft.data

data class LoginUIState (
    var email :String = "",
    var password :String = "",

    // mi permettono di capire se qualcuno di questi 4 campi non è valido (l'utente ha inserito dei dati errati)
    var emailError :Boolean = false,
    var passwordError :Boolean = false
)


