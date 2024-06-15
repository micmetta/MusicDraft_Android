package com.example.musicdraft.data.tables.exchange_management_cards

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface ExchangeManagementCardsDao {

    @Insert
    suspend fun insertNewOffer(exchange: ExchangeManagementCards)

//    @Query("SELECT * FROM Exchange_management")
//    suspend fun getAll(): List<Exchange_management>
//
//    @Query("SELECT * FROM Exchange_management WHERE id = :id")
//    suspend fun getById(id: Int): Exchange_management?
//
//    @Update
//    suspend fun update(exchange: Exchange_management)
//
//    @Query("DELETE FROM Exchange_management WHERE id = :id")
//    suspend fun deleteById(id: Int)
}