package com.example.musicdraft.model

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.musicdraft.data.tables.matchmaking.MatchSummaryConcluded
import com.example.musicdraft.data.tables.matchmaking.MatchSummaryConcludedDao
import com.example.musicdraft.viewModel.MatchmakingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * repository che si preoccupa di richiamare le funzioni del MatchSummaryConcludedDao per la comunicazione con la tabella MatchSummaryConcluded del database.
 *
 * @param viewModel che corrisponde all'istanza di un MatchmakingViewModel.
 * @param dao che corrisponde all'istanza di un MatchSummaryConcludedDao.
 */
class MatchSummaryConcludedRepository(val viewModel: MatchmakingViewModel, val dao: MatchSummaryConcludedDao) {

    val matches: List<MatchSummaryConcluded>? = null
    val matchesConcludedByCurrentUser: MutableStateFlow<List<MatchSummaryConcluded>?> = MutableStateFlow(matches)

    /**
     * Inserisce un nuovo oggetto nella tabella MatchSummaryConcluded richiamando la funzione insertNewSummaryMatch del MatchSummaryConcludedDao.
     *
     * @param matchSummaryConcluded Oggetto da inserire come nuova riga nella tabella.
     */
    fun insertNewSummaryMatch(matchSummaryConcluded: MatchSummaryConcluded){
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dao.insertNewSummaryMatch(matchSummaryConcluded)
            }
        }
    }

    /**
     * Elimina una riga della tabella Matchmaking richiamando la funzione deleteSummaryMatch del MatchSummaryConcludedDao.
     *
     * @param id Identificatore della riga da eliminare.
     */
    fun deleteSummaryMatch(id: Int){
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dao.deleteSummaryMatch(id)
            }
        }
    }

    /**
     * Aggiorna la variabile 'matchesConcludedByCurrentUser' tramite l'invocazione della funzione getAllGamesConludedByNickname del MatchSummaryConcludedDao.
     * In questo modo si riesce ad ottenere l'aggiornamento di tutte le partite giocate dall'utente loggato.
     *
     * @param nickname Nickname dell'utente per il quale si vuole eseguire l'aggiornamento.
     */
    fun getAllGamesConludedByNickname(nickname: String){
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val matches = dao.getAllGamesConludedByNickname(nickname)
                matches.collect { response ->
                    matchesConcludedByCurrentUser.value = response
                    Log.i("MatchSummaryConcludedRepository", "matchesConcludedByCurrentUser.value: ${matchesConcludedByCurrentUser.value}")
                }
            }
        }
    }
}