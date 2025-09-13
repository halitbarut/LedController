package com.halitbarut.ledcontroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.halitbarut.ledcontroller.navigation.AppNavigation
import com.halitbarut.ledcontroller.ui.theme.LedControllerTheme
import com.halitbarut.ledcontroller.ui.viewmodel.MainViewModel

/**
 * Uygulamanın ana giriş noktası olan Activity.
 * Bu Activity, Jetpack Compose ile oluşturulan arayüzü barındırır.
 */
class MainActivity : ComponentActivity() {

    // `by viewModels()` delege'si, Activity veya Fragment yaşam döngüsüne uygun olarak
    // ViewModel'in doğru şekilde oluşturulmasını ve saklanmasını sağlayan bir KTX (Kotlin Extension) özelliğidir.
    // Ekran döndürme gibi konfigürasyon değişikliklerinde ViewModel'in kaybolmasını önler ve
    // tüm Composable'ların aynı ViewModel örneğine erişmesini garanti eder.
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // `setContent`, bu Activity'nin arayüzünün XML layout'lar yerine
        // Jetpack Compose ile oluşturulacağını belirtir.
        setContent {
            // LedControllerTheme, uygulamanın renklerini, yazı tiplerini ve şekillerini tanımlayan
            // kendi özel temamızdır. (ui/theme paketinde bulunur)
            LedControllerTheme {
                // Surface, uygulamanın arka plan rengini ve diğer yüzey özelliklerini ayarlar.
                // Genellikle en dış katman olarak kullanılır.
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Uygulamanın navigasyon grafiğini (ve dolayısıyla tüm ekranlarını)
                    // oluşturduğumuz ViewModel ile birlikte başlatıyoruz.
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}