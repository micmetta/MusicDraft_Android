package com.example.musicdraft.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.musicdraft.data.tables.artisti.Artisti
import com.example.musicdraft.data.tables.user_cards.User_Cards_Artisti
import com.example.musicdraft.database.MusicDraftDatabase
import com.example.musicdraft.model.AuthRepository
import com.example.musicdraft.model.UserArtistCardRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

class CardsViewModel(application: Application, private val loginViewModel: LoginViewModel): AndroidViewModel(application) {

    private val database = MusicDraftDatabase.getDatabase(application)
    private val dao = database.ownArtCardsDao()
    private val daoLog =database.userDao()

    private val _acquiredCards : List<Artisti>? = null
    val acquiredCards: MutableStateFlow<List<Artisti>?> = MutableStateFlow(_acquiredCards)

    private val ownArtistRepo: UserArtistCardRepo = UserArtistCardRepo(dao!!,daoLog!!,this)




    fun getallcards() {
        val email = loginViewModel.userLoggedInfo.value!!.email
        acquiredCards.value = ownArtistRepo.getCardsforUser(email)

    }

     fun insertArtistToUser(artista:Artisti, email:String){
         val totalPoint = loginViewModel.userLoggedInfo.value?.points?.minus((artista.popolarita*10))
         if (totalPoint != null) {
             if(totalPoint >= 0) {
                 val inserimento = User_Cards_Artisti(0,artista.id, email)
                 ownArtistRepo.insertUserCardArtista(inserimento)
                 ownArtistRepo.updatePoints(totalPoint,email)

             }
         }
    }

}




