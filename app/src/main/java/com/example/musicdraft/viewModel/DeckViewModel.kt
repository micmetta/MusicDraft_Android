
import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicdraft.data.tables.deck.Deck
import com.example.musicdraft.data.tables.exchange_management_cards.ExchangeManagementCards
import com.example.musicdraft.data.tables.user_cards.User_Cards_Artisti
import com.example.musicdraft.data.tables.user_cards.User_Cards_Track
import com.example.musicdraft.database.MusicDraftDatabase
import com.example.musicdraft.model.DeckRepo
import com.example.musicdraft.model.UserCardsRepo
import com.example.musicdraft.viewModel.CardsViewModel
import com.example.musicdraft.viewModel.ExchangeManagementCardsViewModel
import com.example.musicdraft.viewModel.LoginViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class DeckViewModel(
    application: Application,
    private val loginViewModel: LoginViewModel,
    private val cardsViewModel: CardsViewModel,
    private val exchangeManagementCardsViewModel: ExchangeManagementCardsViewModel
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
    var isMod = mutableStateOf(false)


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

    private val _isCreatingNewDeck = MutableLiveData(false)
    val isCreatingNewDeck: LiveData<Boolean> get() = _isCreatingNewDeck

    private val _selectedCards = MutableStateFlow<List<Cards>>(emptyList())
    val selectedCards: StateFlow<List<Cards>> get() = _selectedCards

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> get() = _message

    private val _deckName = MutableStateFlow("")
    val deckName: StateFlow<String> get() = _deckName

    var mazzi: MutableList<Mazzo> = emptyList<Mazzo>().toMutableList()
    val cardList: MutableList<Cards> = emptyList<Cards>().toMutableList()


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
        val cardList: MutableList<Cards> = emptyList<Cards>().toMutableList()
        mazzi.clear()
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

        val listofCards: MutableList<Cards>? = mutableListOf()
        ownCardA.value?.forEach { elem ->
            val c = Cards(elem.id_carta, elem.nome, elem.immagine, elem.popolarita)
            listofCards?.add(c)
        }
        ownCardT.value?.forEach { elem ->
            val c = Cards(elem.id_carta, elem.nome, elem.immagine, elem.popolarita)
            listofCards?.add(c)
        }

        cards.value = listofCards
        Log.i("cards", "${cards.value}")

        deckRepository.getallDecksByEmail(email!!)
        ownDeck.value = deckRepository.allDecks.value
        Log.i("d", "${ownDeck.value}")
        deckRepository.getNomedeck(email)
        val n = deckRepository.namesDecks?.value
        Log.i("n", "${n}")
        var c = 0
        for(i in ownDeck.value!!){

            cards.value!!.forEach { elem ->
                if (elem.id_carta == i.carte_associate) {
                    cardList.add(elem)
                    Log.i("gerry", "${elem.immagine}")
                    c = c+1
                }
            }
            if(c==5) {
                // Creare una copia profonda di cardList
                val deepCopyCardList =
                    cardList.map { Cards(it.id_carta, it.nome_carta, it.immagine, it.popolarita) }
                        .toMutableList()
                mazzi.add(Mazzo(i.nome_mazzo, deepCopyCardList))
                cardList.clear()
                c = 0
            }
            }
    }



    fun toggleCardSelection(
        card: Cards,
        reqSentCurrentUser: List<ExchangeManagementCards>?,
        reqReceivedCurrentUser: List<ExchangeManagementCards>?
    ) {
        val currentSelectedCards = _selectedCards.value?.toMutableList() ?: mutableListOf()

        // Check if the card is in another deck
        val cardId = card.id_carta
        val isCardInAnotherDeck = mazzi.any { deck ->
            deck.id_mazzo != selectedDeck.value!!.id_mazzo && deck.carte.any { it.id_carta == cardId }
        }

        ////////////////////////////////////////////////////////////////////////////////
//        Log.d("toggleCardSelection", "reqReceivedCurrentUser: ${reqReceivedCurrentUser}")
//        Log.d("toggleCardSelection", "reqSentCurrentUser: ${reqSentCurrentUser}")

        // Controllo che la carta che si vuole aggiungere al mazzo non sia presente:
        // 1) In una qualsiasi offerta ricevuta dall'utente corrente (come carta richiesta)
        // 2) In una qualsiasi offerta inviata dall'utente corrente (come una carta offerta)
        val isCardInAnReceivedOffer = reqReceivedCurrentUser?.any { reicevdOffer ->
            reicevdOffer.idRequiredCard == cardId
        }
        val isCardInAnSentOffer = reqSentCurrentUser?.any{ sentOffer ->
            sentOffer.listOfferedCards.any { offerdCard ->
                offerdCard == cardId
            }
        }
//        Log.d("toggleCardSelection", "isCardInAnReceivedOffer: ${isCardInAnReceivedOffer}")
//        Log.d("toggleCardSelection", "isCardInAnSentOffer: ${isCardInAnSentOffer}")
        ////////////////////////////////////////////////////////////////////////////////

        if (false) {
            _message.value = "Questa carta è già presente in un altro mazzo"
        }
        ////////////////////////////////////////////////////////////////////////////////
        else if(isCardInAnReceivedOffer == true){
            _message.value = "This card was requested from you in some offer you received!\n\n" +
                             "Before adding it to a deck you must cancel all offers received in which this card was requested."
        }
        else if(isCardInAnSentOffer == true){
            _message.value = "You offered this card in some offer you sent!\n\n" +
                             "Before adding it to a deck you must delete all offers sent in which you have offered this card."
        }
        ////////////////////////////////////////////////////////////////////////////////
        else {
            if (currentSelectedCards.contains(card)) {
                currentSelectedCards.remove(card)
            } else {
                currentSelectedCards.add(card)
            }
            _selectedCards.value = currentSelectedCards
        }
    }



    fun creaNuovoMazzo() {

        selectedDeck.value = Mazzo("", emptyList())
        isEditing.value = true
        isMod.value =false
        _selectedCards.value= emptyList<Cards>().toMutableList()
        _isCreatingNewDeck.value = true

    }

    fun cancelEditing() {
        isEditing.value = false
        selectedDeck.value = null
    }
    fun startEditingDeck(deck: Mazzo) {
        selectedDeck.value = deck
        isEditing.value = true
        _isCreatingNewDeck.value = false
        updateAvailableAndSelectedCards()
    }

    private fun updateAvailableAndSelectedCards() {
        val allCards = cards.value ?: emptyList()
        val deckCards = selectedDeck.value?.carte ?: emptyList()

        _selectedCards.value = deckCards
        cards.value = allCards.filter { card -> !deckCards.contains(card) }
    }

    fun modificaMazzo(deck: Mazzo) {

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
                if(names?.contains(deckName.value) == true){
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
            _selectedCards.value = emptyList()
            _message.value= "Deck saved Succefully"
        }
    }

    fun modificaMazzo() {
        val email = loginViewModel.userLoggedInfo.value!!.email
        val SC =_selectedCards.value
        Log.i("nome_vecchio","${selectedDeck.value!!.id_mazzo}")
        mazzi.removeIf { it.id_mazzo == selectedDeck.value!!.id_mazzo }
        deckRepository.deleteDeck(selectedDeck.value!!.id_mazzo,email)
        salvaMazzo()


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


    fun updateIsEditing(deck: Mazzo){
        selectedDeck.value = deck
        isMod.value = !(isMod.value)
        Log.i("Deck","${selectedDeck.value!!.id_mazzo}")

    }

    fun eliminaMazzo(deck:Mazzo) {
        val email = loginViewModel.userLoggedInfo.value!!.email
        mazzi.removeIf { it.id_mazzo == deck.id_mazzo }


        deckRepository.deleteDeck(deck.id_mazzo,email)
        Log.i("cardsFiltered", "${cards.value}")

        init()
    }



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
