package com.example.musicdraft.data.tables.user
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val nickname: String,
    val password: String,
    val isOnline: Boolean,
    val points: Int
)
