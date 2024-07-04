package com.example.musicdraft.data.tables.handleFriends

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Classe entità che rappresenta la gestione delle relazioni tra amici memorizzata nel database Room.
 *
 * @property id Chiave primaria generata automaticamente da Room per ogni voce della gestione relazioni.
 * @property email1 Email della prima persona coinvolta nella relazione.
 * @property email2 Email della seconda persona coinvolta nella relazione.
 * @property state Stato della relazione tra le due persone.
 */
@Entity
data class HandleFriends(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email1: String,
    val email2: String,
    val state: String
)