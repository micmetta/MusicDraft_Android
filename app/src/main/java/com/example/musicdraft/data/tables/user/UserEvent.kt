package com.example.musicdraft.data.tables.user

/**
 * Interfaccia sigillata che rappresenta gli eventi relativi alla gestione dell'utente.
 * Gli eventi possono essere utilizzati per comunicare azioni specifiche all'interno dell'applicazione.
 */
sealed interface UserEvent {
    /**
     * Evento per salvare l'utente.
     */
    object SaveUser: UserEvent

    /**
     * Evento per impostare l'email dell'utente.
     *
     * @param email Nuovo valore dell'email.
     */
    data class SetEmailUser(val email: String): UserEvent

    /**
     * Evento per impostare il nickname dell'utente.
     *
     * @param nickname Nuovo valore del nickname.
     */
    data class SetNicknamedUser(val nickname: String): UserEvent

    /**
     * Evento per impostare la password dell'utente.
     *
     * @param password Nuovo valore della password.
     */
    data class SetPasswordUser(val password: String): UserEvent

    /**
     * Evento per impostare i punti dell'utente.
     *
     * @param points Nuovo valore dei punti.
     */
    data class SetPointsUser(val points: String): UserEvent

    /**
     * Evento per eliminare l'utente.
     *
     * @param user Utente da eliminare.
     */
    data class DeleteUser(val user: User): UserEvent
}
