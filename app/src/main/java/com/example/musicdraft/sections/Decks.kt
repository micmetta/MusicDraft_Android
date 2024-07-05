package com.example.musicdraft.sections

import DeckViewModel
import android.annotation.SuppressLint

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.musicdraft.viewModel.ExchangeManagementCardsViewModel
import com.example.musicdraft.viewModel.LoginViewModel
import com.example.musicdraft.viewModel.DeckViewModel



/**
 * Composable per la visualizzazione e la gestione dei mazzi di carte.
 *
 * @param viewModel ViewModel che gestisce la logica dei mazzi di carte.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Decks(viewModel: DeckViewModel, loginViewModel: LoginViewModel, exchangeManagementCardsViewModel: ExchangeManagementCardsViewModel) {


    viewModel.init()

    val isEditing by viewModel.isEditing
    val isMod by viewModel.isMod
    val selectedDeck by viewModel.selectedDeck.collectAsState()
    val decks = viewModel.mazzi
    val availableCards by viewModel.cards.collectAsState(emptyList())
    val selectedCards by viewModel.selectedCards.collectAsState()
    val message by viewModel.message.collectAsState()
    val deckName by viewModel.deckName.collectAsState()
    val isCreatingNewDeck by viewModel.isCreatingNewDeck.observeAsState(false)

    ////////////////////////////////////////////////////////////////////////////////////////////////
    val reqSentCurrentUser by exchangeManagementCardsViewModel.allOffersSentByCurrentUser.collectAsState(null) // contiene tutte le richieste inviate dall'utente corrente
    val reqReceivedCurrentUser by exchangeManagementCardsViewModel.allOffersReceivedByCurrentUser.collectAsState(null) // contiene tutte le richieste ricevute dall'utente corrente
    loginViewModel.userLoggedInfo.value?.let {
        exchangeManagementCardsViewModel.getOffersReceveidByCurrentUser(it.nickname)
        exchangeManagementCardsViewModel.getOffersSentByCurrentUser(it.nickname)
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    if (message != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearMessage() },
            title = { Text(text = "Error") },
            text = { Text(text = message!!) },
            confirmButton = {
                Button(onClick = { viewModel.clearMessage() }) {
                    Text("OK")
                }
            },
            //text = { Text(message ?: "") }
        )
    }

    if (message != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearMessage() },
            title = { Text(text = "Error") },
            text = { Text(text = message!!) },
            confirmButton = {
                Button(onClick = { viewModel.clearMessage() }) {
                    Text("OK")
                }
            },
            //text = { Text(message ?: "") }
        )
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        Text("I miei mazzi di Carte", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        if (!isEditing) {
            Button(onClick = { viewModel.creaNuovoMazzo() }) {
                Text("Crea Nuovo Mazzo")
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (decks == null || decks.value!!.isEmpty()) {
                Text(
                    "Non hai ancora creato nessun mazzo",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(decks.value!!) { deck ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { viewModel.startEditingDeck(deck) }
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(deck.id_mazzo, style = MaterialTheme.typography.bodyLarge)
                                deck.carte.forEach { card ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Image(
                                            painter = rememberImagePainter(data = card.immagine),
                                            contentDescription = null,
                                            modifier = Modifier.size(50.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("${card.nome_carta} (Popolarità: ${card.popolarita})")
                                    }
                                }

                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(onClick = { viewModel.startEditingDeck(deck) }) {
                                        Text("Modifica")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(onClick = { viewModel.eliminaMazzo(deck)}) {
                                        Text("Elimina")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (selectedDeck != null) {
            Text(
                text = if (selectedDeck!!.carte.isNotEmpty()) "Modifica Mazzo" else "Crea un Nuovo Mazzo",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column {
                OutlinedTextField(
                    value = deckName,
                    onValueChange = { viewModel.updateDeckName(it) },
                    label = { Text("Nome del Mazzo") },
                    placeholder = { Text("Inserisci il nome del mazzo") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    // Colonna per "Aggiungi Carta"
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        item {
                            Text("Aggiungi Carta:", style = MaterialTheme.typography.titleMedium)
                        }

                        if (availableCards?.isEmpty() == true) {
                            item {
                                Text(
                                    "Nessuna carta disponibile",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        } else {
                            items(availableCards!!) { card ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            ////////////////////////////////////////////////////////////////////////////////////
                                            // quando una carta viene selezionata eseguo questo:
                                            viewModel.toggleCardSelection(card, reqSentCurrentUser, reqReceivedCurrentUser)
                                            ////////////////////////////////////////////////////////////////////////////////////
                                        }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Image(
                                            painter = rememberImagePainter(data = card.immagine),
                                            contentDescription = null,
                                            modifier = Modifier.size(50.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("${card.nome_carta}, pop: ${card.popolarita}")
                                    }
                                }
                            }
                        }
                    }

                    // Colonna per "Carte Selezionate"
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        item {
                            Text("Carte Selezionate:", style = MaterialTheme.typography.titleMedium)
                        }

                        items(selectedCards) { card ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { viewModel.toggleCardSelection(
                                        card,
                                        reqSentCurrentUser,
                                        reqReceivedCurrentUser
                                    ) }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Image(
                                        painter = rememberImagePainter(data = card.immagine),
                                        contentDescription = null,
                                        modifier = Modifier.size(50.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("${card.nome_carta} (Popolarità: ${card.popolarita})")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = {
                        if (isCreatingNewDeck) {
                            viewModel.salvaMazzo()
                        } else {
                            viewModel.modificaMazzo()
                        }
                    }) {
                        Text("Salva")
                    }
                    Button(onClick = { viewModel.cancelEditing() }) {
                        Text("Annulla")
                    }
                }
            }
        }
    }
}