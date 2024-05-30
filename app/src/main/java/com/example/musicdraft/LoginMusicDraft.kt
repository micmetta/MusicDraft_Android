package com.example.musicdraft

import android.app.Application
import com.google.firebase.FirebaseApp

/*
- Questa classe estende ApplicationClass e si preoccupa di inizializzare Firebase.
- Firebase viene inizializzata all'avvio dell'applicazione sia grazie al fatto che estende la classe Application()
  e sia perchè è stata inserita nel manifest.
*/
class LoginMusicDraft : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
    }
}