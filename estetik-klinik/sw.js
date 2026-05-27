const CACHE = 'estetik-klinik-v25';
const ASSETS = [
  './', './index.html', './manifest.json', './icon.svg',
  './icon192.png', './icon512.png',
  './apple-touch-icon.png', './apple-touch-icon-152.png', './apple-touch-icon-167.png',
  './splash-1170x2532.png', './splash-1290x2796.png', './splash-1179x2556.png',
  './splash-750x1334.png', './splash-828x1792.png', './splash-1242x2688.png'
];

self.addEventListener('install', e => {
  e.waitUntil(caches.open(CACHE).then(c => c.addAll(ASSETS)).then(() => self.skipWaiting()));
});

self.addEventListener('activate', e => {
  e.waitUntil(
    caches.keys().then(keys => Promise.all(keys.filter(k => k !== CACHE).map(k => caches.delete(k))))
      .then(() => self.clients.claim())
  );
});

self.addEventListener('fetch', e => {
  e.respondWith(
    caches.match(e.request).then(cached => cached || fetch(e.request).catch(() => caches.match('./index.html')))
  );
});
