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

/**
 * ViewModel per la gestione degli scambi di carte tra utenti.
 *
 * @param application Applicazione Android utilizzata per ottenere il riferimento al database.
 */
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
        0
        //"",
        //0,
        //0
    ))

    var selectedShowSentOffer = mutableStateOf(ExchangeManagementCards(
        id = -1,
        "",
        "",
        "",
        "",
        listOf<String>(),
        listOf<String>(),
        0
        //"",
        //0,
        //0
    ))

    /**
     * Gestisce gli eventi UI relativi agli scambi di carte.
     *
     * @param event Evento UI da gestire.
     * @param navController Controller di navigazione per la navigazione tra schermate.
     */
    fun onEvent(event: UIEventExchangeCards, navController: NavController) {
        // gestione dei diversi eventi possibili:
        when (event) {
            is UIEventExchangeCards.exchangeCards -> {
                //nicknameUserRequestCard.value = nicknameRequiredCardUser
                navController.navigate(Screens.ExchangeCards.screen)
            }
        }
    }

    /**
     * Inserisce una nuova offerta di scambio di carte nel repository.
     *
     * @param nicknameU1 Nickname dell'utente che offre le carte.
     * @param nicknameU2 Nickname dell'utente a cui sono offerte le carte.
     * @param idRequiredCard ID della carta richiesta.
     * @param typeRequiredCard Tipo della carta richiesta.
     * @param listOfferedCards Lista delle carte offerte.
     * @param listTypesOfferedCards Lista dei tipi delle carte offerte.
     * @param pointsOffered Punti offerti per lo scambio.
     */
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
            pointsOffered = pointsOffered
            //statusOffer = "pending"
            //idStart = 0,
            //numberCounterOffer = 0
        )
        exchangeManagementCardsRepository.insertNewOffer(exchangeManagementCards)
    }
    /**
     * Recupera le offerte ricevute dall'utente corrente.
     *
     * @param nicknameUserCurrent Nickname dell'utente corrente.
     */
    fun getOffersReceveidByCurrentUser(nicknameUserCurrent: String) {
        exchangeManagementCardsRepository.getOffersReceveidByCurrentUser(nicknameUserCurrent)
//        return allOffersReceivedByCurrentUser
//        allOffersReceivedByCurrentUser.value = exchangeManagementCardsRepository.getOffersReceveidByCurrentUser(nicknameUserCurrent).value
//
//        Log.i("ExchangeManagementCardsViewModel", "exchangeManagementCardsRepository.getOffersReceveidByCurrentUser(nicknameUserCurrent).value prima del return: ${exchangeManagementCardsRepository.getOffersReceveidByCurrentUser(nicknameUserCurrent).value}")
//        return exchangeManagementCardsRepository.getOffersReceveidByCurrentUser(nicknameUserCurrent).value
    }

    /**
     * Recupera le offerte inviate dall'utente corrente.
     *
     * @param nicknameUserCurrent Nickname dell'utente corrente.
     */
    fun getOffersSentByCurrentUser(nicknameUserCurrent: String){
        exchangeManagementCardsRepository.getOffersSentByCurrentUser(nicknameUserCurrent)
    }

    /**
     * Aggiorna l'offerta ricevuta selezionata per la visualizzazione.
     *
     * @param obj Oggetto dell'offerta ricevuta da visualizzare.
     */
    fun updateSelectedShowReceivedOffer(obj: ExchangeManagementCards){
        selectedShowReceivedOffer.value = obj
    }

    /**
     * Aggiorna l'offerta inviata selezionata per la visualizzazione.
     *
     * @param obj Oggetto dell'offerta inviata da visualizzare.
     */
    fun updateSelectedShowSentOffer(obj: ExchangeManagementCards){
        selectedShowSentOffer.value = obj
    }

    /**
     * Elimina un'offerta di scambio dal repository.
     *
     * @param id ID dell'offerta da eliminare.
     */
    fun deleteOffer(id: Int){
        exchangeManagementCardsRepository.deleteOffer(id)
    }

}