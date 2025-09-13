package com.halitbarut.ledcontroller.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.halitbarut.ledcontroller.ui.screens.main.MainScreen
import com.halitbarut.ledcontroller.ui.screens.settings.SettingsScreen
import com.halitbarut.ledcontroller.ui.viewmodel.MainViewModel

/**
 * Uygulamanın navigasyon grafiğini yöneten ana Composable.
 * Jetpack Navigation Compose kütüphanesini kullanarak ekranlar arasındaki geçişleri tanımlar.
 *
 * @param viewModel Tüm ekranların paylaşacağı ve iş mantığını içeren MainViewModel örneği.
 */
@Composable
fun AppNavigation(viewModel: MainViewModel) {
    // NavController, ekranlar arası geçişleri tetikleyen (navigate), geri tuşu yığınını (back stack)
    // yöneten ve mevcut ekranın durumunu takip eden merkezi bir objedir.
    // `rememberNavController` sayesinde bu state, recomposition'lar arasında korunur.
    val navController = rememberNavController()

    // NavHost, navigasyon grafiğindeki mevcut hedefe (route) göre doğru Composable'ı gösteren bir konteynerdir.
    // `startDestination` parametresi, uygulama ilk açıldığında hangi ekranın gösterileceğini belirtir.
    NavHost(navController = navController, startDestination = AppScreens.MainScreen.route) {

        /**
         * Ana Ekran (`main_screen`) için rota tanımı.
         * Bu blok, `navController.navigate("main_screen")` çağrıldığında çalıştırılır.
         */
        composable(route = AppScreens.MainScreen.route) {
            MainScreen(
                viewModel = viewModel,
                // Ana ekrandaki ayarlar butonuna tıklandığında ne olacağını tanımlıyoruz.
                // Bu lambda fonksiyonu, NavController'ı kullanarak ayarlar ekranına geçişi tetikler.
                onSettingsClicked = {
                    navController.navigate(AppScreens.SettingsScreen.route)
                }
            )
        }

        /**
         * Ayarlar Ekranı (`settings_screen`) için rota tanımı.
         */
        composable(route = AppScreens.SettingsScreen.route) {
            SettingsScreen(
                viewModel = viewModel,
                // Ayarlar ekranındaki geri tuşuna veya kaydet butonuna basıldığında ne olacağını tanımlıyoruz.
                // `popBackStack`, navigasyon yığınındaki mevcut ekranı kaldırır ve bir öncekine döner.
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}