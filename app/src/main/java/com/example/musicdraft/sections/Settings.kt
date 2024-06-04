package com.example.musicdraft.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.musicdraft.components.ButtonComponent
import com.example.musicdraft.ui.theme.BlueApp
import com.example.musicdraft.ui.theme.MusicDraftTheme
import com.example.musicdraft.viewModel.LoginViewModel


@Composable
fun Settings(navController: NavController, loginViewModel: LoginViewModel) {

    // definisco una box
    Box(modifier = Modifier.fillMaxSize()){
        // all'interno del box ci sarà una colonna
        Column(modifier = Modifier
            .fillMaxSize()
            .align(Alignment.Center), // colonna allineata centralmente
            verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            // inserisco un text all'interno della colonna
            Text(text = "Settings", fontSize = 30.sp, color = BlueApp) // GreenJC colore definito in "Color.kt"
        
            ButtonComponent(
                value = "Logout",
                onButtonClick = {
                    loginViewModel.logoutFromFirebase(navController)
                },
                isEnabled = true
            )
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