package com.example.musicdraft.screens_to_signUp_signIn

import android.util.Log
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.musicdraft.R
import com.example.musicdraft.components.ButtonComponentLogin
import com.example.musicdraft.components.ClickableLoginTextComponent
import com.example.musicdraft.components.ClickableUnderLinedNormalTextComponent
import com.example.musicdraft.components.DividerTextComponent
import com.example.musicdraft.components.HeadingTextComponent
import com.example.musicdraft.components.MyTextFieldComponent
import com.example.musicdraft.components.NormalTextComponent
import com.example.musicdraft.components.PasswordTextFieldComponent
import com.example.musicdraft.data.UIEventSignIn
import com.example.musicdraft.utility.Constant
import com.example.musicdraft.viewModel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch

/**
 * Schermata di login rappresentata come composable.
 *
 * @param navController Il controller di navigazione utilizzato per navigare tra i composables.
 * @param loginViewModel Il ViewModel che gestisce la logica e lo stato della schermata di login.
 */
@Composable
fun LoginScreen(navController: NavController, loginViewModel: LoginViewModel) {

    // Contesto per l'accesso alle risorse
    val context = LocalContext.current

    // Stato dal ViewModel
    val googleSignInState = loginViewModel.googleState.value
    val scope = rememberCoroutineScope()

    // Variabili osservabili dal ViewModel
    val errorDialogActivated by loginViewModel.errorDialogActivated
    val stringToShowErrorDialog by loginViewModel.stringToShowErrorDialog

    // Launcher per il risultato dell'attività di accesso con Google
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
        val account = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val result = account.getResult(ApiException::class.java)
            val email = result.email
            Log.d("SignUpScreen", "L'utente vuole loggare con questa email: $email")
            email?.let {
                loginViewModel.onEvent(UIEventSignIn.EmailChanged(it), navController)
            }
            // Queste righe erano commentate perché eseguivano direttamente la registrazione con Google
            // utilizzando le credenziali, senza attendere che l'utente inserisse la password e premesse il pulsante Register.
            // val credentials = GoogleAuthProvider.getCredential(result.idToken, null)
            // loginViewModel.googleSignIn(credentials)

        } catch (it: ApiException) {
            print(it)
        }
    }

    // Struttura principale dell'UI
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color.White,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(28.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // Titolo della schermata di login
                NormalTextComponent(value = stringResource(id = R.string.login))

                // Testo di benvenuto
                HeadingTextComponent(value = stringResource(id = R.string.welcome))

                // Spazio verticale
                Spacer(modifier = Modifier.height(20.dp))

                // Campo di testo per l'email
                MyTextFieldComponent(
                    loginViewModel = loginViewModel,
                    stringResource(id = R.string.email),
                    Icons.Default.Email,
                    onTextSelected = {
                        loginViewModel.onEvent(UIEventSignIn.EmailChanged(it), navController)
                    })

                // Campo di testo per la password
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

                // Spazio verticale
                Spacer(modifier = Modifier.height(40.dp))

                // Testo cliccabile per il recupero della password
                ClickableUnderLinedNormalTextComponent(stringResource(id = R.string.forgot_password)) {
                    loginViewModel.onEvent(UIEventSignIn.ForgotPassword, navController)
                }

                // Spazio verticale
                Spacer(modifier = Modifier.height(40.dp))

                // Pulsante di login
                ButtonComponentLogin(
                    value = stringResource(id = R.string.login),
                    onButtonClick = {
                        loginViewModel.onEvent(UIEventSignIn.LoginButtonClick, navController)
                    },
                    // il button di login sarà attivo solamente se tutti i campi della schermata login
                    // saranno validi:
                    isEnabled = loginViewModel.allValidationCompletedLogin.value
                )

                // Spazio verticale
                Spacer(modifier = Modifier.height(20.dp))

                // Divider
                DividerTextComponent()

                // Testo cliccabile per passare alla schermata di registrazione
                ClickableLoginTextComponent(tryingToLogin = false, onTextSelected = {
                    loginViewModel.onEvent(UIEventSignIn.InvalidateDataSignIn, navController) // invalido i dati della schermata di login subito dopo che l'utente
                    // ha cliccato su questa schermata sul button 'Register' in modo tale che se dovesse tornare di nuovo sulla schermata di Login
                    // non vedrà il button 'Login' abilitato.
                    navController.navigate("signUp")
                })

                // Box per il pulsante di login con Google
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // - Button di SignUp con Google:
                    IconButton(onClick = {
                        // Una volta che l'utente avrà cliccato sull'account prenderemo tutte le info
                        // come username e email
                        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(Constant.ServerClient) // id_token, ServerClient=è il client_id dell'app riconosciuta da Firebase
                            .requestEmail() // email utente
//                            .requestIdToken(ServerClient) // id_token, ServerClient=è il client_id dell'app riconosciuta da Firebase
                            .build()
                        val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
                        googleSignInClient.signOut() // serve per permettere all'utente di selezionare l'acocunt che desidera
                        launcher.launch(googleSignInClient.signInIntent)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = "Google Icon",
                            modifier = Modifier.size(50.dp),
                            tint = Color.Unspecified
                        )
                    }
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

//                // - COMPARE SOLO SU UNO SMARTPHONE FISICO!!!
//                // mi permette di mostrare un messaggio di errore sullo schermo nel momento in cui l'utente cerca di regsitrarsi
//                // con un'email già registrata nel sistema:
//                LaunchedEffect(key1 = loginViewModel.errorDialogActivated.value) {
//                    // lancio una coroutine per non bloccare il thread UI:
//                    scope.launch {
//                        if (loginViewModel.errorDialogActivated.value) {
//                            // se entro qui vuol dire che il login con Google è avvenuto con successo e quindi
//                            // tramite Toast.makeText mostro un breve messaggio informativo all'utente sottoforma di
//                            // popup (che si sovrappone all'interfaccia) che in questo caso poichè c'è il parametro "Toast.LENGTH_LONG" durerà un tempo più lungo:
//                            Toast.makeText(context, loginViewModel.stringToShowErrorDialog.value, Toast.LENGTH_LONG).show()
////                            loginViewModel.reset_errorDialogActivated(mutableStateOf(false))
////                            loginViewModel.reset_stringToShowErrorDialog(mutableStateOf(""))
//                            loginViewModel.reset_errorDialogActivated()
//                            loginViewModel.reset_stringToShowErrorDialog()
//                        }
//                    }
//                }
            }

            // - Barra di caricamento che si attiverà nel momento in cui l'attributo
            //   'loading' dello stato 'googleSignInState' sarà uguale a true:
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                if (googleSignInState.loading){
                    CircularProgressIndicator()
                }
            }
        }

        if(errorDialogActivated){
            // In questo caso inserisco l'indicatore circolare di caricamento
            // che verrà mostrato subito dopo che l'utente
            // avrà cliccato su "Register":
            //CircularProgressIndicator() c'era prima..
            //Dialog(stringToShowErrorDialog, true)
            Column {
                AlertDialog(
                    onDismissRequest = {
                        loginViewModel.reset_errorDialogActivated()
                        loginViewModel.reset_stringToShowErrorDialog()
                    },
                    title = {
                        Text(text = "Error") },
                    text = {
                        Text(text = stringToShowErrorDialog) },
                    confirmButton = {
                        Button(
                            onClick = {
                                loginViewModel.reset_errorDialogActivated()
                                loginViewModel.reset_stringToShowErrorDialog()
                            }
                        ) {
                            Text(text = "Ok")
                        }
                    }
                )
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
