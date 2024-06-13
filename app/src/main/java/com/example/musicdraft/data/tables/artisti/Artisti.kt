package com.example.musicdraft.data.tables.artisti

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Artisti(
    @PrimaryKey(autoGenerate = true) val identifier: Int,
    val id: String,
    val genere: String,
    val immagine: String,
    val nome: String,
    val popolarita: Int,

){}