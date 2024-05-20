package com.example.musicdraft.data


// - Questa sarà la data class che conterrà i dati dei campi della registrazione
//   che l'utente inserirà.
// - Questa classe verrà istanziata all'interno del LoginViewModel.
data class RegistrationUIState (
    var nickName :String = "",
    var email :String = "",
    var password :String = ""
)