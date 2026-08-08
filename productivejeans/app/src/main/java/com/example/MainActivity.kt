package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.PhoningViewModel
import com.example.ui.components.BottomPhoningBar
import com.example.ui.components.TopPhoningStatusBar
import com.example.ui.screens.FocusCallScreen
import com.example.ui.screens.PhoningChatScreen
import com.example.ui.screens.PhoningRoomScreen
import com.example.ui.screens.ScreenWidgetsScreen
import com.example.ui.screens.TodoListScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PhoningLimeBubble

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                PhoningFocusApp()
            }
        }
    }
}

@Composable
fun PhoningFocusApp(
    viewModel: PhoningViewModel = viewModel()
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val selectedCompanion by viewModel.selectedCompanion.collectAsState()
    var showOptionsPage by remember { mutableStateOf(false) }

    val memberTheme = com.example.ui.screens.getMemberTheme(selectedCompanion)

    val animatedTopColor by androidx.compose.animation.animateColorAsState(
        targetValue = memberTheme.primaryColor,
        animationSpec = androidx.compose.animation.core.tween(700),
        label = "bg_top_color"
    )

    val animatedBottomColor by androidx.compose.animation.animateColorAsState(
        targetValue = memberTheme.secondaryColor,
        animationSpec = androidx.compose.animation.core.tween(700),
        label = "bg_bottom_color"
    )

    // Dynamic Phoning Gradient Background matching member theme
    val phoningGradient = Brush.verticalGradient(
        colors = listOf(
            animatedTopColor,
            Color.White.copy(alpha = 0.95f),
            animatedBottomColor
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(phoningGradient)
    ) {
        if (showOptionsPage) {
            OptionsScreen(
                viewModel = viewModel,
                onClose = { showOptionsPage = false }
            )
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    TopPhoningStatusBar(
                        onOptionsClick = { showOptionsPage = true }
                    )
                },
                bottomBar = {
                    BottomPhoningBar(
                        selectedTab = selectedTab,
                        onTabSelected = { viewModel.selectTab(it) }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Crossfade(
                        targetState = selectedTab,
                        label = "tab_crossfade"
                    ) { tab ->
                        when (tab) {
                            0 -> FocusCallScreen(viewModel)
                            1 -> TodoListScreen(viewModel)
                            2 -> PhoningChatScreen(viewModel)
                            3 -> PhoningRoomScreen(viewModel)
                            else -> FocusCallScreen(viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OptionsScreen(
    viewModel: PhoningViewModel,
    onClose: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val currentUserName by viewModel.userName.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(28.dp)) // Top status inset spacing

        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Ajustes",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
                Text(
                    text = "Configuración de usuario y música",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.border(1.5.dp, Color.Black, RoundedCornerShape(12.dp))
            ) {
                Text("Volver", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, Color.Black, RoundedCornerShape(16.dp))
                .background(Color(0xFFFAFAFA), RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // --- USERNAME ---
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "NOMBRE DE USUARIO",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
                Text(
                    text = "Si no escribes un nombre, la IA te identificará como bunnie.",
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )
                OutlinedTextField(
                    value = currentUserName,
                    onValueChange = { viewModel.setUserName(it) },
                    placeholder = { Text("bunnie", fontSize = 13.sp, color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Black, RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black
                    )
                )
                Text(
                    text = if (currentUserName.isBlank()) "Identificación actual: bunnie" else "Identificación actual: $currentUserName",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // --- MUSIC SYNC ---
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "SINCRONIZAR APP DE MÚSICA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
                Text(
                    text = "Selecciona la aplicación para vincularte:",
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )

                // Spotify
                Button(
                    onClick = { viewModel.openSpotify(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Black, RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text("Spotify", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Sincronizar", color = Color.DarkGray, fontSize = 12.sp)
                    }
                }

                // Apple Music
                Button(
                    onClick = { viewModel.openAppleMusic(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Black, RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text("Apple Music", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Sincronizar", color = Color.DarkGray, fontSize = 12.sp)
                    }
                }

                // YouTube Music
                Button(
                    onClick = { viewModel.openYouTubeMusic(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Black, RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text("YouTube Music", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Sincronizar", color = Color.DarkGray, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}


