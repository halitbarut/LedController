package com.halitbarut.ledcontroller.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.halitbarut.ledcontroller.data.DataStoreManager
import com.halitbarut.ledcontroller.data.WebhookService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Arayüzün (UI) o anki durumunu temsil eden, değişmez (immutable) bir veri sınıfı.
 */
data class UiState(
    val urlAc: String = "",
    val urlKapat: String = "",
    val isLoading: Boolean = false,
    val message: String? = null,
    val ledName: String = "" // YENİ: LED'in mevcut ismini tutar
)

/**
 * MainActivity ve altındaki Composable'lar için iş mantığını yöneten ViewModel.
 * Arayüz ile veri katmanı arasında bir köprü görevi görür.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = DataStoreManager(application)
    private val webhookService = WebhookService

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        // ViewModel oluşturulduğunda, DataStore'daki tüm ayar değişikliklerini dinlemeye başlar.
        viewModelScope.launch {
            dataStore.urlAcFlow.collectLatest { url ->
                _uiState.update { currentState -> currentState.copy(urlAc = url) }
            }
        }
        viewModelScope.launch {
            dataStore.urlKapatFlow.collectLatest { url ->
                _uiState.update { currentState -> currentState.copy(urlKapat = url) }
            }
        }
        // YENİ: DataStore'dan LED ismini dinlemeye başlıyoruz.
        viewModelScope.launch {
            dataStore.ledNameFlow.collectLatest { name ->
                _uiState.update { currentState -> currentState.copy(ledName = name) }
            }
        }
    }

    /** Arayüzden "AÇ" butonuna tıklandığında bu fonksiyon çağrılır. */
    fun onTurnOnClicked() {
        triggerWebhook(uiState.value.urlAc, "LED açma komutu gönderiliyor...")
    }

    /** Arayüzden "KAPAT" butonuna tıklandığında bu fonksiyon çağrılır. */
    fun onTurnOffClicked() {
        triggerWebhook(uiState.value.urlKapat, "LED kapatma komutu gönderiliyor...")
    }

    private fun triggerWebhook(url: String, loadingMessage: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = loadingMessage) }
            val success = webhookService.triggerWebhook(url)
            val resultMessage = if (success) "Komut başarıyla gönderildi!" else "Hata: URL'yi veya bağlantıyı kontrol edin."
            _uiState.update { it.copy(isLoading = false, message = resultMessage) }
        }
    }

    /** Ayarlar ekranından tüm ayarları kaydetmek için çağrılır. */
    fun saveSettings(urlAc: String, urlKapat: String, ledName: String) {
        viewModelScope.launch {
            dataStore.saveSettings(urlAc, urlKapat, ledName)
            _uiState.update { it.copy(message = "Ayarlar kaydedildi!") }
        }
    }

    /**
     * Arayüzde bir Toast mesajı gösterildikten sonra, state'deki mesajı temizler.
     */
    fun messageShown() {
        _uiState.update { it.copy(message = null) }
    }
}