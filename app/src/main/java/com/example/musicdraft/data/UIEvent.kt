package com.example.musicdraft.data


// - Questa è la sealed class che conterrà l'insieme finito di eventi che l'utente
//   potrà generare sulla schermata di creazione dell'account ("SignUpScreen.kt")
sealed class UIEvent {

    // - Nel momento in cui l'utente inserisce un qualche carattere all'interno del
    //   text field del Nickname l'evento "NicknameChanged" verrà innescato e il valore inserito dall'utente
    //   si troverà all'interno del parametro 'nickname'.
    //   e i caratteri inseriti nel campo verranno catturati:
    // - Il tipo di dato restituito da questa classe sarà "UIEvent()".
    // - Stesso ragionamento vale per le altre data classes.
    data class NicknameChanged(val nickname :String)  : UIEvent()
    data class EmailChanged(val email :String) : UIEvent()
    data class PasswordChanged(val password :String) : UIEvent()

    object RegisterButtonClick : UIEvent()
}