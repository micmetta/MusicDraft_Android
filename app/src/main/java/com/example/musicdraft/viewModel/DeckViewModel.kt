import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicdraft.data.tables.deck.Deck
import com.example.musicdraft.data.tables.user_cards.User_Cards_Artisti
import com.example.musicdraft.data.tables.user_cards.User_Cards_Track
import com.example.musicdraft.database.MusicDraftDatabase
import com.example.musicdraft.model.DeckRepo
import com.example.musicdraft.model.UserCardsRepo
import com.example.musicdraft.viewModel.CardsViewModel
import com.example.musicdraft.viewModel.LoginViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class DeckViewModel(
    application: Application,
    private val loginViewModel: LoginViewModel,
    private val cardsViewModel: CardsViewModel
) : AndroidViewModel(application) {

    private val database = MusicDraftDatabase.getDatabase(application)
    private val daoOwnCards = database.ownArtCardsDao()
    private val daoDeck = database.deckDao()
    private val loginDao = database.userDao()
    private val dao = database.ownArtCardsDao()
    private val daoLog = database.userDao()
    private val daoTrack = database.ownTrackCardsDao()

    private val deckRepository: DeckRepo = DeckRepo(this, daoDeck!!)
    private val ownArtistRepo: UserCardsRepo = UserCardsRepo(dao!!, daoLog!!, daoTrack!!, cardsViewModel)

    var isEditing = mutableStateOf(false)

    private val _selectedDeck : Mazzo? =null
    val selectedDeck: MutableStateFlow<Mazzo?> = MutableStateFlow(_selectedDeck)

    private val _ownDeck : List<Deck>? = null
    val ownDeck: MutableStateFlow<List<Deck>?> = MutableStateFlow(_ownDeck)

    private val _ownCardsA = MutableStateFlow<List<User_Cards_Artisti>?>(null)
    val ownCardA: StateFlow<List<User_Cards_Artisti>?> get() = _ownCardsA

    private val _ownCardsT = MutableStateFlow<List<User_Cards_Track>?>(null)
    val ownCardT: StateFlow<List<User_Cards_Track>?> get() = _ownCardsT

    private val _cards:List<Cards>?=null
    val cards: MutableStateFlow<List<Cards>?> = MutableStateFlow(_cards)

    private val _selectedCards = MutableStateFlow<List<Cards>>(emptyList())
    val selectedCards: StateFlow<List<Cards>> get() = _selectedCards

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> get() = _message

    private val _deckName = MutableStateFlow("")
    val deckName: StateFlow<String> get() = _deckName


    var mazzi: MutableList<Mazzo> = emptyList<Mazzo>().toMutableList()
    val cardList: MutableList<Cards> = emptyList<Cards>().toMutableList()

    ////////////////////////////////////////////////////////////////////////////////////////
    // mi serve per sapere se una carta si trova in un qualsiasi mazzo dell'utente corrente:
    var isInDeck = deckRepository.isInDeck
    ////////////////////////////////////////////////////////////////////////////////////////



    // Inner classes
    inner class Cards(
        val id_carta: String,
        val nome_carta: String,
        val immagine: String,
        val popolarita: Int
    )

    inner class Mazzo(
        var id_mazzo: String,
        val carte: List<Cards>
    )

    fun init() {
        val email = loginViewModel.userLoggedInfo.value!!.email
        ownArtistRepo.getArtCardsforUser(email)

        // Launch a coroutine to collect the flow and update the state
        viewModelScope.launch {
            ownArtistRepo.allCardsforUserA.collect { allArtisti ->
                val ownCardsA = allArtisti?.filter { elem ->
                    !elem.onMarket
                }
                _ownCardsA.value = ownCardsA
            }
        }
        ownArtistRepo.getTrackCardsforUser(email)

        viewModelScope.launch {
            ownArtistRepo.allCardsforUserT.collect { allArtisti ->
                val ownCardsT = allArtisti?.filter { elem ->
                    !elem.onMarket
                }
                _ownCardsT.value = ownCardsT
            }
        }

            val listofCards:MutableList<Cards>?= mutableListOf()
            ownCardA.value?.forEach{elem->

                val c = Cards(elem.id_carta,elem.nome,elem.immagine,elem.popolarita)

                listofCards?.add(c)
            }
            ownCardT.value?.forEach{elem->

            val c = Cards(elem.id_carta,elem.nome,elem.immagine,elem.popolarita)

            listofCards?.add(c)
            }


            cards.value = listofCards
            Log.i("cards","${cards.value}")


            deckRepository.getallDecksByEmail(email!!)
            ownDeck.value = deckRepository.allDecks.value
            Log.i("d","${ownDeck.value}")
            deckRepository.getNomedeck(email)
            val n = deckRepository.namesDecks?.value
            Log.i("n","${n}")

            if (n != null) {
                for(i in n){
                    Log.i("nome","${i}")

                    deckRepository.getCarteAss(email,i)
                    val c = deckRepository.carteAssociate?.value
                    Log.i("t","${c}")

                    if (c != null) {
                        val exe =cards.value


                        for(j in c){

                            exe!!.forEach { card->
                                Log.i("teramo","${j}")

                                if (card.id_carta == j) {
                                    cardList!!.add(card)
                                    Log.i("to","${cardList}")

                                }
                            }

                        }

                    }
                    cardList?.let { Mazzo(i, it) }?.let { mazzi?.add(it) }
                }
            }

            Log.i("taggone","${mazzi}")








    }

    fun toggleCardSelection(card: Cards) {
        val currentList = _selectedCards.value.toMutableList()
        if (currentList.contains(card)) {
            currentList.remove(card)
        } else {
            currentList.add(card)
        }
        _selectedCards.value = currentList
    }

    fun getCardById(id: String): Cards? {
        return cards.value?.find { it.id_carta == id }
    }

    fun creaNuovoMazzo() {

        selectedDeck.value = Mazzo("", emptyList())
        isEditing.value = true
    }



    fun modificaMazzo(deck: Mazzo) {

    }

    fun eliminaMazzo(deck: Mazzo) {

    }

    fun salvaMazzo() {
        val email = loginViewModel.userLoggedInfo.value!!.email
        val names = deckRepository.namesDecks?.value
        val currentDeck = selectedDeck.value
        val currentCards = selectedCards.value

        Log.i("nomi","${names}")

        if (currentDeck != null) {
            // Check if deck name is unique
            mazzi?.forEach { elem->
                if(names?.contains(elem.id_mazzo) == true){
                    _message.value = "Deck name already exists!"
                    return
                }

            }

            // Check if exactly 5 unique cards are selected
            if (currentCards.distinctBy { it.id_carta }.size != 5) {
                _message.value="Exactly 5 unique cards must be selected!"
                return
            }

            var pop:Float = 0F
            for (i in selectedCards.value){
                pop += i.popolarita
            }
            val temp: MutableList<Deck> = emptyList<Deck>().toMutableList()
            for (i in selectedCards.value){
                Log.i("nome","${_deckName.value}")
               val m = Deck(0,_deckName.value,i.id_carta,email,pop/5)
                Log.i("m","${m}")
                if (m != null) {
                    temp.add(m)
                }
            }
            Log.i("temp","${temp}")
            deckRepository.insertAllNewDeck(temp)

            mazzi?.add(Mazzo(deckName.value,_selectedCards.value))

            // Reset the states
            annullaModifica()
            _message.value= "Deck saved Succefully"
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    fun annullaModifica() {
        isEditing.value = false
        selectedDeck.value = null
        _deckName.value = ""
    }
    fun updateDeckName(newName: String) {
        _deckName.value = newName
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Verifica se una carta specifica è presente in un mazzo dell'utente.
     *
     * @param userEmail L'email dell'utente.
     * @param card La carta da verificare.
     * @return True se la carta è presente nel mazzo, false altrimenti.
     */
    suspend fun checkCardInDeck(userEmail: String, card: String): Boolean {
        return deckRepository.isCardInDeck(userEmail, card)
    }
    //////////////////////////////////////////////////////////////////////////////////////////



    /*



      var selectedCards = mutableStateOf(listOf<Card>())

      fun creaNuovoMazzo() {
          selectedDeck.value = Deck("", emptyList())
          isEditing.value = true
      }

      fun modificaMazzo(deck: Deck) {
          selectedDeck.value = deck
          selectedCards.value = deck.carteassociate
          isEditing.value = true
      }

      fun eliminaMazzo(deck: Deck) {
          decks.value = decks.value.filter { it != deck }
      }

      fun salvaMazzo() {
          selectedDeck.value?.let {
              if (!decks.value.contains(it)) {
                  decks.value = decks.value + it
              }
              isEditing.value = false
              selectedDeck.value = null
              selectedCards.value = listOf()
          }
      }

      fun annullaModifica() {
          isEditing.value = false
          selectedDeck.value = null
          selectedCards.value = listOf()
      }

      fun toggleCardSelection(card: Card) {
          selectedCards.value = if (selectedCards.value.contains(card)) {
              selectedCards.value - card
          } else {
              selectedCards.value + card
          }
      }*/
}
