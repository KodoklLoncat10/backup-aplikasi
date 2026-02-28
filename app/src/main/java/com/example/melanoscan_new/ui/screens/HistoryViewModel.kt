package com.example.melanoscan_new.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.melanoscan_new.data.HistoryEntity
import com.example.melanoscan_new.data.HistoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HistoryRepository

    val history: StateFlow<List<HistoryEntity>>

    init {
        repository = HistoryRepository(application)
        history = repository.getAllHistory().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun deleteHistory(history: HistoryEntity) {
        viewModelScope.launch {
            repository.deleteHistory(history)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAllHistory()
        }
    }
}