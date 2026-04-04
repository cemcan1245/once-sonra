import SwiftUI
import WebKit

// MARK: - SwiftUI Wrapper

struct WebView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> WebViewController {
        WebViewController()
    }
    func updateUIViewController(_ vc: WebViewController, context: Context) {}
}

// MARK: - UIViewController

class WebViewController: UIViewController {

    private var webView: WKWebView!

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = UIColor(red: 0.102, green: 0.102, blue: 0.180, alpha: 1)
        setupWebView()
        loadHTML()
    }

    // MARK: - Kurulum

    private func setupWebView() {
        let config = WKWebViewConfiguration()

        // Medya izinleri
        config.allowsInlineMediaPlayback = true
        config.mediaTypesRequiringUserActionForPlayback = []

        // iOS ↔ JavaScript köprüsü
        config.userContentController.add(self, name: "iOSApp")

        webView = WKWebView(frame: .zero, configuration: config)
        webView.translatesAutoresizingMaskIntoConstraints = false
        webView.scrollView.bounces = false
        webView.scrollView.contentInsetAdjustmentBehavior = .never
        webView.isOpaque = false
        webView.backgroundColor = .clear

        view.addSubview(webView)
        NSLayoutConstraint.activate([
            webView.topAnchor.constraint(equalTo: view.topAnchor),
            webView.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            webView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            webView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
        ])
    }

    private func loadHTML() {
        guard let url = Bundle.main.url(forResource: "before-after", withExtension: "html") else {
            showError("before-after.html bulunamadı.\nDosyayı OnceSonra klasörüne kopyalayıp projeye ekleyin.")
            return
        }
        // allowingReadAccessTo: aynı klasördeki kaynakları (css, js) da okuyabilir
        webView.loadFileURL(url, allowingReadAccessTo: url.deletingLastPathComponent())
    }

    private func showError(_ msg: String) {
        let html = """
        <body style="background:#1a1a2e;color:#e94560;font-family:sans-serif;
                     display:flex;align-items:center;justify-content:center;
                     height:100vh;margin:0;padding:20px;box-sizing:border-box;text-align:center;">
          <p>\(msg)</p>
        </body>
        """
        webView.loadHTMLString(html, baseURL: nil)
    }
}

// MARK: - WKScriptMessageHandler (JavaScript köprüsü)

extension WebViewController: WKScriptMessageHandler {

    func userContentController(_ userContentController: WKUserContentController,
                                didReceive message: WKScriptMessage) {
        guard
            let body     = message.body as? [String: Any],
            let action   = body["action"]   as? String,
            let dataUrl  = body["dataUrl"]  as? String,
            let filename = body["filename"] as? String,
            action == "saveFile"
        else { return }

        saveFile(dataUrl: dataUrl, filename: filename)
    }

    // MARK: Dosya kaydetme

    private func saveFile(dataUrl: String, filename: String) {
        // "data:image/png;base64,AAAA..." → Data
        guard
            let commaIdx = dataUrl.firstIndex(of: ",")
        else { return }

        let base64 = String(dataUrl[dataUrl.index(after: commaIdx)...])

        guard let data = Data(base64Encoded: base64, options: .ignoreUnknownCharacters) else { return }

        // Geçici dosyaya yaz
        let tmp = FileManager.default.temporaryDirectory.appendingPathComponent(filename)
        do {
            try data.write(to: tmp)
        } catch {
            DispatchQueue.main.async { self.showToast("Yazma hatası: \(error.localizedDescription)") }
            return
        }

        // iOS paylaşım sayfasını göster (Files'a kaydet, AirDrop, vb.)
        DispatchQueue.main.async {
            let vc = UIActivityViewController(activityItems: [tmp], applicationActivities: nil)
            // iPad için popover kaynağı
            if let popover = vc.popoverPresentationController {
                popover.sourceView = self.view
                popover.sourceRect = CGRect(
                    x: self.view.bounds.midX,
                    y: self.view.bounds.maxY - 50,
                    width: 0, height: 0
                )
            }
            self.present(vc, animated: true)
        }
    }

    // MARK: Kısa bildirim

    private func showToast(_ message: String) {
        let js = "var t=document.createElement('div');" +
                 "t.textContent='\(message)';" +
                 "t.style.cssText='position:fixed;bottom:80px;left:50%;transform:translateX(-50%);" +
                 "background:rgba(0,0,0,0.8);color:#fff;padding:10px 20px;border-radius:20px;" +
                 "font-size:14px;z-index:9999;pointer-events:none';" +
                 "document.body.appendChild(t);" +
                 "setTimeout(()=>t.remove(),2500);"
        webView.evaluateJavaScript(js, completionHandler: nil)
    }
}
