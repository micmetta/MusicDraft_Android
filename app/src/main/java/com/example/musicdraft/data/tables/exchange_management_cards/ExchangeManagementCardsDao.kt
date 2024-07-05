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

    /**
     * Inserisce una riga nuova riga nella tabella ExchangeManagementCards presente nel database.
     *
     * @param exchange Oggetto da inserire come nuova riga nella tabella.
     */
    @Insert
    suspend fun insertNewOffer(exchange: ExchangeManagementCards)

    /**
     * Elimina una riga della tabella ExchangeManagementCards presente nel database.
     *
     * @param id Identificatore della riga da eliminare.
     */
    @Query("DELETE FROM ExchangeManagementCards WHERE id = :id")
    suspend fun deleteOffer(id: Int)

    /**
     * Prende tutte le offerte di scambi ricevute dall'utente con 'nicknameCurrentUser'.
     *
     * @param nicknameCurrentUser Nickname dell'utente per il quale si desidera avere le informazioni sulle offerte di scambi ricevute.
     * @return Flow che emette una lista di ExchangeManagementCards.
     */
    @Query("SELECT * FROM ExchangeManagementCards WHERE nicknameU2 = :nicknameCurrentUser")
    fun getOffersReceveidByCurrentUser(nicknameCurrentUser: String): Flow<List<ExchangeManagementCards>>

    /**
     * Prende tutte le offerte di scambi inviate dall'utente con 'nicknameCurrentUser'.
     *
     * @param nicknameCurrentUser Nickname dell'utente per il quale si desidera avere le informazioni sulle offerte di scambi inviate.
     * @return Flow che emette una lista di ExchangeManagementCards.
     */
    @Query("SELECT * FROM ExchangeManagementCards WHERE nicknameU1 = :nicknameCurrentUser")
    fun getOffersSentByCurrentUser(nicknameCurrentUser: String): Flow<List<ExchangeManagementCards>>
}