package com.example.musicdraft.data


// - Questa sarà la data class che conterrà i dati dei campi della registrazione
//   che l'utente inserirà.
// - Questa classe viene istanziata all'interno del 'LoginViewModel' e viene utilizzata come tipo
//   per lo stato che verrà aggiornato dal LoginViewModel nel momento in cui l'utente inserirà
//   dei caratteri per uno di questi 3 campi.
data class RegistrationUIState (
    var nickName :String = "",
    var email :String = "",
    var password :String = "",

    // mi permettono di capire se qualcuno di questi 3 campi non è valido (l'utente ha inserito dei dati errati)
    var nicknameError :Boolean = false,
    var emailError :Boolean = false,
    var passwordError :Boolean = false

)