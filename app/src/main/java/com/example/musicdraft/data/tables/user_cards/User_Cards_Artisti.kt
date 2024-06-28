package com.example.musicdraft.data.tables.user_cards

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_cards_artisti")
data class User_Cards_Artisti(
    @PrimaryKey(autoGenerate = true) val identifier:Int,
    val id_carta: String,
    val genere: String,
    val immagine: String,
    val nome: String,
    val popolarita: Int,
    val email: String,
    val onMarket: Boolean
 ){}