import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class Deck(val nomemazzo: String, val carteassociate: List<Card>)
data class Card(val nome: String, val popolarita: Int, val immagine: String)

class DeckViewModel : ViewModel() {
    var isEditing = mutableStateOf(false)
    var selectedDeck = mutableStateOf<Deck?>(null)
    var decks = mutableStateOf(listOf(
        Deck("Mazzo 1", listOf(Card("Carta 1", 5, "https://via.placeholder.com/150"))),
        Deck("Mazzo 2", listOf(Card("Carta 2", 10, "https://via.placeholder.com/150")))
    ))
    var availableCards = mutableStateOf(listOf(
        Card("Carta 3", 7, "https://via.placeholder.com/150"),
        Card("Carta 4", 8, "https://via.placeholder.com/150")
    ))
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
    }
}
