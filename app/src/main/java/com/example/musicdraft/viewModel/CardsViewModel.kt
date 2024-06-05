package com.example.musicdraft.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.musicdraft.data.tables.artisti.Artisti
import com.example.musicdraft.data.tables.track.Track
import com.example.musicdraft.database.MusicDraftDatabase
import com.example.musicdraft.model.ArtistRepository
import com.example.musicdraft.model.TracksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CardsViewModel(application: Application) : AndroidViewModel(application)  {
    //prima
    private val database = MusicDraftDatabase.getDatabase(application)
    val artistDao = database.artistDao()
    val trackDao = database.trackDao()
    private val artistRepo: ArtistRepository = ArtistRepository(this, artistDao!!)
    private val trackRepo: TracksRepository = TracksRepository(this, trackDao!!)
    // inizializzazione tabelle artisti e brani
    val initArtisti = artistRepo.init()
    val inittrack = trackRepo.init()


    private val _allArtists = MutableStateFlow<List<Artisti>>(emptyList())
    val allArtists: StateFlow<List<Artisti>> = _allArtists

    private val _allTracks = MutableStateFlow<List<Track>>(emptyList())
    val allTracks: StateFlow<List<Track>> = _allTracks

    init {
        fetchAllArtisti()
        fetchAllTracks()
    }

    private fun fetchAllArtisti() {
        viewModelScope.launch {
            _allArtists.value = artistRepo.getAllArtisti()
        }
    }

    private fun fetchAllTracks() {
        viewModelScope.launch {
            _allTracks.value = trackRepo.getAllTracks()
        }
    }

}