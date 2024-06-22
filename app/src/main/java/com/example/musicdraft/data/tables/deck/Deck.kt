package com.example.musicdraft.data.tables.deck

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Deck (

    @PrimaryKey(autoGenerate = true) val identifier: Int,
    val nome_mazzo:String,
    val carte_associate: String,
    val email:String,
    val popolarita: Int


){
}