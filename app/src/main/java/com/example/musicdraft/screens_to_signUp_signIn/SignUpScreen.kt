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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.musicdraft.R
import com.example.musicdraft.components.ButtonComponent
import com.example.musicdraft.components.CheckboxComponent
import com.example.musicdraft.components.ClickableLoginTextComponent
import com.example.musicdraft.components.DividerTextComponent
import com.example.musicdraft.components.HeadingTextComponent
import com.example.musicdraft.components.MyTextFieldComponent
import com.example.musicdraft.components.NormalTextComponent
import com.example.musicdraft.components.PasswordTextFieldComponent
import com.example.musicdraft.sections.Screens

@Composable
fun SignUpScreen(navController: NavController) {
    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp)
    ){
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            NormalTextComponent(value = stringResource(id = R.string.hello)) // componente creato in "components"

            HeadingTextComponent(value = stringResource(id = R.string.create_account)) // componente creato in "components"

            Spacer(modifier = Modifier.height(20.dp)) // inserisco dello spazio

            MyTextFieldComponent(
                labelValue = stringResource(id = R.string.nickname),
                Icons.Default.Person
            )
            MyTextFieldComponent(
                labelValue = stringResource(id = R.string.email),
                Icons.Default.Email
            )
            PasswordTextFieldComponent(
                labelValue = stringResource(id = R.string.password),
                Icons.Default.Lock
            )
            CheckboxComponent(
                value = stringResource(id = R.string.terms_and_conditions),
                onTextSelected = {
                    navController.navigate("termsAndConditionsScreen")
                })

            Spacer(modifier = Modifier.height(40.dp))

            ButtonComponent(value = stringResource(id = R.string.register))

            Spacer(modifier = Modifier.height(20.dp))

            DividerTextComponent()

            ClickableLoginTextComponent(tryingToLogin = true, onTextSelected = {
                navController.navigate("login")
            })

//            // Adesso qui da qualche parte prima o poi quando l'utente cliccherà su un bottone (ad esempio per confermare i dati) e l'account sarà
//            // validato, allora la schermata che dovrà aprirsi sarà quella creata dal Composable "MusicDraftUI()" in questo modo l'utente entrerà veramente
//            // all'interno dell'app.
//            Button(onClick = { navController.navigate("musicDraftUI"){
//                popUpTo(0) // in questo modo nello stack non mantengo memorizzato la shermata di login o crea_account precedente.
//            } }) {}

        }
    }
}

//@Preview
//@Composable
//fun DefaultPreviewOfSignUpScreen(){
//    SignUpScreen()
//}