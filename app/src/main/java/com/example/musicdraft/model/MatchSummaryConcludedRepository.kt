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

class MatchSummaryConcludedRepository(val viewModel: MatchmakingViewModel, val dao: MatchSummaryConcludedDao) {

    val matches: List<MatchSummaryConcluded>? = null
    val matchesConcludedByCurrentUser: MutableStateFlow<List<MatchSummaryConcluded>?> = MutableStateFlow(matches)


    fun insertNewSummaryMatch(matchSummaryConcluded: MatchSummaryConcluded){
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dao.insertNewSummaryMatch(matchSummaryConcluded)
            }
        }
    }

    fun deleteSummaryMatch(id: Int){
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dao.deleteSummaryMatch(id)
            }
        }
    }


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