package com.example.musicdraft.data.tables.user_cards

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UCTDao {
    @Insert
    suspend fun insertUserCardTrack(card_user: User_Cards_Track)

    @Delete
    suspend fun deleteUserCardArt(card_user: User_Cards_Track)

    //@Query("SELECT * From USER_CARDS_TRACK WHERE email= :email & onMarket=false") // c'era prima
    @Query("SELECT * From USER_CARDS_TRACK WHERE email= :email")
    fun getAllCardTrackForUser(email: String): Flow<List<User_Cards_Track>>

    @Query("SELECT * FROM User_Cards_Track")
    fun getallcards(): Flow<List<User_Cards_Track>>

    //////////////////////////////////////////////////////////////////////////////////////////
    // prende le info della carta che ha email= :email_user e id==idCard
    @Query("SELECT * FROM User_Cards_Track WHERE email= :email_user AND id_carta= :idCard")
    fun getInfoCardTrackByEmailAndId(email_user: String, idCard: String): Flow<User_Cards_Track?>
    //////////////////////////////////////////////////////////////////////////////////////////


    //////////////////////////////////////////////////////////////////////////////////////////
    // prende le info di tutte le carte di tipo brano che hanno email= :email_user e che hanno l'id uguale ad un qualche id presente nella lista 'idsCards'
    @Query("SELECT * FROM USER_CARDS_TRACK WHERE (email= :email_user) AND (id_carta IN (:idsCards))")
    fun getInfoAllCardTracksByEmailAndListOfIds(email_user: String, idsCards: List<String>): Flow<List<User_Cards_Track>>
    //////////////////////////////////////////////////////////////////////////////////////////


    //////////////////////////////////////////////////////////////////////////////////////////
    @Query("UPDATE USER_CARDS_TRACK SET email = :newEmailOwner WHERE id_carta = :idCard")
    suspend fun updateCardTrackOwner(newEmailOwner: String, idCard: String)
    //////////////////////////////////////////////////////////////////////////////////////////

    @Query("UPDATE user_cards_track SET onMarket = true WHERE email =:email AND id_carta=:id_carta")
    suspend fun updateOnMarkeState(email:String, id_carta: String)

    @Query("UPDATE user_cards_track SET onMarket = false WHERE email =:email AND id_carta=:id_carta")
    suspend fun updateNotOnMarkeState(email:String,id_carta: String)

    @Query("SELECT * FROM user_cards_track WHERE id_carta=:id_carta AND email=:email")
    fun getCardbyId(id_carta:String,email:String):Flow<List<User_Cards_Track>>

    @Query("SELECT * FROM user_cards_track WHERE onMarket=true")
    fun getCardsOnMarket():Flow<List<User_Cards_Track>>
}
