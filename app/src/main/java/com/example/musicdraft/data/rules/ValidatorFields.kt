package com.example.musicdraft.data.rules

/**
 * Questo object contiene tutti i controlli necessari per validare
 * i campi della creazione account e del login.
 * Questi controlli verranno eseguiti dal 'LoginViewModel' nel momento in cui l'utente
 * cliccherà sul button per confermare la registrazione.
 */
object ValidatorFields {

    /**
     * Valida il nickname.
     * Il nickname non deve essere null o vuoto e deve essere di almeno 6 caratteri.
     *
     * @param nickname Il nickname da validare.
     * @return ValidationResult che indica se il nickname è valido.
     */
    fun validateNickname(nickname: String) : ValidationResult{
        return ValidationResult(
            // Il nickname non deve essere Null o Empty e deve essere di almeno 6 caratteri
            (nickname.isNotEmpty() && nickname.length>=6) // restituisce true solamente se le due condizioni sono rispettate
        )
    }

    /**
     * Valida l'email.
     * L'email non deve essere null o vuota.
     *
     * @param email L'email da validare.
     * @return ValidationResult che indica se l'email è valida.
     */
    fun validateEmail(email: String): ValidationResult{
        return ValidationResult(
            (email.isNotEmpty()) // restituisce true solamente se la condizione è rispettata
        )
    }

    /**
     * Valida la password.
     * La password non deve essere null o vuota e deve essere di almeno 6 caratteri.
     *
     * @param password La password da validare.
     * @return ValidationResult che indica se la password è valida.
     */
    fun validatePassword(password: String): ValidationResult{
        return ValidationResult(
            (password.isNotEmpty() && password.length>=6) // restituisce true solamente se le due condizioni sono rispettate
        )
    }

    /**
     * Valida il consenso ai termini e condizioni.
     * Il valore deve essere true se l'utente ha accettato i termini e condizioni.
     *
     * @param statusValue Il valore del consenso.
     * @return ValidationResult che indica se il consenso è valido.
     */
    fun validatePrivacyAndPolicy(statusValue: Boolean): ValidationResult{
        return ValidationResult(
            statusValue // restituisce statusValue (che sarà 'true' se l'utente ha validato la checkbox dei termini d'uso altrimenti sarà false)
        )
    }
}
/**
 * Classe di risultato della validazione.
 *
 * @param status Indica se i dati inseriti sono validati o meno.
 */
data class ValidationResult(
    val status :Boolean = false // mi dirà se i dati inseriti sono validati o meno
)