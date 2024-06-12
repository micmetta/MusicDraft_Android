package com.example.musicdraft.model

import androidx.lifecycle.viewModelScope
import com.example.musicdraft.data.tables.artisti.Artisti
import com.example.musicdraft.data.tables.handleFriends.HandleFriends
import com.example.musicdraft.data.tables.user.UserDao
import com.example.musicdraft.data.tables.user_cards.UCADao
import com.example.musicdraft.data.tables.user_cards.User_Cards_Artisti
import com.example.musicdraft.viewModel.CardsViewModel
import com.example.musicdraft.viewModel.LoginViewModel
import com.example.musicdraft.viewModel.MarketplaceViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class UserArtistCardRepo(
    val dao: UCADao,
    val daoLog: UserDao,
    private val cardsViewModel: CardsViewModel


) {
    val CardsForUsers:List<Artisti>? = null
    val allCardsforUser: MutableStateFlow<List<Artisti>?> = MutableStateFlow(CardsForUsers)


    fun init(email:String){
        // Avvia una coroutine nel viewModelScope per eseguire la query
        cardsViewModel.viewModelScope.launch {
            // Ottieni la lista di artisti per l'utente
            val artistiFlow = getCardsforUser(email)


            }
        }

     fun getCardsforUser(email:String): List<Artisti>? {
         cardsViewModel.viewModelScope.launch {
             val allcardsforusers = dao.getAllCardArtForUser(email)
             allcardsforusers.collect{ response->
                 allCardsforUser.value = response
             }
         }
         return allCardsforUser.value
    }


    fun insertUserCardArtista(obj : User_Cards_Artisti) {
        cardsViewModel.viewModelScope.launch {
            dao.insertUserCardArt(obj)
        }
     }

    fun updatePoints(point:Int,email:String){
        cardsViewModel.viewModelScope.launch {
            daoLog.updatePoints(point,email)
        }
    }


}