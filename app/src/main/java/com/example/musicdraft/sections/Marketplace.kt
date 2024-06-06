import androidx.compose.foundation.Image
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.musicdraft.data.tables.artisti.Artisti
import com.example.musicdraft.data.tables.track.Track
import com.example.musicdraft.viewModel.MarketplaceViewModel

@Composable
fun Marketplace(viewModel: MarketplaceViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    var popThreshold by remember { mutableStateOf("") }
    var nameQuery by remember { mutableStateOf("") }
    var genreQuery by remember { mutableStateOf("") }

    val artisti by if (!(popThreshold.isNullOrEmpty())) {
        viewModel.filteredArtisti.observeAsState(emptyList())
    } else {
        viewModel.allArtist.observeAsState(emptyList())
    }

    val brani by if (popThreshold == "") {
        viewModel.filteredBrani.observeAsState(emptyList())
    } else {
        viewModel.allTracks.observeAsState(emptyList())
    }

    Column(modifier = Modifier.padding(top = 65.dp)) {
        TabRow(selectedTabIndex = selectedTab, modifier = Modifier.fillMaxWidth()) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("Artisti")
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("Brani")
            }
        }

        if (selectedTab == 0) {
            ArtistiFilter(
                popThreshold = popThreshold,
                nameQuery = nameQuery,
                genreQuery = genreQuery,
                onPopThresholdChange = { popThreshold = it },
                onNameQueryChange = { nameQuery = it },
                onGenreQueryChange = { genreQuery = it },
                onApplyFilter = {
                    viewModel.applyArtistFilter(
                        popThreshold.toIntOrNull(),
                        nameQuery.takeIf { it.isNotEmpty() },
                        genreQuery.takeIf { it.isNotEmpty() }
                    )
                }
            )
        } else {
            BraniFilter(
                popThreshold = popThreshold,
                onPopThresholdChange = { popThreshold = it },
                onApplyFilter = {
                    viewModel.applyBraniFilter(
                        popThreshold.toIntOrNull()
                    )
                }
            )
        }

        when (selectedTab) {
            0 -> ArtistiScreen(artisti)
            1 -> BraniScreen(brani)
        }
    }
}
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
    Column(modifier = Modifier.padding(16.dp)) {
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

@Composable
fun BraniFilter(
    popThreshold: String,
    onPopThresholdChange: (String) -> Unit,
    onApplyFilter: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = popThreshold,
            onValueChange = { onPopThresholdChange(it) },
            label = { Text("Popolarità massima") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = { onApplyFilter() }) {
            Text("Applica filtro")
        }
    }
}

//pafina delle CARDS


@Composable
fun ArtistiScreen(artisti: List<Artisti>) {
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(artisti.size) { index ->
            ArtistaCard(artisti[index], Modifier.height(8.dp))
        }
    }
}

@Composable
fun BraniScreen(brani: List<Track>) {
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(brani.size) { index ->
            BranoCard(brani[index], Modifier.height(8.dp))
        }
    }
}


@Composable
fun BranoCard(brano: Track, height: Modifier) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = brano.nome, style = MaterialTheme.typography.bodyLarge)
            Text(text = brano.anno_pubblicazione)
            Text(text = brano.durata)
            Text(text = "Pop: ${brano.popolarita}")
            Image(
                painter = rememberAsyncImagePainter(brano.immagine),
                contentDescription = null,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Button(onClick = { /* openVendiPopup logic here */ }) {
                Text("Vendi")
            }
        }
    }
}

@Composable
fun ArtistaCard(artista: Artisti, height: Modifier) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = artista.nome, style = MaterialTheme.typography.bodyLarge)
            Text(text = artista.genere)
            Text(text = "Pop: ${artista.popolarita}")
            Image(
                painter = rememberAsyncImagePainter(artista.immagine),
                contentDescription = null,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Button(onClick = { /* openVendiPopup logic here */ }) {
                Text("Vendi")
            }
        }
    }
}


