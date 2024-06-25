import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.musicdraft.data.tables.deck.Cards
import com.example.musicdraft.data.tables.deck.Deck
import com.example.musicdraft.data.tables.user_cards.User_Cards_Artisti
import com.example.musicdraft.data.tables.user_cards.User_Cards_Track
import com.example.musicdraft.database.MusicDraftDatabase
import com.example.musicdraft.model.DeckRepo
import com.example.musicdraft.viewModel.CardsViewModel
import com.example.musicdraft.viewModel.LoginViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class DeckViewModel(application: Application,private val loginViewModel: LoginViewModel,private val cardsViewModel: CardsViewModel): AndroidViewModel(application) {
    private val database = MusicDraftDatabase.getDatabase(application)
    private val daoOwnCards = database.ownArtCardsDao()
    private val daoDeck = database.deckDao()
    private val loginDao = database.userDao()

    private val deckRepository: DeckRepo = DeckRepo(this,daoDeck!!)

    var isEditing = mutableStateOf(false)

    private val _selectedDeck : Mazzo? =null
    val selectedDeck: MutableStateFlow<Mazzo?> = MutableStateFlow(_selectedDeck)

    private val _ownDeck : List<Deck>? = null
    val ownDeck: MutableStateFlow<List<Deck>?> = MutableStateFlow(_ownDeck)

    private val _ownCardsA: List<User_Cards_Artisti>? = null
    val ownCardA: MutableStateFlow<List<User_Cards_Artisti>?> = MutableStateFlow(_ownCardsA)

    private val _ownCardsT: List<User_Cards_Track>? = null
    val ownCardT: MutableStateFlow<List<User_Cards_Track>?> = MutableStateFlow(_ownCardsT)

    private val _cards:List<Cards>?=null
    val cards: MutableStateFlow<List<Cards>?> = MutableStateFlow(_cards)

    private val _selectedCards = MutableStateFlow<List<Cards>>(emptyList())
    val selectedCards: StateFlow<List<Cards>> = _selectedCards

    var mazzi : MutableList<Mazzo>? = null
    val cardList: MutableList<Cards>? = null

    // Funzione per selezionare/deselezionare una carta


    inner class Mazzo(
        val id_mazzo:String,
        val carte: List<Cards>
    )

    fun init(){
            ownCardA.value = cardsViewModel.acquiredCardsA.value
            ownCardT.value = cardsViewModel.acquiredCardsT.value

            val listofCards:MutableList<Cards>?=null
            ownCardA.value?.forEach{elem->
                listofCards?.add(Cards(elem.id_carta,elem.nome,elem.immagine,elem.popolarita))
            }
            ownCardT.value?.forEach { elem->
                listofCards?.add(Cards(elem.id_carta,elem.nome,elem.immagine,elem.popolarita))

            }

            cards.value = listofCards

            val email = loginViewModel.userLoggedInfo.value?.email
            deckRepository.getallDecksByEmail(email!!)
            ownDeck.value = deckRepository.allDecks.value
            deckRepository.getNomedeck(email)
            val n = deckRepository.namesDecks?.value
            if (n != null) {
                for(i in n){
                    deckRepository.getCarteAss(email,i)
                    val c = deckRepository.carteAssociate?.value
                    if (c != null) {
                        for(j in c){
                            cards.value?.forEach { card->
                                if (card.id_carta == j) {
                                    cardList?.add(card)
                                }
                            }

                        }
                        cardList?.let { Mazzo(i, it) }?.let { mazzi?.add(it) }
                    }
                }
            }









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
        TODO("Not yet implemented")
    }

    fun annullaModifica() {
        TODO("Not yet implemented")
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
