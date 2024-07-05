package com.example.musicdraft.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.musicdraft.data.tables.matchmaking.MatchSummaryConcluded
import com.example.musicdraft.data.tables.matchmaking.Matchmaking
import com.example.musicdraft.database.MusicDraftDatabase
import com.example.musicdraft.model.MatchSummaryConcludedRepository
import com.example.musicdraft.model.MatchmakingRepository



/**
 * Viewmodel che estende la classe AndroidViewModel e che si preoccupa della gestione delle sfide tra gli utenti registrati nell'applicazione
 * e dell'aggiornamento dei risultati dei matches ottenuti da questi ultimi.
 * Per queste ragioni istanzia al suo interno oggetti dei seguenti tipi: MatchmakingRepository e MatchSummaryConcludedRepository
 * @param application utilizzato come contesto dell'applicazione.
 */
class MatchmakingViewModel(application: Application) : AndroidViewModel(application) {

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // mi prendo il riferimento al DB:
    private val database = MusicDraftDatabase.getDatabase(application)
    private val matchmakingDao = database.matchmakingDao()
    private val matchSummaryConcludedDao = database.matchSummaryConcludedDao()
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // istanzio il repository
    private val matchmakingRepository: MatchmakingRepository = MatchmakingRepository(this, matchmakingDao!!)
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // istanzio il repository
    private val matchSummaryConcludedRepository: MatchSummaryConcludedRepository = MatchSummaryConcludedRepository(this, matchSummaryConcludedDao!!)
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////
    var matchesConcludedByCurrentUser =  matchSummaryConcludedRepository.matchesConcludedByCurrentUser
    /////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////
    var matching = matchmakingRepository.matching
    /////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////
    var matchesWait = matchmakingRepository.matchesWait
    /////////////////////////////////////////////////////////////////////////////

    /**
     * Inserisce un nuovo oggetto nella tabella Matchmaking richiamando la funzione insertNewMatch di MatchmakingRepository.
     *
     * @param matchmaking Oggetto da inserire come nuova riga nella tabella.
     */
    fun insertNewMatch(matchmaking: Matchmaking){
        matchmakingRepository.insertNewMatch(matchmaking)
    }

    /**
     * Inserisce un nuovo oggetto nella tabella MatchSummaryConcluded richiamando la funzione insertNewSummaryMatch di MatchSummaryConcluded.
     *
     * @param matchSummaryConcluded Oggetto da inserire come nuova riga nella tabella.
     */
    fun insertNewSummaryMatch(matchSummaryConcluded: MatchSummaryConcluded){
        matchSummaryConcludedRepository.insertNewSummaryMatch(matchSummaryConcluded)
    }

    /**
     * Elimina una riga della tabella Matchmaking richiamando la funzione deleteMatch di MatchmakingRepository.
     *
     * @param id Identificatore della riga da eliminare.
     */
    fun deleteMatch(id: Int){
        matchmakingRepository.deleteMatch(id)
    }

    /**
     * Aggiorna la variabile 'matching' legata al -MutableStateFlow<List<Matchmaking>?>- di 'MatchmakingRepository'.
     * In questo modo si riesce ad ottenere l'aggiornamento di tutte le partite (con un max di 100) nelle quali
     * ci sono utenti che sono in attesa di trovare un avversario.
     *
     * @param nicknameUserCurrent Nickname dell'utente per il quale si vuole eseguire l'aggiornamento.
     */
    fun getAllMatchesWaiting(nicknameUserCurrent:String) {
        return matchmakingRepository.getAllMatchesWaiting(nicknameUserCurrent)
    }

    /**
     * Aggiorna la variabile 'matchesConcludedByCurrentUser' legata al -MutableStateFlow<List<MatchSummaryConcluded>?>- di 'MatchSummaryConcludedRepository'.
     * In questo modo si riesce ad ottenere l'aggiornamento di tutte le partite giocate dall'utente loggato.
     *
     * @param nickname Nickname dell'utente per il quale si vuole eseguire l'aggiornamento.
     */
    fun getAllGamesConludedByNickname(nickname: String){
        return matchSummaryConcludedRepository.getAllGamesConludedByNickname(nickname)
    }

    /**
     * Aggiorna la variabile 'matchesWait' legata al -MutableStateFlow<List<Matchmaking>?>- di 'MatchmakingRepository'.
     * In questo modo si riesce ad ottenere l'aggiornamento di tutte le partite nelle quali l'utente loggato è in attesa.
     *
     * @param nickname Nickname dell'utente per il quale si vuole eseguire l'aggiornamento.
     */
    fun getAllMatchesWaitingByNickname(nickname: String) {
        return matchmakingRepository.getAllMatchesWaitingByNickname(nickname)
    }
}