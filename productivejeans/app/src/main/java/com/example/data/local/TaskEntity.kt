package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val note: String = "",
    val category: String = "Study", // Study, Work, Glow Up, Chill, Custom
    val priorityStar: Boolean = false,
    val isCompleted: Boolean = false,
    val dueDateMillis: Long? = null,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val subtasksJson: String = "" // Simple comma or newline separated list
)
