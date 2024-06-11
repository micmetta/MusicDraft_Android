package com.example.musicdraft.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.musicdraft.data.tables.artisti.Artisti
import com.example.musicdraft.data.tables.user_cards.User_Cards_Artisti
import com.example.musicdraft.database.MusicDraftDatabase
import com.example.musicdraft.model.UserArtistCardRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CardsViewModel(application: Application, private val loginViewModel: LoginViewModel): AndroidViewModel(application) {

    private val database = MusicDraftDatabase.getDatabase(application)
    private val dao = database.ownArtCardsDao()

    private val _acquiredCards = MutableLiveData<List<Artisti>>(emptyList())
    val acquiredCards: LiveData<List<Artisti>> get() = _acquiredCards

    private val ownArtistRepo: UserArtistCardRepo = UserArtistCardRepo(dao!!,this)

    val inizializzazione = loginViewModel.userLoggedInfo.value?.let { ownArtistRepo.init(it.email) }


    suspend fun insertArtistToUser(artista:Artisti, email:String){

        withContext(Dispatchers.IO) {
            val inserimento = User_Cards_Artisti(artista.id, email)
            dao?.insertUserCardArt(inserimento)
        }
        }

}




