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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.musicdraft.data.UIEvent
import com.example.musicdraft.sections.Screens
import com.example.musicdraft.viewModel.LoginViewModel


// Con il parametro loginViewModel: LoginViewModel = viewModel() istanzio e passo al 'SignUpScreen'
// il LoginViewModel.
@Composable
//fun SignUpScreen(navController: NavController, loginViewModel: LoginViewModel = viewModel()) { // c'era prima..
fun SignUpScreen(navController: NavController, loginViewModel: LoginViewModel) {
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

            // TextField "nickname"
            MyTextFieldComponent(
                labelValue = stringResource(id = R.string.nickname),
                Icons.Default.Person,
                // onTextSelected è una funzione di callback che verrà chiamata ogni volta che
                // l'utente inserirà qualcosa all'interno del 'MyTextFieldComponent' corrente
                // nella schermata di creazione dell'account:
                onTextSelected = {
                    // Chiamo la funzione 'onEvent' del loginViewModel e gli passo in input
                    // il tipo di evento che è stato generato (in questo caso il 'UIEvent.NicknameChanged'
                    // in modo tale che il loginViewModel possa modificare lo stato presente al suo interno chiamato
                    // 'registrationUIState' (in questo caso in realtà verrà modificato solo il campo 'registrationUIState.NicknameChanged'
                    // mentre gli altri due (email e password) rimarranno invariati):
                    loginViewModel.onEvent(UIEvent.NicknameChanged(it), navController)
                },
                // passo come 4° parametro al mio 'MyTextFieldComponent' il risultato (booleano) della validazione del campo "nickname"
                // in questo modo qualora questo valore fosse "true" o "false" allora il componente cambierà automaticamente
                // colore in base a quale dei due valori valori è presente in 'nicknameError':
                errorStatus = loginViewModel.registrationUIState.value.nicknameError,
                registration = true // mi trovo sulla schermata di registrazione
            )

            // TextField "email"
            MyTextFieldComponent(
                labelValue = stringResource(id = R.string.email),
                Icons.Default.Email,
                onTextSelected = {
                    // Chiamo la funzione 'onEvent' del loginViewModel e gli passo in input
                    // il tipo di evento che è stato generato (in questo caso il 'UIEvent.EmailChanged'
                    // in modo tale che il loginViewModel possa modificare lo stato presente al suo interno chiamato
                    // registrationUIState (in questo caso in realtà verrà modificato solo il campo 'registrationUIState.EmailChanged'
                    // mentre gli altri due rimarranno invariati):
                    loginViewModel.onEvent(UIEvent.EmailChanged(it), navController)
                },
                errorStatus = loginViewModel.registrationUIState.value.emailError, // aggiornamento colore composable da loginViewModel a interfaccia
                registration = true // mi trovo sulla schermata di registrazione
            )

            // TextField "password"
            PasswordTextFieldComponent(
                labelValue = stringResource(id = R.string.password),
                Icons.Default.Lock,
                // onTextSelected è una funzione di callback che verrà chiamata ogni volta che
                // l'utente inserirà qualcosa all'interno del 'PasswordTextFieldComponent' corrente
                // nella schermata di creazione dell'account:
                onTextSelected = {
                    // Chiamo la funzione 'onEvent' del loginViewModel e gli passo in input
                    // il tipo di evento che è stato generato (in questo caso il 'UIEvent.PasswordChanged'
                    // in modo tale che il loginViewModel possa modificare lo stato presente al suo interno chiamato
                    // registrationUIState (in questo caso in realtà verrà modificato solo il campo 'registrationUIState.PasswordChanged'
                    // mentre gli altri due rimarranno invariati):
                    loginViewModel.onEvent(UIEvent.PasswordChanged(it), navController)
                },
                errorStatus = loginViewModel.registrationUIState.value.passwordError, // aggiornamento colore composable da loginViewModel a interfaccia
                registration = true // mi trovo sulla schermata di registrazione
            )


            CheckboxComponent(
                // parametri del CheckboxComponent:

                value = stringResource(id = R.string.terms_and_conditions),

                // 'onTextSelected' (passato come parametro) definisce una lambda che viene eseguita quando il testo
                // vicino alla checkbox viene selezionato/cliccato, in questo caso
                // si navigherà ad una nuova schermata chiamata "termsAndConditionsScreen":
                onTextSelected = {
                    navController.navigate("termsAndConditionsScreen")
                },

                // 'CheckedChange' (passato come parametro) definisce sempre una lambda che verrà eseguita
                //  quando l'utente selezionerà o deselezionerà la CheckboxComponent. Quando si verificherà
                //  uno di questi due casi verrà generato l'evento 'UIEvent.PrivacyPolicyCheckBoxClicked(it)'
                //  che verrà gestito dal 'loginViewModel' e 'it' rappresenterà il valore dello stato in cui
                //  si trova la CheckboxComponent (true(spuntata) o false(non spuntata):
                CheckedChange = {
                    loginViewModel.onEvent(UIEvent.PrivacyPolicyCheckBoxClicked(it), navController)
                }

            )

            Spacer(modifier = Modifier.height(40.dp))

            // Button di conferma registrazione
            ButtonComponent(value = stringResource(id = R.string.register),
                onButtonClick = {
                    loginViewModel.onEvent(UIEvent.RegisterButtonClick, navController) // quando il button viene cliccato verrà generato l'evento 'UIEvent.RegisterButtonClick' che verrà gestito dal "LoginViewModel"
                },
                // il button di registrazione sarà attivo solamente se tutti i campi della registrazione
                // saranno validi:
                isEnabled = loginViewModel.allValidationCompleted.value

            )

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