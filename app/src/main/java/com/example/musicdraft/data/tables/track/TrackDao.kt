package com.example.musicdraft.data.tables.track

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.musicdraft.data.tables.artisti.Artisti
import kotlinx.coroutines.flow.Flow


@Dao
interface TrackDao {
    @Query("SELECT * FROM Track")
    fun getAllTracks(): Flow<List<Track>>

    @Insert
    suspend fun insertTrack(track: Track)

    @Insert
    suspend fun insertAllTracks(track: Array<Track>)

    @Delete
    suspend fun deleteTrack(track: Track)

    // con il Flow qui sotto, ogni volta che ci sarà un aggiornamento
    // nella tabella artista (ad esempio viene inserito un nuovo artista,
    // il flow qui sotto emetterà una nuova lista di artisti che conterrà
    // anche l'artista appena inserito nel DB:
    @Query("SELECT * FROM Track ORDER BY popolarita ASC")
    fun getTrackOrderedByPop(): Flow<List<Track>>

    @Query("SELECT * FROM Track ORDER BY nome ASC")
    fun getTrackOrderedByName(): Flow<List<Track>>
}