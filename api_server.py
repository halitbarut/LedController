# -*- coding: utf-8 -*-
"""
Bu Flask sunucusu, bir Bluetooth Low Energy (BLE) şerit LED'i web üzerinden kontrol etmek için bir API sağlar.
Gelen HTTP GET isteklerini BLE komutlarına çevirir ve hedef cihaza gönderir.
n8n gibi otomasyon araçlarıyla veya doğrudan HTTP istekleri ile kullanılmak üzere tasarlanmıştır.

systemd ile bir servis olarak çalıştırılması tavsiye edilir.
"""

import asyncio
from flask import Flask, jsonify
from bleak import BleakClient

# --- YAPILANDIRMA (CONFIGURATION) ---
# Lütfen bu bölümü kendi donanımınıza göre güncelleyin.
# ---------------------------------------------------
LED_MAC_ADRESI = "XX:XX:XX:XX:XX:XX"  # Kontrol edilecek BLE şerit LED'in MAC adresi.
YAZMA_UUID = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"  # Komutların yazılacağı Characteristic UUID'si.

# Bu komutlar, kullanılan LED kontrolcüsünün modeline göre değişiklik gösterir.
# Kendi cihazınız için doğru komut setini bulmanız gerekebilir.
KOMUT_AC = bytearray.fromhex("7e0004f00001ff00ef")
KOMUT_KAPAT = bytearray.fromhex("7e0004000000ff00ef")
KOMUT_PARLAKLIK_100 = bytearray.fromhex("7e0001ff00000000ef")

# --- FLASK UYGULAMASI ---
# ------------------------
app = Flask(__name__)

async def run_ble_command(commands: list[bytearray]):
    """
    Verilen komut listesini asenkron olarak BLE cihazına bağlanıp gönderir.

    Args:
        commands: Gönderilecek bytearray komutlarının bir listesi.

    Returns:
        İşlem başarılıysa True, aksi takdirde False.
    """
    app.logger.info(f"Cihaza bağlanılıyor: {LED_MAC_ADRESI}")
    try:
        # `async with` bloğu, bağlantının otomatik olarak yönetilmesini ve kapatılmasını sağlar.
        async with BleakClient(LED_MAC_ADRESI, timeout=10.0) as client:
            if not client.is_connected:
                app.logger.error("Hata: Cihaza bağlanılamadı.")
                return False

            # Birden fazla komut varsa, aralarında kısa bir bekleme süresi bırakarak sırayla gönder.
            for i, command in enumerate(commands):
                app.logger.info(f"-> Komut {i+1}/{len(commands)} gönderiliyor: {command.hex()}")
                await client.write_gatt_char(YAZMA_UUID, command)
                if i < len(commands) - 1:
                    await asyncio.sleep(0.2)
            
            app.logger.info("Tüm komutlar başarıyla gönderildi.")
            return True
    except Exception as e:
        app.logger.error(f"Bir BLE hatası oluştu: {e}")
        return False

def handle_request(commands: list[bytearray], success_message: str):
    """
    Gelen bir isteği işler, BLE komutunu çalıştırır ve uygun JSON yanıtını döndürür.
    Bu yardımcı fonksiyon, API endpoint'lerindeki kod tekrarını önler.
    """
    # Not: asyncio.run(), her istek için yeni bir event loop başlatır. Bu, yüksek trafikli
    # uygulamalar için verimsiz olabilir, ancak bu projenin seyrek kullanım senaryosu için
    # basitliği nedeniyle kabul edilebilir bir çözümdür.
    success = asyncio.run(run_ble_command(commands))
    if success:
        return jsonify({"status": "success", "message": success_message})
    else:
        # 500 Internal Server Error, sunucu tarafında bir sorun olduğunu belirtir.
        return jsonify({"status": "error", "message": "LED kontrol edilemedi. Cihazın açık ve menzilde olduğundan emin olun."}), 500

# --- API ENDPOINTS ---
# ---------------------

@app.route('/', methods=['GET'])
def health_check():
    """
    Sunucunun çalışıp çalışmadığını kontrol etmek için basit bir sağlık kontrolü endpoint'i.
    """
    return jsonify({"status": "ok", "message": "LED API sunucusu çalışıyor."})

@app.route('/ac', methods=['GET'])
def turn_on():
    """
    LED'i açar ve parlaklığı %100'e ayarlar.
    """
    app.logger.info("'/ac' isteği alındı.")
    commands_to_run = [KOMUT_AC, KOMUT_PARLAKLIK_100]
    return handle_request(commands_to_run, "LED açıldı ve parlaklık ayarlandı.")

@app.route('/kapat', methods=['GET'])
def turn_off():
    """
    LED'i kapatır.
    """
    app.logger.info("'/kapat' isteği alındı.")
    return handle_request([KOMUT_KAPAT], "LED kapatıldı.")

# --- SUNUCUYU BAŞLATMA ---
# -------------------------
if __name__ == '__main__':
    # 'host="0.0.0.0"' sunucunun tüm ağ arayüzlerini dinlemesini sağlar.
    # Bu, Docker konteynerlerinden veya yerel ağdaki diğer cihazlardan erişim için gereklidir.
    # 'debug=False' production ortamları için daha stabil ve güvenlidir.
    app.run(host='0.0.0.0', port=18331, debug=False)