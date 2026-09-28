package com.lectorvoz.app.ui

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lectorvoz.app.data.Book
import com.lectorvoz.app.service.TtsReaderService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    viewModel: BookViewModel,
    book: Book,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var paragraphs by remember { mutableStateOf<List<String>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(0) }
    var total by remember { mutableStateOf(0) }
    var isSpeaking by remember { mutableStateOf(false) }
    var speed by remember { mutableStateOf(1.0f) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(book.id) {
        loading = true
        paragraphs = viewModel.loadParagraphs(book)
        total = paragraphs.size
        val savedIndex = viewModel.getProgress(book.id).coerceIn(0, (paragraphs.size - 1).coerceAtLeast(0))
        currentIndex = savedIndex
        loading = false

        TtsReaderService.onProgress = { idx, tot, speaking ->
            currentIndex = idx
            total = tot
            isSpeaking = speaking
            viewModel.saveProgress(book.id, idx)
        }

        val intent = Intent(context, TtsReaderService::class.java).apply {
            action = TtsReaderService.ACTION_START
            putExtra(TtsReaderService.EXTRA_TITLE, book.title)
            putStringArrayListExtra(TtsReaderService.EXTRA_PARAGRAPHS, ArrayList(paragraphs))
            putExtra(TtsReaderService.EXTRA_START_INDEX, savedIndex)
            putExtra(TtsReaderService.EXTRA_SPEED, speed)
        }
        context.startForegroundService(intent)
    }

    DisposableEffect(Unit) {
        onDispose { TtsReaderService.onProgress = null }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(book.title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←", style = MaterialTheme.typography.titleLarge) }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (loading) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = if (total > 0) "Párrafo ${currentIndex + 1} de $total" else "Sin contenido",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                if (total > 0) {
                    Text(
                        text = paragraphs.getOrElse(currentIndex) { "" },
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 4
                    )
                }
                Spacer(Modifier.height(24.dp))
                LinearProgressIndicator(
                    progress = if (total > 0) (currentIndex + 1) / total.toFloat() else 0f,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(32.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    FilledIconButton(
                        modifier = Modifier.size(72.dp),
                        onClick = {
                            val action = if (isSpeaking) TtsReaderService.ACTION_PAUSE else TtsReaderService.ACTION_PLAY
                            context.startService(Intent(context, TtsReaderService::class.java).setAction(action))
                        }
                    ) {
                        Icon(
                            if (isSpeaking) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    FilledTonalIconButton(
                        modifier = Modifier.size(72.dp),
                        onClick = {
                            context.startService(
                                Intent(context, TtsReaderService::class.java).setAction(TtsReaderService.ACTION_STOP)
                            )
                        }
                    ) {
                        Icon(Icons.Filled.Stop, contentDescription = null, modifier = Modifier.size(36.dp))
                    }
                }

                Spacer(Modifier.height(32.dp))
                Text("Velocidad: ${String.format("%.1f", speed)}x")
                Slider(
                    value = speed,
                    onValueChange = {
                        speed = it
                        context.startService(
                            Intent(context, TtsReaderService::class.java)
                                .setAction(TtsReaderService.ACTION_SET_SPEED)
                                .putExtra(TtsReaderService.EXTRA_SPEED, speed)
                        )
                    },
                    valueRange = 0.5f..2.0f
                )
            }
        }
    }
}
