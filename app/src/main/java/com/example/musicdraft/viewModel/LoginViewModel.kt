package com.example.musicdraft.viewModel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.musicdraft.data.LoginUIState
import com.example.musicdraft.data.RegistrationUIState
import com.example.musicdraft.data.UIEventSignIn
import com.example.musicdraft.data.UIEventSignUp
import com.example.musicdraft.data.rules.ValidatorFields
import com.example.musicdraft.data.tables.handleFriends.HandleFriends
import com.example.musicdraft.data.tables.user.User
import com.example.musicdraft.database.MusicDraftDatabase
import com.example.musicdraft.login.GoogleSignInState
import com.example.musicdraft.model.AuthRepository
import com.example.musicdraft.sections.Screens
import com.google.firebase.auth.EmailAuthProvider
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

    // altra sottoscrizione:
    var friendRequestCard = authRepository.friendRequestCard

    ///////////////////////////////////////////////////////
    // altre sottoscrizioni a variabili del repository:
    var allUsersFriendsOfCurrentUser =  authRepository.allUsersFriendsOfCurrentUser
    var allUsersrReceivedRequestByCurrentUser =  authRepository.allUsersrReceivedRequestByCurrentUser
    ///////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////
    // States utili per permettere all'utente di eseguire il reset della password:
    var email by mutableStateOf("")
    var forgotPasswordInProgress by mutableStateOf(false)
    var forgotPasswordSuccess by mutableStateOf<String?>(null)
    var forgotPasswordError by mutableStateOf<String?>(null)
    var showDialogSentEmail = mutableStateOf(false)
        private set
    var showDialogErrorSentEmail = mutableStateOf(false)
        private set
    /////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////
    // States utili per permettere all'utente di eseguire il reset del nickname:
    var showDialogUpdateNickname = mutableStateOf(false)
        private set
    var showDialogErrorUpdateNickname = mutableStateOf(false)
        private set
    var messageDialogErrorUpdateNickname = mutableStateOf("")
    /////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////////
    // Mutable live data per gestire la sessione attiva dell'utente:
    val isUSerLoggedIn: MutableLiveData<Boolean> = MutableLiveData()
    /////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////
    var utilityFriendInfo = authRepository.utilityFriendInfo
    /////////////////////////////////////////////////////////////////////////////

    var emailUserLog = mutableStateOf("") // conterrà l'email dell'utente loggato attraverso o
    // la registrazione o attraverso il login.

    /////////////////////////////////////////////////////////////////////////////
    val opponentMatch = authRepository.opponentMatch
    /////////////////////////////////////////////////////////////////////////////

    /**
     * Gestisce gli eventi UI relativi alla schermata di registrazione dell'utente.
     *
     * La funzione qui sotto verrà invocata ogni volta che l'utente
     * farà scattare un qualche evento sulla schermata di Creazione account ("SignUpScreen.kt")
     * per questo motivo prende in input un evento di tipo "UIEvent".
     *
     * @param event Evento UI da gestire, derivato da [UIEventSignUp].
     * @param navController Controller di navigazione per la navigazione tra schermate.
     */
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


    /**
     * Gestisce gli eventi UI relativi alla schermata di accesso dell'utente.
     *
     * La funzione qui sotto verrà invocata ogni volta che l'utente
     * farà scattare un qualche evento sulla schermata di Login ("SignInScreen.kt")
     * per questo motivo prende in input un evento di tipo "UIEvent".
     *
     * @param event Evento UI da gestire, derivato da [UIEventSignIn].
     * @param navController Controller di navigazione per la navigazione tra schermate.
     */
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

            // evento che viene generato nel momento in cui l'utente vuole aggiornare la password
            is UIEventSignIn.ForgotPassword -> {
                navController.navigate(Screens.ForgotPassword.screen)
            }

            is UIEventSignIn.UpdateNickname -> {
                navController.navigate(Screens.UpdateNickname.screen)
            }
        }

        // Ogni volta che uno qualsiasi degli eventi sopra è stato gestito,
        // verrà eseguita subito la funzione 'validateDataLogin()' in modo tale che
        // ogni volta che l'utente inserisce/cancella un qualche carattere in una qualsiasi delle caselle di testo
        // (email e password), questa casella aggiornerà il proprio colore immediatamente in modo tale
        // da fargli capire se i dati che sta inserendo sono accettabili o meno dal sistema.
        validateDataLogin() // da inserire in un thread
    }

    /**
     * Invalida i dati della schermata di registrazione resettando tutti i campi dello stato 'registrationUIState'.
     * Viene chiamata quando l'utente passa dalla schermata di registrazione alla schermata di accesso.
     */
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

    /**
     * Invalida i dati della schermata di accesso resettando tutti i campi dello stato 'loginUIState'.
     * Viene chiamata quando l'utente passa dalla schermata di accesso alla schermata di registrazione.
     */
    private fun invalidateDataSigIn() {
        // resetto tutti i campi di "loginUIState":
        loginUIState.value = loginUIState.value.copy(
            email = "",
            password = ""
        )
        allValidationCompletedLogin.value = false
    }


    /**
     * Esegue la procedura di registrazione dell'utente.
     * Questa funzione viene chiamata quando l'utente preme il pulsante "Register" nella schermata di registrazione.
     *
     * @param navController Controller di navigazione per la navigazione tra schermate.
     */    private fun signUp(navController: NavController) {

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

    /**
     * Gestisce il processo di accesso dell'utente quando l'utente preme il pulsante "Login" nella schermata di accesso.
     *
     * @param navController Controller di navigazione per la navigazione tra schermate.
     */
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


    /**
     * Esegue la validazione dei dati inseriti dall'utente nella schermata di registrazione.
     * Aggiorna lo stato dell'interfaccia 'registrationUIState' con i risultati di validazione.
     */
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

    /**
     * Esegue la validazione dei dati inseriti dall'utente nella schermata di login.
     * Aggiorna lo stato dell'interfaccia 'loginUIState' con i risultati di validazione.
     */
    private fun validateDataLogin() {
        val emailResult = ValidatorFields.validateEmail(email = loginUIState.value.email)
        val passwordResult = ValidatorFields.validatePassword(password = loginUIState.value.password)

        Log.d(TAG, "Sono dentro validateData() e i risultati delle validazioni sono i seguenti:")
        Log.d(TAG, "emailResult= $emailResult")
        Log.d(TAG, "passwordResult= $passwordResult")

        // aggiornamenti dello stato 'loginUIState' in base ai risultati di validazione per ogni campo:
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

    /**
     * Stampa lo stato corrente dello stato 'registrationUIState' nel Logcat per scopi di debug durante la registrazione.
     */
    private fun printStateSignUp(){
        Log.d(TAG, "Sono dentro printState() della registrazione")
        Log.d(TAG, "registrationUIState corrente: " + registrationUIState.value.toString())
    }

    /**
     * Stampa lo stato corrente dello stato 'loginUIState' nel Logcat per scopi di debug durante l'accesso.
     */
    private fun printStateSignIn(){
        Log.d(TAG, "Sono dentro printState() del login")
        Log.d(TAG, loginUIState.value.toString())
    }

    /**
     * Crea un nuovo utente nel database Firebase Authentication con le credenziali fornite.
     * Controlla prima se esiste già un utente con lo stesso nickname nel proprio database locale
     * per garantire l'unicità del nickname.
     *
     * @param nickname Nickname dell'utente da registrare.
     * @param email Email dell'utente da registrare.
     * @param password Password dell'utente da registrare.
     * @param navController Controller di navigazione per la navigazione tra schermate.
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

                            // prendo le info principali dell'utente dalla tabella User:
                            //authRepository.getUserByEmail(email)
                            emailUserLog.value = email // mi memorizzo l'email dell'utente che si è appena registrato

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

    /**
     * Esegue il login dell'utente utilizzando le credenziali fornite.
     * Controlla prima nel database locale se l'utente esiste.
     *
     * @param navController Controller di navigazione per la navigazione tra schermate.
     */
    private fun login(navController: NavController){

        //checkForActiveSessionUser()
        //securityLogoutFromFirebase()

        // attivo l'indicatore di caricamento:
        signInInProgress.value = true

        ////////////////////////////////////////////////////////
//        val firebaseAuth = FirebaseAuth.getInstance()
//        firebaseAuth.signOut()
        ////////////////////////////////////////////////////////

        val email = loginUIState.value.email
        val password = loginUIState.value.password
        emailUserLog.value = email // mi memorizzo l'email dell'utente che vuole loggare

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
                        Log.d(TAG, "login: Sono dentro addOnCompleteListener di LOGIN!")
                        Log.d(TAG, "login: l'utente vuole loggare con questa mail: ${email}")

                        Log.d(TAG, "login: isSuccesful = ${it.isSuccessful}")
                        Log.d(TAG, "login: Login completato con successo!")

                        if(it.isSuccessful){

                            Log.d(TAG, "login: it.isSuccessful")

                            // prendo le info principali dell'utente dalla tabella User per aggiornare automaticamente
                            // anche il mutableState 'userLoggedInfo' che contiene le info dell'utente
                            // (come ad esempio l'email) che verranno mostrate automaticamente sull'interfaccia
                            // grafica (in particolare nella schermata 'Home' e 'Friends'):
                            //authRepository.getUserByEmail(email)

                            // setto che l'utente è online (in modo tale da avere isOnline=true nel DB in
                            // corrispondenza dell'utente corrente):
                            authRepository.setisOnlineUser(email, true)

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
                        Log.d(TAG, "login: Sono dentro addOnFailureListener di LOGIN!")
                        Log.d(TAG, "login: Si è verificato un errore durante il login dell'utente su FIREBASE.")
                        Log.d(TAG, "login: message = ${it.message}")
                        Log.d(TAG, "login: Exception = ${it.localizedMessage}")

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

    /**
     * Ottiene le informazioni dell'utente dal repository utilizzando l'email memorizzata in 'emailUserLog'.
     */
    fun getUserByEmail(){
        authRepository.getUserByEmail(emailUserLog.value)
    }


    /**
     * Controlla se c'è una sessione utente attiva verificando l'esistenza di un utente corrente in Firebase Authentication.
     * Se c'è una sessione attiva, aggiorna lo stato dell'utente loggato nel LiveData `isUSerLoggedIn` e ottiene le informazioni
     * dell'utente dal repository utilizzando l'email dell'utente attivo.
     * Se non c'è una sessione attiva, imposta il LiveData `isUSerLoggedIn` su false.
     */
    fun checkForActiveSessionUser(){
        if(FirebaseAuth.getInstance().currentUser != null){
            // mi prendo l'email dell'utente ancora attivo
            FirebaseAuth.getInstance().currentUser?.also {
                it.email?.also {email ->
                    // se entro qui vuol dire che c'è ancora una sessione attiva
                    Log.d(TAG, "checkForActiveSessionUser: C'è ancora una sessione attiva!")
                    isUSerLoggedIn.value = true // aggiorno il mutableLiveData che memorizza lo stato della sessione
                    // adesso invoco il metodo 'authRepository.getUserByEmail(email)' passandogli l'email dell'utente attivo,
                    // in modo tale da
                    // aggiornare automaticamente anche la var 'userLoggedInfo' che conterrà i dati
                    // dell'utente che verranno mostrati sull'interfaccia grafica (in particolare nella
                    // schermata 'Home' e 'Friends'):
                    authRepository.getUserByEmail(email)
                }
            }
        }else{
            Log.d(TAG, "checkForActiveSessionUser: NON c'è una sessione attiva!")
            isUSerLoggedIn.value = false
        }
    }


    /**
     * Esegue il logout dell'utente da Firebase Authentication.
     * Effettua il logout dell'utente corrente, aggiorna lo stato dei LiveData `loginUIState`, `registrationUIState`
     * e `isUSerLoggedIn`, e naviga l'utente alla schermata di login.
     *
     * @param navController Controller di navigazione per la navigazione tra schermate.
     */
    fun logoutFromFirebase(navController: NavController) {

        val firebaseAuth = FirebaseAuth.getInstance()
        val emailUserLogged = firebaseAuth.currentUser?.email
        firebaseAuth.signOut() // eseguo il logout

        val authStateListener = AuthStateListener {
                if (firebaseAuth.currentUser == null) {

                    // Logout successful
                    if (emailUserLogged != null) {
                        Log.d(TAG, "logoutFromFirebase, valore di 'emailUserLogged' prima di 'authRepository.LogoutUserLoggedInfo(emailUserLogged)': $emailUserLogged")
                        authRepository.LogoutUserLoggedInfo(emailUserLogged)
                    }

                    // Reset loginUIState
                    loginUIState.value = loginUIState.value.copy(
                        email = "",
                        password = ""
                    )

                    // Reset registrationUIState
                    registrationUIState.value = registrationUIState.value.copy(
                        nickName = "",
                        email = "",
                        password = "",
                        privacyPolicyAccepted = false
                    )

                    // Update login status
                    isUSerLoggedIn.value = false

                    Log.d(TAG, "logoutFromFirebase: Logout eseguito con successo!")
                    navController.navigate(Screens.Login.screen) {
                        popUpTo(0) // Clear the back stack
                    }

                    //authRepository.getUserByEmail("")

                    // Remove the listener
                    //firebaseAuth.removeAuthStateListener(this)
                } else {
                    Log.d(TAG, "logoutFromFirebase: Logout fallito.")
                    // Remove the listener
                    //firebaseAuth.removeAuthStateListener(this)
                }
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        //firebaseAuth.addAuthStateListener(authStateListener)
        //firebaseAuth.signOut() // Perform logout
        //firebaseAuth.removeAuthStateListener(authStateListener)
    }

    /**
     * Esegue il logout dell'utente attualmente loggato da Firebase Authentication.
     * Questa funzione viene utilizzata per garantire un logout sicuro, ascoltando lo stato
     * di autenticazione per assicurarsi che il logout sia completato correttamente.
     * Una volta eseguito il logout, aggiorna lo stato dell'utente, resetta i dati di login e
     * registrazione, e imposta `isUSerLoggedIn` su false.
     */
    fun securityLogoutFromFirebase(){

        if(FirebaseAuth.getInstance().currentUser != null) {

            val firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.signOut()

            val authStateListener = AuthStateListener {
                if (it.currentUser == null) {

                    // se entro qui vuol dire che il logout è andato a buon fine.
                    userLoggedInfo.value?.let {
                        it1 -> authRepository.LogoutUserLoggedInfo(it1.email)
                    }

                    // resetto tutti i campi di "loginUIState":
                    loginUIState.value = loginUIState.value.copy(
                        email = "",
                        password = ""
                    )

                    // resetto tutti i campi di "registrationUIState":
                    registrationUIState.value = registrationUIState.value.copy(
                        nickName = "",
                        email = "",
                        password = "",
                        privacyPolicyAccepted = false
                    )

                    // specifico che non c'è un utente loggato:
                    isUSerLoggedIn.value = false

                    Log.d(
                        TAG,
                        "securityLogoutFromFirebase: Logout di SICUREZZA eseguito con successo!"
                    )

                } else {
                    Log.d(TAG, "securityLogoutFromFirebase: Logout di SICUREZZA fallito.")
                }
            }
            firebaseAuth.addAuthStateListener(authStateListener)

        }else{
            Log.d(TAG, "securityLogoutFromFirebase: Logout di SICUREZZA non necessario.")
        }
    }



    /**
     * Invia una email per il reset della password all'indirizzo specificato.
     * Verifica prima se l'email esiste nel database Firebase. Se l'utente esiste,
     * invia l'email di reset della password. Altrimenti, mostra un messaggio di errore.
     *
     * @param email Indirizzo email dell'utente per il quale si vuole reimpostare la password.
     */
    fun forgotPassword(email: String) {
        forgotPasswordInProgress = true
        forgotPasswordSuccess = null
        forgotPasswordError = null

        checkUserExistenceWithEmail(email) { exists ->
            if (exists) {
                // L'utente esiste:
                Log.d(TAG, "L'email: ${email} esiste nel DB e quindi avvio il processo di reset della password.")
                FirebaseAuth
                    .getInstance()
                    .sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        forgotPasswordInProgress = false
                        if (task.isSuccessful) {
                            forgotPasswordSuccess = "Email sent, check it to reset your password"
                            showDialogSentEmail.value = true
                        } else {
                            forgotPasswordError = "Error sending the reset email: ${task.exception?.message}"
                        }
                    }
                    .addOnFailureListener { exception ->
                        forgotPasswordInProgress = false
                        forgotPasswordError = "Error sending the reset email: ${exception.message}"
                    }
            }else{
                Log.d(TAG, "L'email: ${email} NON esiste nel DB e quindi NON POSSO AVVIARE IL processo di reset della password..")
                showDialogErrorSentEmail.value = true
            }

        }
    }

    /**
     * Rieffettua l'autenticazione dell'utente utilizzando le credenziali email/password fornite.
     * Questa funzione è utilizzata per riautenticare l'utente prima di operazioni sensibili come il cambio
     * della password.
     *
     * @param email Email dell'utente per la riautenticazione.
     * @param password Password dell'utente per la riautenticazione.
     * @param onComplete Callback che ritorna il successo o il fallimento dell'operazione di riautenticazione.
     *                  Il primo parametro indica se l'operazione è riuscita, il secondo contiene un messaggio di errore in caso di fallimento.
     */
    fun reauthenticateUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val credential = EmailAuthProvider.getCredential(email, password)
            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User re-authenticated.")
                        onComplete(true, null)
                    } else {
                        Log.e(TAG, "User re-authentication failed: ${task.exception?.message}")
                        onComplete(false, task.exception?.message)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "User re-authentication failed: ${exception.message}")
                    onComplete(false, exception.message)
                }
        } else {
            onComplete(false, "No active user found.")
        }
    }

//    fun sendVerificationEmail(user: FirebaseUser, newEmail: String, onComplete: (Boolean, String?) -> Unit) {
//        user.verifyBeforeUpdateEmail(newEmail)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Log.d(TAG, "Verification email sent to $newEmail.")
//                    onComplete(true, null) // notifico il chiamante facendogli capire che l'operazione è stata completata con successo.
//                } else {
//                    Log.e(TAG, "Failed to send verification email: ${task.exception?.message}")
//                    onComplete(false, task.exception?.message) // notifico il chiamante facendogli capire che l'operazione è stata completata con INsuccesso.
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.e(TAG, "Failed to send verification email: ${exception.message}")
//                onComplete(false, exception.message) // notifico il chiamante facendogli capire che l'operazione è stata completata con INsuccesso.
//            }
//    }
//    fun updateEmail(currentEmail: String, password: String, newEmail: String) {
//
//        checkUserExistenceWithEmail(newEmail) { exists ->
//            if (!exists) {
//                Log.d(TAG, "L'email: $newEmail NON esiste ancora nel DB e quindi posso avviare il processo di update dell'email.")
//
//                val user = FirebaseAuth.getInstance().currentUser
//                if (user != null) {
//                    // Prima ri-autentica l'utente
//                    reauthenticateUser(currentEmail, password) { reauthSuccess, reauthError ->
//                        if (reauthSuccess) {
//                            // Procedi con l'aggiornamento dell'email
//                            sendVerificationEmail(user, newEmail) { success, errorMessage ->
//                                if (success) {
//                                    Log.d(TAG, "Email di verifica inviata all'email corrente!")
//                                    showDialogSentEmail.value = true
//                                } else {
//                                    Log.e(TAG, "Errore durante l'invio dell'email di verifica: $errorMessage")
//                                    showDialogErrorSentEmail.value = true
//                                }
//                            }
//                        } else {
//                            Log.e(TAG, "Ri-autenticazione fallita: $reauthError")
//                            showDialogErrorSentEmail.value = true
//                        }
//                    }
//                } else {
//                    Log.e(TAG, "Non c'è nessuna sessione attiva e quindi non posso eseguire l'update dell'email..")
//                    showDialogErrorSentEmail.value = true
//                }
//            } else {
//                Log.d(TAG, "L'email: $newEmail esiste già nel DB e quindi NON POSSO AVVIARE IL processo di update dell'email.")
//                showDialogErrorSentEmail.value = true
//            }
//        }
//    }
    /**
     * Aggiorna il nickname dell'utente nel database Firebase.
     * Verifica prima se il nuovo nickname non esiste già nel database.
     * Se il nickname non esiste, riautentica l'utente e procede con l'aggiornamento del nickname.
     * Altrimenti, mostra un messaggio di errore indicando che il nickname esiste già.
     *
     * @param currentEmail Email corrente dell'utente.
     * @param password Password dell'utente per la riautenticazione.
     * @param currentNickname Nickname attuale dell'utente.
     * @param newNickname Nuovo nickname desiderato.
     */
    fun updateNickname(currentEmail: String, password: String, currentNickname: String, newNickname: String){
        checkUserExistenceWithNickname(newNickname) { exists ->
            if (!exists) {
                Log.d(TAG, "Il nickname: $newNickname NON esiste ancora nel DB e quindi posso avviare il processo di update del nickname.")

                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    // Prima ri-autentica l'utente
                    reauthenticateUser(currentEmail, password) { reauthSuccess, reauthError ->
                        if (reauthSuccess) {
                            // Procedi con l'aggiornamento dell'email
                            authRepository.updateNicknameUser(currentEmail, currentNickname, newNickname)
                            showDialogUpdateNickname.value = true
                        } else {
                            Log.e(TAG, "Ri-autenticazione fallita: $reauthError")
                            if (reauthError != null) {
                                messageDialogErrorUpdateNickname.value = reauthError
                            }
                            showDialogErrorUpdateNickname.value = true
                        }
                    }
                } else {
                    Log.e(TAG, "Non c'è nessuna sessione attiva e quindi non posso eseguire l'update dell'email..")
                    messageDialogErrorUpdateNickname.value = "There isn't an active session and so it is impossible update nickname.."
                    showDialogErrorUpdateNickname.value = true
                }
            } else {
                Log.d(TAG, "Il nickname: $newNickname esiste già nel DB e quindi NON POSSO AVVIARE IL processo di update del nickname.")
                messageDialogErrorUpdateNickname.value = "The new nickname already exists into DB so it is impossible update nickname.."
                showDialogErrorUpdateNickname.value = true
            }
        }
    }
    /**
     * Naviga alla schermata delle impostazioni.
     *
     * @param navController NavController utilizzato per la navigazione.
     */
    fun backToScreenSettings(navController: NavController){
        navController.navigate(Screens.Settings.screen)
    }

    /**
     * Resetta il flag `errorDialogActivated` a false per nascondere eventuali dialoghi di errore.
     */
    fun reset_errorDialogActivated(){
        errorDialogActivated.value = false
    }
    /**
     * Resetta la stringa `stringToShowErrorDialog` a una stringa vuota.
     */
    fun reset_stringToShowErrorDialog(){
        stringToShowErrorDialog.value = ""
    }
    /**
     * Salva un nuovo utente nel database.
     *
     * @param email Email del nuovo utente.
     * @param nickname Nickname del nuovo utente.
     * @param password Password del nuovo utente.
     */
    fun SaveNewUserInDB(email: String, nickname: String, password: String){
        ////////////////////////////////////////////////////
        // inserisco il nuovo utente nel DB:
        val user = User(
            email = email,
            nickname = nickname,
            //password = password,
            isOnline = true, // setto che l'utente in questo momento è online
            points = 1000 // I points iniziali sono 1000
        )
        authRepository.insertNewUser(user)
        ////////////////////////////////////////////////////
    }
    /**
     * Verifica se esiste un utente nel database Firebase con l'email specificata.
     * Il risultato viene ritornato tramite il callback `onResult`.
     *
     * @param email Email dell'utente da verificare.
     * @param onResult Callback che ritorna true se l'utente esiste, altrimenti false.
     */
    fun checkUserExistenceWithEmail(email: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val exists = withContext(Dispatchers.IO) {
                authRepository.doesUserExistWithEmail(email)
            }
            onResult(exists)
        }
    }
    /**
     * Verifica se esiste un utente nel database Firebase con il nickname specificato.
     * Il risultato viene ritornato tramite il callback `onResult`.
     *
     * @param nickname Nickname dell'utente da verificare.
     * @param onResult Callback che ritorna true se l'utente esiste, altrimenti false.
     */
    fun checkUserExistenceWithNickname(nickname: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val exists = withContext(Dispatchers.IO) {
                authRepository.doesUserExistWithNickname(nickname)
            }
            onResult(exists)
        }
    }
    /**
     * Ottiene tutti i nickname degli amici dell'utente corrente.
     *
     * @param emails Lista di email degli amici dell'utente.
     */    fun getAllNicknameFriendsOfCurrentUser(emails: List<String>){
        authRepository.getAllUsersFriendsOfCurrentUser(emails)
    }
    /**
     * Ottiene tutti gli utenti che hanno ricevuto una richiesta di amicizia dall'utente corrente.
     *
     * @param emails Lista di oggetti `HandleFriends` contenente le email degli utenti che hanno ricevuto la richiesta.
     */
    fun getallUsersrReceivedRequestByCurrentUser(emails: List<HandleFriends>?){
        // Da ogni 'handleFriends' presente in emails prendo solo il valore del campo 'email2' in modo tale da crearmi
        // una lista di email di utenti che hanno ricevuto la richiesta dall'utente corrente e dopodchè
        // fornisco tale lista in input al metodo 'authRepository.getallUsersrReceivedRequestByCurrentUser'
        // per prendermi tutti i nickname di questi utenti:
        val email2List: List<String>? = emails?.map { it.email2 }
        if (email2List != null) {
            authRepository.getallUsersrReceivedRequestByCurrentUser(email2List)
        }
    }

    /**
     * Ottiene un utente dal repository utilizzando il nickname specificato.
     *
     * @param nickname Nickname dell'utente da ottenere.
     */
    fun getUserByNickname(nickname: String){
        authRepository.getUserByNickname(nickname)
    }

    /**
     * Ottiene un amico dal repository utilizzando il nickname specificato.
     *
     * @param nickname Nickname dell'amico da ottenere.
     */
    fun getFriendByNickname(nickname: String){
        authRepository.getFriendByNickname(nickname)
    }

    /**
     * Ottiene un avversario dal repository utilizzando il nickname specificato.
     *
     * @param nickname Nickname dell'avversario da ottenere.
     */
    fun getOpponentByNickname(nickname: String){
        authRepository.getOpponentByNickname(nickname)
    }

    /**
     * Aggiunge punti all'utente nel repository.
     *
     * @param addPoints Punti da aggiungere.
     * @param email Email dell'utente a cui aggiungere i punti.
     */
    fun addPoints(addPoints: Int, email: String) {
        authRepository.addPoints(addPoints, email)
    }

    /**
     * Sottrae punti all'utente nel repository.
     *
     * @param subtractPoints Punti da sottrarre.
     * @param email Email dell'utente a cui sottrarre i punti.
     */
    fun subtractPoints(subtractPoints: Int, email: String) {
        authRepository.subtractPoints(subtractPoints, email)
    }

}
