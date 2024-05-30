package com.example.musicdraft.screens_to_signUp_signIn

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
import com.example.musicdraft.data.UIEventSignUp
import com.example.musicdraft.utility.Constant.ServerClient
import com.example.musicdraft.viewModel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch


// - Con il parametro loginViewModel: LoginViewModel = viewModel() istanzio e passo al 'SignUpScreen'
//   il LoginViewModel.
// - I parametri state e onSignInClick() -> Click di questo composable servono per gestire il SignUp con Google.
@Composable
fun SignUpScreen(navController: NavController, loginViewModel: LoginViewModel = viewModel()) { // c'era prima..
//fun SignUpScreen(navController: NavController, loginViewModel: LoginViewModel, state: SignInState, onSignInClick: () -> Unit) {

    val context = LocalContext.current
    val googleSignInState = loginViewModel.googleState.value // collego il conposable allo stato 'googleState' presente in 'loginViewModel'
    val scope = rememberCoroutineScope()

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(it.data) // per prendere le info sull'account dell'utente
            try {
                val result = account.getResult(ApiException::class.java) // memorizzo in 'result' le info sull'account
                val credentials = GoogleAuthProvider.getCredential(result.idToken, null) // fornisco in input a 'getCredential' l'oggetto 'result'
                loginViewModel.googleSignIn(credentials) // invochiamo il metodo 'googleSignIn' del 'loginViewModel' passandogli le credenziali
            }catch (it: ApiException){
                print(it) // stampo l'eventuale eccezione
            }
        }


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
                        loginViewModel.onEvent(UIEventSignUp.NicknameChanged(it), navController)
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
                        loginViewModel.onEvent(UIEventSignUp.EmailChanged(it), navController)
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
                        loginViewModel.onEvent(UIEventSignUp.PasswordChanged(it), navController)
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
                        loginViewModel.onEvent(UIEventSignUp.PrivacyPolicyCheckBoxClicked(it), navController)
                    }

                )

                Spacer(modifier = Modifier.height(40.dp))

                // Button di conferma registrazione
                ButtonComponent(value = stringResource(id = R.string.register),
                    onButtonClick = {
                        loginViewModel.onEvent(UIEventSignUp.RegisterButtonClick, navController) // quando il button viene cliccato verrà generato l'evento 'UIEvent.RegisterButtonClick' che verrà gestito dal "LoginViewModel"
                    },
                    // il button di registrazione sarà attivo solamente se tutti i campi della registrazione
                    // saranno validi:
                    isEnabled = loginViewModel.allValidationCompleted.value
                )

                Spacer(modifier = Modifier.height(20.dp))

                DividerTextComponent()

                ClickableLoginTextComponent(tryingToLogin = true, onTextSelected = {
                    loginViewModel.onEvent(UIEventSignUp.InvalidateDataSignUp, navController) // invalido i dati della schermata di login subito dopo che l'utente
                    // ha cliccato su questa schermata sul button 'Login' in modo tale che se dovesse tornare di nuovo sulla schermata di Registrazione
                    // non vedrà il button 'Register' abilitato.
                    navController.navigate("login")
                })

                Spacer(modifier = Modifier.height(40.dp))

                // - Button di SignUp con Google:
                IconButton(onClick = {
                    // Una volta che l'utente avrà cliccato sull'account prenderemo tutte le info
                    // come username e email
                    val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail() // email utente
                        .requestIdToken(ServerClient) // id_token, ServerClient=è il client_id dell'app riconosciuta da Firebase
                        .build()

                        val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)

                    launcher.launch(googleSignInClient.signInIntent)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google Icon",
                        modifier = Modifier.size(50.dp),
                        tint = Color.Unspecified
                    )
                }

                // LaunchedEffect permette di lanciare un'azione asincrona all'interno del composable corrente.
                // In questo caso la funzione LaunchedEffect permette di avviare un'azione asincrona quando cambia il valore
                // di una particolare chiave che in questo caso è la 'key1 = googleSignInState.success'.
                // Quindi l'azione asincrona definita qui sotto si preoccuperà di monitorare i cambiamenti
                // della proprietà 'googleSignInState.success':
                LaunchedEffect(key1 = googleSignInState.success) {
                    // lancio una coroutine per non bloccare il thread UI:
                    scope.launch {
                        if (googleSignInState.success != null) {
                            // se entro qui vuol dire che il login con Google è avvenuto con successo e quindi
                            // tramite Toast.makeText mostro un breve messaggio informativo all'utente sottoforma di
                            // popup (che si sovrappone all'interfaccia) che in questo caso poichè c'è il parametro "Toast.LENGTH_LONG" durerà un tempo più lungo:
                            Toast.makeText(context, "Sign In Success", Toast.LENGTH_LONG).show()
                        }
                    }
                }

            }

            // - Barra di caricamento che si attiverà nel momento in cui l'attributo
            //   'loading' dello stato 'googleSignInState' sarà uguale a true:
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                if (googleSignInState.loading){
                    CircularProgressIndicator()
                }
            }
        }

        // controllo che lo stato "signUpInProgress" presente in "loginViewModel"
        // sia true:
        if(loginViewModel.signUpInProgress.value){
            // In questo caso inserisco l'indicatore circolare di caricamento
            // che verrà mostrato subito dopo che l'utente
            // avrà cliccato su "Register":
            CircularProgressIndicator()
        }
    }
}

//@Preview
//@Composable
//fun DefaultPreviewOfSignUpScreen(){
//    SignUpScreen()
//}