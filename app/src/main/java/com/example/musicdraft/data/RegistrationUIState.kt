package com.example.musicdraft.data

/**
 * Stato dell'interfaccia utente per la schermata di registrazione.
 *
 * Questa sarà la data class che conterrà i dati dei campi della registrazione
 * che l'utente inserirà.
 * Questa classe viene istanziata all'interno del 'LoginViewModel' e viene utilizzata come tipo
 * per lo stato che verrà aggiornato dal LoginViewModel nel momento in cui l'utente inserirà
 * dei caratteri per uno di questi 3 campi.
 *
 * @property nickName Nickname inserito dall'utente.
 * @property email Indirizzo email inserito dall'utente.
 * @property password Password inserita dall'utente.
 * @property privacyPolicyAccepted Indica se l'utente ha accettato la politica sulla privacy.
 * @property nicknameError Indica se c'è un errore nel nickname.
 * @property emailError Indica se c'è un errore nell'indirizzo email.
 * @property passwordError Indica se c'è un errore nella password.
 * @property privacyPolicyError Indica se c'è un errore nella accettazione della politica sulla privacy.

 */
// - Questa sarà la data class che conterrà i dati dei campi della registrazione
//   che l'utente inserirà.
// - Questa classe viene istanziata all'interno del 'LoginViewModel' e viene utilizzata come tipo
//   per lo stato che verrà aggiornato dal LoginViewModel nel momento in cui l'utente inserirà
//   dei caratteri per uno di questi 3 campi.
data class RegistrationUIState (
    var nickName :String = "",
    var email :String = "",
    var password :String = "",
    var privacyPolicyAccepted :Boolean = false,

    // mi permettono di capire se qualcuno di questi 4 campi non è valido (l'utente ha inserito dei dati errati)
    var nicknameError :Boolean = false,
    var emailError :Boolean = false,
    var passwordError :Boolean = false,
    var privacyPolicyError :Boolean = false

)