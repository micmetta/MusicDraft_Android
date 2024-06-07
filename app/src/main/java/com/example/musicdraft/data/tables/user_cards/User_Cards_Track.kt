package com.example.musicdraft.data.tables.user_cards

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User_Cards_Track(
    @PrimaryKey
    val id_carta: String,
    val email: String
) {}