package com.example.musicdraft.model

import androidx.lifecycle.viewModelScope
import com.example.musicdraft.data.tables.matchmaking.MatchSummaryConcluded
import com.example.musicdraft.data.tables.matchmaking.MatchSummaryConcludedDao
import com.example.musicdraft.viewModel.MatchmakingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MatchSummaryConcludedRepository(val viewModel: MatchmakingViewModel, val dao: MatchSummaryConcludedDao) {

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

}