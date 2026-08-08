package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChatMessageEntity
import com.example.ui.PhoningViewModel
import com.example.ui.theme.PhoningAccentHotPink
import com.example.ui.theme.PhoningBlue
import com.example.ui.theme.PhoningLimeBubble
import com.example.ui.theme.PhoningNavyText
import com.example.ui.theme.PhoningPink
import com.example.ui.theme.PhoningYellow

@Composable
fun PhoningChatScreen(
    viewModel: PhoningViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.chatMessages.collectAsState()
    val inputText by viewModel.inputChatMessage.collectAsState()

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val quickPrompts = listOf(
        "Consejos de estudio",
        "Motivación",
        "Recordar tarea",
        "Asistente AI"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        // --- Phoning Messenger Header ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(PhoningPink)
                        .border(1.dp, Color.Black.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👖", fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Productive Jeans AI",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PhoningNavyText
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Asistente de Concentración Gemini • En línea",
                            fontSize = 11.sp,
                            color = PhoningNavyText.copy(alpha = 0.6f)
                        )
                    }
                }

                if (messages.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF0F0F0))
                            .border(1.dp, Color.Black.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .clickable { viewModel.clearChat() }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Limpiar",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Prompts Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(quickPrompts) { prompt ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(PhoningLimeBubble)
                        .border(1.dp, Color.Black.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .clickable {
                            viewModel.updateInputChatMessage(prompt)
                            viewModel.sendUserChatMessage()
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("chat_quick_prompt_$prompt")
                ) {
                    Text(
                        text = prompt,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PhoningNavyText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- Chat Bubble List ---
        if (messages.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay mensajes en el chat.\nEscribe algo abajo para comenzar.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatMessageItem(
                        message = msg,
                        onAddSuggestedTask = { title ->
                            viewModel.addTask(title, "Creado desde el Chat", "Study", true)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- Message Input Box ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { viewModel.updateInputChatMessage(it) },
                placeholder = { Text("Escribe un mensaje para la IA...", fontSize = 13.sp) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_text_input"),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black.copy(alpha = 0.2f)
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PhoningLimeBubble)
                    .border(1.dp, Color.Black, CircleShape)
                    .clickable { viewModel.sendUserChatMessage() }
                    .testTag("send_chat_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send Message",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessageEntity,
    onAddSuggestedTask: (String) -> Unit
) {
    val isUser = message.sender == "User"

    val avatarEmoji = when (message.sender) {
        "하니" -> "🎀"
        "혜인 👁️", "혜인" -> "👁️"
        "해린" -> "🐱"
        "다니엘" -> "🐶"
        "민지" -> "🐻"
        else -> "🐰"
    }

    val memberAvatarBg = when (message.sender) {
        "하니" -> Color(0xFFFFC0CB)
        "혜인 👁️", "혜인" -> Color(0xFFD8B4F8)
        "해린" -> Color(0xFFB8E0FF)
        "다니엘" -> Color(0xFFFFF1A8)
        "민지" -> Color(0xFFC1F0D6)
        else -> PhoningPink
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            // Member Avatar
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(memberAvatarBg)
                    .border(1.dp, Color.Black.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(avatarEmoji, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            // Member Name Above Bubble
            if (!isUser) {
                Text(
                    text = message.sender,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 2.dp, bottom = 2.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
            ) {
                if (isUser) {
                    Text(
                        text = "16:15",
                        fontSize = 10.sp,
                        color = Color.Black.copy(alpha = 0.5f),
                        modifier = Modifier.padding(end = 4.dp, bottom = 2.dp)
                    )
                }

                // Authentic Phoning Bubble Container
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isUser) Color.White else PhoningLimeBubble,
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .border(1.dp, Color.Black, RoundedCornerShape(20.dp))
                        .testTag("chat_bubble_${message.id}")
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                        Text(
                            text = message.text,
                            fontSize = 13.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Normal
                        )

                        // Optional Task Quick Add Action
                        if (message.isTaskSuggestion && message.suggestedTaskTitle != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = PhoningYellow,
                                modifier = Modifier
                                    .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
                                    .clickable {
                                        onAddSuggestedTask(message.suggestedTaskTitle)
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddTask,
                                        contentDescription = "Add Task",
                                        tint = PhoningNavyText,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Add '${message.suggestedTaskTitle}' to Tasks ✨",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PhoningNavyText
                                    )
                                }
                            }
                        }
                    }
                }

                if (!isUser) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                    ) {
                        Text(
                            text = "16:15",
                            fontSize = 10.sp,
                            color = Color.Black.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "文A",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

