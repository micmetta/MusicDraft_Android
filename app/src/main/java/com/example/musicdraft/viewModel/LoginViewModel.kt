package com.example.musicdraft.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.musicdraft.data.RegistrationUIState
import com.example.musicdraft.data.UIEvent

class LoginViewModel : ViewModel() {

    // - La variabile qui sotto sarà uno STATE di tipo "RegistrationUIState()" in modo tale che
    //   il viewModel possa aggiornarsi ogni volta che questo stato cambierà (ovvero ogni volta che
    //   la schermata di creazione dell'account verrà modificata durante l'inserimento dei dati da parte dell'utente):
    var registrationUIState = mutableStateOf(RegistrationUIState())

    private val TAG = LoginViewModel::class.simpleName

    // - La funzione qui sotto verrà invocata ogni volta che l'utente
    //   farà scattare un qualche evento sulla schermata di Creazione account ("SignUpScreen.kt")
    //   per questo motivo prende in input un evento di tipo "UIEvent".
    fun onEvent(event:UIEvent){
        // gestione dei diversi eventi possibili:
        when(event){
            // Aggiorno il "registrationUIState" prendendo il 'event.nickname' inserito dall'utente:
            is UIEvent.NicknameChanged -> {
                // - Qui c'è quello che verrà eseguito nel momento in cui l'utente inserirà i caratteri
                //   nel campo "nickname".
                // - Grazie al .copy viene eseguita una copia dell'oggetto "registrationUIState"
                //   e le proprietà non modificate di tale oggetto
                //   (ovvero in questo caso email e password) rimangono immutate mentre in questo caso solo il
                //   valore del campo nickName all'interno della nuova istanza sarà aggiornato.
                // IN QUESTO MODO RIESCO AD AGGIORNARE lo stato 'registrationUIState'
                // (per essere più precisi aggiorno il campo 'nickname' DELLO STATO 'registrationUIState'):
                registrationUIState.value = registrationUIState.value.copy(
                    nickName = event.nickname
                )
                printState() // stampo per verificare che il registrationUIState sia stato aggiornato
            }

            is UIEvent.EmailChanged -> {
                registrationUIState.value = registrationUIState.value.copy(
                    email = event.email
                )
                printState() // stampo per verificare che il registrationUIState sia stato aggiornato
            }

            is UIEvent.PasswordChanged -> {
                registrationUIState.value = registrationUIState.value.copy(
                    password = event.password
                )
                printState() // stampo per verificare che il registrationUIState sia stato aggiornato
            }

            // evento generato nel momento in cui l'utente clicca su "Register"
            // durante la creazione dell'account:
            is UIEvent.RegisterButtonClick -> {
                signUp()
            }

        }
    }

    private fun signUp() {
        Log.d(TAG, "Registrazione eseguita con successo!")
        printState()
    }

    // mi serve solo per verificare che il registrationUIState venga aggiornato
    // correttamente ogni volta che si verifica un 'UIEvent' usando il Logcat:
    private fun printState(){
        Log.d(TAG, "Sono dentro printState")
        Log.d(TAG, registrationUIState.value.toString())
    }
}