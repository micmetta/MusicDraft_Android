package com.example.musicdraft.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.musicdraft.database.MusicDraftDatabase
import com.example.musicdraft.model.ArtistRepository
import com.example.musicdraft.model.TracksRepository

class CardsViewModel(application: Application) : AndroidViewModel(application)  {

    private val database = MusicDraftDatabase.getDatabase(application)
    private val artistDao = database.artistDao()
    private val trackDao = database.trackDao()
    private val artistRepo: ArtistRepository = ArtistRepository(this, artistDao!!)
    private val trackRepo: TracksRepository = TracksRepository(this, trackDao!!)
    // inizializzazione tabelle artisti e brani
    val a = artistRepo.init()
    val t = trackRepo.init()


}