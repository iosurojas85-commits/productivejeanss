package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Grid3x3
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Grid3x3
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PhoningAccentHotPink
import com.example.ui.theme.PhoningLimeBubble
import com.example.ui.theme.PhoningNavyText
import com.example.ui.theme.PhoningPink

data class PhoningTabItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun BottomPhoningBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        PhoningTabItem("Call", Icons.Filled.Call, Icons.Outlined.Call, "nav_tab_call"),
        PhoningTabItem("Notes", Icons.Filled.EditNote, Icons.Outlined.EditNote, "nav_tab_notes"),
        PhoningTabItem("Chat", Icons.Filled.ChatBubble, Icons.Outlined.ChatBubbleOutline, "nav_tab_chat"),
        PhoningTabItem("Room", Icons.Filled.Grid3x3, Icons.Outlined.Grid3x3, "nav_tab_room")
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(12.dp, RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp)),
        color = Color.White.copy(alpha = 0.95f),
        tonalElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = selectedTab == index

                val containerBg by animateColorAsState(
                    targetValue = if (isSelected) PhoningLimeBubble else Color.Transparent,
                    animationSpec = spring(),
                    label = "tab_bg"
                )

                val iconTint by animateColorAsState(
                    targetValue = if (isSelected) Color.Black else PhoningNavyText.copy(alpha = 0.6f),
                    label = "tab_icon"
                )

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(containerBg)
                        .then(
                            if (isSelected) Modifier.border(1.dp, Color.Black, CircleShape) else Modifier
                        )
                        .clickable { onTabSelected(index) }
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag(tab.testTag),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                            contentDescription = tab.title,
                            tint = iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = tab.title,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (isSelected) PhoningNavyText else PhoningNavyText.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}
