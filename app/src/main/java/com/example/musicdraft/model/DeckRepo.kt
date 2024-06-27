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

class DeckRepo(val viewModel: DeckViewModel,val daoDeck: DaoDeck) {

    val allDecks: MutableStateFlow<List<Deck>?> = MutableStateFlow(emptyList())
    val namesDecks: MutableStateFlow<List<String>>? =  MutableStateFlow(emptyList())
    val carteAssociate: MutableStateFlow<List<String>>? =  MutableStateFlow(emptyList())
    val Pop: MutableStateFlow<Float>? = MutableStateFlow<Float>(0F)


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
    fun insertNewDeck(deck: Deck){
        viewModel.viewModelScope.launch {
            daoDeck.insertdeck(deck)
        }
    }

    fun insertallNewDeck(decks: List<Deck>){
        viewModel.viewModelScope.launch {
            daoDeck.insertAllDecks(decks)
        }
    }




}