package com.example.musicdraft.sections

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.musicdraft.data.tables.artisti.Artisti
import com.example.musicdraft.data.tables.track.Track
import com.example.musicdraft.data.tables.user_cards.User_Cards_Artisti
import com.example.musicdraft.data.tables.user_cards.User_Cards_Track
import com.example.musicdraft.viewModel.CardsViewModel


@Composable
fun Cards(viewModel: CardsViewModel) {

    // Variabili di stato per la gestione della scheda selezionata e dei filtri
    var selectedTab by remember { mutableStateOf(0) }
    var popThreshold by remember { mutableStateOf("") }
    var nameQuery by remember { mutableStateOf("") }
    var genreQuery by remember { mutableStateOf("") }
    viewModel.getallcards()
    // Ottieni la lista degli artisti e delle tracce in base alla scheda selezionata e ai filtri applicati
    val artisti by viewModel.acquiredCardsA.collectAsState(emptyList())
    val brani by viewModel.acquiredCardsT.collectAsState(emptyList())
    val mercatoA by viewModel.MarketArtist.collectAsState(emptyList())



    // Composable principale per la schermata del Marketplace
    Column(modifier = Modifier.padding(top = 65.dp)) {
        // TabRow per la navigazione tra Artisti e Brani
        TabRow(selectedTabIndex = selectedTab, modifier = Modifier.fillMaxWidth()) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("Artisti")
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("Brani")
            }
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                Text("In Vendita")
            }
        }
        // Visualizza i filtri corrispondenti alla scheda selezionata

        // Visualizza la lista degli artisti o delle tracce in base alla scheda selezionata
        when (selectedTab) {
            0 -> artisti?.let { ArtistiScreen(it,viewModel,true) }
            1 -> brani?.let { BraniScreen(it,viewModel) }
            2 -> mercatoA?.let { brani?.let { it1 -> CarteinVendita(it1, it,viewModel,false) } }
        }
    }
}

@Composable
fun CarteinVendita(
    brani: List<User_Cards_Track>,
    artisti: List<User_Cards_Artisti>,
    viewModel: CardsViewModel,
    m:Boolean
) {
    // Visualizza una griglia di carte per i brani
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        // Itera attraverso i brani e visualizza una carta per ciascuno
        items(artisti.size){index->
            if(artisti[index].onMarket==true){
                ArtistaCard(artisti[index], Modifier.height(8.dp),viewModel,m)
            }

        }
        items(brani.size) { index ->
            if(brani[index].onMarket==true) {
                BranoCard(brani[index], Modifier.height(8.dp),viewModel)
            }
        }

    }
}




/**
 * Composable per i filtri degli artisti.
 * @param popThreshold Popolarità massima per il filtro.
 * @param nameQuery Query per il nome dell'artista.
 * @param genreQuery Query per il genere dell'artista.
 * @param onPopThresholdChange Callback per la modifica della popolarità massima.
 * @param onNameQueryChange Callback per la modifica del nome dell'artista.
 * @param onGenreQueryChange Callback per la modifica del genere dell'artista.
 * @param onApplyFilter Callback per l'applicazione del filtro.
 */

@Composable
fun ArtistiFilter(
    popThreshold: String,
    nameQuery: String,
    genreQuery: String,
    onPopThresholdChange: (String) -> Unit,
    onNameQueryChange: (String) -> Unit,
    onGenreQueryChange: (String) -> Unit,
    onApplyFilter: () -> Unit
) {
    // Column per i filtri degli artisti
    Column(modifier = Modifier.padding(16.dp)) {
        // TextFields per i filtri della popolarità, del nome e del genere dell'artista
        TextField(
            value = popThreshold,
            onValueChange = { onPopThresholdChange(it) },
            label = { Text("Popolarità massima") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = nameQuery,
            onValueChange = { onNameQueryChange(it) },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = genreQuery,
            onValueChange = { onGenreQueryChange(it) },
            label = { Text("Genere") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = { onApplyFilter() }) {
            Text("Applica filtro")
        }
    }
}
/**
 * Composable per i filtri dei brani.
 * @param popThreshold Popolarità massima per il filtro.
 * @param onPopThresholdChange Callback per la modifica della popolarità massima.
 * @param onApplyFilter Callback per l'applicazione del filtro.
 */
@Composable
fun BraniFilter(
    popThreshold: String,
    onPopThresholdChange: (String) -> Unit,
    onApplyFilter: () -> Unit
) {
    // Column per i filtri dei brani
    Column(modifier = Modifier.padding(16.dp)) {
        // TextField per il filtro della popolarità massima
        TextField(
            value = popThreshold,
            onValueChange = { onPopThresholdChange(it) },
            label = { Text("Popolarità massima") },
            modifier = Modifier.fillMaxWidth()
        )
        // Button per l'applicazione del filtro
        Button(onClick = { onApplyFilter() }) {
            Text("Applica filtro")
        }
    }
}


/**
 * Composable per la visualizzazione degli artisti.
 * @param artisti Elenco degli artisti da visualizzare.
 */
@Composable
fun ArtistiScreen(artisti: List<User_Cards_Artisti>, viewModel: CardsViewModel,m:Boolean) {
    // Visualizza una griglia di carte per gli artisti
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        // Itera attraverso gli artisti e visualizza una carta per ciascuno
        items(artisti.size) { index ->
            ArtistaCard(artisti[index], Modifier.height(8.dp),viewModel,m)
        }
    }
}
/**
 * Composable per la visualizzazione dei brani.
 * @param brani Elenco dei brani da visualizzare.
 */
@Composable
fun BraniScreen(brani: List<User_Cards_Track>, viewModel: CardsViewModel) {
    // Visualizza una griglia di carte per i brani
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        // Itera attraverso i brani e visualizza una carta per ciascuno
        items(brani.size) { index ->
            BranoCard(brani[index], Modifier.height(8.dp),viewModel)
        }
    }
}

/**
 * Composable per la visualizzazione di una carta di un brano.
 * @param brano Oggetto [Track] rappresentante il brano da visualizzare.
 * @param height Modificatore per la altezza della carta.
 */
@Composable
fun BranoCard(brano: User_Cards_Track, height: Modifier,viewModel: CardsViewModel) {
    // Carta contenente le informazioni del brano
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = brano.nome, style = MaterialTheme.typography.bodyLarge)
            Text(text = brano.anno_pubblicazione)
            Text(text = brano.durata)
            Text(text = "Pop: ${brano.popolarita}")
            Text(text = "Costo: ${brano.popolarita*10}")
            // Immagine del brano con dimensioni specificate
            Image(
                painter = rememberAsyncImagePainter(brano.immagine),
                contentDescription = null,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Button(onClick = {viewModel.vendi_track(brano) }) {
                Text("Vendi")
            }
        }
    }
}

/**
 * Composable per la visualizzazione di una carta di un artista.
 * @param artista Oggetto [Artisti] rappresentante l'artista da visualizzare.
 * @param height Modificatore per la altezza della carta.
 */
@Composable
fun ArtistaCard(artista: User_Cards_Artisti, height: Modifier,viewModel: CardsViewModel,m:Boolean) {
    // Carta contenente le informazioni dell'artista
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = artista.nome, style = MaterialTheme.typography.bodyLarge)
            Text(text = artista.genere)
            Text(text = "Pop: ${artista.popolarita}")
            Text(text = "Costo: ${artista.popolarita*10}")
            // Immagine dell'artista con dimensioni specificate
            Image(
                painter = rememberAsyncImagePainter(artista.immagine),
                contentDescription = null,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Button(onClick = { viewModel.vendi_artista(artista)}, enabled = m) {
                Text("Vendi")
            }
        }
    }
}







