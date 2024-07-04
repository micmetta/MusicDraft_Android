package com.example.musicdraft.data.tables.user

/**
 * Stato dell'applicazione relativo agli utenti.
 *
 * @property users Lista degli utenti correntemente caricati nello stato.
 * @property email Email dell'utente corrente o dell'ultimo utente manipolato.
 * @property nickname Nickname dell'utente corrente o dell'ultimo utente manipolato.
 * @property password Password dell'utente corrente o dell'ultimo utente manipolato.
 * @property points Punteggio dell'utente corrente o dell'ultimo utente manipolato.
 */
data class UserState(
    val users: List<User> = emptyList(),
    val email: String = "",
    val nickname: String = "",
    val password: String = "",
    val points: Int = 0,
//    val sortType: SortType = SortType.EMAIL
)
