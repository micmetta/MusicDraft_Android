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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserArtistCardRepo(
    val dao: UCADao,
    val daoLog: UserDao,
    val daoTrack:UCTDao,
    private val cardsViewModel: CardsViewModel


) {
    val CardsForUsersA:List<User_Cards_Artisti>? = null
    val CardsForUsersT:List<User_Cards_Track>? = null

    val allCardsforUserA: MutableStateFlow<List<User_Cards_Artisti>?> = MutableStateFlow(CardsForUsersA)
    val allCardsforUserT: MutableStateFlow<List<User_Cards_Track>?> = MutableStateFlow(CardsForUsersT)

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



    fun getArtCardsforUser(email:String): List<User_Cards_Artisti>? {
         cardsViewModel.viewModelScope.launch {
             val allcardsforusers = dao.getAllCardArtForUser(email)
             allcardsforusers.collect{ response->
                 allCardsforUserA.value = response
             }
         }
         return allCardsforUserA.value
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


    fun getTrackCardsforUser(email:String): List<User_Cards_Track>? {
        cardsViewModel.viewModelScope.launch {
            val allcardsforusers = daoTrack.getAllCardTrackForUser(email)
            allcardsforusers.collect{ response->
                allCardsforUserT.value = response
            }
        }
        return allCardsforUserT.value
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


    fun insertUserCardTrack(obj : User_Cards_Track) {
        cardsViewModel.viewModelScope.launch {
            daoTrack.insertUserCardTrack(obj)
        }
    }

    fun insertUserCardArtista(obj : User_Cards_Artisti) {
        cardsViewModel.viewModelScope.launch {
            dao.insertUserCardArt(obj)
        }
     }

    fun updatePoints(point:Int,email:String){
        cardsViewModel.viewModelScope.launch {
            daoLog.updatePoints(point, email)
        }
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