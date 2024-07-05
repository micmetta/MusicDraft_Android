package com.example.musicdraft.model

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.musicdraft.data.tables.user.UserDao
import com.example.musicdraft.data.tables.user_cards.UCADao
import com.example.musicdraft.data.tables.user_cards.UCTDao
import com.example.musicdraft.data.tables.user_cards.User_Cards_Artisti
import com.example.musicdraft.data.tables.user_cards.User_Cards_Track
import com.example.musicdraft.viewModel.CardsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Repository per la gestione delle carte utente nell'applicazione.
 *
 * @property dao Il DAO per l'accesso alle carte degli artisti nel database.
 * @property daoLog Il DAO per l'accesso agli utenti nel database.
 * @property daoTrack Il DAO per l'accesso alle carte delle tracce nel database.
 * @property cardsViewModel Il ViewModel associato a questo repository.
 */
class UserCardsRepo(
    val dao: UCADao,
    val daoLog: UserDao,
    val daoTrack: UCTDao,
    private val cardsViewModel: CardsViewModel
) {

    val CardsForUsersA:List<User_Cards_Artisti>? = null
    val CardsForUsersT:List<User_Cards_Track>? = null

    /** Lista delle carte degli artisti inizializzata a null. */
    val artCard: List<User_Cards_Artisti>? = null

    /** Flusso che contiene tutte le carte degli artisti per un utente. */
    private val _allCardsforUserA = MutableStateFlow<List<User_Cards_Artisti>>(emptyList())
    val allCardsforUserA: StateFlow<List<User_Cards_Artisti>> get() = _allCardsforUserA

    /** Flusso che contiene tutte le carte delle tracce per un utente. */
    val allCardsforUserT: MutableStateFlow<List<User_Cards_Track>?> = MutableStateFlow(emptyList())

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    // per essere certo di aggiornare solamente le carte dell'utente amico al quale l'utente corrente
    // vuole inviare una richiesta e che quindi partendo dalla sezione 'Mates' ha cliccato sul button "Exchange Cards"
    // e adesso si trova nella schermata dove deve visualizzare le carte artisti/brani del suo amico:
    val allCardsforFriendA: MutableStateFlow<List<User_Cards_Artisti>?> = MutableStateFlow(CardsForUsersA)
    val allCardsforFriendT: MutableStateFlow<List<User_Cards_Track>?> = MutableStateFlow(CardsForUsersT)
    //////////////////////////////////////////////////////////////////////////////////////////////////////


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    // per aggiornare la carta richiesta (potrà essere artista o brano) all'utente loggato da parte di un suo amico:
    var infoCardArtistRequest: MutableStateFlow<User_Cards_Artisti?> = MutableStateFlow(null)
    var infoCardTrackRequest: MutableStateFlow<User_Cards_Track?> = MutableStateFlow(null)
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    val listOfArtists: List<User_Cards_Artisti>? = null
    val listOfTracks: List<User_Cards_Track>? = null
    val listAllInfoAboutCardsArtistOffered: MutableStateFlow<List<User_Cards_Artisti>?> = MutableStateFlow(listOfArtists)
    val listAllInfoAboutCardsTracksOffered: MutableStateFlow<List<User_Cards_Track>?> = MutableStateFlow(listOfTracks)
    //////////////////////////////////////////////////////////////////////////////////////////////////////



    /**
     * Ottiene tutte le carte degli artisti per un utente specifico e aggiorna il flusso [_allCardsforUserA].
     *
     * @param email L'email dell'utente.
     */
    fun getArtCardsforUser(email: String) {
        cardsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val allCardsForUsers = dao.getAllCardArtForUser(email)
                allCardsForUsers.collect { response ->
                    _allCardsforUserA.value = response
                }
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    fun getArtCardsforFriend(email:String): List<User_Cards_Artisti>? {
        cardsViewModel.viewModelScope.launch {
            val allcardsforusers = dao.getAllCardArtForUser(email)
            allcardsforusers.collect{ response->
                allCardsforFriendA.value = response
            }
        }
        return allCardsforFriendA.value
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




    /**
     * Ottiene tutte le carte delle tracce per un utente specifico e aggiorna il flusso [allCardsforUserT].
     *
     * @param email L'email dell'utente.
     */
    fun getTrackCardsforUser(email: String) {
        cardsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val allCardsForUsers = daoTrack.getAllCardTrackForUser(email)
                allCardsForUsers.collect { response ->
                    allCardsforUserT.value = response
                }
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    fun getTrackCardsforFriend(email:String): List<User_Cards_Track>? {
        cardsViewModel.viewModelScope.launch {
            val allcardsforusers = daoTrack.getAllCardTrackForUser(email)
            allcardsforusers.collect{ response->
                allCardsforFriendT.value = response
            }
        }
        return allCardsforFriendT.value
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Inserisce una nuova carta traccia per un utente.
     *
     * @param obj La carta traccia da inserire.
     */
    fun insertUserCardTrack(obj: User_Cards_Track) {
        cardsViewModel.viewModelScope.launch {
            daoTrack.insertUserCardTrack(obj)
        }
    }

    /**
     * Inserisce una nuova carta artista per un utente.
     *
     * @param obj La carta artista da inserire.
     */
    fun insertUserCardArtista(obj: User_Cards_Artisti) {
        cardsViewModel.viewModelScope.launch {
            dao.insertUserCardArt(obj)
        }
    }

    /**
     * Aggiorna i punti per un utente.
     *
     * @param point Il nuovo punteggio.
     * @param email L'email dell'utente.
     */
    fun updatePoints(point: Int, email: String) {
        cardsViewModel.viewModelScope.launch {
            daoLog.updatePoints(point, email)
        }
    }


    /**
     * Ottiene una carta artista per ID e utente specifico e aggiorna il flusso [_allCardsforUserA].
     *
     * @param id_carta L'ID della carta.
     * @param email L'email dell'utente.
     * @return La lista delle carte degli artisti con l'ID specificato.
     */
    fun getArtistCardById(id_carta: String, email: String): List<User_Cards_Artisti>? {
        cardsViewModel.viewModelScope.launch {
            val allCardsForUsers = dao.getCardbyId(id_carta, email)
            allCardsForUsers.collect { response ->
                _allCardsforUserA.value = response
            }
        }
        return allCardsforUserA.value
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // prende le info della carta 'artista' che ha email= :email_user e id==idCard
    fun getInfoCardArtistByEmailAndId(email_user: String, idCard: String) {
        cardsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // mi faccio restituire dalla funzione "getUserByEmail(email)" del DAO
                // il flow:
                val InfoCard = dao.getInfoCardArtistByEmailAndId(email_user, idCard)
                InfoCard.collect { cardInfo ->
                    infoCardArtistRequest.value = cardInfo
                    Log.i("UserArtistCardRepo", "info carta 'artista' richiesta all'utente corrente: ${infoCardArtistRequest.value}")
                }

            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    /**
     * Ottiene una carta traccia per ID e utente specifico e aggiorna il flusso [allCardsforUserT].
     *
     * @param id_carta L'ID della carta.
     * @param email L'email dell'utente.
     * @return La lista delle carte delle tracce con l'ID specificato.
     */
    fun getTrackCardById(id_carta: String, email: String): List<User_Cards_Track>? {
        cardsViewModel.viewModelScope.launch {
            val allCardsForUsers = daoTrack.getCardbyId(id_carta, email)
            allCardsForUsers.collect { response ->
                allCardsforUserT.value = response
            }
        }
        return allCardsforUserT.value
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // prende le info della carta 'brano' che ha email= :email_user e id==idCard
    fun getInfoCardTrackByEmailAndId(email_user: String, idCard: String) {
        cardsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // mi faccio restituire dalla funzione "getInfoCardTrackByEmailAndId(email)" del DAO
                // il flow:
                val InfoCard = daoTrack.getInfoCardTrackByEmailAndId(email_user, idCard)
                InfoCard.collect { cardInfo ->
                    infoCardTrackRequest.value = cardInfo
                    Log.i("UserArtistCardRepo", "info carta 'brano' richiesta all'utente corrente: ${infoCardTrackRequest.value}")
                }

            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




    /**
     * Ottiene tutte le carte degli artisti presenti sul mercato e aggiorna il flusso [_allCardsforUserA].
     *
     * @return La lista delle carte degli artisti sul mercato.
     */
    fun getAllOnMarketCardsA(): List<User_Cards_Artisti>? {
        cardsViewModel.viewModelScope.launch {
            val allCardsForUsers = dao.getCardsOnMarket()
            allCardsForUsers.collect { response ->
                _allCardsforUserA.value = response
            }
        }
        return allCardsforUserA.value
    }

    /**
     * Ottiene tutte le carte delle tracce presenti sul mercato e aggiorna il flusso [allCardsforUserT].
     *
     * @return La lista delle carte delle tracce sul mercato.
     */
    fun getAllOnMarketCardsT(): List<User_Cards_Track>? {
        cardsViewModel.viewModelScope.launch {
            val allCardsForUsers = daoTrack.getCardsOnMarket()
            allCardsForUsers.collect { response ->
                allCardsforUserT.value = response
            }
        }
        return allCardsforUserT.value
    }

    /**
     * Aggiorna lo stato di una carta artista come presente sul mercato.
     *
     * @param email L'email dell'utente.
     * @param id_carta L'ID della carta.
     */
    fun updateMarketStateA(email: String, id_carta: String) {
        cardsViewModel.viewModelScope.launch {
            dao.updateOnMarkeState(email, id_carta)
        }
    }

    /**
     * Aggiorna lo stato di una carta artista come non presente sul mercato.
     *
     * @param email L'email dell'utente.
     * @param id_carta L'ID della carta.
     */
    fun updateMarketNotStateA(email: String, id_carta: String) {
        cardsViewModel.viewModelScope.launch {
            dao.updateNotOnMarkeState(email, id_carta)
        }
    }

    /**
     * Aggiorna lo stato di una carta traccia come presente sul mercato.
     *
     * @param email L'email dell'utente.
     * @param id_carta L'ID della carta.
     */
    fun updateMarketStateT(email: String, id_carta: String) {
        cardsViewModel.viewModelScope.launch {
            daoTrack.updateOnMarkeState(email, id_carta)
        }
    }

    /**
     * Aggiorna lo stato di una carta traccia come non presente sul mercato.
     *
     * @param email L'email dell'utente.
     * @param id_carta L'ID della carta.
     */
    fun updateMarketNotStateT(email: String, id_carta: String) {
        cardsViewModel.viewModelScope.launch {
            daoTrack.updateNotOnMarkeState(email, id_carta)
        }
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    - Questa funzione andrà a ciclare sugli elementi di 'listIdsCardsOffered' e per ogni elemento prenderà il suo tipo direttamente dalla
      seconda lista chiamata 'listTypesCardsOffered'.
      In base a qual è il tipo della carta corrente 'artista' o 'brano' invocherà un metodo particolare del 'dao' che gli permetterà di prendere
      tutte le info della carta corrente e salvarle nella lista chiamata 'listAllInfoAboutCardsOffered' (di tipo 'MutableStateFlow')
      che conterrà quindi tutte le info di tutte le carte offerte.
    */
    fun getInfoCardsOfferedByEmailAndIdsAndTypes(email_user_offer: String, listIdsCardsOffered: List<String>, listTypesCardsOffered: List<String>) {

        val listOfArtistsIds = mutableListOf<String>()
        val listOfTracksIds = mutableListOf<String>()

        for (i in listIdsCardsOffered.indices) {
            if(listTypesCardsOffered[i] == "artist"){
                listOfArtistsIds.add(listIdsCardsOffered[i])
            }else{
                listOfTracksIds.add(listIdsCardsOffered[i])
            }
        }

        cardsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val ListUserCardsArtist = dao.getInfoAllCardArtistByEmailAndListOfIds(email_user_offer, listOfArtistsIds)
                ListUserCardsArtist.collect { list ->
                    listAllInfoAboutCardsArtistOffered.value = list
                    Log.i("UserArtistCardRepo", "listAllInfoAboutCardsArtistOffered: ${listAllInfoAboutCardsArtistOffered.value}")
                }
            }
        }

        cardsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val ListUserCardsTrack = daoTrack.getInfoAllCardTracksByEmailAndListOfIds(email_user_offer, listOfTracksIds)
                ListUserCardsTrack.collect { list ->
                    listAllInfoAboutCardsTracksOffered.value = list
                    Log.i("UserArtistCardRepo", "listAllInfoAboutCardsTracksOffered: ${listAllInfoAboutCardsTracksOffered.value}")
                }
            }
        }

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    fun updateCardArtistOwner(newEmailOwner: String, idCard: String){
        cardsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dao.updateCardArtistOwner(newEmailOwner, idCard)
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    fun updateCardTrackOwner(newEmailOwner: String, idCard: String){
        cardsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                daoTrack.updateCardTrackOwner(newEmailOwner, idCard)
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
