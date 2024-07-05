package com.example.musicdraft

//import com.example.musicdraft.utility.ExchangeCards
//import com.example.musicdraft.utility.ExchangeCards
import DeckViewModel
import Marketplace
import Decks
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.musicdraft.factory.CardsViewModelFactory
import com.example.musicdraft.factory.DeckViewModelFactory
import com.example.musicdraft.factory.MarketplaceViewModelFactory
import com.example.musicdraft.screens_to_signUp_signIn.ForgotPassword
import com.example.musicdraft.screens_to_signUp_signIn.LoginScreen
import com.example.musicdraft.screens_to_signUp_signIn.SignUpScreen
import com.example.musicdraft.screens_to_signUp_signIn.TermsAndConditionsScreen
import com.example.musicdraft.sections.Cards
import com.example.musicdraft.sections.Decks
import com.example.musicdraft.sections.ExchangeCards
import com.example.musicdraft.sections.Friends
import com.example.musicdraft.sections.Home
import com.example.musicdraft.sections.Matchmaking
import com.example.musicdraft.sections.Screens
import com.example.musicdraft.sections.SelectDeck
import com.example.musicdraft.sections.Settings
import com.example.musicdraft.sections.ShowOfferReceived
import com.example.musicdraft.sections.ShowOfferSent
import com.example.musicdraft.ui.theme.BlueApp
import com.example.musicdraft.ui.theme.MusicDraftTheme
import com.example.musicdraft.utility.NavigationManager
import com.example.musicdraft.utility.UpdateNickname
import com.example.musicdraft.viewModel.CardsViewModel
import com.example.musicdraft.viewModel.DeckViewModel
import com.example.musicdraft.viewModel.ExchangeManagementCardsViewModel
import com.example.musicdraft.viewModel.HandleFriendsViewModel
import com.example.musicdraft.viewModel.LoginViewModel
import com.example.musicdraft.viewModel.MarketplaceViewModel
import com.example.musicdraft.viewModel.MatchmakingViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.launch

/**
 * Classe principale dell'attività che avvia l'applicazione.
 */
class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {

        //this.deleteDatabase("musicDraftDB")

        super.onCreate(savedInstanceState)
        setContent {

            val loginViewModel: LoginViewModel by viewModels()
            val handleFriendsViewModel: HandleFriendsViewModel by viewModels()
            val exchangeManagementCardsViewModel: ExchangeManagementCardsViewModel by viewModels()
            val matchmakingViewModel: MatchmakingViewModel by viewModels()

            val cardsViewModelFactory = CardsViewModelFactory(application, loginViewModel)
            val cardsViewModel: CardsViewModel = ViewModelProvider(this, cardsViewModelFactory).get(CardsViewModel::class.java)

            val marketplaceViewModelFactory = MarketplaceViewModelFactory(application, cardsViewModel, loginViewModel)
            val marketplaceViewModel: MarketplaceViewModel = ViewModelProvider(this, marketplaceViewModelFactory).get(MarketplaceViewModel::class.java)

            val deckfactory = DeckViewModelFactory(application, loginViewModel, cardsViewModel, exchangeManagementCardsViewModel)
            val decksViewModel = ViewModelProvider(this, deckfactory).get(DeckViewModel::class.java)


            MusicDraftTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ){
                    Navigation(loginViewModel, handleFriendsViewModel, exchangeManagementCardsViewModel, cardsViewModel, marketplaceViewModel, decksViewModel, matchmakingViewModel)
                    //Navigation(loginViewModel, handleFriendsViewModel,cardsViewModel,marketplaceViewModel,decksViewModel)
                }
            }
        }
    }
}

/**
 * Funzione composable che gestisce la navigazione all'interno dell'applicazione.
 *
 * @param loginViewModel ViewModel per la gestione del login.
 * @param handleFriendsViewModel ViewModel per la gestione degli amici.
 * @param cardsViewModel ViewModel per la gestione delle carte.
 * @param marketplaceViewmodel ViewModel per la gestione del marketplace.
 * @param decksViewModel ViewModel per la gestione dei mazzi.
 */
@Composable
//fun Navigation(loginViewModel: LoginViewModel, state: SignInState, launcher: ActivityResultLauncher<IntentSenderRequest>, googleAuthUiClient: GoogleAuthUiClient, context: Context){ // c'era prima
//fun Navigation(loginViewModel: LoginViewModel, state: SignInState){
fun Navigation(
    loginViewModel: LoginViewModel,
    handleFriendsViewModel: HandleFriendsViewModel,
    exchangeManagementCardsViewModel: ExchangeManagementCardsViewModel,
    cardsViewModel: CardsViewModel,
    marketplaceViewmodel: MarketplaceViewModel,
    decksViewModel: DeckViewModel,
    matchmakingViewModel: MatchmakingViewModel
){
    val navigationController = rememberNavController()

    // Determina la schermata iniziale in base al fatto se già esiste o meno una sessione attiva:
    loginViewModel.checkForActiveSessionUser() // il metodo 'loginViewModel.checkForActiveSessionUser()', qualora esistesse una sessione attiva, prende (da Firebase) l'email dell'utente
    // che fa parte della sessione attiva e aggiorna automaticamente (tramite invocazione del metodo 'getUserByEmail' dell'AuthRepository)
    // il mutableStateFlow 'userLoggedInfo' (al quale il loginViewModel è sottoscritto) che contiene le info dell'utente.
    // Grazie a questo aggiornamento, nel momento in cui verrà mostrata la schermata 'Home', verranno mostrati le info dell'utente ancora attivo.

    val startDestination = if (loginViewModel.isUSerLoggedIn.value == true) {
        Log.d("Navigation", "Esiste già un utente loggato e quindi vado direttamente alla schermata Home!")
        Screens.MusicDraftUI.screen // se c'è ancora una sessione attivo vado alla schermata Screens.MusicDraftUI.screen'
        // che farà apparire la sezione 'Home' con i dati dell'utente della sessione attiva.
    } else {
        Log.d("Navigation", "NON esiste già un utente loggato e quindi vado alla schermata di SignUp.")
        Screens.SignUp.screen // altrimenti apparirà la schermata di registrazione.
    }


    // - La Schermata iniziale sarà "SignUp" ovvero quella di registrazione dell'utente
    //NavHost(navController = navigationController, startDestination = Screens.SignUp.screen){ // c'era prima..
    NavHost(navController = navigationController, startDestination = startDestination){
        composable(Screens.SignUp.screen){
            SignUpScreen(navigationController, loginViewModel) // composable che verrà aperto per mostrare la creazione dell'account (c'era prima..)
        }
        composable(Screens.Login.screen){
            //LoginScreen(navigationController) // composable che verrà aperto per mostrare il login (c'era prima..)
            LoginScreen(navigationController, loginViewModel)
        }
        composable(Screens.TermsAndConditionsScreen.screen){
            TermsAndConditionsScreen() // composable che verrà aperto per mostrare i termini e condizioni dell'app
        }
        composable(Screens.ForgotPassword.screen){
            ForgotPassword(navigationController, loginViewModel)
        }
        composable(Screens.UpdateNickname.screen){
            UpdateNickname(navigationController, loginViewModel)
        }
        composable(Screens.ExchangeCards.screen){
            ExchangeCards(navigationController, loginViewModel, exchangeManagementCardsViewModel, cardsViewModel, decksViewModel)
        }
        composable(Screens.ShowOfferReceived.screen){
            ShowOfferReceived(navigationController, exchangeManagementCardsViewModel, cardsViewModel, loginViewModel, decksViewModel)
        }
        composable(Screens.ShowOfferSent.screen){
            ShowOfferSent(navigationController, exchangeManagementCardsViewModel, cardsViewModel, loginViewModel)
        }

        composable(Screens.SelectDeck.screen){
            SelectDeck(navigationController, matchmakingViewModel, decksViewModel, loginViewModel)
        }
        composable(Screens.MusicDraftUI.screen){
            MusicDraftUI(navigationController, loginViewModel, handleFriendsViewModel, exchangeManagementCardsViewModel, cardsViewModel, marketplaceViewmodel, decksViewModel, matchmakingViewModel) // composable che verrà aperto una volta che l'utente sarà loggato nell'app
        }
    }
}


/**
 * Funzione composable che rappresenta l'interfaccia utente principale dell'app una volta loggati.
 *
 * @param navControllerInitialScreens NavController per gestire la navigazione tra schermate.
 * @param loginViewModel ViewModel per la gestione del login.
 * @param handleFriendsViewModel ViewModel per la gestione degli amici.
 * @param cardsViewModel ViewModel per la gestione delle carte.
 * @param marketplaceViewmodel ViewModel per la gestione del marketplace.
 * @param decksViewModel ViewModel per la gestione dei mazzi.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicDraftUI(
    navControllerInitialScreens: NavController,
    loginViewModel: LoginViewModel,
    handleFriendsViewModel: HandleFriendsViewModel,
    exchangeManagementCardsViewModel: ExchangeManagementCardsViewModel,
    cardsViewModel: CardsViewModel,
    marketplaceViewmodel: MarketplaceViewModel,
    decksViewModel: DeckViewModel,
    matchmakingViewModel: MatchmakingViewModel
){
    val navigationController = rememberNavController() // inizializzazione del nav controller
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val context = LocalContext.current.applicationContext

    val navigationManager = remember { NavigationManager() } // istanzio il navigationManager
    val num_max_new_screens = 10 // num max di schermate che potranno essere inserite nello stack.
    val NUM_POINTS_MIN = 100 // numero minimo di points richiesti per giocare un game

    // Il "ModalNavigationDrawer" sarà proprio il menù laterale sulla sinistra in verticale tramite il quale
    // l'utente potrà navigare all'interno delle diverse sezioni dell'app:
    ModalNavigationDrawer(
        // Nelle '()' Definisco tutti i paremetri del "ModalNavigationDrawer":

        drawerState = drawerState, // mi permette di gestire la chiusura e l'apertura del menù in verticale sulla sinistra che mostrerà tutte
        // le sezioni sulle quali l'utente potrà cliccare.
        gesturesEnabled = true, // abilita lo swap del 'ModalNavigationDrawer'
        drawerContent = {
            ModalDrawerSheet {
                Box(modifier = Modifier
                    .background(BlueApp) // Imposta come colore di sfondo il BlueApp
                    .fillMaxWidth()
                    .height(150.dp)){
                    Text(text = "")
                }
                Divider() // inserisco una linea sotto l'header del ModalNavigationDrawer


                // Definisco la sezione "Home":
                NavigationDrawerItem(label = { Text(text = "Home", color = BlueApp)},
                    selected = false, // determina se un item è selezionato o meno,
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "home", tint = BlueApp)},

                    // Qui dentro definisco quello che accade quando l'utente cliccherà sul navigation item (quindi in questo caso sulla sezione/item "Home")
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close() // quando cliccherà sulla sezione "Home", il ModalNavigationDrawer (ovvero il menù sulla sinistra)
                            // si chiuderà automaticamente in modo tale da mostrare sullo schermo solo la schermata "Home" selezionata dall'utente
                        }


                        // SUPPONENDO "num_max_new_screens == 5":

                        // Passo alla schermata 'Home', Mantenendo però gli ultimi 5 screens:
                        val recentScreens = navigationManager.getRecentScreens() // mi prendo tutte le schermate sulle quali l'utente ha navigato

                        // La riga qui sotto cerca di ottenere la schermata più vecchia tra le ultime 5 schermate recenti.
                        // - Se ci sono meno di 5 schermate recenti, imposta il punto di pop-up alla schermata "Home".

                        // - recentScreens.getOrNull(recentScreens.size - 5):
                        //      Cerca di ottenere la schermata che è la quinta più vecchia nella lista.
                        //      Se la lista ha meno di 5 elementi, ritorna null e praticamente riparte dalla schermata corrente (in questo caso 'Home') e se si continua
                        //      ad andare indietro con il 'back' si potrà tornare alle schermate precedenti.

                        // - Altrimenti verranno se recentScreens.getOrNull(recentScreens.size - 5) non restituisce null allora vuol dire che
                        //   nello stack ci sono almeno 5 schermate e quindi quello che accadrà in questo caso sarà che partendo dalla schermata in cui si trova
                        //   in questo momento l'utente verranno cancellate le 4 schermate precedenti e quindi se l'utente cliccherà su 'back' tornerà alla
                        //   schermata selezinata 5 schermate precedenti.
                        val popUpToRoute = recentScreens.getOrNull(recentScreens.size - num_max_new_screens) ?: Screens.Home.screen
                        navigationController.navigate(Screens.Home.screen) {
                            // 'popUpTo(popUpToRoute)' specifica che tutte le schermate fino alla schermata identificata da 'popUpToRoute'
                            // devono essere rimosse dallo stack di navigazione.
                            popUpTo(popUpToRoute) {
                                inclusive = false // questo indica che la schermata 'popUpToRoute' stessa non deve essere rimossa.
                                // Se 'inclusive' fosse impostato a true, anche 'popUpToRoute' verrebbe rimossa.
                            }
                            navigationManager.addScreen(Screens.Home.screen)
                        }
                    })

                // Definisco la sezione "Friends":
                NavigationDrawerItem(label = { Text(text = "Friends", color = BlueApp)},
                    selected = false, // determina se un item è selezionato o meno,
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "friends", tint = BlueApp)},

                    // Qui dentro definisco quello che accade quando l'utente cliccherà sul navigation item (quindi in questo caso sulla sezione/item "Friends")
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close() // quando cliccherà sulla sezione "Friends", il ModalNavigationDrawer (ovvero il menù sulla sinistra)
                            // si chiuderà automaticamente in modo tale da mostrare sullo schermo solo la schermata "Friends" selezionata dall'utente
                        }


                        // Passo alla schermata 'Friends', Mantenendo però gli ultimi 5 screens:
                        val recentScreens = navigationManager.getRecentScreens() // mi prendo le ultime 5 schermate sulle quali l'utente ha navigato
                        val popUpToRoute = recentScreens.getOrNull(recentScreens.size - num_max_new_screens) ?: Screens.Friends.screen
                        navigationController.navigate(Screens.Friends.screen) {
                            // 'popUpTo(popUpToRoute)' specifica che tutte le schermate fino alla schermata identificata da 'popUpToRoute'
                            // devono essere rimosse dallo stack di navigazione.
                            popUpTo(popUpToRoute) {
                                inclusive = false // questo indica che la schermata 'popUpToRoute' stessa non deve essere rimossa.
                                // Se 'inclusive' fosse impostato a true, anche 'popUpToRoute' verrebbe rimossa.
                            }
                            navigationManager.addScreen(Screens.Friends.screen)
                        }
                        //Log.d("MusicDraftUI", "recentScreens.size: ${recentScreens.size}")
                    })

                // Definisco la sezione "Cards":
                NavigationDrawerItem(label = { Text(text = "Cards", color = BlueApp)},
                    selected = false, // determina se un item è selezionato o meno,
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "cards", tint = BlueApp)},

                    // Qui dentro definisco quello che accade quando l'utente cliccherà sul navigation item (quindi in questo caso sulla sezione/item "Cards")
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close() // quando cliccherà sulla sezione "Cards", il ModalNavigationDrawer (ovvero il menù sulla sinistra)
                            // si chiuderà automaticamente in modo tale da mostrare sullo schermo solo la schermata "Cards" selezionata dall'utente
                        }

                        // Passo alla schermata 'Cards', Mantenendo però gli ultimi 5 screens:
                        val recentScreens = navigationManager.getRecentScreens() // mi prendo le ultime 5 schermate sulle quali l'utente ha navigato
                        val popUpToRoute = recentScreens.getOrNull(recentScreens.size - num_max_new_screens) ?: Screens.Cards.screen
                        navigationController.navigate(Screens.Cards.screen) {
                            // 'popUpTo(popUpToRoute)' specifica che tutte le schermate fino alla schermata identificata da 'popUpToRoute'
                            // devono essere rimosse dallo stack di navigazione.
                            popUpTo(popUpToRoute) {
                                inclusive = false // questo indica che la schermata 'popUpToRoute' stessa non deve essere rimossa.
                                // Se 'inclusive' fosse impostato a true, anche 'popUpToRoute' verrebbe rimossa.
                            }
                            navigationManager.addScreen(Screens.Cards.screen)
                        }
                        //Log.d("MusicDraftUI", "recentScreens.size: ${recentScreens.size}")
                    })

                // Definisco la sezione "Decks":
                NavigationDrawerItem(label = { Text(text = "Decks", color = BlueApp)},
                    selected = false, // determina se un item è selezionato o meno,
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "decks", tint = BlueApp)},

                    // Qui dentro definisco quello che accade quando l'utente cliccherà sul navigation item (quindi in questo caso sulla sezione/item "Decks")
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close() // quando cliccherà sulla sezione "Decks", il ModalNavigationDrawer (ovvero il menù sulla sinistra)
                            // si chiuderà automaticamente in modo tale da mostrare sullo schermo solo la schermata "Decks" selezionata dall'utente
                        }

                        // Passo alla schermata 'Decks', Mantenendo però gli ultimi 5 screens:
                        val recentScreens = navigationManager.getRecentScreens() // mi prendo le ultime 5 schermate sulle quali l'utente ha navigato
                        val popUpToRoute = recentScreens.getOrNull(recentScreens.size - num_max_new_screens) ?: Screens.Decks.screen
                        navigationController.navigate(Screens.Decks.screen) {
                            // 'popUpTo(popUpToRoute)' specifica che tutte le schermate fino alla schermata identificata da 'popUpToRoute'
                            // devono essere rimosse dallo stack di navigazione.
                            popUpTo(popUpToRoute) {
                                inclusive = false // questo indica che la schermata 'popUpToRoute' stessa non deve essere rimossa.
                                // Se 'inclusive' fosse impostato a true, anche 'popUpToRoute' verrebbe rimossa.
                            }
                            navigationManager.addScreen(Screens.Decks.screen)
                        }
                        //Log.d("MusicDraftUI", "recentScreens.size: ${recentScreens.size}")
                    })


                // Definisco la sezione "Marketplace":
                NavigationDrawerItem(label = { Text(text = "Marketplace", color = BlueApp)},
                    selected = false, // determina se un item è selezionato o meno,
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "marketplace", tint = BlueApp)},

                    // Qui dentro definisco quello che accade quando l'utente cliccherà sul navigation item (quindi in questo caso sulla sezione/item "Marketplace")
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close() // quando cliccherà sulla sezione "Marketplace", il ModalNavigationDrawer (ovvero il menù sulla sinistra)
                            // si chiuderà automaticamente in modo tale da mostrare sullo schermo solo la schermata "Marketplace" selezionata dall'utente
                        }

                        // Passo alla schermata 'Marketplace', Mantenendo però gli ultimi 5 screens:
                        val recentScreens = navigationManager.getRecentScreens() // mi prendo le ultime 5 schermate sulle quali l'utente ha navigato
                        val popUpToRoute = recentScreens.getOrNull(recentScreens.size - num_max_new_screens) ?: Screens.Marketplace.screen
                        navigationController.navigate(Screens.Marketplace.screen) {
                            // 'popUpTo(popUpToRoute)' specifica che tutte le schermate fino alla schermata identificata da 'popUpToRoute'
                            // devono essere rimosse dallo stack di navigazione.
                            popUpTo(popUpToRoute) {
                                inclusive = false // questo indica che la schermata 'popUpToRoute' stessa non deve essere rimossa.
                                // Se 'inclusive' fosse impostato a true, anche 'popUpToRoute' verrebbe rimossa.
                            }
                            navigationManager.addScreen(Screens.Marketplace.screen)
                        }
                        //Log.d("MusicDraftUI", "recentScreens.size: ${recentScreens.size}")
                    })


                // Definisco la sezione "Matchmaking":
                NavigationDrawerItem(label = { Text(text = "Matchmaking", color = BlueApp)},
                    selected = false, // determina se un item è selezionato o meno,
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "matchmaking", tint = BlueApp)},

                    // Qui dentro definisco quello che accade quando l'utente cliccherà sul navigation item (quindi in questo caso sulla sezione/item "Matchmaking")
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close() // quando cliccherà sulla sezione "Matchmaking", il ModalNavigationDrawer (ovvero il menù sulla sinistra)
                            // si chiuderà automaticamente in modo tale da mostrare sullo schermo solo la schermata "Home" selezionata dall'utente
                        }
                        // Passo alla schermata 'Matchmaking', Mantenendo però gli ultimi 5 screens:
                        val recentScreens = navigationManager.getRecentScreens() // mi prendo le ultime 5 schermate sulle quali l'utente ha navigato
                        val popUpToRoute = recentScreens.getOrNull(recentScreens.size - num_max_new_screens) ?: Screens.Matchmaking.screen
                        navigationController.navigate(Screens.Matchmaking.screen) {
                            // 'popUpTo(popUpToRoute)' specifica che tutte le schermate fino alla schermata identificata da 'popUpToRoute'
                            // devono essere rimosse dallo stack di navigazione.
                            popUpTo(popUpToRoute) {
                                inclusive = false // questo indica che la schermata 'popUpToRoute' stessa non deve essere rimossa.
                                // Se 'inclusive' fosse impostato a true, anche 'popUpToRoute' verrebbe rimossa.
                            }
                            navigationManager.addScreen(Screens.Matchmaking.screen)
                        }
                        //Log.d("MusicDraftUI", "recentScreens.size: ${recentScreens.size}")
                    })


                // Definisco la sezione "Settings":
                NavigationDrawerItem(label = { Text(text = "Settings", color = BlueApp)},
                    selected = false, // determina se un item è selezionato o meno,
                    icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = "settings", tint = BlueApp)},

                    // Qui dentro definisco quello che accade quando l'utente cliccherà sul navigation item (quindi in questo caso sulla sezione/item "Settings")
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close() // quando cliccherà sulla sezione "Settings", il ModalNavigationDrawer (ovvero il menù sulla sinistra)
                            // si chiuderà automaticamente in modo tale da mostrare sullo schermo solo la schermata "Settings" selezionata dall'utente
                        }
                        // Passo alla schermata 'Settings', Mantenendo però gli ultimi 5 screens:
                        val recentScreens = navigationManager.getRecentScreens() // mi prendo le ultime 5 schermate sulle quali l'utente ha navigato
                        val popUpToRoute = recentScreens.getOrNull(recentScreens.size - num_max_new_screens) ?: Screens.Settings.screen
                        navigationController.navigate(Screens.Settings.screen) {
                            // 'popUpTo(popUpToRoute)' specifica che tutte le schermate fino alla schermata identificata da 'popUpToRoute'
                            // devono essere rimosse dallo stack di navigazione.
                            popUpTo(popUpToRoute) {
                                inclusive = false // questo indica che la schermata 'popUpToRoute' stessa non deve essere rimossa.
                                // Se 'inclusive' fosse impostato a true, anche 'popUpToRoute' verrebbe rimossa.
                            }
                            navigationManager.addScreen(Screens.Settings.screen)
                        }

                    })

                Spacer(modifier = Modifier.height(50.dp))

                // Definisco la sezione "Logout":
                NavigationDrawerItem(label = { Text(text = "", color = BlueApp)},
                    selected = false, // determina se un item è selezionato o meno,
                    icon = { Icon(imageVector = Icons.Default.Logout, contentDescription = "logout", tint = BlueApp)},
                    onClick = {
                        // eseguo il logout tramite l'utilizzo di Firebase:
                        loginViewModel.logoutFromFirebase(navControllerInitialScreens)
                    })
            }
        },
    ) {

        // Qui dentro inserisco il codice per creare la topBar.
        // Definisco prima uno Scaffold che funge da container e in questo caso conterrà
        // solo la topBar:
        Scaffold (
            topBar = {
                val coroutineScope = rememberCoroutineScope()
                TopAppBar(

                    // Qui dentro inserisco tutti i parametri che mi permetteranno di caratterizzare
                    // la TopAppBar nella quale sulla sinistra ci saranno le 3 linee che una volta cliccate
                    // mostreranno all'utente il menù in verticale sulla sinistra:

                    title = { Text(text = "MusicDraft") }, // titolo della TopAppBar
                    colors = TopAppBarDefaults.topAppBarColors(
                        // parametri di colors:
                        containerColor = BlueApp, // colore della TopAppBar:
                        titleContentColor = Color.White, // colore del titolo della TopAppBar
                        navigationIconContentColor = Color.White // colore dell'icona della TopAppBar
                    ),
                    navigationIcon = {
                        // Adesso definisco i parametri di "navigationIcon":
                        IconButton(onClick = {
                            coroutineScope.launch {
                                drawerState.open() // quando l'utente cliccherà sull'IconButton allora si aprirà il menù sulla sinistra
                            }
                        }) {
                            Icon(
                                // Specifico come dovrà essere l'icona del menù (le 3 linee):
                                Icons.Rounded.Menu, contentDescription = "MenuButton"
                            )
                        }
                    },
                )
            }
        ){
            // Qui definisco il NavHost che specificherà quali schermate dovranno aprirsi
            // in base alle sezioni sulle quali cliccherà l'utente.
            // - La Schermata iniziale sarà "Home"
            NavHost(navController = navigationController, startDestination = Screens.Home.screen){
                composable(Screens.Home.screen){
                    Home(loginViewModel, handleFriendsViewModel, matchmakingViewModel, cardsViewModel, NUM_POINTS_MIN) // composable che verrà aperto quando l'utente cliccherà sulla sezione "Home"
                }
                composable(Screens.Friends.screen){
//                    Friends(navigationController) // composable che verrà aperto quando l'utente cliccherà sulla sezione "Home"
                    Friends(navControllerInitialScreens, handleFriendsViewModel, loginViewModel, cardsViewModel, exchangeManagementCardsViewModel, decksViewModel)
                }
                composable(Screens.Cards.screen){
                    Cards(cardsViewModel) // composable che verrà aperto quando l'utente cliccherà sulla sezione "Cards"
                }
                composable(Screens.Decks.screen){
                    Decks(decksViewModel, loginViewModel, exchangeManagementCardsViewModel) // composable che verrà aperto quando l'utente cliccherà sulla sezione "Decks"
                }
                composable(Screens.Marketplace.screen){
                    Marketplace(marketplaceViewmodel) // composable che verrà aperto quando l'utente cliccherà sulla sezione "Marketplace"
                }
                composable(Screens.Matchmaking.screen){
                    Matchmaking(navControllerInitialScreens, matchmakingViewModel, decksViewModel, loginViewModel, NUM_POINTS_MIN) // composable che verrà aperto quando l'utente cliccherà sulla sezione "Matchmaking"
                }
                composable(Screens.Settings.screen){
                    Settings(navControllerInitialScreens, loginViewModel) // composable che verrà aperto quando l'utente cliccherà sulla sezione "Settings"
                }


            }
        }
    }
}

