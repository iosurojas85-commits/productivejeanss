package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val durationMinutes: Int,
    val sessionType: String = "Focus", // "Focus" or "Break"
    val companionName: String = "Bunny", // e.g. "Bunny", "Hanni Vibe", "Minji Vibe"
    val timestampMillis: Long = System.currentTimeMillis()
)
