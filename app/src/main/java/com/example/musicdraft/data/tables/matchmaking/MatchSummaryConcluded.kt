package com.example.musicdraft.data.tables.matchmaking

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class MatchSummaryConcluded(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var dataGame: String,
    var nickWinner: String,
    var nickname1: String,
    var nickname2: String,
    var nameDeckU1: String,
    var nameDeckU2: String,
    var popularityDeckU1: Float,
    var popularityDeckU2: Float,
)
