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

    Column(modifier = Modifier.padding(vertical = 60.dp)) {
        Text("I miei mazzi di Carte", style = MaterialTheme.typography.titleLarge)

        if (!isEditing) {
            Button(onClick = { viewModel.creaNuovoMazzo() }) {
                Text("Crea Nuovo Mazzo")
            }
            LazyColumn {
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
                            deck.carte.forEach {card->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Image(
                                        painter = rememberImagePainter(data = card?.immagine),
                                        contentDescription = null,
                                        modifier = Modifier.size(50.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("${card?.nome_carta} (Popolarità: ${card?.popolarita})")
                                }
                            }
                        }
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(onClick = { viewModel.modificaMazzo(deck) }) {
                                    Text("Modifica")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(onClick = { viewModel.eliminaMazzo(deck) }) {
                                    Text("Elimina")
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

            Column {
                OutlinedTextField(
                    value = selectedDeck?.id_mazzo ?: "",
                    onValueChange = { /* viewModel.updateDeckName(it) */ },
                    label = { Text("Nome del Mazzo") },
                    modifier = Modifier.fillMaxWidth()
                )

                LazyColumn {
                    (selectedDeck?: null)?.let {it->
                        items(selectedDeck!!.carte) { card ->
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
                                        Text("${card.id_carta} (Popolarità: ${card.popolarita})")
                                    }
                                    Row(
                                        horizontalArrangement = Arrangement.End,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Button(onClick = { /* Implementa l'azione desiderata */ }) {
                                            Text("Rimuovi")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Text("Aggiungi Carta:", style = MaterialTheme.typography.titleLarge)
                LazyColumn {
                    items(availableCards!!) { card ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
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
                                Text(card.id_carta)
                            }
                        }
                    }
                }

                // Aggiungi altri tipi di card come necessario

                Text("Carte Selezionate:", style = MaterialTheme.typography.titleLarge)
                LazyColumn {
                    items(selectedCards) { card ->
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
                                    Text("${card.id_carta} (Popolarità: ${card.popolarita})")
                                }
                            }
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
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
    }
}
