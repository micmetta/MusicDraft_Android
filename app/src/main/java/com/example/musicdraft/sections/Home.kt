package com.example.musicdraft.sections

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.musicdraft.data.tables.matchmaking.MatchSummaryConcluded
import com.example.musicdraft.data.tables.user.User
import com.example.musicdraft.viewModel.CardsViewModel
import com.example.musicdraft.viewModel.HandleFriendsViewModel
import com.example.musicdraft.viewModel.LoginViewModel
import com.example.musicdraft.viewModel.MatchmakingViewModel


@Composable
fun Home(
    loginViewModel: LoginViewModel,
    handleFriendsViewModel: HandleFriendsViewModel,
    matchmakingViewModel: MatchmakingViewModel,
    cardsViewModel: CardsViewModel,
    NUM_POINTS_MIN: Int
) {

    val infoUserCurrent by loginViewModel.userLoggedInfo.collectAsState(initial = null)
    val allFriendsCurrentUser by handleFriendsViewModel.allFriendsCurrentUser.collectAsState(null)
    val allUsersFriendsOfCurrentUser by loginViewModel.allUsersFriendsOfCurrentUser.collectAsState(null)
    val matchesConcludedByCurrentUser by matchmakingViewModel.matchesConcludedByCurrentUser.collectAsState(null)
    // caricamento carte dell'utente corrente:
    val artistsUser by cardsViewModel.acquiredCardsA.collectAsState(emptyList()) // lista artisti dell'utente corrente (Flow<List<User_Cards_Track>>)
    val tracksUser by cardsViewModel.acquiredCardsT.collectAsState(emptyList()) // lista brani dell'utente corrente (Flow<List<User_Cards_Track>>)
    ////////////////////////////////////////////

    //////////////////////
    var rankUser = 0f
    var contCardsUser = 0
    //////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Se non c'è nessun utente attivo allora
    // richiedo l'update di 'userLoggedInfo' e quindi automaticamente anche 'infoUserCurrent' sarà aggiornato.
    // Se invece dovesse esserci già un utente attivo allora 'infoUserCurrent' già conterrà i dati dell'utente attivo.
    if (loginViewModel.isUSerLoggedIn.value == false){
        loginViewModel.getUserByEmail()
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    infoUserCurrent?.let{
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        // Chiamata al metodo per aggiornare gli amici dell'utente corrente:
        handleFriendsViewModel.getAllFriendsByUser(it.email)
        ////////////////////////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////////////////////////
        // Aggiorno tutti i nicknames di tutti gli amici dell'utente corrente:
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
        ////////////////////////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////////////////////////
        // Aggiorno tutte le partite terminate dell'utente corrente: (aggiorna 'matchesConcludedByCurrentUser')
        matchmakingViewModel.getAllGamesConludedByNickname(infoUserCurrent!!.nickname)
        ////////////////////////////////////////////////////////////////////////////////////////////////////

        // richiesta aggiornamento carte artisti/brani dell'utente:
        cardsViewModel.getallcards()
        ////////////////////////////////////////////

        Log.d("Home", "Sono dentro la schermata Home() e..:")
        Log.d("Home", "Sono dentro la schermata Home() e l'email dell'utente è il seguente: " + infoUserCurrent!!.email)
        Log.d("Home", "Sono dentro la schermata Home() e il nickname dell'utente è il seguente: " + infoUserCurrent!!.nickname)
        Log.d("Home", "Sono dentro la schermata Home() e i points dell'utente sono i seguenti: " + infoUserCurrent!!.points)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(50.dp)) // c'era 60

        /////////////////////////////////////////////////////////////////////////////////////////////////////
        // Saluto l'utente
        infoUserCurrent?.let { user ->
            Text(
                text = "Hello, ${user.nickname}",
                style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    //.fillMaxWidth()
                    //.padding(12.dp)
                    .background(
                        color = MaterialTheme.colorScheme.background
                    )
                    .padding(16.dp), // c'era 16
                textAlign = TextAlign.Start
            )
        }
        /////////////////////////////////////////////////////////////////////////////////////////////////////


        /////////////////////////////////////////////////////////////////////////////////////////////////////
        // Sezione Points Guadagnati:
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(8.dp) // c'era 16
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    infoUserCurrent?.let {user ->
                        // Testo "Points" e valore "1500pt" concatenati
                        Text(
                            text = "Points: ${user.points}" + "pt",
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp)) // c'era 8

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    infoUserCurrent?.let { user ->
                        artistsUser?.forEach { artistCard ->
                            rankUser += artistCard.popolarita
                            contCardsUser++
                        }
                        tracksUser?.forEach { tracksCard ->
                            rankUser += tracksCard.popolarita
                            contCardsUser++
                        }

                        if(rankUser.toInt() != 0){
                            rankUser = rankUser/contCardsUser
                            //formattedRank = String.format("%.2f", rankUser)
                            Text(
                                text = "Rank: ${String.format("%.2f", rankUser)}",
                                style = MaterialTheme.typography.headlineMedium,
                            )
                        }
                        else{
                            Text(
                                text = "Rank: 0",
                                style = MaterialTheme.typography.headlineMedium,
                            )
                        }
                    }
                }
            }
        }
        /////////////////////////////////////////////////////////////////////////////////////////////////////

        Spacer(modifier = Modifier.height(20.dp))

        ////////////////////////////////////////////////////////////////////////////////////////////////////
        // Dati per il grafico
//        val dates = listOf("01/01/2023", "02/01/2023", "03/01/2023", "04/01/2023", "05/01/2023")
//        val values = listOf(5f, 8f, 6f, 10f, 7f)
//        val lineData = dates.indices.map { index -> Point(index.toFloat(), values[index]) }
        if (matchesConcludedByCurrentUser != null) {
            if (matchesConcludedByCurrentUser!!.size > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    // Sezione Statistiche Giocatori
                    Text(
                        text = "Points trend",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )
                    // creo il grafico:
                    //BarChartScreen()
                    LineChartComposable(
                        allUsersFriendsOfCurrentUser,
                        matchesConcludedByCurrentUser,
                        infoUserCurrent,
                        NUM_POINTS_MIN
                    )
                }
            }
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////

        Spacer(modifier = Modifier.height(16.dp))
        //Spacer(modifier = Modifier.height(8.dp))

        ////////////////////////////////////////////////////////////////////////////////////////////////////
        // Sezione Amici Online
        if (!allUsersFriendsOfCurrentUser.isNullOrEmpty()) { // verifico che l'utente corrente abbia degli amici
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                ) {
                    Text(
                        text = "Friends online/offline",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(allUsersFriendsOfCurrentUser!!) { friendUser ->
                                FriendCard(friendUser = friendUser)
                        }
                    }
                }
            }
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////

    }
}

@Composable
fun FriendCard(friendUser: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
            //.background(MaterialTheme.colorScheme.secondary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFCCC2DC))
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = friendUser.nickname, fontWeight = FontWeight.Bold)
                //Spacer(modifier = Modifier.height(4.dp))
                Text(text = friendUser.email)
            }
            //Spacer(modifier = Modifier.weight(1f)) // Spazio flessibile per separare dalla stringa di stato
            Spacer(modifier = Modifier.width(8.dp)) // Spazio fisso tra i dati dell'amico e la scritta di stato
            Text(
                text = if (friendUser.isOnline) "is online" else "is offline",
                color = if (friendUser.isOnline) Color(0xFF006400) else Color.Red,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold
            )
        }
    }
}



@Composable
fun LineChartComposable(
    allUsersFriendsOfCurrentUser: List<User>?,
    matchesConcludedByCurrentUser: List<MatchSummaryConcluded>?,
    infoUserCurrent: User?,
    NUM_POINTS_MIN: Int
) {

    if (matchesConcludedByCurrentUser != null) {
        if (matchesConcludedByCurrentUser.size > 0) {

            Log.d("LineChartComposable", "matchesConcludedByCurrentUser: ${matchesConcludedByCurrentUser}")

            val steps = matchesConcludedByCurrentUser.size
            val pointsData = mutableMapOf<String, Int>() // mappa vuota

            if(matchesConcludedByCurrentUser.size > 10){
                // inserisco nel grafico i punteggi solamente delle ultime 10 partite:
                val last10Matches = matchesConcludedByCurrentUser.take (10)

                // inserisco nel grafico tutte le partite
                last10Matches.forEach { matchConcluded ->
                    if (matchConcluded.nickWinner == infoUserCurrent!!.nickname || matchConcluded.nickWinner == "") { // l'utente corrente ha vinto o ha pareggiato il game
                        if (matchConcluded.dataGame in pointsData) { // controllo se la data del match corrente è già presente nella mappa
                            Log.d(
                                "LineChartComposable",
                                "La data ${matchConcluded.dataGame} è già presente nella mappa."
                            )
                            pointsData[matchConcluded.dataGame] =
                                pointsData[matchConcluded.dataGame]!! + NUM_POINTS_MIN
                        } else {
                            Log.d(
                                "LineChartComposable",
                                "La data ${matchConcluded.dataGame} NON è già presente nella mappa."
                            )
                            pointsData[matchConcluded.dataGame] = NUM_POINTS_MIN
                        }
                    } else { // l'utente corrente ha perso
                        if (matchConcluded.dataGame in pointsData) { // controllo se la data del match corrente è già presente nella mappa
                            Log.d(
                                "LineChartComposable",
                                "La data ${matchConcluded.dataGame} è già presente nella mappa."
                            )
                            pointsData[matchConcluded.dataGame] =
                                pointsData[matchConcluded.dataGame]!! - NUM_POINTS_MIN
                        } else {
                            Log.d(
                                "LineChartComposable",
                                "La data ${matchConcluded.dataGame} NON è già presente nella mappa."
                            )
                            pointsData[matchConcluded.dataGame] = -NUM_POINTS_MIN
                        }
                    }
                }


            }else {

                //Log.d("LineChartComposable", "matchesConcludedByCurrentUser.take (2): ${matchesConcludedByCurrentUser.take (2)}")

                // aggiungo in cima come primo elemento un elemento fittizio in modo tale da migliorare la visualizzazione del grafico:
                pointsData[""] = 0

                // inserisco nel grafico tutte le partite
                matchesConcludedByCurrentUser.forEach { matchConcluded ->
                    if (matchConcluded.nickWinner == infoUserCurrent!!.nickname || matchConcluded.nickWinner == "") { // l'utente corrente ha vinto o ha pareggiato il game
                        if (matchConcluded.dataGame in pointsData) { // controllo se la data del match corrente è già presente nella mappa
                            Log.d(
                                "LineChartComposable",
                                "La data ${matchConcluded.dataGame} è già presente nella mappa."
                            )
                            pointsData[matchConcluded.dataGame] = pointsData[matchConcluded.dataGame]!! + NUM_POINTS_MIN
                        } else {
                            Log.d(
                                "LineChartComposable",
                                "La data ${matchConcluded.dataGame} NON è già presente nella mappa."
                            )
                            pointsData[matchConcluded.dataGame] = NUM_POINTS_MIN
                        }
                    } else { // l'utente corrente ha perso
                        if (matchConcluded.dataGame in pointsData) { // controllo se la data del match corrente è già presente nella mappa
                            Log.d(
                                "LineChartComposable",
                                "La data ${matchConcluded.dataGame} è già presente nella mappa."
                            )
                            pointsData[matchConcluded.dataGame] =
                                pointsData[matchConcluded.dataGame]!! - NUM_POINTS_MIN
                        } else {
                            Log.d(
                                "LineChartComposable",
                                "La data ${matchConcluded.dataGame} NON è già presente nella mappa."
                            )
                            pointsData[matchConcluded.dataGame] = -NUM_POINTS_MIN
                        }
                    }
                }
            }


            // Estraggo le chiavi (le date) dalla mappa e ordino le date
            val sortedDates = pointsData.keys.toList().sorted()

            // Crea i punti utilizzando i valori float corrispondenti alle date
            val points = sortedDates.mapIndexed { index, date ->
//            Point(index.toFloat(), pointsData[date]!!) // c'era prima
                //android.graphics.Point(index, pointsData[date]!!)
                Point(index.toFloat(), pointsData[date]!!.toFloat())
            }

            val xAxisData = AxisData.Builder()
                .axisStepSize(100.dp)
                .backgroundColor(Color.Transparent)
                .steps(points.size - 1)
                .labelData { i -> sortedDates[i] } // Utilizza le date come etichette sull'asse x
                .labelAndAxisLinePadding(10.dp)
                .axisLineColor(MaterialTheme.colorScheme.tertiary)
                .axisLabelColor(MaterialTheme.colorScheme.tertiary)
                .build()

            val yAxisData = AxisData.Builder()
                .steps(steps)
                .backgroundColor(Color.Transparent)
                .labelAndAxisLinePadding(20.dp)
                .labelData { i ->
                    val yScale = 100 / steps // 100 = numero max asse y
                    (i * yScale).toString()
                }
                .axisLineColor(MaterialTheme.colorScheme.tertiary)
                .axisLineColor(MaterialTheme.colorScheme.tertiary)
                .build()

            val lineChartData = LineChartData(
                linePlotData = LinePlotData(
                    lines = listOf(
                        Line(
                            dataPoints = points,
                            LineStyle(
                                color = MaterialTheme.colorScheme.tertiary,
                                lineType = LineType.SmoothCurve(isDotted = false)
                            ),
                            IntersectionPoint(
                                color = MaterialTheme.colorScheme.tertiary
                            ),
                            SelectionHighlightPoint(color = MaterialTheme.colorScheme.primary),
                            ShadowUnderLine(
                                alpha = 0.5f,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.inversePrimary,
                                        Color.Transparent
                                    )
                                )
                            ),
                            SelectionHighlightPopUp()
                        )
                    ),
                ),
                backgroundColor = MaterialTheme.colorScheme.surface,
                xAxisData = xAxisData,
                yAxisData = yAxisData,
                gridLines = GridLines(color = MaterialTheme.colorScheme.outlineVariant)
            )

            if (!allUsersFriendsOfCurrentUser.isNullOrEmpty()){
                LineChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp), // se ci sono amici allora il grafico sarà più piccolo
                    lineChartData = lineChartData
                )
            }else{
                LineChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    lineChartData = lineChartData
                )
            }

        }
    }
}

//@Preview
//@Composable
//fun HomePreview() {
//    MusicDraftTheme {
//        Home()
//    }
//}
