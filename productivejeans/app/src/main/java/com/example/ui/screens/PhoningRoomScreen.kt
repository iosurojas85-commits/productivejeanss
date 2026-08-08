package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.PhoningViewModel
import com.example.ui.theme.PhoningAccentHotPink
import com.example.ui.theme.PhoningBlue
import com.example.ui.theme.PhoningLilac
import com.example.ui.theme.PhoningMint
import com.example.ui.theme.PhoningNavyText
import com.example.ui.theme.PhoningPink
import com.example.ui.theme.PhoningYellow

data class StickerBadge(
    val id: String,
    val title: String,
    val emoji: String,
    val description: String,
    val minFocusMinutes: Int
)

@Composable
fun PhoningRoomScreen(
    viewModel: PhoningViewModel,
    modifier: Modifier = Modifier
) {
    val totalFocusMins by viewModel.totalFocusMinutes.collectAsState()
    val totalSessions by viewModel.totalSessionsCount.collectAsState()
    val tasks by viewModel.filteredTasks.collectAsState()

    val completedTasksCount = tasks.count { it.isCompleted }

    var selectedTheme by remember { mutableStateOf("Pastel Sky ☁️") }

    val stickerBadges = listOf(
        StickerBadge("1", "First Call 📞", "🐰", "Completed 1st Focus Call", 0),
        StickerBadge("2", "Focus Star ⭐", "🌟", "Reached 25 focus minutes", 25),
        StickerBadge("3", "Study Bunny 📚", "🥕", "Reached 50 focus minutes", 50),
        StickerBadge("4", "Glow Master ✨", "👑", "Reached 100 focus minutes", 100),
        StickerBadge("5", "Phoning Idol 🎀", "💖", "Reached 200 focus minutes", 200),
        StickerBadge("6", "Legendary Focus 🏆", "🔥", "Reached 500 focus minutes", 500)
    )

    val themes = listOf("Pastel Sky ☁️", "Cyber Pink 💖", "Lavender Cloud 💜", "Mint Chill 🌿")

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // --- Room Hero Header ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🎨 MY PHONING ROOM",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PhoningNavyText.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "Productivity Stats & Y2K Wall",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PhoningNavyText
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(PhoningPink)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "🔥 5 Day Streak",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PhoningNavyText
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Productivity Metrics Cards Grid ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Focus Minutes Stat
            StatCard(
                title = "Total Focus",
                value = "${totalFocusMins ?: 0}m",
                icon = Icons.Default.Timer,
                color = PhoningBlue,
                modifier = Modifier.weight(1f)
            )

            // Calls Completed Stat
            StatCard(
                title = "Focus Calls",
                value = "$totalSessions",
                icon = Icons.Default.LocalFireDepartment,
                color = PhoningPink,
                modifier = Modifier.weight(1f)
            )

            // Tasks Completed Stat
            StatCard(
                title = "Done Notes",
                value = "$completedTasksCount",
                icon = Icons.Default.CheckCircle,
                color = PhoningMint,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- Weekly Focus Visualizer ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📊 THIS WEEK'S FOCUS (MINUTES)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PhoningNavyText
                )

                Spacer(modifier = Modifier.height(16.dp))

                val days = listOf("M", "T", "W", "T", "F", "S", "S")
                val sampleHeights = listOf(0.4f, 0.7f, 0.5f, 0.9f, 0.6f, 1f, 0.8f)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Bottom
                ) {
                    days.forEachIndexed { idx, day ->
                        val barHeightFactor = sampleHeights[idx]
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(22.dp)
                                    .height((80 * barHeightFactor).dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (idx == 5) PhoningAccentHotPink else PhoningBlue
                                    )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = day,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PhoningNavyText
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- Y2K Sticker Collection ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🏷️ UNLOCKED STICKERS & BADGES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PhoningNavyText
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(stickerBadges) { badge ->
                        val isUnlocked = (totalFocusMins ?: 0) >= badge.minFocusMinutes

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isUnlocked) PhoningYellow else PhoningNavyText.copy(alpha = 0.05f),
                            modifier = Modifier.testTag("sticker_badge_${badge.id}")
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (isUnlocked) badge.emoji else "🔒",
                                    fontSize = 28.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = badge.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUnlocked) PhoningNavyText else PhoningNavyText.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = if (isUnlocked) "Unlocked!" else "Need ${badge.minFocusMinutes}m",
                                    fontSize = 9.sp,
                                    color = PhoningNavyText.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = PhoningNavyText,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PhoningNavyText
            )
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = PhoningNavyText.copy(alpha = 0.8f)
            )
        }
    }
}
