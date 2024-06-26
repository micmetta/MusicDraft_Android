package com.example.musicdraft.data


// - Questa è la sealed class che conterrà l'insieme finito di eventi che l'utente
//   potrà generare sulla schermata di creazione dell'account ("SignUpScreen.kt")
// - Nel momento in cui un UIEvent verrà innescato, questo sarà catturato dal "LoginViewModel" che si
//   preoccuperà di gestirlo andando a modificare uno stato tenendo conto di quale azione ha eseguito l'utente
//   (ad es. inserito caratteri nella casella nickname oppure email oppure password oppure aver cliccato sul button
//   di registrazione).
sealed class UIEventSignUp {

    // - Nel momento in cui l'utente inserisce un qualche carattere all'interno del
    //   text field del Nickname l'evento "NicknameChanged" verrà innescato e il valore inserito dall'utente
    //   si troverà all'interno del parametro 'nickname'.
    //   e i caratteri inseriti nel campo verranno catturati:
    // - Il tipo di dato restituito da questa classe sarà "UIEvent()".
    // - Stesso ragionamento vale per le altre data classes.
    data class NicknameChanged(val nickname :String)  : UIEventSignUp()
    data class EmailChanged(val email :String) : UIEventSignUp()
    data class PasswordChanged(val password :String) : UIEventSignUp()

    // se l'utente avrà fatto il check sulla CheckBox che fa riferimento alla privacy allora
    // il valore del parametro status  sarà true:
    data class PrivacyPolicyCheckBoxClicked(val status:Boolean) : UIEventSignUp()
    object RegisterButtonClick : UIEventSignUp()
    object InvalidateDataSignUp : UIEventSignUp()

}