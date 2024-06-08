package com.example.musicdraft.sections
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.Pending
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.musicdraft.data.tables.handleFriends.HandleFriends
import com.example.musicdraft.data.tables.user.User
import com.example.musicdraft.viewModel.HandleFriendsViewModel
import com.example.musicdraft.viewModel.LoginViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Friends(handleFriendsViewModel: HandleFriendsViewModel, loginViewModel: LoginViewModel) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
//    var showDialog by remember { mutableStateOf(false) }
//    var selectedUser by remember { mutableStateOf<User?>(null) }
//    var showConfirmationDialog by remember { mutableStateOf(false) }

    val usersFilter by handleFriendsViewModel.usersFilter.collectAsState()
    val reqReceivedCurrentUser by handleFriendsViewModel.reqReceivedCurrentUser.collectAsState(null)
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
        )
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val pagerState = rememberPagerState(pageCount = { tabItems.size })

//        LaunchedEffect(pagerState.currentPage) {
//            selectedTabIndex = pagerState.currentPage
//            if (selectedTabIndex == 1) {
//                infoUserCurrent?.let {
//                    handleFriendsViewModel.getRequestReceivedByUser(it.email)
//                }
//            }
//        }

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
                            RequestMatesList(allUsersFriendsOfCurrentUser, infoUserCurrent)
                        }
                    }
                    1 -> RequestReceivedList(handleFriendsViewModel, reqReceivedCurrentUser = reqReceivedCurrentUser, infoUserCurrent)
                    2 -> RequestSent(handleFriendsViewModel, usersFilter, infoUserCurrent, allUsersFriendsOfCurrentUser)
                }
            }
        }

//        if (showDialog) {
//            AlertDialog(
//                onDismissRequest = { showDialog = false },
//                title = { Text(text = "Friend Request") },
//                text = { Text(text = "Do you want to send a friend request to this user?") },
//                confirmButton = {
//                    TextButton(
//                        onClick = {
//                            selectedUser?.email?.let { handleFriendsViewModel.insertNewRequest(email1 = infoUserCurrent!!.email, email2 = it) }
//                            showDialog = false
//                            showConfirmationDialog = true
//                        }
//                    ) {
//                        Text("Yes")
//                    }
//                },
//                dismissButton = {
//                    TextButton(onClick = { showDialog = false }) {
//                        Text("No")
//                    }
//                }
//            )
//        }

//        if (showConfirmationDialog) {
//            AlertDialog(
//                onDismissRequest = { showConfirmationDialog = false },
//                title = { Text(text = "Success") },
//                text = { Text(text = "Friend request sent successfully!") },
//                confirmButton = {
//                    TextButton(
//                        onClick = { showConfirmationDialog = false }
//                    ) {
//                        Text("OK")
//                    }
//                }
//            )
//        }
    }
}

data class TabItem(
    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
)



@Composable
fun RequestMatesList(allUsersFriendsOfCurrentUser: List<User>?, infoUserCurrent: User?) {
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
                            IconButton(onClick = { /* Azione quando si clicca sull'icona 1 */ }) {
                                Icon(imageVector = Icons.Default.ChangeCircle, contentDescription = "ChangeCards")
                            }
                            IconButton(onClick = { /* Azione quando si clicca sull'icona 2 */ }) {
                                Icon(imageVector = Icons.Default.LocalFireDepartment, contentDescription = "ChallengeFriend")
                            }
                            IconButton(onClick = { /* Azione quando si clicca sull'icona 3 */ }) {
                                Icon(imageVector = Icons.Default.DeleteForever, contentDescription = "DeleteFriend")
                            }
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
}




//text = if (friendUser.email1 != infoUserCurrent.email) {
//    friendUser.email1 // sono certo che l'email dell'amico è questa perchè friendUser.email2 è l'email dell'utente corrente
//} else {
//    friendUser.email2 // sono certo che l'email dell'amico è questa perchè friendUser.email1 è l'email dell'utente corrente
//},


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


//        if (showAcceptedDialog) {
//            AlertDialog(
//                onDismissRequest = { showAcceptedDialog = false },
//                title = { Text(text = "Success") },
//                text = { Text(text = "Now you and this user are friends!") },
//                confirmButton = {
//                    TextButton(
//                        onClick = {
//                            showAcceptedDialog = false
//                            handleFriendsViewModel.getRequestReceivedByUser(email2) // richiedo l'aggiornamento delle richieste d'amicizia
//                        }
//                    ) {
//                        Text("OK")
//                    }
//                }
//            )
//        }
//
//        if (showRefuseDialog) {
//            AlertDialog(
//                onDismissRequest = { showAcceptedDialog = false },
//                title = { Text(text = "Success") },
//                text = { Text(text = "Request friend refused!") },
//                confirmButton = {
//                    TextButton(
//                        onClick = {
//                            showAcceptedDialog = false
//                            handleFriendsViewModel.getRequestReceivedByUser(email2) // richiedo l'aggiornamento delle richieste d'amicizia
//                        }
//                    ) {
//                        Text("OK")
//                    }
//                }
//            )
//        }

    }
}


@Composable
fun RequestSent(
    handleFriendsViewModel: HandleFriendsViewModel,
    usersFilter: List<User>?,
    infoUserCurrent: User?,
    allUsersFriendsOfCurrentUser: List<User>?,
   ){

    var showDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showDialogYouHaveAlreadyReceived by remember { mutableStateOf(false) }
    var showDialogYouHaveAlreadySent by remember { mutableStateOf(false) }

    val allPendingRequest by handleFriendsViewModel.allPendingRequest.collectAsState(null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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



                ////////////////////////////////////////////////////////////////////////////////////////////////////
                // Chiamata al metodo per aggiornare le richieste pendenti nelle quali è coinvolto l'utente corrente:
                infoUserCurrent?.email?.let { handleFriendsViewModel.getAllPendingRequestByUser(it) }
                ////////////////////////////////////////////////////////////////////////////////////////////////////

                Column(modifier = Modifier.fillMaxWidth()) {
                    // scorro tutti gli utenti filtrati (ogni utente filtrato è rappresentato da 'user'):
                    users.forEach { user ->

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

                            Text(
                                text = user.email,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {

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

}