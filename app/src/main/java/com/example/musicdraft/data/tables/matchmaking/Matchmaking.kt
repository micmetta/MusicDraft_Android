package com.example.musicdraft.data.tables.matchmaking

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Tabella del database che conterrà tutte le informazioni riguardanti i due giocatori per il quale
 * il sistema ha trovato un match.
 *
 */
@Entity
data class Matchmaking(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var nickname1: String,
    var nickname2: String,
    var nameDeckU1: String,
    var nameDeckU2: String,
    var popularityDeckU1: Float,
    var popularityDeckU2: Float
)
