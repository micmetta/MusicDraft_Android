package com.example.musicdraft.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.musicdraft.data.RegistrationUIState
import com.example.musicdraft.data.LoginUIState
import com.example.musicdraft.data.UIEventSignIn
import com.example.musicdraft.data.UIEventSignUp
import com.example.musicdraft.data.rules.ValidatorFields
import com.example.musicdraft.sections.Screens


// - Nel momento in cui un 'UIEvent' verrà innescato, questo sarà catturato dal "LoginViewModel" che si
//   preoccuperà di gestirlo andando a modificare lo stato dell'interfaccia chiamato 'registrationUIState'.
class LoginViewModel() : ViewModel() {

    // - La variabile qui sotto sarà uno STATE di tipo "RegistrationUIState()" in modo tale che
    //   il viewModel possa aggiornarsi ogni volta che questo stato cambierà (ovvero ogni volta che
    //   la schermata di creazione dell'account verrà modificata durante l'inserimento dei dati da parte dell'utente):
    var registrationUIState = mutableStateOf(RegistrationUIState())
    var loginUIState = mutableStateOf(LoginUIState()) // stessa cosa di "RegistrationUIState()" ma per i componenti della schermata di login

    var allValidationCompleted = mutableStateOf(false) // dirà se tutti i campi sono stati validati o meno (nella schermata di Registrazione)
    var allValidationCompletedLogin = mutableStateOf(false) // dirà se tutti i campi sono stati validati o meno (nella schermata di Login)

    var signUpInProgress = mutableStateOf(false) // stato che permetterà di attivare/disattivare l'indicatore di caricamento
    // una volta che l'utente avrà cliccato su "Register" durante la creazione dell'account.

    var signInInProgress = mutableStateOf(false) // stessa cosa di 'signUpInProgress' ma dopo aver cliccato su 'Login'

    private val TAG = LoginViewModel::class.simpleName

    // - La funzione qui sotto verrà invocata ogni volta che l'utente
    //   farà scattare un qualche evento sulla schermata di Creazione account ("SignUpScreen.kt")
    //   per questo motivo prende in input un evento di tipo "UIEvent".
    fun onEvent(event:UIEventSignUp, navController: NavController){

        // gestione dei diversi eventi possibili:
        when(event){
            // Aggiorno il "registrationUIState" prendendo il 'event.nickname' inserito dall'utente:
            is UIEventSignUp.NicknameChanged -> {
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
                printStateSignUp() // stampo per verificare che il registrationUIState sia stato aggiornato
            }

            is UIEventSignUp.EmailChanged -> {
                registrationUIState.value = registrationUIState.value.copy(
                    email = event.email
                )
                printStateSignUp() // stampo per verificare che il registrationUIState sia stato aggiornato
            }

            is UIEventSignUp.PasswordChanged -> {
                registrationUIState.value = registrationUIState.value.copy(
                    password = event.password
                )
                printStateSignUp() // stampo per verificare che il registrationUIState sia stato aggiornato
            }

            // evento generato nel momento in cui l'utente clicca su "Register" dalla schermata di registrazione
            // durante la creazione dell'account:
            is UIEventSignUp.RegisterButtonClick -> {
                signUp(navController)
            }

            // una volta che viene generato l'evento qui sotto
            // verrà aggiornato il valore di 'privacyPolicyAccepted':
            is UIEventSignUp.PrivacyPolicyCheckBoxClicked -> {
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
        validateData() // da inserire in un thread
    }

    // - La funzione qui sotto verrà invocata ogni volta che l'utente
    //   farà scattare un qualche evento sulla schermata di Login ("SignInScreen.kt")
    //   per questo motivo prende in input un evento di tipo "UIEvent".
    fun onEvent(event: UIEventSignIn, navController: NavController){

        // gestione dei diversi eventi possibili:
        when(event){

            is UIEventSignIn.EmailChanged -> {
                loginUIState.value = loginUIState.value.copy(
                    email = event.email
                )
                printStateSignIn() // stampo per verificare che il registrationUIState sia stato aggiornato
            }

            is UIEventSignIn.PasswordChanged -> {
                loginUIState.value = loginUIState.value.copy(
                    password = event.password
                )
                printStateSignIn() // stampo per verificare che il registrationUIState sia stato aggiornato
            }

            // evento generato nel momento in cui l'utente clicca su "Login" dalla schermata di login
            // durante la creazione dell'account:
            is UIEventSignIn.LoginButtonClick -> {
                signIn(navController)
            }
        }

        // Ogni volta che uno qualsiasi degli eventi sopra è stato gestito,
        // verrà eseguita subito la funzione 'validateDataLogin()' in modo tale che
        // ogni volta che l'utente inserisce/cancella un qualche carattere in una qualsiasi delle caselle di testo
        // (email e password), questa casella aggiornerà il proprio colore immediatamente in modo tale
        // da fargli capire se i dati che sta inserendo sono accettabili o meno dal sistema.
        validateDataLogin() // da inserire in un thread
    }


    // - Questa è la funzione che verrà eseguita una volta che l'utente avrà premuto sul button "Register".
    private fun signUp(navController: NavController) {

        // attivo l'indicatore di caricamento:
        signUpInProgress.value = true

        Log.d(TAG, "Hai cliccato sul button 'Register'!")
        printStateSignUp()

        // - Con la funzione di sotto eseguo la validazione dei dati inseriti usando il 'ValidatorFields'  presente nel package 'rules':
        validateData()


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // controlli da fare nel DB per vedere se i dati inseriti dall'utente sono corretti rispetto a quelli presenti nel DB in questo momento
        // ...... TO DO...
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        // disattivo l'indicatore di caricamento:
        signUpInProgress.value = false

        // Qui passo alla schermata: navigationController.navigate(Screens.home.screen)
        if (allValidationCompleted.value) {

            // resetto tutti i campi di "registrationUIState":
            registrationUIState.value = registrationUIState.value.copy(
                nickName = "",
                email = "",
                password = "",
                privacyPolicyAccepted = false
            )
            navController.navigate(Screens.MusicDraftUI.screen)
        }

    }

    // - Questa è la funzione che verrà eseguita una volta che l'utente avrà premuto sul button "Login".
    private fun signIn(navController: NavController) {

        // attivo l'indicatore di caricamento:
        signInInProgress.value = true

        Log.d(TAG, "Hai cliccato sul button 'Login'!")
        printStateSignIn()

        // - Con la funzione di sotto eseguo la validazione dei dati inseriti usando il 'ValidatorFields'  presente nel package 'rules':
        validateDataLogin()


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // controlli da fare nel DB per vedere se i dati inseriti dall'utente sono corretti rispetto a quelli presenti nel DB in questo momento
        // ...... TO DO...
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        // disattivo l'indicatore di caricamento:
        signInInProgress.value = false

        // Qui passo alla schermata: navigationController.navigate(Screens.home.screen)
        if (allValidationCompletedLogin.value) {

            // resetto tutti i campi di "loginUIState":
            loginUIState.value = loginUIState.value.copy(
                email = "",
                password = "",
            )

            navController.navigate(Screens.MusicDraftUI.screen)
        }

    }


    // - Funzioni di validazione dei dati inseriti dall'utente e aggiornamento dello stato dell'interfaccia
    //   per la schermata di registrazione.
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

    // - Funzioni di validazione dei dati inseriti dall'utente e aggiornamento dello stato dell'interfaccia
    //   per la schermata di login.
    private fun validateDataLogin() {
        val emailResult = ValidatorFields.validateEmail(email = loginUIState.value.email)
        val passwordResult = ValidatorFields.validatePassword(password = loginUIState.value.password)

        Log.d(TAG, "Sono dentro validateData() e i risultati delle validazioni sono i seguenti:")
        Log.d(TAG, "emailResult= $emailResult")
        Log.d(TAG, "passwordResult= $passwordResult")

        // aggiornamenti dello stato 'registrationUIState' in base ai risultati di validazione per ogni campo:
        loginUIState.value = loginUIState.value.copy(
            emailError = emailResult.status,
            passwordError = passwordResult.status,
        )

        // se tutti i campi sono stati validati allora lo stato 'allValidationCompleted' sarà true e di conseguenza
        // il button di registrazione si attiverà:
        if(emailResult.status && passwordResult.status){
            allValidationCompletedLogin.value = true
        }else{
            allValidationCompletedLogin.value = false
        }
    }

    // mi serve solo per verificare che il registrationUIState venga aggiornato
    // correttamente ogni volta che si verifica un 'UIEvent' usando il Logcat:
    private fun printStateSignUp(){
        Log.d(TAG, "Sono dentro printState() della registrazione")
        Log.d(TAG, registrationUIState.value.toString())
    }

    // mi serve solo per verificare che il loginUIState venga aggiornato
    // correttamente ogni volta che si verifica un 'UIEvent' usando il Logcat:
    private fun printStateSignIn(){
        Log.d(TAG, "Sono dentro printState() del login")
        Log.d(TAG, loginUIState.value.toString())
    }
}