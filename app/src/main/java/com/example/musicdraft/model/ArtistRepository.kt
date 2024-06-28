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

/**
 * Repository per la gestione degli artisti nell'applicazione.
 *
 * @property viewModel Il ViewModel associato a questo repository.
 * @property dao Il DAO per l'accesso alla tabella degli artisti nel database.
 */
class ArtistRepository(val viewModel: MarketplaceViewModel, val dao: ArtistiDao) {
    /** Lista degli artisti inizializzata a null. */
    val _artisti:List<Artisti>? = null

    /** Flusso che contiene la lista degli artisti. */
    val artisti: MutableStateFlow<List<Artisti>?> = MutableStateFlow(_artisti)

    /**
     * Ottiene tutti gli artisti dal database.
     *
     * @return Un flusso contenente la lista degli artisti.
     */
    suspend fun getAllArtisti(): Flow<List<Artisti>> {
        return dao.getAllArtisti()
    }

    /**
     * Elimina un artista dal database.
     *
     * @param artista L'artista da eliminare.
     */
     fun delete_artista(artista: Artisti){
        viewModel.viewModelScope.launch {
            dao.deleteArtist(artista)
        }
    }

    /**
     * Ottiene un artista per ID dal database e aggiorna il flusso [artisti].
     *
     * @param id L'ID dell'artista.
     * @return La lista degli artisti con l'ID specificato.
     */
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