package com.example.musicdraft.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicdraft.R
import com.example.musicdraft.ui.theme.BgColor
import com.example.musicdraft.ui.theme.GrayColor
import com.example.musicdraft.ui.theme.Primary
import com.example.musicdraft.ui.theme.Secondary
import com.example.musicdraft.ui.theme.TextColor
import com.example.musicdraft.viewModel.LoginViewModel


@Composable
fun NormalTextComponent(value:String){
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp), // altezza minima 80.dp
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        ),
        color = TextColor,
        //color = colorResource(id = R.color.colorText),
        textAlign = TextAlign.Center
    )
}

@Composable
fun HeadingTextComponent(value:String){
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(),
        style = TextStyle(
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Normal
        ),
        color = TextColor,
        //color = colorResource(id = R.color.colorText),
        textAlign = TextAlign.Center
    )
}


// - Questo composable di sotto è utilizzato per tutti i campi
//   nei quali l'utente inserisce i dati ECCETTO IL CAMPO "password"
//   per il quale ho creato un composable specifico chiamato "PasswordTextFieldComponent".
// - onTextSelected: (String) -> Unit è una funzione di callback che mi permette di poter catturare la stringa di testo inserita
//   nel MyTextFieldComponent da parte dell'utente.
// - errorStatus: permette al composable di potersi auto-aggiornare (in termini di cambiamento di colore) nel momento in cui l'utente inserire dei dati
//   al suo interno e quindi se i dati inseriti saranno corretti allora il composable assumerà un certo colore altrimenti ne assumerà un altro.
//   (Il valore di questo parametro verrà modificato direttamente dal 'LoginViewModel' e ricevuto dal composable).
//   Il valore di default è 'false'.
// - registration: mi permette di sapere se il 'MyTextFieldComponent' si trova nella schermata di registrazione (true) o no (false) (default = false).
@Composable
fun MyTextFieldComponent(loginViewModel: LoginViewModel, labelValue:String, icon:ImageVector, onTextSelected:(String) -> Unit, errorStatus:Boolean = false, registration:Boolean = false){

    // grazie alla funzione 'remember' mi ricordo dell'ultimo valore inserito dall'utente nell'OutlinedTextField di sotto
    var textValue = remember {
        mutableStateOf("")
    }
    val registrationState by loginViewModel.registrationUIState // mi collego allo stato 'loginViewModel.registrationUIState' presente nel loginViewModel in questo modo
    // posso aggiornare automaticamente il 'value' dell'OutlinedTextField presente qui sotto.

    val loginState by loginViewModel.loginUIState // mi collego allo stato 'loginViewModel.registrationUIState' presente nel loginViewModel in questo modo
    // posso aggiornare automaticamente il 'value' dell'OutlinedTextField presente qui sotto.

    // OutlinedTextField è un componente di JC che crea un campo di testo con un bordo esterno
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(componentShapes.small)
            .background(color = BgColor),
        label = { Text(text = labelValue) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorResource(id = R.color.colorPrimary),
            focusedLabelColor = colorResource(id = R.color.colorPrimary),
            cursorColor = colorResource(id = R.color.colorPrimary)
        ),

        //keyboardOptions = KeyboardOptions.Default, // senza il pulsante di next sulla tastiera
        // - Queste 3 righe di codice qui sotto invece, fanno compararire il pulsante
        // di "next" all'interno della tastiera nel momento in cui l'utente
        // clicca su un campo di testo:
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        singleLine = true,
        maxLines = 1,

        // c'era prima:
//        // In 'value' ci saranno i caratteri inseriti dall'utente nell'OutlinedTextField:
//        value = textValue.value, // la proprietà 'value' prende accetta solo una stringa e non un 'MutableState<String>' per questo ho inserito .value
//        // Dentro 'onValueChange' inserisco cosa deve accadere nel momento in cui l'utente aggiorna il l'OutlinedTextField:
//        onValueChange = {
//            Log.d("AppComponent", "Sono dentro onValueChange")
//            textValue.value = it // aggiorno il valore del campo di testo dell'OutlinedTextField
//            onTextSelected(it) // eseguo questa funzione di callback che mi permette di poter catturare la stringa di testo inserita
//                              //  nel MyTextFieldComponent dall'utente e di passarla al LoginViewModel per fargli capire che è stato generato un evento.
//            // In 'value' ci saranno i caratteri inseriti dall'utente nell'OutlinedTextField:
//        },

        // In 'value' ci saranno i caratteri inseriti dall'utente nell'OutlinedTextField e in base a se il 'MyTextFieldComponent' corrente fa riferimento all'email o al nickname
        // verrà aggiornato automaticamente il campo che tra questi due sarà stato modificato dall'utente (anche eventualmente dopo aver selezionato l'account google):
        value = (
                if(registration){
                    if(labelValue == "Email") {
                        registrationState.email
                    } else {
                        registrationState.nickName
                    }
                }else{
                    loginState.email
                }
        ),
        onValueChange = {
            Log.d("AppComponent", "Sono dentro onValueChange")
            textValue.value = it // aggiorno il valore del campo di testo dell'OutlinedTextField
            onTextSelected(it) // eseguo questa funzione di callback che mi permette di poter catturare la stringa di testo inserita
                              //  nel MyTextFieldComponent dall'utente e di passarla al LoginViewModel per fargli capire che è stato generato un evento.
        },

        // inserisco l'icona all'interno dell'OutlinedTextField:
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = "")
        },

        // - l'attributo 'isError' dell'OutlinedTextField sarà sempre uguale al valore opposto rispetto
        //   al parametro 'errorStatus' in modo tale che:
        //   SE errorStatus==true allora vuol dire che i dati inseriti sono corretti e QUINDI isError=false
        //   ALTRIMENTI, SE errorStatus==false allora vuol dire che i dati inseriti sono sbagliati e QUINDI isError=true
        isError = if(registration) !errorStatus else false
    )

}

// - Questo composable è specifico per il campo password.
// - onTextSelected: (String) -> Unit è una funzione di callback che mi permette di poter catturare la stringa di testo inserita
//   nel PasswordTextFieldComponent da parte dell'utente.
// - errorStatus: permette al composable di potersi auto-aggiornare (in termini di cambiamento di colore) nel momento in cui l'utente inserire dei dati
//   al suo interno e quindi se i dati inseriti saranno corretti allora il composable assumerà un certo colore altrimenti ne assumerà un altro.
//   (Il valore di questo parametro verrà modificato direttamente dal 'LoginViewModel' e ricevuto dal composable).
//   Il valore di default è 'false'.
// - registration: mi permette di sapere se il 'MyTextFieldComponent' si trova nella schermata di registrazione (true) o no (false) (default = false).
@Composable
fun PasswordTextFieldComponent(labelValue:String, icon: ImageVector, onTextSelected: (String) -> Unit, errorStatus:Boolean = false, registration:Boolean = false) {

    val localFocusManager = LocalFocusManager.current

    // grazie alla funzione 'remembea visibilità r' mi ricordo dell'ultimo valore inserito dall'utente nell'OutlinedTextField di sotto
    val password = remember {
        mutableStateOf("")
    }

    // variabile che mi permetterà di poter gestire lo meno dei caratteri della password
    val passwordVisible = remember {
        mutableStateOf(false) // all'inizio è settata a false
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(componentShapes.small)
            .background(color = BgColor),
        label = { Text(text = labelValue) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorResource(id = R.color.colorPrimary),
            focusedLabelColor = colorResource(id = R.color.colorPrimary),
            cursorColor = colorResource(id = R.color.colorPrimary)
        ),

        //keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), // nel momento in cui arrivo al campo password il pulsante next si toglie automaticamente e al suo posto viene messo quello di cancellazione

        // - Grazie a queste 3 istruzioni qui sotto invece, una volta arrivato al campo "password"
        //   (che è l'ultimo campo), l'utente vedrà invece del tasto "next" il tasto con la spunta "v"
        //   e una volta cliccato grazie a "keyboardActions" la tastiera scomparirà (clearFocus()) in modo tale da permettere all'utente di avere la visione completa
        //   dei campi e degli altri elementi dell'interfaccia.
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        singleLine = true,
        keyboardActions = KeyboardActions{
            localFocusManager.clearFocus()
        },
        maxLines = 1,

        // In 'value' ci saranno i caratteri inseriti dall'utente nell'OutlinedTextField:
        value = password.value, // la proprietà 'value' prende accetta solo una stringa e non un 'MutableState<String>' per questo ho inserito .value
        onValueChange = {
            password.value = it // aggiorno il valore del campo di testo dell'OutlinedTextField
            onTextSelected(it) // eseguo questa funzione di callback che mi permette di poter catturare la stringa di testo inserita
            //  nel PasswordTextFieldComponent dall'utente e di passarla al LoginViewModel per fargli capire che è stato generato un evento.
        },
        // inserisco l'icona all'interno dell'OutlinedTextField:
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = "")
        },
        trailingIcon = {
            // il valore di iconImage dipenderà dall'if-else
            val iconImage = if(passwordVisible.value) {
                Icons.Filled.Visibility
            }else{
                Icons.Filled.VisibilityOff
            }
            // il valore di description dipenderà dall'if-else
            var description = if(passwordVisible.value){
                stringResource(id = R.string.hide_password)
            }else{
                stringResource(id = R.string.show_password)
            }

            // inserisco un button che nel momento in cui verrà cliccato
            // aggiornerà la variabile "passwordVisible" con il complementare
            // rispetto al valore che aveva prima del click:
            IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                Icon(imageVector = iconImage, contentDescription = description)
            }
        },
        visualTransformation = if(passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),

        // - l'attributo 'isError' dell'OutlinedTextField sarà sempre uguale al valore opposto rispetto
        //   al parametro 'errorStatus' in modo tale che:
        //   SE errorStatus==true allora vuol dire che i dati inseriti sono corretti e QUINDI isError=false
        //   ALTRIMENTI, SE errorStatus==false allora vuol dire che i dati inseriti sono sbagliati e QUINDI isError=true
        isError = if(registration) !errorStatus else false
    )
}


@Composable
fun CheckboxComponent(value: String, onTextSelected: (String) -> Unit, CheckedChange :(Boolean)-> Unit){
    Row(modifier = Modifier
        .fillMaxWidth()
        .heightIn(56.dp),
        verticalAlignment = Alignment.CenterVertically,
    ){

        // Creo uno stato per la 'Checkbox' che definisco sotto:
        val checkedState = remember {
            mutableStateOf(false) // settaggio iniziale
        }
        Checkbox(
            checked = checkedState.value,
            onCheckedChange = {
                checkedState.value = !checkedState.value
                CheckedChange.invoke(it) // mi permette di far sapere all'esterno se è stato fatto il check sulla checkbox o meno
            })

        // istanzio il composable 'ClickableTextComponent' (definito sotto)
        // passandogli come stringa di testo la variabile 'value'
        // ricevuta in input dal 'CheckboxComponent'
        ClickableTextComponent(value = value, onTextSelected)
    }
}


// - Il composable di sotto mi permette di
//   inserire la stringa di accettazione dei termini d'uso
//   rendendo cliccabile anche due parti di questa stringa
@Composable
fun ClickableTextComponent(value: String, onTextSelected: (String) -> Unit){

    val initialPartText = "By continuing you accept our "
    val privacyPolicyPartText = "Privacy Policy "
    val finalPartText = " and "
    val termsAndConditionsText = "Term of Use"

    val annotatedString = buildAnnotatedString {
        append(initialPartText) // l'aggiungo come una stringa normale
        withStyle(style = SpanStyle(color = Primary)){
            pushStringAnnotation(tag = privacyPolicyPartText, annotation = privacyPolicyPartText) // specifico che questa stringa sarà diversa perchè l'utente potrà cliccarci sopra
            // e per questo ho definito per essa un 'tag' e 'un'annotation'.
            append(privacyPolicyPartText)
        }
        append(finalPartText) // l'aggiungo come una stringa normale
        withStyle(style = SpanStyle(color = Primary)){
            pushStringAnnotation(tag = termsAndConditionsText, annotation = termsAndConditionsText) // specifico che questa stringa sarà diversa perchè l'utente potrà cliccarci sopra
            // e per questo ho definito per essa un 'tag' e 'un'annotation'.
            append(termsAndConditionsText)
        }
    }

    ClickableText(text = annotatedString, onClick = {offset ->

        // grazie all'offset sapremo su quale parte di 'annotatedString' ha cliccato l'utente:
        annotatedString.getStringAnnotations(offset, offset)
            .firstOrNull()?.also { span ->
                // stampo nel Logcat il messaggio per sapere se l'utente ha cliccato su 'item=Privacy Policy'
                // o su 'item=Term of Use'
                Log.d("ClickableTextComponent", "{$span}")

                // Quando uno dei due item sarà cliccato allora andrò ad una
                // nuova schermata:
                if((span.item == termsAndConditionsText) || (span.item == privacyPolicyPartText)){
                    onTextSelected(span.item) // cambiando il valore del parametro "onTextSelected" automaticamente
                    // verrà eseguita la lambda function legata al "onTextSelected" presente
                    // nel "CheckboxComponent" all'interno del file chiamato "SignUpScreen.kt"
                    // e con l'esecuzione di questa funzione verrà mostrata la schermata
                    // che ha come nome "termsAndConditionsScreen".
                }
            }
    })
}


// - Utilizzato come botton per avviare la registrazione.
// - La funzione di callback onButtonClick: () -> Unit che mi permette
//   di poter catturare il click del button e lanciare ad esempio in 'SignUpScreen.kt'
//   l'eventi 'loginViewModel.onEvent(UIEvent.RegisterButtonClick)'
@Composable
fun ButtonComponent(value: String, onButtonClick: () -> Unit, isEnabled : Boolean = false){
    Button(onClick = {
        // ogni volta che il button verrà cliccato, verrà eseguita la funzione
        // onButtonClick
        onButtonClick.invoke()
    },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp),
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        shape = RoundedCornerShape(50.dp),
        enabled = isEnabled
        ) {

            Box(modifier = Modifier
                .fillMaxWidth()
                .heightIn(48.dp)
                .background(
                    brush = Brush.horizontalGradient(listOf(Secondary, Primary)),
                    shape = RoundedCornerShape(50.dp)
                ),
                    contentAlignment = Alignment.Center
            ){
                Text(text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
    }
}


// - Utilizzato come botton per effettuare il login.
// - La funzione di callback onButtonClick: () -> Unit che mi permette
//   di poter catturare il click del button e lanciare ad esempio in 'SignUpScreen.kt'
//   l'eventi 'loginViewModel.onEvent(UIEvent.RegisterButtonClick)'
@Composable
fun ButtonComponentLogin(value: String, onButtonClick: () -> Unit, isEnabled : Boolean = false){
    Button(onClick = {
        // ogni volta che il button verrà cliccato, verrà eseguita la funzione
        // onButtonClick
        onButtonClick.invoke()
    },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp),
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        shape = RoundedCornerShape(50.dp),
        enabled = isEnabled
    ) {

        Box(modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp)
            .background(
                brush = Brush.horizontalGradient(listOf(Secondary, Primary)),
                shape = RoundedCornerShape(50.dp)
            ),
            contentAlignment = Alignment.Center
        ){
            Text(text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


// - Utilizzato per avere le due linee a sinistra e destra di 'or' per permettere
//   all'utente di creare l'account o fare il login con Google o con un altro social:
@Composable
fun DividerTextComponent(){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){

        Divider(modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
            color = GrayColor,
            thickness = 1.dp
        )

        Text(
            modifier = Modifier.padding(8.dp),
            text = stringResource(id = R.string.or),
            fontSize = 18.sp,
            color = TextColor)

        Divider(modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
            color = GrayColor,
            thickness = 1.dp
        )

    }
}


// - Se tryingToLogin == true (comportamento di default), allora
//   Mi permette di creare due stringhe,
//   la prima con la scritta "Already have an account? "
//   e di avere la seconda stringa "Login" cliccabile,
//   ALTRIMENTI,
//   Mi permette di creare due stringhe,
//   la prima con la scritta "Don't have an account yet? "
//   e di avere la seconda stringa "Register" cliccabile.
@Composable
fun ClickableLoginTextComponent(tryingToLogin:Boolean = true, onTextSelected: (String) -> Unit){

    // in base a dove questo composable verrà utilizzato, ovvero in "SignUpScreen.kt" piuttosto che in "LoginScreen",
    // le due stringhe che verranno composte saranno differenti:
    val initialPartText = if(tryingToLogin) "Already have an account? " else "Don't have an account yet? "
    val loginText = if(tryingToLogin) "Login" else "Register"

    val annotatedString = buildAnnotatedString {
        append(initialPartText) // l'aggiungo come una stringa normale
        withStyle(style = SpanStyle(color = Primary)){
            pushStringAnnotation(tag = loginText, annotation = loginText) // specifico che questa stringa sarà diversa perchè l'utente potrà cliccarci sopra
            // e per questo ho definito per essa un 'tag' e 'un'annotation'.
            append(loginText)
        }
    }

    ClickableText(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp), // altezza minima 80.dp
        style = TextStyle(
            fontSize = 21.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal,
            textAlign = TextAlign.Center
        ),

        text = annotatedString, onClick = {offset ->

        // grazie all'offset sapremo su quale parte di 'annotatedString' ha cliccato l'utente:
        annotatedString.getStringAnnotations(offset, offset)
            .firstOrNull()?.also { span ->
                // stampo nel Logcat il messaggio per sapere se l'utente ha cliccato su 'item=Login'
                // o su 'item=Term of Use'
                Log.d("ClickableTextComponent", "{$span}")

                // Quando Login sarà cliccato allora andrò ad una
                // nuova schermata:
                if(span.item == loginText){
                    onTextSelected(span.item) // cambiando il valore del parametro "onTextSelected" automaticamente
                    // verrà eseguita la lambda function legata al "onTextSelected" presente
                    // nel "ClickableLoginTextComponent" all'interno del file chiamato "SignUpScreen.kt"
                    // e con l'esecuzione di questa funzione verrà mostrata la schermata
                    // che ha come nome "Login".
                }
            }
    })
}


// - Composable utilizzato per la scritta "Forgot your password":
@Composable
fun ClickableUnderLinedNormalTextComponent(
    value: String,
    onClick: () -> Unit // Azione da eseguire quando viene cliccato
) {
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp) // Altezza minima 40.dp
            .clickable(onClick = onClick), // Aggiungi il modifier Clickable
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        ),
        color = colorResource(id = R.color.colorGray),
        textAlign = TextAlign.Center,
        textDecoration = TextDecoration.Underline
    )
}

@Composable
fun Dialog(message: String, active: Boolean){
    Column {
        val openDialog = remember {
            mutableStateOf(active)
        }
        if(openDialog.value){
            AlertDialog(
                onDismissRequest = { openDialog.value = false },
                title = {
                    Text(text = "Error")
                },
                text = {
                    Text(text = message)
                },
                confirmButton = {
                    Button(
                        onClick = { openDialog.value = false }
                    ) {
                        Text(text = "Ok")
                    }
                }
            )
        }
    }
}
