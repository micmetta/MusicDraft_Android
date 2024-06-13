package com.example.musicdraft.model

import androidx.lifecycle.viewModelScope
import com.example.musicdraft.data.tables.artisti.Artisti
import com.example.musicdraft.data.tables.artisti.ArtistiDao
import com.example.musicdraft.viewModel.MarketplaceViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArtistRepository(val viewModel: MarketplaceViewModel, val dao: ArtistiDao) {
    suspend fun getAllArtisti(): Flow<List<Artisti>> {
        return dao.getAllArtisti()
    }


     fun delete_artista(artista: Artisti){
        viewModel.viewModelScope.launch {
            dao.deleteArtist(artista)
        }
    }
    /*
    * la funzione init serve a inizializzare il db con dati presi da un file JSOn estrattto da una tabella presa
    * tramite l'api di spotify
    * */

}