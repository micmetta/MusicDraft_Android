package com.example.musicdraft.data.tables.artisti

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistiDao {
    @Query("SELECT * FROM Artisti")
    fun getAllArtisti(): Flow<List<Artisti>>

    @Insert
    suspend fun insertArtist(artista: Artisti)

    @Insert
    suspend fun insertAllArtist(artisti : Array<Artisti>)

    @Delete
    suspend fun deleteArtist(artista: Artisti)

    // con il Flow qui sotto, ogni volta che ci sarà un aggiornamento
    // nella tabella artista (ad esempio viene inserito un nuovo artista,
    // il flow qui sotto emetterà una nuova lista di artisti che conterrà
    // anche l'artista appena inserito nel DB:
    @Query("SELECT * FROM Artisti ORDER BY popolarita ASC")
    fun getArtistiOrderedByPop(): Flow<List<Artisti>>

    @Query("SELECT * FROM Artisti ORDER BY nome ASC")
    fun getArtistiOrderedByNome(): Flow<List<Artisti>>

    @Query("SELECT * FROM Artisti WHERE popolarita <= :maxPopolarita ORDER BY popolarita ASC")
    fun getArtistiWithMaxPop(maxPopolarita: Int): Flow<List<Artisti>>
}