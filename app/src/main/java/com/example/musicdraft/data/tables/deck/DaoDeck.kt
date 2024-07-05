package com.example.musicdraft.data.tables.deck

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
/**
 * Data Access Object (DAO) per la gestione dei mazzi di carte nel database Room.
 */
@Dao
interface DaoDeck {
    /**
     * Ottiene tutti i mazzi di carte di un utente specifico in modo asincrono tramite Flow.
     *
     * @param email L'email dell'utente di cui ottenere i mazzi.
     * @return Flow che emette una lista di Deck associati all'email specificata.
     */
    @Query("SELECT * FROM Deck WHERE email = :email")
    fun getAllDeckbyEmail(email:String): Flow<List<Deck>>

    /**
     * Inserisce un mazzo di carte in modo asincrono.
     *
     * @param deck Il mazzo di carte da inserire.
     */
    @Insert
    suspend fun insertdeck(deck: Deck)

    /**
     * Inserisce una lista di mazzi di carte in modo asincrono.
     *
     * @param decks La lista di mazzi di carte da inserire.
     */
    @Insert
    suspend fun insertAllDecks(decks: List<Deck>)

    /**
     * Elimina un mazzo di carte in modo asincrono.
     *
     * @param deck Il mazzo di carte da eliminare.
     */
    @Delete
    suspend fun deleteDeck(deck: Deck)

    /**
    * Elimina tutti i mazzi di carte con un determinato nome e appartenenti a un utente specifico in modo asincrono.
    *
    * @param nomeMazzoDaEliminare Il nome del mazzo di carte da eliminare.
    * @param email L'email dell'utente proprietario dei mazzi.
    */
    @Query("DELETE FROM deck WHERE nome_mazzo = :nomeMazzoDaEliminare AND email =:email")
    suspend fun deleteMazziByNome(nomeMazzoDaEliminare: String,email: String)

    /**
     * Ottiene i nomi distinti dei mazzi di carte di un utente specifico in modo asincrono tramite Flow.
     *
     * @param email L'email dell'utente di cui ottenere i nomi dei mazzi.
     * @return Flow che emette una lista di nomi distinti dei mazzi di carte dell'utente specificato.
     */
    @Query("SELECT DISTINCT nome_mazzo FROM Deck WHERE email= :email")
    fun getDistinctDeckNames(email:String): Flow<List<String>>


    /**
     * Ottiene le carte associate a un mazzo di carte specifico di un utente in modo asincrono tramite Flow.
     *
     * @param nomeMazzo Il nome del mazzo di carte di cui ottenere le carte.
     * @param email L'email dell'utente proprietario del mazzo di carte.
     * @return Flow che emette una lista di carte associate al mazzo specificato.
     */
    @Query("SELECT carte_associate FROM Deck WHERE nome_mazzo = :nomeMazzo AND email = :email")
    fun getDecksByNomeMazzoAndEmail(nomeMazzo: String, email: String): Flow<List<String>>

    /**
     * Ottiene la popolarità dei mazzi di carte con un determinato nome e appartenenti a un utente specifico in modo asincrono tramite Flow.
     *
     * @param nomeMazzo Il nome del mazzo di carte di cui ottenere la popolarità.
     * @param email L'email dell'utente proprietario del mazzo di carte.
     * @return Flow che emette la popolarità del mazzo di carte specificato.
     */
    @Query("SELECT popolarita FROM Deck WHERE nome_mazzo = :nomeMazzo AND email = :email GROUP BY popolarita")
    fun getDecksPop(nomeMazzo: String, email: String): Flow<Float>

//    //////////////////////////////////////////////////////
//    @Query("SELECT * FROM Deck WHERE identifier = :id")
//    suspend fun getDeckById(id: Int): Flow<Deck?>
//    //////////////////////////////////////////////////////


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