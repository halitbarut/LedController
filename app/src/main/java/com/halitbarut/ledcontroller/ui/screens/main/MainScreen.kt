package com.halitbarut.ledcontroller.ui.screens.main

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Highlight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.halitbarut.ledcontroller.ui.viewmodel.MainViewModel

/**
 * Uygulamanın ana ekranını oluşturan Composable fonksiyon.
 * Kullanıcının LED'i açıp kapatabileceği kontrol panelini içerir.
 *
 * @param viewModel Arayüz durumunu (UiState) ve iş mantığını sağlayan MainViewModel.
 * @param onSettingsClicked Ayarlar ikonuna tıklandığında çağrılacak olan navigasyon fonksiyonu.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onSettingsClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = uiState.message) {
        uiState.message?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.messageShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LED Kontrol Paneli") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                actions = {
                    IconButton(onClick = onSettingsClicked) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Ayarlar")
                    }
                }
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            ActionCard(
                ledName = uiState.ledName,
                isLoading = uiState.isLoading,
                onTurnOn = { viewModel.onTurnOnClicked() },
                onTurnOff = { viewModel.onTurnOffClicked() }
            )
        }
    }
}

/**
 * Ana ekrandaki kontrol kartını içeren, yeniden kullanılabilir özel bir Composable.
 * `private` olarak işaretlenmiştir çünkü sadece MainScreen içinde kullanılır.
 */
@Composable
private fun ActionCard(
    ledName: String,
    isLoading: Boolean,
    onTurnOn: () -> Unit,
    onTurnOff: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = ledName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    strokeWidth = 4.dp
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ActionButton(
                        icon = Icons.Outlined.Highlight,
                        text = "AÇ",
                        onClick = onTurnOn,
                        color = Color(0xFFFBC02D)
                    )
                    ActionButton(
                        icon = Icons.Default.PowerSettingsNew,
                        text = "KAPAT",
                        onClick = onTurnOff,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/**
 * "AÇ" ve "KAPAT" butonlarını oluşturan, daha da küçük, özel bir Composable.
 */
@Composable
private fun ActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        // --- DÜZELTME BURADA ---
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.size(80.dp),
            colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.1f)),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(40.dp),
                tint = color
            )
        }
        Text(text = text, fontWeight = FontWeight.SemiBold)
    }
}