# Önce & Sonra — iOS Kurulum Kılavuzu

## Yöntem 1 — Safari PWA (En Kolay, Mac Gerekmez)

> iOS 16.4 ve üzeri tam PWA desteği sunar.

1. Dosyaları ücretsiz bir barındırma hizmetine yükleyin (GitHub Pages, Netlify vb.)
2. iPhone/iPad'de **Safari** ile siteyi açın
3. Paylaş butonu (□↑) → **"Ana Ekrana Ekle"**
4. Uygulama tam ekran, ikonlu şekilde çalışır

---

## Yöntem 2 — Xcode ile IPA Derleme (Mac Gerekli)

### Gereksinimler

| Araç | Nereden |
|---|---|
| Mac (macOS 13+) | — |
| Xcode 15+ | Mac App Store (ücretsiz) |
| Apple ID | apple.com (ücretsiz) |
| iPhone/iPad bağlantısı | USB veya Wi-Fi |

> **Not:** App Store'a dağıtmak için Apple Developer Program üyeliği ($99/yıl) gerekir.
> Kendi cihazınıza kurmak ücretsizdir.

---

### Adımlar

**1. HTML dosyasını kopyalayın**

`ios-proje` klasöründe `assets-kopyala-ios.bat` dosyasına çift tıklayın.

Sonuç: `before-after.html` → `ios-proje/OnceSonra/` klasörüne kopyalanır.

**2. Projeyi Mac'e aktarın**

`ios-proje` klasörünün tamamını Mac'e kopyalayın (USB, AirDrop veya bulut).

**3. Xcode'da açın**

```
Xcode → Open → ios-proje/OnceSonra.xcodeproj
```

**4. Signing & Capabilities ayarlayın**

- Sol panelde `OnceSonra` projesini seçin
- `Signing & Capabilities` sekmesi
- **Team** → Apple ID'nizi seçin (ilk açılışta giriş isteyebilir)
- **Bundle Identifier**: `tr.com.oncesonra` (veya benzersiz bir ID girin)

**5. Cihazı bağlayın ve çalıştırın**

- iPhone/iPad'i USB ile bağlayın
- Xcode üst çubuğunda cihazınızı seçin
- **▶ Run** (veya `Cmd+R`)

İlk kurulumda iPhone'da:
```
Ayarlar → Genel → VPN ve Cihaz Yönetimi → Apple Geliştirici → Güven
```

---

### Dağıtım (Başka iPhone'lara Göndermek)

**TestFlight (önerilen):**
1. Apple Developer hesabı gerekir ($99/yıl)
2. `Product → Archive` ile arşiv oluşturun
3. App Store Connect'e yükleyin → TestFlight linki paylaşın

**AltStore ile sideload (ücretsiz, 7 gün sınırı):**
1. Mac'te [AltServer](https://altstore.io) kurun
2. iPhone'a AltStore kurun
3. IPA dosyasını AltStore ile yükleyin
4. Her 7 günde bir yenileme gerekir (Wi-Fi ile otomatik yenilenebilir)

---

## Uygulama Özellikleri (iOS)

| Özellik | Durum |
|---|---|
| Fotoğraf yükleme (galeri) | ✅ iOS 14+ |
| Video yükleme | ✅ iOS 14+ |
| Görsel birleştirme | ✅ |
| Hizalama noktası seçimi | ✅ |
| İşlem etiketi | ✅ |
| PNG kaydetme | ✅ Paylaş sayfası açılır |
| Video kaydetme (WebM) | ✅ Paylaş sayfası açılır |
| EXIF tarih okuma | ⚠️ İnternet bağlantısı gerekir |

---

## Proje Yapısı

```
ios-proje/
├── OnceSonra.xcodeproj/
│   └── project.pbxproj
└── OnceSonra/
    ├── OnceSonraApp.swift      ← Uygulama giriş noktası
    ├── ContentView.swift       ← Ana görünüm
    ├── WebView.swift           ← WKWebView + iOS köprüsü
    ├── Info.plist              ← İzin açıklamaları
    ├── before-after.html       ← ← BAT ile kopyalanır
    └── Assets.xcassets/        ← İkon ve renkler
```
