package com.example.musicdraft.data.tables.artisti

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Artisti(
    @PrimaryKey
    val id: String,
    val genere: String,
    val immagine: String,
    val nome: String,
    val popolarita: Int
){}