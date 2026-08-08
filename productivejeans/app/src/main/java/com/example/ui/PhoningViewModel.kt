package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.TaskEntity
import com.example.data.remote.GeminiAiService
import com.example.data.repository.PhoningRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class WidgetType {
    POMODORO_TIMER, // Pomodoro Call Widget with Play, Pause, Reset
    QUICK_TASK_PAD,  // Quick Task Pad with task completion & quick add
    AI_COACH_TIP,   // Gemini AI Coach Quote/Tip widget
    FOCUS_STREAK,   // Focus Streak & Minutes badge
    LOFI_RADIO      // Cozy Lo-Fi Music Widget
}

data class ScreenWidget(
    val id: String,
    val type: WidgetType,
    val title: String
)

enum class PomodoroMode {
    FOCUS, BREAK
}

class PhoningViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = PhoningRepository(db.taskDao(), db.focusSessionDao(), db.chatMessageDao())
    private val geminiAiService = GeminiAiService()

    // --- Active Screen Widgets ---
    private val _activeWidgets = MutableStateFlow(
        listOf(
            ScreenWidget("w_pomo", WidgetType.POMODORO_TIMER, "Pomodoro Call Timer 📞"),
            ScreenWidget("w_ai", WidgetType.AI_COACH_TIP, "Productive Jeans AI Coach 👖"),
            ScreenWidget("w_task", WidgetType.QUICK_TASK_PAD, "Quick Task Pad 📝"),
            ScreenWidget("w_streak", WidgetType.FOCUS_STREAK, "Focus Streak & Stats 🔥")
        )
    )
    val activeWidgets: StateFlow<List<ScreenWidget>> = _activeWidgets.asStateFlow()

    fun addWidget(type: WidgetType) {
        val existing = _activeWidgets.value
        if (existing.any { it.type == type }) return

        val title = when (type) {
            WidgetType.POMODORO_TIMER -> "Pomodoro Call Timer 📞"
            WidgetType.QUICK_TASK_PAD -> "Quick Task Pad 📝"
            WidgetType.AI_COACH_TIP -> "Productive Jeans AI Coach 👖"
            WidgetType.FOCUS_STREAK -> "Focus Streak & Stats 🔥"
            WidgetType.LOFI_RADIO -> "Cozy Lo-Fi Radio 🎵"
        }
        val newWidget = ScreenWidget(UUID.randomUUID().toString(), type, title)
        _activeWidgets.value = _activeWidgets.value + newWidget
    }

    fun removeWidget(widgetId: String) {
        _activeWidgets.value = _activeWidgets.value.filter { it.id != widgetId }
    }

    // --- Navigation State ---
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    // --- Task State ---
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val allTasks = repository.tasks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredTasks = combine(repository.tasks, _selectedCategory, _searchQuery) { tasks, cat, query ->
        tasks.filter { task ->
            val matchesCategory = if (cat == "All") true else task.category.equals(cat, ignoreCase = true)
            val matchesQuery = query.isBlank() || task.title.contains(query, ignoreCase = true) || task.note.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalFocusMinutes = repository.totalFocusMinutes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val totalSessionsCount = repository.totalSessionsCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val chatMessages = repository.chatMessages.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addTask(title: String, note: String, category: String, priorityStar: Boolean) {
        viewModelScope.launch {
            repository.insertTask(
                TaskEntity(
                    title = title,
                    note = note,
                    category = category,
                    priorityStar = priorityStar
                )
            )
        }
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(task)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // --- Pomodoro Call Timer State ---
    private val _pomodoroMode = MutableStateFlow(PomodoroMode.FOCUS)
    val pomodoroMode: StateFlow<PomodoroMode> = _pomodoroMode.asStateFlow()

    private val _customFocusDurationMinutes = MutableStateFlow(25)
    val customFocusDurationMinutes: StateFlow<Int> = _customFocusDurationMinutes.asStateFlow()

    private val _customBreakDurationMinutes = MutableStateFlow(5)
    val customBreakDurationMinutes: StateFlow<Int> = _customBreakDurationMinutes.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(25 * 60)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _selectedCompanion = MutableStateFlow("Bunny 🐰")
    val selectedCompanion: StateFlow<String> = _selectedCompanion.asStateFlow()

    private val _ambientSound = MutableStateFlow("Lofi Rain 🌧️")
    val ambientSound: StateFlow<String> = _ambientSound.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    fun setUserName(name: String) {
        _userName.value = name
    }

    fun getEffectiveUserName(): String {
        return _userName.value.trim().ifEmpty { "Bunnie" }
    }

    private val _selectedMusicPlatform = MutableStateFlow("Spotify")
    val selectedMusicPlatform: StateFlow<String> = _selectedMusicPlatform.asStateFlow()

    private val _selectedPlaylistQuery = MutableStateFlow("Focus Session")
    val selectedPlaylistQuery: StateFlow<String> = _selectedPlaylistQuery.asStateFlow()

    private var timerJob: Job? = null

    fun setMusicPlatform(platform: String) {
        _selectedMusicPlatform.value = platform
    }

    fun setPlaylistQuery(query: String) {
        _selectedPlaylistQuery.value = query
    }

    fun openSpotify(context: android.content.Context, searchOrUri: String? = null) {
        val query = searchOrUri ?: _selectedPlaylistQuery.value
        try {
            val uriStr = if (query.startsWith("spotify:")) {
                query
            } else {
                "spotify:search:${android.net.Uri.encode(query)}"
            }
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(uriStr)).apply {
                setPackage("com.spotify.music")
                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val webUrl = "https://open.spotify.com/search/${android.net.Uri.encode(query)}"
                val webIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(webUrl)).apply {
                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(webIntent)
            } catch (ex: Exception) {
                android.widget.Toast.makeText(context, "Abriendo Spotify...", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun openYouTubeMusic(context: android.content.Context, searchOrUri: String? = null) {
        val query = searchOrUri ?: _selectedPlaylistQuery.value
        try {
            val webUrl = "https://music.youtube.com/search?q=${android.net.Uri.encode(query)}"
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(webUrl)).apply {
                setPackage("com.google.android.apps.youtube.music")
                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val webUrl = "https://music.youtube.com/search?q=${android.net.Uri.encode(query)}"
                val webIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(webUrl)).apply {
                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(webIntent)
            } catch (ex: Exception) {
                android.widget.Toast.makeText(context, "Abriendo YouTube Music...", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun openAppleMusic(context: android.content.Context, searchOrUri: String? = null) {
        val query = searchOrUri ?: _selectedPlaylistQuery.value
        try {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("music://")).apply {
                setPackage("com.apple.android.music")
                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val webUrl = "https://music.apple.com/us/search?term=${android.net.Uri.encode(query)}"
                val webIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(webUrl)).apply {
                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(webIntent)
            } catch (ex: Exception) {
                android.widget.Toast.makeText(context, "Abriendo Apple Music...", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun sendMediaControl(context: android.content.Context, keyCode: Int) {
        val audioManager = context.getSystemService(android.content.Context.AUDIO_SERVICE) as? android.media.AudioManager
        if (audioManager != null) {
            val eventDown = android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, keyCode)
            val eventUp = android.view.KeyEvent(android.view.KeyEvent.ACTION_UP, keyCode)
            audioManager.dispatchMediaKeyEvent(eventDown)
            audioManager.dispatchMediaKeyEvent(eventUp)
            val msg = when (keyCode) {
                android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> "Play/Pause enviado a Spotify/YT Music 🎵"
                android.view.KeyEvent.KEYCODE_MEDIA_NEXT -> "Siguiente canción ⏭️"
                android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS -> "Canción anterior ⏮️"
                else -> "Comando de música enviado 🎶"
            }
            android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    fun setCompanion(companion: String) {
        _selectedCompanion.value = companion
    }

    fun setAmbientSound(sound: String) {
        _ambientSound.value = sound
    }

    fun setFocusDuration(minutes: Int) {
        _customFocusDurationMinutes.value = minutes
        if (_pomodoroMode.value == PomodoroMode.FOCUS && !_isTimerRunning.value) {
            _remainingSeconds.value = minutes * 60
        }
    }

    fun setBreakDuration(minutes: Int) {
        _customBreakDurationMinutes.value = minutes
        if (_pomodoroMode.value == PomodoroMode.BREAK && !_isTimerRunning.value) {
            _remainingSeconds.value = minutes * 60
        }
    }

    init {
        // Register receiver for widget toggle intent
        try {
            val filter = android.content.IntentFilter(com.example.widget.ProductiveJeansWidgetProvider.ACTION_VIEWMODEL_TOGGLE_TIMER)
            val receiver = object : android.content.BroadcastReceiver() {
                override fun onReceive(context: android.content.Context?, intent: android.content.Intent?) {
                    if (_isTimerRunning.value) {
                        pauseTimer()
                    } else {
                        startTimer()
                    }
                }
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                application.registerReceiver(receiver, filter, android.content.Context.RECEIVER_NOT_EXPORTED)
            } else {
                application.registerReceiver(receiver, filter)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startTimer() {
        if (_isTimerRunning.value) return
        _isTimerRunning.value = true
        com.example.widget.ProductiveJeansWidgetProvider.sendWidgetUpdate(
            getApplication(),
            true,
            _remainingSeconds.value,
            _selectedCompanion.value
        )
        timerJob = viewModelScope.launch {
            while (_remainingSeconds.value > 0) {
                delay(1000L)
                _remainingSeconds.value -= 1
                com.example.widget.ProductiveJeansWidgetProvider.sendWidgetUpdate(
                    getApplication(),
                    true,
                    _remainingSeconds.value,
                    _selectedCompanion.value
                )
            }
            // Timer Finished
            _isTimerRunning.value = false
            onTimerComplete()
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _isTimerRunning.value = false
        com.example.widget.ProductiveJeansWidgetProvider.sendWidgetUpdate(
            getApplication(),
            false,
            _remainingSeconds.value,
            _selectedCompanion.value
        )
    }

    fun resetTimer() {
        timerJob?.cancel()
        _isTimerRunning.value = false
        val minutes = if (_pomodoroMode.value == PomodoroMode.FOCUS) _customFocusDurationMinutes.value else _customBreakDurationMinutes.value
        _remainingSeconds.value = minutes * 60
        com.example.widget.ProductiveJeansWidgetProvider.sendWidgetUpdate(
            getApplication(),
            false,
            _remainingSeconds.value,
            _selectedCompanion.value
        )
    }

    private fun onTimerComplete() {
        viewModelScope.launch {
            val minutes = if (_pomodoroMode.value == PomodoroMode.FOCUS) _customFocusDurationMinutes.value else _customBreakDurationMinutes.value
            if (_pomodoroMode.value == PomodoroMode.FOCUS) {
                repository.recordFocusSession(minutes, "Focus", _selectedCompanion.value)
                repository.sendChatMessage(
                    sender = "Bunny",
                    text = "🎉 Awesome Call! You completed a $minutes minute focus session with ${_selectedCompanion.value}! Keep shining ✨",
                    isTaskSuggestion = false
                )
                // Switch to Break
                _pomodoroMode.value = PomodoroMode.BREAK
                _remainingSeconds.value = _customBreakDurationMinutes.value * 60
            } else {
                repository.recordFocusSession(minutes, "Break", _selectedCompanion.value)
                repository.sendChatMessage(
                    sender = "Bunny",
                    text = "🌸 Break time is complete! Ready for your next productive Focus Call? 📞",
                    isTaskSuggestion = false
                )
                // Switch to Focus
                _pomodoroMode.value = PomodoroMode.FOCUS
                _remainingSeconds.value = _customFocusDurationMinutes.value * 60
            }
        }
    }

    // --- Chat / Bunny Buddy State ---
    private val _inputChatMessage = MutableStateFlow("")
    val inputChatMessage: StateFlow<String> = _inputChatMessage.asStateFlow()

    fun updateInputChatMessage(msg: String) {
        _inputChatMessage.value = msg
    }

    fun sendUserChatMessage() {
        val msg = _inputChatMessage.value.trim()
        if (msg.isBlank()) return
        _inputChatMessage.value = ""

        viewModelScope.launch {
            val senderName = getEffectiveUserName()
            repository.sendChatMessage(senderName, msg)

            // Gather recent history for AI context
            val history = repository.chatMessages.first().takeLast(6).map { it.sender to it.text }

            // Query Gemini AI
            val aiReply = geminiAiService.generateAiResponse(
                prompt = msg,
                userName = senderName,
                conversationHistory = history
            )

            var isSuggestion = false
            var suggestedTitle: String? = null

            val lower = msg.lowercase()
            if (lower.contains("task") || lower.contains("todo") || lower.contains("remind") || lower.contains("tarea") || lower.contains("recordar")) {
                val extractedTitle = msg.replace(Regex("(?i)(add|remind me to|create task|tarea|recordar|hacer)"), "").trim().ifEmpty { msg }
                isSuggestion = true
                suggestedTitle = extractedTitle
            }

            repository.sendChatMessage("Productive Jeans AI", aiReply, isSuggestion, suggestedTitle)
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChat()
        }
    }

    init {
        // Pre-seed sample tasks if database is empty
        viewModelScope.launch {
            val existingTasks = repository.tasks.first()
            if (existingTasks.isEmpty()) {
                repository.insertTask(TaskEntity(title = "Study Math Chapter 4 📐", note = "Review formulas and complete practice problem set 1-10", category = "Study", priorityStar = true))
                repository.insertTask(TaskEntity(title = "Design Y2K Moodboard 🎨", note = "Collect pastel color swatches & retro stickers", category = "Glow Up", priorityStar = true))
                repository.insertTask(TaskEntity(title = "Hydrate & 10 Min Stretch 💧", note = "Drink 2 glasses of water and do light yoga", category = "Chill", priorityStar = false))
                repository.insertTask(TaskEntity(title = "Finish English Essay ✍️", note = "Draft introduction paragraph and thesis statement", category = "Work", priorityStar = false))
            }

            // Remove all chat messages from database
            repository.clearChat()
        }
    }

    fun requestPinHomeScreenWidget(context: android.content.Context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val appWidgetManager = android.appwidget.AppWidgetManager.getInstance(context)
            val myProvider = android.content.ComponentName(context, com.example.widget.ProductiveJeansWidgetProvider::class.java)
            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                val pinnedWidgetCallbackIntent = android.content.Intent(context, com.example.widget.ProductiveJeansWidgetProvider::class.java)
                val successCallback = android.app.PendingIntent.getBroadcast(
                    context,
                    0,
                    pinnedWidgetCallbackIntent,
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
                )
                appWidgetManager.requestPinAppWidget(myProvider, null, successCallback)
            }
        }
    }
}
