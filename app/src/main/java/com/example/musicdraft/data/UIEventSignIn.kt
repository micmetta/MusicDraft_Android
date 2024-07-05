package com.example.musicdraft.data

/**
 * Sealed class che rappresenta gli eventi UI per la schermata di accesso (signin).
 */
sealed class UIEventSignIn {
    /**
     * Evento generato quando l'utente cambia l'email.
     *
     * @property email Nuovo valore dell'email inserito dall'utente.
     */
    data class EmailChanged(val email: String) : UIEventSignIn()

    /**
     * Evento generato quando l'utente cambia la password.
     *
     * @property password Nuovo valore della password inserita dall'utente.
     */
    data class PasswordChanged(val password: String) : UIEventSignIn()

    /**
     * Evento generato quando l'utente clicca sul pulsante di login.
     */
    object LoginButtonClick : UIEventSignIn()

    /**
     * Evento generato per invalidare i dati di accesso.
     */
    object InvalidateDataSignIn : UIEventSignIn()

    /**
     * Evento generato quando l'utente clicca sul pulsante "Password dimenticata".
     */
    object forgotPassword: UIEventSignIn()

    /**
     * Evento generato quando l'utente vuole modificare il suo nickname.
     */
    object updateNickname: UIEventSignIn()
}