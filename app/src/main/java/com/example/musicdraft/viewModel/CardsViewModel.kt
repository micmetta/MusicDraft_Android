package com.example.musicdraft.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.musicdraft.data.tables.artisti.Artisti
import com.example.musicdraft.data.tables.track.Track
import com.example.musicdraft.data.tables.user_cards.User_Cards_Artisti
import com.example.musicdraft.data.tables.user_cards.User_Cards_Track
import com.example.musicdraft.database.MusicDraftDatabase
import com.example.musicdraft.model.UserArtistCardRepo
import kotlinx.coroutines.flow.MutableStateFlow

class CardsViewModel(application: Application, private val loginViewModel: LoginViewModel): AndroidViewModel(application) {

    private val database = MusicDraftDatabase.getDatabase(application)
    private val dao = database.ownArtCardsDao()
    private val daoLog =database.userDao()
    private val daoTrack =database.ownTrackCardsDao()

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // per prendere le carte "artisti/brani" dell'utente corrente:
    private val _acquiredCardsArtist : List<User_Cards_Artisti>? = null
    val acquiredCardsA: MutableStateFlow<List<User_Cards_Artisti>?> = MutableStateFlow(_acquiredCardsArtist)

    private val _acquiredCardsTrack : List<User_Cards_Track>? = null
    val acquiredCardsT: MutableStateFlow<List<User_Cards_Track>?> = MutableStateFlow(_acquiredCardsTrack)
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // per prendere le carte "artisti/brani" di un amico:
    private val _acquiredCardsArtistFriend : List<User_Cards_Artisti>? = null
    val acquiredCardsAFriend: MutableStateFlow<List<User_Cards_Artisti>?> = MutableStateFlow(_acquiredCardsArtistFriend)

    private val _acquiredCardsTrackFriend : List<User_Cards_Track>? = null
    val acquiredCardsTFriend: MutableStateFlow<List<User_Cards_Track>?> = MutableStateFlow(_acquiredCardsTrackFriend)
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private val ownArtistRepo: UserArtistCardRepo = UserArtistCardRepo(dao!!,daoLog!!,daoTrack!!,this)

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // sottoscrizione alle variabili "infoCardArtistRequest" e "infoCardTrackRequest" sempre del repository
    // "UserArtistCardRepo":
    var infoCardArtistRequest =  ownArtistRepo.infoCardArtistRequest
    var infoCardTrackRequest =  ownArtistRepo.infoCardTrackRequest
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    var listAllInfoAboutCardsArtistOffered =  ownArtistRepo.listAllInfoAboutCardsArtistOffered
    var listAllInfoAboutCardsTracksOffered =  ownArtistRepo.listAllInfoAboutCardsTracksOffered
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun getallcards() {
        val email = loginViewModel.userLoggedInfo.value!!.email
        acquiredCardsA.value = ownArtistRepo.getArtCardsforUser(email)
        acquiredCardsT.value =ownArtistRepo.getTrackCardsforUser(email)
    }

    fun getAllCardFriend(email_friend: String){
        acquiredCardsAFriend.value = ownArtistRepo.getArtCardsforUser(email_friend)
        acquiredCardsTFriend.value =ownArtistRepo.getTrackCardsforUser(email_friend)
    }


     fun insertArtistToUser(artista:Artisti, email:String){
         val totalPoint = loginViewModel.userLoggedInfo.value?.points?.minus((artista.popolarita*10))
         if (totalPoint != null) {
             if(totalPoint >= 0) {
                 val card = User_Cards_Artisti(0,artista.id,artista.genere,artista.immagine,artista.nome,artista.popolarita,email)
                 ownArtistRepo.insertUserCardArtista(card)
                 ownArtistRepo.updatePoints(totalPoint, email)

             }
         }
    }
    fun insertTrackToUser(track: Track, email: String){
        val totalPoint = loginViewModel.userLoggedInfo.value?.points?.minus((track.popolarita*10))
        if (totalPoint != null) {
            if(totalPoint >= 0) {
                val card = User_Cards_Track(0,track.id,track.anno_pubblicazione,track.durata,track.immagine,track.nome,track.popolarita,email)
                ownArtistRepo.insertUserCardTrack(card)
                ownArtistRepo.updatePoints(totalPoint, email)

            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    // prende le info della carta 'artista' che ha email= :email_user e id==idCard
    fun getInfoCardArtistByEmailAndId(email_user: String, idCard: String) {
        ownArtistRepo.getInfoCardArtistByEmailAndId(email_user, idCard)
    }
    // prende le info della carta 'brano' che ha email= :email_user e id==idCard
    fun getInfoCardTrackByEmailAndId(email_user: String, idCard: String) {
        ownArtistRepo.getInfoCardTrackByEmailAndId(email_user, idCard)
    }
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////
    // prende tutte le info di tutte le carte offerte all'utente con 'email_user_offer':
    fun getInfoCardsOfferedByEmailAndIdsAndTypes(email_user_offer: String, listIdsCardsOffered: List<String>, listTypesCardsOffered: List<String>) {
        ownArtistRepo.getInfoCardsOfferedByEmailAndIdsAndTypes(email_user_offer, listIdsCardsOffered, listTypesCardsOffered)
    }
    ////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    fun updateCardArtistOwner(newEmailOwner: String, idCard: String){
        ownArtistRepo.updateCardArtistOwner(newEmailOwner, idCard)
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    fun updateCardTrackOwner(newEmailOwner: String, idCard: String){
        ownArtistRepo.updateCardTrackOwner(newEmailOwner, idCard)
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


}




