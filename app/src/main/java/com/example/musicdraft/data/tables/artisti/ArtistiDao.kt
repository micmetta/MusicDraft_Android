package com.example.musicdraft.data.tables.artisti

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.musicdraft.data.tables.user.User
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) per la gestione degli artisti nel database Room.
 */

@Dao
interface ArtistiDao {
    /**
     * Ottiene tutti gli artisti ordinati per popolarità in modo asincrono tramite Flow.
     */
    @Query("SELECT * FROM Artisti")
    fun getAllArtisti(): Flow<List<Artisti>>

    /**
     * Inserisce un singolo artista in modo asincrono.
     */
    @Insert
    suspend fun insertArtist(artista: Artisti)

    /**
     * Inserisce una lista di artisti in modo asincrono.
     */
    @Insert
    suspend fun insertAllArtist(artisti : Array<Artisti>)

    /**
     * Cancella un artista in modo asincrono.
     */
    @Delete
    suspend fun deleteArtist(artista: Artisti)

    /**
     * Ottiene tutti gli artisti ordinati per popolarità in modo ascendente tramite Flow.
     */
    @Query("SELECT * FROM Artisti ORDER BY popolarita ASC")
    fun getArtistiOrderedByPop(): Flow<List<Artisti>>

    /**
     * Ottiene tutti gli artisti ordinati per nome in modo ascendente tramite Flow.
     */
    @Query("SELECT * FROM Artisti ORDER BY nome ASC")
    fun getArtistiOrderedByNome(): Flow<List<Artisti>>

    /**
     * Ottiene gli artisti con una popolarità massima specificata in modo ascendente tramite Flow.
     */
    @Query("SELECT * FROM Artisti WHERE popolarita <= :maxPopolarita ORDER BY popolarita ASC")
    fun getArtistiWithMaxPop(maxPopolarita: Int): Flow<List<Artisti>>

    /**
     * Ottiene un artista per ID in modo asincrono tramite Flow.
     */
    @Query("SELECT * FROM Artisti WHERE id=:id")
    fun getallArtistbyId(id:String):Flow<List<Artisti>>

}