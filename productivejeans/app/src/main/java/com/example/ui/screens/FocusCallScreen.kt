package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PhoningViewModel
import com.example.ui.PomodoroMode
import com.example.ui.WidgetType
import com.example.ui.theme.PhoningAccentHotPink
import com.example.ui.theme.PhoningBlue
import com.example.ui.theme.PhoningBlueDark
import com.example.ui.theme.PhoningLimeBubble
import com.example.ui.theme.PhoningNavyText
import com.example.ui.theme.PhoningPink
import com.example.ui.theme.PhoningYellow

data class MemberTheme(
    val primaryColor: Color,
    val secondaryColor: Color,
    val accentColor: Color,
    val chipBg: Color,
    val mascotTint: Color,
    val emoji: String,
    val name: String,
    val subtitle: String
)

fun getMemberTheme(companion: String): MemberTheme {
    val lower = companion.lowercase()
    return when {
        lower.contains("hanni") -> MemberTheme(
            primaryColor = Color(0xFFFFB2D6),      // Pink
            secondaryColor = Color(0xFFFFE0EF),
            accentColor = Color(0xFFFF1493),
            chipBg = Color(0xFFFF80BF),
            mascotTint = Color(0xFFFFB2D6),      // Hanni Pink Mascot
            emoji = "🌸",
            name = "Hanni 🌸",
            subtitle = "Pink Blossom Vibe 🎀"
        )
        lower.contains("danielle") || lower.contains("dani") -> MemberTheme(
            primaryColor = Color(0xFFFFF168),      // Yellow
            secondaryColor = Color(0xFFFFFDE0),
            accentColor = Color(0xFFF57C00),
            chipBg = Color(0xFFFFD54F),
            mascotTint = Color(0xFFFFF168),      // Danielle Yellow Mascot
            emoji = "✨",
            name = "Danielle ✨",
            subtitle = "Sunny Yellow Energy 🌟"
        )
        lower.contains("minji") -> MemberTheme(
            primaryColor = Color(0xFFA6E3FF),      // Blue
            secondaryColor = Color(0xFFE1F5FE),
            accentColor = Color(0xFF0288D1),
            chipBg = Color(0xFF81D4FA),
            mascotTint = Color(0xFFA6E3FF),      // Minji Blue Mascot
            emoji = "💙",
            name = "Minji 💙",
            subtitle = "Calm Ocean Blue 🌊"
        )
        lower.contains("haerin") -> MemberTheme(
            primaryColor = Color(0xFFB5FF75),      // Green
            secondaryColor = Color(0xFFF1F8E9),
            accentColor = Color(0xFF2E7D32),
            chipBg = Color(0xFFAED581),
            mascotTint = Color(0xFFB5FF75),      // Haerin Green Mascot
            emoji = "🐱",
            name = "Haerin 🐱",
            subtitle = "Cozy Green Matcha 🍃"
        )
        lower.contains("hyein") -> MemberTheme(
            primaryColor = Color(0xFFE2C2FF),      // Purple
            secondaryColor = Color(0xFFF3E5F5),
            accentColor = Color(0xFF7B1FA2),
            chipBg = Color(0xFFCE93D8),
            mascotTint = Color(0xFFE2C2FF),      // Hyein Purple Mascot
            emoji = "🎀",
            name = "Hyein 🎀",
            subtitle = "Dreamy Violet Lavender 💜"
        )
        else -> MemberTheme(
            primaryColor = Color(0xFFF2F2F7),      // Pure White / Cream
            secondaryColor = Color(0xFFFFFFFF),
            accentColor = Color(0xFF455A64),
            chipBg = Color(0xFFE0E0E0),
            mascotTint = Color.White,            // Bunnies = White Mascot
            emoji = "🐰",
            name = "Bunny 🐰",
            subtitle = "Productive Jeans Mascot 👖"
        )
    }
}

@Composable
fun FocusCallScreen(
    viewModel: PhoningViewModel,
    modifier: Modifier = Modifier
) {
    val remainingSeconds by viewModel.remainingSeconds.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val mode by viewModel.pomodoroMode.collectAsState()
    val companion by viewModel.selectedCompanion.collectAsState()
    val ambientSound by viewModel.ambientSound.collectAsState()
    val focusDuration by viewModel.customFocusDurationMinutes.collectAsState()
    val totalFocusMins by viewModel.totalFocusMinutes.collectAsState()
    val activeWidgets by viewModel.activeWidgets.collectAsState()
    val tasks by viewModel.allTasks.collectAsState()
    val sessionCount by viewModel.totalSessionsCount.collectAsState()
    val selectedMusicPlatform by viewModel.selectedMusicPlatform.collectAsState()
    val selectedPlaylistQuery by viewModel.selectedPlaylistQuery.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    val memberTheme = getMemberTheme(companion)

    val companions = listOf(
        "Hanni 🌸",
        "Danielle ✨",
        "Minji 💙",
        "Haerin 🐱",
        "Hyein 🎀",
        "Bunny 🐰"
    )
    val ambientSounds = listOf("Lofi Rain 🌧️", "Café ☕", "Cat Purr 🐱", "Soft Waves 🌊", "Off 🔇")
    val focusPresets = listOf(15, 25, 45, 60)

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    // Pulse animation for active call
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isTimerRunning) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Dynamic Member Color Animations
    val animatedPrimaryColor by animateColorAsState(
        targetValue = memberTheme.primaryColor,
        animationSpec = tween(600),
        label = "theme_primary"
    )

    val animatedAccentColor by animateColorAsState(
        targetValue = memberTheme.accentColor,
        animationSpec = tween(600),
        label = "theme_accent"
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Header Call Banner with Member Colors ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, Color.Black, RoundedCornerShape(24.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(animatedPrimaryColor, memberTheme.secondaryColor)
                    )
                )
                .padding(16.dp),
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(animatedAccentColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "📞 PRODUCTIVE JEANS CALL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = PhoningNavyText.copy(alpha = 0.8f)
                        )
                    }
                    Text(
                        text = if (mode == PomodoroMode.FOCUS) "Focus Call with ${memberTheme.name}" else "Break Call with ${memberTheme.name}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = PhoningNavyText
                    )
                    Text(
                        text = memberTheme.subtitle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = animatedAccentColor
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Stats",
                        tint = animatedAccentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${totalFocusMins ?: 0} m today",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PhoningNavyText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- Animated Call Circle & Timer themed by Member ---
        Box(
            modifier = Modifier
                .size(220.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(animatedPrimaryColor, memberTheme.secondaryColor)
                    )
                )
                .border(5.dp, Color.Black, CircleShape)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = formattedTime,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    color = PhoningNavyText,
                    modifier = Modifier.testTag("pomodoro_timer_display")
                )

                // Call Status Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isTimerRunning) PhoningLimeBubble else Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = if (isTimerRunning) "● IN CALL WITH ${memberTheme.name.uppercase()}" else "READY TO CALL",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- Call Action Controls ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reset Button
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.5.dp, Color.Black, CircleShape)
                    .clickable { viewModel.resetTimer() }
                    .testTag("pomodoro_reset_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    tint = PhoningNavyText,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Main Answer / Call Toggle Button
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(
                        if (isTimerRunning) Color(0xFFFF5252) else animatedPrimaryColor
                    )
                    .border(2.5.dp, Color.Black, CircleShape)
                    .clickable {
                        if (isTimerRunning) viewModel.pauseTimer() else viewModel.startTimer()
                    }
                    .testTag("pomodoro_start_pause_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.Call,
                    contentDescription = if (isTimerRunning) "Pause" else "Start Call",
                    tint = Color.Black,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Ambient Sound Toggle
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(memberTheme.chipBg)
                    .border(1.5.dp, Color.Black, CircleShape)
                    .clickable {
                        val nextIndex = (ambientSounds.indexOf(ambientSound) + 1) % ambientSounds.size
                        viewModel.setAmbientSound(ambientSounds[nextIndex])
                    }
                    .testTag("ambient_sound_toggle"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Ambient Sound",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Companion Selector (Hanni, Danielle, Minji, Haerin, Hyein) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, Color.Black, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "🌸 CHOOSE CALL COMPANION (MEMBER THEME)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = PhoningNavyText
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(companions) { comp ->
                        val isSelected = companion.contains(comp.take(5), ignoreCase = true) || companion == comp
                        val targetTheme = getMemberTheme(comp)

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) targetTheme.primaryColor else Color(0xFFF5F5F5))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color.Black else Color.Gray.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { viewModel.setCompanion(comp) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("companion_chip_$comp")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                com.example.ui.components.BunnyMascotIcon(
                                    color = targetTheme.mascotTint,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = comp,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Duration Presets & Ambient Sound Cards ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(1.5.dp, Color.Black, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "⏱️ FOCUS MINUTES",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = PhoningNavyText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        focusPresets.forEach { dur ->
                            val isSelected = focusDuration == dur
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) animatedPrimaryColor else Color(0xFFF0F0F0))
                                    .border(if (isSelected) 1.dp else 0.dp, Color.Black, RoundedCornerShape(10.dp))
                                    .clickable { viewModel.setFocusDuration(dur) }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${dur}m",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(1.5.dp, Color.Black, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "🎧 AMBIENT AUDIO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = PhoningNavyText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = ambientSound,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = animatedAccentColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

