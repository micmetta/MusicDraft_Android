package com.example.musicdraft.data.tables.matchmaking

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface MatchSummaryConcludedDao {

    @Insert
    suspend fun insertNewSummaryMatch(matchSummaryConcluded: MatchSummaryConcluded)

    @Query("DELETE FROM MatchSummaryConcluded WHERE id = :id")
    suspend fun deleteSummaryMatch(id: Int)

    @Query("SELECT * FROM MatchSummaryConcluded WHERE (nickname1 = :nickname OR nickname2 = :nickname) ORDER BY id DESC")
    fun getAllGamesConludedByNickname(nickname: String): Flow<List<MatchSummaryConcluded>>

}