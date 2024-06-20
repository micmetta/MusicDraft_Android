package com.example.musicdraft.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicdraft.data.tables.artisti.Artisti
import com.example.musicdraft.data.tables.track.Track
import com.example.musicdraft.data.tables.user_cards.User_Cards_Artisti
import com.example.musicdraft.data.tables.user_cards.User_Cards_Track
import com.example.musicdraft.database.MusicDraftDatabase
import com.example.musicdraft.model.UserCardsRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CardsViewModel(application: Application, private val loginViewModel: LoginViewModel): AndroidViewModel(application) {

    private val database = MusicDraftDatabase.getDatabase(application)
    private val dao = database.ownArtCardsDao()
    private val daoLog =database.userDao()
    private val daoTrack =database.ownTrackCardsDao()
    private val artistDao =database.artistDao()
    private val trackDao = database.trackDao()

    private val _acquiredCardsArtist : List<User_Cards_Artisti>? = null
    val acquiredCardsA: MutableStateFlow<List<User_Cards_Artisti>?> = MutableStateFlow(_acquiredCardsArtist)

    private val _acquiredCardsTrack : List<User_Cards_Track>? = null
    val acquiredCardsT: MutableStateFlow<List<User_Cards_Track>?> = MutableStateFlow(_acquiredCardsTrack)

    private val _MarketArtist : List<User_Cards_Artisti>? = null
    val MarketArtist: MutableStateFlow<List<User_Cards_Artisti>?> = MutableStateFlow(_MarketArtist)

    private val _MarketTrack : List<User_Cards_Track>? = null
    val MarketTrack: MutableStateFlow<List<User_Cards_Track>?> = MutableStateFlow(_MarketTrack)

    private val ownArtistRepo: UserCardsRepo = UserCardsRepo(dao!!,daoLog!!,daoTrack!!,this)




    fun getallcards() {

            val email = loginViewModel.userLoggedInfo.value!!.email

        //recupera carte Artisti
            ownArtistRepo.getArtCardsforUser(email)
            val allArtisti = ownArtistRepo.allCardsforUserA.value
            val ownCardsA = allArtisti?.filter{elem->
                !elem.onMarket
            }
            val marketCardsA =allArtisti?.filter{elem->
                elem.onMarket
            }
            acquiredCardsA.value = ownCardsA
            MarketArtist.value = marketCardsA

        //recupera carte Track
            ownArtistRepo.getTrackCardsforUser(email)
            val allTrackT = ownArtistRepo.allCardsforUserT.value
            val ownCardsT = allTrackT?.filter{elem->
                !elem.onMarket
            }
            val marketCardsT =allTrackT?.filter{elem->
                elem.onMarket
            }
            acquiredCardsT.value = ownCardsT
            MarketTrack.value = marketCardsT



    }

     fun insertArtistToUser(artista:Artisti, email:String){
         val totalPoint = loginViewModel.userLoggedInfo.value?.points?.minus((artista.popolarita*10))
         if (totalPoint != null) {
             if(totalPoint >= 0) {
                 val card = User_Cards_Artisti(0,artista.id,artista.genere,artista.immagine,artista.nome,artista.popolarita,email,false)
                 ownArtistRepo.insertUserCardArtista(card)
                 ownArtistRepo.updatePoints(totalPoint,email)
                 val market = ownArtistRepo.getAllOnMarketCardsA()
                 market?.forEach { elem->
                     if(elem.id_carta == artista.id){
                         val gain = loginViewModel.userLoggedInfo.value?.points?.plus((elem.popolarita))
                         if (gain != null) {
                             ownArtistRepo.updatePoints(gain,elem.email)
                             ownArtistRepo.updateMarketNotStateA(email,elem.id_carta)
                         }
                     }

                 }

                updateAcquiredAddA(card)
             }
         }
    }

    fun insertTrackToUser(track:Track, email:String){
        val totalPoint = loginViewModel.userLoggedInfo.value?.points?.minus((track.popolarita*10))
        if (totalPoint != null) {
            if(totalPoint >= 0) {
                val card = User_Cards_Track(0,track.id,track.anno_pubblicazione,track.durata,track.immagine,track.nome,track.popolarita,email,false)
                ownArtistRepo.insertUserCardTrack(card)
                ownArtistRepo.updatePoints(totalPoint,email)
                val market = ownArtistRepo.getAllOnMarketCardsT()
                market?.forEach { elem->
                    if(elem.id_carta == track.id){
                        val gain = loginViewModel.userLoggedInfo.value?.points?.plus((elem.popolarita))
                        if (gain != null) {
                            ownArtistRepo.updatePoints(gain,elem.email)
                            ownArtistRepo.updateMarketNotStateT(email,elem.id_carta)
                        }
                    }

                }

                updateAcquiredAddT(card)
            }
        }
    }



    fun updateAcquiredRemoveA(artista:User_Cards_Artisti){
        val updatedAcquired = acquiredCardsA.value?.toMutableList()?.apply {
            remove(artista)
        }
        acquiredCardsA.value = (updatedAcquired)
    }
    fun updateAcquiredRemoveT(track:User_Cards_Track){
        val updatedAcquired = acquiredCardsT.value?.toMutableList()?.apply {
            remove(track)
        }
        acquiredCardsT.value = (updatedAcquired)
    }


    fun updateAcquiredAddA(artista:User_Cards_Artisti){
        val updatedAcquired = acquiredCardsA.value?.toMutableList()?.apply {
            add(artista)
        }
        acquiredCardsA.value = (updatedAcquired)
    }
    fun updateAcquiredAddT(track:User_Cards_Track){
        val updatedAcquired = acquiredCardsT.value?.toMutableList()?.apply {
            add(track)
        }
        acquiredCardsT.value = (updatedAcquired)
    }


    fun updateOnMarketRemoveA(artista:User_Cards_Artisti){
        val updatedonMarket = MarketArtist?.value?.toMutableList()?.apply {
            remove(artista)
        }
        MarketArtist.value = (updatedonMarket)
    }
    fun updateOnMarketRemoveT(track:User_Cards_Track){
        val updatedonMarket = MarketTrack?.value?.toMutableList()?.apply {
            remove(track)
        }
        MarketTrack.value = (updatedonMarket)
    }


    fun updateOnMarketAddA(artista:User_Cards_Artisti){
        val updatedonMarket = MarketArtist?.value?.toMutableList()?.apply {
            add(artista)
        }
        MarketArtist.value = (updatedonMarket)
    }
    fun updateOnMarketAddT(track:User_Cards_Track){
        val updatedonMarket = MarketTrack?.value?.toMutableList()?.apply {
            add(track)
        }
        MarketTrack.value = (updatedonMarket)
    }



    fun vendi_artista(artista:User_Cards_Artisti){
        this.viewModelScope.launch {
            val email = loginViewModel.userLoggedInfo.value!!.email
            val cards_on_market = ownArtistRepo.getArtistCardbyId(artista.id_carta, email)
            MarketArtist.value = cards_on_market
            ownArtistRepo.updateMarketStateA(email,artista.id_carta)
            updateAcquiredRemoveA(artista)
            updateOnMarketAddA(artista)
            val a = Artisti(0,artista.id_carta,artista.genere,artista.immagine,artista.nome,artista.popolarita)
            artistDao?.insertArtist(a)



        }
    }

    fun vendi_track(track:User_Cards_Track){
        this.viewModelScope.launch {
            val email = loginViewModel.userLoggedInfo.value!!.email
            val cards_on_market = ownArtistRepo.getTrackCardbyId(track.id_carta, email)
            MarketTrack.value = cards_on_market
            ownArtistRepo.updateMarketStateT(email,track.id_carta)
            updateAcquiredRemoveT(track)
            updateOnMarketAddT(track)
            val t = Track(0,track.id_carta,track.anno_pubblicazione,track.durata,track.immagine,track.nome,track.popolarita)
            trackDao?.insertTrack(t)



        }
    }

}





