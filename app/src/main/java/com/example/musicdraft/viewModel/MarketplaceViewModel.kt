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
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MarketplaceViewModel(application: Application, private val cardsViewModel: CardsViewModel,private val loginViewModel: LoginViewModel) : AndroidViewModel(application) {



    // Database e repository per artisti e tracce
    private val database = MusicDraftDatabase.getDatabase(application)
    val artistDao = database.artistDao()
    val trackDao = database.trackDao()
    val ucaDao = database.ownArtCardsDao()
    val uctDao =database.ownTrackCardsDao()
    val authDao = database.userDao()
    private val artistRepo: ArtistRepository = ArtistRepository(this, artistDao!!)
    private val trackRepo: TracksRepository = TracksRepository(this, trackDao!!)
    private val ownRepo: UserCardsRepo = UserCardsRepo(ucaDao!!,authDao!!,uctDao!!,cardsViewModel)

    /////////////////////
    val artists: List<Artisti>? = null
    // Variabili LiveData per visualizzare tutti gli artisti e tutte le tracce
    val allartist: MutableStateFlow<List<Artisti>?> = MutableStateFlow(artists)

    val tracks:List<Track>?=null
    val alltrack: MutableStateFlow<List<Track>?> = MutableStateFlow(tracks)


    // Variabili MutableLiveData per visualizzare gli artisti e le tracce filtrati
    val _filteredArtisti = MutableStateFlow<List<Artisti>?>(artists)
    val _filteredBrani = MutableStateFlow<List<Track>?>(tracks)

    private val _showDialog = MutableLiveData(false)
    val showDialog: LiveData<Boolean> get() = _showDialog

    init {
        // Inizializzazione delle tabelle artisti e brani
        //val initArtisti = artistRepo.init()
        //val inittrack = trackRepo.init()
        // Inizializzazione dei LiveData per visualizzare tutti gli artisti e tutte le tracce
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
                requestsReceived.collect{ response ->
                    alltrack.value = response
                    Log.i("TG", "alltrack updated: ${alltrack.value}")
                }
            }
        }

    }

    fun getOnmarketCards() {
        this.viewModelScope.launch {
            withContext(Dispatchers.IO) {

                val marketCards = ownRepo.getAllOnMarketCardsA()
                marketCards?.forEach { elem ->
                    val a =
                        Artisti(
                            0,
                            elem.id_carta,
                            elem.genere,
                            elem.immagine,
                            elem.nome,
                            elem.popolarita
                        )
                    if ((artistRepo.getArtistbyId(elem.id_carta)?.size == 0)) {
                        val temp = allartist.onEach { list ->
                            if (list != null) {
                                list + a
                            }
                        }
                        temp.collect { res ->
                            allartist.value = res

                        }
                    }
                }
            }
        }
    }

    /**
     * Applica i filtri agli artisti e aggiorna la lista visualizzata.
     * @param popThreshold La popolarità massima degli artisti da visualizzare.
     * @param nameQuery La query per filtrare gli artisti per nome.
     * @param genreQuery La query per filtrare gli artisti per genere.
     */
    fun applyArtistFilter(popThreshold: Int?, nameQuery: String?, genreQuery: String?) {

        val filteredArtisti = allartist.value?.filter { artist ->
            val popFilter = popThreshold?.let { artist.popolarita <= it } ?: true
            val nameFilter = nameQuery?.let { artist.nome.contains(it, ignoreCase = true) } ?: true
            val genreFilter = genreQuery?.let { artist.genere.contains(it, ignoreCase = true) } ?: true
            popFilter && nameFilter && genreFilter
        }
        _filteredArtisti.value = if (popThreshold == null && nameQuery.isNullOrEmpty() && genreQuery.isNullOrEmpty()) {
            // Se tutti i filtri sono vuoti, visualizza tutti gli artisti senza applicare alcun filtro
            allartist.value ?: emptyList()
        } else {
            // Altrimenti, applica i filtri normalmente
            filteredArtisti ?: emptyList()
        }
    }

    /**
     * Applica il filtro di popolarità alle tracce e aggiorna la lista visualizzata.
     * @param popThreshold La popolarità massima delle tracce da visualizzare.
     */
    fun applyBraniFilter(popThreshold: Int?) {
        val filteredBrani = alltrack.value?.filter { brano ->
            val popFilter = popThreshold?.let { brano.popolarita <= it } ?: true
            popFilter
        }
        _filteredBrani.value = if (popThreshold == null) {
            alltrack.value ?: emptyList()
        } else {
            filteredBrani ?: emptyList()
        }
        println("Filter applied with popThreshold: $popThreshold, result count: ${_filteredBrani.value?.size}")
    }

    fun compra_track(track:Track){
        viewModelScope.launch {
            val email = loginViewModel.userLoggedInfo.value!!.email

            // Logica sospesa, ad esempio una chiamata al database
            // Aggiorna il database, se necessario

            // Aggiorna le liste di artisti filtrati e le carte acquistate

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
            if(c==0) {
                if (size == null) {

                    val currentFilteredList = alltrack.value

                    // Aggiorna le liste di artisti filtrati e le carte acquistate
                    val updatedFilteredList = currentFilteredList!!.toMutableList().apply {
                        remove(track)
                    }
                    alltrack.value = (updatedFilteredList)
                    trackRepo.delete_track(track)
                    val email = loginViewModel.userLoggedInfo.value!!.email
                    cardsViewModel.insertTrackToUser(track, email)
                } else {
                    val currentFilteredList = _filteredBrani.value
                    val updatedFilteredList = currentFilteredList!!.toMutableList().apply {
                        remove(track)
                    }
                    _filteredBrani.value = (updatedFilteredList)

                    cardsViewModel.insertTrackToUser(track, email)
                    trackRepo.delete_track(track)
                }
            }else{
                _showDialog.value = true // Show dialog

            }



        }
    }
    // Funzione per comprare un artista
    fun compra_artisti(artista: Artisti) {
        viewModelScope.launch {
            val email = loginViewModel.userLoggedInfo.value!!.email

            // Logica sospesa, ad esempio una chiamata al database
            // Aggiorna il database, se necessario

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
            //do nothing//
            if (c == 0) {
                if (size == null) {
                    val currentFilteredList = allartist.value
                    // Aggiorna le liste di artisti filtrati e le carte acquistate
                    val updatedFilteredList = currentFilteredList!!.toMutableList().apply {
                        remove(artista)
                    }
                    allartist.value = (updatedFilteredList)
                    artistRepo.delete_artista(artista)
                    val email = loginViewModel.userLoggedInfo.value!!.email
                    cardsViewModel.insertArtistToUser(artista, email)
                } else {
                    val currentFilteredList = _filteredArtisti.value
                    val updatedFilteredList = currentFilteredList!!.toMutableList().apply {
                        remove(artista)
                    }
                    _filteredArtisti.value = (updatedFilteredList)

                    cardsViewModel.insertArtistToUser(artista, email)
                    artistRepo.delete_artista(artista)
                }
            }else{
                _showDialog.value = true // Show dialog

            }
        }
            }

    fun onDialogDismiss() {
        _showDialog.value = false
    }







}