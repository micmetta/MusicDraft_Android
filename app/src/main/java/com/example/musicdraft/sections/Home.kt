package com.example.musicdraft.sections

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.musicdraft.viewModel.LoginViewModel


// Adesso creati il viewModel che prenderà i points dell'utente dal DB, calcolerà il suo rank in base alle carte che possiede,
// i suoi amici.. E CAMBIA IL COLORE DEI COMPONENTI IN MODO DA RENDERLI PIU' VICINI A QUELLI DELL'app

@Composable
fun Home(loginViewModel: LoginViewModel) {

    val infoUserCurrent by loginViewModel.userLoggedInfo.collectAsState(initial = null)

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Se non c'è nessun utente attivo allora
    // richiedo l'update di 'userLoggedInfo' e quindi automaticamente anche 'infoUserCurrent' sarà aggiornato.
    // Se invece dovesse esserci già un utente attivo allora 'infoUserCurrent' già conterrà i dati dell'utente attivo.
    if (loginViewModel.isUSerLoggedIn.value == false){
        loginViewModel.getUserByEmail()
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    infoUserCurrent?.let{
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

        Spacer(modifier = Modifier.height(60.dp))

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
                    .padding(16.dp),
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
                modifier = Modifier.padding(16.dp)
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

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Testo "Rank" e valore "0" concatenati
                    Text(
                        text = "Rank: 0",
                        style = MaterialTheme.typography.headlineMedium,
                    )
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
            LineChartComposable()
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////

        Spacer(modifier = Modifier.height(16.dp))

//        ////////////////////////////////////////////////////////////////////////////////////////////////////
//        // Sezione Amici Online
//        Text(
//            text = "Online Friends",
//            style = MaterialTheme.typography.headlineMedium
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(4.dp),
//            verticalArrangement = Arrangement.spacedBy(4.dp)
//        ) {
//            items(items = listOf("Ada Lovelace", "Mark Hopper", "Margaret Hamilton")) { playerName ->
//                PlayerCard(playerName = playerName)
//            }
//        }
//        ////////////////////////////////////////////////////////////////////////////////////////////////////

    }
}

@Composable
fun LineChartComposable() {
    val steps = 10
    val pointsData = mapOf(
        "01-06-2022" to 40f,
        "02-06-2022" to 90f,
        "03-06-2022" to 0f,
        "04-06-2022" to 60f,
        "05-06-2022" to 10f,
        "06-06-2022" to 80f,
        "07-06-2022" to 45f,
        "08-06-2022" to 39f,
        "09-06-2022" to 100f,
        "10-06-2022" to 90f,
    )

    // Estraggo le chiavi (le date) dalla mappa e ordino le date
    val sortedDates = pointsData.keys.toList().sorted()

    // Crea i punti utilizzando i valori float corrispondenti alle date
    val points = sortedDates.mapIndexed { index, date ->
        Point(index.toFloat(), pointsData[date]!!)
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

    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        lineChartData = lineChartData
    )
}


//@Composable
//fun BarChartScreen() {
//    val stepSize = 5
//    val barsData = DataUtils.getBarChartData(
//        listSize = 8,
//        maxRange = 8,
//        barChartType = BarChartType.VERTICAL,
//        dataCategoryOptions = DataCategoryOptions()
//    )
//
//    // Lista di date specificate manualmente
//    val dates = listOf("01/06", "02/06", "03/06", "04/06", "05/06", "06/06", "07/06", "08/06")
//
//
//    val xAxisData = AxisData.Builder()
//        .axisStepSize(100.dp)
//        .steps(barsData.size - 1)
//        .labelAndAxisLinePadding(40.dp)
//        .axisLabelAngle(20f)
//        .labelData { index -> barsData[index].label }
//        .axisLineColor(MaterialTheme.colorScheme.tertiary)
//        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
//        .build()
//
//    val yAxisData = AxisData.Builder()
//        .steps(stepSize)
//        .labelAndAxisLinePadding(20.dp)
//        .axisOffset(20.dp)
//        .labelData {index -> (index * (100 / stepSize)).toString()}
//        .axisLineColor(MaterialTheme.colorScheme.tertiary)
//        .axisLineColor(MaterialTheme.colorScheme.tertiary)
//        .build()
//
//    val barChartData = BarChartData(
//        chartData = barsData,
//        xAxisData = xAxisData,
//        yAxisData = yAxisData,
//        backgroundColor = MaterialTheme.colorScheme.surface
//    )
//
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Bottom
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(350.dp)
//                .padding(start = 50.dp) // Aggiungi margine a sinistra per l'asse x
//        ) {
//            BarChart(
//                modifier = Modifier.fillMaxSize(),
//                barChartData = barChartData
//            )
//        }
//        LazyRow(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 50.dp),
//            horizontalArrangement = Arrangement.spacedBy(20.dp)
//        ) {
//            items(dates) { date ->
//                Text(
//                    text = date,
//                    style = MaterialTheme.typography.bodySmall,
//                    modifier = Modifier.width(60.dp)
//                )
//            }
//        }
//    }
//}


@Composable
fun PlayerCard(playerName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Text(
            text = playerName,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
fun OnlinePlayerIndicator(playerName: String) {
    Column(
        modifier = Modifier.size(48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.secondary)
                .clip(androidx.compose.foundation.shape.CircleShape)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(text = playerName, style = MaterialTheme.typography.bodySmall)
    }
}

//@Preview
//@Composable
//fun HomePreview() {
//    MusicDraftTheme {
//        Home()
//    }
//}
