package com.example.musicdraft.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.musicdraft.data.tables.artisti.Artisti
import com.example.musicdraft.data.tables.track.Track
import com.example.musicdraft.data.tables.user_cards.User_Cards_Artisti
import com.example.musicdraft.data.tables.user_cards.User_Cards_Track
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
    private val daoTrack =database.ownTrackCardsDao()

    private val _acquiredCardsArtist : List<User_Cards_Artisti>? = null
    val acquiredCardsA: MutableStateFlow<List<User_Cards_Artisti>?> = MutableStateFlow(_acquiredCardsArtist)

    private val _acquiredCardsTrack : List<User_Cards_Track>? = null
    val acquiredCardsT: MutableStateFlow<List<User_Cards_Track>?> = MutableStateFlow(_acquiredCardsTrack)

    private val ownArtistRepo: UserArtistCardRepo = UserArtistCardRepo(dao!!,daoLog!!,daoTrack!!,this)




    fun getallcards() {
        val email = loginViewModel.userLoggedInfo.value!!.email
        acquiredCardsA.value = ownArtistRepo.getArtCardsforUser(email)
        acquiredCardsT.value =ownArtistRepo.getTrackCardsforUser(email)

    }

     fun insertArtistToUser(artista:Artisti, email:String){
         val totalPoint = loginViewModel.userLoggedInfo.value?.points?.minus((artista.popolarita*10))
         if (totalPoint != null) {
             if(totalPoint >= 0) {
                 val card = User_Cards_Artisti(0,artista.id,artista.genere,artista.immagine,artista.nome,artista.popolarita,email)
                 ownArtistRepo.insertUserCardArtista(card)
                 ownArtistRepo.updatePoints(totalPoint,email)

             }
         }
    }
    fun insertTrackToUser(track: Track, email: String){
        val totalPoint = loginViewModel.userLoggedInfo.value?.points?.minus((track.popolarita*10))
        if (totalPoint != null) {
            if(totalPoint >= 0) {
                val card = User_Cards_Track(0,track.id,track.anno_pubblicazione,track.durata,track.immagine,track.nome,track.popolarita,email)
                ownArtistRepo.insertUserCardTrack(card)
                ownArtistRepo.updatePoints(totalPoint,email)

            }
        }
    }

}



