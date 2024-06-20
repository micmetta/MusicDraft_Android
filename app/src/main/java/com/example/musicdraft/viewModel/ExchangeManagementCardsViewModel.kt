package com.example.musicdraft.viewModel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavController
import com.example.musicdraft.data.UIEventExchangeCards.UIEventExchangeCards
import com.example.musicdraft.data.tables.exchange_management_cards.ExchangeManagementCards
import com.example.musicdraft.database.MusicDraftDatabase
import com.example.musicdraft.model.ExchangeManagementCardsRepository
import com.example.musicdraft.sections.Screens


class ExchangeManagementCardsViewModel(application: Application) : AndroidViewModel(application) {

    // mi prendo il riferimento al DB:
    private val database = MusicDraftDatabase.getDatabase(application)
    private val exchangeManagementCardsDao = database.ExchangeManagementCardsDao()
    ///////
    private val exchangeManagementCardsRepository: ExchangeManagementCardsRepository = ExchangeManagementCardsRepository(this, exchangeManagementCardsDao!!) // istanzio il repository
    var nicknameUserCurrent = mutableStateOf("")
    var nicknameUserRequestCard = mutableStateOf("")
    var infoCardRequest = mutableStateOf(-1)

    var allOffersReceivedByCurrentUser =  exchangeManagementCardsRepository.allOffersReceivedByCurrentUser
    var allOffersSentByCurrentUser = exchangeManagementCardsRepository.allOffersSentByCurrentUser

    var selectedShowReceivedOffer = mutableStateOf(ExchangeManagementCards(
        id = -1,
        "",
        "",
        "",
        "",
        listOf<String>(),
        listOf<String>(),
        0,
        "",
        0,
        0
    ))


    fun onEvent(event: UIEventExchangeCards, navController: NavController) {
        // gestione dei diversi eventi possibili:
        when (event) {
            is UIEventExchangeCards.exchangeCards -> {
                //nicknameUserRequestCard.value = nicknameRequiredCardUser
                navController.navigate(Screens.ExchangeCards.screen)
            }
        }
    }

    fun insertNewOffer(nicknameU1: String,
                       nicknameU2: String,
                       idRequiredCard: String,
                       typeRequiredCard: String,
                       listOfferedCards: List<String>,
                       listTypesOfferedCards: List<String>,
                       pointsOffered: Int
    ){
        val exchangeManagementCards = ExchangeManagementCards(
            nicknameU1 = nicknameU1,
            nicknameU2 = nicknameU2,
            idRequiredCard = idRequiredCard,
            typeRequiredCard = typeRequiredCard,
            listOfferedCards = listOfferedCards,
            listTypesOfferedCards = listTypesOfferedCards,
            pointsOffered = pointsOffered,
            statusOffer = "pending",
            idStart = 0,
            numberCounterOffer = 0
        )
        exchangeManagementCardsRepository.insertNewOffer(exchangeManagementCards)
    }

    fun getOffersReceveidByCurrentUser(nicknameUserCurrent: String) {
        exchangeManagementCardsRepository.getOffersReceveidByCurrentUser(nicknameUserCurrent)
//        return allOffersReceivedByCurrentUser
//        allOffersReceivedByCurrentUser.value = exchangeManagementCardsRepository.getOffersReceveidByCurrentUser(nicknameUserCurrent).value
//
//        Log.i("ExchangeManagementCardsViewModel", "exchangeManagementCardsRepository.getOffersReceveidByCurrentUser(nicknameUserCurrent).value prima del return: ${exchangeManagementCardsRepository.getOffersReceveidByCurrentUser(nicknameUserCurrent).value}")
//        return exchangeManagementCardsRepository.getOffersReceveidByCurrentUser(nicknameUserCurrent).value
    }

    fun getOffersSentByCurrentUser(nicknameUserCurrent: String){
        exchangeManagementCardsRepository.getOffersSentByCurrentUser(nicknameUserCurrent)
    }


    fun updateSelectedShowReceivedOffer(obj: ExchangeManagementCards){
        selectedShowReceivedOffer.value = obj
    }

    fun deleteOffer(id: Int){
        exchangeManagementCardsRepository.deleteOffer(id)
    }

}