package com.example.musicdraft.model

import androidx.lifecycle.viewModelScope
import com.example.musicdraft.data.tables.artisti.Artisti
import com.example.musicdraft.data.tables.user_cards.UCADao
import com.example.musicdraft.viewModel.CardsViewModel
import com.example.musicdraft.viewModel.MarketplaceViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class UserArtistCardRepo(
    val dao: UCADao,
    private val cardsViewModel: CardsViewModel
) {

    fun init(email:String){
        // Avvia una coroutine nel viewModelScope per eseguire la query
        cardsViewModel.viewModelScope.launch {
            // Ottieni la lista di artisti per l'utente
            val artistiFlow = getCardsforUser(email)


            }
        }

    suspend fun getCardsforUser(email:String): Flow<List<Artisti>> {
        return dao.getAllCardArtForUser(email)
    }


}