package com.example.musicdraft.model

import androidx.lifecycle.viewModelScope
import com.example.musicdraft.data.tables.track.Track
import com.example.musicdraft.data.tables.track.TrackDao
import com.example.musicdraft.viewModel.MarketplaceViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TracksRepository(val viewModel: MarketplaceViewModel, val dao: TrackDao) {
    suspend fun getAllTracks(): Flow<List<Track>> {
        return dao.getAllTracks()
    }
    suspend fun delete_track(track:Track){
        dao.deleteTrack(track)
    }
}