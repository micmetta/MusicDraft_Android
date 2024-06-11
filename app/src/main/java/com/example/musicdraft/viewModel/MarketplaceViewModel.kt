package com.example.musicdraft.viewModel

import android.app.Application
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.musicdraft.data.tables.artisti.Artisti
import com.example.musicdraft.data.tables.track.Track
import com.example.musicdraft.database.MusicDraftDatabase
import com.example.musicdraft.model.ArtistRepository
import com.example.musicdraft.model.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MarketplaceViewModel(application: Application, private val cardsViewModel: CardsViewModel,private val loginViewModel: LoginViewModel) : AndroidViewModel(application) {



    // Database e repository per artisti e tracce
    private val database = MusicDraftDatabase.getDatabase(application)
    val artistDao = database.artistDao()
    val trackDao = database.trackDao()
    private val artistRepo: ArtistRepository = ArtistRepository(this, artistDao!!)
    private val trackRepo: TracksRepository = TracksRepository(this, trackDao!!)



    // Variabili LiveData per visualizzare tutti gli artisti e tutte le tracce
    val allArtist: LiveData<List<Artisti>>
    val allTracks: LiveData<List<Track>>

    // Variabili MutableLiveData per visualizzare gli artisti e le tracce filtrati
    private val _filteredArtisti = MutableLiveData<List<Artisti>?>()
    val filteredArtisti: MutableLiveData<List<Artisti>?> get() = _filteredArtisti
    private val _filteredBrani = MutableLiveData<List<Track>>()
    val filteredBrani: LiveData<List<Track>> get() = _filteredBrani


    init {
        // Inizializzazione delle tabelle artisti e brani
        val initArtisti = artistRepo.init()
        val inittrack = trackRepo.init()
        // Inizializzazione dei LiveData per visualizzare tutti gli artisti e tutte le tracce
        allArtist = liveData {
            val tracks = withContext(Dispatchers.IO) {
                artistDao?.getAllArtisti()
            }
            emit(tracks ?: emptyList())
        }

        allTracks = liveData {
            val tracks = withContext(Dispatchers.IO) {
                trackDao?.getAllTracks()
            }
            emit(tracks ?: emptyList())
        }

    }



    /**
     * Applica i filtri agli artisti e aggiorna la lista visualizzata.
     * @param popThreshold La popolarità massima degli artisti da visualizzare.
     * @param nameQuery La query per filtrare gli artisti per nome.
     * @param genreQuery La query per filtrare gli artisti per genere.
     */
    fun applyArtistFilter(popThreshold: Int?, nameQuery: String?, genreQuery: String?) {

        val filteredArtisti = allArtist.value?.filter { artist ->
            val popFilter = popThreshold?.let { artist.popolarita <= it } ?: true
            val nameFilter = nameQuery?.let { artist.nome.contains(it, ignoreCase = true) } ?: true
            val genreFilter = genreQuery?.let { artist.genere.contains(it, ignoreCase = true) } ?: true
            popFilter && nameFilter && genreFilter
        }
        _filteredArtisti.value = if (popThreshold == null && nameQuery.isNullOrEmpty() && genreQuery.isNullOrEmpty()) {
            // Se tutti i filtri sono vuoti, visualizza tutti gli artisti senza applicare alcun filtro
            allArtist.value ?: emptyList()
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
        val filteredBrani = allTracks.value?.filter { brano ->
            val popFilter = popThreshold?.let { brano.popolarita <= it } ?: true
            popFilter
        }
        _filteredBrani.value = if (popThreshold == null) {
            allTracks.value ?: emptyList()
        } else {
            filteredBrani ?: emptyList()
        }
        println("Filter applied with popThreshold: $popThreshold, result count: ${_filteredBrani.value?.size}")
    }
    // Funzione per comprare un artista
    fun compra(artista: Artisti) {
        viewModelScope.launch {

                // Logica sospesa, ad esempio una chiamata al database
                // Aggiorna il database, se necessario

                // Aggiorna le liste di artisti filtrati e le carte acquistate

            val size = _filteredArtisti.value?.size
            val currentFilteredList = if( size == null){
                allArtist.value
            }else{
                _filteredArtisti.value
            }
            // Aggiorna le liste di artisti filtrati e le carte acquistate
            val updatedFilteredList = currentFilteredList!!.toMutableList().apply {
                remove(artista)
            }
                (updatedFilteredList)
                loginViewModel.userLoggedInfo.value?.let { cardsViewModel.insertArtistToUser(artista, it.email) }

        }
    }



}