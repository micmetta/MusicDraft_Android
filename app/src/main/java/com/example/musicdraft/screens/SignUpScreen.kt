package com.example.musicdraft.screens

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
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicdraft.R
import com.example.musicdraft.components.CheckboxComponent
import com.example.musicdraft.components.HeadingTextComponent
import com.example.musicdraft.components.MyTextFieldComponent
import com.example.musicdraft.components.NormalTextComponent
import com.example.musicdraft.components.PasswordTextFieldComponent

@Composable
fun SignUpScreen() {
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
            Spacer(modifier = Modifier.height(20.dp))
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
                value = stringResource(id = R.string.terms_and_conditions)
            )

        }
    }
}

@Preview
@Composable
fun DefaultPreviewOfSignUpScreen(){
    SignUpScreen()
}