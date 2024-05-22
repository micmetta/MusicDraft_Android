package com.example.musicdraft.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.musicdraft.data.RegistrationUIState
import com.example.musicdraft.data.UIEvent
import com.example.musicdraft.data.rules.ValidatorFields
import com.example.musicdraft.sections.Screens


// - Nel momento in cui un 'UIEvent' verrà innescato, questo sarà catturato dal "LoginViewModel" che si
//   preoccuperà di gestirlo andando a modificare lo stato dell'interfaccia chiamato 'registrationUIState'.
class LoginViewModel() : ViewModel() {

    // - La variabile qui sotto sarà uno STATE di tipo "RegistrationUIState()" in modo tale che
    //   il viewModel possa aggiornarsi ogni volta che questo stato cambierà (ovvero ogni volta che
    //   la schermata di creazione dell'account verrà modificata durante l'inserimento dei dati da parte dell'utente):
    var registrationUIState = mutableStateOf(RegistrationUIState())

    var allValidationCompleted = mutableStateOf(false) // dirà se tutti i campi sono stati validati o meno

    private val TAG = LoginViewModel::class.simpleName

    // - La funzione qui sotto verrà invocata ogni volta che l'utente
    //   farà scattare un qualche evento sulla schermata di Creazione account ("SignUpScreen.kt")
    //   per questo motivo prende in input un evento di tipo "UIEvent".
    fun onEvent(event:UIEvent, navController: NavController){

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
                signUp(navController)
            }

            // una volta che viene generato l'evento qui sotto
            // verrà aggiornato il valore di 'privacyPolicyAccepted':
            is UIEvent.PrivacyPolicyCheckBoxClicked -> {
                registrationUIState.value = registrationUIState.value.copy(
                    privacyPolicyAccepted = event.status
                )
            }

        }

        // Ogni volta che uno qualsiasi degli eventi sopra è stato gestito,
        // verrà eseguita subito la funzione 'validateData()' in modo tale che
        // ogni volta che l'utente inserisce/cancella un qualche carattere in una qualsiasi delle caselle di testo
        // (nickname, email e password), questa casella aggiornerà il proprio colore immediatamente in modo tale
        // da fargli capire se i dati che sta inserendo sono accettabili o meno dal sistema.
        validateData()

    }

    // - Questa è la funzione che verrà eseguita una volta che l'utente avrà premuto sul button "Register".
    private fun signUp(navController: NavController) {
        Log.d(TAG, "Hai cliccato sul button 'Register'!")
        printState()

        // - Con la funzione di sotto eseguo la validazione dei dati inseriti usando il 'ValidatorFields'  presente nel package 'rules':
        validateData()

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // controlli da fare nel DB per vedere se i dati inseriti dall'utente sono corretti rispetto a quelli presenti nel DB in questo momento
        // ...... TO DO...
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // Qui passo alla schermata: navigationController.navigate(Screens.home.screen)
        if (allValidationCompleted.value) {
            navController.navigate(Screens.MusicDraftUI.screen)
        }
    }

    // - Funzione di validazione dei dati inseriti dall'utente e aggiornamento dello stato dell'interfaccia.
    private fun validateData() {

        val nicknameResult = ValidatorFields.validateNickname(nickname = registrationUIState.value.nickName)
        val emailResult = ValidatorFields.validateEmail(email = registrationUIState.value.email)
        val passwordResult = ValidatorFields.validatePassword(password = registrationUIState.value.password)
        val privacyPolicyResult = ValidatorFields.validatePrivacyAndPolicy(statusValue = registrationUIState.value.privacyPolicyAccepted)

        Log.d(TAG, "Sono dentro validateData() e i risultati delle validazioni sono i seguenti:")
        Log.d(TAG, "nicknameResult= $nicknameResult")
        Log.d(TAG, "emailResult= $emailResult")
        Log.d(TAG, "passwordResult= $passwordResult")
        Log.d(TAG, "privacyPolicyResult= $privacyPolicyResult")

        // aggiornamenti dello stato 'registrationUIState' in base ai risultati di validazione per ogni campo:
        registrationUIState.value = registrationUIState.value.copy(
            nicknameError = nicknameResult.status,
            emailError = emailResult.status,
            passwordError = passwordResult.status,
            privacyPolicyError = privacyPolicyResult.status
        )

        // se tutti i campi sono stati validati allora lo stato 'allValidationCompleted' sarà true e di conseguenza
        // il button di registrazione si attiverà:
        if(nicknameResult.status && emailResult.status && passwordResult.status && privacyPolicyResult.status){
            allValidationCompleted.value = true
        }else{
            allValidationCompleted.value = false
        }

    }

    // mi serve solo per verificare che il registrationUIState venga aggiornato
    // correttamente ogni volta che si verifica un 'UIEvent' usando il Logcat:
    private fun printState(){
        Log.d(TAG, "Sono dentro printState()")
        Log.d(TAG, registrationUIState.value.toString())
    }
}