package com.halitbarut.ledcontroller.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.halitbarut.ledcontroller.ui.viewmodel.MainViewModel

/**
 * Webhook URL'lerini ve LED ismini girmek ve kaydetmek için kullanılan Ayarlar ekranı.
 *
 * @param viewModel İş mantığını ve veri kaydetme fonksiyonlarını içeren MainViewModel.
 * @param onNavigateBack Geri butonuna veya Kaydet butonuna basıldığında çağrılacak navigasyon fonksiyonu.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // TextField'ların içindeki metni tutmak için kendi yerel durumlarımızı (local states) oluşturuyoruz.
    // `remember` sayesinde bu durumlar, recomposition sırasında sıfırlanmaz.
    // `key1` parametresi sayesinde, eğer ViewModel'deki değer değişirse (örneğin ilk yüklemede),
    // bu yerel durumlar da güncellenir.
    var urlAc by remember(uiState.urlAc) { mutableStateOf(uiState.urlAc) }
    var urlKapat by remember(uiState.urlKapat) { mutableStateOf(uiState.urlKapat) }
    var ledName by remember(uiState.ledName) { mutableStateOf(uiState.ledName) } // YENİ: LED ismi için yerel durum.

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Webhook Ayarları") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Geri",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // YENİ: LED ismi için metin alanı.
            OutlinedTextField(
                value = ledName,
                onValueChange = { ledName = it },
                label = { Text("LED İsmi") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = urlAc,
                onValueChange = { urlAc = it },
                label = { Text("Açma Webhook URL'si") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = urlKapat,
                onValueChange = { urlKapat = it },
                label = { Text("Kapatma Webhook URL'si") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = {
                    // Butona basıldığında, tüm yerel durumlardaki metinleri ViewModel aracılığıyla kaydet.
                    viewModel.saveSettings(urlAc, urlKapat, ledName)
                    // Kaydettikten sonra otomatik olarak bir önceki ekrana dön.
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Kaydet")
            }
        }
    }
}