package com.example.musicdraft.model

import DeckViewModel
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.musicdraft.data.tables.deck.DaoDeck
import com.example.musicdraft.data.tables.deck.Deck
import com.example.musicdraft.data.tables.user.User
import com.example.musicdraft.data.tables.user_cards.User_Cards_Artisti
import com.example.musicdraft.data.tables.user_cards.User_Cards_Track
import com.example.musicdraft.database.MusicDraftDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Repository per la gestione dei mazzi (Deck) dell'applicazione.
 *
 * @property viewModel Il ViewModel associato a questo repository.
 * @property daoDeck Il DAO per l'accesso alla tabella Deck nel database.
 */
class DeckRepo(val viewModel: DeckViewModel,val daoDeck: DaoDeck) {
    /** Flusso che contiene tutti i mazzi dell'utente. */
    val allDecks: MutableStateFlow<List<Deck>?> = MutableStateFlow(emptyList())

    /** Flusso che contiene i nomi distinti dei mazzi dell'utente. */
    val namesDecks: MutableStateFlow<List<String>>? =  MutableStateFlow(emptyList())

    /** Flusso che contiene le carte associate ai mazzi dell'utente. */
    val carteAssociate: MutableStateFlow<List<String>>? =  MutableStateFlow(emptyList())

    /** Flusso che contiene la popolarità di un mazzo specifico. */
    val Pop: MutableStateFlow<Float>? = MutableStateFlow<Float>(0F)

    /** Flusso che indica se una carta è presente in un mazzo dell'utente. */
    val isInDeck:MutableStateFlow<Boolean> = MutableStateFlow<Boolean>(false)

    /**
     * Ottiene tutti i mazzi associati all'email dell'utente e li aggiorna nel flusso [allDecks].
     *
     * @param email L'email dell'utente.
     */
    fun getallDecksByEmail(email:String){
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {

                val deck = daoDeck.getAllDeckbyEmail(email)
                deck.collect { it ->
                    allDecks.value = it
                }
            }
        }
    }

    /**
     * Ottiene i nomi distinti dei mazzi associati all'email dell'utente e li aggiorna nel flusso [namesDecks].
     *
     * @param email L'email dell'utente.
     */
    fun getNomedeck(email:String){
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {

                val N = daoDeck.getDistinctDeckNames(email)
                N.collect { it ->
                    namesDecks?.value = it

                }

            }
        }
    }

    /**
     * Ottiene le carte associate a un mazzo specifico dell'utente e le aggiorna nel flusso [carteAssociate].
     *
     * @param email L'email dell'utente.
     * @param nomeMazzo Il nome del mazzo.
     */
    fun getCarteAss(email: String,nomeMazzo:String){
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {

                    val c = daoDeck.getDecksByNomeMazzoAndEmail(nomeMazzo,email)
                    c.collect { it ->
                        carteAssociate?.value = it


                }
            }
        }
    }

    /**
     * Ottiene la popolarità di un mazzo specifico dell'utente e la aggiorna nel flusso [Pop].
     *
     * @param email L'email dell'utente.
     * @param nomeMazzo Il nome del mazzo.
     */
    fun getDeckPop(email: String,nomeMazzo:String){
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {

                val c = daoDeck.getDecksPop(nomeMazzo,email)
                c.collect { it ->
                    Pop?.value = it


                }
            }
        }
    }
    /**
     * Inserisce un nuovo mazzo nel database.
     *
     * @param deck Il mazzo da inserire.
     */
    fun insertNewDeck(deck: Deck) {
        viewModel.viewModelScope.launch {
            daoDeck.insertdeck(deck)
        }
    }

    /**
     * Inserisce una lista di nuovi mazzi nel database.
     *
     * @param decks La lista dei mazzi da inserire.
     */
    fun insertAllNewDeck(decks: List<Deck>) {
        viewModel.viewModelScope.launch {
            daoDeck.insertAllDecks(decks)
        }
    }

    /**
     * Verifica se una carta specifica è presente in un mazzo dell'utente e aggiorna il flusso [isInDeck].
     *
     * @param userEmail L'email dell'utente.
     * @param card La carta da verificare.
     */
    fun isCardInDeck(userEmail: String, card: String) {
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val b = daoDeck.isCardInDeck(userEmail, card)
                b.collect { it ->
                    isInDeck.value = it
                }
            }
        }
    }
    fun deleteDeck(nomemazzo: String, email: String){
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO){
                daoDeck.deleteMazziByNome(nomemazzo, email)

            }
        }
    }
}