package com.example.musicdraft.model

import androidx.lifecycle.viewModelScope
import com.example.musicdraft.data.tables.exchange_management_cards.ExchangeManagementCards
import com.example.musicdraft.data.tables.exchange_management_cards.ExchangeManagementCardsDao
import com.example.musicdraft.viewModel.ExchangeManagementCardsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExchangeManagementCardsRepository(val viewModel: ExchangeManagementCardsViewModel, val exchangeManagementCardsDao: ExchangeManagementCardsDao) {

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Questa INSERT  viene invocata dopo che l'utente (con "nicknameU1") ha selezionato nell'interfaccia la carta per la quale
    // vuole fare un'offerta al suo amico (con "nicknameU2") ed ha giò impostato la sua offerta che deve contenere questi campi avvalorati:
    // - idRequiredCard,
    // - listOfferedCards (può essere vuota oppure no),
    // - pointsOffered (possono essere 0 oppure no),
    // per eseguire la query qui sotto in corrispondenza del campo "statoOfferta" scriverò "in attesa":

    // caso da gestire:
    // - Se U1 fa un offerta che ha "listOfferedCards" VUOTA e pointsOfferti==0, ALLORA L'OFFERTA VIENE AUTOMATICAMENTE
    //   RIFIUTATA già nel backend perchè non considerata valida.
    //   INFO: LE OFFERTE DI SCAMBIO POTRANNO ESSERE FATTE SOLAMENTE TRA AMICI, questa cosa verrà gestita direttamente nel frontend.
    fun insertNewOffer(exchangeManagementCards: ExchangeManagementCards){
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                exchangeManagementCardsDao.insertNewOffer(exchangeManagementCards)
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}