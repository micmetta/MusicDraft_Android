package com.example.musicdraft.sections

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf


// Tutto l'insieme finito delle sottoclassi della classe sealed "Screens" di sotto
// sono quelle presenti al suo interno.
// - Qui sotto ci sono tutte le route dell'app.
sealed class Screens (val screen: String) {

    // - Ogni object rappresenta uno screen (schermata) specifico all'interno della nostra app e
    //   ciascuno di essi è inizializzato con il corrispondente identificatore
    //   di tipo stringa passato come parametro a Screens:
    data object SignUp: Screens("signUp")
    data object Login: Screens("login")
    data object TermsAndConditionsScreen: Screens("termsAndConditionsScreen")
    data object MusicDraftUI: Screens("musicDraftUI")
    data object Home: Screens("home")
    data object Friends: Screens("friends")
    data object Cards: Screens("cards")
    data object Decks: Screens("decks")
    data object Marketplace: Screens("marketplace")
    data object Matchmaking: Screens("matchmaking")
    data object Settings: Screens("settings")
    data object ErrorDialog: Screens("errorDialog")

}