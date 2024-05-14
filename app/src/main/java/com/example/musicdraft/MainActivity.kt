package com.example.musicdraft

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.musicdraft.sections.Home
import com.example.musicdraft.sections.Friends
import com.example.musicdraft.sections.Cards
import com.example.musicdraft.sections.Decks
import com.example.musicdraft.sections.Marketplace
import com.example.musicdraft.sections.Matchmaking
import com.example.musicdraft.sections.Settings
import com.example.musicdraft.sections.Screens
import com.example.musicdraft.ui.theme.GreenJC
import com.example.musicdraft.ui.theme.MusicDraftTheme
import kotlinx.coroutines.launch

// test commit
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicDraftTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ){
                    MusicDraftUI()
                }
            }
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicDraftUI(){
    val navigationController = rememberNavController() // inizializzazione del nav controller
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val context = LocalContext.current.applicationContext


    // Il "ModalNavigationDrawer" sarà proprio il menù laterale sulla sinistra in verticale tramite il quale
    // l'utente potrà navigare all'interno delle diverse sezioni dell'app:
    ModalNavigationDrawer(
        // Nelle '()' Definisco tutti i paremetri del "ModalNavigationDrawer":

        drawerState = drawerState, // mi permette di gestire la chiusura e l'apertura del menù in verticale sulla sinistra che mostrerà tutte
        // le sezioni sulle quali l'utente potrà cliccare.
        gesturesEnabled = true, // permette di aprire automaticamente lo swap il navigation draw
        drawerContent = {
            ModalDrawerSheet {
                Box(modifier = Modifier
                    .background(GreenJC) // Imposta come colore di sfondo il GreenJC
                    .fillMaxWidth()
                    .height(150.dp)){
                    Text(text = "")
                }
                Divider() // inserisco una linea sotto l'header del ModalNavigationDrawer

                // Definisco la sezione "Home":
                NavigationDrawerItem(label = { Text(text = "Home", color = GreenJC)},
                    selected = false, // determina se un item è selezionato o meno,
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "home", tint = GreenJC)},

                    // Qui dentro definisco quello che accade quando l'utente cliccherà sul navigation item (quindi in questo caso sulla sezione/item "Home")
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close() // quando cliccherà sulla sezione "Home", il ModalNavigationDrawer (ovvero il menù sulla sinistra)
                            // si chiuderà automaticamente in modo tale da mostrare sullo schermo solo la schermata "Home" selezionata dall'utente
                        }

                        // vado alla schermata "Home" che ho definito in "Screen.kt"
                        navigationController.navigate(Screens.Home.screen){
                            popUpTo(0) // in questo modo nello stack non mantengo memorizzato le sezioni precedenti
                            // nelle quali l'utente è andato precedentemente. Quindi qualora l'utente dopo aver cliccato sulla sezione "Home",
                            // cliccasse su "back" uscirà direttamente dall'app!
                        }
                    })

                // Definisco la sezione "Friends":
                NavigationDrawerItem(label = { Text(text = "Friends", color = GreenJC)},
                    selected = false, // determina se un item è selezionato o meno,
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "friends", tint = GreenJC)},

                    // Qui dentro definisco quello che accade quando l'utente cliccherà sul navigation item (quindi in questo caso sulla sezione/item "Friends")
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close() // quando cliccherà sulla sezione "Friends", il ModalNavigationDrawer (ovvero il menù sulla sinistra)
                            // si chiuderà automaticamente in modo tale da mostrare sullo schermo solo la schermata "Friends" selezionata dall'utente
                        }

                        // vado alla schermata "Friends" che ho definito in "Screen.kt"
                        navigationController.navigate(Screens.Friends.screen){
                            popUpTo(0) // in questo modo nello stack non mantengo memorizzato le sezioni precedenti
                            // nelle quali l'utente è andato precedentemente. Quindi qualora l'utente dopo aver cliccato sulla sezione "Friends",
                            // cliccasse su "back" uscirà direttamente dall'app!
                        }
                    })

                // Definisco la sezione "Cards":
                NavigationDrawerItem(label = { Text(text = "Cards", color = GreenJC)},
                    selected = false, // determina se un item è selezionato o meno,
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "cards", tint = GreenJC)},

                    // Qui dentro definisco quello che accade quando l'utente cliccherà sul navigation item (quindi in questo caso sulla sezione/item "Cards")
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close() // quando cliccherà sulla sezione "Cards", il ModalNavigationDrawer (ovvero il menù sulla sinistra)
                            // si chiuderà automaticamente in modo tale da mostrare sullo schermo solo la schermata "Cards" selezionata dall'utente
                        }

                        // vado alla schermata "Cards" che ho definito in "Screen.kt"
                        navigationController.navigate(Screens.Cards.screen){
                            popUpTo(0) // in questo modo nello stack non mantengo memorizzato le sezioni precedenti
                            // nelle quali l'utente è andato precedentemente. Quindi qualora l'utente dopo aver cliccato sulla sezione "Cards",
                            // cliccasse su "back" uscirà direttamente dall'app!
                        }
                    })

                // Definisco la sezione "Decks":
                NavigationDrawerItem(label = { Text(text = "Decks", color = GreenJC)},
                    selected = false, // determina se un item è selezionato o meno,
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "decks", tint = GreenJC)},

                    // Qui dentro definisco quello che accade quando l'utente cliccherà sul navigation item (quindi in questo caso sulla sezione/item "Decks")
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close() // quando cliccherà sulla sezione "Decks", il ModalNavigationDrawer (ovvero il menù sulla sinistra)
                            // si chiuderà automaticamente in modo tale da mostrare sullo schermo solo la schermata "Decks" selezionata dall'utente
                        }

                        // vado alla schermata "Decks" che ho definito in "Screen.kt"
                        navigationController.navigate(Screens.Decks.screen){
                            popUpTo(0) // in questo modo nello stack non mantengo memorizzato le sezioni precedenti
                            // nelle quali l'utente è andato precedentemente. Quindi qualora l'utente dopo aver cliccato sulla sezione "Decks",
                            // cliccasse su "back" uscirà direttamente dall'app!
                        }
                    })


                // Definisco la sezione "Marketplace":
                NavigationDrawerItem(label = { Text(text = "Marketplace", color = GreenJC)},
                    selected = false, // determina se un item è selezionato o meno,
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "marketplace", tint = GreenJC)},

                    // Qui dentro definisco quello che accade quando l'utente cliccherà sul navigation item (quindi in questo caso sulla sezione/item "Marketplace")
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close() // quando cliccherà sulla sezione "Marketplace", il ModalNavigationDrawer (ovvero il menù sulla sinistra)
                            // si chiuderà automaticamente in modo tale da mostrare sullo schermo solo la schermata "Marketplace" selezionata dall'utente
                        }

                        // vado alla schermata "Marketplace" che ho definito in "Screen.kt"
                        navigationController.navigate(Screens.Marketplace.screen){
                            popUpTo(0) // in questo modo nello stack non mantengo memorizzato le sezioni precedenti
                            // nelle quali l'utente è andato precedentemente. Quindi qualora l'utente dopo aver cliccato sulla sezione "Marketplace",
                            // cliccasse su "back" uscirà direttamente dall'app!
                        }
                    })


                // Definisco la sezione "Matchmaking":
                NavigationDrawerItem(label = { Text(text = "Matchmaking", color = GreenJC)},
                    selected = false, // determina se un item è selezionato o meno,
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "matchmaking", tint = GreenJC)},

                    // Qui dentro definisco quello che accade quando l'utente cliccherà sul navigation item (quindi in questo caso sulla sezione/item "Matchmaking")
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close() // quando cliccherà sulla sezione "Matchmaking", il ModalNavigationDrawer (ovvero il menù sulla sinistra)
                            // si chiuderà automaticamente in modo tale da mostrare sullo schermo solo la schermata "Home" selezionata dall'utente
                        }

                        // vado alla schermata "Matchmaking" che ho definito in "Screen.kt"
                        navigationController.navigate(Screens.Matchmaking.screen){
                            popUpTo(0) // in questo modo nello stack non mantengo memorizzato le sezioni precedenti
                            // nelle quali l'utente è andato precedentemente. Quindi qualora l'utente dopo aver cliccato sulla sezione "Matchmaking",
                            // cliccasse su "back" uscirà direttamente dall'app!
                        }
                    })


                // Definisco la sezione "Settings":
                NavigationDrawerItem(label = { Text(text = "Settings", color = GreenJC)},
                    selected = false, // determina se un item è selezionato o meno,
                    icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = "settings", tint = GreenJC)},

                    // Qui dentro definisco quello che accade quando l'utente cliccherà sul navigation item (quindi in questo caso sulla sezione/item "Settings")
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close() // quando cliccherà sulla sezione "Settings", il ModalNavigationDrawer (ovvero il menù sulla sinistra)
                            // si chiuderà automaticamente in modo tale da mostrare sullo schermo solo la schermata "Settings" selezionata dall'utente
                        }

                        // vado alla schermata "Settings" che ho definito in "Screen.kt"
                        navigationController.navigate(Screens.Settings.screen){
                            popUpTo(0) // in questo modo nello stack non mantengo memorizzato le sezioni precedenti
                            // nelle quali l'utente è andato precedentemente. Quindi qualora l'utente dopo aver cliccato sulla sezione "Settings",
                            // cliccasse su "back" uscirà direttamente dall'app!
                        }
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
                              containerColor = GreenJC, // colore della TopAppBar:
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
            // - Schermata iniziale sarà "Home"
            NavHost(navController = navigationController, startDestination = Screens.Home.screen){
                composable(Screens.Home.screen){
                    Home() // composable che verrà aperto quando l'utente cliccherà sulla sezione "Home"
                }
                composable(Screens.Friends.screen){
                    Friends() // composable che verrà aperto quando l'utente cliccherà sulla sezione "Home"
                }
                composable(Screens.Cards.screen){
                    Cards() // composable che verrà aperto quando l'utente cliccherà sulla sezione "Home"
                }
                composable(Screens.Decks.screen){
                    Decks() // composable che verrà aperto quando l'utente cliccherà sulla sezione "Home"
                }
                composable(Screens.Marketplace.screen){
                    Marketplace() // composable che verrà aperto quando l'utente cliccherà sulla sezione "Home"
                }
                composable(Screens.Matchmaking.screen){
                    Matchmaking() // composable che verrà aperto quando l'utente cliccherà sulla sezione "Home"
                }
                composable(Screens.Settings.screen){
                    Settings() // composable che verrà aperto quando l'utente cliccherà sulla sezione "Settings"
                }

            }
        }
    }
}