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
import androidx.compose.material3.ScrollableTabRow
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
        ),
        ///////////////////////////////////////////

        // aggiungere TabItem "Offers sent" per visualizzare le offerte di scambi inviate agli amici:
        TabItem(
            title = "Offers sent",
            unselectedIcon = Icons.Outlined.Pending,
            selectedIcon = Icons.Filled.Pending
        )
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
                    3 -> OffersReceived(navController, exchangeManagementCardsViewModel, infoUserCurrent, loginViewModel, cardsViewModel)
                    4 -> OffersSent(navController, exchangeManagementCardsViewModel, infoUserCurrent)
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

    var nicknameUserCurrent by exchangeManagementCardsViewModel.nicknameUserCurrent
    var nicknameUserRequestCard by exchangeManagementCardsViewModel.nicknameUserRequestCard


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
                                nicknameUserCurrent = infoUserCurrent.nickname
                                nicknameUserRequestCard = friendUser.nickname
                                navController.navigate("exchangeCards")
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
 - Composable che permette di creare l'offerta che l'utente loggato invierà ad un altro utente (suo amico).
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
    var requestCardArtist by remember { mutableStateOf(User_Cards_Artisti(identifier = -1, id_carta = "", genere = "", immagine = "", nome = "", popolarita = -1, email = "", onMarket = false)) }
    var requestCardTrack by remember { mutableStateOf(User_Cards_Track(identifire = -1, id_carta = "", anno_pubblicazione = "", durata = "", immagine = "", nome = "", popolarita = -1, email = "", onMarket = false)) }
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
                    listOfCardsTracksOfferedByCurrentUser,
                    exchangeManagementCardsViewModel,
                    loginViewModel
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
    onRemoveClick: () -> Unit,
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
- Composable per la visualizzazione delle carte 'Artista' inserite nell'offerta senza la possibilità
  di eliminare tale carta.
 */
@Composable
fun OfferedCardArtist(
    card: User_Cards_Artisti
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
- Composable per la visualizzazione delle carte 'Brano' inserite nell'offerta senza la possibilità
  di eliminare tale carta.
 */
@Composable
fun OfferedCardTrack(
    card: User_Cards_Track
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
        }
    }
}



/**
 * Composable per la visualizzazione degli artisti nella schermata dove l'utente corrente deve scegliere la carta 'artista' da richiedere all'amico.
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
 * @param artistsCurrentUser Elenco degli artisti dell'utente corrente da visualizzare.
 */
@Composable
fun ArtistisScreenCurrentUser(
    artistsCurrentUser: List<User_Cards_Artisti>,
    listOfCardsArtistsOfferedByCurrentUser: SnapshotStateList<User_Cards_Artisti>,
    exchangeManagementCardsViewModel: ExchangeManagementCardsViewModel,
    loginViewModel: LoginViewModel
) {
    // Visualizza una griglia di carte per gli artisti
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        // Itera attraverso gli artisti e visualizza una carta per ciascuno
        items(artistsCurrentUser.size) { index ->
            ArtistsCardOffer(
                artistsCurrentUser[index],
                listOfCardsArtistsOfferedByCurrentUser,
                exchangeManagementCardsViewModel,
                loginViewModel
            )
        }
    }
}



/**
 * Composable per la visualizzazione dei brani nella schermata dove l'utente corrente deve scegliere la carta 'brano' da richiedere all'amico.
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
    exchangeManagementCardsViewModel: ExchangeManagementCardsViewModel,
    loginViewModel: LoginViewModel
) {
    // Visualizza una griglia di carte per i brani
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        // Itera attraverso i brani e visualizza una carta per ciascuno
        items(tracksCurrentUser.size) { index ->
            TracksCardOffer(
                tracksCurrentUser[index],
                listOfCardsTracksOfferedByCurrentUser,
                exchangeManagementCardsViewModel,
                loginViewModel
            )
        }
    }
}



/**
 * Composable per la selezione di una carta di un artista che si vuole richiedere ad un utente amico.
 * @param artistSelected Oggetto [Artisti] rappresentante l'artista da visualizzare.
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
 * @param artistSelected Oggetto [Artisti] rappresentante l'artista selezionato dall'utente e che quindi vorrebbe offrire al suo amico.
 */
@Composable
fun ArtistsCardOffer(
    artistSelected: User_Cards_Artisti,
    listOfCardsArtistsOfferedByCurrentUser: SnapshotStateList<User_Cards_Artisti>,
    exchangeManagementCardsViewModel: ExchangeManagementCardsViewModel,
    loginViewModel: LoginViewModel
) {

    ////////////////////////////////////////////////////////////////////////
    val infoUserCurrent by loginViewModel.userLoggedInfo.collectAsState(initial = null) // info utente che ha inviato l'offerta corrente e che è loggato in questo momento
    val reqSentCurrentUser by exchangeManagementCardsViewModel.allOffersSentByCurrentUser.collectAsState(null) // contiene tutte le richieste inviate dall'utente corrente
    val reqReceivedCurrentUser by exchangeManagementCardsViewModel.allOffersReceivedByCurrentUser.collectAsState(null) // contiene tutte le richieste ricevute dall'utente corrente
    ////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////
    // - Aggiorno tutte le offerte inviate e ricevute dall'utente corrente:
    infoUserCurrent?.let {
        exchangeManagementCardsViewModel.getOffersSentByCurrentUser(it.nickname) // Aggiorno tutte le offerte inviate dall'utente corrente (reqSentCurrentUser)
        exchangeManagementCardsViewModel.getOffersReceveidByCurrentUser(it.nickname) // Aggiorno tutte le offerte ricevute dall'utente corrente (reqReceivedCurrentUser)
        Log.d("ShowOfferSent", "Nickname utente corrente: ${it.nickname}")
    }
    ////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////
    var selectedCardAdded by remember { mutableStateOf(false) }
    var selectedCardAlreadyPresent by remember { mutableStateOf(false) }
    /////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////
    // per le finestre di dialogo:
    val requestedCardAlreadyPresentAnotherOfferSent = remember { mutableStateOf(false) }
    val requestedCardAlreadyPresentAnotherOfferReceived = remember { mutableStateOf(false) }
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

                // Controllo 1):
                ////////////////////////////////////////////////////////////////////////////////
                // 1) IN UNA QUALCHE OFFERTA INVIATA DALL'UTENTE CORRENTE..
                // SCORRO LA LISTA DI OFFERTE INVIATE DALL'UTENTE CORRENTE E SE C'E' ANCHE SOLO UNA CHE CONTIENE L'ID DELLA CARTA CORRENTE
                // ALLORA MOSTRA ERRORE.. ALTRIMENTI SI PUO' ESEGUIRE IL CONTROLLO 2)
                reqSentCurrentUser?.let { listOfSentOffers ->
                    if (listOfSentOffers.isNotEmpty()) {
                        // Itera su tutti gli elementi
                        for ((index, SentOffer) in listOfSentOffers.withIndex()) {
                            Log.d(
                                "ArtistsCardOffer",
                                "Offerta inviata dall'utente corrente: ${SentOffer}"
                            )
                            // Adesso posso accedere ai campi di ogni offerta come offer.nicknameU1, offer.nicknameU2, ecc..
                            if (SentOffer.listOfferedCards.isNotEmpty()) {
                                // scorro tutti gli ids delle carte offerte in questa offerta:
                                for (i in SentOffer.listOfferedCards.indices) {

                                    // se l'id della carta corrente (ovvero 'artistSelected.id_carta')
                                    // è uguale all'id della carta offerta corrente, allora questa carta selezionata non
                                    // può essere inserita perchè l'utente corrente l'ha già inserita in un'altra offerta
                                    // pendente:
                                    if (artistSelected.id_carta == SentOffer.listOfferedCards[i]) {

                                        Log.d(
                                            "ArtistsCardOffer",
                                            "La Carta richiesta in questa offerta è stata trovata in un'altra offerta inviata dall'utente corrente."
                                        )
                                        Log.d(
                                            "ArtistsCardOffer",
                                            "artistSelected.id_carta: ${artistSelected.id_carta}"
                                        )
                                        Log.d(
                                            "ArtistsCardOffer",
                                            "SentOffer.listOfferedCards[i]: ${SentOffer.listOfferedCards[i]}"
                                        )
                                        requestedCardAlreadyPresentAnotherOfferSent.value = true
                                    }
                                }
                            }
                        }
                    }
                }
                ////////////////////////////////////////////////////////////////////////////////


                // Controllo 2):
                ////////////////////////////////////////////////////////////////////////////////
                // 2) IN UNA QUALCHE OFFERTA RICEVUTA DALL'UTENTE CORRENTE..
                // SCORRI LA LISTA DI OFFERTE INVIATE DALL'UTENTE CORRENTE E SE C'E' ANCHE SOLO UNA CHE CONTIENE L'ID DELLA CARTA CORRENTE
                // ALLORA MOSTRA ERRORE dicendo all'utente che prima deve rifiutare le altre offerte.. ALTRIMENTI SI PUO' ESEGUIRE IL CONTROLLO 3)
                reqReceivedCurrentUser?.let { listOfReceivedOffers ->
                    if (listOfReceivedOffers.isNotEmpty()) {
                        // Itera su tutti gli elementi
                        for ((index, ReceivedOffer) in listOfReceivedOffers.withIndex()) {
                            Log.d(
                                "ArtistsCardOffer",
                                "Offerta ricevuta dall'utente corrente: ${ReceivedOffer}"
                            )
                            // Adesso posso accedere ai campi di ogni offerta come offer.nicknameU1, offer.nicknameU2, ecc..
                            if (artistSelected.id_carta == ReceivedOffer.idRequiredCard) {
                                Log.d(
                                    "ArtistsCardOffer",
                                    "La Carta richiesta in questa offerta è stata trovata in un'altra offerta ricevuta dall'utente corrente."
                                )
                                Log.d(
                                    "ArtistsCardOffer",
                                    "artistSelected.id_carta: ${artistSelected.id_carta}"
                                )
                                Log.d(
                                    "ArtistsCardOffer",
                                    "ReceivedOffer.idRequiredCard: ${ReceivedOffer.idRequiredCard}"
                                )
                                requestedCardAlreadyPresentAnotherOfferReceived.value = true
                            }
                        }
                    }
                }
                ////////////////////////////////////////////////////////////////////////////////


                // Il controllo 3) da inserire appena Pietro completa i mazzi..

                if((!requestedCardAlreadyPresentAnotherOfferSent.value) && (!requestedCardAlreadyPresentAnotherOfferReceived.value)){
                    // l'utente clicca sul button della carta corrente per specificare che vuole offire questa carta e quindi questa
                    // verrà subito aggiunta a listOfCardsArtistsOfferedByCurrentUser:
                    // controllo che la carta selezionata non sia già presente in 'listOfCardsArtistsOfferedByCurrentUser':
                    if(!listOfCardsArtistsOfferedByCurrentUser.contains(artistSelected)){
                        listOfCardsArtistsOfferedByCurrentUser.add(artistSelected) // aggiunta
                        selectedCardAdded = true

                    }else{
                        selectedCardAlreadyPresent = true
                    }
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

    if (requestedCardAlreadyPresentAnotherOfferSent.value) {
        AlertDialog(
            onDismissRequest = {
                requestedCardAlreadyPresentAnotherOfferSent.value = false
                //navController.popBackStack()
            },
            title = { Text(text = "Error") },
            text = {
                Text(
                    text = "The requested card is already present in another offer you have sent! \n\n" +
                            "Before carrying out this operation you must first delete all the offers sent in which you have inserted this card."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        requestedCardAlreadyPresentAnotherOfferSent.value = false
                        //navController.popBackStack()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    if (requestedCardAlreadyPresentAnotherOfferReceived.value) {
        AlertDialog(
            onDismissRequest = {
                requestedCardAlreadyPresentAnotherOfferReceived.value = false
                //navController.popBackStack()
            },
            title = { Text(text = "Error") },
            text = {
                Text(
                    text = "The requested card is already present in another offer you have received! \n\n" +
                            "Before carrying out this operation you must first reject all the offers received in which this card was requested."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        requestedCardAlreadyPresentAnotherOfferReceived.value = false
                        //navController.popBackStack()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}





/**
 * Composable per la selezione di una carta di un brano che si vuole richiedere ad un utente amico.
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
 */
@Composable
fun TracksCardOffer(
    trackSelected: User_Cards_Track,
    listOfCardsTracksOfferedByCurrentUser: SnapshotStateList<User_Cards_Track>,
    exchangeManagementCardsViewModel: ExchangeManagementCardsViewModel,
    loginViewModel: LoginViewModel
) {

    ////////////////////////////////////////////////////////////////////////
    val infoUserCurrent by loginViewModel.userLoggedInfo.collectAsState(initial = null) // info utente che ha inviato l'offerta corrente e che è loggato in questo momento
    val reqSentCurrentUser by exchangeManagementCardsViewModel.allOffersSentByCurrentUser.collectAsState(null) // contiene tutte le richieste inviate dall'utente corrente
    val reqReceivedCurrentUser by exchangeManagementCardsViewModel.allOffersReceivedByCurrentUser.collectAsState(null) // contiene tutte le richieste ricevute dall'utente corrente
    ////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////
    // - Aggiorno tutte le offerte inviate e ricevute dall'utente corrente:
    infoUserCurrent?.let {
        exchangeManagementCardsViewModel.getOffersSentByCurrentUser(it.nickname) // Aggiorno tutte le offerte inviate dall'utente corrente (reqSentCurrentUser)
        exchangeManagementCardsViewModel.getOffersReceveidByCurrentUser(it.nickname) // Aggiorno tutte le offerte ricevute dall'utente corrente (reqReceivedCurrentUser)
        Log.d("ShowOfferSent", "Nickname utente corrente: ${it.nickname}")
    }
    ////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////
    var selectedCardAdded by remember { mutableStateOf(false) }
    var selectedCardAlreadyPresent by remember { mutableStateOf(false) }
    /////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////
    // per le finestre di dialogo:
    val requestedCardAlreadyPresentAnotherOfferSent = remember { mutableStateOf(false) }
    val requestedCardAlreadyPresentAnotherOfferReceived = remember { mutableStateOf(false) }
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

                // Controllo 1):
                ////////////////////////////////////////////////////////////////////////////////
                // 1) IN UNA QUALCHE OFFERTA INVIATA DALL'UTENTE CORRENTE..
                // SCORRO LA LISTA DI OFFERTE INVIATE DALL'UTENTE CORRENTE E SE C'E' ANCHE SOLO UNA CHE CONTIENE L'ID DELLA CARTA CORRENTE
                // ALLORA MOSTRA ERRORE.. ALTRIMENTI SI PUO' ESEGUIRE IL CONTROLLO 2)
                reqSentCurrentUser?.let { listOfSentOffers ->
                    if (listOfSentOffers.isNotEmpty()) {
                        // Itera su tutti gli elementi
                        for ((index, SentOffer) in listOfSentOffers.withIndex()) {
                            Log.d(
                                "ArtistsCardOffer",
                                "Offerta inviata dall'utente corrente: ${SentOffer}"
                            )
                            // Adesso posso accedere ai campi di ogni offerta come offer.nicknameU1, offer.nicknameU2, ecc..
                            if (SentOffer.listOfferedCards.isNotEmpty()) {
                                // scorro tutti gli ids delle carte offerte in questa offerta:
                                for (i in SentOffer.listOfferedCards.indices) {

                                    // se l'id della carta corrente (ovvero 'artistSelected.id_carta')
                                    // è uguale all'id della carta offerta corrente, allora questa carta selezionata non
                                    // può essere inserita perchè l'utente corrente l'ha già inserita in un'altra offerta
                                    // pendente:
                                    if (trackSelected.id_carta == SentOffer.listOfferedCards[i]) {

                                        Log.d(
                                            "ArtistsCardOffer",
                                            "La Carta richiesta in questa offerta è stata trovata in un'altra offerta inviata dall'utente corrente."
                                        )
                                        Log.d(
                                            "ArtistsCardOffer",
                                            "trackSelected.id_carta: ${trackSelected.id_carta}"
                                        )
                                        Log.d(
                                            "ArtistsCardOffer",
                                            "SentOffer.listOfferedCards[i]: ${SentOffer.listOfferedCards[i]}"
                                        )
                                        requestedCardAlreadyPresentAnotherOfferSent.value = true
                                    }
                                }
                            }
                        }
                    }
                }
                ////////////////////////////////////////////////////////////////////////////////


                // Controllo 2):
                ////////////////////////////////////////////////////////////////////////////////
                // 2) IN UNA QUALCHE OFFERTA RICEVUTA DALL'UTENTE CORRENTE..
                // SCORRI LA LISTA DI OFFERTE INVIATE DALL'UTENTE CORRENTE E SE C'E' ANCHE SOLO UNA CHE CONTIENE L'ID DELLA CARTA CORRENTE
                // ALLORA MOSTRA ERRORE dicendo all'utente che prima deve rifiutare le altre offerte.. ALTRIMENTI SI PUO' ESEGUIRE IL CONTROLLO 3)
                reqReceivedCurrentUser?.let { listOfReceivedOffers ->
                    if (listOfReceivedOffers.isNotEmpty()) {
                        // Itera su tutti gli elementi
                        for ((index, ReceivedOffer) in listOfReceivedOffers.withIndex()) {
                            Log.d(
                                "ArtistsCardOffer",
                                "Offerta ricevuta dall'utente corrente: ${ReceivedOffer}"
                            )
                            // Adesso posso accedere ai campi di ogni offerta come offer.nicknameU1, offer.nicknameU2, ecc..
                            if (trackSelected.id_carta == ReceivedOffer.idRequiredCard) {
                                Log.d(
                                    "ArtistsCardOffer",
                                    "La Carta richiesta in questa offerta è stata trovata in un'altra offerta ricevuta dall'utente corrente."
                                )
                                Log.d(
                                    "ArtistsCardOffer",
                                    "trackSelected.id_carta: ${trackSelected.id_carta}"
                                )
                                Log.d(
                                    "ArtistsCardOffer",
                                    "ReceivedOffer.idRequiredCard: ${ReceivedOffer.idRequiredCard}"
                                )
                                requestedCardAlreadyPresentAnotherOfferReceived.value = true
                            }
                        }
                    }
                }
                ////////////////////////////////////////////////////////////////////////////////

                if((!requestedCardAlreadyPresentAnotherOfferSent.value) && (!requestedCardAlreadyPresentAnotherOfferReceived.value)){
                    // l'utente clicca sul button della carta corrente per specificare che vuole offire questa carta e quindi questa
                    // verrà subito aggiunta a listOfCardsTracksOfferedByCurrentUser:
                    // controllo che la carta selezionata non sia già presente in 'listOfCardsTracksOfferedByCurrentUser':
                    if(!listOfCardsTracksOfferedByCurrentUser.contains(trackSelected)){
                        listOfCardsTracksOfferedByCurrentUser.add(trackSelected) // aggiunta
                        selectedCardAdded = true
                    }else{
                        selectedCardAlreadyPresent = true
                    }
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

    if (requestedCardAlreadyPresentAnotherOfferSent.value) {
        AlertDialog(
            onDismissRequest = {
                requestedCardAlreadyPresentAnotherOfferSent.value = false
                //navController.popBackStack()
            },
            title = { Text(text = "Error") },
            text = {
                Text(
                    text = "The requested card is already present in another offer you have sent! \n\n" +
                            "Before carrying out this operation you must first delete all the offers sent in which you have inserted this card."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        requestedCardAlreadyPresentAnotherOfferSent.value = false
                        //navController.popBackStack()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    if (requestedCardAlreadyPresentAnotherOfferReceived.value) {
        AlertDialog(
            onDismissRequest = {
                requestedCardAlreadyPresentAnotherOfferReceived.value = false
                //navController.popBackStack()
            },
            title = { Text(text = "Error") },
            text = {
                Text(
                    text = "The requested card is already present in another offer you have received! \n\n" +
                            "Before carrying out this operation you must first reject all the offers received in which this card was requested."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        requestedCardAlreadyPresentAnotherOfferReceived.value = false
                        //navController.popBackStack()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}




/*
 - Composable che permette all'utente loggato di poter aggiungere una carta artista/brano all'interno di un'offerta
   che vuole mandare ad un suo amico.
*/
@Composable
fun AddCardComposable(
    cardsViewModel: CardsViewModel,
    onBack: () -> Unit,
    listOfCardsArtistsOfferedByCurrentUser: SnapshotStateList<User_Cards_Artisti>,
    listOfCardsTracksOfferedByCurrentUser: SnapshotStateList<User_Cards_Track>,
    exchangeManagementCardsViewModel: ExchangeManagementCardsViewModel,
    loginViewModel: LoginViewModel
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
                    listOfCardsArtistsOfferedByCurrentUser,
                    exchangeManagementCardsViewModel,
                    loginViewModel
                )

            }

            1 -> tracksUser?.let {
                TracksScreenCurrentUser(
                    it,
                    listOfCardsTracksOfferedByCurrentUser,
                    exchangeManagementCardsViewModel,
                    loginViewModel
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
    infoUserCurrent: User?,
    loginViewModel: LoginViewModel,
    cardsViewModel: CardsViewModel
    ){

    if (infoUserCurrent != null) {
        exchangeManagementCardsViewModel.getOffersReceveidByCurrentUser(infoUserCurrent.nickname)
        exchangeManagementCardsViewModel.getOffersSentByCurrentUser(infoUserCurrent.nickname)
    }

    /////////////////////////////////////////////////////////////////////
    val reqSentCurrentUser by exchangeManagementCardsViewModel.allOffersSentByCurrentUser.collectAsState(null) // contiene tutte le richieste inviate dall'utente corrente
    val reqReceivedCurrentUser by exchangeManagementCardsViewModel.allOffersReceivedByCurrentUser.collectAsState(null) // contiene tutte le richieste ricevute dall'utente corrente
    val utilityFriendInfo by loginViewModel.utilityFriendInfo.collectAsState() // info utente che ha fatto l'offerta corrente
    val listAllInfoAboutCardsArtistOffered by cardsViewModel.listAllInfoAboutCardsArtistOffered.collectAsState(null)
    val listAllInfoAboutCardsTracksOffered by cardsViewModel.listAllInfoAboutCardsTracksOffered.collectAsState(null)
    /////////////////////////////////////////////////////////////////////

    Log.d("OffersReceived", "reqReceivedCurrentUser: ${reqReceivedCurrentUser}")

    var selectedShowReceivedOffer by exchangeManagementCardsViewModel.selectedShowReceivedOffer


    /////////////////////////////////////////////////////////////////////
    // per le finestre di dialogo:
    val confirmAcceptRequest = remember { mutableStateOf(false) }
    val confirmRejectRequest = remember { mutableStateOf(false) }
    val requestedCardAlreadyPresentAnotherOfferSent = remember { mutableStateOf(false) }
    val requestedCardAlreadyPresentAnotherOfferReceived = remember { mutableStateOf(false) }
    var yesAccept = remember { mutableStateOf(false) }
    var successfullyAccept = remember { mutableStateOf(false) }
    val successfullyReject = remember { mutableStateOf(false) }
    /////////////////////////////////////////////////////////////////////

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))
        if (reqReceivedCurrentUser.isNullOrEmpty()) {
            Text(
                text = "No offers received",
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
                                confirmAcceptRequest.value = true
                            }) {
                                Icon(imageVector = Icons.Default.MarkEmailRead, contentDescription = "AcceptOffer")
                            }
                            IconButton(onClick = {
                                // quando clicco su questa icona, viene chiesto all'utente se è sicuro di voler rifiutare
                                // l'offerta ricevuta:
                                confirmRejectRequest.value = true
                            }) {
                                Icon(imageVector = Icons.Default.DeleteForever, contentDescription = "RejectOffer")
                            }
                        }
                    }
                    Divider(
                        color = Color.Black,
                        thickness = 2.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    if(confirmAcceptRequest.value){
                        AlertDialog(
                            onDismissRequest = { confirmAcceptRequest.value = false },
                            title = { Text(text = "Accept request") },
                            text = { Text(text = "Do you really want to accept this offer?") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        confirmAcceptRequest.value = false
                                        yesAccept.value = true

                                        Log.d("OffersReceived", "confirmAcceptRequest.value: ${confirmAcceptRequest.value}")
                                        Log.d("OffersReceived", "yesAccept.value: ${yesAccept.value}")
                                    }
                                ) {
                                    Text("Yes")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { confirmAcceptRequest.value = false }) {
                                    Text("No")
                                }
                            }
                        )
                    }

                    if(yesAccept.value){

                        // mi prendo subito l'email dell'utente che ha inviato l'offerta (aggiorna 'utilityFriendInfo'):
                        loginViewModel.getFriendByNickname(selectedShowReceivedOffer.nicknameU1)


                        // BISOGNERA' VERIFICARE SEMPRE QUI ANCHE IL FATTO CHE LA CARTA CORRENTE NON SIA PRESENTE:
                        // 1) IN NESSUN'ALTRA OFFERTA FATTA DALL'UTENTE CORRENTE.
                        // 2) IN NESSUN'ALTRA OFFERTA RICEVUTA DALL'UTENTE CORRENTE PER LA CARTA CORRENTE.
                        // 3) IN NESSUN MAZZO DELL'UTENTE CORRENTE.

                        // Controllo 1):
                        ////////////////////////////////////////////////////////////////////////////////
                        // 1) IN UNA QUALCHE OFFERTA INVIATA DALL'UTENTE CORRENTE..
                        // SCORRO LA LISTA DI OFFERTE INVIATE DALL'UTENTE CORRENTE E SE C'E' ANCHE SOLO UNA CHE CONTIENE L'ID DELLA CARTA CORRENTE
                        // ALLORA MOSTRA ERRORE.. ALTRIMENTI SI PUO' ESEGUIRE IL CONTROLLO 2)
                        reqSentCurrentUser?.let { listOfSentOffers ->
                            if (listOfSentOffers.isNotEmpty()) {
                                // Itera su tutti gli elementi
                                for ((index, SentOffer) in listOfSentOffers.withIndex()) {
                                    Log.d(
                                        "OffersReceived",
                                        "Offerta inviata dall'utente corrente: ${SentOffer}"
                                    )
                                    // Adesso posso accedere ai campi di ogni offerta come offer.nicknameU1, offer.nicknameU2, ecc..
                                    if (SentOffer.listOfferedCards.isNotEmpty()) {
                                        // scorro tutti gli ids delle carte offerte in questa offerta:
                                        for (i in SentOffer.listOfferedCards.indices) {

                                            // se l'id della carta richiesta all'utente corrente dell'offerta corrente (ovvero 'offerReceived.idRequiredCard')
                                            // è uguale all'id della carta offerta corrente (ovvero SentOffer.listOfferedCards[i]), allora non sarà possibile
                                            // accettare questa offerta poichè 'offerReceived.idRequiredCard' è stata già inserita in una qualche altra offerta
                                            // fatta dall'utente corrente:
                                            if (offerReceived.idRequiredCard == SentOffer.listOfferedCards[i] &&
                                                offerReceived.id != SentOffer.id) {
                                                Log.d(
                                                    "OffersReceived",
                                                    "La Carta richiesta in questa offerta è stata trovata in un'altra offerta inviata dall'utente corrente."
                                                )
                                                Log.d(
                                                    "OffersReceived",
                                                    "offerReceived.idRequiredCard: ${offerReceived.idRequiredCard}"
                                                )
                                                Log.d(
                                                    "OffersReceived",
                                                    "SentOffer.listOfferedCards[i]: ${SentOffer.listOfferedCards[i]}"
                                                )
                                                yesAccept.value = false
                                                requestedCardAlreadyPresentAnotherOfferSent.value = true
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        ////////////////////////////////////////////////////////////////////////////////


                        // Controllo 2):
                        ////////////////////////////////////////////////////////////////////////////////
                        // 2) IN UNA QUALCHE OFFERTA RICEVUTA DALL'UTENTE CORRENTE..
                        // SCORRO LA LISTA DI OFFERTE RICEVUTE DALL'UTENTE CORRENTE E SE C'E' ANCHE SOLO UNA CHE CONTIENE
                        // L'ID DELLA CARTA RICHIESTA NELL'OFFERTA CORRENTE
                        // ALLORA MOSTRO UN ERRORE dicendo all'utente che prima deve rifiutare le altre offerte nelle quali questa carta richiesta
                        // è presente..
                        // ALTRIMENTI SI PUO' ESEGUIRE IL CONTROLLO 3)
                        reqReceivedCurrentUser?.let { listOfReceivedOffers ->
                            if (listOfReceivedOffers.isNotEmpty()) {
                                // Itera su tutti gli elementi
                                for ((index, ReceivedOffer) in listOfReceivedOffers.withIndex()) {
                                    Log.d(
                                        "OffersReceived",
                                        "Offerta ricevuta dall'utente corrente: ${ReceivedOffer}"
                                    )
                                    // Adesso posso accedere ai campi di ogni offerta come offer.nicknameU1, offer.nicknameU2, ecc..
                                    if (offerReceived.idRequiredCard == ReceivedOffer.idRequiredCard &&
                                        offerReceived.id != ReceivedOffer.id) {
                                        Log.d(
                                            "OffersReceived",
                                            "La Carta richiesta in questa offerta è stata trovata in un'altra offerta ricevuta dall'utente corrente."
                                        )
                                        Log.d(
                                            "OffersReceived",
                                            "offerReceived.idRequiredCard: ${offerReceived.idRequiredCard}"
                                        )
                                        Log.d(
                                            "OffersReceived",
                                            "ReceivedOffer.idRequiredCard: ${ReceivedOffer.idRequiredCard}"
                                        )
                                        yesAccept.value = false
                                        requestedCardAlreadyPresentAnotherOfferReceived.value = true
                                    }
                                }
                            }
                        }
                        ////////////////////////////////////////////////////////////////////////////////

                        // manca il controllo 3) da inserire appena Pietro completa i mazzi..

                        // Se tutti i controlli di sopra vanno a buon fine allora invio feedback all'utente:
                        if((!requestedCardAlreadyPresentAnotherOfferSent.value) &&
                            (!requestedCardAlreadyPresentAnotherOfferReceived.value)){

                            ////////////////////////////////////////////////////////////////////////////////
                            // Aggiorno il proprietario della carta richiesta (diventerà colui che ha inviato l'offerta corrente):
                            if (selectedShowReceivedOffer.typeRequiredCard == "artist") {
                                utilityFriendInfo?.let {
                                    cardsViewModel.updateCardArtistOwner(
                                        it.email,
                                        selectedShowReceivedOffer.idRequiredCard
                                    )
                                    Log.d(
                                        "ShowOfferReceived",
                                        "artist-Nuovo proprietario carta richiesta: ${it.email}"
                                    )
                                }
                            } else {
                                utilityFriendInfo?.let {
                                    cardsViewModel.updateCardTrackOwner(
                                        it.email,
                                        selectedShowReceivedOffer.idRequiredCard
                                    )
                                    Log.d(
                                        "ShowOfferReceived",
                                        "track-Nuovo proprietario carta richiesta: ${it.email}"
                                    )
                                }
                            }
                            ////////////////////////////////////////////////////////////////////////////////

                            ///////////////////////////////////////////////////////////////////////////////////////
                            // Se la 'listAllInfoAboutCardsArtistOffered' NON E' VUOTA, scorro
                            // tutti gli elementi presenti al suo interno e aggiorno il suo proprietario
                            // (diventerà colui che ha ricevuto l'offerta):
                            if (!listAllInfoAboutCardsArtistOffered?.isEmpty()!!) {
                                for (i in listAllInfoAboutCardsArtistOffered!!.indices) {
                                    infoUserCurrent?.let {
                                        cardsViewModel.updateCardArtistOwner(
                                            it.email,
                                            listAllInfoAboutCardsArtistOffered!![i].id_carta
                                        )
                                        Log.d(
                                            "ShowOfferReceived",
                                            "listAllInfoAboutCardsArtistOffered-Nuovo proprietario carta richiesta: ${it.email}"
                                        )
                                    }
                                }
                            }
                            ///////////////////////////////////////////////////////////////////////////////////////

                            ///////////////////////////////////////////////////////////////////////////////////////
                            // Se la 'listAllInfoAboutCardsArtistOffered' NON E' VUOTA, scorro
                            // tutti gli elementi presenti al suo interno e aggiorno il suo proprietario
                            // (diventerà colui che ha ricevuto l'offerta):
                            if (!listAllInfoAboutCardsTracksOffered?.isEmpty()!!) {
                                for (i in listAllInfoAboutCardsTracksOffered!!.indices) {
                                    infoUserCurrent?.let {
                                        cardsViewModel.updateCardTrackOwner(
                                            it.email,
                                            listAllInfoAboutCardsTracksOffered!![i].id_carta
                                        )
                                        Log.d(
                                            "ShowOfferReceived",
                                            "listAllInfoAboutCardsTracksOffered-Nuovo proprietario carta richiesta: ${it.email}"
                                        )
                                    }
                                }
                            }
                            ///////////////////////////////////////////////////////////////////////////////////////

                            // se nell'offerta accettata c'erano dei points offerti allora aggiorno i points togliendoli
                            // all'utente che ha fatto l'offerta e aggiungendoli a colui che ha ricevuto l'offerta:
                            if(selectedShowReceivedOffer.pointsOffered > 0){
                                // eseguo update points:
                                loginViewModel.subtractPoints(selectedShowReceivedOffer.pointsOffered, utilityFriendInfo!!.email)
                                loginViewModel.addPoints(selectedShowReceivedOffer.pointsOffered, infoUserCurrent!!.email)
                            }


                            ///////////////////////////////////////////////////////////////////////////////////////
                            // - Adesso cancello l'offerta dal DB:
                            exchangeManagementCardsViewModel.deleteOffer(
                                selectedShowReceivedOffer.id
                            )
                            ///////////////////////////////////////////////////////////////////////////////////////

                            yesAccept.value = false
                            successfullyAccept.value = true // feedback per l'utente
                        }

                    }

                    if(confirmRejectRequest.value){
                        AlertDialog(
                            onDismissRequest = { confirmRejectRequest.value = false },
                            title = { Text(text = "Reject request") },
                            text = { Text(text = "Do you really want to reject this offer?") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        confirmRejectRequest.value = false

                                        // cancello l'offerta corrente dal DB:
                                        exchangeManagementCardsViewModel.deleteOffer(
                                            selectedShowReceivedOffer.id
                                        )

                                        successfullyReject.value = true // feedback all'utente
                                    }
                                ) {
                                    Text("Yes")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { confirmRejectRequest.value = false }) {
                                    Text("No")
                                }
                            }
                        )
                    }

                    if(successfullyAccept.value){
                        AlertDialog(
                            onDismissRequest = {
                                successfullyAccept.value = false
                                //yesAccept.value = false
                            },
                            title = { Text(text = "Operation performed successfully!") },
                            text = { Text(text = "Offer successfully accepted!") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        successfullyAccept.value = false
                                        //yesAccept.value = false
                                    }
                                ) {
                                    Text("OK")
                                }
                            }
                        )
                    }

                    if(successfullyReject.value){
                        AlertDialog(
                            onDismissRequest = { successfullyReject.value = false },
                            title = { Text(text = "Operation performed successfully!") },
                            text = { Text(text = "Offer successfully rejected!") },
                            confirmButton = {
                                TextButton(
                                    onClick = { successfullyReject.value = false }
                                ) {
                                    Text("OK")
                                }
                            }
                        )
                    }

                    if (requestedCardAlreadyPresentAnotherOfferSent.value) {
                        AlertDialog(
                            onDismissRequest = {
                                requestedCardAlreadyPresentAnotherOfferSent.value = false
                                //navController.popBackStack()
                            },
                            title = { Text(text = "Error") },
                            text = {
                                Text(
                                    text = "The requested card is already present in another offer you have sent! \n\n" +
                                            "Before carrying out this operation you must first delete all the offers sent in which you have inserted this card."
                                )
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        requestedCardAlreadyPresentAnotherOfferSent.value = false
                                        //navController.popBackStack()
                                    }
                                ) {
                                    Text("OK")
                                }
                            }
                        )
                    }

                    if (requestedCardAlreadyPresentAnotherOfferReceived.value) {
                        AlertDialog(
                            onDismissRequest = {
                                requestedCardAlreadyPresentAnotherOfferReceived.value = false
                                //navController.popBackStack()
                            },
                            title = { Text(text = "Error") },
                            text = {
                                Text(
                                    text = "The requested card is already present in another offer you have received! \n\n" +
                                            "Before carrying out this operation you must first reject all the offers received in which this card was requested."
                                )
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        requestedCardAlreadyPresentAnotherOfferReceived.value = false
                                        //navController.popBackStack()
                                    }
                                ) {
                                    Text("OK")
                                }
                            }
                        )
                    }

                }
            }
        }
    }

}


/*
 - Composable che permette all'utente di visualizzare l'offerta ricevuta che ha selezionato nella schermata rappresentata
   dal composable 'OffersReceived'.
*/
@Composable
fun ShowOfferReceived(
    navController: NavController,
    exchangeManagementCardsViewModel: ExchangeManagementCardsViewModel,
    cardsViewModel: CardsViewModel,
    loginViewModel: LoginViewModel
){

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    val infoUserCurrent by loginViewModel.userLoggedInfo.collectAsState(initial = null) // info utente che ha ricevuto l'offerta corrente e che è loggato in questo momento
    var selectedShowReceivedOffer by exchangeManagementCardsViewModel.selectedShowReceivedOffer
    val infoCardArtistRequest by cardsViewModel.infoCardArtistRequest.collectAsState(initial = null) // mi prendo da qui le info della carta che eventualmente sarà di tipo 'artista' richiesta all'utente corrente nell'offerta corrente.
    val infoCardTrackRequest by cardsViewModel.infoCardTrackRequest.collectAsState(initial = null) // mi prendo da qui le info della carta che eventualmente sarà di tipo 'brano'richiesta all'utente corrente nell'offerta corrente.
    val listAllInfoAboutCardsArtistOffered by cardsViewModel.listAllInfoAboutCardsArtistOffered.collectAsState(null)
    val listAllInfoAboutCardsTracksOffered by cardsViewModel.listAllInfoAboutCardsTracksOffered.collectAsState(null)
    val utilityFriendInfo by loginViewModel.utilityFriendInfo.collectAsState() // info utente che ha fatto l'offerta corrente
    val reqSentCurrentUser by exchangeManagementCardsViewModel.allOffersSentByCurrentUser.collectAsState(null) // contiene tutte le richieste inviate dall'utente corrente
    val reqReceivedCurrentUser by exchangeManagementCardsViewModel.allOffersReceivedByCurrentUser.collectAsState(null) // contiene tutte le richieste ricevute dall'utente corrente
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////
    // per finestre di dialogo:
    val showSuccessUpdateExchangeCards = remember { mutableStateOf(false) }
    val showSuccessRejectExchangeCards = remember { mutableStateOf(false) }
    val requestedCardAlreadyPresentAnotherOfferSent = remember { mutableStateOf(false) }
    val requestedCardAlreadyPresentAnotherOfferReceived = remember { mutableStateOf(false) }
    //////////////////////////////////////////////////////////////////////////////


    // mi prendo tutte le info necessarie della carta richiesta:
    if(selectedShowReceivedOffer.typeRequiredCard == "artist"){
        infoUserCurrent?.let { cardsViewModel.getInfoCardArtistByEmailAndId(it.email, selectedShowReceivedOffer.idRequiredCard) }
        Log.d("ShowOfferReceived", "Ho invocato il metodo cardsViewModel.getInfoCardArtistByEmailAndId(..) per avere le info della carta richiesta dentro 'infoCardArtistRequest'")
        Log.d("ShowOfferReceived", "infoCardArtistRequest: ${infoCardArtistRequest}")
    }else{
        infoUserCurrent?.let { cardsViewModel.getInfoCardTrackByEmailAndId(it.email, selectedShowReceivedOffer.idRequiredCard) }
        Log.d("ShowOfferReceived", "Ho invocato il metodo cardsViewModel.getInfoCardTrackByEmailAndId(..) per avere le info della carta richiesta dentro 'infoCardTrackRequest'")
        Log.d("ShowOfferReceived", "infoCardTrackRequest: ${infoCardTrackRequest}")
    }
    Log.d("", "")
    Log.d("", "")
    Log.d("ShowOfferReceived", "L'offerta che l'utente vuole visualizzare è la seguente: ${selectedShowReceivedOffer}")
    Log.d("", "")
    Log.d("", "")
    /////////////////////////////////////////////////////////////

    // mi prendo tutte le info necessarie delle carte offerte:

    // mi prendo prima l'email dell'utente che ha inviato l'offerta (aggiorna 'utilityFriendInfo'):
    loginViewModel.getFriendByNickname(selectedShowReceivedOffer.nicknameU1)

    // mi prendo in 'listAllInfoAboutCardsArtistOffered' e 'listAllInfoAboutCardsTracksOffered' tutte le info di tutte le carte che sono state offerte
    // nell'offerta corrente:
    utilityFriendInfo?.let {
        Log.d("ShowOfferReceived", "Info amico che ha inviato l'offerta corrente: ${utilityFriendInfo}")
        cardsViewModel.getInfoCardsOfferedByEmailAndIdsAndTypes(it.email, selectedShowReceivedOffer.listOfferedCards, selectedShowReceivedOffer.listTypesOfferedCards)
        Log.d("ShowOfferReceived", "reqSentCurrentUser fuori COLUMN: ${reqSentCurrentUser}")
        Log.d("ShowOfferReceived", "reqReceivedCurrentUser fuori COLUMN: ${reqReceivedCurrentUser}")
        Log.d("ShowOfferReceived", "listAllInfoAboutCardsArtistOffered: ${listAllInfoAboutCardsArtistOffered}")
        Log.d("ShowOfferReceived", "listAllInfoAboutCardsTracksOffered: ${listAllInfoAboutCardsTracksOffered}")
        Log.d("", "")
        Log.d("", "")
    }
    /////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////
    // - Aggiorno tutte le offerte inviate e ricevute dall'utente corrente:
    infoUserCurrent?.let {
        exchangeManagementCardsViewModel.getOffersSentByCurrentUser(it.nickname) // Aggiorno tutte le offerte inviate dall'utente corrente (reqSentCurrentUser)
        exchangeManagementCardsViewModel.getOffersReceveidByCurrentUser(it.nickname) // Aggiorno tutte le offerte ricevute dall'utente corrente (reqReceivedCurrentUser)
        Log.d("ShowOfferReceived", "Nickname utente corrente: ${it.nickname}")
    }
    ////////////////////////////////////////////////////////////////////////


    infoUserCurrent?.let {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {

            Log.d("ShowOfferReceived", "reqSentCurrentUser dentro COLUMN: ${reqSentCurrentUser}")
            Log.d("ShowOfferReceived", "reqReceivedCurrentUser dentro COLUMN: ${reqReceivedCurrentUser}")


            Text("This is your card that was requested:")

            if (selectedShowReceivedOffer.typeRequiredCard == "artist") {

                infoCardArtistRequest?.let {
                    // mostro la carta artista richiesta:
                    Card(modifier = Modifier.padding(8.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = infoCardArtistRequest!!.nome,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(text = infoCardArtistRequest!!.genere)
                            Text(text = "Pop: ${infoCardArtistRequest!!.popolarita}")
                            Text(text = "Costo: ${infoCardArtistRequest!!.popolarita * 10}")
                            // Immagine dell'artista con dimensioni specificate
                            Image(
                                painter = rememberAsyncImagePainter(infoCardArtistRequest!!.immagine),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(150.dp)
                                    .fillMaxWidth(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            } else {

                infoCardTrackRequest?.let {
                    // mostro la carta brano richiesta:
                    Card(modifier = Modifier.padding(8.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = infoCardTrackRequest!!.nome,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(text = infoCardTrackRequest!!.anno_pubblicazione)
                            Text(text = infoCardTrackRequest!!.durata)
                            Text(text = "Pop: ${infoCardTrackRequest!!.popolarita}")
                            Text(text = "Costo: ${infoCardTrackRequest!!.popolarita * 10}")
                            // Immagine del brano con dimensioni specificate
                            Image(
                                painter = rememberAsyncImagePainter(infoCardTrackRequest!!.immagine),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(150.dp)
                                    .fillMaxWidth(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))


            // Adesso l'utente corrente visualizzerà le carte che gli sono state offerte in questa offerta:

            Text("These are the cards that have been offered to you by ${selectedShowReceivedOffer.nicknameU1}:")


            ////////////////////////////////////////////////////////////////////////////////////
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp)
            ) {

                listAllInfoAboutCardsArtistOffered?.let {
                    listAllInfoAboutCardsTracksOffered?.let {
                        // - Mostro la lista delle carte offerte (di tipo artist o track) se non sono entrambi vuote vuota:
                        if (listAllInfoAboutCardsArtistOffered!!.isNotEmpty() || listAllInfoAboutCardsTracksOffered!!.isNotEmpty()) {

                            // Se ci sono carte offerte, vengono mostrate:
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Offered Cards:")

                            // Mostra le carte artisti offerte dall'utente corrente:
                            listAllInfoAboutCardsArtistOffered?.forEach { card ->
                                OfferedCardArtist(card = card)
                            }

                            // Mostra le carte brani offerte dall'utente corrente:
                            listAllInfoAboutCardsTracksOffered?.forEach { card ->
                                OfferedCardTrack(card = card)
                            }
                        }
                    }
                }

                // Mostro "Offered Points" anche qualo non ci fossero carte offerte o points offerti:
                Spacer(modifier = Modifier.height(8.dp))
                Text("Offered Points: ${selectedShowReceivedOffer.pointsOffered}")
                Spacer(modifier = Modifier.height(16.dp))

                // Row per posizionare i pulsanti orizzontalmente
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Button "Accept offer" sempre visibile:
                    Button(
                        onClick = {

                            // Azione per accettare l'offerta:

                            ////////////////////////////////////////////////////////////////////////////////
                            // Qui bisognerà mettere i controlli per verificare che le carte richieste
                            // dell'utente corrente presenti in questa offerta NON SIANO PRESENTI:
                            // 1) IN UNA QUALCHE OFFERTA (dentro 'listOfferedCards') INVIATA DALL'UTENTE CORRENTE..
                            // 2) IN UNA QUALCHE OFFERTA RICEVUTA (dentro 'idRequiredCard') DALL'UTENTE CORRENTE..
                            // 3) IN UN QUALCHE MAZZO DELL'UTENTE CORRENTE..
                            ////////////////////////////////////////////////////////////////////////////////

                            ////////////////////////////////////////////////////////////////////////////////
                            // 1) IN UNA QUALCHE OFFERTA INVIATA DALL'UTENTE CORRENTE..
                            // SCORRI LA LISTA DI OFFERTE INVIATE DALL'UTENTE CORRENTE E SE C'E' ANCHE SOLO UNA CHE CONTIENE L'ID DELLA CARTA CORRENTE
                            // ALLORA MOSTRA ERRORE.. ALTRIMENTI SI PUO' ESEGUIRE IL CONTROLLO 2)
                            reqSentCurrentUser?.let { listOfSentOffers ->
                                if (listOfSentOffers.isNotEmpty()) {
                                    // Itera su tutti gli elementi
                                    for ((index, SentOffer) in listOfSentOffers.withIndex()) {
                                        Log.d(
                                            "ShowOfferReceived",
                                            "Offerta inviata dall'utente corrente: ${SentOffer}"
                                        )
                                        // Adesso posso accedere ai campi di ogni offerta come offer.nicknameU1, offer.nicknameU2, ecc..
                                        if (SentOffer.listOfferedCards.isNotEmpty()) {
                                            // scorro tutti gli ids delle carte offerte in questa offerta:
                                            for (i in SentOffer.listOfferedCards.indices) {
                                                if ((SentOffer.listOfferedCards[i] == selectedShowReceivedOffer.idRequiredCard) &&
                                                    (SentOffer.id != selectedShowReceivedOffer.id)) {

                                                    Log.d(
                                                        "ShowOfferReceived",
                                                        "La Carta richiesta in questa offerta è stata trovata in un'altra offerta inviata dall'utente corrente."
                                                    )
                                                    Log.d(
                                                        "ShowOfferReceived",
                                                        "SentOffer.listOfferedCards[i]: ${SentOffer.listOfferedCards[i]}"
                                                    )
                                                    Log.d(
                                                        "ShowOfferReceived",
                                                        "selectedShowReceivedOffer.idRequiredCard: ${selectedShowReceivedOffer.idRequiredCard}"
                                                    )
                                                    requestedCardAlreadyPresentAnotherOfferSent.value =
                                                        true
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            ////////////////////////////////////////////////////////////////////////////////


                            ////////////////////////////////////////////////////////////////////////////////
                            // 2) IN UNA QUALCHE OFFERTA RICEVUTA DALL'UTENTE CORRENTE..
                            // SCORRI LA LISTA DI OFFERTE INVIATE DALL'UTENTE CORRENTE E SE C'E' ANCHE SOLO UNA CHE CONTIENE L'ID DELLA CARTA CORRENTE
                            // ALLORA MOSTRA ERRORE dicendo all'utente che prima deve rifiutare le altre offerte.. ALTRIMENTI SI PUO' ESEGUIRE IL CONTROLLO 3)
                            reqReceivedCurrentUser?.let { listOfReceivedOffers ->
                                if (listOfReceivedOffers.isNotEmpty()) {
                                    // Itera su tutti gli elementi
                                    for ((index, ReceivedOffer) in listOfReceivedOffers.withIndex()) {
                                        Log.d(
                                            "ShowOfferReceived",
                                            "Offerta ricevuta dall'utente corrente: ${ReceivedOffer}"
                                        )
                                        // Adesso posso accedere ai campi di ogni offerta come offer.nicknameU1, offer.nicknameU2, ecc..
                                        if ((ReceivedOffer.idRequiredCard == selectedShowReceivedOffer.idRequiredCard) &&
                                            (ReceivedOffer.id != selectedShowReceivedOffer.id)) {
                                            Log.d(
                                                "ShowOfferReceived",
                                                "La Carta richiesta in questa offerta è stata trovata in un'altra offerta ricevuta dall'utente corrente."
                                            )
                                            Log.d(
                                                "ShowOfferReceived",
                                                "ReceivedOffer.idRequiredCard: ${ReceivedOffer.idRequiredCard}"
                                            )
                                            Log.d(
                                                "ShowOfferReceived",
                                                "selectedShowReceivedOffer.idRequiredCard: ${selectedShowReceivedOffer.idRequiredCard}"
                                            )
                                            requestedCardAlreadyPresentAnotherOfferReceived.value = true
                                        }
                                    }
                                }
                            }
                            ////////////////////////////////////////////////////////////////////////////////


                            ////////////////////////////////////////////////////////////////////////////////
                            // 3) Appena Pietro completa i mazzi..
                            ////////////////////////////////////////////////////////////////////////////////


                            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                            // QUI SOTTO SI ENTRERA' SOLAMENTE SE NON SI VERIFICA NESSUNO DEI 3 CONTROLLI CITATI SOPRA: (manca quella sui mazzi)
                            if ((!requestedCardAlreadyPresentAnotherOfferSent.value) && (!requestedCardAlreadyPresentAnotherOfferReceived.value)) {
                                ////////////////////////////////////////////////////////////////////////////////
                                // Aggiorno il proprietario della carta richiesta (diventerà colui che ha inviato l'offerta corrente):
                                if (selectedShowReceivedOffer.typeRequiredCard == "artist") {
                                    utilityFriendInfo?.let {
                                        cardsViewModel.updateCardArtistOwner(
                                            it.email,
                                            selectedShowReceivedOffer.idRequiredCard
                                        )
                                        Log.d(
                                            "ShowOfferReceived",
                                            "artist-Nuovo proprietario carta richiesta: ${it.email}"
                                        )
                                    }
                                } else {
                                    utilityFriendInfo?.let {
                                        cardsViewModel.updateCardTrackOwner(
                                            it.email,
                                            selectedShowReceivedOffer.idRequiredCard
                                        )
                                        Log.d(
                                            "ShowOfferReceived",
                                            "track-Nuovo proprietario carta richiesta: ${it.email}"
                                        )
                                    }
                                }
                                ////////////////////////////////////////////////////////////////////////////////

                                ///////////////////////////////////////////////////////////////////////////////////////
                                // Se la 'listAllInfoAboutCardsArtistOffered' NON E' VUOTA, scorro
                                // tutti gli elementi presenti al suo interno e aggiorno il suo proprietario
                                // (diventerà colui che ha ricevuto l'offerta):
                                if (!listAllInfoAboutCardsArtistOffered?.isEmpty()!!) {
                                    for (i in listAllInfoAboutCardsArtistOffered!!.indices) {
                                        infoUserCurrent?.let {
                                            cardsViewModel.updateCardArtistOwner(
                                                it.email,
                                                listAllInfoAboutCardsArtistOffered!![i].id_carta
                                            )
                                            Log.d(
                                                "ShowOfferReceived",
                                                "listAllInfoAboutCardsArtistOffered-Nuovo proprietario carta richiesta: ${it.email}"
                                            )
                                        }
                                    }
                                }
                                ///////////////////////////////////////////////////////////////////////////////////////

                                ///////////////////////////////////////////////////////////////////////////////////////
                                // Se la 'listAllInfoAboutCardsArtistOffered' NON E' VUOTA, scorro
                                // tutti gli elementi presenti al suo interno e aggiorno il suo proprietario
                                // (diventerà colui che ha ricevuto l'offerta):
                                if (!listAllInfoAboutCardsTracksOffered?.isEmpty()!!) {
                                    for (i in listAllInfoAboutCardsTracksOffered!!.indices) {
                                        infoUserCurrent?.let {
                                            cardsViewModel.updateCardTrackOwner(
                                                it.email,
                                                listAllInfoAboutCardsTracksOffered!![i].id_carta
                                            )
                                            Log.d(
                                                "ShowOfferReceived",
                                                "listAllInfoAboutCardsTracksOffered-Nuovo proprietario carta richiesta: ${it.email}"
                                            )
                                        }
                                    }
                                }
                                ///////////////////////////////////////////////////////////////////////////////////////

                                // se nell'offerta accettata c'erano dei points offerti allora aggiorno i points togliendoli
                                // all'utente che ha fatto l'offerta e aggiungendoli a colui che ha ricevuto l'offerta:
                                if(selectedShowReceivedOffer.pointsOffered > 0){
                                    // eseguo update points:
                                    loginViewModel.subtractPoints(selectedShowReceivedOffer.pointsOffered, utilityFriendInfo!!.email)
                                    loginViewModel.addPoints(selectedShowReceivedOffer.pointsOffered, infoUserCurrent!!.email)
                                }


                                ///////////////////////////////////////////////////////////////////////////////////////
                                // - Adesso cancello l'offerta dal DB:
                                exchangeManagementCardsViewModel.deleteOffer(
                                    selectedShowReceivedOffer.id
                                )
                                ///////////////////////////////////////////////////////////////////////////////////////

                                showSuccessUpdateExchangeCards.value =
                                    true // feedback per l'utente
                            }
                            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        },
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                    ) {
                        Text("Accept Offer")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Button "Reject offer" sempre visibile:
                    Button(
                        onClick = {
                            // Azione per rifiutare l'offerta:

                            // cancello l'offerta corrente dal DB:
                            exchangeManagementCardsViewModel.deleteOffer(
                                selectedShowReceivedOffer.id
                            )
                            showSuccessRejectExchangeCards.value =
                                true // feedback per l'utente
                        },
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                    ) {
                        Text("Reject Offer")
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))
            }
            ////////////////////////////////////////////////////////////////////////////////////


            Spacer(modifier = Modifier.height(8.dp))

            if (showSuccessUpdateExchangeCards.value) {
                AlertDialog(
                    onDismissRequest = {
                        showSuccessUpdateExchangeCards.value = false
                        navController.popBackStack()
                    },
                    title = { Text(text = "Successfully") },
                    text = { Text(text = "Card exchange update successful!") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showSuccessUpdateExchangeCards.value = false
                                navController.popBackStack()
                            }
                        ) {
                            Text("OK")
                        }
                    }
                )
            }

            if (showSuccessRejectExchangeCards.value) {
                AlertDialog(
                    onDismissRequest = {
                        showSuccessRejectExchangeCards.value = false
                        navController.popBackStack()
                    },
                    title = { Text(text = "Successfully") },
                    text = { Text(text = "Offer rejected successfully!") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showSuccessRejectExchangeCards.value = false
                                navController.popBackStack()
                            }
                        ) {
                            Text("OK")
                        }
                    }
                )
            }

            if (requestedCardAlreadyPresentAnotherOfferSent.value) {
                AlertDialog(
                    onDismissRequest = {
                        requestedCardAlreadyPresentAnotherOfferSent.value = false
                        navController.popBackStack()
                    },
                    title = { Text(text = "Error") },
                    text = {
                        Text(
                            text = "The requested card is already present in another offer you have sent! \n\n" +
                                    "Before carrying out this operation you must first delete all the offers sent in which you have inserted this card."
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                requestedCardAlreadyPresentAnotherOfferSent.value = false
                                navController.popBackStack()
                            }
                        ) {
                            Text("OK")
                        }
                    }
                )
            }

            if (requestedCardAlreadyPresentAnotherOfferReceived.value) {
                AlertDialog(
                    onDismissRequest = {
                        requestedCardAlreadyPresentAnotherOfferReceived.value = false
                        navController.popBackStack()
                    },
                    title = { Text(text = "Error") },
                    text = {
                        Text(
                            text = "The requested card is already present in another offer you have received! \n\n" +
                                    "Before carrying out this operation you must first reject all the offers received in which this card was requested."
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                requestedCardAlreadyPresentAnotherOfferReceived.value =
                                    false
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
}




/*
 - Composable che permette all'utente loggato di poter visualizzare tutte le offerte di scambi inviate e
   di poter decidere quali eventualmente cancellare.
*/
@Composable
fun OffersSent(
    navController: NavController,
    exchangeManagementCardsViewModel: ExchangeManagementCardsViewModel,
    infoUserCurrent: User?){

    if (infoUserCurrent != null) {
        exchangeManagementCardsViewModel.getOffersReceveidByCurrentUser(infoUserCurrent.nickname)
        exchangeManagementCardsViewModel.getOffersSentByCurrentUser(infoUserCurrent.nickname)
    }
    val reqSentOffersByCurrentUser by exchangeManagementCardsViewModel.allOffersSentByCurrentUser.collectAsState(null) // contiene tutte le richieste ricevute dall'utente corrente

    Log.d("OffersSent", "reqSentOffersByCurrentUser: ${reqSentOffersByCurrentUser}")

    var selectedShowSentOffer by exchangeManagementCardsViewModel.selectedShowSentOffer

    // finestre di dialogo:
    var confirmDeleteRequest = remember { mutableStateOf(false) }
    var successfullyDelete = remember { mutableStateOf(false) }
    //////////////////////


    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))
        if (reqSentOffersByCurrentUser.isNullOrEmpty()) {
            Text(
                text = "No offers sent",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(reqSentOffersByCurrentUser!!) { offerSent ->
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
                            // Prendo il Nickname dell'utente amico a cui l'utente corrente ha inviato l'offerta corrente:
                            Text(
                                text = offerSent.nicknameU2,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 4.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            // Icone cliccabili
                            IconButton(onClick = {
                                // quando clicco su questa icona, viene mostrata all'utente l'offerta inviata:
                                exchangeManagementCardsViewModel.updateSelectedShowSentOffer(offerSent) // memorizzo l'offerta che ha inviato e che vuole visualizzare
                                navController.navigate("ShowOfferSent")
                            }) {
                                Icon(imageVector = Icons.Default.Markunread, contentDescription = "ShowOffer")
                            }
                            IconButton(onClick = {
                                // quando clicco su questa icona, viene chiesto all'utente se è sicuro di voler cancellare
                                // l'offerta inviata:
                                confirmDeleteRequest.value = true
                            }) {
                                Icon(imageVector = Icons.Default.DeleteForever, contentDescription = "DeleteOffer")
                            }
                        }
                    }
                    Divider(
                        color = Color.Black,
                        thickness = 2.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    if(confirmDeleteRequest.value){
                        AlertDialog(
                            onDismissRequest = { confirmDeleteRequest.value = false },
                            title = { Text(text = "Delete request") },
                            text = { Text(text = "Do you really want to delete this offer?") },
                            confirmButton = {
                                TextButton(
                                    onClick = {

                                        // cancello l'offerta corrente dal DB:
                                        exchangeManagementCardsViewModel.deleteOffer(
                                            offerSent.id
                                        )

                                        confirmDeleteRequest.value = false
                                        successfullyDelete.value = true // feedback all'utente
                                    }
                                ) {
                                    Text("Yes")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { confirmDeleteRequest.value = false }) {
                                    Text("No")
                                }
                            }
                        )
                    }

                    if(successfullyDelete.value){
                        AlertDialog(
                            onDismissRequest = {
                                successfullyDelete.value = false
                            },
                            title = { Text(text = "Operation performed successfully!") },
                            text = { Text(text = "Offer successfully deleted!") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        successfullyDelete.value = false
                                    }
                                ) {
                                    Text("OK")
                                }
                            }
                        )

                    }

                }
            }
        }
    }
}


/*
 - Composable che permette all'utente di visualizzare l'offerta inviata che ha selezionato nella schermata rappresentata
   dal composable 'OffersSent'.
*/
@Composable
fun ShowOfferSent(
    navController: NavController,
    exchangeManagementCardsViewModel: ExchangeManagementCardsViewModel,
    cardsViewModel: CardsViewModel,
    loginViewModel: LoginViewModel
){

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    val infoUserCurrent by loginViewModel.userLoggedInfo.collectAsState(initial = null) // info utente che ha inviato l'offerta corrente e che è loggato in questo momento
    var selectedShowSentOffer by exchangeManagementCardsViewModel.selectedShowSentOffer
    val infoCardArtistRequest by cardsViewModel.infoCardArtistRequest.collectAsState(initial = null) // mi prendo da qui le info della carta che eventualmente sarà di tipo 'artista' richiesta dall'utente corrente nell'offerta corrente.
    val infoCardTrackRequest by cardsViewModel.infoCardTrackRequest.collectAsState(initial = null) // mi prendo da qui le info della carta che eventualmente sarà di tipo 'brano' richiesta dall'utente corrente nell'offerta corrente.
    val listAllInfoAboutCardsArtistRequired by cardsViewModel.listAllInfoAboutCardsArtistOffered.collectAsState(null)
    val listAllInfoAboutCardsTracksRequired by cardsViewModel.listAllInfoAboutCardsTracksOffered.collectAsState(null)
    val utilityFriendInfo by loginViewModel.utilityFriendInfo.collectAsState() // info utente che ha ricevuto l'offerta corrente
    val reqSentCurrentUser by exchangeManagementCardsViewModel.allOffersSentByCurrentUser.collectAsState(null) // contiene tutte le richieste inviate dall'utente corrente
    val reqReceivedCurrentUser by exchangeManagementCardsViewModel.allOffersReceivedByCurrentUser.collectAsState(null) // contiene tutte le richieste ricevute dall'utente corrente
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////
    // per finestre di dialogo:
    val showSuccessDeleteExchangeCards = remember { mutableStateOf(false) }
    //////////////////////////////////////////////////////////////////////////////


    // mi prendo tutte le info necessarie della carta richiesta (che in questa schermata appartiene a 'utilityFriendInfo'):
    if(selectedShowSentOffer.typeRequiredCard == "artist"){
        utilityFriendInfo?.let { cardsViewModel.getInfoCardArtistByEmailAndId(it.email, selectedShowSentOffer.idRequiredCard) }
        Log.d("ShowOfferSent", "Ho invocato il metodo cardsViewModel.getInfoCardArtistByEmailAndId(..) per avere le info della carta richiesta dentro 'infoCardArtistRequest'")
        Log.d("ShowOfferSent", "infoCardArtistRequest: ${infoCardArtistRequest}")
    }else{
        utilityFriendInfo?.let { cardsViewModel.getInfoCardTrackByEmailAndId(it.email, selectedShowSentOffer.idRequiredCard) }
        Log.d("ShowOfferSent", "Ho invocato il metodo cardsViewModel.getInfoCardTrackByEmailAndId(..) per avere le info della carta richiesta dentro 'infoCardTrackRequest'")
        Log.d("ShowOfferSent", "infoCardTrackRequest: ${infoCardTrackRequest}")
    }
    Log.d("", "")
    Log.d("", "")
    Log.d("ShowOfferSent", "L'offerta inviata che l'utente vuole visualizzare è la seguente: ${selectedShowSentOffer}")
    Log.d("", "")
    Log.d("", "")
    /////////////////////////////////////////////////////////////

    // mi prendo tutte le info necessarie delle carte offerte:

    // mi prendo prima l'email dell'utente che ha ricevuto l'offerta (aggiorna 'utilityFriendInfo'):
    loginViewModel.getFriendByNickname(selectedShowSentOffer.nicknameU2)

    // mi prendo in 'listAllInfoAboutCardsArtistOffered' e 'listAllInfoAboutCardsTracksOffered' tutte le info di tutte le carte che sono state offerte
    // nell'offerta corrente:
    infoUserCurrent?.let {
        Log.d("ShowOfferSent", "Info dell'utente che ha inviato l'offerta corrente (sarebbe l'utente corrente): ${infoUserCurrent}")
        cardsViewModel.getInfoCardsOfferedByEmailAndIdsAndTypes(infoUserCurrent!!.email, selectedShowSentOffer.listOfferedCards, selectedShowSentOffer.listTypesOfferedCards)
        Log.d("ShowOfferSent", "reqSentCurrentUser fuori COLUMN: ${reqSentCurrentUser}")
        Log.d("ShowOfferSent", "reqReceivedCurrentUser fuori COLUMN: ${reqReceivedCurrentUser}")
        Log.d("ShowOfferSent", "listAllInfoAboutCardsArtistRequired: ${listAllInfoAboutCardsArtistRequired}")
        Log.d("ShowOfferSent", "listAllInfoAboutCardsTracksRequired: ${listAllInfoAboutCardsTracksRequired}")
        Log.d("", "")
        Log.d("", "")
    }
    /////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////
    // - Aggiorno tutte le offerte inviate e ricevute dall'utente corrente:
    infoUserCurrent?.let {
        exchangeManagementCardsViewModel.getOffersSentByCurrentUser(it.nickname) // Aggiorno tutte le offerte inviate dall'utente corrente (reqSentCurrentUser)
        exchangeManagementCardsViewModel.getOffersReceveidByCurrentUser(it.nickname) // Aggiorno tutte le offerte ricevute dall'utente corrente (reqReceivedCurrentUser)
        Log.d("ShowOfferSent", "Nickname utente corrente: ${it.nickname}")
    }
    ////////////////////////////////////////////////////////////////////////


    infoUserCurrent?.let {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {

            Log.d("ShowOfferSent", "reqSentCurrentUser dentro COLUMN: ${reqSentCurrentUser}")
            Log.d("ShowOfferSent", "reqReceivedCurrentUser dentro COLUMN: ${reqReceivedCurrentUser}")


            Text("This is the card you requested:")

            if (selectedShowSentOffer.typeRequiredCard == "artist") {

                infoCardArtistRequest?.let {
                    // mostro la carta artista richiesta:
                    Card(modifier = Modifier.padding(8.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = infoCardArtistRequest!!.nome,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(text = infoCardArtistRequest!!.genere)
                            Text(text = "Pop: ${infoCardArtistRequest!!.popolarita}")
                            Text(text = "Costo: ${infoCardArtistRequest!!.popolarita * 10}")
                            // Immagine dell'artista con dimensioni specificate
                            Image(
                                painter = rememberAsyncImagePainter(infoCardArtistRequest!!.immagine),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(150.dp)
                                    .fillMaxWidth(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            } else {

                infoCardTrackRequest?.let {
                    // mostro la carta brano richiesta:
                    Card(modifier = Modifier.padding(8.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = infoCardTrackRequest!!.nome,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(text = infoCardTrackRequest!!.anno_pubblicazione)
                            Text(text = infoCardTrackRequest!!.durata)
                            Text(text = "Pop: ${infoCardTrackRequest!!.popolarita}")
                            Text(text = "Costo: ${infoCardTrackRequest!!.popolarita * 10}")
                            // Immagine del brano con dimensioni specificate
                            Image(
                                painter = rememberAsyncImagePainter(infoCardTrackRequest!!.immagine),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(150.dp)
                                    .fillMaxWidth(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))


            // Adesso l'utente corrente visualizzerà le carte che lui stesso ha offerto in questa offerta:

            Text("These are the cards you offered:")


            ////////////////////////////////////////////////////////////////////////////////////
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp)
            ) {

                listAllInfoAboutCardsArtistRequired?.let {
                    listAllInfoAboutCardsTracksRequired?.let {
                        // - Mostro la lista delle carte offerte (di tipo artist o track) se non sono entrambi vuote vuota:
                        if (listAllInfoAboutCardsArtistRequired!!.isNotEmpty() || listAllInfoAboutCardsTracksRequired!!.isNotEmpty()) {

                            // Se ci sono carte offerte, vengono mostrate:
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Offered Cards:")

                            // Mostra le carte artisti offerte dall'utente corrente:
                            listAllInfoAboutCardsArtistRequired?.forEach { card ->
                                OfferedCardArtist(card = card)
                            }

                            // Mostra le carte brani offerte dall'utente corrente:
                            listAllInfoAboutCardsTracksRequired?.forEach { card ->
                                OfferedCardTrack(card = card)
                            }
                        }
                    }
                }

                // Mostro "Offered Points" anche qualo non ci fossero carte offerte o points offerti:
                Spacer(modifier = Modifier.height(8.dp))
                Text("Offered Points: ${selectedShowSentOffer.pointsOffered}")
                Spacer(modifier = Modifier.height(16.dp))

                // Row per posizionare i pulsanti orizzontalmente
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    // Button "Reject offer" sempre visibile:
                    Button(
                        onClick = {
                            // Azione per cancellare l'offerta:

                            // cancello l'offerta corrente dal DB:
                            exchangeManagementCardsViewModel.deleteOffer(
                                selectedShowSentOffer.id
                            )
                            showSuccessDeleteExchangeCards.value = true // feedback per l'utente
                        },
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                    ) {
                        Text("Delete Offer")
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))
            }
            ////////////////////////////////////////////////////////////////////////////////////

            Spacer(modifier = Modifier.height(8.dp))



            if (showSuccessDeleteExchangeCards.value) {
                AlertDialog(
                    onDismissRequest = {
                        showSuccessDeleteExchangeCards.value = false
                        navController.popBackStack()
                    },
                    title = { Text(text = "Successfully") },
                    text = { Text(text = "Offer deleted successfully!") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showSuccessDeleteExchangeCards.value = false
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
}

