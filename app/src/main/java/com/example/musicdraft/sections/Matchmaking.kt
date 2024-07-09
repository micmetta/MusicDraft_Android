package com.example.musicdraft.sections

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.Pending
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.musicdraft.data.tables.matchmaking.MatchSummaryConcluded
import com.example.musicdraft.data.tables.matchmaking.Matchmaking
import com.example.musicdraft.data.tables.user.User
import com.example.musicdraft.viewModel.DeckViewModel
import com.example.musicdraft.viewModel.LoginViewModel
import com.example.musicdraft.viewModel.MatchmakingViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter



/**
 * Composable che permette all'utente di poter visualizzare e gestire tutte le sezioni che fanno riferimento alla schermata Matchmaking
 * dell'applicazione.
 *
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Matchmaking(
    navController: NavController,
    matchmakingViewModel: MatchmakingViewModel,
    decksViewModel: DeckViewModel,
    loginViewModel: LoginViewModel,
    NUM_POINTS_MIN: Int
    ) {


    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tabItems = listOf(
        TabItem(
            title = "Search match",
            unselectedIcon = Icons.Outlined.Person,
            selectedIcon = Icons.Filled.Person
        ),
        TabItem(
            title = "Matches waiting",
            unselectedIcon = Icons.Outlined.PersonAdd,
            selectedIcon = Icons.Filled.PersonAdd
        ),
        TabItem(
            title = "Games concluded",
            unselectedIcon = Icons.Outlined.Pending,
            selectedIcon = Icons.Filled.Pending
        ),

    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val pagerState = rememberPagerState(pageCount = { tabItems.size })

//        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        // più lento perchè il cambiamento della sezione la fa solo dopo che è terminato lo scrolling orizzontale..
//        // ogni volta che il valore di "pagerState.current" cambierà,
//        // (e cambierà quando l'utente eseguirà lo scrolling)
//        // automaticamente verrà eseguita la coroutine qui sotto.
//        // - Anche se un pò più lento sono certo di non avere delay o errori durante lo scrolling orizzontale:
        LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress){
            if(!pagerState.isScrollInProgress){
                // eseguo la selezione dello screnn sul quale l'utente
                // è arrivato dopo aver fatto lo scrolling:
                selectedTabIndex = pagerState.currentPage
//                if (selectedTabIndex == 1) {
//                    infoUserCurrent?.let {
//                        handleFriendsViewModel.getRequestReceivedByUser(it.email)
//                    }
//                }
            }
        }
//        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////
        // Quando l'utente clicca su una sotto-sezione allora passo a quella schermata:
        LaunchedEffect(selectedTabIndex) {
            pagerState.animateScrollToPage(selectedTabIndex)
        }
        ////////////////////////////////////////////////////////////

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(60.dp))

            // 'ScrollableTabRow' a differenza di 'TabRow' mi permette di scrollare i 'TabItems'
            // in modo tale da non dover inserire per forza tutti tenendo conto dell'ampiezza massima dello schermo.
            ScrollableTabRow(selectedTabIndex = selectedTabIndex) {
                tabItems.forEachIndexed { index, item ->
                    Tab(
                        selected = index == selectedTabIndex,
                        onClick = {
                            selectedTabIndex = index
                        },
                        text = { Text(text = item.title) },
                        icon = {
                            Icon(
                                imageVector = if (index == selectedTabIndex) {
                                    item.selectedIcon
                                } else {
                                    item.unselectedIcon
                                },
                                contentDescription = item.title
                            )
                        }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                when (page) {
                    0 -> SearchMatch(navController, matchmakingViewModel, decksViewModel, loginViewModel, NUM_POINTS_MIN)
                    1 -> MatchesWaiting(navController, matchmakingViewModel, decksViewModel, loginViewModel)
                    2 -> GamesConcluded(navController, matchmakingViewModel, decksViewModel, loginViewModel, NUM_POINTS_MIN)
                }
            }
        }
    }
}

@Composable
fun SearchMatch(
    navController: NavController,
    matchmakingViewModel: MatchmakingViewModel,
    decksViewModel: DeckViewModel,
    loginViewModel: LoginViewModel,
    NUM_POINTS_MIN: Int // numero minimo di points richiesti per giocare un game
){

    decksViewModel.init()

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // finestre di dialogo:
    var showDialogSelectDeck = remember { mutableStateOf(false) }
    ////////////////////////////////////////////////////////////////////////////////////////////////


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        //horizontalAlignment = Alignment.CenterHorizontally,
        //verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "In this section you can start a game against a random player by clicking on the button below. \n",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Start
        )
        Text(
            text = "To start a game you will need to have at least ${NUM_POINTS_MIN} points available.\n",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = Color.Black)
        )
        Text(
            text = "If you want to challenge a friend, you can go to the section 'Friend' and after clicking on the 'Mates' screen you can click on the fiery icon.",
            style = MaterialTheme.typography.bodyLarge
        )
        Button(
            onClick = {
                /* Azione quando verrà cliccato il button */

                // 1) Scelta mazzo di gioco:
                navController.navigate("selectDeck")
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "search match")
        }
    }
}


@Composable
fun SelectDeck(
    navController: NavController,
    matchmakingViewModel: MatchmakingViewModel,
    decksViewModel: DeckViewModel,
    loginViewModel: LoginViewModel,
    NUM_POINTS_MIN: Int // numero minimo di points richiesti per giocare un game
){

//    decksViewModel.init()
    val decks = decksViewModel.mazzi
    var pop_deck: Float = 0F
    Log.d("SelectDeck","decks: ${decks}")

    val matches by matchmakingViewModel.matching.collectAsState(null)
    val infoUserCurrent by loginViewModel.userLoggedInfo.collectAsState(initial = null)
    val opponentMatch by loginViewModel.opponentMatch.collectAsState(initial = null)

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // finestre di dialogo:
    val newMatchInsert = remember { mutableStateOf(false) }
    val matchingFoundWinner = remember { mutableStateOf(false) }
    val matchingFoundLooser = remember { mutableStateOf(false) }
    val matchingFoundEquality = remember { mutableStateOf(false) }
    val notSufficientPoints = remember { mutableStateOf(false) }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    infoUserCurrent?.let {
        Log.d("SelectDeck","infoUserCurrent: ${infoUserCurrent}")
        matchmakingViewModel.getAllMatchesWaiting(infoUserCurrent!!.nickname) // aggiorna 'matches'
    }


    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        Text("My decks of cards:", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        if (decks.value!!.isEmpty()) {
            Text(
                "You don't have any decks yet.\n" +
                     "Create at least one in the 'Decks' section.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(decks.value!!) { deck ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            //.clickable { decksViewModel.startEditingDeck(deck) }
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(deck.id_mazzo, style = MaterialTheme.typography.bodyLarge)
                            deck.carte.forEach { card ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Image(
                                        painter = rememberImagePainter(data = card.immagine),
                                        contentDescription = null,
                                        modifier = Modifier.size(50.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("${card.nome_carta} (Popolarità: ${card.popolarita})")
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                Button(onClick = {

//                                    matchmakingViewModel.getAllMatchesWithARangeOfPop(infoUserCurrent!!.nickname, pop_deck) // aggiorna 'matches'
//
                                    // calcolo pop deck selezionato:
                                    for (card in deck.carte) {
                                        pop_deck += card.popolarita
                                    }
                                    pop_deck = pop_deck/5

                                    // controllo se il giocatore corrente ha almeno NUM_POINTS_MIN per poter giocare un game:
                                    if(infoUserCurrent!!.points >= NUM_POINTS_MIN){
//                                        // calcolo pop deck selezionato:
//                                        for (card in deck.carte) {
//                                            pop_deck += card.popolarita
//                                        }
                                        //pop_deck = pop_deck/5
//                                        matchmakingViewModel.getAllMatchesWithARangeOfPop(infoUserCurrent!!.nickname, pop_deck) // aggiorna 'matches'

                                        Log.d("SelectDeck","pop_deck: ${pop_deck}")
                                        Log.d("SelectDeck","matches: ${matches}")

                                        // 1) Controllo se ci sono altri utenti in attesa che il sistema può matchare con l'utente corrente (matches):
                                        if(!matches.isNullOrEmpty()){
                                            loginViewModel.getOpponentByNickname(matches!![0].nickname1) // aggiorna opponentMatch
                                            // vuol dire che ho trovato almeno un altro giocatore in attesa e quindi quello che faccio è questo:
//                                        loginViewModel.getOpponentByNickname(matches!![0].nickname1) // aggiorna opponentMatch
                                            Log.d("SelectDeck","nickname_opponent: ${matches!![0].nickname1}")

                                            //////////////////////////////////////////////////////////////
                                            // caricamento data odierna:

                                            // prendo la data corrente
                                            val currentDate = LocalDate.now()
                                            // definisco il formato
                                            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                                            // Formatto l'oggetto LocalDate come stringa
                                            val formattedDate = currentDate.format(formatter)
                                            // Stampa la data formattata
                                            Log.d("SelectDeck","formattedDate: ${formattedDate}")
                                            //////////////////////////////////////////////////////////////

                                            //////////////////////////////////////////////////////////////////
                                            // 1) seleziono l'utente in cerca di un'avversario che è in attesa da più tempo:
                                            val opponentMatching = matches!![0]

                                            // 2) Vedo chi tra l'utente corrente e il suo avversario ha vinto il game:
                                            if(pop_deck > opponentMatching.popularityDeckU1){
                                                // ha vinto l'utente corrente!
                                                // creo il nuovo oggetto di tipo "MatchSummaryConcluded":
                                                val matchSummaryConcluded =
                                                    infoUserCurrent?.let { it1 ->
                                                        MatchSummaryConcluded(
                                                            dataGame = formattedDate,
                                                            nickWinner = infoUserCurrent!!.nickname,
                                                            nickname1 = opponentMatching.nickname1,
                                                            nickname2 = it1.nickname,
                                                            nameDeckU1 = opponentMatching.nameDeckU1,
                                                            nameDeckU2 = deck.id_mazzo,
                                                            popularityDeckU1 = opponentMatching.popularityDeckU1,
                                                            popularityDeckU2 = pop_deck
                                                        )
                                                    }
                                                if (matchSummaryConcluded != null) {
                                                    Log.d("matchmaking", "entrato vittoria utente corrente")
                                                    Log.d("matchmaking", "opponentMatch: ${opponentMatch}")

                                                    // inserisco l'oggetto appena creato nella tabella "MatchSummaryConcluded":
                                                    matchmakingViewModel.insertNewSummaryMatch(matchSummaryConcluded)

                                                    // aggiornamento points:
                                                    loginViewModel.addPoints(NUM_POINTS_MIN, infoUserCurrent!!.email)
                                                    // non c'è bisogno di sottrarre points all'avversario perchè per mettersi in attesa gli erano
                                                    // stati già sottratti NUM_POINTS_MIN.

                                                    // elimino la partita appena conclusa:
                                                    matchmakingViewModel.deleteMatch(opponentMatching.id)
                                                    matchingFoundWinner.value = true
                                                }

                                            } else if (pop_deck < opponentMatching.popularityDeckU1){
                                                // ha vinto l'avversario!
                                                // creo il nuovo oggetto di tipo "MatchSummaryConcluded":
                                                val matchSummaryConcluded =
                                                    infoUserCurrent?.let { it1 ->
                                                        MatchSummaryConcluded(
                                                            dataGame = formattedDate,
                                                            nickWinner = opponentMatching.nickname1,
                                                            nickname1 = opponentMatching.nickname1,
                                                            nickname2 = it1.nickname,
                                                            nameDeckU1 = opponentMatching.nameDeckU1,
                                                            nameDeckU2 = deck.id_mazzo,
                                                            popularityDeckU1 = opponentMatching.popularityDeckU1,
                                                            popularityDeckU2 = pop_deck
                                                        )
                                                    }
                                                if (matchSummaryConcluded != null) {

                                                    Log.d("matchmaking", "entrato persa utente corrente")
                                                    Log.d("matchmaking", "opponentMatch: ${opponentMatch}")

                                                    // inserisco l'oggetto appena creato nella tabella "MatchSummaryConcluded":
                                                    matchmakingViewModel.insertNewSummaryMatch(matchSummaryConcluded)

                                                    // aggiornamento points:
                                                    loginViewModel.subtractPoints(NUM_POINTS_MIN, infoUserCurrent!!.email) // sottraggo points all'utente corrente
//                                                    loginViewModel.addPoints((2*NUM_POINTS_MIN), opponentMatch!!.email) // 2x perchè gli erano stati sotratti
//                                                    // NUM_POINTS_MIN per mettersi in coda
                                                    loginViewModel.addPointsNick((2*NUM_POINTS_MIN), matchSummaryConcluded.nickWinner) // 2x perchè gli erano stati sotratti
                                                    // NUM_POINTS_MIN per mettersi in coda

                                                    // elimino la partita appena conclusa:
                                                    matchmakingViewModel.deleteMatch(opponentMatching.id)
                                                    matchingFoundLooser.value = true
                                                }

                                            } else{
                                                // è pareggio!
                                                // creo il nuovo oggetto di tipo "MatchSummaryConcluded":
                                                val matchSummaryConcluded =
                                                    infoUserCurrent?.let { it1 ->
                                                        MatchSummaryConcluded(
                                                            dataGame = formattedDate,
                                                            nickWinner = "",
                                                            nickname1 = opponentMatching.nickname1,
                                                            nickname2 = it1.nickname,
                                                            nameDeckU1 = opponentMatching.nameDeckU1,
                                                            nameDeckU2 = deck.id_mazzo,
                                                            popularityDeckU1 = opponentMatching.popularityDeckU1,
                                                            popularityDeckU2 = pop_deck
                                                        )
                                                    }
                                                if (matchSummaryConcluded != null) {

                                                    Log.d("matchmaking", "pareggio")
                                                    Log.d("matchmaking", "opponentMatch: ${opponentMatch}")

                                                    // inserisco l'oggetto appena creato nella tabella "MatchSummaryConcluded":
                                                    matchmakingViewModel.insertNewSummaryMatch(matchSummaryConcluded)

                                                    // aggiornamento points:
                                                    loginViewModel.addPoints(NUM_POINTS_MIN, infoUserCurrent!!.email)
//                                                    loginViewModel.addPoints((2*NUM_POINTS_MIN), opponentMatch!!.email) // 2x perchè gli erano stati sotratti
//                                                    // NUM_POINTS_MIN per mettersi in coda
                                                    loginViewModel.addPointsNick((2*NUM_POINTS_MIN), matchSummaryConcluded.nickname1) // 2x perchè gli erano stati sotratti
                                                    // NUM_POINTS_MIN per mettersi in coda

                                                    // elimino la partita appena conclusa:
                                                    matchmakingViewModel.deleteMatch(opponentMatching.id)
                                                    matchingFoundEquality.value = true
                                                }
                                            }
                                            //////////////////////////////////////////////////////////////////

                                        }else{
                                            // 2) Se 1) non va a buon fine allora creo una nuova istanza della tabella 'Matchmaking':
                                            val matchmaking = Matchmaking(
                                                nickname1 = infoUserCurrent!!.nickname,
                                                nickname2 = "",
                                                nameDeckU1 = deck.id_mazzo,
                                                nameDeckU2 = "",
                                                popularityDeckU1 = pop_deck,
                                                popularityDeckU2 = 0f
                                            )

                                            // sottraggo NUM_POINTS_MIN all'utente corrente per mettersi in coda:
                                            loginViewModel.subtractPoints(NUM_POINTS_MIN, infoUserCurrent!!.email)

                                            matchmakingViewModel.insertNewMatch(matchmaking)
                                            newMatchInsert.value = true
                                        }
                                    }else{
                                        notSufficientPoints.value = true
                                    }


                                }) {
                                    Text("Select")
                                }
                            }
                        }
                    }
                }
            }

            if(notSufficientPoints.value){
                AlertDialog(
                    onDismissRequest = { notSufficientPoints.value = false },
                    title = { Text(text = "Operation not executable") },
                    text = { Text(text =  "You don't have enough points to play a game..\n" +
                                          "(${NUM_POINTS_MIN} points required)") },
                    confirmButton = {
                        TextButton(
                            onClick = { notSufficientPoints.value = false }
                        ) {
                            Text("OK")
                        }
                    }
                )
            }

            if (newMatchInsert.value) {
                AlertDialog(
                    onDismissRequest = { newMatchInsert.value = false },
                    title = { Text(text = "Operation performed successfully") },
                    text = { Text(text = "You have been successfully entered into the database!\n" +
                            "As soon as an opponent is found the match will begin.\n\n" +
                            "You can go to the 'Matches waiting' section to manage pending matches.") },
                    confirmButton = {
                        TextButton(
                            onClick = { newMatchInsert.value = false }
                        ) {
                            Text("OK")
                        }
                    }
                )
            }

            if (matchingFoundWinner.value) {
                AlertDialog(
                    onDismissRequest = { matchingFoundWinner.value = false },
                    title = { Text(text = "Opponent found!") },
                    text = {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("Congratulations you won the game!")
                                }
                                append("\n\nTo see the info for this match you can go to the 'Games concluded' section")
                            }
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = { matchingFoundWinner.value = false }
                        ) {
                            Text("OK")
                        }
                    }
                )
            }

            if (matchingFoundLooser.value) {
                AlertDialog(
                    onDismissRequest = { matchingFoundLooser.value = false },
                    title = { Text(text = "Opponent found!") },
                    text = {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("Unfortunately you lost this match!")
                                }
                                append("\n\nTo see the info for this match you can go to the 'Games concluded' section")
                            }
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = { matchingFoundLooser.value = false }
                        ) {
                            Text("OK")
                        }
                    }
                )
            }

            if (matchingFoundEquality.value) {
                AlertDialog(
                    onDismissRequest = { matchingFoundEquality.value = false },
                    title = { Text(text = "Opponent found!") },
                    text = {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("The match ended in a draw!")
                                }
                                append("\n\nTo see the info for this match you can go to the 'Games concluded' section")
                            }
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = { matchingFoundEquality.value = false }
                        ) {
                            Text("OK")
                        }
                    }
                )
            }


        }
    }
}



@Composable
fun MatchesWaiting(
    navController: NavController,
    matchmakingViewModel: MatchmakingViewModel,
    decksViewModel: DeckViewModel,
    loginViewModel: LoginViewModel){

    // Ottieni le informazioni sull'utente loggato
    val infoUserCurrent by loginViewModel.userLoggedInfo.collectAsState(initial = null)

    // Ottieni le partite concluse dall'utente corrente
    val matchesWait by matchmakingViewModel.matchesWait.collectAsState(null)


    // Carica le partite in attesa dell'utente corrente
    infoUserCurrent?.let {
        matchmakingViewModel.getAllMatchesWaitingByNickname(it.nickname)
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Text("Games waiting to find an opponent:", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        if (matchesWait.isNullOrEmpty()) {
            Text(
                "You are not waiting in any match.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(matchesWait!!) { match ->
                    GameCardWait(match, matchmakingViewModel)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

}

@Composable
fun GameCardWait(match: Matchmaking, matchmakingViewModel: MatchmakingViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // "name deck" in nero
                Text(
                    "name deck: ",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = Color.Black)
                )
                // Spazio per separare "name deck" da match.nameDeckU1
                Spacer(modifier = Modifier.width(8.dp))
                // match.nameDeckU1
                Text(
                    match.nameDeckU1,
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF2196F3))
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "popularity deck: ",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = Color.Black)
                )
                // Spazio per separare "popularity deck" da match.popularityDeckU1
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "${match.popularityDeckU1}",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF2196F3))
                )
            }

            // Pulsante "Delete" allineato a destra, al centro della card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        // Azione che verrà eseguita quando viene premuto il pulsante Delete
                        matchmakingViewModel.deleteMatch(match.id)
                    },
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Text("Delete")
                }
            }
        }
    }
}


@Composable
private fun GameCard(
    match: MatchSummaryConcluded,
    matchmakingViewModel: MatchmakingViewModel,
    infoUserCurrent: User?,
    NUM_POINTS_MIN: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                match.dataGame,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = Color.DarkGray)
            )

            if (infoUserCurrent != null) {
                if(match.nickWinner != infoUserCurrent.nickname){
                    Text(
                        "LOST GAME (-${NUM_POINTS_MIN} POINTS)",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color =  Color(0xFFC62828)) //dard red
                    )
                }else if(match.nickWinner == infoUserCurrent.nickname){
                    Text(
                        "GAME WON (+${NUM_POINTS_MIN} POINTS)",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFF388E3C)) //dard green
                    )
                }else{
                    Text(
                        "GAME DRAWN (+${NUM_POINTS_MIN} POINTS)",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1976D2)) //dard blue
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            if(match.nickWinner != ""){
                Text(
                    "Winner: ${match.nickWinner}",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF388E3C)) //dard green
                )

                if (match.nickWinner == match.nickname1) {
                    PlayerInfo("Player 1", match.nickname1, match.nameDeckU1, match.popularityDeckU1)
                    Text(
                        "Looser: ${match.nickname2}",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFFC62828)) //dard red
                    )
                    PlayerInfo("Player 2", match.nickname2, match.nameDeckU2, match.popularityDeckU2)
                } else{
                    PlayerInfo("Player 2", match.nickname2, match.nameDeckU2, match.popularityDeckU2)
                    Text(
                        "Looser: ${match.nickname1}",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFFC62828)) //dard red
                    )
                    PlayerInfo("Player 1", match.nickname1, match.nameDeckU1, match.popularityDeckU1)
                }
            }else{
                Text(
                    "Match drawn:",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF1976D2)) //dard blue
                )
                PlayerInfo("Player 1", match.nickname1, match.nameDeckU1, match.popularityDeckU1)
                PlayerInfo("Player 2", match.nickname2, match.nameDeckU2, match.popularityDeckU2)
            }

//            // Pulsante "Delete" allineato a destra, al centro della card
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.End,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Button(
//                    onClick = {
//                        // Azione da eseguire quando viene premuto il pulsante Delete
//                        matchmakingViewModel.deleteSummaryMatch(match.id)
//                    },
//                    modifier = Modifier.padding(end = 16.dp)
//                ) {
//                    Text("Delete")
//                }
//            }
        }
    }
}



@Composable
fun GamesConcluded(
    navController: NavController,
    matchmakingViewModel: MatchmakingViewModel,
    decksViewModel: DeckViewModel,
    loginViewModel: LoginViewModel,
    NUM_POINTS_MIN: Int
) {
    // Ottieni le informazioni sull'utente loggato
    val infoUserCurrent by loginViewModel.userLoggedInfo.collectAsState(initial = null)

    // Ottieni le partite concluse dall'utente corrente
    val matchesConcludedByCurrentUser by matchmakingViewModel.matchesConcludedByCurrentUser.collectAsState(null)

    // Carica le partite concluse dell'utente corrente
    infoUserCurrent?.let {
        if (matchesConcludedByCurrentUser == null) {
            matchmakingViewModel.getAllGamesConludedByNickname(it.nickname)
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text("Games Finished", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        if (matchesConcludedByCurrentUser.isNullOrEmpty()) {
            Text(
                "You haven't played any games yet.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(matchesConcludedByCurrentUser!!) { match ->
                    GameCard(match, matchmakingViewModel, infoUserCurrent, NUM_POINTS_MIN)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}


@Composable
private fun PlayerInfo(playerLabel: String, nickname: String, deckName: String, deckPopularity: Float) {
    Column(
        modifier = Modifier.padding(start = 16.dp)
    ) {
        Text("$playerLabel: $nickname", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = Color.Black))
        Text("Deck: $deckName", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = Color.Black))
        Text("Popularity: $deckPopularity", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = Color.Black))
    }
}
