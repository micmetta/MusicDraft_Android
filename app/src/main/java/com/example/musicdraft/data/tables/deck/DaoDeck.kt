package com.example.musicdraft.data.tables.deck

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.musicdraft.data.tables.track.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoDeck {
    @Query("SELECT * FROM Deck WHERE email = :email")
    fun getAllDeckbyEmail(email:String): Flow<List<Deck>>

    @Insert
    suspend fun insertdeck(deck: Deck)

    @Insert
    suspend fun insertAllDecks(decks: Array<Deck>)

    @Delete
    suspend fun deleteDeck(deck: Deck)

    @Query("SELECT DISTINCT nome_mazzo FROM Deck WHERE email= :email")
    fun getDistinctDeckNames(email:String): Flow<List<String>>

    @Query("SELECT carte_associate FROM Deck WHERE nome_mazzo = :nomeMazzo AND email = :email")
    fun getDecksByNomeMazzoAndEmail(nomeMazzo: String, email: String): Flow<List<String>>


}