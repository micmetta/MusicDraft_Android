package com.example.musicdraft.data.tables.user_cards

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) per la gestione delle relazioni tra utenti e carte degli artisti.
 * Questo DAO fornisce metodi per l'inserimento, la cancellazione e la query delle carte degli artisti
 * possedute dagli utenti, nonché per la gestione dello stato di mercato delle carte.
 */
@Dao
interface UCADao {

    /**
     * Inserisce una nuova carta di artista posseduta da un utente nel database.
     *
     * @param card_user La relazione tra utente e carta da inserire.
     */
    @Insert
    suspend fun insertUserCardArt(card_user: User_Cards_Artisti)

    /**
     * Cancella una carta di artista posseduta da un utente dal database.
     *
     * @param card_user La relazione tra utente e carta da cancellare.
     */
    @Delete
    suspend fun deleteUserCardArt(card_user: User_Cards_Artisti)

    /**
     * Ottiene tutte le carte di artisti possedute da un utente specifico.
     *
     * @param email L'email dell'utente di cui ottenere le carte.
     * @return [Flow] che emette la lista delle carte di artisti possedute dall'utente.
     */
    @Query("SELECT * FROM user_cards_artisti WHERE email= :email " )
    fun getAllCardArtForUser(email: String): Flow<List<User_Cards_Artisti>>

    /**
     * Ottiene tutte le carte di artisti presenti nel database.
     *
     * @return [Flow] che emette la lista di tutte le carte di artisti nel database.
     */
    @Query("SELECT * FROM user_cards_artisti")
    fun getallcards(): Flow<List<User_Cards_Artisti>>

    //////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Prende tutte le informazioni della carta che fa riferimento ad uno specifico utente.
     *
     * @param email_user L'email dell'utente.
     * @param idCard identificatore della carta.
     * @return [Flow] di tipo User_Cards_Artisti che emette le informazioni della carta.
     */
    @Query("SELECT * FROM USER_CARDS_ARTISTI WHERE email= :email_user AND id_carta= :idCard")
    fun getInfoCardArtistByEmailAndId(email_user: String, idCard: String): Flow<User_Cards_Artisti?>
    //////////////////////////////////////////////////////////////////////////////////////////


    //////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Prende tutte le informazioni di tutte le carte di tipo artista che
     * sono in possesso di un certo utente e che hanno l'id uguale ad un qualche id presente nella lista di ids
     * fornita in input.
     *
     * @param email_user L'email dell'utente.
     * @param idsCards lista degli identificatori delle carte.
     * @return [Flow] Flow che emette una lista di User_Cards_Artisti.
     */
    @Query("SELECT * FROM USER_CARDS_ARTISTI WHERE (email= :email_user) AND (id_carta IN (:idsCards))")
    fun getInfoAllCardArtistByEmailAndListOfIds(email_user: String, idsCards: List<String>): Flow<List<User_Cards_Artisti>>
    //////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Aggiorna il propietario di una certa carta-artista.
     *
     * @param newEmailOwner L'email dell'utente nuovo proprietario.
     * @param idCard Identificatore della carta.
     */
    @Query("UPDATE USER_CARDS_ARTISTI SET email = :newEmailOwner WHERE id_carta = :idCard")
    suspend fun updateCardArtistOwner(newEmailOwner: String, idCard: String)
    //////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Ottiene una specifica carta di artista posseduta da un utente.
     *
     * @param id_carta L'ID della carta di artista.
     * @param email L'email dell'utente di cui è la carta.
     * @return [Flow] che emette la lista delle corrispondenti carte di artisti possedute dall'utente.
     */
    @Query("SELECT * FROM user_cards_artisti WHERE id_carta=:id_carta AND email=:email")
    fun getCardbyId(id_carta:String,email:String):Flow<List<User_Cards_Artisti>>

    /**
     * Ottiene tutte le carte di artisti attualmente sul mercato.
     *
     * @return [Flow] che emette la lista delle carte di artisti attualmente sul mercato.
     */
    @Query("SELECT * FROM user_cards_artisti WHERE onMarket=true")
    fun getCardsOnMarket():Flow<List<User_Cards_Artisti>>

    /**
     * Aggiorna lo stato di mercato di una carta di artista posseduta da un utente, impostandola su "sul mercato".
     *
     * @param email L'email dell'utente di cui è la carta.
     * @param id_carta L'ID della carta di artista.
     */
    @Query("UPDATE user_cards_artisti SET onMarket = true WHERE email =:email AND id_carta=:id_carta")
    suspend fun updateOnMarkeState(email:String, id_carta: String)

    /**
     * Aggiorna lo stato di mercato di una carta di artista posseduta da un utente, impostandola su "non sul mercato".
     *
     * @param email L'email dell'utente di cui è la carta.
     * @param id_carta L'ID della carta di artista.
     */
    @Query("UPDATE user_cards_artisti SET onMarket = false WHERE email =:email AND id_carta=:id_carta")
    suspend fun updateNotOnMarkeState(email:String,id_carta: String)
}
