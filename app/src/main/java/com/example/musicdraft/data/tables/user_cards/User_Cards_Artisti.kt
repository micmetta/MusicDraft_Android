package com.example.musicdraft.data.tables.user_cards

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Rappresenta l'entità di una carta di artista posseduta da un utente nel database.
 *
 * @property identifier Identificatore unico auto-generato per la carta.
 * @property id_carta ID univoco della carta di artista.
 * @property genere Genere musicale dell'artista associato alla carta.
 * @property immagine URL dell'immagine dell'artista associato alla carta.
 * @property nome Nome dell'artista associato alla carta.
 * @property popolarita Livello di popolarità dell'artista associato alla carta.
 * @property email Email dell'utente che possiede la carta.
 * @property onMarket Flag che indica se la carta è sul mercato o meno.
 */
@Entity(tableName = "user_cards_artisti")
data class User_Cards_Artisti(
    @PrimaryKey(autoGenerate = true) val identifier: Int,
    val id_carta: String,
    val genere: String,
    val immagine: String,
    val nome: String,
    val popolarita: Int,
    val email: String,
    val onMarket: Boolean
)