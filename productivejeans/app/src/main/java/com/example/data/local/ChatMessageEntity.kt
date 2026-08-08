package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String, // "User" or "Bunny"
    val text: String,
    val isTaskSuggestion: Boolean = false,
    val suggestedTaskTitle: String? = null,
    val timestampMillis: Long = System.currentTimeMillis()
)
