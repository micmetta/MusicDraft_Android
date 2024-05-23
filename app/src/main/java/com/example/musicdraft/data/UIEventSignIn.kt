package com.example.musicdraft.data

sealed class UIEventSignIn {
    data class EmailChanged(val email :String) : UIEventSignIn()
    data class PasswordChanged(val password :String) : UIEventSignIn()
    object LoginButtonClick : UIEventSignIn()
}