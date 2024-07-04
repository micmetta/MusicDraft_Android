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
                    isEnabled = loginViewModel.allValidationCompletedLogin.value
                )

                // Spazio verticale
                Spacer(modifier = Modifier.height(20.dp))

                // Divider
                DividerTextComponent()

                // Testo cliccabile per passare alla schermata di registrazione
                ClickableLoginTextComponent(tryingToLogin = false) {
                    loginViewModel.onEvent(UIEventSignIn.InvalidateDataSignIn, navController)
                    navController.navigate("signUp")
                }

                // Box per il pulsante di login con Google
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = {
                        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(Constant.ServerClient) // id_token, ServerClient=è il client_id dell'app riconosciuta da Firebase
                            .requestEmail() // email utente
                            .build()
                        val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
                        googleSignInClient.signOut()
                        launcher.launch(googleSignInClient.signInIntent)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = "Icona Google",
                            modifier = Modifier.size(50.dp),
                            tint = Color.Unspecified
                        )
                    }
                }

                // Effetto lanciato quando cambia lo stato di successo del login con Google
                LaunchedEffect(key1 = googleSignInState.success) {
                    scope.launch {
                        if (googleSignInState.success != null) {
                            Toast.makeText(context, "Accesso effettuato con successo", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

            // Barra di caricamento mostrata quando è attivo il login con Google
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                if (googleSignInState.loading) {
                    CircularProgressIndicator()
                }
            }
        }

        // Dialog di errore mostrato quando errorDialogActivated è true
        if (errorDialogActivated) {
            Column {
                AlertDialog(
                    onDismissRequest = {
                        loginViewModel.reset_errorDialogActivated()
                        loginViewModel.reset_stringToShowErrorDialog()
                    },
                    title = {
                        Text(text = "Errore")
                    },
                    text = {
                        Text(text = stringToShowErrorDialog)
                    },
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

        // Indicatore di caricamento mostrato quando signInInProgress è true
        if (loginViewModel.signInInProgress.value) {
            CircularProgressIndicator()
        }
    }
}


//@Preview
//@Composable
//fun LoginScreenPrewview(){
//    LoginScreen()
//}
