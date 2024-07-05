package com.example.musicdraft.data.tables.user
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Entità che rappresenta un utente nel sistema.
 *
 * @property id Identificativo univoco dell'utente nel database (auto-generato).
 * @property email Indirizzo email dell'utente.
 * @property nickname Nickname dell'utente.
 * @property isOnline Flag che indica se l'utente è attualmente online.
 * @property points Punteggio o punti accumulati dall'utente.
 */
@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var email: String,
    var nickname: String,
    var isOnline: Boolean,
    var points: Int
)