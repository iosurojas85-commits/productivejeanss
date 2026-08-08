package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TaskEntity
import com.example.ui.PhoningViewModel
import com.example.ui.PomodoroMode
import com.example.ui.ScreenWidget
import com.example.ui.WidgetType
import com.example.ui.theme.PhoningAccentHotPink
import com.example.ui.theme.PhoningBlue
import com.example.ui.theme.PhoningLimeBubble
import com.example.ui.theme.PhoningNavyText
import com.example.ui.theme.PhoningPink
import com.example.ui.theme.PhoningYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenWidgetsScreen(
    viewModel: PhoningViewModel,
    modifier: Modifier = Modifier
) {
    val activeWidgets by viewModel.activeWidgets.collectAsState()
    var showAddWidgetSheet by remember { mutableStateOf(false) }

    val remainingSeconds by viewModel.remainingSeconds.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val pomodoroMode by viewModel.pomodoroMode.collectAsState()
    val tasks by viewModel.allTasks.collectAsState()
    val totalMinutes by viewModel.totalFocusMinutes.collectAsState()
    val sessionCount by viewModel.totalSessionsCount.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    val context = androidx.compose.ui.platform.LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("screen_widgets_screen")
    ) {
        // Desktop / Widgets Header Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(PhoningYellow)
                            .border(1.dp, Color.Black, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Widgets,
                            contentDescription = "Widgets",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Widgets 🧩",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = PhoningNavyText
                        )
                        Text(
                            text = "Home screen & in-app widgets",
                            fontSize = 11.sp,
                            color = PhoningNavyText.copy(alpha = 0.6f)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Pin to System Home Screen Button
                    Button(
                        onClick = { viewModel.requestPinHomeScreenWidget(context) },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PhoningYellow),
                        modifier = Modifier
                            .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
                            .testTag("pin_home_screen_widget_button")
                    ) {
                        Text(
                            text = "Añadir a Inicio 📲",
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Add In-App Widget Button
                    Button(
                        onClick = { showAddWidgetSheet = true },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PhoningLimeBubble),
                        modifier = Modifier
                            .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
                            .testTag("add_widget_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (activeWidgets.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🧩", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No widgets on screen!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PhoningNavyText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap '+ Widget' above to add a Pomodoro timer, AI Coach, or Task pad!",
                        fontSize = 12.sp,
                        color = PhoningNavyText.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(activeWidgets, key = { it.id }) { widget ->
                    WidgetContainer(
                        widget = widget,
                        onRemove = { viewModel.removeWidget(widget.id) }
                    ) {
                        when (widget.type) {
                            WidgetType.POMODORO_TIMER -> {
                                PomodoroWidgetContent(
                                    remainingSeconds = remainingSeconds,
                                    isRunning = isTimerRunning,
                                    mode = pomodoroMode,
                                    onPlay = { viewModel.startTimer() },
                                    onPause = { viewModel.pauseTimer() },
                                    onReset = { viewModel.resetTimer() }
                                )
                            }
                            WidgetType.QUICK_TASK_PAD -> {
                                QuickTaskPadWidgetContent(
                                    tasks = tasks,
                                    onToggleTask = { viewModel.toggleTask(it) },
                                    onAddTask = { title ->
                                        viewModel.addTask(title = title, note = "", category = "Study", priorityStar = true)
                                    }
                                )
                            }
                            WidgetType.AI_COACH_TIP -> {
                                AiCoachTipWidgetContent(viewModel = viewModel)
                            }
                            WidgetType.FOCUS_STREAK -> {
                                val minutesVal = totalMinutes ?: 0
                                val sessionsVal = sessionCount ?: 0
                                FocusStreakWidgetContent(
                                    totalMinutes = minutesVal,
                                    sessionCount = sessionsVal,
                                    streakDays = if (minutesVal > 0) 3 else 1
                                )
                            }
                            WidgetType.LOFI_RADIO -> {
                                LofiRadioWidgetContent(viewModel = viewModel)
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Modal Sheet to Add Widgets
    if (showAddWidgetSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddWidgetSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Add Widgets to Screen 🧩",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = PhoningNavyText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Select widgets to pin to your active desktop canvas:",
                    fontSize = 12.sp,
                    color = PhoningNavyText.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                val availableOptions = listOf(
                    WidgetType.POMODORO_TIMER to ("Pomodoro Call Timer 📞" to "Interactive timer with Play & Pause buttons"),
                    WidgetType.QUICK_TASK_PAD to ("Quick Task Pad 📝" to "View uncompleted study tasks and add new ones"),
                    WidgetType.AI_COACH_TIP to ("Productive Jeans AI Coach 👖" to "Gemini AI focus quotes & custom study tips"),
                    WidgetType.FOCUS_STREAK to ("Focus Streak & Stats 🔥" to "Track focus session minutes and streak badges"),
                    WidgetType.LOFI_RADIO to ("Cozy Lo-Fi Radio 🎵" to "Ambient study music player widget")
                )

                availableOptions.forEach { (type, info) ->
                    val isAlreadyAdded = activeWidgets.any { it.type == type }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable(enabled = !isAlreadyAdded) {
                                viewModel.addWidget(type)
                                showAddWidgetSheet = false
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isAlreadyAdded) Color(0xFFF0F0F0) else PhoningPink.copy(alpha = 0.3f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = info.first,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PhoningNavyText
                                )
                                Text(
                                    text = info.second,
                                    fontSize = 11.sp,
                                    color = PhoningNavyText.copy(alpha = 0.7f)
                                )
                            }

                            if (isAlreadyAdded) {
                                Text(
                                    text = "Added ✓",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(PhoningLimeBubble)
                                        .border(1.dp, Color.Black, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "Add",
                                        tint = Color.Black,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun WidgetContainer(
    widget: ScreenWidget,
    onRemove: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, Color.Black, RoundedCornerShape(20.dp))
            .testTag("widget_item_${widget.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Widget Title Header with X Delete Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(PhoningAccentHotPink)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = widget.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PhoningNavyText
                    )
                }

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFEBEE))
                        .border(1.dp, Color.Black.copy(alpha = 0.3f), CircleShape)
                        .clickable { onRemove() }
                        .testTag("remove_widget_${widget.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Remove Widget",
                        tint = Color.Red,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            content()
        }
    }
}

// 1. Interactive Pomodoro Widget with Play & Pause Buttons
@Composable
fun PomodoroWidgetContent(
    remainingSeconds: Int,
    isRunning: Boolean,
    mode: PomodoroMode,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit
) {
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    val modeTitle = if (mode == PomodoroMode.FOCUS) "Focus Mode 🎯" else "Break Rest 🌸"
    val modeBg = if (mode == PomodoroMode.FOCUS) PhoningYellow else PhoningPink

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = modeBg.copy(alpha = 0.4f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = modeTitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PhoningNavyText
                )

                // Status Pill
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isRunning) PhoningLimeBubble else Color.LightGray)
                        .border(1.dp, Color.Black, CircleShape)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isRunning) "● RUNNING" else "PAUSED",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Large Digital Countdown
            Text(
                text = timeFormatted,
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = PhoningNavyText
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Play / Pause / Reset Control Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isRunning) {
                    // PAUSE BUTTON
                    Button(
                        onClick = onPause,
                        colors = ButtonDefaults.buttonColors(containerColor = PhoningPink),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .border(1.dp, Color.Black, RoundedCornerShape(14.dp))
                            .testTag("pomodoro_widget_pause")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Pause,
                            contentDescription = "Pause",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PAUSE ⏸️", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                } else {
                    // PLAY BUTTON
                    Button(
                        onClick = onPlay,
                        colors = ButtonDefaults.buttonColors(containerColor = PhoningLimeBubble),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .border(1.dp, Color.Black, RoundedCornerShape(14.dp))
                            .testTag("pomodoro_widget_play")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PLAY ▶️", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                // RESET BUTTON
                Button(
                    onClick = onReset,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .border(1.dp, Color.Black, RoundedCornerShape(14.dp))
                        .testTag("pomodoro_widget_reset")
                ) {
                    Icon(
                        imageVector = Icons.Filled.RestartAlt,
                        contentDescription = "Reset",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset 🔄", color = Color.Black, fontSize = 12.sp)
                }
            }
        }
    }
}

// 2. Quick Task Pad Widget Content
@Composable
fun QuickTaskPadWidgetContent(
    tasks: List<TaskEntity>,
    onToggleTask: (TaskEntity) -> Unit,
    onAddTask: (String) -> Unit
) {
    var quickTaskTitle by remember { mutableStateOf("") }
    val uncompletedTasks = tasks.filter { !it.isCompleted }.take(3)

    Column(modifier = Modifier.fillMaxWidth()) {
        // Quick input row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = quickTaskTitle,
                onValueChange = { quickTaskTitle = it },
                placeholder = { Text("Quick add study task...", fontSize = 11.sp) },
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Gray
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(6.dp))

            Button(
                onClick = {
                    if (quickTaskTitle.isNotBlank()) {
                        onAddTask(quickTaskTitle)
                        quickTaskTitle = ""
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PhoningYellow),
                modifier = Modifier
                    .height(46.dp)
                    .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
            ) {
                Text("+ Task", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (uncompletedTasks.isEmpty()) {
            Text(
                text = "All study pads cleared! ✨ Great job!",
                fontSize = 11.sp,
                color = PhoningNavyText.copy(alpha = 0.6f),
                modifier = Modifier.padding(vertical = 6.dp)
            )
        } else {
            uncompletedTasks.forEach { task ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = { onToggleTask(task) },
                        colors = CheckboxDefaults.colors(checkedColor = PhoningAccentHotPink)
                    )
                    Text(
                        text = task.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PhoningNavyText,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// 3. Productive Jeans AI Coach Tip Widget Content
@Composable
fun AiCoachTipWidgetContent(
    viewModel: PhoningViewModel
) {
    var aiTip by remember { mutableStateOf("👖 'Focus on progress, not perfection. Step by step you build your dreams!'") }
    var isGenerating by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PhoningBlue.copy(alpha = 0.3f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gemini AI Daily Focus Quote 💡",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PhoningNavyText
                )

                IconButton(
                    onClick = {
                        isGenerating = true
                        viewModel.sendUserChatMessage()
                        aiTip = "👖 'Remember: Take short 5m breaks between focus calls to recharge your mind!' ✨"
                        isGenerating = false
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Refresh Tip",
                        tint = PhoningNavyText,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = aiTip,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = PhoningNavyText,
                lineHeight = 16.sp
            )
        }
    }
}

// 4. Focus Streak Widget Content
@Composable
fun FocusStreakWidgetContent(
    totalMinutes: Int,
    sessionCount: Int,
    streakDays: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🔥", fontSize = 20.sp)
            Text("$streakDays Days", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PhoningNavyText)
            Text("Streak", fontSize = 10.sp, color = Color.Gray)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⏱️", fontSize = 20.sp)
            Text("$totalMinutes Min", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PhoningNavyText)
            Text("Focused", fontSize = 10.sp, color = Color.Gray)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📞", fontSize = 20.sp)
            Text("$sessionCount Calls", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PhoningNavyText)
            Text("Completed", fontSize = 10.sp, color = Color.Gray)
        }
    }
}

// 5. Lo-Fi Radio Widget Content
@Composable
fun LofiRadioWidgetContent(
    viewModel: PhoningViewModel
) {
    val sound by viewModel.ambientSound.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Text("Musica y Beats de Estudio", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PhoningNavyText)
        Text("Sonido: $sound", fontSize = 10.sp, color = PhoningNavyText.copy(alpha = 0.7f))

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                    .clickable { viewModel.openSpotify(context) }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Spotify", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                    .clickable { viewModel.openAppleMusic(context) }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Apple Music", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                    .clickable { viewModel.openYouTubeMusic(context) }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("YouTube Music", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
