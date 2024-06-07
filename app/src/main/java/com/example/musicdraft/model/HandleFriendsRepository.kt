package com.example.musicdraft.model

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.musicdraft.data.tables.handleFriends.HandleFriends
import com.example.musicdraft.data.tables.handleFriends.HandleFriendsDao
import com.example.musicdraft.data.tables.user.User
import com.example.musicdraft.data.tables.user.UserDao
import com.example.musicdraft.viewModel.HandleFriendsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HandleFriendsRepository(val handleFriendsViewModel: HandleFriendsViewModel, val HandleFriendsdao: HandleFriendsDao, val userDao: UserDao){

    val users: List<User>? = null
    val usersFilter: MutableStateFlow<List<User>?> = MutableStateFlow(users)
//    val reqReceivedCurrentUser: MutableStateFlow<List<HandleFriends>?> = MutableStateFlow(null)

    // problema..
    //var reqReceivedCurrentUser = HandleFriendsdao.getRequestReceivedByUser(handleFriendsViewModel.infoUserCurrent!!.email)
    /////////////////////
    val handleFriends: List<HandleFriends>? = null
    val reqReceivedCurrentUser: MutableStateFlow<List<HandleFriends>?> = MutableStateFlow(handleFriends)


    fun insertNewRequest(email1: String, email2: String){
        handleFriendsViewModel.viewModelScope.launch {
            val handleFriends = HandleFriends(
                email1 = email1,
                email2 = email2,
                state = "pending"
            )
            HandleFriendsdao.insertRequest(handleFriends)
        }
    }

    fun getRequestReceivedByUser(email2: String){
        handleFriendsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val requestsReceived = HandleFriendsdao.getRequestReceivedByUser(email2)
                requestsReceived.collect { response ->
                    reqReceivedCurrentUser.value = response
                    Log.i("TG", "reqReceivedCurrentUser updated: $reqReceivedCurrentUser")
                }
            }
        }
    }

    fun getUsersByFilter(filter: String){
        handleFriendsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {

                if(filter.isEmpty()){
                    usersFilter.value = emptyList()
                } else{
                    // mi faccio restituire dalla funzione "getUserByEmail(email)" del DAO
                    // il flow:
                    val usersFilternew = userDao.getAllUsersFilterEmail(filter)
                    usersFilternew.collect { users ->
                        usersFilter.value = users
                        Log.i("TG", "Utenti filtrati: ${usersFilter.value}")
                    }
                }
            }
        }
    }

}