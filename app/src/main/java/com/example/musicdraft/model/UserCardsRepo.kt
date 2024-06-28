package com.example.musicdraft.model

import androidx.lifecycle.viewModelScope
import com.example.musicdraft.data.tables.user.UserDao
import com.example.musicdraft.data.tables.user_cards.UCADao
import com.example.musicdraft.data.tables.user_cards.UCTDao
import com.example.musicdraft.data.tables.user_cards.User_Cards_Artisti
import com.example.musicdraft.data.tables.user_cards.User_Cards_Track
import com.example.musicdraft.viewModel.CardsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Repository per la gestione delle carte utente nell'applicazione.
 *
 * @property dao Il DAO per l'accesso alle carte degli artisti nel database.
 * @property daoLog Il DAO per l'accesso agli utenti nel database.
 * @property daoTrack Il DAO per l'accesso alle carte delle tracce nel database.
 * @property cardsViewModel Il ViewModel associato a questo repository.
 */
class UserCardsRepo(
    val dao: UCADao,
    val daoLog: UserDao,
    val daoTrack: UCTDao,
    private val cardsViewModel: CardsViewModel
) {

    /** Lista delle carte degli artisti inizializzata a null. */
    val artCard: List<User_Cards_Artisti>? = null

    /** Flusso che contiene tutte le carte degli artisti per un utente. */
    private val _allCardsforUserA = MutableStateFlow<List<User_Cards_Artisti>>(emptyList())
    val allCardsforUserA: StateFlow<List<User_Cards_Artisti>> get() = _allCardsforUserA

    /** Flusso che contiene tutte le carte delle tracce per un utente. */
    val allCardsforUserT: MutableStateFlow<List<User_Cards_Track>?> = MutableStateFlow(emptyList())

    /**
     * Ottiene tutte le carte degli artisti per un utente specifico e aggiorna il flusso [_allCardsforUserA].
     *
     * @param email L'email dell'utente.
     */
    fun getArtCardsforUser(email: String) {
        cardsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val allCardsForUsers = dao.getAllCardArtForUser(email)
                allCardsForUsers.collect { response ->
                    _allCardsforUserA.value = response
                }
            }
        }
    }

    /**
     * Ottiene tutte le carte delle tracce per un utente specifico e aggiorna il flusso [allCardsforUserT].
     *
     * @param email L'email dell'utente.
     */
    fun getTrackCardsforUser(email: String) {
        cardsViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val allCardsForUsers = daoTrack.getAllCardTrackForUser(email)
                allCardsForUsers.collect { response ->
                    allCardsforUserT.value = response
                }
            }
        }
    }

    /**
     * Inserisce una nuova carta traccia per un utente.
     *
     * @param obj La carta traccia da inserire.
     */
    fun insertUserCardTrack(obj: User_Cards_Track) {
        cardsViewModel.viewModelScope.launch {
            daoTrack.insertUserCardTrack(obj)
        }
    }

    /**
     * Inserisce una nuova carta artista per un utente.
     *
     * @param obj La carta artista da inserire.
     */
    fun insertUserCardArtista(obj: User_Cards_Artisti) {
        cardsViewModel.viewModelScope.launch {
            dao.insertUserCardArt(obj)
        }
    }

    /**
     * Aggiorna i punti per un utente.
     *
     * @param point Il nuovo punteggio.
     * @param email L'email dell'utente.
     */
    fun updatePoints(point: Int, email: String) {
        cardsViewModel.viewModelScope.launch {
            daoLog.updatePoints(point, email)
        }
    }

    /**
     * Ottiene una carta artista per ID e utente specifico e aggiorna il flusso [_allCardsforUserA].
     *
     * @param id_carta L'ID della carta.
     * @param email L'email dell'utente.
     * @return La lista delle carte degli artisti con l'ID specificato.
     */
    fun getArtistCardById(id_carta: String, email: String): List<User_Cards_Artisti>? {
        cardsViewModel.viewModelScope.launch {
            val allCardsForUsers = dao.getCardbyId(id_carta, email)
            allCardsForUsers.collect { response ->
                _allCardsforUserA.value = response
            }
        }
        return allCardsforUserA.value
    }

    /**
     * Ottiene una carta traccia per ID e utente specifico e aggiorna il flusso [allCardsforUserT].
     *
     * @param id_carta L'ID della carta.
     * @param email L'email dell'utente.
     * @return La lista delle carte delle tracce con l'ID specificato.
     */
    fun getTrackCardById(id_carta: String, email: String): List<User_Cards_Track>? {
        cardsViewModel.viewModelScope.launch {
            val allCardsForUsers = daoTrack.getCardbyId(id_carta, email)
            allCardsForUsers.collect { response ->
                allCardsforUserT.value = response
            }
        }
        return allCardsforUserT.value
    }

    /**
     * Ottiene tutte le carte degli artisti presenti sul mercato e aggiorna il flusso [_allCardsforUserA].
     *
     * @return La lista delle carte degli artisti sul mercato.
     */
    fun getAllOnMarketCardsA(): List<User_Cards_Artisti>? {
        cardsViewModel.viewModelScope.launch {
            val allCardsForUsers = dao.getCardsOnMarket()
            allCardsForUsers.collect { response ->
                _allCardsforUserA.value = response
            }
        }
        return allCardsforUserA.value
    }

    /**
     * Ottiene tutte le carte delle tracce presenti sul mercato e aggiorna il flusso [allCardsforUserT].
     *
     * @return La lista delle carte delle tracce sul mercato.
     */
    fun getAllOnMarketCardsT(): List<User_Cards_Track>? {
        cardsViewModel.viewModelScope.launch {
            val allCardsForUsers = daoTrack.getCardsOnMarket()
            allCardsForUsers.collect { response ->
                allCardsforUserT.value = response
            }
        }
        return allCardsforUserT.value
    }

    /**
     * Aggiorna lo stato di una carta artista come presente sul mercato.
     *
     * @param email L'email dell'utente.
     * @param id_carta L'ID della carta.
     */
    fun updateMarketStateA(email: String, id_carta: String) {
        cardsViewModel.viewModelScope.launch {
            dao.updateOnMarkeState(email, id_carta)
        }
    }

    /**
     * Aggiorna lo stato di una carta artista come non presente sul mercato.
     *
     * @param email L'email dell'utente.
     * @param id_carta L'ID della carta.
     */
    fun updateMarketNotStateA(email: String, id_carta: String) {
        cardsViewModel.viewModelScope.launch {
            dao.updateNotOnMarkeState(email, id_carta)
        }
    }

    /**
     * Aggiorna lo stato di una carta traccia come presente sul mercato.
     *
     * @param email L'email dell'utente.
     * @param id_carta L'ID della carta.
     */
    fun updateMarketStateT(email: String, id_carta: String) {
        cardsViewModel.viewModelScope.launch {
            daoTrack.updateOnMarkeState(email, id_carta)
        }
    }

    /**
     * Aggiorna lo stato di una carta traccia come non presente sul mercato.
     *
     * @param email L'email dell'utente.
     * @param id_carta L'ID della carta.
     */
    fun updateMarketNotStateT(email: String, id_carta: String) {
        cardsViewModel.viewModelScope.launch {
            daoTrack.updateNotOnMarkeState(email, id_carta)
        }
    }
}
