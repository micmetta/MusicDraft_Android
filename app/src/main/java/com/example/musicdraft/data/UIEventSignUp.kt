package com.example.musicdraft.data


/**
 * Sealed class che rappresenta gli eventi UI per la schermata di creazione dell'account ("SignUpScreen.kt").
 * Questa classe contiene l'insieme finito di eventi che l'utente può generare durante la registrazione.
 *
 * Nel momento in cui un UIEvent viene innescato, il "LoginViewModel" si preoccupa di gestirlo
 * modificando lo stato in base all'azione eseguita dall'utente, come l'inserimento di caratteri
 * nel campo nickname, email o password, o il clic sul pulsante di registrazione.
 */
sealed class UIEventSignUp {

    /**
     * Evento generato quando l'utente cambia il nickname.
     *
     * @property nickname Nuovo valore del nickname inserito dall'utente.
     */
    data class NicknameChanged(val nickname: String) : UIEventSignUp()

    /**
     * Evento generato quando l'utente cambia l'email.
     *
     * @property email Nuovo valore dell'email inserito dall'utente.
     */
    data class EmailChanged(val email: String) : UIEventSignUp()

    /**
     * Evento generato quando l'utente cambia la password.
     *
     * @property password Nuovo valore della password inserita dall'utente.
     */
    data class PasswordChanged(val password: String) : UIEventSignUp()

    /**
     * Evento generato quando l'utente clicca sulla checkbox della privacy policy.
     *
     * @property status Booleano che indica se l'utente ha selezionato o deselezionato la checkbox.
     */
    data class PrivacyPolicyCheckBoxClicked(val status: Boolean) : UIEventSignUp()

    /**
     * Evento generato quando l'utente clicca sul pulsante di registrazione.
     */
    object RegisterButtonClick : UIEventSignUp()

    /**
     * Evento generato per invalidare i dati di registrazione.
     */
    object InvalidateDataSignUp : UIEventSignUp()
}
