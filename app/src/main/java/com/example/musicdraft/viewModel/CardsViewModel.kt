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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CardsViewModel(application: Application) : AndroidViewModel(application) {
    //prima
    private val database = MusicDraftDatabase.getDatabase(application)
    val artistDao = database.artistDao()
    val trackDao = database.trackDao()
    private val artistRepo: ArtistRepository = ArtistRepository(this, artistDao!!)
    private val trackRepo: TracksRepository = TracksRepository(this, trackDao!!)

    // inizializzazione tabelle artisti e brani
    val initArtisti = artistRepo.init()
    val inittrack = trackRepo.init()

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
}