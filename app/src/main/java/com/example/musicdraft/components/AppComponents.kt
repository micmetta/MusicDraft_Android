package com.example.musicdraft.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.example.musicdraft.ui.theme.TextColor
import com.example.musicdraft.R
import com.example.musicdraft.ui.theme.BgColor
import com.example.musicdraft.ui.theme.Primary


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

@Composable
fun MyTextFieldComponent(labelValue:String, icon: ImageVector){

    // grazie alla funzione 'remember' mi ricordo dell'ultimo valore inserito dall'utente nell'OutlinedTextField di sotto
    val textValue = remember {
        mutableStateOf("")
    }

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
        keyboardOptions = KeyboardOptions.Default,
        // In 'value' ci saranno i caratteri inseriti dall'utente nell'OutlinedTextField:
        value = textValue.value, // la proprietà 'value' prende accetta solo una stringa e non un 'MutableState<String>' per questo ho inserito .value
        onValueChange = {
            textValue.value = it // aggiorno il valore del campo di testo dell'OutlinedTextField
        },
        // inserisco l'icona all'interno dell'OutlinedTextField:
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = "")
        }
    )

}

@Composable
fun PasswordTextFieldComponent(labelValue:String, icon: ImageVector) {

    // grazie alla funzione 'remember' mi ricordo dell'ultimo valore inserito dall'utente nell'OutlinedTextField di sotto
    val password = remember {
        mutableStateOf("")
    }

    // variabile che mi permetterà di poter gestire la visibilità o meno dei caratteri della password
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
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        // In 'value' ci saranno i caratteri inseriti dall'utente nell'OutlinedTextField:
        value = password.value, // la proprietà 'value' prende accetta solo una stringa e non un 'MutableState<String>' per questo ho inserito .value
        onValueChange = {
            password.value = it // aggiorno il valore del campo di testo dell'OutlinedTextField
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
        visualTransformation = if(passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation()
    )
}


@Composable
fun CheckboxComponent(value: String, onTextSelected: (String) -> Unit){
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
            })

        // istanzio il composable 'ClickableTextComponent' (definito sotto)
        // passandogli come stringa di testo la variabile 'value'
        // ricevuta in input dal 'CheckboxComponent'
        ClickableTextComponent(value = value, onTextSelected)
    }
}


// Il composable di sotto mi permette di
// inserire la stringa di accettazione dei termini d'uso
// rendendo cliccabile anche due parti di questa stringa
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

