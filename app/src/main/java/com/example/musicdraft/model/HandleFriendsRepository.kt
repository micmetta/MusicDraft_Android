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
    val usersFilterbyNickname: MutableStateFlow<List<User>?> = MutableStateFlow(users)
//    val reqReceivedCurrentUser: MutableStateFlow<List<HandleFriends>?> = MutableStateFlow(null)

    // problema..
    //var reqReceivedCurrentUser = HandleFriendsdao.getRequestReceivedByUser(handleFriendsViewModel.infoUserCurrent!!.email)
    /////////////////////
    val handleFriends: List<HandleFriends>? = null
    val reqReceivedCurrentUser: MutableStateFlow<List<HandleFriends>?> = MutableStateFlow(handleFriends)
    val reqSentFromCurrentUser: MutableStateFlow<List<HandleFriends>?> = MutableStateFlow(handleFriends)
    val allFriendsCurrentUser: MutableStateFlow<List<HandleFriends>?> = MutableStateFlow(handleFriends)
    val allPendingRequest: MutableStateFlow<List<HandleFriends>?> = MutableStateFlow(handleFriends)


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
                    Log.i("TG", "reqReceivedCurrentUser updated: ${reqReceivedCurrentUser.value}")
                }
            }
        }
    }

    fun getRequestSent(email1: String){
        handleFriendsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val requestsSent = HandleFriendsdao.getRequestSent(email1)
                requestsSent.collect { response ->
                    reqSentFromCurrentUser.value = response
                    Log.i("TG", "reqReceivedCurrentUser updated: ${reqSentFromCurrentUser.value}")
                }
            }
        }
    }


    fun getAllFriendsByUser(email_user: String){
        handleFriendsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val friends = HandleFriendsdao.getAllFriendsByUser(email_user)
                friends.collect { response ->
                    allFriendsCurrentUser.value = response
                    Log.i("TG", "Tutti gli amici dell'utente corrente: ${allFriendsCurrentUser.value}")
                }
            }
        }
    }

    fun getAllPendingRequestByUser(email_user: String){
        handleFriendsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val pendingRequest = HandleFriendsdao.getAllPendingRequestByUser(email_user)
                pendingRequest.collect { response ->
                    allPendingRequest.value = response
                    Log.i("TG", "Tutte le richieste in attesa sono le seguenti: ${allPendingRequest.value}")
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


    fun getUsersByFilterNickname(filter: String){
        handleFriendsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {

                if(filter.isEmpty()){
                    usersFilterbyNickname.value = emptyList()
                } else{
                    // mi faccio restituire dalla funzione "getAllUsersFilterNickname(nickname: String)" del DAO
                    // il flow:
                    val usersFilternew = userDao.getAllUsersFilterNickname(filter)
                    usersFilternew.collect { users ->
                        usersFilterbyNickname.value = users
                        Log.i("TG", "Utenti filtrati in base ai nicknames: ${usersFilterbyNickname.value}")
                    }
                }
            }
        }
    }


    fun acceptRequest(email1: String, email2: String){
        handleFriendsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO){
                HandleFriendsdao.acceptRequest(email1, email2, "accepted")
            }
        }
    }

    fun refuseRequest(email1: String, email2: String){
        handleFriendsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO){
                HandleFriendsdao.deleteRequest(email1, email2)
            }
        }
    }

    fun deleteFriendship(email1: String, email2: String){
        handleFriendsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO){
                HandleFriendsdao.deleteFriendship(email1, email2)

            }
        }
    }

}