package com.example.musicdraft.viewModel

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
import com.example.musicdraft.utility.compareDeckNames
import com.example.musicdraft.utility.distinctCards
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel per la gestione dei mazzi di carte.
 *
 * @param application Oggetto Application per l'accesso al contesto dell'applicazione.
 * @param loginViewModel ViewModel per la gestione del login dell'utente.
 * @param cardsViewModel ViewModel per la gestione delle carte.
 */
class DeckViewModel(
    application: Application,
    private val loginViewModel: LoginViewModel,
    private val cardsViewModel: CardsViewModel,
    private val exchangeManagementCardsViewModel: ExchangeManagementCardsViewModel
) : AndroidViewModel(application) {

    // Inizializzazione del database e dei DAO necessari
    private val database = MusicDraftDatabase.getDatabase(application)
    private val daoOwnCards = database.ownArtCardsDao()
    private val daoDeck = database.deckDao()
    private val daoLog = database.userDao()
    private val daoTrack = database.ownTrackCardsDao()

    // Repository per l'accesso ai dati dei mazzi e delle carte degli artisti
    private val deckRepository: DeckRepo = DeckRepo(this, daoDeck!!)
    private val ownArtistRepo: UserCardsRepo = UserCardsRepo(daoOwnCards!!, daoLog!!, daoTrack!!, cardsViewModel)

    // Stati mutable utilizzati nel ViewModel
    var isEditing = mutableStateOf(false)
    var isMod = mutableStateOf(false)

    // Stato del mazzo selezionato
    private val _selectedDeck: Mazzo? = null
    val selectedDeck: MutableStateFlow<Mazzo?> = MutableStateFlow(_selectedDeck)

    // Lista dei mazzi di carte dell'utente
    private val _ownDeck: List<Deck>? = null
    val ownDeck: MutableStateFlow<List<Deck>?> = MutableStateFlow(_ownDeck)

    // Stato delle carte degli artisti possedute dall'utente
    private val _ownCardsA = MutableStateFlow<List<User_Cards_Artisti>?>(null)
    val ownCardA: StateFlow<List<User_Cards_Artisti>?> get() = _ownCardsA

    // Stato delle tracce musicali possedute dall'utente
    private val _ownCardsT = MutableStateFlow<List<User_Cards_Track>?>(null)
    val ownCardT: StateFlow<List<User_Cards_Track>?> get() = _ownCardsT

    // Stato delle carte disponibili per la creazione del mazzo
    private val _cards: List<Cards>? = null
    val cards: MutableStateFlow<List<Cards>?> = MutableStateFlow(_cards)

    // Stato booleano per indicare se si sta creando un nuovo mazzo
    private val _isCreatingNewDeck = MutableLiveData(false)
    val isCreatingNewDeck: LiveData<Boolean> get() = _isCreatingNewDeck

    // Stato delle carte selezionate per il mazzo in modifica
    private val _selectedCards = MutableStateFlow<List<Cards>>(emptyList())
    val selectedCards: StateFlow<List<Cards>> get() = _selectedCards

    // Stato del messaggio da visualizzare all'utente
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> get() = _message

    // Stato del nome del mazzo in modifica
    private val _deckName = MutableStateFlow("")
    val deckName: StateFlow<String> get() = _deckName

    val cardList: MutableList<Cards> = emptyList<Cards>().toMutableList()

    //////////////////////////////////////////////////////////////////////////////////////////
    // per prendere la pop di un mazzo:
    var pop_deck = deckRepository.Pop
    //////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////
    var deckByid = deckRepository.deckById
    //////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////////////
    var associateCards = deckRepository.cardAssociate
    /////////////////////////////////////////////////////////////////////////////////////////

    // Lista dei mazzi di carte dell'utente
    var mazzi: MutableLiveData<MutableList<Mazzo>> = MutableLiveData(mutableListOf())


    /**
     * Classe interna per rappresentare una carta.
     *
     * @property id_carta ID della carta.
     * @property nome_carta Nome della carta.
     * @property immagine URL dell'immagine della carta.
     * @property popolarita Livello di popolarità della carta.
     */
    class Cards(
        val id_carta: String,
        val nome_carta: String,
        val immagine: String,
        val popolarita: Int
    )

    /**
     * Classe interna per rappresentare un mazzo di carte.
     *
     * @property id_mazzo ID del mazzo.
     * @property carte Lista di carte associate al mazzo.
     */
    inner class Mazzo(
        var id_mazzo: String,
        val carte: List<Cards>
    )

    /**
     * Metodo per inizializzare il ViewModel.
     */
    fun init() {
        // Pulizia delle liste e recupero delle carte possedute dall'utente
        val cardList: MutableList<Cards> = mutableListOf()
        mazzi.value!!.clear()
        val email = loginViewModel.userLoggedInfo.value!!.email

        // Recupero delle carte degli artisti dell'utente
        ownArtistRepo.getArtCardsforUser(email)
        viewModelScope.launch {
            ownArtistRepo.allCardsforUserA.collect { allArtisti ->
                val ownCardsA = allArtisti?.filter { !it.onMarket }
                _ownCardsA.value = ownCardsA
            }
        }

        // Recupero delle tracce musicali possedute dall'utente
        ownArtistRepo.getTrackCardsforUser(email)
        viewModelScope.launch {
            ownArtistRepo.allCardsforUserT.collect { allTracks ->
                val ownCardsT = allTracks?.filter { !it.onMarket }
                _ownCardsT.value = ownCardsT
            }
        }

        // Creazione della lista di tutte le carte disponibili per la creazione del mazzo
        val listofCards: MutableList<Cards> = mutableListOf()
        ownCardA.value?.forEach { elem ->
            val c = Cards(elem.id_carta, elem.nome, elem.immagine, elem.popolarita)
            listofCards.add(c)
        }
        ownCardT.value?.forEach { elem ->
            val c = Cards(elem.id_carta, elem.nome, elem.immagine, elem.popolarita)
            listofCards.add(c)
        }

        // Aggiornamento dello stato delle carte disponibili
        cards.value = listofCards

        // Recupero dei mazzi di carte dell'utente e aggiunta delle carte associate ai mazzi
        deckRepository.getallDecksByEmail(email)
        ownDeck.value = deckRepository.allDecks.value
        deckRepository.getNomedeck(email)
        val names = deckRepository.namesDecks?.value

        var c = 0
        for (i in ownDeck.value!!) {
            cards.value!!.forEach { elem ->
                if (elem.id_carta == i.carte_associate) {
                    cardList.add(elem)
                    c = c + 1
                }
            }
            if (c == 5) {
                // Creazione di una copia profonda di cardList
                val deepCopyCardList = cardList.map { Cards(it.id_carta, it.nome_carta, it.immagine, it.popolarita) }
                    .toMutableList()
                mazzi.value!!.add(Mazzo(i.nome_mazzo, deepCopyCardList))
                cardList.clear()
                c = 0
            }
        }
    }


    /**
     * Metodo per gestire il toggle della selezione di una carta.
     *
     * @param card Carta da selezionare/deselezionare.
     */
    fun toggleCardSelection(
        card: Cards,
        reqSentCurrentUser: List<ExchangeManagementCards>?,
        reqReceivedCurrentUser: List<ExchangeManagementCards>?
    ) {
        val currentSelectedCards = _selectedCards.value?.toMutableList() ?: mutableListOf()

        // Verifica se la carta è presente in un altro mazzo
        val cardId = card.id_carta
//        val isCardInAnotherDeck = mazzi.any { deck ->
//            deck.id_mazzo != selectedDeck.value!!.id_mazzo && deck.carte.any { it.id_carta == cardId }
//        }

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
            _message.value = "This card is owned by another deck"
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

    /**
     * Metodo per creare un nuovo mazzo di carte.
     */
    fun creaNuovoMazzo() {
        selectedDeck.value = Mazzo("", emptyList())
        isEditing.value = true
        isMod.value = false
        _selectedCards.value = emptyList()
        _isCreatingNewDeck.value = true
    }

    /**
     * Metodo per annullare la modifica del mazzo in corso.
     */
    fun cancelEditing() {
        isEditing.value = false
        selectedDeck.value = null
    }

    /**
     * Metodo per iniziare la modifica di un mazzo esistente.
     *
     * @param deck Mazzo da modificare.
     */
    fun startEditingDeck(deck: Mazzo) {
        selectedDeck.value = deck
        isEditing.value = true
        _isCreatingNewDeck.value = false
        updateAvailableAndSelectedCards()
    }

    /**
     * Metodo privato per aggiornare le carte disponibili e selezionate per il mazzo in modifica.
     */
    private fun updateAvailableAndSelectedCards() {
        val allCards = cards.value ?: emptyList()
        val deckCards = selectedDeck.value?.carte ?: emptyList()

        _selectedCards.value = deckCards
        cards.value = allCards.filter { card -> !deckCards.contains(card) }
    }

    /**
     * Metodo per salvare il mazzo di carte in creazione o modifica.
     */
    fun salvaMazzo() {
        val email = loginViewModel.userLoggedInfo.value!!.email
        val names = deckRepository.namesDecks?.value
        val currentDeck = selectedDeck.value
        val currentCards = selectedCards.value

        if (currentDeck != null) {
            // Verifica se il nome del mazzo è unico
            mazzi.value!!.forEach { elem ->
                /*
                 if (names?.contains(deckName.value) == true) {
                    _message.value = "Il nome del mazzo esiste già!"
                    return
                }
                 */
                for(n in names!!){
                    if(compareDeckNames(n,deckName.value)){
                        _message.value = "Deck name already exist"
                        return
                    }
                }

            }

            // Verifica che siano selezionate esattamente 5 carte uniche
            /*if (currentCards.distinctBy { it.id_carta }.size != 5) {
                _message.value = "È necessario selezionare esattamente 5 carte uniche!"
                return
            }*/
            if(!(distinctCards(currentCards))){
                _message.value = "You had to choose 5 distinct cards"
                return
            }

            // Calcolo della popolarità media delle carte selezionate
            var pop: Float = 0F
            for (i in selectedCards.value) {
                pop += i.popolarita
            }
            val temp: MutableList<Deck> = mutableListOf()
            for (i in selectedCards.value) {
                val m = Deck(0, _deckName.value, i.id_carta, email, pop / 5)
                temp.add(m)
            }
            deckRepository.insertAllNewDeck(temp)

            mazzi.value!!.add(Mazzo(deckName.value, _selectedCards.value))

            // Reset degli stati
            annullaModifica()
            _selectedCards.value = emptyList()
            _message.value = "Deck Saved"
        }
    }

    /**
     * Metodo per annullare la modifica o la creazione del mazzo.
     */
    fun annullaModifica() {
        isEditing.value = false
        selectedDeck.value = null
        _deckName.value = ""
    }

    /**
     * Metodo per aggiornare il nome del mazzo in modifica.
     *
     * @param newName Nuovo nome del mazzo.
     */
    fun updateDeckName(newName: String) {
        _deckName.value = newName
    }

    /**
     * Metodo per aggiornare lo stato di modifica del mazzo.
     *
     * @param deck Mazzo da modificare.
     */
    fun updateIsEditing(deck: Mazzo) {
        selectedDeck.value = deck
        isMod.value = !isMod.value
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



    /**
     * Metodo per eliminare un mazzo di carte.
     *
     * @param deck Mazzo da eliminare.
     */
    fun eliminaMazzo(deck: Mazzo) {
        val email = loginViewModel.userLoggedInfo.value!!.email

        // Crea una nuova lista a partire da quella esistente e rimuovi il mazzo
        val updatedList = mazzi.value?.toMutableList()?.apply {
            removeIf { it.id_mazzo == deck.id_mazzo }
        }

        // Aggiorna il valore del MutableLiveData
        mazzi.value = updatedList

        // Esegui l'operazione di eliminazione nel repository
        deckRepository.deleteDeck(deck.id_mazzo, email)

        // Inizializza nuovamente i dati se necessario
        init()
        _message.value = "Deck cancellato!"
        return
    }


    //////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Ottiene la popolarità di un mazzo specifico dell'utente e la aggiorna nel flusso [Pop].
     *
     * @param email L'email dell'utente.
     * @param nomeMazzo Il nome del mazzo.
     */
    fun getDeckPop(email: String, nomeMazzo:String){
        deckRepository.getDeckPop(email, nomeMazzo)
    }
    //////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Richiama il metodo getCarteAss di DeckRepo
     * per ottenere le carte associate a un mazzo specifico dell'utente e le aggiorna nel flusso [carteAssociate].
     *
     * @param email L'email dell'utente.
     * @param nomeMazzo Il nome del mazzo.
     */
    fun getCarteAss(email: String, nomeMazzo:String){
        deckRepository.getCarteAss(email, nomeMazzo)
    }
    //////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Aggiorna un mazzo dell'utente.
     *
     */
    fun modificaMazzo() {
        val email = loginViewModel.userLoggedInfo.value!!.email
        val SC =_selectedCards.value
        Log.i("nome_vecchio","${selectedDeck.value!!.id_mazzo}")
        mazzi.value!!.removeIf { it.id_mazzo == selectedDeck.value!!.id_mazzo }
        deckRepository.deleteDeck(selectedDeck.value!!.id_mazzo,email)
        salvaMazzo()
    }


    /**
     * Metodo per pulire il messaggio visualizzato all'utente.
     */
    fun clearMessage() {
        _message.value = null
    }




}
