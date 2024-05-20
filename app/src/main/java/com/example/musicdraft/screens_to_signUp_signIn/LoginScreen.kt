package com.example.musicdraft.screens_to_signUp_signIn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.musicdraft.R
import com.example.musicdraft.components.ButtonComponent
import com.example.musicdraft.components.ClickableLoginTextComponent
import com.example.musicdraft.components.DividerTextComponent
import com.example.musicdraft.components.HeadingTextComponent
import com.example.musicdraft.components.MyTextFieldComponent
import com.example.musicdraft.components.NormalTextComponent
import com.example.musicdraft.components.PasswordTextFieldComponent
import com.example.musicdraft.components.UnderLinedNormalTextComponent
import com.example.musicdraft.data.UIEvent
import com.example.musicdraft.viewModel.LoginViewModel

@Composable
fun LoginScreen(navController: NavController, loginViewModel: LoginViewModel = viewModel()){

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
                    // registrationUIState (in questo caso in realtà verrà modificato solo il campo 'registrationUIState.NicknameChanged'
                    // mentre gli altri due rimarranno invariati):
                    loginViewModel.onEvent(UIEvent.NicknameChanged(it))
                })
            PasswordTextFieldComponent(
                stringResource(id = R.string.password),
                Icons.Default.Lock,
                onTextSelected = {
                    // Chiamo la funzione 'onEvent' del loginViewModel e gli passo in input
                    // il tipo di evento che è stato generato (in questo caso il 'UIEvent.PasswordChanged'
                    // in modo tale che il loginViewModel possa modificare lo stato presente al suo interno chiamato
                    // registrationUIState (in questo caso in realtà verrà modificato solo il campo 'registrationUIState.PasswordChanged'
                    // mentre gli altri due rimarranno invariati):
                    loginViewModel.onEvent(UIEvent.PasswordChanged(it))
                })

            Spacer(modifier = Modifier.height(40.dp)) // spazio
            UnderLinedNormalTextComponent(stringResource(id = R.string.forgot_password))

            Spacer(modifier = Modifier.height(40.dp)) // spazio
            ButtonComponent(value = stringResource(id = R.string.login),
                onButtonClick = {
                    loginViewModel.onEvent(UIEvent.RegisterButtonClick)
                })

            Spacer(modifier = Modifier.height(20.dp)) // spazio
            DividerTextComponent()

            ClickableLoginTextComponent(tryingToLogin = false, onTextSelected = {
                navController.navigate("signUp")
            })

        }
    }

}


//@Preview
//@Composable
//fun LoginScreenPrewview(){
//    LoginScreen()
//}
