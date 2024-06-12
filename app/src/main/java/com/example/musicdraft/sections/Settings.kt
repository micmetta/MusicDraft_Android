package com.example.musicdraft.sections

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.musicdraft.data.UIEventSignIn
import com.example.musicdraft.ui.theme.Secondary
import com.example.musicdraft.viewModel.LoginViewModel

// c'era prima..
@Composable
fun Settings(navController: NavController, loginViewModel: LoginViewModel) {
    val infoUserCurrent by loginViewModel.userLoggedInfo.collectAsState(initial = null)

    infoUserCurrent?.let {
        Log.d("Settings", "Sono dentro la schermata Settings() e..:")
        Log.d("Settings", "Sono dentro la schermata Settings() e l'email dell'utente è il seguente: " + infoUserCurrent!!.email)
        Log.d("Settings", "Sono dentro la schermata Settings() e il nickname dell'utente è il seguente: " + infoUserCurrent!!.nickname)
        Log.d("Settings", "Sono dentro la schermata Settings() e i points dell'utente sono i seguenti: " + infoUserCurrent!!.points)



        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
//                    .align(Alignment.Center),
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally
                    .padding(10.dp)
            ) {

                Spacer(modifier = Modifier.height(80.dp))

                Text(
                    text = "In this section you can update email, nickname and password:",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Secondary)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Current email: ${infoUserCurrent!!.email}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
                Button(
                    onClick = {
                        // Logica per cambiare l'email
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Change email",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Secondary)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Current nickname: ${infoUserCurrent!!.nickname}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
                Button(
                    onClick = {
                        // Logica per cambiare il nickname
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Change nickname",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Secondary)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Do you want change password?",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
                Button(
                    onClick = {
                        // Logica per cambiare la password
                        loginViewModel.onEvent(UIEventSignIn.forgotPassword, navController)
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Change password",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

            }
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