package com.example.musicdraft.utility

/*
- Questa sealed class si preoccupa di memorizzare i valori dello stato.
*/
sealed class Resource<T>(val data: T? = null, val message: String? = null){
    class Success<T>(data: T) : Resource<T>(data) // se si verifica un successo allora otterremo un certo dato
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message) // se si verifica un errore allora otterremo un'altra risposta da un dato e da un messaggio
    class Loading<T>(data: T? = null) : Resource<T>(data) // se si verifica un caricamento otterremo un certo dato
}