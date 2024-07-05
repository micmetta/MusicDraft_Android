package com.example.musicdraft.model

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.musicdraft.data.tables.matchmaking.Matchmaking
import com.example.musicdraft.data.tables.matchmaking.MatchmakingDao
import com.example.musicdraft.viewModel.MatchmakingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * repository che si preoccupa di richiamare le funzioni del MatchmakingDao per la comunicazione con la tabella Matchmaking del database.
 *
 * @param viewModel che corrisponde all'istanza di un MatchmakingViewModel.
 * @param dao che corrisponde all'istanza di un MatchmakingDao.
 */
class MatchmakingRepository(val viewModel: MatchmakingViewModel, val dao: MatchmakingDao){

    val matchings: List<Matchmaking>? = null
    val matching: MutableStateFlow<List<Matchmaking>?> = MutableStateFlow(matchings)

    val matchingsWait: List<Matchmaking>? = null
    val matchesWait: MutableStateFlow<List<Matchmaking>?> = MutableStateFlow(matchingsWait)


    /**
     * Inserisce un nuovo oggetto nella tabella Matchmaking richiamando la funzione insertNewMatch del MatchmakingDao.
     *
     * @param matchmaking Oggetto da inserire come nuova riga nella tabella.
     */
    fun insertNewMatch(matchmaking: Matchmaking){
        viewModel.viewModelScope.launch {
            dao.insertNewMatch(matchmaking)
        }
    }

    /**
     * Elimina una riga della tabella Matchmaking richiamando la funzione deleteMatch del MatchmakingDao.
     *
     * @param id Identificatore della riga da eliminare.
     */
    fun deleteMatch(id: Int){
        viewModel.viewModelScope.launch {
            dao.deleteMatch(id)
        }
    }

    /**
     * Aggiorna la variabile 'matching' tramite l'invocazione della funzione getAllMatchesWaiting del MatchmakingDao.
     * In questo modo si riesce ad ottenere l'aggiornamento di tutte le partite (con un max di 100) nelle quali
     * ci sono utenti che sono in attesa di trovare un avversario.
     *
     * @param nicknameUserCurrent Nickname dell'utente per il quale si vuole eseguire l'aggiornamento.
     */
    fun getAllMatchesWaiting(nicknameUserCurrent:String) {
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val match = dao.getAllMatchesWaiting(nicknameUserCurrent)
                match.collect { matchFound ->
                    matching.value = matchFound
                    Log.i("MatchmakingRepository", "matching.value: ${matching.value}")
                }
            }
        }
    }

    /**
     * Aggiorna la variabile 'matchesConcludedByCurrentUser' tramite l'invocazione della funzione getAllMatchesWaitingByNickname del MatchmakingDao.
     * In questo modo si riesce ad ottenere l'aggiornamento di tutte le partite nelle quali l'utente loggato è in attesa.
     *
     * @param nickname Nickname dell'utente per il quale si vuole eseguire l'aggiornamento.
     */
    fun getAllMatchesWaitingByNickname(nickname: String) {
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val match = dao.getAllMatchesWaitingByNickname(nickname)
                match.collect { matchWait ->
                    matchesWait.value = matchWait
                    Log.i("MatchmakingRepository", "matchesWait.value: ${matchesWait.value}")
                }
            }
        }
    }
}