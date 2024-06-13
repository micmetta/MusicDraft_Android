package com.example.musicdraft.utility

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.musicdraft.viewModel.LoginViewModel

@Composable
fun UpdateNickname(navController: NavController, loginViewModel: LoginViewModel){
    //var currentEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    //var currentNickname by remember { mutableStateOf("") }
    var newNickname by remember { mutableStateOf("") }

    var showDialogUpdateNickname by loginViewModel.showDialogUpdateNickname // per catturare l'invio dell'email dal loginViewModel
    var messageDialogErrorUpdateNickname by loginViewModel.messageDialogErrorUpdateNickname
    var showDialogErrorUpdateNickname by loginViewModel.showDialogErrorUpdateNickname
    var showDialogPasswordEmpty by remember { mutableStateOf(false) }
    var showDialogNewNicknameEmpty by remember { mutableStateOf(false) }

    val infoUserCurrent by loginViewModel.userLoggedInfo.collectAsState(initial = null)
    var currentEmail = infoUserCurrent?.email ?: ""
    var currentNickname = infoUserCurrent?.nickname ?: ""
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        // current email:
        Text(
            text = "Your current email:",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(bottom = 8.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        TextField(
            value = currentEmail,
            onValueChange = {},
            label = {},
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            singleLine = true,
            enabled = false, // make the TextField read-only
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // password:
        Text(
            text = "Enter your password below:",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(bottom = 8.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    Icon(imageVector = icon, contentDescription = "Toggle password visibility")
                }
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Lock, contentDescription = "Password lock icon")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.height(16.dp))

        // current nickname:
        Text(
            text = "Your current nickname:",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(bottom = 8.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        TextField(
            value = currentNickname,
            onValueChange = {},
            label = {},
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            singleLine = true,
            enabled = false, // make the TextField read-only
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        )

        // new nickname:
        Text(
            text = "Enter your new nickname below:",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(bottom = 8.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        TextField(
            value = newNickname,
            onValueChange = { newNickname = it },
            label = { Text("New nickname") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if((password.isNotEmpty()) && (newNickname.isNotEmpty())){
                    loginViewModel.updateNickname(currentEmail, password, currentNickname, newNickname)
                }else if(!password.isNotEmpty()){
                    showDialogPasswordEmpty = true
                }else{
                    showDialogNewNicknameEmpty = true
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "change nickname",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    if(showDialogUpdateNickname){
        AlertDialog(
            onDismissRequest = {
                showDialogUpdateNickname = false
                navController.popBackStack() // ritorno alla schermata precedente (ovvero 'Settings')
            },
            title = { Text(text = "Success") },
            text = { Text(text = "Nickname updated successfully!") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialogUpdateNickname = false
                        navController.popBackStack() // ritorno alla schermata precedente (ovvero 'Settings')
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    if(showDialogErrorUpdateNickname){
        AlertDialog(
            onDismissRequest = { showDialogErrorUpdateNickname = false },
            title = { Text(text = "Error") },
            text = { Text(text = messageDialogErrorUpdateNickname) },
            confirmButton = {
                TextButton(
                    onClick = { showDialogErrorUpdateNickname = false }
                ) {
                    Text("OK")
                }
            }
        )
    }

    if(showDialogPasswordEmpty){
        AlertDialog(
            onDismissRequest = { showDialogPasswordEmpty = false },
            title = { Text(text = "Error") },
            text = { Text(text = "The field Password is empty!") },
            confirmButton = {
                TextButton(
                    onClick = { showDialogPasswordEmpty = false }
                ) {
                    Text("OK")
                }
            }
        )
    }

    if(showDialogNewNicknameEmpty){
        AlertDialog(
            onDismissRequest = { showDialogNewNicknameEmpty = false },
            title = { Text(text = "Error") },
            text = { Text(text = "The Field 'New nickname' is empty!") },
            confirmButton = {
                TextButton(
                    onClick = { showDialogNewNicknameEmpty = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
}