package com.example.musicdraft.data.tables.user_cards

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) per la gestione delle relazioni tra utenti e carte dei brani musicali.
 * Questo DAO fornisce metodi per l'inserimento, la cancellazione e la query delle carte dei brani
 * possedute dagli utenti, nonché per la gestione dello stato di mercato delle carte.
 */
@Dao
interface UCTDao {

    /**
     * Inserisce una nuova carta di brano musicale posseduta da un utente nel database.
     *
     * @param card_user La relazione tra utente e carta da inserire.
     */
    @Insert
    suspend fun insertUserCardTrack(card_user: User_Cards_Track)

    /**
     * Cancella una carta di brano musicale posseduta da un utente dal database.
     *
     * @param card_user La relazione tra utente e carta da cancellare.
     */
    @Delete
    suspend fun deleteUserCardArt(card_user: User_Cards_Track)

    /**
     * Ottiene tutte le carte di brani musicali possedute da un utente specifico.
     *
     * @param email L'email dell'utente di cui ottenere le carte.
     * @return [Flow] che emette la lista delle carte di brani musicali possedute dall'utente.
     */
    @Query("SELECT * From USER_CARDS_TRACK WHERE email= :email")
    fun getAllCardTrackForUser(email: String): Flow<List<User_Cards_Track>>

    /**
     * Ottiene tutte le carte di brani musicali presenti nel database.
     *
     * @return [Flow] che emette la lista di tutte le carte di brani musicali nel database.
     */
    @Query("SELECT * FROM User_Cards_Track")
    fun getallcards(): Flow<List<User_Cards_Track>>

    //////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Prende tutte le informazioni della carta che fa riferimento ad uno specifico utente.
     *
     * @param email_user L'email dell'utente.
     * @param idCard identificatore della carta.
     * @return [Flow] di tipo User_Cards_Track che emette le informazioni della carta.
     */
    @Query("SELECT * FROM User_Cards_Track WHERE email= :email_user AND id_carta= :idCard")
    fun getInfoCardTrackByEmailAndId(email_user: String, idCard: String): Flow<User_Cards_Track?>
    //////////////////////////////////////////////////////////////////////////////////////////


    //////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Prende tutte le informazioni di tutte le carte di tipo brano che
     * sono in possesso di un certo utente e che hanno l'id uguale ad un qualche id presente nella lista di ids
     * fornita in input.
     *
     * @param email_user L'email dell'utente.
     * @param idsCards lista degli identificatori delle carte.
     * @return [Flow] Flow che emette una lista di User_Cards_Track.
     */
    @Query("SELECT * FROM USER_CARDS_TRACK WHERE (email= :email_user) AND (id_carta IN (:idsCards))")
    fun getInfoAllCardTracksByEmailAndListOfIds(email_user: String, idsCards: List<String>): Flow<List<User_Cards_Track>>
    //////////////////////////////////////////////////////////////////////////////////////////


    //////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Aggiorna il propietario di una certa carta-brano.
     *
     * @param newEmailOwner L'email dell'utente nuovo proprietario.
     * @param idCard Identificatore della carta.
     */
    @Query("UPDATE USER_CARDS_TRACK SET email = :newEmailOwner WHERE id_carta = :idCard")
    suspend fun updateCardTrackOwner(newEmailOwner: String, idCard: String)
    //////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Ottiene una specifica carta di brano musicale posseduta da un utente.
     *
     * @param id_carta L'ID della carta di brano musicale.
     * @param email L'email dell'utente di cui è la carta.
     * @return [Flow] che emette la lista delle corrispondenti carte di brani musicali possedute dall'utente.
     */
    @Query("SELECT * FROM user_cards_track WHERE id_carta=:id_carta AND email=:email")
    fun getCardbyId(id_carta:String,email:String):Flow<List<User_Cards_Track>>

    /**
     * Ottiene tutte le carte di brani musicali attualmente sul mercato.
     *
     * @return [Flow] che emette la lista delle carte di brani musicali attualmente sul mercato.
     */
    @Query("SELECT * FROM user_cards_track WHERE onMarket=true")
    fun getCardsOnMarket():Flow<List<User_Cards_Track>>

    /**
     * Aggiorna lo stato di mercato di una carta di brano musicale posseduta da un utente, impostandola su "sul mercato".
     *
     * @param email L'email dell'utente di cui è la carta.
     * @param id_carta L'ID della carta di brano musicale.
     */
    @Query("UPDATE user_cards_track SET onMarket = true WHERE email =:email AND id_carta=:id_carta")
    suspend fun updateOnMarkeState(email:String, id_carta: String)

    /**
     * Aggiorna lo stato di mercato di una carta di brano musicale posseduta da un utente, impostandola su "non sul mercato".
     *
     * @param email L'email dell'utente di cui è la carta.
     * @param id_carta L'ID della carta di brano musicale.
     */
    @Query("UPDATE user_cards_track SET onMarket = false WHERE email =:email AND id_carta=:id_carta")
    suspend fun updateNotOnMarkeState(email:String,id_carta: String)
}
