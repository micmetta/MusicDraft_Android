package com.example.musicdraft.screens_to_signUp_signIn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicdraft.R
import com.example.musicdraft.components.BodyTextComponent
import com.example.musicdraft.components.HeadingTextComponent

@Composable
fun TermsAndConditionsScreen(){
    Surface(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White)
        .padding(16.dp)) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                HeadingTextComponent(value = stringResource(id = R.string.terms_and_conditions_header))
                BodyTextComponent(value = stringResource(id = R.string.terms_and_conditions_body))
             }
        }
}

@Preview
@Composable
fun TermsAndConditionsScreenPreview(){
    TermsAndConditionsScreen()
}

