package com.example.musicdraft.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.musicdraft.database.MusicDraftDatabase
import com.example.musicdraft.model.HandleFriendsRepository

class HandleFriendsViewModel(application: Application) : AndroidViewModel(application) {

    //////////////////////////////////////////////////////
    // istanziazione del repository 'handleFriendsRepository' e dao 'handleFriendsDao'
    private val database = MusicDraftDatabase.getDatabase(application)
    private val handleFriendsDao = database.handleFriendsDao()
    private val userDao = database.userDao()
    private val handleFriendsRepository: HandleFriendsRepository = HandleFriendsRepository(this, handleFriendsDao!!, userDao!!) // istanzio il repository
    var usersFilter =  handleFriendsRepository.usersFilter
    //var reqReceivedCurrentUser =  handleFriendsRepository.reqReceivedCurrentUser
    //////////////////////////////////////////////////////

    private val loginViewModel : LoginViewModel = LoginViewModel(application)

    //val infoUserCurrent = loginViewModel.userLoggedInfo.value // c'era prima.. problema
    var reqReceivedCurrentUser = handleFriendsRepository.reqReceivedCurrentUser

//    var reqReceivedCurrentUser = handleFriendsRepository.reqReceivedCurrentUser
//        private set

    //////////////////////////////////////////////////////
//    // Per cercare un utente nel DB:
//    private val _searchText = MutableStateFlow("")
//    val searchText = _searchText.asStateFlow()
//    private val _isSearching = MutableStateFlow(false)
//    val isSearching = _isSearching.asStateFlow()
//    private val _users = MutableStateFlow(listOf<User>())
//    val users = searchText
//        .combine(_users){ text, users ->
//            // questo codice verrà eseguito se ci sarà un cambiamento o in 'searchText' o 'in _users'
//            if(text.isBlank()) {
//                users
//            }else{
//                users.filter {
//                    it.doesMatchSearchQUery(text)
//                }
//            }
//        }
//        .stateIn(
//            viewModelScope,
//            SharingStarted.WhileSubscribed(5000),
//            _users.value
//        )
    //////////////////////////////////////////////////////

    fun insertNewRequest(email1: String, email2: String){
        handleFriendsRepository.insertNewRequest(email1, email2)
    }

    // per cercare un utente nel DB:
    fun onSearchTextChange(filter: String){
        //_searchText.value = text
        handleFriendsRepository.getUsersByFilter(filter)
    }

    fun getRequestReceivedByUser(email2: String){
        handleFriendsRepository.getRequestReceivedByUser(email2)
    }
}