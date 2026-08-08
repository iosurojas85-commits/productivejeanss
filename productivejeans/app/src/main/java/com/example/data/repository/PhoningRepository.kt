package com.example.data.repository

import com.example.data.local.ChatMessageDao
import com.example.data.local.ChatMessageEntity
import com.example.data.local.FocusSessionDao
import com.example.data.local.FocusSessionEntity
import com.example.data.local.TaskDao
import com.example.data.local.TaskEntity
import kotlinx.coroutines.flow.Flow

class PhoningRepository(
    private val taskDao: TaskDao,
    private val focusSessionDao: FocusSessionDao,
    private val chatMessageDao: ChatMessageDao
) {
    val tasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val focusSessions: Flow<List<FocusSessionEntity>> = focusSessionDao.getAllSessions()
    val totalFocusMinutes: Flow<Int?> = focusSessionDao.getTotalFocusMinutes()
    val totalSessionsCount: Flow<Int> = focusSessionDao.getTotalCompletedSessions()
    val chatMessages: Flow<List<ChatMessageEntity>> = chatMessageDao.getAllMessages()

    suspend fun insertTask(task: TaskEntity): Long = taskDao.insertTask(task)
    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)
    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)
    suspend fun deleteTaskById(id: Long) = taskDao.deleteTaskById(id)
    suspend fun toggleTaskCompletion(task: TaskEntity) {
        taskDao.updateTaskCompletion(task.id, !task.isCompleted)
    }

    suspend fun recordFocusSession(minutes: Int, sessionType: String = "Focus", companionName: String = "Bunny"): Long {
        return focusSessionDao.insertSession(
            FocusSessionEntity(
                durationMinutes = minutes,
                sessionType = sessionType,
                companionName = companionName
            )
        )
    }

    suspend fun sendChatMessage(sender: String, text: String, isTaskSuggestion: Boolean = false, taskTitle: String? = null) {
        chatMessageDao.insertMessage(
            ChatMessageEntity(
                sender = sender,
                text = text,
                isTaskSuggestion = isTaskSuggestion,
                suggestedTaskTitle = taskTitle
            )
        )
    }

    suspend fun clearChat() {
        chatMessageDao.clearAllMessages()
    }
}
