package com.example.musicdraft.data.tables.handleFriends

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HandleFriends(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email1: String,
    val email2: String,
    val state: String
)