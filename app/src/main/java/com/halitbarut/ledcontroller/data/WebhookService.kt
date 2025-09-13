package com.halitbarut.ledcontroller.data

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

/**
 * n8n Webhook'larına ağ istekleri göndermekten sorumlu Singleton nesnesi.
 * Ktor kütüphanesini kullanarak asenkron HTTP GET istekleri yapar.
 */
object WebhookService {

    // Ağ isteklerini yönetmek için tek bir HttpClient örneği kullanmak performansı artırır.
    // CIO, coroutine tabanlı basit bir motordur.
    private val client = HttpClient(CIO)

    /**
     * Verilen URL'ye bir HTTP GET isteği gönderir.
     * @param url Tetiklenecek webhook URL'si.
     * @return İstek başarılıysa (HTTP durum kodu 2xx ise) true, aksi takdirde false döner.
     */
    suspend fun triggerWebhook(url: String): Boolean {
        // Eğer URL boş veya sadece boşluklardan oluşuyorsa, gereksiz bir ağ isteği yapmadan doğrudan false döndür.
        if (url.isBlank()) {
            println("Webhook URL'si boş, istek gönderilmedi.")
            return false
        }

        return try {
            println("Webhook tetikleniyor: $url")
            val response: HttpResponse = client.get(url)
            println("Yanıt durumu: ${response.status.value}")

            // HTTP durum kodu 200 ile 299 arasındaysa, işlem başarılı kabul edilir.
            response.status.value in 200..299
        } catch (e: Exception) {
            // Ağ hatası (örn: internet yok) veya başka bir sorun olursa, hatayı logla ve false döndür.
            println("Webhook tetiklenirken hata oluştu: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}