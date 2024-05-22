package com.example.musicdraft.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.musicdraft.data.RegistrationUIState
import com.example.musicdraft.data.UIEvent
import com.example.musicdraft.data.rules.ValidatorFields


// - Nel momento in cui un 'UIEvent' verrà innescato, questo sarà catturato dal "LoginViewModel" che si
//   preoccuperà di gestirlo andando a modificare lo stato dell'interfaccia chiamato 'registrationUIState'.
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

        // Ogni volta che viene generato uno qualsiasi degli eventi qui sotto
        // verrà eseguita subito la funzione 'validateData()' in modo tale che
        // ogni volta che l'utente inserisce/cancella un qualche carattere in una qualsiasi delle caselle di testo
        // (nickname, email e password), questa casella aggiornerà il probprio colore immediatamente in modo tale
        // da fargli capire se i dati che sta inserendo sono accettabili o meno dal sistema.
        validateData()


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
        Log.d(TAG, "Hai cliccato sul button 'Register'!")
        printState()

        // - Con la funzione di sotto eseguo la validazione dei dati inseriti usando il 'ValidatorFields'  presente nel package 'rules':
        validateData()
    }

    // - Funzione di validazione dei dati inseriti dall'utente e aggiornamento dello stato dell'interfaccia.
    private fun validateData() {
        val nicknameResult = ValidatorFields.validateNickname(nickname = registrationUIState.value.nickName)
        val emailResult = ValidatorFields.validateEmail(email = registrationUIState.value.email)
        val passwordResult = ValidatorFields.validatePassword(password = registrationUIState.value.password)

        Log.d(TAG, "Sono dentro validateData() e i risultati delle validazioni sono i seguenti:")
        Log.d(TAG, "nicknameResult= $nicknameResult")
        Log.d(TAG, "emailResult= $emailResult")
        Log.d(TAG, "passwordResult= $passwordResult")

        // aggiornamenti dello stato 'registrationUIState' in base ai risultati di validazione per ogni campo:
        registrationUIState.value = registrationUIState.value.copy(
            nicknameError = nicknameResult.status,
            emailError = emailResult.status,
            passwordError = passwordResult.status
        )

    }

    // mi serve solo per verificare che il registrationUIState venga aggiornato
    // correttamente ogni volta che si verifica un 'UIEvent' usando il Logcat:
    private fun printState(){
        Log.d(TAG, "Sono dentro printState()")
        Log.d(TAG, registrationUIState.value.toString())
    }
}