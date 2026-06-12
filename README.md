# EduConnect

EduConnect adalah aplikasi komunitas akademik berbasis Android yang dirancang sebagai platform pusat informasi dan komunikasi terintegrasi bagi mahasiswa. Aplikasi ini mempermudah mahasiswa dalam mengakses info akademik, diskusi perkuliahan, seminar, info magang, hingga beasiswa yang biasanya tersebar acak di berbagai grup chat, ke dalam satu wadah terorganisir.

---

## 1. Deskripsi Aplikasi

EduConnect menggabungkan tiga pilar utama dalam pemenuhannya sebagai aplikasi penunjang perkuliahan:
* **Pilar Sosial & Komunikasi:** Menyediakan ruang bagi mahasiswa untuk saling berinteraksi, melihat anggota komunitas, serta berdiskusi melalui kolom komentar pada setiap postingan.
* **Pilar Pendidikan:** Seluruh konten dan informasi yang beredar di dalam sistem difokuskan secara eksklusif untuk kebutuhan akademik mahasiswa.
* **Aksesibilitas Tinggi:** Mendukung kenyamanan visual pengguna melalui fitur Dual Tema (*Dark Mode* dan *Light Mode*) serta ketersediaan akses data secara offline.

---

## 2. Panduan Penggunaan (Cara Penggunaan)

Berikut adalah alur dan cara penggunaan fitur-fitur utama di dalam aplikasi EduConnect:

### 2.1 Alur Autentikasi
1.  **Splash Screen:** Saat pertama kali aplikasi dibuka, pengguna akan disambut oleh halaman *Splash* yang menampilkan logo dan slogan aplikasi.
2.  **Registrasi & Login:** Pengguna baru dapat membuat akun terlebih dahulu melalui `RegisterActivity`, kemudian masuk menggunakan akun tersebut melalui `LoginActivity`.

### 2.2 Menjelajahi Postingan dan Filter Kategori
1.  **Membaca Informasi:** Pada halaman Utama (`HomeFragment`), pengguna dapat melihat *feed* berisi ringkasan postingan terbaru.
2.  **Pencarian Konten:** Gunakan *Search Bar* di bagian atas untuk menyaring informasi akademik secara spesifik berdasarkan kata kunci.
3.  **Filter Kategori:** Ketuk salah satu *Filter Chip* (seperti Akademik, Magang, Seminar, Beasiswa) untuk mengelompokkan postingan berdasarkan topik tertentu.
4.  **Memperbarui Data:** Tarik layar ke bawah (*Swipe-to-Refresh*) untuk memaksa aplikasi mengambil data postingan terbaru dari server.

### 2.3 Manajemen Bookmark dan Detail Postingan
1.  **Membaca Detail & Komentar:** Ketuk salah satu kartu postingan untuk membuka `DetailPostActivity`. Di halaman ini, pengguna dapat membaca isi konten secara utuh serta melihat komentar dari mahasiswa lain.
2.  **Menyimpan Konten (Bookmark):** Ketuk tombol **Simpan** pada postingan. Postingan tersebut akan langsung tersimpan ke database lokal.
3.  **Akses Offline:** Buka `BookmarkFragment` melalui navigasi bawah. Semua konten yang sudah kamu simpan dapat dibaca kembali di sini, bahkan saat perangkatmu tidak terhubung ke internet.

### 2.4 Pengaturan Profil dan Tema
1.  Buka `ProfileFragment` melalui menu navigasi bawah.
2.  Geser *Switch* pada menu **Dark Mode** untuk mengubah tampilan aplikasi menjadi tema gelap, atau geser kembali untuk tema terang.
3.  Ketuk tombol **Logout** untuk keluar dari sesi akun dan kembali ke halaman Login.

---

## 3. Implementasi Teknis

EduConnect dibangun menggunakan arsitektur komponen Android modern untuk memastikan aplikasi berjalan secara responsif, hemat memori, dan mendukung mode offline.

### 3.1 Komponen Arsitektur Utama

| Komponen Arsitektur | Kelas / Library | Implementasi Teknis & Fungsi |
| :--- | :--- | :--- |
| **View Binding** | `Jetpack ViewBinding` | Menghubungkan layout XML dengan kode Java secara *null-safe* dan *type-safe*, menggantikan fungsi `findViewById`. |
| **Navigation** | `Navigation Component` | Mengelola transaksi dan perpindahan antar Fragment (`Home`, `Community`, `Bookmark`) dalam satu `MainActivity` menggunakan *Bottom Navigation*. |
| **Asynchronous Engine** | `ExecutorService` (Background Thread) | Menangani operasi I/O berat seperti proses *read/write* database lokal SQLite agar tidak memblokir *UI Thread* utama. |

### 3.2 Integrasi Sumber Data (Data Sources)

#### A. Web Service / REST API (Retrofit)
Aplikasi memanfaatkan library **Retrofit** dengan arsitektur dua *Base URL* berbeda untuk manajemen data online:
* **MockAPI Endpoint:** Digunakan untuk melakukan HTTP `GET` data postingan (`posts`) dan direktori profil pengguna (`users`).
* **JSONPlaceholder Endpoint:** Digunakan untuk melakukan HTTP `GET` dan `POST` data komentar (`comments`) secara real-time berdasarkan ID postingan terkait.

#### B. Penyimpanan Lokal & Cache (SQLite & SharedPreferences)
* **SQLite Database (`DatabaseHelper`):** Bertindak sebagai repositori penyimpanan lokal. Ketika data berhasil ditarik dari API, sistem melakukan *caching* ke SQLite. SQLite juga menjadi fondasi utama fitur *Bookmark* agar data bersifat persisten saat offline.
* **SharedPreferences:** Digunakan sebagai penyimpanan data primitif yang ringan untuk memegang *state* sesi login pengguna (`UserSession`) dan konfigurasi status tema (*Dark/Light Mode*).

#### C. Sinkronisasi Tema Dinamis (`ThemeUtils`)
Perpindahan tema memanfaatkan intersep dari status komparasi nilai Boolean di *SharedPreferences*. Ketika *Switch* Dark Mode ditekan, sistem memicu perintah `requireActivity().recreate()` untuk merender ulang seluruh komponen UI agar menyesuaikan *Theme Attribute* (`?attr/...`) yang telah dideklarasikan pada struktur file `themes.xml`.


