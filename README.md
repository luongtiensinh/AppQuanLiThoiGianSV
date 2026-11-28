<div align="center">
  <img src="app/src/main/res/drawable/studyflow_logo.jpg" alt="StudyFlow Logo" width="120" height="120">
  
  # StudyFlow - Quản Lý Thời Gian Sinh Viên 📚⏰

  **Đồ án tốt nghiệp** ứng dụng di động hỗ trợ sinh viên lập kế hoạch học tập, quản lý thời khóa biểu, theo dõi deadline và rèn luyện sự tập trung qua phương pháp Pomodoro. Ứng dụng được thiết kế tối ưu hóa theo quy chuẩn **Google Play Vitals**.

  ![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
  ![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
  ![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white)
  ![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)

  ---
  
  <h3>📥 TẢI XUỐNG ỨNG DỤNG TẠI ĐÂY</h3>
  
  [![Download APK](https://img.shields.io/badge/Download-Release_v1.0.1-00C853?style=for-the-badge&logo=android&logoColor=white)](https://github.com/luongtiensinh/AppQuanLiThoiGianSV/releases/download/v1.0.1/app-release.apk)

  *Hoặc dùng ứng dụng quét mã QR bên dưới để tải trực tiếp vào điện thoại Android:*
  
  <img src="app/src/main/res/drawable/qr_code_download.png" alt="Mã QR Tải App" width="200" height="200">
</div>

---

## 🌟 TÍNH NĂNG NỔI BẬT

- **📅 Lịch biểu Thông minh:** Theo dõi lịch học theo ngày/tuần. Tự động nhận diện Giảng viên và Phòng học. Phân ca (Sáng/Chiều/Tối) logic.
- **✅ Quản lý Nhiệm vụ & Deadline:** Tạo và theo dõi tiến độ bài tập, nhắc nhở hạn nộp thông qua Local Notifications, phân chia việc con (Subtasks).
- **⏱️ Góc Tập Trung (Pomodoro):** Hẹn giờ tập trung giải quyết bài tập, tự động chuyển trạng thái nghỉ ngơi, có hỗ trợ nhạc nền tập trung (White noise).
- **📝 Ghi chú Học phần:** Tạo các bản tóm tắt, dán nhãn màu sắc. Tự động liên kết Ghi chú với Môn học có trong Lịch.
- **📊 Quản lý Điểm số:** Theo dõi tín chỉ, tính toán GPA hệ 4.0 và 10.0 chuẩn xác, hỗ trợ nhiều Học kỳ.
- **☁️ Đồng bộ đám mây (Cloud Sync):** Xây dựng kiến trúc *Offline-first* với Room Database kết hợp với *Firebase Firestore* để lưu trữ hai chiều (Delta Fetch, Concurrent Batch Write) đảm bảo không mất dữ liệu.

## 🛠️ KIẾN TRÚC & CÔNG NGHỆ

Dự án tuân thủ nghiêm ngặt **Clean Architecture** và mô hình **MVVM**:

- **Ngôn ngữ:** Kotlin 100%.
- **UI Toolkit:** Jetpack Compose & Material Design 3 (Giao diện Declarative).
- **Data Layer:** Room Database (SQLite), Datastore Preferences.
- **Background Tasks:** WorkManager (Quản lý tiến trình ngầm cho đồng bộ và nhắc nhở).
- **Cloud:** Firebase Firestore, Firebase Authentication, Credential Manager (Google Sign-In).
- **Dependency/Build:** Gradle Kotlin DSL (Version Catalogs), Coroutines, Flow (StateFlow, SharedFlow).

## 🚀 HƯỚNG DẪN CÀI ĐẶT DÀNH CHO NHÀ PHÁT TRIỂN

Nếu bạn muốn clone dự án này về máy để chạy trên Android Studio:

1. **Clone repository:**
   ```bash
   git clone https://github.com/luongtiensinh/AppQuanLiThoiGianSV.git
   ```
2. **Mở dự án:**
   Mở thư mục vừa clone bằng Android Studio (Khuyên dùng bản Android Studio Koala trở lên).
3. **Cấu hình Firebase:**
   - Dự án đã có sẵn file `google-services.json`. 
   - *Lưu ý:* Để test được tính năng Đăng nhập Google, bạn cần thay thế SHA-1 key của bạn vào Firebase Console, hoặc liên hệ chủ kho lưu trữ để cấp quyền test.
4. **Build & Run:**
   - Đảm bảo thiết bị / máy ảo chạy Android 7.0 (API 24) trở lên.
   - Bấm nút **Run** (Shift + F10) hoặc chạy lệnh Gradle:
     ```bash
     ./gradlew assembleDebug
     ```

## 🛡️ CHẤT LƯỢNG MÃ NGUỒN & HIỆU NĂNG

Ứng dụng được Audit để đạt tiêu chuẩn phát hành **Google Play Vitals**:
- Tối ưu hóa ANR (App Not Responding) bằng cách đẩy toàn bộ I/O Query sang `Dispatchers.IO`.
- Xử lý Soft Delete Cascade qua `@Transaction` đảm bảo tính Atomicity cho CSDL.
- Ứng dụng thuật toán chia khối (Chunking) để vượt qua giới hạn WriteBatch của Firestore.
- Thiết lập Proguard Rules đầy đủ ngăn chặn lỗi dịch ngược (Obfuscation issue) cho Gson và Firebase.

---
<div align="center">
  <i>Đồ án được thực hiện bởi sinh viên Lương Tiến Sinh - Khoa Công nghệ Thông tin</i>
</div>
