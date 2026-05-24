# Betulingo - Çok Dilli Masaüstü Kelime Öğrenme Platformu

Betulingo Academy; JavaFX, Maven ve SQLite teknolojileri kullanılarak nesne yönelimli programlama prensipleri (OOP) ve modern UI/UX yaklaşımları doğrultusunda geliştirilmiş, genişletilebilir bir masaüstü kelime dağarcığı geliştirme uygulamasıdır.

## 🚀 Mimari ve Öne Çıkan Özellikler

* **Modüler Dil Yönetimi (`TabPane` Mimarisi):** Kullanıcının aktif olarak çalıştığı dil kütüphanelerine göre dinamik olarak ayrışan, veri tutarlılığı yüksek ve sekmeli arayüz yapısı.
* **Asenkron Çeviri Entegrasyonu:** Kelime giriş esnasında seçilen kütüphanenin dil koduna (İngilizce, Fransızca, Almanca, İspanyolca vb.) duyarlı olarak Google Translate API uç noktasını tetikleyen ve ana arayüz iş parçacığını (Main Thread) kilitlemeyen asenkron `Thread` mimarisi.
* **Klavye Odaklı Akış (Kullanıcı Deneyimi):** Form alanlarında ardışık `Enter` tuşu basımlarıyla bir sonraki metin alanına geçiş sağlayan, son basımda veriyi veritabanına işleyerek formu sıfırlayan ve odağı (Focus) yeniden ilk alana çeken kesintisiz veri giriş mekanizması.
* **Algoritmik Pratik ve Puanlama:** Test modülünde doğru cevaplar için +10, yanlış cevaplar için -10 puan uygulayan; kullanıcının zayıf olduğu (başarı skoru düşük) kelimeleri ağırlıklı karıştırma algoritmasıyla öncelikli olarak ekrana getiren dinamik havuz yönetimi.
* **Bento Grid Dashboard:** Toplam kelime gelişim sürecini, öğrenilmiş (master) seviyedeki kelimeleri ve tekrar edilmesi gereken kritik eşikteki verileri gerçek zamanlı raporlayan istatistik paneli.

## 🛠️ Kullanılan Teknolojiler

* **Dil / Runtime:** Java 17 veya üzeri (JDK)
* **Arayüz Frameworkü:** JavaFX (Özelleştirilmiş CSS bileşen mimarisi ile)
* **Veritabanı Katmanı:** SQLite (Gömülü / Embedded Veritabanı yapısı, JDBC Sürücüsü)
* **Bağımlılık ve Paket Yönetimi:** Maven

## 📦 Kurulum ve Çalıştırma

### Geliştirici Ortamı (IDE / Terminal)

Depoyu yerel ortamınıza klonladıktan sonra, projenin kök dizininde aşağıdaki komutları çalıştırarak uygulamayı derleyebilir ve ayağa kaldırabilirsiniz:

```bash
# Bağımlılıkları temizleyin ve projeyi derleyin
mvn clean compile

# Uygulamayı JavaFX eklentisi ile çalıştırın
mvn javafx:run
