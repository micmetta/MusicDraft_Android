package com.example.musicdraft.data.tables.deck

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Classe entità che rappresenta un mazzo di carte memorizzato nel database Room.
 *
 * @property identifier Chiave primaria generata automaticamente da Room per ogni voce del mazzo.
 * @property nome_mazzo Nome del mazzo.
 * @property carte_associate Stringa separata da virgole che rappresenta le carte associate.
 * @property email Email dell'utente associato al mazzo.
 * @property popolarita Valutazione di popolarità del mazzo.
 */
@Entity
data class Deck (

    @PrimaryKey(autoGenerate = true) val identifier: Int,
    val nome_mazzo:String,
    val carte_associate: String,
    val email:String,
    val popolarita: Float


){
}