package com.example.musicdraft.data.tables.exchange_management_cards

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


/**
 * Dao che si preoccupa di eseguire le queries sulla tabella ExchangeManagementCards presente nel database.
 *
 */
@Dao
interface ExchangeManagementCardsDao {

    @Insert
    suspend fun insertNewOffer(exchange: ExchangeManagementCards)

    @Query("DELETE FROM ExchangeManagementCards WHERE id = :id")
    suspend fun deleteOffer(id: Int)

    // prendere tutte le offerte di scambi ricevute dall'utente con 'nicknameCurrentUser'
    @Query("SELECT * FROM ExchangeManagementCards WHERE nicknameU2 = :nicknameCurrentUser")
    fun getOffersReceveidByCurrentUser(nicknameCurrentUser: String): Flow<List<ExchangeManagementCards>>

    // prendere tutte le offerte di scambi inviate dall'utente con 'nicknameCurrentUser'
    @Query("SELECT * FROM ExchangeManagementCards WHERE nicknameU1 = :nicknameCurrentUser")
    fun getOffersSentByCurrentUser(nicknameCurrentUser: String): Flow<List<ExchangeManagementCards>>
}