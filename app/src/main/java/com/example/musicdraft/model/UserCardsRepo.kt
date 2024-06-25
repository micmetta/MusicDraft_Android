package com.example.musicdraft.model

import androidx.lifecycle.viewModelScope
import com.example.musicdraft.data.tables.user.UserDao
import com.example.musicdraft.data.tables.user_cards.UCADao
import com.example.musicdraft.data.tables.user_cards.UCTDao
import com.example.musicdraft.data.tables.user_cards.User_Cards_Artisti
import com.example.musicdraft.data.tables.user_cards.User_Cards_Track
import com.example.musicdraft.viewModel.CardsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserCardsRepo(
    val dao: UCADao,
    val daoLog: UserDao,
    val daoTrack:UCTDao,
    private val cardsViewModel: CardsViewModel


) {

    val artCard: List<User_Cards_Artisti>? = null
    private val _allCardsforUserA = MutableStateFlow<List<User_Cards_Artisti>>(emptyList())
    val allCardsforUserA: StateFlow<List<User_Cards_Artisti>> get() = _allCardsforUserA

    val allCardsforUserT: MutableStateFlow<List<User_Cards_Track>?> = MutableStateFlow(emptyList())



    fun getArtCardsforUser(email:String) {
            cardsViewModel.viewModelScope.launch {
                withContext(Dispatchers.IO) {

                    val allcardsforusers = dao.getAllCardArtForUser(email)
                    allcardsforusers.collect { response ->
                        _allCardsforUserA.value = response
                    }
                }
            }

    }
    fun getTrackCardsforUser(email:String) {
        cardsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {

                val allcardsforusers = daoTrack.getAllCardTrackForUser(email)
                allcardsforusers.collect { response ->
                    allCardsforUserT.value = response
                }
            }
        }
    }

    fun insertUserCardTrack(obj : User_Cards_Track) {
        cardsViewModel.viewModelScope.launch {
            daoTrack.insertUserCardTrack(obj)
        }
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

    fun getArtistCardbyId(id_carta:String,email:String): List<User_Cards_Artisti>? {
        cardsViewModel.viewModelScope.launch {
            val allcardsforusers = dao.getCardbyId(id_carta,email)
            allcardsforusers.collect{ response->
                _allCardsforUserA.value = response
            }
        }
        return allCardsforUserA.value
    }
    fun getTrackCardbyId(id_carta:String,email:String): List<User_Cards_Track>? {
        cardsViewModel.viewModelScope.launch {
            val allcardsforusers = daoTrack.getCardbyId(id_carta,email)
            allcardsforusers.collect{ response->
                allCardsforUserT.value = response
            }
        }
        return allCardsforUserT.value
    }


    fun getAllOnMarketCardsA(): List<User_Cards_Artisti>? {
        cardsViewModel.viewModelScope.launch {
            val allcardsforusers = dao.getCardsOnMarket()
            allcardsforusers.collect{ response->
                _allCardsforUserA.value = response
            }
        }
        return allCardsforUserA.value
    }

    fun getAllOnMarketCardsT(): List<User_Cards_Track>? {
        cardsViewModel.viewModelScope.launch {
            val allcardsforusers = daoTrack.getCardsOnMarket()
            allcardsforusers.collect{ response->
                allCardsforUserT.value = response
            }
        }
        return allCardsforUserT.value
    }

     fun updateMarketStateA(email:String,id_carta: String){
        cardsViewModel.viewModelScope.launch {
            dao.updateOnMarkeState(email,id_carta)
        }
    }
    fun updateMarketNotStateA(email:String,id_carta: String){
        cardsViewModel.viewModelScope.launch {
            dao.updateNotOnMarkeState(email,id_carta)
        }
    }
    fun updateMarketStateT(email:String,id_carta: String){
        cardsViewModel.viewModelScope.launch {
            daoTrack.updateOnMarkeState(email,id_carta)
        }
    }
    fun updateMarketNotStateT(email:String,id_carta: String){
        cardsViewModel.viewModelScope.launch {
            daoTrack.updateNotOnMarkeState(email,id_carta)
        }
    }
}