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

    @Query("SELECT * From USER_CARDS_ARTISTI WHERE email= :email")
    fun getAllCardArtForUser(email: String): Flow<List<User_Cards_Artisti>>

    @Query("SELECT * FROM user_cards_artisti")
    fun getallcards(): Flow<List<User_Cards_Artisti>>
}