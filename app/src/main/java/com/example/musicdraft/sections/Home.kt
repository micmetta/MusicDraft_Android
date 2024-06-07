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
import androidx.compose.ui.unit.sp
import com.example.musicdraft.ui.theme.BlueApp
import com.example.musicdraft.viewModel.LoginViewModel


// Adesso creati il viewModel che prenderà i points dell'utente dal DB, calcolerà il suo rank in base alle carte che possiede,
// i suoi amici.. E CAMBIA IL COLORE DEI COMPONENTI IN MODO DA RENDERLI PIU' VICINI A QUELLI DELL'app

@Composable
fun Home(loginViewModel: LoginViewModel) {

    // definisco una box
    Box(modifier = Modifier.fillMaxSize()) {
        // all'interno del box ci sarà una colonna
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center), // colonna allineata centralmente
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // inserisco un text all'interno della colonna
            Text(
                text = "Decks",
                fontSize = 30.sp,
                color = BlueApp
            ) // GreenJC colore definito in "Color.kt"
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
