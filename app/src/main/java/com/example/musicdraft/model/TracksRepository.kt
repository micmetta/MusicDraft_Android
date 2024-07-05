package com.example.musicdraft.model

import com.example.musicdraft.data.tables.track.Track
import com.example.musicdraft.data.tables.track.TrackDao
import com.example.musicdraft.viewModel.MarketplaceViewModel
import kotlinx.coroutines.flow.Flow

/**
 * Repository per la gestione delle tracce musicali nell'applicazione.
 *
 * @property viewModel Il ViewModel associato a questo repository.
 * @property dao Il DAO per l'accesso alla tabella delle tracce nel database.
 */
class TracksRepository(val viewModel: MarketplaceViewModel, val dao: TrackDao) {

    /**
     * Ottiene tutte le tracce musicali dal database.
     *
     * @return Un flusso contenente la lista delle tracce.
     */
    suspend fun getAllTracks(): Flow<List<Track>> {
        return dao.getAllTracks()
    }

    /**
     * Elimina una traccia musicale dal database.
     *
     * @param track La traccia da eliminare.
     */
    suspend fun deleteTrack(track: Track) {
        dao.deleteTrack(track)
    }
}
