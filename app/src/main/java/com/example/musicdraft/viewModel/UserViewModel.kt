//package com.example.musicdraft.viewModel
//
//import androidx.compose.runtime.State
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.musicdraft.data.tables.user.SortType
//import com.example.musicdraft.data.tables.user.User
//import com.example.musicdraft.data.tables.user.UserDao
//import com.example.musicdraft.data.tables.user.UserEvent
//import com.example.musicdraft.data.tables.user.UserState
//import com.example.musicdraft.login.GoogleSignInState
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//
//class UserViewModel(private val dao: UserDao): ViewModel(){
//
//    // flow privato alla classe corrente:
//    private val _state = MutableStateFlow(UserState())
//    // rendo il flow pubblico:
//    val state: StateFlow<UserState> get() = _state
//
//    fun onEvent(event: UserEvent){
//        when(event) {
//            is UserEvent.DeleteUser -> {
//                viewModelScope.launch {
//                    dao.deleteUser(event.user)
//                }
//            }
//
//            is UserEvent.SaveUser -> {
//                val email = state.value.email
//                val nickname = state.value.nickname
//                val password = state.value.password
//                val points = state.value.points
//                if(email.isBlank() || nickname.isBlank() || password.isBlank()){
//                    return
//                }
//
//                ////////////////////////////////////////////////////
//                // inserisco il nuovo utente nel DB:
//                val user = User(
//                    email = email,
//                    nickname = nickname,
//                    password = password,
//                    points = 1000 // I points iniziali sono 1000
//                )
//                viewModelScope.launch {
//                    dao.insertUser(user)
//                }
//                ////////////////////////////////////////////////////
//
//            }
//            is UserEvent.SetEmailUser -> {
//                _state.update { it.copy(
//                    email = event.email
//                )}
//            }
//            is UserEvent.SetNicknamedUser -> {
//                _state.update { it.copy(
//                    nickname = event.nickname
//                )}
//            }
//            is UserEvent.SetPasswordUser -> {
//                _state.update { it.copy(
//                    password = event.password
//                )}
//            }
//            is UserEvent.SetPointsUser -> {
//                _state.update { it.copy(
//                    points = event.points.toInt()
//                )}
//            }
//
//            //            UserEvent.HideDialog -> {
////                _state.update { it.copy(
////
////                ) }
////            }
//
////            UserEvent.ShowDialog -> TODO()
////            is UserEvent.SortUsers -> {
////                _sortType.value = event.sortType
////            }
//        }
//    }
//}