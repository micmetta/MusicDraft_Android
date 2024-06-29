package com.example.musicdraft.data.tables.deck

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoDeck {
    @Query("SELECT * FROM Deck WHERE email = :email")
    fun getAllDeckbyEmail(email:String): Flow<List<Deck>>

    @Insert
    suspend fun insertdeck(deck: Deck)

    @Insert
    suspend fun insertAllDecks(decks: List<Deck>)

    @Delete
    suspend fun deleteDeck(deck: Deck)

    @Query("DELETE FROM deck WHERE nome_mazzo = :nomeMazzoDaEliminare AND email =:email")
    suspend fun deleteMazziByNome(nomeMazzoDaEliminare: String,email: String)

    @Query("SELECT DISTINCT nome_mazzo FROM Deck WHERE email= :email")
    fun getDistinctDeckNames(email:String): Flow<List<String>>

    @Query("SELECT carte_associate FROM Deck WHERE nome_mazzo = :nomeMazzo AND email = :email")
    fun getDecksByNomeMazzoAndEmail(nomeMazzo: String, email: String): Flow<List<String>>

    @Query("SELECT popolarita FROM Deck WHERE nome_mazzo = :nomeMazzo AND email = :email GROUP BY popolarita")
    fun getDecksPop(nomeMazzo: String, email: String): Flow<Float>


    /**
     * Verifica se una carta specifica è associata a un mazzo di un utente specifico.
     *
     * @param userEmail L'email dell'utente di cui verificare i mazzi.
     * @param card La carta da verificare.
     * @return True se la carta è associata a un mazzo dell'utente, altrimenti False.
     */
//    @Query("SELECT COUNT(*) > 0 FROM Deck WHERE email = :userEmail AND carte_associate LIKE '%' || :card || '%'")
//    fun isCardInDeck(userEmail: String, card: String): Flow<Boolean>

     @Query("SELECT COUNT(*) > 0 FROM Deck WHERE email = :userEmail AND carte_associate LIKE '%' || :card || '%'")
     suspend fun isCardInDeck(userEmail: String, card: String): Boolean


}