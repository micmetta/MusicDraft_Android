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

class MatchmakingRepository(val viewModel: MatchmakingViewModel, val dao: MatchmakingDao){

    val matchings: List<Matchmaking>? = null
    val matching: MutableStateFlow<List<Matchmaking>?> = MutableStateFlow(matchings)



    fun insertNewMatch(matchmaking: Matchmaking){
        viewModel.viewModelScope.launch {
            dao.insertNewMatch(matchmaking)
        }
    }

    fun deleteMatch(id: Int){
        viewModel.viewModelScope.launch {
            dao.deleteMatch(id)
        }
    }

    // aggiorna matching:
    fun getAllMatchesWithARangeOfPop(pop: Float) {
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val match = dao.getAllMatchesWithARangeOfPop(pop)
                match.collect { matchFound ->
                    matching.value = matchFound
                    Log.i("MatchmakingRepository", "matching.value: ${matching.value}")
                }
            }
        }
    }

}