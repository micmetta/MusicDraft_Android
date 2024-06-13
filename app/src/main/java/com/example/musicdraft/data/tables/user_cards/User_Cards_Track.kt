package com.example.musicdraft.data.tables.user_cards

import androidx.room.Entity
import androidx.room.PrimaryKey

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