package com.lectorvoz.app.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lectorvoz.app.data.Book
import com.lectorvoz.app.data.BookRepository
import com.lectorvoz.app.data.ReadingProgressStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = BookRepository(app)
    private val progressStore = ReadingProgressStore(app)

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _hasFolder = MutableStateFlow(repository.getSavedFolderUri() != null)
    val hasFolder: StateFlow<Boolean> = _hasFolder

    fun onFolderChosen(uri: Uri) {
        repository.persistFolderPermission(uri)
        _hasFolder.value = true
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _loading.value = true
            _books.value = repository.scanBooks()
            _loading.value = false
        }
    }

    suspend fun loadParagraphs(book: Book) = repository.loadContent(book).paragraphs

    fun getProgress(bookId: String) = progressStore.getProgress(bookId)
    fun saveProgress(bookId: String, index: Int) = progressStore.saveProgress(bookId, index)
}
