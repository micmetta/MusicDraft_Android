package com.example.musicdraft.model

import androidx.lifecycle.viewModelScope
import com.example.musicdraft.data.tables.user.UserDao
import com.example.musicdraft.data.tables.user_cards.UCADao
import com.example.musicdraft.data.tables.user_cards.UCTDao
import com.example.musicdraft.data.tables.user_cards.User_Cards_Artisti
import com.example.musicdraft.data.tables.user_cards.User_Cards_Track
import com.example.musicdraft.viewModel.CardsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class UserArtistCardRepo(
    val dao: UCADao,
    val daoLog: UserDao,
    val daoTrack:UCTDao,
    private val cardsViewModel: CardsViewModel


) {
    val CardsForUsersA:List<User_Cards_Artisti>? = null
    val CardsForUsersT:List<User_Cards_Track>? = null

    val allCardsforUserA: MutableStateFlow<List<User_Cards_Artisti>?> = MutableStateFlow(CardsForUsersA)
    val allCardsforUserT: MutableStateFlow<List<User_Cards_Track>?> = MutableStateFlow(CardsForUsersT)

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    // per aggiornare carte friend:
    val CardsForFriendA:List<User_Cards_Artisti>? = null
    val CardsForFriendT:List<User_Cards_Track>? = null

    val allCardsforFriendA: MutableStateFlow<List<User_Cards_Artisti>?> = MutableStateFlow(CardsForFriendA)
    val allCardsforFriendT: MutableStateFlow<List<User_Cards_Track>?> = MutableStateFlow(CardsForFriendT)
    //////////////////////////////////////////////////////////////////////////////////////////////////////


    fun getArtCardsforUser(email:String): List<User_Cards_Artisti>? {
         cardsViewModel.viewModelScope.launch {
             val allcardsforusers = dao.getAllCardArtForUser(email)
             allcardsforusers.collect{ response->
                 allCardsforUserA.value = response
             }
         }
         return allCardsforUserA.value
    }

    /////////////////////////////////////////////////////////////////////////////
    fun getArtCardsforFriend(email_friend:String): List<User_Cards_Artisti>? {
        cardsViewModel.viewModelScope.launch {
            val allcardsforusers = dao.getAllCardArtForUser(email_friend)
            allcardsforusers.collect{ response->
                allCardsforFriendA.value = response
            }
        }
        return allCardsforFriendA.value
    }
    /////////////////////////////////////////////////////////////////////////////


    fun getTrackCardsforUser(email:String): List<User_Cards_Track>? {
        cardsViewModel.viewModelScope.launch {
            val allcardsforusers = daoTrack.getAllCardTrackForUser(email)
            allcardsforusers.collect{ response->
                allCardsforUserT.value = response
            }
        }
        return allCardsforUserT.value
    }

    /////////////////////////////////////////////////////////////////////////////
    fun getTrackCardsforFriend(email_friend:String): List<User_Cards_Track>? {
        cardsViewModel.viewModelScope.launch {
            val allcardsforusers = daoTrack.getAllCardTrackForUser(email_friend)
            allcardsforusers.collect{ response->
                allCardsforFriendT.value = response
            }
        }
        return allCardsforFriendT.value
    }
    /////////////////////////////////////////////////////////////////////////////



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


}