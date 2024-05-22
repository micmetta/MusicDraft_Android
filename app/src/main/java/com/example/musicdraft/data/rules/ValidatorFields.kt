package com.example.musicdraft.data.rules

// - Questo object contiene tutti i controlli necessari per validare
//   i campi della creazione account e del login.
// - Questi controlli verranno eseguiti dal 'LoginViewModel' nel momento in cui l'utente
//   cliccherà sul button per confermare la registrazione.
object ValidatorFields {

    fun validateNickname(nickname: String) : ValidationResult{
        return ValidationResult(
            // Il nickname non deve essere Null o Empty e deve essere di almeno 6 caratteri
            (!nickname.isNullOrEmpty() && nickname.length>=6) // restituisce true solamente se le due condizioni sono rispettate
        )
    }
    fun validateEmail(email: String): ValidationResult{
        return ValidationResult(
            (!email.isNullOrEmpty()) // restituisce true solamente se la condizione è rispettata
        )
    }
    fun validatePassword(password: String): ValidationResult{
        return ValidationResult(
            (!password.isNullOrEmpty() && password.length>=4) // restituisce true solamente se le due condizioni sono rispettate
        )
    }
}

data class ValidationResult(
    val status :Boolean = false // mi dirà se i dati inseriti sono validati o meno
)