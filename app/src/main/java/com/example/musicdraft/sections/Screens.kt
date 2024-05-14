package com.example.musicdraft.sections


// Tutto l'insieme finito delle sottoclassi della classe sealed "Screens" di sotto
// sono quelle presenti al suo interno:
sealed class Screens (val screen: String) {

    // Ogni object rappresenta uno screen specifico all'interno della nostra app e
    // ciascuno di essi è inizializzato con il corrispondente identificatore
    // di tipo stringa passato come parametro a Screens:
    data object Home: Screens("home")
    data object Friends: Screens("friends")
    data object Cards: Screens("cards")
    data object Decks: Screens("decks")
    data object Marketplace: Screens("marketplace")
    data object Matchmaking: Screens("matchmaking")
    data object Settings: Screens("settings")
}