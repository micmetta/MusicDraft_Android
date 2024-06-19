package com.example.musicdraft.sections
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Markunread
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.Pending
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.musicdraft.data.tables.artisti.Artisti
import com.example.musicdraft.data.tables.handleFriends.HandleFriends
import com.example.musicdraft.data.tables.track.Track
import com.example.musicdraft.data.tables.user.User
import com.example.musicdraft.data.tables.user_cards.User_Cards_Artisti
import com.example.musicdraft.data.tables.user_cards.User_Cards_Track
import com.example.musicdraft.viewModel.CardsViewModel
import com.example.musicdraft.viewModel.ExchangeManagementCardsViewModel
import com.example.musicdraft.viewModel.HandleFriendsViewModel
import com.example.musicdraft.viewModel.LoginViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Friends(
    navController: NavController,
    handleFriendsViewModel: HandleFriendsViewModel,
    loginViewModel: LoginViewModel,
    cardsViewModel: CardsViewModel,
    exchangeManagementCardsViewModel: ExchangeManagementCardsViewModel) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val usersFilter by handleFriendsViewModel.usersFilter.collectAsState()
    val usersFilterbyNickname by handleFriendsViewModel.usersFilterbyNickname.collectAsState()

    val reqReceivedCurrentUser by handleFriendsViewModel.reqReceivedCurrentUser.collectAsState(null)
    val reqSentFromCurrentUser by handleFriendsViewModel.reqSentFromCurrentUser.collectAsState(null)
    val infoUserCurrent by loginViewModel.userLoggedInfo.collectAsState(initial = null)

    val allFriendsCurrentUser by handleFriendsViewModel.allFriendsCurrentUser.collectAsState(null)
    val allUsersFriendsOfCurrentUser by loginViewModel.allUsersFriendsOfCurrentUser.collectAsState(null)

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
        ),

        // aggiungere TabItem "Offers received" per visualizzare le offerte di scambi ricevute dagli amici:
        TabItem(
            title = "Offers received",
            unselectedIcon = Icons.Outlined.Pending,
            selectedIcon = Icons.Filled.Pending
        )
        ///////////////////////////////////////////

        // aggiungere TabItem "Offers sent" per visualizzare le offerte di scambi inviate agli amici:

        ///////////////////////////////////////////

    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val pagerState = rememberPagerState(pageCount = { tabItems.size })

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // più lento perchè il cambiamento della sezione la fa solo dopo che è terminato lo scrolling orizzontale..
        // ogni volta che il valore di "pagerState.current" cambierà,
        // (e cambierà quando l'utente eseguirà lo scrolling)
        // automaticamente verrà eseguita la coroutine qui sotto.
        // - Anche se un pò più lento sono certo di non avere delay o errori durante lo scrolling orizzontale:
        LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress){
            if(!pagerState.isScrollInProgress){
                // eseguo la selezione dello screnn sul quale l'utente
                // è arrivato dopo aver fatto lo scrolling:
                selectedTabIndex = pagerState.currentPage
                if (selectedTabIndex == 1) {
                    infoUserCurrent?.let {
                        handleFriendsViewModel.getRequestReceivedByUser(it.email)
                    }
                }
            }
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////
        // Quando l'utente clicca su una sotto-sezione allora passo a quella schermata:
        LaunchedEffect(selectedTabIndex) {
            pagerState.animateScrollToPage(selectedTabIndex)
        }
        ////////////////////////////////////////////////////////////

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(60.dp))

            TabRow(selectedTabIndex = selectedTabIndex) {
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
                    0 -> {
                        infoUserCurrent?.let {

                            ////////////////////////////////////////////////////////////////////////////////////////////////////
                            // Chiamata al metodo per aggiornare gli amici dell'utente corrente
                            handleFriendsViewModel.getAllFriendsByUser(it.email)
                            ////////////////////////////////////////////////////////////////////////////////////////////////////

                            val emailsList: List<String>? = allFriendsCurrentUser?.map { friend ->
                                ////////////////////////////////////////////////////////////////////////////////////////
                                // se friend.email1 è diversa dall'email dell'utente loggato allora vuol dire che
                                // friend.email1 è proprio l'email di un amico dell'utente corrente, altrimenti
                                // sarà friend.email2 l'email di un amico dell'utente corrente:
                                if (friend.email1 != infoUserCurrent?.email) {
                                    friend.email1
                                } else {
                                    friend.email2
                                }
                                ////////////////////////////////////////////////////////////////////////////////////////
                            }
                            if (emailsList != null) {
                                loginViewModel.getAllNicknameFriendsOfCurrentUser(emailsList)
                            }
                            RequestMatesList(navController, loginViewModel, handleFriendsViewModel, exchangeManagementCardsViewModel, cardsViewModel, allUsersFriendsOfCurrentUser, infoUserCurrent)
                        }
                    }
                    1 -> RequestReceivedList(handleFriendsViewModel, reqReceivedCurrentUser = reqReceivedCurrentUser, infoUserCurrent)
                    2 -> RequestSent(handleFriendsViewModel, loginViewModel, usersFilter, usersFilterbyNickname, infoUserCurrent, reqSentFromCurrentUser = reqSentFromCurrentUser, allUsersFriendsOfCurrentUser)
                    3 -> OffersReceived(navController, exchangeManagementCardsViewModel, infoUserCurrent)

                }
            }
        }
    }
}

data class TabItem(
    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
)

//var NicknameFriendUserSelected = ""

@Composable
fun RequestMatesList(
    navController: NavController,
    loginViewModel: LoginViewModel,
    handleFriendsViewModel: HandleFriendsViewModel,
    exchangeManagementCardsViewModel: ExchangeManagementCardsViewModel,
    cardsViewModel: CardsViewModel,
    allUsersFriendsOfCurrentUser: List<User>?,
    infoUserCurrent: User?,
    ) {

    var showDialogToDeleteFriendship by remember { mutableStateOf(false) }
    var showDialogToConfirmDeleteFriendship by remember { mutableStateOf(false) }
    var ExchangeFriendUserSelected by remember { mutableStateOf(false) }
    //var NicknameFriendUserSelected by remember { mutableStateOf("") }
    //var nicknameFriendUserSelected by exchangeManagementCardsViewModel.nicknameFriendUserSelected

    var nicknameUserCurrent by exchangeManagementCardsViewModel.nicknameUserCurrent
    var nicknameUserRequestCard by exchangeManagementCardsViewModel.nicknameUserRequestCard


    // lo vede:
//    val infoUserCurrentLogged by loginViewModel.userLoggedInfo.collectAsState(initial = null)
//    infoUserCurrentLogged?.let {
//        Log.d("RequestMatesList", "Nickname utente corrente: " + infoUserCurrentLogged!!.email)
//    }

    nicknameUserCurrent?.let {
        Log.d("RequestMatesList", "Nickname utente corrente: " + nicknameUserCurrent)
    }
    nicknameUserRequestCard?.let {
        Log.d("RequestMatesList", "Nickname utente a cui si vuole richiedere la carta: " + nicknameUserRequestCard)
    }


    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))
        if (allUsersFriendsOfCurrentUser.isNullOrEmpty()) {
            Text(
                text = "No friends yet",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(allUsersFriendsOfCurrentUser) { friendUser ->
                    if (infoUserCurrent != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Icona
                            Icon(
                                imageVector = Icons.Default.Person, // icona specifica dell'utente amico dell'utente corrente
                                contentDescription = "User Icon",
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(24.dp)
                            )
                            // Prendo il Nickname dell'utente amico
                            Text(
                                text = friendUser.nickname,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 4.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            // Icone cliccabili
                            IconButton(onClick = {

                                /* Azione quando si clicca sull'icona 1 (per avviare una richiesta di scambio con l'amico corrente) */
                                //exchangeManagementCardsViewModel.onEvent(UIEventExchangeCards.exchangeCards, navController, friendUser.nickname)
//                                NicknameFriendUserSelected = friendUser.nickname
                                //NicknameFriendUserSelected = friendUser.nickname
                                //nicknameFriendUserSelected = friendUser.nickname
                                nicknameUserCurrent = infoUserCurrent.nickname
                                nicknameUserRequestCard = friendUser.nickname
                                navController.navigate("exchangeCards")
                                //ExchangeFriendUserSelected = true
                            }) {
                                Icon(imageVector = Icons.Default.ChangeCircle, contentDescription = "exchangeCards")
                            }
                            IconButton(onClick = { /* Azione quando si clicca sull'icona 2 */ }) {
                                Icon(imageVector = Icons.Default.LocalFireDepartment, contentDescription = "ChallengeFriend")
                            }
                            IconButton(onClick = {
                                // quando clicco su questa icona, viene chiesto all'utente se è sicuro di voler cancellare
                                // dalla sua lista amici l'amico che ha selezionato e se conferma allora verrà cancellata dal
                                // DB la riga che rappresentava l'amiciza tra questi due utenti:
                                showDialogToDeleteFriendship = true
                            }) {
                                Icon(imageVector = Icons.Default.DeleteForever, contentDescription = "DeleteFriend")
                            }
                        }


                        if(showDialogToDeleteFriendship){
                            AlertDialog(
                                onDismissRequest = { showDialogToDeleteFriendship = false },
                                title = { Text(text = "Delete Friendship") },
                                text = { Text(text = "Do you really want to remove this user from your friends list?") },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            handleFriendsViewModel.deleteFriendship(infoUserCurrent.email, friendUser.email)
                                            showDialogToDeleteFriendship = false
                                            showDialogToConfirmDeleteFriendship = true
                                        }
                                    ) {
                                        Text("Yes")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDialogToDeleteFriendship = false }) {
                                        Text("No")
                                    }
                                }
                            )
                        }
                    }
                    Divider(
                        color = Color.Black,
                        thickness = 2.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }

    if (showDialogToConfirmDeleteFriendship) {
        AlertDialog(
            onDismissRequest = { showDialogToConfirmDeleteFriendship = false },
            title = { Text(text = "Delete Friendship Success") },
            text = { Text(text = "Friendship successfully deleted!") },
            confirmButton = {
                TextButton(
                    onClick = { showDialogToConfirmDeleteFriendship = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
}



@Composable
fun RequestReceivedList(handleFriendsViewModel: HandleFriendsViewModel, reqReceivedCurrentUser: List<HandleFriends>?, infoUserCurrent: User?) {
    Column(modifier = Modifier.fillMaxSize()) {
//        Text(
//            text = "Friend Requests Received",
//            style = MaterialTheme.typography.displaySmall,
//            modifier = Modifier.padding(16.dp)
//        )
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
                    Column {
                        if (infoUserCurrent != null) {
                            FriendRequestItem(handleFriendsViewModel, email1 = friendUser.email1, infoUserCurrent.email)
                        }
                        Divider(
                            color = Color.Black,
                            thickness = 2.dp, // spessore delle linee
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FriendRequestItem(handleFriendsViewModel: HandleFriendsViewModel, email1: String, email2: String) {

    var showAcceptedDialog by remember { mutableStateOf(false) }
    var showRefuseDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = email1,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            imageVector = Icons.Filled.PersonAdd,
            contentDescription = "AddFriend",
            modifier = Modifier
                .clickable {
                    // Richiesta d'amicizia accettata:
                    //handleFriendsViewModel.acceptRequest(email1, email2)
                    showAcceptedDialog = true // start LaunchedEffect(key1 = showAcceptedDialog)
                }
                .size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = "Refuse",
            modifier = Modifier
                .clickable {
                    // Richiesta d'amicizia rifiutata:
                    //handleFriendsViewModel.refuseRequest(email1, email2)
                    showRefuseDialog = true // start LaunchedEffect(key1 = showRefuseDialog)
                }
                .size(24.dp)
        )

        // Non appena  cambierà il valore di 'showAcceptedDialog' allora verrà eseguita la coroutine qui sotto:
        LaunchedEffect(key1 = showAcceptedDialog) {
            // lancio una coroutine per non bloccare il thread UI:
            scope.launch {
                if (showAcceptedDialog) {
                    // se entro qui vuol dire che il login con Google è avvenuto con successo e quindi
                    // tramite Toast.makeText mostro un breve messaggio informativo all'utente sottoforma di
                    // popup (che si sovrappone all'interfaccia) che in questo caso poichè c'è il parametro "Toast.LENGTH_LONG" durerà un tempo più lungo:
                    Toast.makeText(context, "Now you and this user are friends!", Toast.LENGTH_LONG).show()
                    handleFriendsViewModel.acceptRequest(email1, email2)
                    showAcceptedDialog = false
                }
            }
        }

        // Non appena  cambierà il valore di 'showRefuseDialog' allora verrà eseguita la coroutine qui sotto:
        LaunchedEffect(key1 = showRefuseDialog) {
            // lancio una coroutine per non bloccare il thread UI:
            scope.launch {
                if (showRefuseDialog) {
                    // se entro qui vuol dire che il login con Google è avvenuto con successo e quindi
                    // tramite Toast.makeText mostro un breve messaggio informativo all'utente sottoforma di
                    // popup (che si sovrappone all'interfaccia) che in questo caso poichè c'è il parametro "Toast.LENGTH_LONG" durerà un tempo più lungo:
                    Toast.makeText(context, "Request friend refused!", Toast.LENGTH_LONG).show()
                    handleFriendsViewModel.refuseRequest(email1, email2)
                    showRefuseDialog = false
                }
            }
        }
    }
}


@Composable
fun RequestSent(
    handleFriendsViewModel: HandleFriendsViewModel,
    loginViewModel: LoginViewModel,
    usersFilter: List<User>?,
    usersFilterbyNickname: List<User>?,
    infoUserCurrent: User?,
    reqSentFromCurrentUser: List<HandleFriends>?,
    allUsersFriendsOfCurrentUser: List<User>?,
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showDialogYouHaveAlreadyReceived by remember { mutableStateOf(false) }
    var showDialogYouHaveAlreadySent by remember { mutableStateOf(false) }
    var showDialogToDeleteRequestFriendship by remember { mutableStateOf(false) }
    var showDialogToConfirmDeleteRequestFriendship by remember { mutableStateOf(false) }


    val allPendingRequest by handleFriendsViewModel.allPendingRequest.collectAsState(null)
    val allUsersrReceivedRequestByCurrentUser by loginViewModel.allUsersrReceivedRequestByCurrentUser.collectAsState(null)

    // Chiamo subito questi due metodi che mi permetteranno di aggiornare rispettivamente
    // handleFriendsViewModel.getRequestSent(it) -> 'reqSentFromCurrentUser'
    // loginViewModel.getallUsersrReceivedRequestByCurrentUser(reqSentFromCurrentUser) -> 'allUsersrReceivedRequestByCurrentUser'
    infoUserCurrent?.email?.let{
        // Chiamata al metodo per aggiornare le richieste di amicizia inviate dall'utente corrente:
        handleFriendsViewModel.getRequestSent(it)
        // aggiorno tutti i nicknames degli utenti ai quali l'utente corrente ha inviato una richiesta
        loginViewModel.getallUsersrReceivedRequestByCurrentUser(reqSentFromCurrentUser)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {

        var searchText by remember { mutableStateOf(TextFieldValue("")) }
        var searchByEmail by remember { mutableStateOf(false) }
        var searchByNickname by remember { mutableStateOf(false) }

        Row {
            Checkbox(
                checked = searchByEmail,
                onCheckedChange = {
                    searchByEmail = it
                    if (it) searchByNickname = false
                }
            )
            Text("Search by email")
            Spacer(modifier = Modifier.width(16.dp))
            Checkbox(
                checked = searchByNickname,
                onCheckedChange = {
                    searchByNickname = it
                    if (it) searchByEmail = false
                }
            )
            Text("Search by nickname")
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (searchByEmail) {
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    handleFriendsViewModel.onSearchTextChange(it.text)
                    handleFriendsViewModel.onSearchTextChangeByNickname(it.text) // eseguo anche questo aggiornamento in modo tale che
                    // se l'utente dovesse fare il check su "Search by nickname" potrà vedere immeddiatamente l'aggiornamento
                    // in base ai caratteri che aveva inserito quando aveva la spunta su "Sarch by email".
                },
                label = { Text("Search for the email of a user to send the friend request to") },
                modifier = Modifier.fillMaxWidth()
            )
            Column(modifier = Modifier
                //.weight(1f)
                .heightIn(max = 100.dp) // altezza massima occupata in verticale dalla colonna nella quale compariranno le emails degli utenti ricercarti
                                        // (una volta raggiunta questa altezza massima, le emails potranno essere scrollate dall'utente.
            )
            {
                usersFilter?.let { users ->
                    if (users.isNotEmpty()) {
                        ////////////////////////////////////////////////////////////////////////////////////////////////////
                        // Chiamata al metodo per aggiornare le richieste pendenti nelle quali è coinvolto l'utente corrente:
                        infoUserCurrent?.email?.let { handleFriendsViewModel.getAllPendingRequestByUser(it) }
                        ////////////////////////////////////////////////////////////////////////////////////////////////////

                        LazyColumn(modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(4.dp)
                        ) {
                            items(users) { user ->
                                if (
                                // - L'utente filtrato (ovvero 'user.email') è quell'utente al quale l'utente corrente
                                //   può inviare la richiesta di amicizia.
                                //   Devono però essere rispettatte queste due condizioni:
                                // 1) l'email dell'utente filtrato non deve essere uguale all'email dell'utente corrente:
                                    (user.email != infoUserCurrent?.email) &&
                                    // 2) l'email dell'utente filtrato non è già presente nella lista amici dell'utente corrente;
                                    //    il true alla fine verifica che la condizione 2) valga per tutti gli elementi della
                                    //    lista 'allUsersFriendsOfCurrentUser' (con 'it' prendo ogni valore presente nella lista
                                    //    'allUsersFriendsOfCurrentUser'):
                                    (allUsersFriendsOfCurrentUser?.none { it.email == user.email } == true)
                                ) {

                                    var backgroundColor by remember { mutableStateOf(Color.White) }


                                    Log.d("Friends", "user.email SOTTO LA BARRA DI RICERCA IN Friends: ${user.email}")
                                    Text(
                                        text = user.email,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .background(
                                                backgroundColor,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .clickable {
                                                backgroundColor = Color.LightGray

                                                infoUserCurrent?.email?.let {
                                                    //handleFriendsViewModel.getAllPendingRequestByUser(it) // aggiorno le richieste pendenti
                                                    // 1) controllo che l'utente corrente non abbia già mandato la richiesta a 'selectedUser'
                                                    // 2) controllo che l'utente corrente non abbia già ricevuto una richiesta da parte di 'selectedUser'
                                                    val requestStatus = when {
                                                        allPendingRequest?.any { pendingRequest ->
                                                            pendingRequest.email1 == user.email && pendingRequest.email2 == infoUserCurrent.email
                                                        } == true -> {
                                                            // La richiesta è stata già inviata dall'utente filtrato all'utente corrente
                                                            "This user has already sent you a request!"
                                                        }

                                                        allPendingRequest?.any { pendingRequest ->
                                                            pendingRequest.email1 == infoUserCurrent.email && pendingRequest.email2 == user.email
                                                        } == true -> {
                                                            // La richiesta è stata inviata dall'utente corrente all'utente filtrato
                                                            "You have already sent a request to this user!"
                                                        }

                                                        else -> {
                                                            // Nessuna richiesta è stata inviata tra questi due utenti
                                                            "NoRequest"
                                                        }
                                                    }

                                                    // Controllo il 'requestStatus':
                                                    when (requestStatus) {
                                                        "This user has already sent you a request!" -> {
                                                            // La richiesta è stata già inviata dall'utente filtrato all'utente corrente
                                                            showDialogYouHaveAlreadyReceived = true
                                                        }

                                                        "You have already sent a request to this user!" -> {
                                                            // La richiesta è stata inviata dall'utente corrente all'utente filtrato
                                                            showDialogYouHaveAlreadySent = true
                                                        }

                                                        "NoRequest" -> {
                                                            // Nessuna richiesta è stata inviata tra questi due utenti
                                                            selectedUser = user
                                                            showDialog = true
                                                        }
                                                    }
                                                }
                                                backgroundColor = Color.White
                                            }
                                            .background(
                                                backgroundColor,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 8.dp)
                                        //.padding(horizontal = 8.dp),
                                        //style = MaterialTheme.typography.bodyLarge
                                        //style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
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
        } else if (searchByNickname) {
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    handleFriendsViewModel.onSearchTextChangeByNickname(it.text)
                    handleFriendsViewModel.onSearchTextChange(it.text) // eseguo anche questo aggiornamento in modo tale che
                    // se l'utente dovesse fare il check su "Search by email" potrà vedere immeddiatamente l'aggiornamento
                    // in base ai caratteri che aveva inserito quando aveva la spunta su "Sarch by nickname".
                },
                label = { Text("Search for the nickname of a user to send the friend request to") },
                modifier = Modifier.fillMaxWidth()
            )
            Column(modifier =
                    Modifier
                    //.weight(1f)
                    .heightIn(max = 100.dp) // altezza massima occupata in verticale dalla colonna nella quale compariranno i nicknames degli utenti ricercarti
                                            // (una volta raggiunta questa altezza massima, i nicknames potranno essere scrollati dall'utente.
            ) {
                usersFilterbyNickname?.let { users ->
                    if (users.isNotEmpty()) {
                        ////////////////////////////////////////////////////////////////////////////////////////////////////
                        // Chiamata al metodo per aggiornare le richieste pendenti nelle quali è coinvolto l'utente corrente:
                        infoUserCurrent?.email?.let { handleFriendsViewModel.getAllPendingRequestByUser(it) }
                        ////////////////////////////////////////////////////////////////////////////////////////////////////

                        LazyColumn(modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(4.dp)
                        ) {
                            items(users) { user ->
                                if (
                                // - L'utente filtrato (ovvero 'user.email') è quell'utente al quale l'utente corrente
                                //   può inviare la richiesta di amicizia.
                                //   Devono però essere rispettatte queste due condizioni:
                                // 1) l'email dell'utente filtrato non deve essere uguale all'email dell'utente corrente:
                                    (user.email != infoUserCurrent?.email) &&
                                    // 2) l'email dell'utente filtrato non è già presente nella lista amici dell'utente corrente;
                                    //    il true alla fine verifica che la condizione 2) valga per tutti gli elementi della
                                    //    lista 'allUsersFriendsOfCurrentUser' (con 'it' prendo ogni valore presente nella lista
                                    //    'allUsersFriendsOfCurrentUser'):
                                    (allUsersFriendsOfCurrentUser?.none { it.email == user.email } == true)
                                ) {

                                    var backgroundColor by remember { mutableStateOf(Color.White) }

                                    Text(
                                        text = user.nickname,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .background(
                                                backgroundColor,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .clickable {
                                                backgroundColor = Color.LightGray
                                                infoUserCurrent?.email?.let {

                                                    //handleFriendsViewModel.getAllPendingRequestByUser(it) // aggiorno le richieste pendenti
                                                    // 1) controllo che l'utente corrente non abbia già mandato la richiesta a 'selectedUser'
                                                    // 2) controllo che l'utente corrente non abbia già ricevuto una richiesta da parte di 'selectedUser'
                                                    val requestStatus = when {
                                                        allPendingRequest?.any { pendingRequest ->
                                                            pendingRequest.email1 == user.email && pendingRequest.email2 == infoUserCurrent.email
                                                        } == true -> {
                                                            // La richiesta è stata già inviata dall'utente filtrato all'utente corrente
                                                            "This user has already sent you a request!"
                                                        }

                                                        allPendingRequest?.any { pendingRequest ->
                                                            pendingRequest.email1 == infoUserCurrent.email && pendingRequest.email2 == user.email
                                                        } == true -> {
                                                            // La richiesta è stata inviata dall'utente corrente all'utente filtrato
                                                            "You have already sent a request to this user!"
                                                        }

                                                        else -> {
                                                            // Nessuna richiesta è stata inviata tra questi due utenti
                                                            "NoRequest"
                                                        }
                                                    }

                                                    // Controllo il 'requestStatus':
                                                    when (requestStatus) {
                                                        "This user has already sent you a request!" -> {
                                                            // La richiesta è stata già inviata dall'utente filtrato all'utente corrente
                                                            showDialogYouHaveAlreadyReceived = true
                                                        }

                                                        "You have already sent a request to this user!" -> {
                                                            // La richiesta è stata inviata dall'utente corrente all'utente filtrato
                                                            showDialogYouHaveAlreadySent = true
                                                        }

                                                        "NoRequest" -> {
                                                            // Nessuna richiesta è stata inviata tra questi due utenti
                                                            selectedUser = user
                                                            showDialog = true
                                                        }
                                                    }
                                                }
                                                backgroundColor = Color.White
                                            }
                                            .background(
                                                backgroundColor,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 8.dp)
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

        if(!allUsersrReceivedRequestByCurrentUser.isNullOrEmpty()){

            Spacer(modifier = Modifier.height(50.dp))

            // Se l'utente corrente ha già inviato almeno una richiesta di amicizia a un qualsiasi altro utente
            // allora mostro queste richieste, altrimenti NON MOSTRO NULLA.
            Text(
                text = "Users to whom you have already sent requests:",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                infoUserCurrent?.email?.let {
//                // Chiamata al metodo per aggiornare le richieste di amicizia inviate dall'utente corrente:
//                handleFriendsViewModel.getRequestSent(it)
//
//                // aggiorno tutti i nicknames degli utenti ai quali l'utente corrente ha inviato una richiesta
//                loginViewModel.getallUsersrReceivedRequestByCurrentUser(reqSentFromCurrentUser)

                    // Scorro la lista delle richieste di amicizia inviate dall'utente corrente:
                    items(allUsersrReceivedRequestByCurrentUser ?: emptyList()) { userReceivedRequestByCurrentUser ->
                        // Singolo elemento della lista:
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "User Icon",
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(24.dp)
                            )
                            // Testo con il nickname dell'utente:
                            Text(
                                text = userReceivedRequestByCurrentUser.nickname, // mostro SOLO il nickname dell'utente
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 4.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            // Icona per eliminare la richiesta di amicizia
                            IconButton(onClick = {
                                // quando clicco su questa icona, viene chiesto all'utente se è sicuro di voler cancellare
                                // la richiesta inviata all'utente corrente e se conferma allora verrà cancellata dal
                                // DB la riga che rappresentava la richiesta di amicizia tra questi due utenti:
                                showDialogToDeleteRequestFriendship = true
                            }) {
                                Icon(imageVector = Icons.Default.DeleteForever, contentDescription = "DeleteFriend")
                            }
                        }
                        if(showDialogToDeleteRequestFriendship){
                            AlertDialog(
                                onDismissRequest = { showDialogToDeleteRequestFriendship = false },
                                title = { Text(text = "Delete Friendship") },
                                text = { Text(text = "Do you really want to delete this request?") },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            handleFriendsViewModel.refuseRequest(infoUserCurrent.email, userReceivedRequestByCurrentUser.email)
                                            showDialogToDeleteRequestFriendship = false
                                            showDialogToConfirmDeleteRequestFriendship = true
                                        }
                                    ) {
                                        Text("Yes")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDialogToDeleteRequestFriendship = false }) {
                                        Text("No")
                                    }
                                }
                            )
                        }

                        // linea di divisione tra un utente e il successivo:
                        Divider(
                            color = Color.Black,
                            thickness = 2.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
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
                        selectedUser?.email?.let { handleFriendsViewModel.insertNewRequest(email1 = infoUserCurrent!!.email, email2 = it) }
                        showDialog = false
                        showConfirmationDialog = true
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

    if (showDialogYouHaveAlreadyReceived) {
        AlertDialog(
            onDismissRequest = { showDialogYouHaveAlreadyReceived = false },
            title = { Text(text = "Success") },
            text = { Text(text = "This user has already sent you a request!") },
            confirmButton = {
                TextButton(
                    onClick = { showDialogYouHaveAlreadyReceived = false }
                ) {
                    Text("OK")
                }
            }
        )
    }

    if (showDialogYouHaveAlreadySent) {
        AlertDialog(
            onDismissRequest = { showDialogYouHaveAlreadySent = false },
            title = { Text(text = "Attention") },
            text = { Text(text = "You have already sent a request to this user! \n Wait for him to answer you..") },
            confirmButton = {
                TextButton(
                    onClick = { showDialogYouHaveAlreadySent = false }
                ) {
                    Text("OK")
                }
            }
        )
    }

    if (showDialogToConfirmDeleteRequestFriendship) {
        AlertDialog(
            onDismissRequest = { showDialogToConfirmDeleteRequestFriendship = false },
            title = { Text(text = "Delete Request Friendship Success") },
            text = { Text(text = "Request Friendship successfully deleted!") },
            confirmButton = {
                TextButton(
                    onClick = { showDialogToConfirmDeleteRequestFriendship = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
}




/*
 - Composable che permette di visualizzare l'offerta fatta dall'utente loggato verso un altro utente (suo amico).
*/
@Composable
fun ExchangeCards(
    navController: NavController,
    loginViewModel: LoginViewModel,
    exchangeManagementCardsViewModel: ExchangeManagementCardsViewModel,
    cardsViewModel: CardsViewModel
) {
    Log.d("ExchangeCards", "Sono nel composable ExchangeCards!")

    var thereIsRequestCard by remember { mutableStateOf(false) }
    var typeOfRequestCard by remember { mutableStateOf("") }

    var thereIsAnOfferCard by remember { mutableStateOf(false) }

    var nicknameUserCurrent by exchangeManagementCardsViewModel.nicknameUserCurrent
    var nicknameUserRequestCard by exchangeManagementCardsViewModel.nicknameUserRequestCard
    val friendRequestCard by loginViewModel.friendRequestCard.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    // per ricordarmi qual è la carta artista/brano che l'utente corrente ha selezionato e che quindi vuole richidere
    // all'amico:
    var requestCardArtist by remember { mutableStateOf(User_Cards_Artisti(identifier = -1, id_carta = "", genere = "", immagine = "", nome = "", popolarita = -1, email = "")) }
    var requestCardTrack by remember { mutableStateOf(User_Cards_Track(identifire = -1, id_carta = "", anno_pubblicazione = "", durata = "", immagine = "", nome = "", popolarita = -1, email = "")) }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // caricamento carte dell'amico al quale si vuole fare l'offerta:
    val artistsFriend by cardsViewModel.acquiredCardsAFriend.collectAsState(emptyList())
    val tracksFriend by cardsViewModel.acquiredCardsTFriend.collectAsState(emptyList())
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////
    // Per mostrare o meno il composable 'AddCardComposable':
    var showAddCardComposable by remember { mutableStateOf(false) }
    //////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////
    // - lista di carte offerte dall'utente corrente:
    val listOfCardsArtistsOfferedByCurrentUser = remember { mutableStateListOf<User_Cards_Artisti>() }
    val listOfCardsTracksOfferedByCurrentUser = remember { mutableStateListOf<User_Cards_Track>() }

    //////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////
    // - Variabili per la gestione dei points offerti dall'utente corrente:
    var offeredPoints by remember { mutableStateOf(0) }
    val showDialogOfferedPoints = remember { mutableStateOf(false) }
    val inputPoints = remember { mutableStateOf("") }
    ////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////
    var showSuccessSendOffer = remember { mutableStateOf(false) }
    var showInsufficientPointsSendOffer = remember { mutableStateOf(false) }
    ////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////
    val infoUserCurrent by loginViewModel.userLoggedInfo.collectAsState(initial = null)
    ////////////////////////////////////////////////////////////////////////


    nicknameUserCurrent?.let {
        Log.d("ExchangeCards", "Nickname utente corrente: $nicknameUserCurrent")
        // richiesta aggiornamento carte artisti/brani dell'utente:
        cardsViewModel.getallcards()
        ////////////////////////////////////////////
    }
    nicknameUserRequestCard?.let {
        Log.d("ExchangeCards", "Nickname utente a cui si vuole richiedere la carta: $nicknameUserRequestCard")
        // aggiorno l'email dell'amico partendo dal suo nickname in modo da poter usare sotto l'email dell'amico ('it1.email'):
        loginViewModel.getUserByNickname(nicknameUserRequestCard)
        // richiesta aggiornamento carte artisti/brani dell'amico al quale l'utente corrente vuole inviare una richiesta:
        friendRequestCard?.let { it1 ->
            Log.d("ExchangeCards", "Email utente a cui si vuole richiedere la carta: ${it1.email}")
            cardsViewModel.getAllCardFriend(it1.email)
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Questa lambda verrà eseguita quando l'utente preme il pulsante indietro:
    BackHandler {
        if (showAddCardComposable) {
            showAddCardComposable = false
        } else if (thereIsRequestCard) {
            thereIsRequestCard = false
        } else {
            navController.popBackStack()
        }
    }
    ////////////////////////////////////////////////////////////////////////////


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {

        if (thereIsRequestCard) {
            if (showAddCardComposable) {
                AddCardComposable(
                    cardsViewModel,
                    onBack = { showAddCardComposable = false },
                    listOfCardsArtistsOfferedByCurrentUser,
                    listOfCardsTracksOfferedByCurrentUser
                )
            } else {
                Text("This is card of $nicknameUserRequestCard requested:")
                if (typeOfRequestCard == "artist") {
                    // mostro la carta artista richiesta:
                    Card(modifier = Modifier.padding(8.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = requestCardArtist.nome, style = MaterialTheme.typography.bodyLarge)
                            Text(text = requestCardArtist.genere)
                            Text(text = "Pop: ${requestCardArtist.popolarita}")
                            Text(text = "Costo: ${requestCardArtist.popolarita * 10}")
                            // Immagine dell'artista con dimensioni specificate
                            Image(
                                painter = rememberAsyncImagePainter(requestCardArtist.immagine),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(150.dp)
                                    .fillMaxWidth(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                } else {
                    // mostro la carta brano richiesta:
                    Card(modifier = Modifier.padding(8.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = requestCardTrack.nome, style = MaterialTheme.typography.bodyLarge)
                            Text(text = requestCardTrack.anno_pubblicazione)
                            Text(text = requestCardTrack.durata)
                            Text(text = "Pop: ${requestCardTrack.popolarita}")
                            Text(text = "Costo: ${requestCardTrack.popolarita * 10}")
                            // Immagine del brano con dimensioni specificate
                            Image(
                                painter = rememberAsyncImagePainter(requestCardTrack.immagine),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(150.dp)
                                    .fillMaxWidth(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))


                // Adesso l'utente corrente può selezionare le proprie carte da inserire nella richiesta oppure
                // può selezionare anche solo i points da inviare al suo amico:

                if (thereIsAnOfferCard) {
                    Text("These are your cards that you want offer to $nicknameUserRequestCard:")
                } else if (!thereIsAnOfferCard || offeredPoints == 0) {
                    Text("You can select your cards or points that you want offer to $nicknameUserRequestCard:")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            // Vado ad una nuova schermata per permettere all'utente di vedere le sue carte artisti/brani
                            // e di selezionare (una carta per volta) quelle che vuole inserire nell'offerta:
                            showAddCardComposable = true
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Add card")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            showDialogOfferedPoints.value = true
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Update points")
                    }
                }


                ////////////////////////////////////////////////////////////////////////////////////
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp)
                ) {

                    // - Mostro la lista delle carte offerte (di tipo artist o track) se non sono entrambi vuote vuota:
                    if (listOfCardsArtistsOfferedByCurrentUser.isNotEmpty() || listOfCardsTracksOfferedByCurrentUser.isNotEmpty()) {

                        // Se ci sono carte offerte, vengono mostrate:
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Offered Cards:")

                        // Mostra le carte artisti offerte dall'utente corrente:
                        listOfCardsArtistsOfferedByCurrentUser.forEach { card ->
                            OfferedCardArtistItem(
                                card = card,
                                onRemoveClick = {
                                    listOfCardsArtistsOfferedByCurrentUser.remove(card)
                                }
                            )
                        }

                        // Mostra le carte brani offerte dall'utente corrente:
                        listOfCardsTracksOfferedByCurrentUser.forEach { card ->
                            OfferedCardTrackItem(
                                card = card,
                                onRemoveClick = {
                                    listOfCardsTracksOfferedByCurrentUser.remove(card)
                                }
                            )
                        }
                    }

                    // Mostro "Offered Points" anche se ancora non ci sono carte offerte o points offerti:
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Offered Points: $offeredPoints")

                    // Button "Send Offer" sempre visibile:
                    Button(
                        onClick = {
                            // Azione per inviare l'offerta:
                            if(infoUserCurrent?.points!! >= offeredPoints){

                                Log.d("ExchangeCards", "Points dell'utente corrente: ${infoUserCurrent?.points}")

                                // Prendo il valore del parametro 'idRequiredCard':
                                val idRequiredCardValue = if (typeOfRequestCard == "artist") {
                                    requestCardArtist.id_carta
                                } else {
                                    requestCardTrack.id_carta
                                }

                                ///////////////////////////////////////////////////////////////////////////////////////
                                // Salvo tutti gli id delle carte offerte in un'unica lista:
                                val listOfferedCards = mutableListOf<String>()
                                // Aggiungi gli ID delle carte artisti
                                listOfferedCards.addAll(listOfCardsArtistsOfferedByCurrentUser.map { it.id_carta })
                                // Aggiungi gli ID delle carte tracce
                                listOfferedCards.addAll(listOfCardsTracksOfferedByCurrentUser.map { it.id_carta })
                                ///////////////////////////////////////////////////////////////////////////////////////

                                ///////////////////////////////////////////////////////////////////////////////////////
                                // Salvo tutti i tipi delle carte offerte in un'unica lista:
                                val listTypesOfferedCards = mutableListOf<String>()
                                // Aggiungi gli ID delle carte artisti
                                listTypesOfferedCards.addAll(listOfCardsArtistsOfferedByCurrentUser.map { "artist" })
                                // Aggiungi gli ID delle carte tracce
                                listTypesOfferedCards.addAll(listOfCardsTracksOfferedByCurrentUser.map { "track" })
                                ///////////////////////////////////////////////////////////////////////////////////////

                                ///////////////////////////////////////////////////////////////////////////////////////
                                // Chiamo il metodo 'insertNewOffer' dell' 'exchangeManagementCardsViewModel' passandogli
                                // tutte le info utili per riuscire ad inserire nel DB la richiesta dell'utente corrente:
                                exchangeManagementCardsViewModel.insertNewOffer(
                                    nicknameUserCurrent,
                                    nicknameUserRequestCard,
                                    idRequiredCardValue,
                                    typeOfRequestCard,
                                    listOfferedCards,
                                    listTypesOfferedCards,
                                    offeredPoints
                                )
                                ///////////////////////////////////////////////////////////////////////////////////////

                                showSuccessSendOffer.value = true // feedback per l'utente
                            }else{
                                Log.d("ExchangeCards", "Points dell'utente corrente: ${infoUserCurrent?.points}")
                                // points inseriti non disponibili da parte dell'utente corrente:
                                showInsufficientPointsSendOffer.value = true
                            }


                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Text("Send Offer")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                ////////////////////////////////////////////////////////////////////////////////////


            }
        } else {
            Text("Select here to select the card to request from user $nicknameUserRequestCard:")
            Spacer(modifier = Modifier.width(16.dp))

            Spacer(modifier = Modifier.height(8.dp))

            // Adesso eseguo la ricerca in base quale sezione l'utente ha selezionato:

            TabRow(selectedTabIndex = selectedTab, modifier = Modifier.fillMaxWidth()) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("Artists of $nicknameUserRequestCard")
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("Tracks of $nicknameUserRequestCard")
                }
            }

            // Visualizza la lista degli artisti o delle tracce (dell'amico) in base alla scheda selezionata:
            when (selectedTab) {
                0 -> artistsFriend?.let {
                    ArtistisScreen(
                        it,
                        requestCardArtist = requestCardArtist,
                        onUpdateRequestCardArtist = { updateRequestCardArtist ->
                            requestCardArtist = updateRequestCardArtist
                        },
                        thereIsRequestCard = thereIsRequestCard,
                        onUpdateRequestCardChange = { updateThereIsRequestCard ->
                            thereIsRequestCard = updateThereIsRequestCard
                        },
                        typeOfRequestCard = typeOfRequestCard,
                        onUpdateTypeOfRequestCardChange = { updateTypeOfRequestCard ->
                            typeOfRequestCard = updateTypeOfRequestCard
                        }
                    )
                }

                1 -> tracksFriend?.let {
                    TracksScreen(
                        it,
                        requestCardTrack = requestCardTrack,
                        onUpdateRequestCardTrack = { updateRequestCardTrack ->
                            requestCardTrack = updateRequestCardTrack
                        },
                        thereIsRequestCard = thereIsRequestCard,
                        onUpdateRequestCardChange = { updateThereIsRequestCard ->
                            thereIsRequestCard = updateThereIsRequestCard
                        },
                        typeOfRequestCard = typeOfRequestCard,
                        onUpdateTypeOfRequestCardChange = { updateTypeOfRequestCard ->
                            typeOfRequestCard = updateTypeOfRequestCard
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // finestra di dialogo per permettere all'utente di inserire i points che vuole offrire:
        if (showDialogOfferedPoints.value) {
            AlertDialog(
                onDismissRequest = { showDialogOfferedPoints.value = false },
                title = { Text(text = "Update Points") },
                text = {
                    Column {
                        Text(text = "Please enter the number of points:")
                        TextField(
                            value = inputPoints.value,
                            onValueChange = { inputPoints.value = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val points = inputPoints.value.toIntOrNull()
                            if (points != null) {
                                offeredPoints = points
                                showDialogOfferedPoints.value = false
                            }
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialogOfferedPoints.value = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if(showSuccessSendOffer.value){
            AlertDialog(
                onDismissRequest = {
                    showSuccessSendOffer.value = false
                    navController.popBackStack()
                },
                title = { Text(text = "Offer sent successfully!") },
                text = { Text(text = "Your offer has been sent to $nicknameUserRequestCard") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showSuccessSendOffer.value = false
                            navController.popBackStack()
                        }
                    ) {
                        Text("OK")
                    }
                }
            )
        }

        if(showInsufficientPointsSendOffer.value){
            AlertDialog(
                onDismissRequest = {
                    showInsufficientPointsSendOffer.value = false
                    navController.popBackStack()
                },
                title = { Text(text = "Insufficient points!") },
                text = { Text(text =  "You do not have enough points to send this offer..") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showInsufficientPointsSendOffer.value = false
                            navController.popBackStack()
                        }
                    ) {
                        Text("OK")
                    }
                }
            )
        }

    }
}


/**
 - Composable per la visualizzazione delle carte 'Artista' inserite nell'offerta con la possibilità
   di eliminare tale carta.
*/
@Composable
fun OfferedCardArtistItem(
    card: User_Cards_Artisti,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(card.immagine),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column (
                modifier = Modifier
                    .weight(1f)
            ){
                Text(text = card.nome, style = MaterialTheme.typography.bodyLarge)
                Text(text = card.genere)
                Text(text = "Pop: ${card.popolarita}")
                Text(text = "Costo: ${card.popolarita * 10}")
            }
            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clickable { onRemoveClick() }
            ) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", modifier = Modifier.padding(8.dp))
            }
        }
    }
}


/**
- Composable per la visualizzazione delle carte 'Brano' inserite nell'offerta con la possibilità
di eliminare tale carta.
 */
@Composable
fun OfferedCardTrackItem(
    card: User_Cards_Track,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(card.immagine),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column (
                modifier = Modifier
                    .weight(1f)
            ){
                Text(text = card.nome, style = MaterialTheme.typography.bodyLarge)
                Text(text = card.durata)
                Text(text = "Pop: ${card.popolarita}")
                Text(text = "Costo: ${card.popolarita * 10}")
            }
            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clickable { onRemoveClick() }
            ) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", modifier = Modifier.padding(8.dp))
            }
        }
    }
}



/**
 * Composable per la visualizzazione degli artisti nella schermata dove l'utente corrente deve scegliere la carta da richiedere all'amico.
 * @param artisti Elenco degli artisti da visualizzare.
 */
@Composable
fun ArtistisScreen(
    artists: List<User_Cards_Artisti>,
    requestCardArtist: User_Cards_Artisti,
    onUpdateRequestCardArtist: (User_Cards_Artisti) -> Unit,
    thereIsRequestCard: Boolean,
    onUpdateRequestCardChange: (Boolean) -> Unit,
    typeOfRequestCard: String,
    onUpdateTypeOfRequestCardChange: (String) -> Unit
) {
    // Visualizza una griglia di carte per gli artisti
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        // Itera attraverso gli artisti e visualizza una carta per ciascuno
        items(artists.size) { index ->
            ArtistsCard(
                artists[index],
                Modifier.height(8.dp),
                requestCardArtist,
                onUpdateRequestCardArtist,
                thereIsRequestCard,
                onUpdateRequestCardChange,
                typeOfRequestCard,
                onUpdateTypeOfRequestCardChange
            )
        }
    }
}

/**
 * Composable per la visualizzazione degli artisti nella schermata dove l'utente corrente deve scegliere le sue carte
 * da inserire nell'offerta fatta al suo amico.
 * @param artisti Elenco degli artisti da visualizzare.
 */
@Composable
fun ArtistisScreenCurrentUser(
    artistsCurrentUser: List<User_Cards_Artisti>,
    listOfCardsArtistsOfferedByCurrentUser: SnapshotStateList<User_Cards_Artisti>,
) {
    // Visualizza una griglia di carte per gli artisti
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        // Itera attraverso gli artisti e visualizza una carta per ciascuno
        items(artistsCurrentUser.size) { index ->
            ArtistsCardOffer(
                artistsCurrentUser[index],
                listOfCardsArtistsOfferedByCurrentUser
            )
        }
    }
}



/**
 * Composable per la visualizzazione dei brani nella schermata dove l'utente corrente deve scegliere la carta da richiedere all'amico.
 * @param brani Elenco dei brani da visualizzare.
 */
@Composable
fun TracksScreen(
    tracks: List<User_Cards_Track>,
    requestCardTrack: User_Cards_Track,
    onUpdateRequestCardTrack: (User_Cards_Track) -> Unit,
    thereIsRequestCard: Boolean,
    onUpdateRequestCardChange: (Boolean) -> Unit,
    typeOfRequestCard: String,
    onUpdateTypeOfRequestCardChange: (String) -> Unit
) {
    // Visualizza una griglia di carte per i brani
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        // Itera attraverso i brani e visualizza una carta per ciascuno
        items(tracks.size) { index ->
            TracksCard(
                tracks[index],
                Modifier.height(8.dp),
                requestCardTrack,
                onUpdateRequestCardTrack,
                thereIsRequestCard,
                onUpdateRequestCardChange,
                typeOfRequestCard,
                onUpdateTypeOfRequestCardChange
            )
        }
    }
}


/**
 * Composable per la visualizzazione dei brani nella schermata dove l'utente corrente deve scegliere la carta da richiedere all'amico.
 * @param brani Elenco dei brani da visualizzare.
 */
@Composable
fun TracksScreenCurrentUser(
    tracksCurrentUser: List<User_Cards_Track>,
    listOfCardsTracksOfferedByCurrentUser: SnapshotStateList<User_Cards_Track>,
) {
    // Visualizza una griglia di carte per i brani
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        // Itera attraverso i brani e visualizza una carta per ciascuno
        items(tracksCurrentUser.size) { index ->
            TracksCardOffer(
                tracksCurrentUser[index],
                listOfCardsTracksOfferedByCurrentUser
            )
        }
    }
}



/**
 * Composable per la selezione di una carta di un artista che si vuole richiedere ad un utente amico.
 * @param artista Oggetto [Artisti] rappresentante l'artista da visualizzare.
 * @param height Modificatore per la altezza della carta.
 */
@Composable
fun ArtistsCard(
    artistSelected: User_Cards_Artisti,
    height: Modifier, requestCardArtist: User_Cards_Artisti,
    onUpdateRequestCardArtist: (User_Cards_Artisti) -> Unit,
    thereIsRequestCard: Boolean,
    onUpdateRequestCardChange: (Boolean) -> Unit,
    typeOfRequestCard: String,
    onUpdateTypeOfRequestCardChange: (String) -> Unit
) {
    // Carta contenente le informazioni dell'artista
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = artistSelected.nome, style = MaterialTheme.typography.bodyLarge)
            Text(text = artistSelected.genere)
            Text(text = "Pop: ${artistSelected.popolarita}")
            Text(text = "Costo: ${artistSelected.popolarita*10}")
            // Immagine dell'artista con dimensioni specificate
            Image(
                painter = rememberAsyncImagePainter(artistSelected.immagine),
                contentDescription = null,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Button(onClick = {
                // mi salvo la carta artista selezionata dall'utente corrente:
                onUpdateRequestCardArtist(requestCardArtist.copy(
                    identifier = artistSelected.identifier,
                    id_carta = artistSelected.id_carta,
                    genere = artistSelected.genere,
                    immagine = artistSelected.immagine,
                    nome = artistSelected.nome,
                    popolarita = artistSelected.popolarita,
                    email = artistSelected.email
                ))

                onUpdateRequestCardChange(!thereIsRequestCard) // in modo tale che l'interfaccia si auto-aggiornerà sostituendo
                // al posto della "TabRow" la carta selezionata dall'utente corrente.
                onUpdateTypeOfRequestCardChange("artist")
            }) {
                Text("Richiedi")
            }
        }
    }
}

/**
 * Composable per la selezione di una carta di un artista che si vuole offrire ad un utente amico.
 * @param artista Oggetto [Artisti] rappresentante l'artista da visualizzare.
 * @param height Modificatore per la altezza della carta.
 */
@Composable
fun ArtistsCardOffer(
    artistSelected: User_Cards_Artisti,
    listOfCardsArtistsOfferedByCurrentUser: SnapshotStateList<User_Cards_Artisti>
) {

    //////////////////////////////////////////////////////////////////////
    var selectedCardAdded by remember { mutableStateOf(false) }
    var selectedCardAlreadyPresent by remember { mutableStateOf(false) }
    /////////////////////////////////////////////////////////////////////

    // Carta contenente le informazioni dell'artista
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = artistSelected.nome, style = MaterialTheme.typography.bodyLarge)
            Text(text = artistSelected.genere)
            Text(text = "Pop: ${artistSelected.popolarita}")
            Text(text = "Costo: ${artistSelected.popolarita*10}")
            // Immagine dell'artista con dimensioni specificate
            Image(
                painter = rememberAsyncImagePainter(artistSelected.immagine),
                contentDescription = null,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Button(onClick = {

                // BISOGNERA' VERIFICARE SEMPRE QUI ANCHE IL FATTO CHE LA CARTA CORRENTE NON SIA PRESENTE:
                // 1) IN NESSUN'ALTRA OFFERTA FATTA DALL'UTENTE CORRENTE.
                // 2) IN NESSUN'ALTRA OFFERTA RICEVUTA DALL'UTENTE CORRENTE PER LA CARTA CORRENTE.
                // 3) IN NESSUN MAZZO DELL'UTENTE CORRENTE.


                // l'utente clicca sul button della carta corrente per specificare che vuole offire questa carta e quindi questa
                // verrà subito aggiunta a listOfCardsArtistsOfferedByCurrentUser:
                // controllo che la carta selezionata non sia già presente in 'listOfCardsArtistsOfferedByCurrentUser':
                if(!listOfCardsArtistsOfferedByCurrentUser.contains(artistSelected)){
                    listOfCardsArtistsOfferedByCurrentUser.add(artistSelected) // aggiunta
                    selectedCardAdded = true

                }else{
                    selectedCardAlreadyPresent = true
                }
            }) {
                Text("Offer")
            }
        }
    }


    if (selectedCardAdded) {
        AlertDialog(
            onDismissRequest = { selectedCardAdded = false },
            title = { Text(text = "Operation performed successfully!") },
            text = { Text(text = "The selected card has been successfully added to the request!\n" +
                                 "You can go back to see it in the list of cards offered.") },
            confirmButton = {
                TextButton(
                    onClick = { selectedCardAdded = false }
                ) {
                    Text("OK")
                }
            }
        )
    }

    if (selectedCardAlreadyPresent) {
        AlertDialog(
            onDismissRequest = { selectedCardAlreadyPresent = false },
            title = { Text(text = "Error") },
            text = { Text(text = "The selected card is already present in the list of cards offered!") },
            confirmButton = {
                TextButton(
                    onClick = { selectedCardAlreadyPresent = false }
                ) {
                    Text("OK")
                }
            }
        )
    }

}





/**
 * Composable per la selezione di una carta di un brano che si vuole richiedere ad un utente amico.
 * @param brano Oggetto [Track] rappresentante il brano da visualizzare.
 * @param height Modificatore per la altezza della carta.
 */
@Composable
fun TracksCard(
    trackSelected: User_Cards_Track,
    height: Modifier,
    requestCardATrack: User_Cards_Track,
    onUpdateRequestCardTrack: (User_Cards_Track) -> Unit,
    thereIsRequestCard: Boolean,
    onUpdateRequestCardChange: (Boolean) -> Unit,
    typeOfRequestCard: String,
    onUpdateTypeOfRequestCardChange: (String) -> Unit
) {
    // Carta contenente le informazioni del brano
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = trackSelected.nome, style = MaterialTheme.typography.bodyLarge)
            Text(text = trackSelected.anno_pubblicazione)
            Text(text = trackSelected.durata)
            Text(text = "Pop: ${trackSelected.popolarita}")
            Text(text = "Costo: ${trackSelected.popolarita*10}")
            // Immagine del brano con dimensioni specificate
            Image(
                painter = rememberAsyncImagePainter(trackSelected.immagine),
                contentDescription = null,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Button(onClick = {
                // mi salvo la carta brano selezionata dall'utente corrente:
                onUpdateRequestCardTrack(requestCardATrack.copy(
                    identifire = trackSelected.identifire,
                    id_carta = trackSelected.id_carta,
                    anno_pubblicazione = trackSelected.anno_pubblicazione,
                    durata = trackSelected.durata,
                    immagine = trackSelected.immagine,
                    nome = trackSelected.nome,
                    popolarita = trackSelected.popolarita,
                    email = trackSelected.email
                ))

                onUpdateRequestCardChange(!thereIsRequestCard) // in modo tale che l'interfaccia si auto-aggiornerà sostituendo
                // al posto della "TabRow" la carta selezionata dall'utente corrente.
                onUpdateTypeOfRequestCardChange("track")
            }) {
                Text("Richiedi")
            }
        }
    }
}


/**
 * Composable per la selezione di una carta di un brano che si vuole richiedere ad un utente amico.
 * @param brano Oggetto [Track] rappresentante il brano da visualizzare.
 * @param height Modificatore per la altezza della carta.
 */
@Composable
fun TracksCardOffer(
    trackSelected: User_Cards_Track,
    listOfCardsTracksOfferedByCurrentUser: SnapshotStateList<User_Cards_Track>
) {

    //////////////////////////////////////////////////////////////////////
    var selectedCardAdded by remember { mutableStateOf(false) }
    var selectedCardAlreadyPresent by remember { mutableStateOf(false) }
    /////////////////////////////////////////////////////////////////////

    // Carta contenente le informazioni del brano
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = trackSelected.nome, style = MaterialTheme.typography.bodyLarge)
            Text(text = trackSelected.anno_pubblicazione)
            Text(text = trackSelected.durata)
            Text(text = "Pop: ${trackSelected.popolarita}")
            Text(text = "Costo: ${trackSelected.popolarita*10}")
            // Immagine del brano con dimensioni specificate
            Image(
                painter = rememberAsyncImagePainter(trackSelected.immagine),
                contentDescription = null,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Button(onClick = {


                // BISOGNERA' VERIFICARE SEMPRE QUI ANCHE IL FATTO CHE LA CARTA CORRENTE NON SIA PRESENTE:
                // 1) IN NESSUN'ALTRA OFFERTA FATTA DALL'UTENTE CORRENTE.
                // 2) IN NESSUN'ALTRA OFFERTA RICEVUTA DALL'UTENTE CORRENTE PER LA CARTA CORRENTE.
                // 3) IN NESSUN MAZZO DELL'UTENTE CORRENTE.


                // l'utente clicca sul button della carta corrente per specificare che vuole offire questa carta e quindi questa
                // verrà subito aggiunta a listOfCardsTracksOfferedByCurrentUser:
                // controllo che la carta selezionata non sia già presente in 'listOfCardsTracksOfferedByCurrentUser':
                if(!listOfCardsTracksOfferedByCurrentUser.contains(trackSelected)){
                    listOfCardsTracksOfferedByCurrentUser.add(trackSelected) // aggiunta
                    selectedCardAdded = true
                }else{
                    selectedCardAlreadyPresent = true
                }
            }) {
                Text("Offer")
            }
        }
    }

    if (selectedCardAdded) {
        AlertDialog(
            onDismissRequest = { selectedCardAdded = false },
            title = { Text(text = "Operation performed successfully!") },
            text = { Text(text = "The selected card has been successfully added to the request!\n" +
                                 "You can go back to see it in the list of cards offered.") },
            confirmButton = {
                TextButton(
                    onClick = { selectedCardAdded = false }
                ) {
                    Text("OK")
                }
            }
        )
    }

    if (selectedCardAlreadyPresent) {
        AlertDialog(
            onDismissRequest = { selectedCardAlreadyPresent = false },
            title = { Text(text = "Error") },
            text = { Text(text = "The selected card is already present in the list of cards offered!") },
            confirmButton = {
                TextButton(
                    onClick = { selectedCardAlreadyPresent = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
}




// ADESSO DEVI CREARE QUESTO COMPOSABLE (TOGLI IL BUTTON BACK CHE NON SERVE..) in modo tale
// che l'utente possa visualizzare le sue carte artisti/brani e selezionare quello da inserire nell'offerta!
@Composable
fun AddCardComposable(
    cardsViewModel: CardsViewModel,
    onBack: () -> Unit,
    listOfCardsArtistsOfferedByCurrentUser: SnapshotStateList<User_Cards_Artisti>,
    listOfCardsTracksOfferedByCurrentUser: SnapshotStateList<User_Cards_Track>
) {

    // richiesta aggiornamento carte artisti/brani dell'utente:
    cardsViewModel.getallcards()
    ////////////////////////////////////////////

    // caricamento carte dell'utente corrente:
    val artistsUser by cardsViewModel.acquiredCardsA.collectAsState(emptyList()) // lista artisti dell'utente corrente (Flow<List<User_Cards_Track>>)
    val tracksUser by cardsViewModel.acquiredCardsT.collectAsState(emptyList()) // lista brani dell'utente corrente (Flow<List<User_Cards_Track>>)
    ////////////////////////////////////////////

    var selectedTab by remember { mutableStateOf(0) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Select your cards that you want to include in the offer:")

        // Adesso eseguo la ricerca in base a dove l'utente ha inserito la spunta:

        TabRow(selectedTabIndex = selectedTab, modifier = Modifier.fillMaxWidth()) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("Your artists")
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("Your tracks")
            }
        }

        // Visualizza la lista degli artisti o delle tracce dell'utente corrente in base alla scheda selezionata
        when (selectedTab) {
            0 -> artistsUser?.let {
                ArtistisScreenCurrentUser(
                    it,
                    listOfCardsArtistsOfferedByCurrentUser
                )

            }

            1 -> tracksUser?.let {
                TracksScreenCurrentUser(
                    it,
                    listOfCardsTracksOfferedByCurrentUser
                )
            }
        }
    }
}




/*
 - Composable che permette all'utente loggato di poter visualizzare tutte le offerte di scambi ricevute e
   di poter decidere quali accettare e quali rifiutare.
*/
@Composable
fun OffersReceived(
    navController: NavController,
    exchangeManagementCardsViewModel: ExchangeManagementCardsViewModel,
    infoUserCurrent: User?){

    if (infoUserCurrent != null) {
        exchangeManagementCardsViewModel.getOffersReceveidByCurrentUser(infoUserCurrent.nickname)
    }
    val reqReceivedCurrentUser by exchangeManagementCardsViewModel.allOffersReceivedByCurrentUser.collectAsState(null)
    Log.d("OffersReceived", "reqReceivedCurrentUser: ${reqReceivedCurrentUser}")

    var selectedShowReceivedOffer by exchangeManagementCardsViewModel.selectedShowReceivedOffer

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))
        if (reqReceivedCurrentUser.isNullOrEmpty()) {
            Text(
                text = "No friends yet",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(reqReceivedCurrentUser!!) { offerReceived ->
                    if (infoUserCurrent != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Icona
                            Icon(
                                imageVector = Icons.Default.Person, // icona specifica dell'utente amico dell'utente corrente
                                contentDescription = "User Icon",
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(24.dp)
                            )
                            // Prendo il Nickname dell'utente amico che ha inviato la richiesta:
                            Text(
                                text = offerReceived.nicknameU1,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 4.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            // Icone cliccabili
                            IconButton(onClick = {
                                // quando clicco su questa icona, viene mostrata all'utente l'offerta ricevuta:
                                exchangeManagementCardsViewModel.updateSelectedShowReceivedOffer(offerReceived) // memorizzo la richiesta che l'utente vuole visualizzare
                                navController.navigate("ShowOfferReceived")
                            }) {
                                Icon(imageVector = Icons.Default.Markunread, contentDescription = "ShowOffer")
                            }
                            IconButton(onClick = {
                                // quando clicco su questa icona, viene chiesto all'utente se è sicuro di voler accettare
                                // l'offerta ricevuta:
                            }) {
                                Icon(imageVector = Icons.Default.MarkEmailRead, contentDescription = "AcceptOffer")
                            }
                            IconButton(onClick = {
                                // quando clicco su questa icona, viene chiesto all'utente se è sicuro di voler rifiutare
                                // l'offerta ricevuta:
                                //showDialogToDeleteFriendship = true
                            }) {
                                Icon(imageVector = Icons.Default.DeleteForever, contentDescription = "RejectOffer")
                            }
                        }


//                        if(showDialogToDeleteFriendship){
//                            AlertDialog(
//                                onDismissRequest = { showDialogToDeleteFriendship = false },
//                                title = { Text(text = "Delete Friendship") },
//                                text = { Text(text = "Do you really want to remove this user from your friends list?") },
//                                confirmButton = {
//                                    TextButton(
//                                        onClick = {
//                                            handleFriendsViewModel.deleteFriendship(infoUserCurrent.email, friendUser.email)
//                                            showDialogToDeleteFriendship = false
//                                            showDialogToConfirmDeleteFriendship = true
//                                        }
//                                    ) {
//                                        Text("Yes")
//                                    }
//                                },
//                                dismissButton = {
//                                    TextButton(onClick = { showDialogToDeleteFriendship = false }) {
//                                        Text("No")
//                                    }
//                                }
//                            )
//                        }
                    }
                    Divider(
                        color = Color.Black,
                        thickness = 2.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }

}



@Composable
fun ShowOfferReceived(navigationController: NavController, exchangeManagementCardsViewModel: ExchangeManagementCardsViewModel){

    var selectedShowReceivedOffer by exchangeManagementCardsViewModel.selectedShowReceivedOffer
    Log.d("ShowOfferReceived", "L'offerta che l'utente vuole visualizzare è la seguente: ${selectedShowReceivedOffer}")

    // RIPRENDI DA QUI..
}



