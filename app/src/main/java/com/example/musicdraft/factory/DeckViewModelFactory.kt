package com.example.musicdraft.factory

import DeckViewModel
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicdraft.viewModel.CardsViewModel
import com.example.musicdraft.viewModel.ExchangeManagementCardsViewModel
import com.example.musicdraft.viewModel.LoginViewModel

class DeckViewModelFactory(
    private val application: Application,
    private val loginViewModel: LoginViewModel,
    private val cardsViewModel: CardsViewModel,
    private val exchangeManagementCardsViewModel: ExchangeManagementCardsViewModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeckViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeckViewModel(application, loginViewModel, cardsViewModel, exchangeManagementCardsViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
