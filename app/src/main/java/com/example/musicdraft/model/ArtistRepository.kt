package com.example.musicdraft.model

import androidx.lifecycle.viewModelScope
import com.example.musicdraft.data.tables.artisti.Artisti
import com.example.musicdraft.data.tables.artisti.ArtistiDao
import com.example.musicdraft.data.tables.user_cards.User_Cards_Artisti
import com.example.musicdraft.viewModel.MarketplaceViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArtistRepository(val viewModel: MarketplaceViewModel, val dao: ArtistiDao) {

    val _artisti:List<Artisti>? = null
    val artisti: MutableStateFlow<List<Artisti>?> = MutableStateFlow(_artisti)


    suspend fun getAllArtisti(): Flow<List<Artisti>> {
        return dao.getAllArtisti()
    }


     fun delete_artista(artista: Artisti){
        viewModel.viewModelScope.launch {
            dao.deleteArtist(artista)
        }
    }

    suspend fun getArtistbyId(id:String): List<Artisti>? {
        viewModel.viewModelScope.launch {
            val result = dao.getallArtistbyId(id)
            result.collect{ response->
                artisti.value = response
            }
        }
        return artisti.value
    }
    /*
    * la funzione init serve a inizializzare il db con dati presi da un file JSOn estrattto da una tabella presa
    * tramite l'api di spotify
    * */

}