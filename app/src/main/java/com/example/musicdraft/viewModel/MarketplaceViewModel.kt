package com.example.musicdraft.viewModel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.musicdraft.data.tables.artisti.Artisti
import com.example.musicdraft.data.tables.handleFriends.HandleFriends
import com.example.musicdraft.data.tables.track.Track
import com.example.musicdraft.database.MusicDraftDatabase
import com.example.musicdraft.model.ArtistRepository
import com.example.musicdraft.model.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MarketplaceViewModel(application: Application, private val cardsViewModel: CardsViewModel,private val loginViewModel: LoginViewModel) : AndroidViewModel(application) {



    // Database e repository per artisti e tracce
    private val database = MusicDraftDatabase.getDatabase(application)
    val artistDao = database.artistDao()
    val trackDao = database.trackDao()
    private val artistRepo: ArtistRepository = ArtistRepository(this, artistDao!!)
    private val trackRepo: TracksRepository = TracksRepository(this, trackDao!!)


    /////////////////////
    val artists: List<Artisti>? = null
    // Variabili LiveData per visualizzare tutti gli artisti e tutte le tracce
    val allartist: MutableStateFlow<List<Artisti>?> = MutableStateFlow(artists)

    val tracks:List<Track>?=null
    val alltrack: MutableStateFlow<List<Track>?> = MutableStateFlow(tracks)


    // Variabili MutableLiveData per visualizzare gli artisti e le tracce filtrati
    val _filteredArtisti = MutableStateFlow<List<Artisti>?>(artists)
    val _filteredBrani = MutableStateFlow<List<Track>?>(tracks)



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

            // Logica sospesa, ad esempio una chiamata al database
            // Aggiorna il database, se necessario

            // Aggiorna le liste di artisti filtrati e le carte acquistate

            val size = _filteredBrani.value?.size
            if( size == null){
                val currentFilteredList = alltrack.value
                // Aggiorna le liste di artisti filtrati e le carte acquistate
                val updatedFilteredList = currentFilteredList!!.toMutableList().apply {
                    remove(track)
                }
                alltrack.value = (updatedFilteredList)
                trackRepo.delete_track(track)
                val email = loginViewModel.userLoggedInfo.value!!.email
                cardsViewModel.insertTrackToUser(track, email)
            }else{
                val currentFilteredList =_filteredBrani.value
                val updatedFilteredList = currentFilteredList!!.toMutableList().apply {
                    remove(track)
                }
                _filteredBrani.value = (updatedFilteredList)

                val email = loginViewModel.userLoggedInfo.value!!.email
                cardsViewModel.insertTrackToUser(track, email)
                trackRepo.delete_track(track)
            }



        }
    }
    // Funzione per comprare un artista
    fun compra_artisti(artista: Artisti) {
        viewModelScope.launch {

                // Logica sospesa, ad esempio una chiamata al database
                // Aggiorna il database, se necessario

                // Aggiorna le liste di artisti filtrati e le carte acquistate

            val size = _filteredArtisti.value?.size
            if( size == null){
                val currentFilteredList = allartist.value
                // Aggiorna le liste di artisti filtrati e le carte acquistate
                val updatedFilteredList = currentFilteredList!!.toMutableList().apply {
                    remove(artista)
                }
                allartist.value = (updatedFilteredList)
                artistRepo.delete_artista(artista)
                val email = loginViewModel.userLoggedInfo.value!!.email
                cardsViewModel.insertArtistToUser(artista, email)
            }else{
                val currentFilteredList =_filteredArtisti.value
                val updatedFilteredList = currentFilteredList!!.toMutableList().apply {
                    remove(artista)
                }
                _filteredArtisti.value = (updatedFilteredList)

                val email = loginViewModel.userLoggedInfo.value!!.email
                cardsViewModel.insertArtistToUser(artista, email)
                artistRepo.delete_artista(artista)
            }



        }
    }



}