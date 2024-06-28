package com.example.musicdraft.data.tables.track

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Track(
    @PrimaryKey(autoGenerate = true) val identifier: Int,
    val id: String,
    val anno_pubblicazione: String,
    val durata: String,
    val immagine: String,
    var nome: String,
    val popolarita: Int,

) {
}