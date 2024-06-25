package com.example.musicdraft.model

import DeckViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicdraft.data.tables.deck.DaoDeck
import com.example.musicdraft.data.tables.deck.Deck
import com.example.musicdraft.data.tables.user_cards.User_Cards_Artisti
import com.example.musicdraft.data.tables.user_cards.User_Cards_Track
import com.example.musicdraft.database.MusicDraftDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DeckRepo(val viewModel: DeckViewModel,val daoDeck: DaoDeck) {

    val allDecks: MutableStateFlow<List<Deck>?> = MutableStateFlow(emptyList())
    val namesDecks: MutableStateFlow<List<String>>? =  MutableStateFlow(emptyList())
    val carteAssociate: MutableStateFlow<List<String>>? =  MutableStateFlow(emptyList())

    fun getallDecksByEmail(email:String){
        viewModel.viewModelScope.launch {
            val deck = daoDeck.getAllDeckbyEmail(email)
            deck.collect{it->
                allDecks.value=it
            }
        }
    }

    fun getNomedeck(email:String){
        viewModel.viewModelScope.launch {
            val N = daoDeck.getDistinctDeckNames(email)
            N.collect{it->
                namesDecks?.value=it

            }

        }
    }
    fun getCarteAss(email: String,nomeMazzo:String){
        viewModel.viewModelScope.launch {
            val c = daoDeck.getDecksByNomeMazzoAndEmail(email,nomeMazzo)
            c.collect{it->
                carteAssociate?.value=it

            }
        }
    }





}