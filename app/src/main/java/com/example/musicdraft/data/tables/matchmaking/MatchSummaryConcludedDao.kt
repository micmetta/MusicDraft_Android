package com.example.musicdraft.data.tables.matchmaking

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface MatchSummaryConcludedDao {

    @Insert
    suspend fun insertNewSummaryMatch(matchSummaryConcluded: MatchSummaryConcluded)

    @Query("DELETE FROM MatchSummaryConcluded WHERE id = :id")
    suspend fun deleteSummaryMatch(id: Int)

}