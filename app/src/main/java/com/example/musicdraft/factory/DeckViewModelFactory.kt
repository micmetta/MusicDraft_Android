package com.example.musicdraft.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicdraft.viewModel.CardsViewModel
import com.example.musicdraft.viewModel.DeckViewModel
import com.example.musicdraft.viewModel.LoginViewModel

/**
 * Factory per la creazione di istanze di [DeckViewModel].
 *
 * Questa factory viene utilizzata per fornire le dipendenze necessarie al ViewModel,
 * in questo caso [Application], [LoginViewModel] e [CardsViewModel].
 *
 * @property application Oggetto Application per l'accesso al contesto dell'applicazione.
 * @property loginViewModel ViewModel per la gestione dell'autenticazione dell'utente.
 * @property cardsViewModel ViewModel per la gestione delle carte.
 */
class DeckViewModelFactory(
    private val application: Application,
    private val loginViewModel: LoginViewModel,
    private val cardsViewModel: CardsViewModel
) : ViewModelProvider.Factory {

    /**
     * Crea una nuova istanza del ViewModel specificato.
     *
     * @param modelClass Classe del ViewModel da creare.
     * @return Istanzia un oggetto di tipo [DeckViewModel] se il modelClass è compatibile,
     * altrimenti lancia un'eccezione [IllegalArgumentException].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeckViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeckViewModel(application, loginViewModel, cardsViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
