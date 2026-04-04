package tr.com.oncesonra;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.*;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class MainActivity extends Activity {

    private WebView webView;
    private ValueCallback<Uri[]> fileChooserCallback;
    private static final int FILE_CHOOSER_RC = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        setContentView(webView);

        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setAllowFileAccess(true);
        ws.setAllowContentAccess(true);
        ws.setDomStorageEnabled(true);
        ws.setMediaPlaybackRequiresUserGesture(false);
        ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        // Büyük canvas/base64 işlemleri için heap artırılıyor
        ws.setJavaScriptCanOpenWindowsAutomatically(false);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView wv,
                                             ValueCallback<Uri[]> cb,
                                             FileChooserParams params) {
                // Önceki bekleyen callback varsa iptal et
                if (fileChooserCallback != null) {
                    fileChooserCallback.onReceiveValue(null);
                }
                fileChooserCallback = cb;
                try {
                    Intent intent = params.createIntent();
                    // Çoklu seçime izin ver
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                    startActivityForResult(
                        Intent.createChooser(intent, "Fotoğraf veya Video Seç"),
                        FILE_CHOOSER_RC
                    );
                } catch (Exception e) {
                    fileChooserCallback = null;
                    return false;
                }
                return true;
            }
        });

        // JavaScript köprüsü: kaydetme ve diğer native işlemler
        webView.addJavascriptInterface(new JsBridge(), "AndroidApp");

        webView.loadUrl("file:///android_asset/before-after.html");
    }

    // ─────────────────────────────────────────────────────────────────
    //  JavaScript Köprüsü
    // ─────────────────────────────────────────────────────────────────
    class JsBridge {

        /**
         * HTML'den çağrılır: canvas.toDataURL() çıktısını (base64) alır ve
         * Downloads klasörüne kaydeder.
         *
         * @param dataUrl  "data:image/png;base64,AAAA..." formatında string
         * @param filename Kaydedilecek dosya adı (örn. once-sonra.png)
         */
        @JavascriptInterface
        public void saveFile(final String dataUrl, final String filename) {
            try {
                // Prefix'i ayır: "data:image/png;base64"
                String[] split = dataUrl.split(",", 2);
                if (split.length < 2) throw new IllegalArgumentException("Geçersiz data URL");

                String meta     = split[0];
                byte[] bytes    = android.util.Base64.decode(split[1], android.util.Base64.DEFAULT);
                String mimeType = meta.replaceAll("data:([^;]+);.*", "$1");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Android 10+ → MediaStore API (izin gerektirmez)
                    ContentValues cv = new ContentValues();
                    cv.put(MediaStore.Downloads.DISPLAY_NAME, filename);
                    cv.put(MediaStore.Downloads.MIME_TYPE, mimeType);
                    cv.put(MediaStore.Downloads.IS_PENDING, 1);

                    Uri col = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                    Uri uri = getContentResolver().insert(col, cv);

                    if (uri != null) {
                        try (OutputStream os = getContentResolver().openOutputStream(uri)) {
                            if (os != null) os.write(bytes);
                        }
                        cv.clear();
                        cv.put(MediaStore.Downloads.IS_PENDING, 0);
                        getContentResolver().update(uri, cv, null, null);
                    }
                } else {
                    // Android 9 ve altı → doğrudan dosya sistemi
                    File dir  = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File file = new File(dir, filename);
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        fos.write(bytes);
                    }
                    // Medya tarayıcısını bilgilendir
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.fromFile(file)));
                }

                runOnUiThread(() ->
                    Toast.makeText(MainActivity.this,
                        "✓ Kaydedildi: " + filename, Toast.LENGTH_SHORT).show()
                );

            } catch (Exception e) {
                runOnUiThread(() ->
                    Toast.makeText(MainActivity.this,
                        "Kaydetme hatası: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  Dosya seçici sonucu
    // ─────────────────────────────────────────────────────────────────
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RC && fileChooserCallback != null) {
            Uri[] results = null;
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                results = new Uri[]{ data.getData() };
            }
            fileChooserCallback.onReceiveValue(results);
            fileChooserCallback = null;
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  Geri tuşu — WebView geçmişi
    // ─────────────────────────────────────────────────────────────────
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
}
