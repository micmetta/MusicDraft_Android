package com.example.musicdraft.data.tables.artisti

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Rappresenta l'entità degli artisti nel database Room.
 *
 * @param identifier Identificatore primario auto-generato.
 * @param id Identificatore dell'artista.
 * @param genere Genere dell'artista.
 * @param immagine URL dell'immagine dell'artista.
 * @param nome Nome dell'artista.
 * @param popolarita Livello di popolarità dell'artista.
 */
@Entity
data class Artisti(
    @PrimaryKey(autoGenerate = true) val identifier: Int,
    val id: String,
    val genere: String,
    val immagine: String,
    val nome: String,
    val popolarita: Int,

){}