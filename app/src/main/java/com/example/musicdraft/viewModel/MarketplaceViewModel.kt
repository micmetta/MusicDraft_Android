package com.example.musicdraft.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.musicdraft.data.tables.artisti.Artisti
import com.example.musicdraft.data.tables.track.Track
import com.example.musicdraft.database.MusicDraftDatabase
import com.example.musicdraft.model.ArtistRepository
import com.example.musicdraft.model.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MarketplaceViewModel(application: Application) : AndroidViewModel(application) {



    //prima
    private val database = MusicDraftDatabase.getDatabase(application)
    val artistDao = database.artistDao()
    val trackDao = database.trackDao()
    private val artistRepo: ArtistRepository = ArtistRepository(this, artistDao!!)
    private val trackRepo: TracksRepository = TracksRepository(this, trackDao!!)

    // inizializzazione tabelle artisti e brani
    val initArtisti = artistRepo.init()
    val inittrack = trackRepo.init()
    private val _filteredArtisti = MutableLiveData<List<Artisti>>()
    val filteredArtisti: LiveData<List<Artisti>> get() = _filteredArtisti
    private val _filteredBrani = MutableLiveData<List<Track>>()
    val filteredBrani: LiveData<List<Track>> get() = _filteredBrani
    val allArtist: LiveData<List<Artisti>>
    val allTracks: LiveData<List<Track>>

    init {
        allArtist = liveData {
            val artists = withContext(Dispatchers.IO) {
                artistDao?.getAllArtisti()
            }
            emit(artists ?: emptyList())
        }

        allTracks = liveData {
            val tracks = withContext(Dispatchers.IO) {
                trackDao?.getAllTracks()
            }
            emit(tracks ?: emptyList())
        }

    }
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


    fun applyBraniFilter(popThreshold: Int?) {
        val filteredBrani = allTracks.value?.filter { brano ->
            val popFilter = popThreshold?.let { brano.popolarita <= it } ?: true
            popFilter
        }
        _filteredBrani.value = if (popThreshold == null) {
            // Se il campo di filtro della popolarità è vuoto, visualizza tutte le tracce senza applicare alcun filtro
            allTracks.value ?: emptyList()
        } else {
            // Altrimenti, applica il filtro normalmente
            filteredBrani ?: emptyList()
        }
    }
}