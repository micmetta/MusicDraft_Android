package com.example.musicdraft.screens_to_signUp_signIn

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.musicdraft.viewModel.LoginViewModel


/**
- Schermata che permette all'utente di reimpostare la password tramite l'utilizzo del servizio di Firebase.
*/
@Composable
fun ForgotPassword(navController: NavController, loginViewModel: LoginViewModel){

    var email by remember { mutableStateOf("") }
    val showDialogAttention = remember { mutableStateOf(false) }
    var showDialogSentEmail by loginViewModel.showDialogSentEmail // per catturare l'invio dell'email dal loginViewModel
    var showDialogErrorSentEmail by loginViewModel.showDialogErrorSentEmail


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Enter your recovery email below:",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                showDialogAttention.value = true
                //loginViewModel.forgotPassword(email)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("send email")
        }
    }


    if(showDialogAttention.value){
        AlertDialog(
            onDismissRequest = {
                showDialogAttention.value = false
            },
            title = { Text(text = "Attention") },
            text = { Text(text = "Attention, before clicking on 'yes' you must be sure that the recovery email entered is an existing email otherwise you risk not being able to re-enter the application!\n" +
                                "\n" +
                                "Are you sure you want to continue with the password recovery process?")
                   },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialogAttention.value = false
                        loginViewModel.forgotPassword(email)
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialogAttention.value = false
                }
                ) {
                    Text("No")
                }
            }
        )
    }


    if(showDialogSentEmail){
        AlertDialog(
            onDismissRequest = {
                showDialogSentEmail = false
//                navController.navigate(Screens.Login.screen)
                loginViewModel.logoutFromFirebase(navController) // una volta inviata la mail di reset eseguo il logout dell'utente corrente
                // in modo tale che la prossima volta che vorrà entrare nell'app dovrà per forza reinserire la password.
            },
            title = { Text(text = "Success") },
            text = { Text(text = "Email sent, check it to reset your password!") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialogSentEmail = false
//                        navController.navigate(Screens.Login.screen)
                        loginViewModel.logoutFromFirebase(navController) // una volta inviata la mail di reset eseguo il logout dell'utente corrente
                        // in modo tale che la prossima volta che vorrà entrare nell'app dovrà per forza reinserire la password.
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    if(showDialogErrorSentEmail){
        AlertDialog(
            onDismissRequest = { showDialogErrorSentEmail = false },
            title = { Text(text = "Error") },
            text = { Text(text = "The email entered is incorrect, please try again..") },
            confirmButton = {
                TextButton(
                    onClick = { showDialogErrorSentEmail = false }
                ) {
                    Text("OK")
                }
            }
        )
    }

}