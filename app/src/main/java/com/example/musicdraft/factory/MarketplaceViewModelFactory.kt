package com.example.musicdraft.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.musicdraft.viewModel.CardsViewModel
import com.example.musicdraft.viewModel.LoginViewModel
import com.example.musicdraft.viewModel.MarketplaceViewModel

/**
 * Factory per creare l'istanza di [MarketplaceViewModel] con i parametri necessari.
 *
 * @property application Oggetto Application per l'accesso al contesto dell'applicazione.
 * @property cardsViewModel Oggetto [CardsViewModel] necessario per fornire dipendenze al [MarketplaceViewModel].
 * @property loginViewModel Oggetto [LoginViewModel] necessario per fornire dipendenze al [MarketplaceViewModel].
 */
class MarketplaceViewModelFactory(
    private val application: Application,
    private val cardsViewModel: CardsViewModel,
    private val loginViewModel: LoginViewModel
) : ViewModelProvider.AndroidViewModelFactory(application) {

    /**
     * Crea una nuova istanza del ViewModel specificato.
     *
     * @param modelClass Classe del ViewModel da creare.
     * @return Istanzia un oggetto di tipo [MarketplaceViewModel] se il modelClass è compatibile,
     * altrimenti lancia un'eccezione [IllegalArgumentException].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MarketplaceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MarketplaceViewModel(application, cardsViewModel, loginViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}