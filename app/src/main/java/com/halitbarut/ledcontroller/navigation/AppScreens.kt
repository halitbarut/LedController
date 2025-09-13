package com.halitbarut.ledcontroller.navigation

/**
 * Uygulamadaki tüm ekranların rotalarını (adreslerini) merkezi ve "type-safe" bir şekilde tanımlar.
 *
 * 'sealed class' kullanmanın avantajı, olası tüm ekran rotalarını derleme zamanında bilmemizi sağlamasıdır.
 * Bu, 'when' ifadelerinde 'else' bloğu gerektirmeyen ve yazım hatalarını tamamen ortadan kaldıran
 * daha güvenli bir yapı oluşturur. String'leri ("main_screen") doğrudan kullanmak yerine bu nesneleri
 * kullanmak, kodun okunabilirliğini ve bakımını kolaylaştırır.
 */
sealed class AppScreens(val route: String) {
    /**
     * Ana ekranı temsil eden nesne.
     */
    object MainScreen : AppScreens("main_screen")

    /**
     * Ayarlar ekranını temsil eden nesne.
     */
    object SettingsScreen : AppScreens("settings_screen")
}