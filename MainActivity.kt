package com.lectorvoz.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lectorvoz.app.data.Book
import com.lectorvoz.app.data.BookFormat
import com.lectorvoz.app.ui.BookViewModel
import com.lectorvoz.app.ui.LibraryScreen
import com.lectorvoz.app.ui.ReaderScreen
import com.lectorvoz.app.ui.theme.LectorVozTheme
import java.net.URLDecoder
import java.net.URLEncoder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LectorVozTheme {
                val notifPermission = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) {}
                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                val viewModel: BookViewModel = viewModel()
                val navController = rememberNavController()
                // Guardamos en memoria los libros por id para poder reconstruir
                // el objeto Book al navegar (evita serializar el Bitmap en la ruta).
                val booksById = remember(viewModel) { mutableMapOf<String, Book>() }

                NavHost(navController = navController, startDestination = "library") {
                    composable("library") {
                        LibraryScreen(
                            viewModel = viewModel,
                            onOpenBook = { book ->
                                booksById[book.id] = book
                                val encoded = URLEncoder.encode(book.id, "UTF-8")
                                navController.navigate("reader/$encoded")
                            }
                        )
                    }
                    composable(
                        "reader/{bookId}",
                        arguments = listOf(navArgument("bookId") { type = NavType.StringType })
                    ) { entry ->
                        val bookId = URLDecoder.decode(entry.arguments?.getString("bookId") ?: "", "UTF-8")
                        val book = booksById[bookId]
                        if (book != null) {
                            ReaderScreen(
                                viewModel = viewModel,
                                book = book,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }

                LaunchedEffect(Unit) { viewModel.refresh() }
            }
        }
    }
}
