package com.example.musicdraft.data.tables.matchmaking

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


/**
 * Dao che si preoccupa di eseguire le queries sulla tabella Matchmaking presente nel database.
 *
 */
@Dao
interface MatchmakingDao {

    /**
     * Inserisce un nuovo oggetto nella tabella Matchmaking presente nel database.
     *
     * @param matchmaking Oggetto da inserire come nuova riga nella tabella.
     */
    @Insert
    suspend fun insertNewMatch(matchmaking: Matchmaking)

    /**
     * Elimina una riga della tabella Matchmaking presente nel database.
     *
     * @param id Identificatore della riga da eliminare.
     */
    @Query("DELETE FROM Matchmaking WHERE id = :id")
    suspend fun deleteMatch(id: Int)

    /**
     * Restituisce un flow di tipo List<Matchmaking> al quale il repository chiamato MatchmakingRepository è collegato per catturare
     * l'aggiornamento di tutte le partite (con un max di 100) nelle quali ci sono utenti che sono in attesa di trovare un avversario.
     *
     * @param nicknameUserCurrent Nickname dell'utente per il quale si vuole eseguire l'aggiornamento.
     */
    @Query("SELECT * FROM Matchmaking WHERE nickname1 != :nicknameUserCurrent AND nickname2 = '' LIMIT 100")
    fun getAllMatchesWaiting(nicknameUserCurrent:String): Flow<List<Matchmaking>>

    /**
     * Restituisce un flow di tipo List<Matchmaking> al quale il repository chiamato MatchmakingRepository è collegato per catturare
     * l'aggiornamento di tutte le partite nelle quali l'utente loggato è in attesa.
     *
     * @param nickname Nickname dell'utente per il quale si vuole eseguire l'aggiornamento.
     */
    @Query("SELECT * FROM Matchmaking WHERE (nickname1 = :nickname)")
    fun getAllMatchesWaitingByNickname(nickname: String): Flow<List<Matchmaking>>
}