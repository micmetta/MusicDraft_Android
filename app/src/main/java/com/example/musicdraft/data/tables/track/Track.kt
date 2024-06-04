package com.example.musicdraft.data.tables.track

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Track(
    @PrimaryKey
    val id: String,
    val anno_pubblicazione: String,
    val durata: String,
    val immagine: String,
    var nome: String,
    val popolarita: Int
) {
}