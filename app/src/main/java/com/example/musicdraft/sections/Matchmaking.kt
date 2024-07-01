package com.example.musicdraft.sections

import DeckViewModel
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import com.example.musicdraft.viewModel.LoginViewModel
import com.example.musicdraft.viewModel.MatchmakingViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Matchmaking(
    navController: NavController,
    matchmakingViewModel: MatchmakingViewModel,
    decksViewModel: DeckViewModel,
    loginViewModel: LoginViewModel
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
                    0 -> SearchMatch(navController, matchmakingViewModel, decksViewModel, loginViewModel)
//                    1 -> RequestReceivedList(handleFriendsViewModel, reqReceivedCurrentUser = reqReceivedCurrentUser, infoUserCurrent)
//                    2 -> RequestSent(handleFriendsViewModel, loginViewModel, usersFilter, usersFilterbyNickname, infoUserCurrent, reqSentFromCurrentUser = reqSentFromCurrentUser, allUsersFriendsOfCurrentUser)
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
    loginViewModel: LoginViewModel
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
            text = "In this section you can start a game against a random player by clicking on the button below. \n\n" +
                    "If you want to challenge a friend, you can go to the section 'Friend' and after clicking on the 'Mates' screen you can click on the fiery icon.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Start
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


//    if(showDialogSelectDeck.value){
//        SelectDeck(navController, matchmakingViewModel, decksViewModel, loginViewModel)
//    }
}

@Composable
fun SelectDeck(
    navController: NavController,
    matchmakingViewModel: MatchmakingViewModel,
    decksViewModel: DeckViewModel,
    loginViewModel: LoginViewModel
){

//    decksViewModel.init()
    val decks = decksViewModel.mazzi
    var pop_deck: Float = 0F
    Log.d("SelectDeck","decks: ${decks}")

    val matches by matchmakingViewModel.matching.collectAsState(null)
    val infoUserCurrent by loginViewModel.userLoggedInfo.collectAsState(initial = null)
    val opponentMatch by loginViewModel.opponentMatch.collectAsState(initial = null)
    //val deckById by decksViewModel.deckByid.collectAsState(initial = null)
    val associateCards by decksViewModel.associateCards.collectAsState(initial = null)

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // finestre di dialogo:
    var newMatchInsert = remember { mutableStateOf(false) }
    var matchingFoundWinner = remember { mutableStateOf(false) }
    var matchingFoundLooser = remember { mutableStateOf(false) }
    var matchingFoundEquality = remember { mutableStateOf(false) }
    ////////////////////////////////////////////////////////////////////////////////////////////////


    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        Text("I miei mazzi di Carte", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        if (decks.isEmpty()) {
            Text(
                "Non hai ancora creato nessun mazzo",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(decks!!) { deck ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { decksViewModel.startEditingDeck(deck) }
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

                                    // aggiorno pop media deck corrente:
                                    pop_deck += card.popolarita
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                //////////////////////////////////////////////////////////////////////////////////////////////////
                                // invocazione 'getAllMatchesWithARangeOfPop' e aggiornamento pop deck selezionato:
                                matchmakingViewModel.getAllMatchesWithARangeOfPop(pop_deck) // aggiorna 'matches'
                                pop_deck = pop_deck/5
                                Log.d("SelectDeck","pop_deck: ${pop_deck}")
                                //////////////////////////////////////////////////////////////////////////////////////////////////

                                Button(onClick = {

                                    //matchmakingViewModel.getAllMatchesWithARangeOfPop(pop_deck) // aggiorna 'matches'

                                    // 2) Controllo se ci sono altri utenti in attesa che il sistema può matchare con l'utente corrente:
//                                    matches.let {

                                    Log.d("SelectDeck","matches: ${matches}")
                                    if(!matches.isNullOrEmpty()){
                                            // vuol dire che ho trovato almeno un altro giocatore in attesa e quindi quello che faccio è questo:
                                            loginViewModel.getOpponentByNickname(matches!![0].nickname1) // aggiorna opponentMatch


                                            // 1) seleziono l'utente in cerca di un'avversario che è in attesa da più tempo:
                                            val opponentMatching = matches!![0]
                                            val associatedcardsdeckU1: MutableList<String> = mutableListOf()
                                            val associatedcardsdeckU2: MutableList<String> = mutableListOf()

                                            //////////////////////////////////////////////////////////////////
                                            // aggiungo tutti gli id delle carte del mazzo dell'avversario
                                            // alla lista 'associatedcardsdeckU1:
                                            opponentMatch?.let { it1 ->
                                                decksViewModel.getCarteAss(it1.nickname, opponentMatching.nameDeckU1) // aggiorna 'associateCards'
                                            }
                                            //////////////////////////////////////////////////////////////////

                                            //////////////////////////////////////////////////////////////////
                                            // aggiungo tutti gli id delle carte del mazzo dell'utente corrente
                                            // alla lista 'associatedcardsdeckU2:
                                            deck.carte.forEach { card ->
                                                associatedcardsdeckU2.add(card.id_carta)
                                            }
                                            //////////////////////////////////////////////////////////////////

                                            //////////////////////////////////////////////////////////////////
                                            // aggiungo tutti gli id delle carte del mazzo dell'avversario alla lista
                                            // 'associatedcardsdeckU1'
                                            associateCards?.forEach { idCard ->
                                                associatedcardsdeckU1.add(idCard)
                                            }
                                            //////////////////////////////////////////////////////////////////

                                            // 2) Vedo chi tra l'utente corrente e il suo avversario ha vinto il game:
                                            if(pop_deck > opponentMatching.popularityDeckU1){
                                                // ha vinto l'utente corrente!
                                                // creo il nuovo oggetto di tipo "MatchSummaryConcluded":
                                                val matchSummaryConcluded =
                                                    infoUserCurrent?.let { it1 ->
                                                        MatchSummaryConcluded(
                                                            nickWinner = infoUserCurrent!!.nickname,
                                                            nickname1 = opponentMatching.nickname1,
                                                            nickname2 = it1.nickname,
                                                            nameDeckU1 = opponentMatching.nameDeckU1,
                                                            nameDeckU2 = deck.id_mazzo,
                                                            associatedcardsDeckU1 = associatedcardsdeckU1,
                                                            associatedcardsDeckU2 = associatedcardsdeckU2,
                                                            popularityDeckU1 = opponentMatching.popularityDeckU1,
                                                            popularityDeckU2 = pop_deck
                                                        )
                                                    }
                                                if (matchSummaryConcluded != null) {
                                                    // inserisco l'oggetto appena creato nella tabella "MatchSummaryConcluded":
                                                    matchmakingViewModel.insertNewSummaryMatch(matchSummaryConcluded)

                                                    // aggiornamento points:

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
                                                            nickWinner = opponentMatching.nickname1,
                                                            nickname1 = opponentMatching.nickname1,
                                                            nickname2 = it1.nickname,
                                                            nameDeckU1 = opponentMatching.nameDeckU1,
                                                            nameDeckU2 = deck.id_mazzo,
                                                            associatedcardsDeckU1 = associatedcardsdeckU1,
                                                            associatedcardsDeckU2 = associatedcardsdeckU2,
                                                            popularityDeckU1 = opponentMatching.popularityDeckU1,
                                                            popularityDeckU2 = pop_deck
                                                        )
                                                    }
                                                if (matchSummaryConcluded != null) {
                                                    // inserisco l'oggetto appena creato nella tabella "MatchSummaryConcluded":
                                                    matchmakingViewModel.insertNewSummaryMatch(matchSummaryConcluded)

                                                    // aggiornamento points:

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
                                                            nickWinner = "",
                                                            nickname1 = opponentMatching.nickname1,
                                                            nickname2 = it1.nickname,
                                                            nameDeckU1 = opponentMatching.nameDeckU1,
                                                            nameDeckU2 = deck.id_mazzo,
                                                            associatedcardsDeckU1 = associatedcardsdeckU1,
                                                            associatedcardsDeckU2 = associatedcardsdeckU2,
                                                            popularityDeckU1 = opponentMatching.popularityDeckU1,
                                                            popularityDeckU2 = pop_deck
                                                        )
                                                    }
                                                if (matchSummaryConcluded != null) {
                                                    // inserisco l'oggetto appena creato nella tabella "MatchSummaryConcluded":
                                                    matchmakingViewModel.insertNewSummaryMatch(matchSummaryConcluded)

                                                    // aggiornamento points:

                                                    // elimino la partita appena conclusa:
                                                    matchmakingViewModel.deleteMatch(opponentMatching.id)
                                                    matchingFoundEquality.value = true
                                                }
                                            }
                                        }else{
                                            // 3) Se 2) non va a buon fine allora creo una nuova istanza della tabella 'Matchmaking':
                                            val matchmaking = Matchmaking(
                                                nickname1 = infoUserCurrent!!.nickname,
                                                nickname2 = "",
                                                nameDeckU1 = deck.id_mazzo,
                                                nameDeckU2 = "",
                                                popularityDeckU1 = pop_deck,
                                                popularityDeckU2 = 0f
                                            )
                                            matchmakingViewModel.insertNewMatch(matchmaking)
                                            newMatchInsert.value = true
                                        }

//                                    }
                                }) {
                                    Text("Select")
                                }
                            }
                        }
                    }
                }
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