package com.example.musicdraft.data.tables.exchange_management_cards

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ExchangeManagementCards(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var nicknameU1: String, // utente che ha mandato l'offerta di scambio
    var nicknameU2: String, // utente che ha ricevuto l'offerta di scambio
    var idRequiredCard: String, // campo che conterrà l'id UNIVOCO della carta richiesta da nicknameU1 che è presente nelle carte possedute da nicknameU2.
    var typeRequiredCard: String, // "artist" o "track"
    var listOfferedCards: List<String>, // campo che conterrà la lista delle carte (anche solo una o 0 perchè magari ha offerto solo points)
    var listTypesOfferedCards: List<String>, // campo che conterrà i tipi della lista delle carte (anche solo una o 0 perchè magari ha offerto solo points)
    var pointsOffered: Int, // conterrà il quantitativo di points offerti (può essere anche 0 perchè magari ha offerto solo
    // delle carte) da "nicknameU1" a "nicknameU2" per la "cartaRichiesta"
    //var statusOffer: String, // "pending", "counteroffer"
    //var idStart: Int, // questo id mi permette di sapere sempre a quale offerta iniziale è legata un'eventuale controfferta.
    //var numberCounterOffer: Int // questo numero mi dice qual è il numero corrente della controfferta. Questo sarà == 0 solo all'inizio
    // quando viene fatta la prima offerta. Questo campo posso usarlo per ordinare, quando richiesto, tutte le controfferte che ci sono state a
    // partire da una certa offerta iniziale.
)