package com.example.musicdraft.sections

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.musicdraft.data.tables.artisti.Artisti
import com.example.musicdraft.data.tables.track.Track
import com.example.musicdraft.ui.theme.BlueApp
import com.example.musicdraft.ui.theme.MusicDraftTheme

@Composable
fun Cards() {

    val artisti = listOf(
        Artisti("Nome1", "Cognome1", "url_to_image1", "dotto", 12),
        Artisti("Nome2", "Cognome2", "url_to_image_2", "wewe", 14)
    )

    val brani = listOf(
        Track("Nome1", "Cognome1", "2023", "3:45", "url_to_image1", 85),
        Track("Nome2", "Cognome2", "2022", "4:00", "url_to_image2", 76)
    )

    var selectedTab by remember { mutableStateOf(0) }

    Column {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("Artisti")
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("Brani")
            }
        }
        when (selectedTab) {
            0 -> ArtistiScreen(artisti)
            1 -> BraniScreen(brani)
        }
    }
}



@Composable
fun BraniScreen(brani: List<Track>) {
    LazyColumn {
        items(brani.size) { index ->
            BranoCard(brani[index])
        }
    }
}

@Composable
fun BranoCard(brano: Track) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "${brano.nome} ", style = MaterialTheme.typography.bodyLarge)
            Text(text = brano.anno_pubblicazione)
            Text(text = brano.durata)
            Text(text = "Pop: ${brano.popolarita}")
            Image(
                painter = rememberImagePainter(brano.immagine),
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
    fun ArtistaCard(artista: Artisti) {
        Card(modifier = Modifier.padding(8.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "${artista.nome}", style = MaterialTheme.typography.bodyLarge)
                Text(text = artista.genere)
                Text(text = "Pop: ${artista.popolarita}")
                Image(
                    painter = rememberImagePainter(artista.immagine),
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
    fun ArtistiScreen(artisti: List<Artisti>) {
        LazyColumn {
            items(artisti.size) { index ->
                ArtistaCard(artisti[index])
            }
        }
    }








//// per lanciare la preview
//@Preview
//@Composable
//fun SettingsPreview(){
//    MusicDraftTheme {
//        Settings()
//    }
//}