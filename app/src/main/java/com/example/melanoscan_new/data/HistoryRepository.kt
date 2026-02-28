package com.example.melanoscan_new.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class HistoryRepository(context: Context) {

    private val historyDao: HistoryDao

    init {
        val database = AppDatabase.getDatabase(context)
        historyDao = database.historyDao()
    }

    fun getAllHistory(): Flow<List<HistoryEntity>> = historyDao.getAllHistory()

    suspend fun saveClassificationResult(result: com.example.melanoscan_new.util.TfliteClassifier.Companion.ClassificationResult, imageUri: String) {
        withContext(Dispatchers.IO) {
            val historyEntity = HistoryEntity(
                imagePath = imageUri,
                result = result.className,
                confidence = result.confidence
            )
            historyDao.insertHistory(historyEntity)
        }
    }

    suspend fun deleteHistory(history: HistoryEntity) {
        withContext(Dispatchers.IO) {
            historyDao.deleteHistory(history)
        }
    }

    suspend fun clearAllHistory() {
        withContext(Dispatchers.IO) {
            historyDao.clearAllHistory()
        }
    }
}