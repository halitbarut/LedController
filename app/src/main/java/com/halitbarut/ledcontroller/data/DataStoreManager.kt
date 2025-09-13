package com.halitbarut.ledcontroller.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit // <<< BU SATIR MUHTEMELEN EKSİK VEYA HATALIYDI
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Jetpack DataStore kullanarak uygulama ayarlarını (Webhook URL'leri, LED İsmi gibi) yönetir.
 * Bu sınıf, verileri cihazın kalıcı depolama alanına güvenli bir şekilde yazar ve okur.
 */
class DataStoreManager(private val context: Context) {

    // DataStore'u context'e bir eklenti (extension) olarak tanımlıyoruz.
    // Bu, uygulama boyunca tek bir DataStore örneği olmasını sağlar ve "settings" adında bir dosya oluşturur.
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    // Anahtar (key) değerlerimizi burada merkezi olarak tanımlıyoruz ki kodun farklı yerlerinde
    // yazım hatası yapma riskini ortadan kaldıralım.
    companion object {
        val WEBHOOK_URL_AC = stringPreferencesKey("webhook_url_ac")
        val WEBHOOK_URL_KAPAT = stringPreferencesKey("webhook_url_kapat")
        val LED_NAME = stringPreferencesKey("led_name")
    }

    /**
     * Verilen tüm ayarları DataStore'a kaydeder.
     * @param urlAc Kaydedilecek "Açma" webhook URL'si.
     * @param urlKapat Kaydedilecek "Kapatma" webhook URL'si.
     * @param ledName Kaydedilecek LED ismi.
     */
    suspend fun saveSettings(urlAc: String, urlKapat: String, ledName: String) {
        context.dataStore.edit { settings ->
            settings[WEBHOOK_URL_AC] = urlAc
            settings[WEBHOOK_URL_KAPAT] = urlKapat
            settings[LED_NAME] = ledName
        }
    }

    /**
     * Kayıtlı "Açma" URL'sini bir Flow olarak döndürür.
     * Flow, veri değiştiğinde dinleyicilere yeni değeri otomatik olarak bildirir.
     */
    val urlAcFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[WEBHOOK_URL_AC] ?: ""
    }

    /**
     * Kayıtlı "Kapatma" URL'sini bir Flow olarak döndürür.
     */
    val urlKapatFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[WEBHOOK_URL_KAPAT] ?: ""
    }

    /**
     * Kayıtlı LED ismini bir Flow olarak döndürür.
     */
    val ledNameFlow: Flow<String> = context.dataStore.data.map { preferences ->
        // Eğer daha önce bir isim kaydedilmemişse, varsayılan bir isim gösteriyoruz.
        preferences[LED_NAME] ?: "Akıllı LED"
    }
}