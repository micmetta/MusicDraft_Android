package com.example.musicdraft.utility

import com.example.musicdraft.viewModel.DeckViewModel

fun compareDeckNames(deckName1:String, deckName2: String):Boolean{

    if(deckName1.replace("\\s".toRegex(), "").equals(deckName2.replace("\\s".toRegex(), ""))){
        return true
    }else{
        return false
    }
}
fun distinctCards(cards:List<DeckViewModel.Cards>):Boolean{
    if(cards.distinctBy{ it.id_carta }.size<5 || cards.distinctBy{ it.id_carta }.size>5){
        return false
    }else{
        return true
    }
}