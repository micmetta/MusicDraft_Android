package com.example.musicdraft.utility

import android.util.Log


/*
- NavigationManager è una classe che serve per gestire le schermate recenti navigate dall'utente.
  È progettata per mantenere una lista delle ultime 5 schermate visitate dall'utente.
  Essa è istanziata direttamente nel composable chiamato 'MusicDraftUI' presente in 'MainActivity.kt'.
*/
class NavigationManager {
    private val recentScreens = mutableListOf<String>()
    fun getRecentScreens(): List<String> {
        return recentScreens
    }
    fun addScreen(screen: String){
        recentScreens.add(screen)
        Log.d("NavigationManager", "Num screen tot: ${getRecentScreens().size}")
        Log.d("NavigationManager", "recentScreens.get(0): ${recentScreens.get(0)}")
    }
    fun removeFirstScreen(){
        recentScreens.removeAt(0)
    }
}
