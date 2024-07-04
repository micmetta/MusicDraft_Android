package com.example.musicdraft.data.tables.matchmaking

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


/**
 * Dao che si preoccupa di eseguire le queries sulla tabella MatchSummaryConcluded presente nel database.
 *
 */
@Dao
interface MatchSummaryConcludedDao {

    /**
     * Inserisce un nuovo oggetto nella tabella MatchSummaryConcluded presente nel database.
     *
     * @param matchmaking Oggetto da inserire come nuova riga nella tabella.
     */
    @Insert
    suspend fun insertNewSummaryMatch(matchSummaryConcluded: MatchSummaryConcluded)

    /**
     * Elimina una riga della tabella MatchSummaryConcluded presente nel database.
     *
     * @param id Identificatore della riga da eliminare.
     */
    @Query("DELETE FROM MatchSummaryConcluded WHERE id = :id")
    suspend fun deleteSummaryMatch(id: Int)

    /**
     * Restituisce un flow di tipo List<MatchSummaryConcluded> al quale il repository chiamato MatchSummaryConcludedRepository è collegato per catturare
     * l'aggiornamento di tutte le partite giocate dall'utente loggato.
     *
     * @param nickname Nickname dell'utente per il quale si vuole eseguire l'aggiornamento.
     */
    @Query("SELECT * FROM MatchSummaryConcluded WHERE (nickname1 = :nickname OR nickname2 = :nickname) ORDER BY id DESC")
    fun getAllGamesConludedByNickname(nickname: String): Flow<List<MatchSummaryConcluded>>

}