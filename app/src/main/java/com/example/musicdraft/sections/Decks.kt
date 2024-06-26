import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

//
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Decks(viewModel: DeckViewModel) {

    viewModel.init()

    val isEditing by viewModel.isEditing
    val selectedDeck by viewModel.selectedDeck.collectAsState()
    val decks = viewModel.mazzi
    val availableCards by viewModel.cards.collectAsState(emptyList())
    val selectedCards by viewModel.selectedCards.collectAsState()
    val message by viewModel.message.collectAsState()

    if (message != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearMessage() },
            confirmButton = {
                Button(onClick = { viewModel.clearMessage() }) {
                    Text("OK")
                }
            },
            text = { Text(message ?: "") }
        )
    }
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text("I miei mazzi di Carte", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        if (!isEditing) {
            Button(onClick = { viewModel.creaNuovoMazzo() }) {
                Text("Crea Nuovo Mazzo")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (decks == null || decks.isEmpty()) {
                Text(
                    "Non hai ancora creato nessun mazzo",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(decks!!) { deck ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            onClick = { /* Implementa l'azione desiderata */ }
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
                            }
                        }
                    }
                }
            }
        } else if (selectedDeck != null) {
            Text(
                text = if (selectedDeck != null) "Modifica Mazzo" else "Crea un Nuovo Mazzo",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column {
                OutlinedTextField(
                    value = selectedDeck?.id_mazzo ?: "",
                    onValueChange = { /* viewModel.updateDeckName(it) */ },
                    label = { Text("Nome del Mazzo") },
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

                        if (availableCards?.isEmpty() == true || availableCards==null) {
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
                                        .padding(vertical = 4.dp),
                                    onClick = { viewModel.toggleCardSelection(card) }
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
                                    .padding(vertical = 4.dp),
                                onClick = { /* Implementa l'azione desiderata */ }
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
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    Button(onClick = { viewModel.salvaMazzo() }) {
                        Text("Salva")
                    }
                    Button(onClick = { viewModel.annullaModifica() }) {
                        Text("Annulla")
                    }
                }
            }
        }

        // Pulsanti Salva e Annulla fissi

        }
    }

