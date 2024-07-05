package com.example.musicdraft.data.tables.track

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.musicdraft.data.tables.artisti.Artisti
import kotlinx.coroutines.flow.Flow


/**
 * Data Access Object (DAO) per l'entità Track, gestisce l'accesso e le operazioni
 * sul database per i brani musicali.
 */
@Dao
interface TrackDao {

    /**
     * Ottiene tutti i brani musicali presenti nel database.
     *
     * @return Flow che emette una lista di Track ogni volta che ci sono aggiornamenti nel database.
     */
    @Query("SELECT * FROM Track")
    fun getAllTracks(): Flow<List<Track>>

    /**
     * Inserisce un nuovo brano musicale nel database.
     *
     * @param track Il brano da inserire nel database.
     */
    @Insert
    suspend fun insertTrack(track: Track)

    /**
     * Inserisce una lista di brani musicali nel database.
     *
     * @param tracks Array di brani musicali da inserire nel database.
     */
    @Insert
    suspend fun insertAllTracks(tracks: Array<Track>)

    /**
     * Elimina un brano musicale dal database.
     *
     * @param track Il brano da eliminare dal database.
     */
    @Delete
    suspend fun deleteTrack(track: Track)

    /**
     * Ottiene tutti i brani musicali ordinati per popolarità crescente.
     *
     * @return Flow che emette una lista di Track ordinate per popolarità.
     */
    @Query("SELECT * FROM Track ORDER BY popolarita ASC")
    fun getTrackOrderedByPop(): Flow<List<Track>>

    /**
     * Ottiene tutti i brani musicali ordinati per nome in ordine alfabetico crescente.
     *
     * @return Flow che emette una lista di Track ordinate per nome.
     */
    @Query("SELECT * FROM Track ORDER BY nome ASC")
    fun getTrackOrderedByName(): Flow<List<Track>>

    /**
     * Ottiene tutti i brani musicali con una popolarità massima specificata.
     *
     * @param maxPopolarita La popolarità massima dei brani da restituire.
     * @return Flow che emette una lista di Track con popolarità fino a maxPopolarita.
     */
    @Query("SELECT * FROM Track WHERE popolarita <= :maxPopolarita ORDER BY popolarita ASC")
    fun getTracksWithMaxPop(maxPopolarita: Int): Flow<List<Track>>
}