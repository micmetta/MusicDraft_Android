package com.example.musicdraft

import android.app.Application
import com.google.firebase.FirebaseApp

/*
- Questa classe estende ApplicationClass e si preoccupa di inizializzare Firebase.
*/
class LoginMusicDraft : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
    }
}