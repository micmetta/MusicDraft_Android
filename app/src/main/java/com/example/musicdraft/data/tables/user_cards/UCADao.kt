package com.example.musicdraft.data.tables.user_cards

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.musicdraft.data.tables.artisti.Artisti
import com.example.musicdraft.data.tables.track.Track
import com.example.musicdraft.data.tables.user.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UCADao {

    @Insert
    suspend fun insertUserCardArt(card_user: User_Cards_Artisti)

    @Delete
    suspend fun deleteUserCardArt(card_user: User_Cards_Artisti)

    @Query("SELECT * From user_cards_artisti WHERE email= :email " )
    fun getAllCardArtForUser(email: String): Flow<List<User_Cards_Artisti>>

    @Query("SELECT * FROM user_cards_artisti")
    fun getallcards(): Flow<List<User_Cards_Artisti>>

    @Query("SELECT * FROM user_cards_artisti WHERE id_carta=:id_carta AND email=:email")
    fun getCardbyId(id_carta:String,email:String):Flow<List<User_Cards_Artisti>>

    @Query("SELECT * FROM user_cards_artisti WHERE onMarket=true")
    fun getCardsOnMarket():Flow<List<User_Cards_Artisti>>

    @Query("UPDATE user_cards_artisti SET onMarket = true WHERE email =:email AND id_carta=:id_carta")
    suspend fun updateOnMarkeState(email:String, id_carta: String)

    @Query("UPDATE user_cards_artisti SET onMarket = false WHERE email =:email AND id_carta=:id_carta")
    suspend fun updateNotOnMarkeState(email:String,id_carta: String)
}