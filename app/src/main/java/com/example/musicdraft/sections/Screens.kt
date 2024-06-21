package com.example.musicdraft.sections


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
    data object ForgotPassword: Screens("forgotPassword")
    data object UpdateNickname: Screens("updateEmail")
    data object ExchangeCards: Screens("exchangeCards")
    data object ShowOfferReceived: Screens("showOfferReceived")
    data object ShowOfferSent: Screens("showOfferSent")
}