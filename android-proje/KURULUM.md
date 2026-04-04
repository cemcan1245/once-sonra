# Önce & Sonra — Android APK Kurulum Kılavuzu

## Yöntem 1 — Chrome PWA (Sunucu Gerekli, En Kolay)

Telefona APK kurmak yerine Chrome üzerinden doğrudan yükleyebilirsiniz.

1. Dosyaları ücretsiz bir barındırma hizmetine yükleyin (GitHub Pages, Netlify vb.)
2. Android telefonda Chrome ile siteyi açın
3. Sağ üst menü → **"Ana Ekrana Ekle"** veya **"Uygulamayı Yükle"**
4. Uygulama ana ekranda ikonla görünür, tam ekran çalışır

---

## Yöntem 2 — Android Studio ile APK Derleme

### Gereksinimler
- [Android Studio](https://developer.android.com/studio) (ücretsiz, ~1 GB)
- Java 17+ (Android Studio ile birlikte gelir)

### Adımlar

**1. HTML dosyasını assets klasörüne kopyalayın**

`android-proje` klasöründe `assets-kopyala.bat` dosyasına çift tıklayın.

Veya manuel: `before-after.html` → `app\src\main\assets\` klasörüne kopyalayın.

**2. Android Studio'da projeyi açın**

- Android Studio → **Open** → `android-proje` klasörünü seçin
- İlk açılışta Gradle senkronizasyonu otomatik başlar (internet gerekli, ~3-5 dk)

**3. APK derleyin**

Menüden:
```
Build → Build Bundle(s) / APK(s) → Build APK(s)
```

Derleme tamamlandığında altta **"Build Apk(s)"** bildiriminde **"locate"** linkine tıklayın.

APK şu konumda oluşur:
```
app\build\outputs\apk\debug\app-debug.apk
```

**4. Telefona kurun**

- `app-debug.apk` dosyasını telefona kopyalayın (USB veya e-posta)
- Telefonda: **Ayarlar → Güvenlik → Bilinmeyen kaynaklara izin ver** (veya dosyayı açınca otomatik sorar)
- APK dosyasına dokunun → **Kur**

---

## Yöntem 3 — Komut Satırı (Android Studio olmadan)

Android Studio kurulu değilse yalnızca komut satırı araçlarıyla derleyebilirsiniz.

**Gereksinim:** [Android SDK Command-line Tools](https://developer.android.com/tools)

```bat
cd android-proje
gradlew.bat assembleDebug
```

---

## Uygulama Özellikleri (Android'de)

| Özellik | Durum |
|---|---|
| Fotoğraf yükleme (galeri) | ✅ Çalışır |
| Video yükleme | ✅ Çalışır |
| Görsel birleştirme | ✅ Çalışır |
| Hizalama (nokta seçimi) | ✅ Çalışır |
| İşlem etiketi | ✅ Çalışır |
| PNG/WebM kaydetme | ✅ Downloads klasörüne kaydeder |
| EXIF tarih okuma | ⚠️ İnternet bağlantısı gerekir |
| Video kaydetme | ✅ Çalışır (Chrome 90+ WebView) |

---

## Sürüm Bilgisi

- **Minimum Android**: 8.0 (API 26)
- **Hedef Android**: 14 (API 34)
- **Paket adı**: tr.com.oncesonra
