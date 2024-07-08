package com.example.musicdraft.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicdraft.data.tables.artisti.Artisti
import com.example.musicdraft.data.tables.track.Track
import com.example.musicdraft.database.MusicDraftDatabase
import com.example.musicdraft.model.ArtistRepository
import com.example.musicdraft.model.TracksRepository
import com.example.musicdraft.model.UserCardsRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel per la gestione del marketplace dell'applicazione.
 *
 * @property cardsViewModel Il ViewModel per la gestione delle carte.
 * @property loginViewModel Il ViewModel per la gestione del login.
 */
class MarketplaceViewModel(application: Application, private val cardsViewModel: CardsViewModel, private val loginViewModel: LoginViewModel) : AndroidViewModel(application) {

    // Database e repository per artisti e tracce
    private val database = MusicDraftDatabase.getDatabase(application)
    val artistDao = database.artistDao()
    val trackDao = database.trackDao()
    val ucaDao = database.ownArtCardsDao()
    val uctDao = database.ownTrackCardsDao()
    val authDao = database.userDao()
    private val artistRepo: ArtistRepository = ArtistRepository(this, artistDao!!)
    private val trackRepo: TracksRepository = TracksRepository(this, trackDao!!)
    private val ownRepo: UserCardsRepo = UserCardsRepo(ucaDao!!, authDao!!, uctDao!!, cardsViewModel)

    // Variabili per gestire gli artisti e le tracce
    val artists: List<Artisti>? = null
    val allartist: MutableStateFlow<List<Artisti>?> = MutableStateFlow(artists)

    val tracks: List<Track>? = null
    val alltrack: MutableStateFlow<List<Track>?> = MutableStateFlow(tracks)

    // Variabili MutableLiveData per visualizzare gli artisti e le tracce filtrati
    val _filteredArtisti = MutableStateFlow<List<Artisti>?>(artists)
    val _filteredBrani = MutableStateFlow<List<Track>?>(tracks)

    private val _showDialog = MutableLiveData(false)
    val showDialog: LiveData<Boolean> get() = _showDialog

    // Stato del messaggio da visualizzare all'utente
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> get() = _message

    init {
        // Inizializzazione dei dati
        this.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val requestsReceived = artistRepo.getAllArtisti()
                requestsReceived.collect { response ->
                    allartist.value = response
                    Log.i("TG", "allartist updated: ${allartist.value}")
                }
            }
        }

        this.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val requestsReceived = trackRepo.getAllTracks()
                requestsReceived.collect { response ->
                    alltrack.value = response
                    Log.i("TG", "alltrack updated: ${alltrack.value}")
                }
            }
        }
    }

    /**
     * Recupera tutte le carte sul mercato e aggiorna la lista degli artisti.
     */
    fun getOnmarketCards() {
        this.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val marketCards = ownRepo.getAllOnMarketCardsA()
                marketCards?.forEach { elem ->
                    val a = Artisti(0, elem.id_carta, elem.genere, elem.immagine, elem.nome, elem.popolarita)
                    if (artistRepo.getArtistbyId(elem.id_carta)?.isEmpty() == true) {
                        val temp = allartist.onEach { list ->
                            list?.plus(a)
                        }
                        temp.collect { res ->
                            allartist.value = res
                        }
                    }
                }
            }
        }
    }

    fun applyArtistFilter(popThreshold: Int?, nameQuery: String?, genreQuery: String?) {
        if (popThreshold == null && nameQuery.isNullOrEmpty() && genreQuery.isNullOrEmpty()) {
            clearFilteredArtisti()
            return
        }

        val filteredArtisti = allartist.value!!.filter { artist ->
            val popFilter = popThreshold?.let { artist.popolarita <= it } ?: true
            val nameFilter = nameQuery?.let { artist.nome.contains(it, ignoreCase = true) } ?: true
            val genreFilter = genreQuery?.let { artist.genere.contains(it, ignoreCase = true) } ?: true
            popFilter && nameFilter && genreFilter
        }

        _filteredArtisti.value = filteredArtisti
    }

    fun applyBraniFilter(popThreshold: Int?, nameQuery: String?) {
        if (popThreshold == null && nameQuery.isNullOrEmpty()) {
            _filteredBrani.value = emptyList()
            return
        }

        val filteredTrack = alltrack.value!!.filter { track ->
            val popFilter = popThreshold?.let { track.popolarita <= it } ?: true
            val nameFilter = nameQuery?.let { track.nome.contains(it, ignoreCase = true) } ?: true
            popFilter && nameFilter
        }
        _filteredBrani.value = filteredTrack
    }

    fun clearFilteredArtisti() {
        _filteredArtisti.value = emptyList()
    }
    fun clearFilteredTrack() {
        _filteredBrani.value = emptyList()
    }

    /**
     * Funzione per comprare una traccia.
     *
     * @param track La traccia da acquistare.
     */
    fun compra_track(track: Track) {
        viewModelScope.launch {
            val email = loginViewModel.userLoggedInfo.value!!.email
            val points = loginViewModel.userLoggedInfo.value!!.points


            // Aggiorna le liste di tracce filtrate e le carte acquistate
            val size = _filteredBrani.value?.size

            ownRepo.getArtCardsforUser(email)
            val allTrack = ownRepo.allCardsforUserT.value
            val isYours = allTrack?.filter { elem ->
                elem.onMarket
            }

            var c = 0
            Log.i("compra", "$isYours")
            if (isYours != null) {
                isYours.forEach { elem ->
                    if (elem.id_carta == track.id) {
                        c += 1
                    }
                }
            }
            if (c == 0) {
                if (points > track.popolarita * 10) {
                    if (size == null) {
                        val currentFilteredList = alltrack.value
                        // Aggiorna le liste di tracce filtrate e le carte acquistate
                        val updatedFilteredList = currentFilteredList!!.toMutableList().apply {
                            remove(track)
                        }
                        alltrack.value = updatedFilteredList
                        trackRepo.deleteTrack(track)
                        cardsViewModel.insertTrackToUser(track, email)
                    } else {
                        val currentFilteredList = _filteredBrani.value
                        val updatedFilteredList = currentFilteredList!!.toMutableList().apply {
                            remove(track)
                        }
                        _filteredBrani.value = updatedFilteredList
                        cardsViewModel.insertTrackToUser(track, email)
                        trackRepo.deleteTrack(track)
                    }
                } else {
                    _message.value = "non hai abbastanza points per comprare questa carta"                }
            }
        }
    }

    /**
     * Funzione per comprare un artista.
     *
     * @param artista L'artista da acquistare.
     */
    fun compra_artisti(artista: Artisti) {
        viewModelScope.launch {
            val email = loginViewModel.userLoggedInfo.value!!.email
            val points = loginViewModel.userLoggedInfo.value!!.points


            // Aggiorna le liste di artisti filtrati e le carte acquistate
            val size = _filteredArtisti.value?.size

            ownRepo.getArtCardsforUser(email)
            val allArtisti = ownRepo.allCardsforUserA.value
            val isYours = allArtisti?.filter { elem ->
                elem.onMarket
            }

            var c = 0
            Log.i("compra", "$isYours")
            if (isYours != null) {
                isYours.forEach { elem ->
                    if (elem.id_carta == artista.id) {
                        c += 1
                    }
                }
            }
            if (c == 0) {
                if (points > artista.popolarita * 10) {
                    if (size == null) {
                        val currentFilteredList = allartist.value
                        // Aggiorna le liste di artisti filtrati e le carte acquistate
                        val updatedFilteredList = currentFilteredList!!.toMutableList().apply {
                            remove(artista)
                        }
                        allartist.value = updatedFilteredList
                        artistRepo.delete_artista(artista)
                        cardsViewModel.insertArtistToUser(artista, email)
                    } else {
                        val currentFilteredList = _filteredArtisti.value
                        val updatedFilteredList = currentFilteredList!!.toMutableList().apply {
                            remove(artista)
                        }
                        _filteredArtisti.value = updatedFilteredList
                        cardsViewModel.insertArtistToUser(artista, email)
                        artistRepo.delete_artista(artista)

                    }
                } else {
                    _message.value = "non hai abbastanza points per comprare questa carta"
                }
            }
        }
    }

    /**
     * Funzione per chiudere il dialogo di conferma acquisto.
     */
    fun clearMessage() {
        _message.value = null
    }

}
