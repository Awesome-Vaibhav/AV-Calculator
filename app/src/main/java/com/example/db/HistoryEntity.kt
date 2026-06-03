package com.example.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "basic", "scientific", etc.
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)
