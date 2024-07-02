package com.example.musicdraft.data.tables.matchmaking

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchmakingDao {
    @Insert
    suspend fun insertNewMatch(matchmaking: Matchmaking)

    @Query("DELETE FROM Matchmaking WHERE id = :id")
    suspend fun deleteMatch(id: Int)

    /*
    - Mi restituisce tutte le partite dove non è stato trovato ancora il secondo giocatore e inoltre la forza massima del mazzo del primo
      giocatore è compresa tra: (pop-50 < pop <= pop+50)
    */
//    @Query("SELECT * FROM Matchmaking WHERE nickname1 != :nicknameUserCurrent AND nickname2 = '' AND popularityDeckU1 > :pop - 50")
//    fun getAllMatchesWithARangeOfPop_1(nicknameUserCurrent:String, pop: Float): Flow<List<Matchmaking>>

//    @Query("SELECT * FROM Matchmaking WHERE nickname1 != :nicknameUserCurrent AND popularityDeckU1 <= :pop + 50")
//    fun getAllMatchesWithARangeOfPop_2(nicknameUserCurrent:String, pop: Float): Flow<List<Matchmaking>>

    @Query("SELECT * FROM Matchmaking WHERE nickname1 != :nicknameUserCurrent AND nickname2 = '' LIMIT 100")
    fun getAllMatchesWaiting(nicknameUserCurrent:String): Flow<List<Matchmaking>>

    @Query("SELECT * FROM Matchmaking WHERE (nickname1 = :nickname)")
    fun getAllMatchesWaitingByNickname(nickname: String): Flow<List<Matchmaking>>
}