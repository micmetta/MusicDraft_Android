package com.example.musicdraft.viewModel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.musicdraft.data.LoginUIState
import com.example.musicdraft.data.RegistrationUIState
import com.example.musicdraft.data.UIEventSignIn
import com.example.musicdraft.data.UIEventSignUp
import com.example.musicdraft.data.rules.ValidatorFields
import com.example.musicdraft.data.tables.user.User
import com.example.musicdraft.database.MusicDraftDatabase
import com.example.musicdraft.login.GoogleSignInState
import com.example.musicdraft.model.AuthRepository
import com.example.musicdraft.sections.Screens
import com.example.musicdraft.utility.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// - Nel momento in cui un 'UIEvent' verrà innescato, questo sarà catturato dal "LoginViewModel" che si
//   preoccuperà di gestirlo andando a modificare lo stato dell'interfaccia chiamato 'registrationUIState'.
class LoginViewModel(application: Application) : AndroidViewModel(application) {


//    // definisco l'oggetto DAO che mi permetterà di eseguire le queries sul DB:
//    private var userDao = application.userDao()

    // mi prendo il riferimento al DB:
    private val database = MusicDraftDatabase.getDatabase(application)
    private val userDao = database.userDao()
    ///////

    private val authRepository: AuthRepository = AuthRepository(this, userDao!!) // istanzio il repository

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

//    // Variabili per gestire il login con Google:
//    private val _state = MutableStateFlow(SignInState())
//    val state = _state.asStateFlow()
//    /////////////////////////////////////////////

    // Variabili per gestire il login con Google:
    val _googleState = mutableStateOf(GoogleSignInState()) // GoogleSignInState() è una data class creata appositamente
    val googleState: State<GoogleSignInState> = _googleState // state pubblico legato al login con Google fatto dall'utente
    /////////////////////////////////////////////

    // stato per attivare/disattivare la finestra 'ErrorDialog':
    var errorDialogActivated = mutableStateOf(false)
        private set
    var stringToShowErrorDialog = mutableStateOf("")
        private set
    ///////////////////////////////////////////////////////////////


    // sottoscrizione alla variabile "userLoggedInfo" sempre del repository, in questo modo
    // non appena "repository.userLoggedInfo" cambierà, automaticamente cambierà anche "userLoggedInfo" del LoginViewModel:
    var userLoggedInfo =  authRepository.userLoggedInfo
    //var userLoggedState = mutableStateOf(UserLoggedState())

    // altra sottoscrizione ad una variabile del repository:
    var allUsersFriendsOfCurrentUser =  authRepository.allUsersFriendsOfCurrentUser


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
                Log.d("LoginViewModel", "Sono entrato in is UIEventSignUp.EmailChanged ->")
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

            is UIEventSignUp.InvalidateDataSignUp -> {
                invalidateDataSigUp()
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

            is UIEventSignIn.InvalidateDataSignIn -> {
                invalidateDataSigIn()
            }
        }

        // Ogni volta che uno qualsiasi degli eventi sopra è stato gestito,
        // verrà eseguita subito la funzione 'validateDataLogin()' in modo tale che
        // ogni volta che l'utente inserisce/cancella un qualche carattere in una qualsiasi delle caselle di testo
        // (email e password), questa casella aggiornerà il proprio colore immediatamente in modo tale
        // da fargli capire se i dati che sta inserendo sono accettabili o meno dal sistema.
        validateDataLogin() // da inserire in un thread
    }

    // Questa funzione serve per invalidare i dati della schermata di registrazione subito dopo che l'utente
    // ha cliccato su questa schermata sul button 'Login'.
    private fun invalidateDataSigUp() {
        // resetto tutti i campi di "registrationUIState":
        registrationUIState.value = registrationUIState.value.copy(
            nickName = "",
            email = "",
            password = "",
            privacyPolicyAccepted = false
        )
        allValidationCompleted.value = false
    }

    // Questa funzione serve per invalidare i dati della schermata di login subito dopo che l'utente
    // ha cliccato su questa schermata sul button 'Register'.
    private fun invalidateDataSigIn() {
        // resetto tutti i campi di "loginUIState":
        loginUIState.value = loginUIState.value.copy(
            email = "",
            password = ""
        )
        allValidationCompletedLogin.value = false
    }


    // - Questa è la funzione che verrà eseguita una volta che l'utente avrà premuto sul button "Register".
    private fun signUp(navController: NavController) {

        Log.d(TAG, "Hai cliccato sul button 'Register'!")
        printStateSignUp()

        // - Con la funzione di sotto eseguo la validazione dei dati inseriti usando il 'ValidatorFields'  presente nel package 'rules':
        //validateData()

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // - Controllerà se la registrazione potrà andare a buon fine o meno:
        createUserInFirebase(
            nickname = registrationUIState.value.nickName,
            email = registrationUIState.value.email,
            password = registrationUIState.value.password,
            navController
        )
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//        // Qui passo alla schermata: navigationController.navigate(Screens.home.screen)
//        if (allValidationCompleted.value) {
//            // resetto tutti i campi di "registrationUIState":
//            registrationUIState.value = registrationUIState.value.copy(
//                nickName = "",
//                email = "",
//                password = "",
//                privacyPolicyAccepted = false
//            )
//            navController.navigate(Screens.MusicDraftUI.screen)
//        }

//        // Inserisco il nuovo utente nel DB:
//        SaveNewUserInDB(registrationUIState.value.email, registrationUIState.value.nickName, registrationUIState.value.password)
    }

    // - Questa è la funzione che verrà eseguita una volta che l'utente avrà premuto sul button "Login".
    private fun signIn(navController: NavController) {
        Log.d(TAG, "Hai cliccato sul button 'Login'!")
        printStateSignIn()

        // - Con la funzione di sotto eseguo la validazione dei dati inseriti usando il 'ValidatorFields'  presente nel package 'rules':
        validateDataLogin()

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // - Controllerà nel DB se il login potrà andare a buon fine o meno:
        login(navController)
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//        // Qui passo alla schermata: navigationController.navigate(Screens.home.screen)
//        if (allValidationCompletedLogin.value) {
//            // resetto tutti i campi di "loginUIState":
//            loginUIState.value = loginUIState.value.copy(
//                email = "",
//                password = ""
//            )
//            navController.navigate(Screens.MusicDraftUI.screen)
//        }
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
        Log.d(TAG, "registrationUIState corrente: " + registrationUIState.value.toString())
    }

    // mi serve solo per verificare che il loginUIState venga aggiornato
    // correttamente ogni volta che si verifica un 'UIEvent' usando il Logcat:
    private fun printStateSignIn(){
        Log.d(TAG, "Sono dentro printState() del login")
        Log.d(TAG, loginUIState.value.toString())
    }

    /*
     - Questa funzione verrà invocata dalla funzione di SignUp per creare e memorizzare nel DB di firebase
       il nuovo utente con tutte le sue info.
    */
    private fun createUserInFirebase(nickname: String, email:String, password:String, navController: NavController){

        // attivo l'indicatore di caricamento:
        signUpInProgress.value = true

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Qui controllo se nel DB esiste già un tale utente con il nickname inserito
        // prima di richiamare il servizio di FirebaseAuth in modo tale da
        // avere l'unicità anche sul Nickname poichè Firebase controlla l'unicità solo sul campo email:
        checkUserExistenceWithNickname(nickname) { exists ->
            if (!exists) {
                FirebaseAuth
                    .getInstance() // ottengo l'istanza di Firebase
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener{

//                        // disattivo l'indicatore di caricamento:
//                        signUpInProgress.value = false

                        // qui dentro c'è quello che verrà eseguito nel momento in cui il processo di creazione viene completato.
                        Log.d(TAG, "Sono dentro addOnCompleteListener di CREATE USER IN FIREBASE!")
                        Log.d(TAG, " isSuccesful = ${it.isSuccessful}")

                        if(it.isSuccessful){

                            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                            // Adesso sono certo di poter inserire il nuovo utente nel DB:
                            SaveNewUserInDB(registrationUIState.value.email, registrationUIState.value.nickName, registrationUIState.value.password)
                            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                            Log.d(TAG, "Il nuovo utente è stato REGISTRATO NEL DB DI FIREBASE!")

                            // resetto tutti i campi di "registrationUIState":
                            registrationUIState.value = registrationUIState.value.copy(
                                nickName = "",
                                email = "",
                                password = "",
                                privacyPolicyAccepted = false
                            )
                            navController.navigate(Screens.MusicDraftUI.screen) // cambio schermata

                        }
                    }
                    .addOnFailureListener {
                        // qui dentro c'è quello che verrà eseguito nel momento in cui si verifica un qualche errore durante il processo di creazione.
                        Log.d(TAG, "Sono dentro addOnFailureListener ")
                        Log.d(TAG, "Si è verificato un errore durante la creazione dell'utente su FIREBASE.")
                        Log.d(TAG, " Exception = ${it.message}") // messaggio d'errore
                        Log.d(TAG, " Exception = ${it.localizedMessage}")

                        // Attivo il Popup di errore che verrà mostrato all'utente:
                        stringToShowErrorDialog.value = it.message.toString()
                        errorDialogActivated.value = true
                    }
            }else{
                // L'utente non esiste, fai qualcos'altro
                println("Nickname already exists..")
                // Attivo il Popup di errore che verrà mostrato all'utente:
                stringToShowErrorDialog.value = "Nickname already exists into Database.."
                errorDialogActivated.value = true
            }
            // disattivo l'indicatore di caricamento:
            signUpInProgress.value = false
        }
    }

    /*
     - Questa funzione permette all'utente di eseguire il login (supponendo che si sia già registrato).
    */
    private fun login(navController: NavController){

        // attivo l'indicatore di caricamento:
        signInInProgress.value = true

        val email = loginUIState.value.email
        val password = loginUIState.value.password

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Qui controllo se nel DB esiste tale utente prima di richiamare il servizio di FirebaseAuth in modo tale da
        // utilizzare il CASE SENSITIVE poichè Firebase non lo implementa per le email:
        checkUserExistenceWithEmail(email) { exists ->
            if (exists) {
                // L'utente esiste, fai qualcosa
                println("User exist!")
                FirebaseAuth
                    .getInstance()
                    .signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener{
                        // lamda function che verrà eseguita qualora il login avesse successo
                        Log.d(TAG, "Sono dentro addOnCompleteListener di LOGIN!")
                        Log.d(TAG, "l'utente vuole loggare con questa mail: ${email}")

                        Log.d(TAG, " isSuccesful = ${it.isSuccessful}")
                        Log.d(TAG, "Login completato con successo!")
                        if(it.isSuccessful){

                            // prendo le info principali dell'utente dalla tabella User:
                            authRepository.getUserByEmail(email)

                            // resetto tutti i campi di "loginUIState":
                            loginUIState.value = loginUIState.value.copy(
                                email = "",
                                password = ""
                            )
                            navController.navigate(Screens.MusicDraftUI.screen)
                        }
                    }
                    .addOnFailureListener{
                        // lamda function che verrà eseguita qualora il login fallisse
                        Log.d(TAG, "Sono dentro addOnFailureListener di LOGIN!")
                        Log.d(TAG, "Si è verificato un errore durante il login dell'utente su FIREBASE.")
                        Log.d(TAG, " message = ${it.message}")
                        Log.d(TAG, " Exception = ${it.localizedMessage}")

                        // Attivo il Popup di errore che verrà mostrato all'utente:
                        stringToShowErrorDialog.value = it.message.toString()
                        errorDialogActivated.value = true
                    }
            } else {
                // L'utente non esiste, fai qualcos'altro
                println("User not exist..")
                // Attivo il Popup di errore che verrà mostrato all'utente:
                stringToShowErrorDialog.value = "User not exist into Database.."
                errorDialogActivated.value = true
            }
            // disattivo l'indicatore di caricamento:
            signInInProgress.value = false
        }
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    /*
     - Questa funzione permette all'utente di eseguire il logout.
    */
    fun logoutFromFirebase(navController: NavController){
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signOut()
        val authStateListener = AuthStateListener {
            if(it.currentUser == null){
                // se entro qui vuol dire che il logout è andato a buon fine.
                Log.d(TAG, "Logout eseguito con successo!")
                navController.navigate(Screens.Login.screen)
            }else{
                Log.d(TAG, "Logout fallito.")
            }
        }
        firebaseAuth.addAuthStateListener(authStateListener)
    }


    /*
    - Funzione che permette all'utente di eseguire il login con Google grazie a Firebase.
    */
    fun googleSignIn(credential: AuthCredential) = viewModelScope.launch{

        authRepository.googleSignIn(credential).collect{ result ->
            // Una volta ottenuta la risposta da 'repository.googleSignIn(credential)'
            // in base al caso che si verificherà verrà aggiornato lo stato '_googleState'
            // a cui l'UI è collegata e quindi in base al risultato l'interfaccia grafica
            // si auto-aggiornerà:
            when(result){
                is Resource.Success ->{
                    val email = result.data?.user?.email
                    if (email != null) {
                        Log.d("LoginViewModel", "Sono entrato in is googleSignIn -> is Resource.Success ->")
                        Log.d("LoginViewModel", "email: $email")
                        //registrationUIState.value = registrationUIState.value.copy(email = email)
                        registrationUIState.value = registrationUIState.value.copy(
                            email = email
                        )
                        printStateSignUp() // stampo per verificare che il registrationUIState sia stato aggiornato
                    }
                    _googleState.value = GoogleSignInState(success = result.data)
                }
                is Resource.Loading ->{
                    _googleState.value = GoogleSignInState(loading = true)
                }
                is Resource.Error ->{
                    _googleState.value = GoogleSignInState(error = result.message!!)
                }
            }
        }
    }

    fun reset_errorDialogActivated(){
        errorDialogActivated.value = false
    }
    fun reset_stringToShowErrorDialog(){
        stringToShowErrorDialog.value = ""
    }

    fun SaveNewUserInDB(email: String, nickname: String, password: String){
        ////////////////////////////////////////////////////
        // inserisco il nuovo utente nel DB:
        val user = User(
            email = email,
            nickname = nickname,
            password = password,
            isOnline = true,
            points = 1000 // I points iniziali sono 1000
        )
        authRepository.insertNewUser(user)
        ////////////////////////////////////////////////////
    }


    fun checkUserExistenceWithEmail(email: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val exists = withContext(Dispatchers.IO) {
                authRepository.doesUserExistWithEmail(email)
            }
            onResult(exists)
        }
    }

    fun checkUserExistenceWithNickname(nickname: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val exists = withContext(Dispatchers.IO) {
                authRepository.doesUserExistWithNickname(nickname)
            }
            onResult(exists)
        }
    }

    fun getAllNicknameFriendsOfCurrentUser(emails: List<String>){
        authRepository.getAllUsersFriendsOfCurrentUser(emails)
    }
}
