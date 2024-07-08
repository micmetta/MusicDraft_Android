package com.example.musicdraft.viewModel

import android.app.Application
import android.util.Log
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

/**
 * ViewModel per la gestione delle carte utente nell'applicazione.
 *
 * @property loginViewModel Il ViewModel per la gestione del login.
 */
class CardsViewModel(application: Application, private val loginViewModel: LoginViewModel): AndroidViewModel(application) {

    private val database = MusicDraftDatabase.getDatabase(application)
    private val dao = database.ownArtCardsDao()
    private val daoLog = database.userDao()
    private val daoTrack = database.ownTrackCardsDao()
    private val artistDao = database.artistDao()
    private val trackDao = database.trackDao()
    private val deckDao = database.deckDao()

    /** Flusso che contiene tutte le carte artista acquisite per un utente. */
    private val _acquiredCardsArtist: List<User_Cards_Artisti>? = null
    val acquiredCardsA: MutableStateFlow<List<User_Cards_Artisti>?> = MutableStateFlow(_acquiredCardsArtist)

    /** Flusso che contiene tutte le carte traccia acquisite per un utente. */
    private val _acquiredCardsTrack: List<User_Cards_Track>? = null
    val acquiredCardsT: MutableStateFlow<List<User_Cards_Track>?> = MutableStateFlow(_acquiredCardsTrack)
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // per prendere le carte "artisti/brani" di un amico:
    private val _acquiredCardsArtistFriend : List<User_Cards_Artisti>? = null
    val acquiredCardsAFriend: MutableStateFlow<List<User_Cards_Artisti>?> = MutableStateFlow(_acquiredCardsArtistFriend)

    private val _acquiredCardsTrackFriend : List<User_Cards_Track>? = null
    val acquiredCardsTFriend: MutableStateFlow<List<User_Cards_Track>?> = MutableStateFlow(_acquiredCardsTrackFriend)
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /** Flusso che contiene tutte le carte artista presenti sul mercato. */
    private val _MarketArtist: List<User_Cards_Artisti>? = null
    val MarketArtist: MutableStateFlow<List<User_Cards_Artisti>?> = MutableStateFlow(_MarketArtist)

    /** Flusso che contiene tutte le carte traccia presenti sul mercato. */
    private val _MarketTrack: List<User_Cards_Track>? = null
    val MarketTrack: MutableStateFlow<List<User_Cards_Track>?> = MutableStateFlow(_MarketTrack)

    private val ownArtistRepo: UserCardsRepo = UserCardsRepo(dao!!, daoLog!!, daoTrack!!, this)

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // sottoscrizione alle variabili "infoCardArtistRequest" e "infoCardTrackRequest" sempre del repository
    // "UserArtistCardRepo":
    var infoCardArtistRequest =  ownArtistRepo.infoCardArtistRequest
    var infoCardTrackRequest =  ownArtistRepo.infoCardTrackRequest
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    var listAllInfoAboutCardsArtistOffered =  ownArtistRepo.listAllInfoAboutCardsArtistOffered
    var listAllInfoAboutCardsTracksOffered =  ownArtistRepo.listAllInfoAboutCardsTracksOffered
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Recupera tutte le carte (artisti e tracce) per l'utente loggato e aggiorna i flussi.
     */
    fun getallcards() {

        val email = loginViewModel.userLoggedInfo.value!!.email
        Log.d("CardsViewModel", "email: ${email}")

        //recupera carte Artisti
        ownArtistRepo.getArtCardsforUser(email)
        val allArtisti = ownArtistRepo.allCardsforUserA.value
        val ownCardsA = allArtisti?.filter { elem ->
            !elem.onMarket
        }
        val marketCardsA = allArtisti?.filter { elem ->
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


    /**
     * Recupera tutte le carte dell'artista e del brano di un amico specificato tramite email.
     *
     * @param email_friend L'email dell'amico di cui si vogliono recuperare le carte.
     */
    fun getAllCardFriend(email_friend: String){
        acquiredCardsAFriend.value = ownArtistRepo.getArtCardsforFriend(email_friend)
        acquiredCardsTFriend.value =ownArtistRepo.getTrackCardsforFriend(email_friend)
    }


    /**
     * Inserisce una carta artista per l'utente e aggiorna i punti.
     *
     * @param artista L'artista da aggiungere.
     * @param email L'email dell'utente.
     */
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


    /**
     * Inserisce una carta traccia per l'utente e aggiorna i punti.
     *
     * @param track La traccia da aggiungere.
     * @param email L'email dell'utente.
     */
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


    /**
     * Rimuove una carta artista acquisita dall'utente.
     *
     * @param artista La carta artista da rimuovere.
     */
    fun updateAcquiredRemoveA(artista:User_Cards_Artisti){
        val updatedAcquired = acquiredCardsA.value?.toMutableList()?.apply {
            remove(artista)
        }
        acquiredCardsA.value = (updatedAcquired)
    }
    /**
     * Rimuove una carta traccia acquisita dall'utente.
     *
     * @param track La carta traccia da rimuovere.
     */
    fun updateAcquiredRemoveT(track:User_Cards_Track){
        val updatedAcquired = acquiredCardsT.value?.toMutableList()?.apply {
            remove(track)
        }
        acquiredCardsT.value = (updatedAcquired)
    }

    /**
     * Aggiunge una carta traccia acquisita all'utente.
     *
     * @param track La carta traccia da aggiungere.
     */
    fun updateAcquiredAddA(artista:User_Cards_Artisti){
        val updatedAcquired = acquiredCardsA.value?.toMutableList()?.apply {
            add(artista)
        }
        acquiredCardsA.value = (updatedAcquired)
    }

    /**
     * Rimuove una carta artista dal mercato.
     *
     * @param artista La carta artista da rimuovere.
     */
    fun updateAcquiredAddT(track:User_Cards_Track){
        val updatedAcquired = acquiredCardsT.value?.toMutableList()?.apply {
            add(track)
        }
        acquiredCardsT.value = (updatedAcquired)
    }

    /**
     * Rimuove una carta traccia dal mercato.
     *
     * @param track La carta traccia da rimuovere.
     */
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

    /**
     * Aggiunge una carta artista al mercato.
     *
     * @param artista La carta artista da aggiungere.
     */
    fun updateOnMarketAddA(artista:User_Cards_Artisti){
        val updatedonMarket = MarketArtist?.value?.toMutableList()?.apply {
            add(artista)
        }
        MarketArtist.value = (updatedonMarket)
    }

    /**
     * Aggiunge una carta traccia al mercato.
     *
     * @param track La carta traccia da aggiungere.
     */

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Recupera le informazioni sulla carta dell'artista specificata basate su email e ID della carta.
     *
     * @param email_user L'email dell'utente che possiede la carta dell'artista.
     * @param idCard L'ID della carta dell'artista.
     */
    fun getInfoCardArtistByEmailAndId(email_user: String, idCard: String) {
        ownArtistRepo.getInfoCardArtistByEmailAndId(email_user, idCard)
    }

    /**
     * Recupera le informazioni sulla carta del brano specificata basate su email e ID della carta.
     *
     * @param email_user L'email dell'utente che possiede la carta del brano.
     * @param idCard L'ID della carta del brano.
     */
    fun getInfoCardTrackByEmailAndId(email_user: String, idCard: String) {
        ownArtistRepo.getInfoCardTrackByEmailAndId(email_user, idCard)
    }
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Recupera le informazioni sulle carte offerte da un utente specifico basate su email, ID delle carte e tipi di carte.
     *
     * @param email_user_offer L'email dell'utente che offre le carte.
     * @param listIdsCardsOffered Lista degli ID delle carte offerte dall'utente.
     * @param listTypesCardsOffered Lista dei tipi di carte offerte dall'utente.
     */
    fun getInfoCardsOfferedByEmailAndIdsAndTypes(email_user_offer: String, listIdsCardsOffered: List<String>, listTypesCardsOffered: List<String>) {
        ownArtistRepo.getInfoCardsOfferedByEmailAndIdsAndTypes(email_user_offer, listIdsCardsOffered, listTypesCardsOffered)
    }
    ////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    fun updateCardArtistOwner(newEmailOwner: String, idCard: String){
        ownArtistRepo.updateCardArtistOwner(newEmailOwner, idCard)
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    fun updateCardTrackOwner(newEmailOwner: String, idCard: String){
        ownArtistRepo.updateCardTrackOwner(newEmailOwner, idCard)
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    fun updateOnMarketAddT(track:User_Cards_Track){
        val updatedonMarket = MarketTrack?.value?.toMutableList()?.apply {
            add(track)
        }
        MarketTrack.value = (updatedonMarket)
    }

/*
    /**
     * Vende una carta artista, aggiornando i flussi e inserendo l'artista nel database.
     *
     * @param artista La carta artista da vendere.
     */
    fun vendi_artista(artista:User_Cards_Artisti){
        this.viewModelScope.launch {

            val email = loginViewModel.userLoggedInfo.value!!.email
            val cards_on_market = ownArtistRepo.getArtistCardById(artista.id_carta, email)
            MarketArtist.value = cards_on_market
            ownArtistRepo.updateMarketStateA(email,artista.id_carta)
            updateAcquiredRemoveA(artista)
            updateOnMarketAddA(artista)
            val a = Artisti(0,artista.id_carta,artista.genere,artista.immagine,artista.nome,artista.popolarita)
            artistDao?.insertArtist(a)
        }
    }

    /**
     * Vende una carta traccia, aggiornando i flussi e inserendo la traccia nel database.
     *
     * @param track La carta traccia da vendere.
     */
    fun vendi_track(track:User_Cards_Track){
        this.viewModelScope.launch {
            val email = loginViewModel.userLoggedInfo.value!!.email
            val cards_on_market = ownArtistRepo.getTrackCardById(track.id_carta, email)
            MarketTrack.value = cards_on_market
            ownArtistRepo.updateMarketStateT(email,track.id_carta)
            updateAcquiredRemoveT(track)
            updateOnMarketAddT(track)
            val t = Track(0,track.id_carta,track.anno_pubblicazione,track.durata,track.immagine,track.nome,track.popolarita)
            trackDao?.insertTrack(t)



        }
    }
*/
}





