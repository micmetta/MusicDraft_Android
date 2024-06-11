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

@Composable
//fun LoginScreen(navController: NavController, loginViewModel: LoginViewModel = viewModel()){ // c'era prima..
fun LoginScreen(navController: NavController, loginViewModel: LoginViewModel){

    val context = LocalContext.current
    val googleSignInState = loginViewModel.googleState.value // collego il composable allo stato 'googleState' presente in 'loginViewModel'
    val scope = rememberCoroutineScope()
//    var errorDialogActivated = loginViewModel.errorDialogActivated.value
//    var stringToShowErrorDialog = loginViewModel.stringToShowErrorDialog.value
    val errorDialogActivated by loginViewModel.errorDialogActivated // mi lego allo state del loginViewModel
    val stringToShowErrorDialog by loginViewModel.stringToShowErrorDialog // mi lego allo state del loginViewModel

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(it.data) // per prendere le info sull'account dell'utente
            try {
                val result = account.getResult(ApiException::class.java) // memorizzo in 'result' le info sull'account
                val email = result.email // prendo l'email dell'utente
                Log.d("SignUpScreen", "L'utente vuole loggare con questa email: $email") // stampo l'email nel log
                // se l'email non è null allora aggiorno il campo 'registrationUIState.email' generando l'evento 'UIEventSignUp.EmailChanged()' nel loginViewModel:
                email?.let {
                    Log.d("SignUpScreen", "Sono in email?.let")
                    Log.d("SignUpScreen", "it: $it")
                    loginViewModel.onEvent(UIEventSignIn.EmailChanged(it), navController) // Aggiorna lo stato dell'email nel ViewModel
                }
                // - Queste due righe qui sotto c'erano prima ma esegue direttamente il signup con google usando le 'credentials' senza aspettare che l'utente abbia inserito la password
                //    e che solo dopo abbia premuto il button Register per questo motivo l'ho commentato:
                //val credentials = GoogleAuthProvider.getCredential(result.idToken, null) // fornisco in input a 'getCredential' l'oggetto 'result'
                //loginViewModel.googleSignIn(credentials) // invoco il metodo 'googleSignIn' del 'loginViewModel' passandogli le credenziali che si preoccuperà di aggiornare
                // il "GoogleSignInState" in base a se si verifica un successo, un caricamento o un errore.

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

            Column(modifier = Modifier
                .fillMaxSize()
            ){

                NormalTextComponent(value = stringResource(id = R.string.login))
                HeadingTextComponent(value = stringResource(id = R.string.welcome))
                Spacer(modifier = Modifier.height(20.dp)) // spazio

                MyTextFieldComponent(
                    loginViewModel = loginViewModel,
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

                ClickableUnderLinedNormalTextComponent(stringResource(id = R.string.forgot_password)){
                    loginViewModel.onEvent(UIEventSignIn.forgotPassword, navController)
                }

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
                    loginViewModel.onEvent(UIEventSignIn.InvalidateDataSignIn, navController) // invalido i dati della schermata di login subito dopo che l'utente
                    // ha cliccato su questa schermata sul button 'Register' in modo tale che se dovesse tornare di nuovo sulla schermata di Login
                    // non vedrà il button 'Login' abilitato.
                    navController.navigate("signUp")
                })


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
