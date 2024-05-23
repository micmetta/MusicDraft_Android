package com.example.musicdraft.screens_to_signUp_signIn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.musicdraft.R
import com.example.musicdraft.components.ButtonComponentLogin
import com.example.musicdraft.components.ClickableLoginTextComponent
import com.example.musicdraft.components.DividerTextComponent
import com.example.musicdraft.components.HeadingTextComponent
import com.example.musicdraft.components.MyTextFieldComponent
import com.example.musicdraft.components.NormalTextComponent
import com.example.musicdraft.components.PasswordTextFieldComponent
import com.example.musicdraft.components.UnderLinedNormalTextComponent
import com.example.musicdraft.data.UIEventSignIn
import com.example.musicdraft.data.UIEventSignUp
import com.example.musicdraft.viewModel.LoginViewModel

@Composable
//fun LoginScreen(navController: NavController, loginViewModel: LoginViewModel = viewModel()){ // c'era prima..
fun LoginScreen(navController: NavController, loginViewModel: LoginViewModel){

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Surface(
            color = Color.White,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(28.dp)
        ){

            Column(modifier = Modifier
                .fillMaxSize()
            ){

                NormalTextComponent(value = stringResource(id = R.string.login))
                HeadingTextComponent(value = stringResource(id = R.string.welcome))
                Spacer(modifier = Modifier.height(20.dp)) // spazio

                MyTextFieldComponent(
                    stringResource(id = R.string.email),
                    Icons.Default.Email, // onTextSelected è una funzione di callback che verrà chiamata ogni volta che
                    // l'utente inserirà qualcosa all'interno del 'MyTextFieldComponent' corrente
                    // nella schermata di creazione dell'account:
                    onTextSelected = {
                        // Chiamo la funzione 'onEvent' del loginViewModel e gli passo in input
                        // il tipo di evento che è stato generato (in questo caso il 'UIEvent.NicknameChanged'
                        // in modo tale che il loginViewModel possa modificare lo stato presente al suo interno chiamato
                        // loginUIState (in questo caso in realtà verrà modificato solo il campo 'loginUIState.NicknameChanged'
                        // mentre gli altri due rimarranno invariati):
                        loginViewModel.onEvent(UIEventSignIn.EmailChanged(it), navController)
                    })
                PasswordTextFieldComponent(
                    stringResource(id = R.string.password),
                    Icons.Default.Lock,
                    onTextSelected = {
                        // Chiamo la funzione 'onEvent' del loginViewModel e gli passo in input
                        // il tipo di evento che è stato generato (in questo caso il 'UIEvent.PasswordChanged'
                        // in modo tale che il loginViewModel possa modificare lo stato presente al suo interno chiamato
                        // loginUIState (in questo caso in realtà verrà modificato solo il campo 'loginUIState.PasswordChanged'
                        // mentre gli altri due rimarranno invariati):
                        loginViewModel.onEvent(UIEventSignIn.PasswordChanged(it), navController)
                    })

                Spacer(modifier = Modifier.height(40.dp)) // spazio
                UnderLinedNormalTextComponent(stringResource(id = R.string.forgot_password))

                Spacer(modifier = Modifier.height(40.dp)) // spazio
                ButtonComponentLogin(value = stringResource(id = R.string.login),
                    onButtonClick = {
                        loginViewModel.onEvent(UIEventSignIn.LoginButtonClick, navController)
                    },
                    // il button di login sarà attivo solamente se tutti i campi della schermata login
                    // saranno validi:
                    isEnabled = loginViewModel.allValidationCompletedLogin.value
                )

                Spacer(modifier = Modifier.height(20.dp)) // spazio
                DividerTextComponent()

                ClickableLoginTextComponent(tryingToLogin = false, onTextSelected = {
                    navController.navigate("signUp")
                })

            }
        }

        // controllo che lo stato "signUpInProgress" presente in "loginViewModel"
        // sia true:
        if(loginViewModel.signInInProgress.value){
            // In questo caso inserisco l'indicatore circolare di caricamento
            // che verrà mostrato subito dopo che l'utente
            // avrà cliccato su "Register":
            CircularProgressIndicator()
        }

    }

}


//@Preview
//@Composable
//fun LoginScreenPrewview(){
//    LoginScreen()
//}
