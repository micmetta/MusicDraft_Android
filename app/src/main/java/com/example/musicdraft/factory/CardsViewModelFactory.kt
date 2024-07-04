package com.example.musicdraft.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.musicdraft.viewModel.CardsViewModel
import com.example.musicdraft.viewModel.LoginViewModel
import com.example.musicdraft.viewModel.MarketplaceViewModel

/**
 * Factory per creare l'istanza di [CardsViewModel] con i parametri necessari.
 *
 * @property application Oggetto Application per l'accesso al contesto dell'applicazione.
 * @property loginViewModel Oggetto [LoginViewModel] necessario per fornire dipendenze al [CardsViewModel].
 */
class CardsViewModelFactory(
    private val application: Application,
    private val loginViewModel: LoginViewModel
) : ViewModelProvider.AndroidViewModelFactory(application) {

    /**
     * Crea una nuova istanza del ViewModel specificato.
     *
     * @param modelClass Classe del ViewModel da creare.
     * @return Istanzia un oggetto di tipo [CardsViewModel] se il modelClass è compatibile,
     * altrimenti lancia un'eccezione [IllegalArgumentException].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CardsViewModel(application, loginViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}