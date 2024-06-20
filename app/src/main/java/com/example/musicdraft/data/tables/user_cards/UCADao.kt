package com.example.musicdraft.data.tables.user_cards

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UCADao {

    @Insert
    suspend fun insertUserCardArt(card_user: User_Cards_Artisti)

    @Delete
    suspend fun deleteUserCardArt(card_user: User_Cards_Artisti)

    @Query("SELECT * From USER_CARDS_ARTISTI WHERE email= :email")
    fun getAllCardArtForUser(email: String): Flow<List<User_Cards_Artisti>>

    @Query("SELECT * FROM user_cards_artisti")
    fun getallcards(): Flow<List<User_Cards_Artisti>>

    //////////////////////////////////////////////////////////////////////////////////////////
    // prende le info della carta che ha email= :email_user e id==idCard
    @Query("SELECT * FROM USER_CARDS_ARTISTI WHERE email= :email_user AND id_carta= :idCard")
    fun getInfoCardArtistByEmailAndId(email_user: String, idCard: String): Flow<User_Cards_Artisti?>
    //////////////////////////////////////////////////////////////////////////////////////////


    //////////////////////////////////////////////////////////////////////////////////////////
    // prende le info di tutte le carte di tipo artista che hanno email= :email_user e che hanno l'id uguale ad un qualche id presente nella lista 'idsCards'
    @Query("SELECT * FROM USER_CARDS_ARTISTI WHERE (email= :email_user) AND (id_carta IN (:idsCards))")
    fun getInfoAllCardArtistByEmailAndListOfIds(email_user: String, idsCards: List<String>): Flow<List<User_Cards_Artisti>>
    //////////////////////////////////////////////////////////////////////////////////////////


    //////////////////////////////////////////////////////////////////////////////////////////
    @Query("UPDATE USER_CARDS_ARTISTI SET email = :newEmailOwner WHERE id_carta = :idCard")
    suspend fun updateCardArtistOwner(newEmailOwner: String, idCard: String)
    //////////////////////////////////////////////////////////////////////////////////////////


}