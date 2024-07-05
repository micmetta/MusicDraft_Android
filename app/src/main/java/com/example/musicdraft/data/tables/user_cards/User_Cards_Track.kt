package com.example.musicdraft.data.tables.user_cards

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Rappresenta l'entità di una carta di traccia musicale posseduta da un utente nel database.
 *
 * @property identifire Identificatore unico auto-generato per la carta.
 * @property id_carta ID univoco della carta di traccia musicale.
 * @property anno_pubblicazione Anno di pubblicazione della traccia musicale associata alla carta.
 * @property durata Durata della traccia musicale associata alla carta.
 * @property immagine URL dell'immagine della traccia musicale associata alla carta.
 * @property nome Nome della traccia musicale associata alla carta.
 * @property popolarita Livello di popolarità della traccia musicale associata alla carta.
 * @property email Email dell'utente che possiede la carta.
 * @property onMarket Flag che indica se la carta è sul mercato o meno.
 */
@Entity
data class User_Cards_Track(
    @PrimaryKey(autoGenerate = true) val identifire:Int,
    val id_carta: String,
    val anno_pubblicazione: String,
    val durata: String,
    val immagine: String,
    var nome: String,
    val popolarita: Int,
    val email: String,
    val onMarket: Boolean
) {}