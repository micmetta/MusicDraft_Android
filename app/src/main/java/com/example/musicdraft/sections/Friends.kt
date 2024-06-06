package com.example.musicdraft.sections
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.musicdraft.data.tables.handleFriends.HandleFriends
import com.example.musicdraft.data.tables.user.User
import com.example.musicdraft.viewModel.HandleFriendsViewModel
import com.example.musicdraft.viewModel.LoginViewModel


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Friends(handleFriendsViewModel: HandleFriendsViewModel, loginViewModel: LoginViewModel) {

    val usersFilter by handleFriendsViewModel.usersFilter.collectAsState()
    //val reqReceivedCurrentUser by handleFriendsViewModel.reqReceivedCurrentUser.collectAsState(null) c'era prima.. Problema
    val reqReceivedCurrentUser by handleFriendsViewModel.reqReceivedCurrentUser.collectAsState(null)
    val infoUserCurrent by loginViewModel.userLoggedInfo.collectAsState(initial = null)


    val tabItems = listOf(
        TabItem(
            title = "Mates",
            unselectedIcon = Icons.Outlined.Person,
            selectedIcon = Icons.Filled.Person
        ),
        TabItem(
            title = "Request received",
            unselectedIcon = Icons.Outlined.PersonAdd,
            selectedIcon = Icons.Filled.PersonAdd
        ),
        TabItem(
            title = "Request sent",
            unselectedIcon = Icons.Outlined.Pending,
            selectedIcon = Icons.Filled.Pending
        )

    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){

        // stato per gestire la sezione superiore sulla quale l'utente clicca
        var selectedTabIndex by remember {
            mutableIntStateOf(0)
        }

        // stato per gestire la sezione di arrivo dopo che l'utente
        // ha eseguito lo scrolling
        val pagerState = rememberPagerState {
            tabItems.size
        }

        var showDialog by remember { mutableStateOf(false) }
        var selectedUser by remember { mutableStateOf<User?>(null) }
        var showConfirmationDialog by remember { mutableStateOf(false) }

        //////////////////////////////////////////////////////////////////
        // ogni volta che il valore di "selectedTabIndex" cambierà,
        // (e cambierà quando l'utente cliccherà su una certa sezione in alto)
        // automaticamente verrà eseguita la coroutine qui sotto:
        LaunchedEffect(selectedTabIndex){
            // eseguo lo scrolling alla pagina selezionata dall'utente
            // nella barra superiore
            pagerState.animateScrollToPage(selectedTabIndex)
        }

        // più lento perchè il cambiamento della sezione la fa solo dopo che è terminato lo scrolling orizzontale..
//        // ogni volta che il valore di "pagerState.current" cambierà,
//        // (e cambierà quando l'utente eseguirà lo scrolling)
//        // automaticamente verrà eseguita la coroutine qui sotto:
//        LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress){
//            if(!pagerState.isScrollInProgress){
//                // eseguo la selezione dello screnn sul quale l'utente
//                // è arrivato dopo aver fatto lo scrolling:
//                selectedTabIndex = pagerState.currentPage
//            }
//        }
//        //////////////////////////////////////////////////////////////////

        // più veloce..
        // ogni volta che il valore di "pagerState.current" cambierà,
        // (e cambierà quando l'utente eseguirà lo scrolling)
        // automaticamente verrà eseguita la coroutine qui sotto:
        LaunchedEffect(pagerState.currentPage){
            // eseguo la selezione dello screnn sul quale l'utente
            // è arrivato dopo aver fatto lo scrolling:
            selectedTabIndex = pagerState.currentPage
        }
        //////////////////////////////////////////////////////////////////


        Column(
            modifier = Modifier
                .fillMaxSize()
        ){

            Spacer(modifier = Modifier.height(60.dp))

            TabRow(selectedTabIndex = selectedTabIndex){
                // itero sui TabItem presenti nella lista 'tabItems' definita sopra:
                tabItems.forEachIndexed{ index, item ->
                    Tab(
                        selected = index == selectedTabIndex,
                        onClick = {
                            selectedTabIndex = index // aggiorno l'indice in modo da ricordarmi che l'utente ha selezionato una nuova sezione
                        },
                        text = {
                            Text(text = item.title)
                        },
                        icon = {
                                Icon(imageVector = if(index == selectedTabIndex){
                                  item.selectedIcon
                                } else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        }
                    )
                }
            }

            ////////////////////////////////////////////////////////////////////////////////////////////////////
            // "Request received" è la seconda scheda, quindi l'indice è 1:
            if (selectedTabIndex == 1) {
                // chiamo il metodo del 'handleFriendsViewModel' per prendere le richieste di amicizia ricevute dall'utente corrente
                // dal DB:
                handleFriendsViewModel.getRequestReceivedByUser(infoUserCurrent!!.email)

                // in 'reqReceivedCurrentUser' c'è la lista delle richieste di amicizia ricevute dall'utente corrente
                // mentre in 'infoUserCurrent' ci sono le info dell'utente corrente salvate nella tabella User:
                RequestReceivedList(reqReceivedCurrentUser = reqReceivedCurrentUser, infoUserCurrent)
            } else {
                // Se non sei nella sezione "Request received", mostra l'HorizontalPager
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) { index ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = tabItems[index].title)
                    }
                }
            }
            ////////////////////////////////////////////////////////////////////////////////////////////////////



            // Se la tab 'Request sent' è selezionata, mostro la barra di ricerca attraverso la quale l'utente
            // può inviare una richiesta ad un altro utente e sotto di essa ci sarà la lista di utenti
            // a cui l'utente corrente ha già inviato una richiesta.
            if (selectedTabIndex == 2) {
                // Inserisci qui il composable generico per la tab 'Mates'
                // Ad esempio:
                // SearchBarComposable()
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ){

                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

                        var searchText by remember { mutableStateOf(TextFieldValue("")) }

                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { newText ->
                                searchText = newText
                                handleFriendsViewModel.onSearchTextChange(newText.text)
                            },
                            label = { Text("Search by email") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        usersFilter?.let { users ->
                            if (users.isNotEmpty()) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    users.forEach { user ->
                                        // Non mostro l'email dell'utente corrente:
                                        if(user.email != infoUserCurrent!!.email){
                                            Text(
                                                text = user.email,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp)
                                                    .clickable {
                                                        selectedUser = user
                                                        showDialog = true
                                                    },
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = "No users found",
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }

                    }
                }

            }

            // l'HorizontalPager permette all'utente di poter eseguire lo scrolling laterale
            // per cambiare schermata:
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ){ index ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    Text(text = tabItems[index].title) // mostro il titolo della schermata corrente
                }
            }
        }


        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Friend Request") },
                text = { Text(text = "Do you want to send a friend request to this user?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // chiamo il metodo 'handleFriendsViewModel.insertNewRequest' passandogli come parametri
                            // l'email1 = email user corrente e l'email2 = email dell'utente a cui l'utente corrrente vuole inviare
                            // la richiesta di amicizia:
                            selectedUser?.email?.let { handleFriendsViewModel.insertNewRequest(email1 = infoUserCurrent!!.email, email2 = it) }
                            showDialog = false
                            showConfirmationDialog = true // in modo da far apparire la finestra di dialogo di sotto dove c'è if(showConfirmationDialog)
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("No")
                    }
                }
            )
        }

        if (showConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmationDialog = false },
                title = { Text(text = "Success") },
                text = { Text(text = "Friend request sent successfully!") },
                confirmButton = {
                    TextButton(
                        onClick = { showConfirmationDialog = false }
                    ) {
                        Text("OK")
                    }
                }
            )
        }



    }
}

data class TabItem(
    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
)


@Composable
fun RequestReceivedList(reqReceivedCurrentUser: List<HandleFriends>?, infoUserCurrent: User?) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Friend Requests Received",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(16.dp)
        )
        if (reqReceivedCurrentUser.isNullOrEmpty()) {
            Text(
                text = "No friend requests received",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(reqReceivedCurrentUser) { friendUser ->
//                    if(friendUser.email1 != infoUserCurrent!!.email){
//                        FriendRequestItem(email = friendUser.email1)
//                    }else{
//                        FriendRequestItem(email = friendUser.email2)
//                    }
                    FriendRequestItem(email = friendUser.email1)
                    //Divider()
                }
            }
        }
    }
}

@Composable
fun FriendRequestItem(email: String) {
    // Implementazione dell'elemento dell'elenco della richiesta di amicizia
    // Puoi personalizzare il layout in base alle tue esigenze
    Text(
        text = email,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}



// per lanciare la preview
//@Preview
//@Composable
//fun SettingsPreview(){
//    MusicDraftTheme {
//        Friends()
//    }
//}

