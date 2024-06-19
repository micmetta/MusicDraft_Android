package com.example.musicdraft.data.tables.exchange_management_cards

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeManagementCardsDao {

    @Insert
    suspend fun insertNewOffer(exchange: ExchangeManagementCards)

    @Query("SELECT * FROM ExchangeManagementCards WHERE nicknameU2 = :nicknameCurrentUser")
    fun getOffersReceveidByCurrentUser(nicknameCurrentUser: String): Flow<List<ExchangeManagementCards>>
}