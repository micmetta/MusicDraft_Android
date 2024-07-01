package com.example.musicdraft.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.musicdraft.data.tables.matchmaking.MatchSummaryConcluded
import com.example.musicdraft.data.tables.matchmaking.Matchmaking
import com.example.musicdraft.database.MusicDraftDatabase
import com.example.musicdraft.model.MatchSummaryConcludedRepository
import com.example.musicdraft.model.MatchmakingRepository

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




//    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    // - Variabili logica di controllo
//    private val _matches = MutableStateFlow<List<Matchmaking>>(emptyList())
//    val matches: StateFlow<List<Matchmaking>> get() = _matches
//    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////
    var matching = matchmakingRepository.matching
    /////////////////////////////////////////////////////////////////////////////


    fun insertNewMatch(matchmaking: Matchmaking){
        matchmakingRepository.insertNewMatch(matchmaking)
    }

    fun insertNewSummaryMatch(matchSummaryConcluded: MatchSummaryConcluded){
        matchSummaryConcludedRepository.insertNewSummaryMatch(matchSummaryConcluded)
    }


    fun deleteMatch(id: Int){
        matchmakingRepository.deleteMatch(id)
    }

    fun deleteSummaryMatch(id: Int){
        matchSummaryConcludedRepository.deleteSummaryMatch(id)
    }



    // aggiorna matching:
    fun getAllMatchesWithARangeOfPop(pop: Float) {
        return matchmakingRepository.getAllMatchesWithARangeOfPop(pop)
    }

}