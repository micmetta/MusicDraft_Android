package com.example.musicdraft

import com.example.musicdraft.utility.compareDeckNames
import com.example.musicdraft.utility.distinctCards
import com.example.musicdraft.viewModel.DeckViewModel
import junit.framework.TestCase.assertEquals
import org.junit.Test

/**
 *Test per progetto MusicDraft
 */
class UtilTest {
    @Test
    fun testDeckNamesLowcaseUppercase() {
        val deckname1 = "deck1"
        val deckname2 = "DECK1"
        val res = compareDeckNames(deckname1,deckname2)
        assertEquals(false,res)
    }
    @Test
    fun testDeckNamesDifferent() {
        val deckname1 = "deCk1"
        val deckname2 = "deck1"
        val res = compareDeckNames(deckname1,deckname2)
        assertEquals(false,res)
    }
    @Test
    fun testDeckNamesEquals() {
        val deckname1 = "deck1"
        val deckname2 = "deck1"
        val res = compareDeckNames(deckname1,deckname2)
        assertEquals(true,res)
    }
    @Test
    fun testDeckNamesDifferent2() {
        val deckname1 = " deck1"
        val deckname2 = "deck1"
        val res = compareDeckNames(deckname1,deckname2)
        assertEquals(true,res)
    }

    @Test
    fun testDeckNamesDifferent3() {
        val deckname1 = " deck 1"
        val deckname2 = "deck  1"
        val res = compareDeckNames(deckname1,deckname2)
        assertEquals(true,res)
    }

    @Test
    fun testDistinctCards() {


        val card1 = DeckViewModel.Cards("0","Jimmy","i.png",10)
        val card2 = DeckViewModel.Cards("1","J","j.png",11)
        val card3 = DeckViewModel.Cards("2","Ji","k.png",12)
        val card4 = DeckViewModel.Cards("3","Jim","l.png",13)
        val card5 = DeckViewModel.Cards("4","Jimm","z.png",14)

        val cards:List<DeckViewModel.Cards> = listOf(card1,card2,card3,card4,card5)

        val res = distinctCards(cards)
        assertEquals(true,res)
    }
    @Test
    fun testDistinctCardsWith1Equal() {


        val card1 = DeckViewModel.Cards("0","Jimmy","i.png",10)
        val card2 = DeckViewModel.Cards("0","Jimmy","i.png",10)
        val card3 = DeckViewModel.Cards("2","Ji","k.png",12)
        val card4 = DeckViewModel.Cards("3","Jim","l.png",13)
        val card5 = DeckViewModel.Cards("4","Jimm","z.png",14)

        val cards:List<DeckViewModel.Cards> = listOf(card1,card2,card3,card4,card5)

        val res = distinctCards(cards)
        assertEquals(false,res)
    }

    @Test
    fun testDistinctCardsWithLessCards() {


        val card1 = DeckViewModel.Cards("0","Jimmy","i.png",10)
        val card2 = DeckViewModel.Cards("1","Jimmy","i.png",10)
        val card3 = DeckViewModel.Cards("2","Ji","k.png",12)
        val card4 = DeckViewModel.Cards("3","Jim","l.png",13)

        val cards:List<DeckViewModel.Cards> = listOf(card1,card2,card3,card4)

        val res = distinctCards(cards)
        assertEquals(false,res)
    }

    @Test
    fun testDistinctCardsWithMoreCards() {


        val card1 = DeckViewModel.Cards("0","Jimmy","i.png",10)
        val card2 = DeckViewModel.Cards("1","Jimmy","i.png",10)
        val card3 = DeckViewModel.Cards("2","Ji","k.png",12)
        val card4 = DeckViewModel.Cards("3","Jim","l.png",13)
        val card5 = DeckViewModel.Cards("4","Jimm","z.png",14)
        val card6 = DeckViewModel.Cards("5","JimmyOh","x.png",15)


        val cards:List<DeckViewModel.Cards> = listOf(card1,card2,card3,card4,card5,card6)

        val res = distinctCards(cards)
        assertEquals(false,res)
    }



}