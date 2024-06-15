package com.example.musicdraft.data.tables.user
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var email: String,
    var nickname: String,
    var isOnline: Boolean,
    var points: Int
)