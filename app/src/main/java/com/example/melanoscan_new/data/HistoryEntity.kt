package com.example.melanoscan_new.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val imagePath: String,
    val result: String,
    val confidence: Float,
    val timestamp: Long = System.currentTimeMillis()
)