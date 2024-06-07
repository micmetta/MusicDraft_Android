package com.example.musicdraft.data.tables.user_cards

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_cards_artisti")
data class User_Cards_Artisti(
    @PrimaryKey val id_carta: String,
    val email: String
 ){}