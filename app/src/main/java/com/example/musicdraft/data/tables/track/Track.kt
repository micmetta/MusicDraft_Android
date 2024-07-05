package com.example.musicdraft.data.tables.track

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Rappresenta un brano musicale nel sistema.
 *
 * @property identifier Identificatore univoco del brano (auto-generato).
 * @property id ID del brano, univoco all'interno del sistema musicale.
 * @property anno_pubblicazione Anno di pubblicazione del brano.
 * @property durata Durata del brano nel formato HH:MM:SS.
 * @property immagine URL dell'immagine associata al brano.
 * @property nome Nome del brano.
 * @property popolarita Livello di popolarità del brano, rappresentato da un valore intero.
 */
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