# Kế hoạch triển khai nâng cấp tính năng Điểm số

Kế hoạch này phác thảo các thay đổi cần thiết để thực hiện 4 tính năng cải tiến cho trình quản lý điểm số:
1. Dự báo GPA toàn kỳ.
2. Cảnh báo trực quan cho điểm số cần đạt.
3. Chỉnh sửa điểm thành phần sau khi tạo môn.
4. Quản lý lịch sử học kỳ.

## User Review Required

> [!WARNING]
> Thay đổi này yêu cầu cập nhật cơ sở dữ liệu Room (thêm `SemesterEntity` và liên kết với `SubjectGradeEntity`). Chúng ta sẽ cần tăng version database và viết Migration để không làm mất dữ liệu hiện tại của người dùng.

> [!IMPORTANT]
> Giao diện sẽ cần thay đổi khá nhiều để phù hợp với việc thêm màn hình quản lý Học kỳ (Semester) và đưa màn hình Môn học (Subject) vào bên trong chi tiết của Học kỳ.

## Open Questions

- Bạn có muốn tự động tạo một "Học kỳ mặc định" cho các dữ liệu môn học hiện có, hay yêu cầu người dùng chọn một học kỳ mới khi mở app sau khi update? (Đề xuất: Tự động tạo một học kỳ "Học kỳ 1" và gán tất cả môn hiện có vào đó để tránh crash).
- Đối với việc chỉnh sửa trọng số điểm, nếu người dùng đã nhập điểm cho một cột, việc đổi trọng số có thể thay đổi điểm hiện tại, bạn có đồng ý với rủi ro này không?

## Proposed Changes

### Data Layer
- Thêm Entity mới: `SemesterEntity` (id, name, isCurrent).
- Cập nhật `SubjectGradeEntity`: Thêm trường `semesterId`.
- Cập nhật `AppDatabase`: Tăng version, thêm table mới, viết Migration từ V(hiện tại) sang V(mới).
- Cập nhật `GradeDao`:
  - Thêm các hàm CRUD cho `SemesterEntity`.
  - Sửa hàm `getAllSubjectsWithGrades` thành lấy theo `semesterId`.
  - Thêm hàm tính tổng tín chỉ và GPA theo `semesterId`.
- Cập nhật `GradeRepository`: Tương ứng với DAO.

---

### Domain / State Layer (ViewModel)
- Cập nhật `GradeViewModel`:
  - Quản lý danh sách `SemesterEntity`.
  - Quản lý trạng thái `selectedSemesterId`.
  - Load danh sách subject theo học kỳ đang chọn.
  - Tính toán GPA toàn kỳ và tổng tín chỉ của học kỳ.
  - Thêm function update/delete GradeComponent.

---

### UI Layer (Giao diện)
- Tạo màn hình mới `StudyFlowSemesterScreen.kt`: Liệt kê các học kỳ, hiển thị tổng GPA và tín chỉ của mỗi học kỳ.
- Cập nhật `StudyFlowGradesScreen.kt`: Đổi thành hiển thị danh sách môn của MỘT học kỳ (được truyền vào qua navigation). Hiển thị GPA toàn kỳ của học kỳ đó ở Header.
- Cập nhật `StudyFlowSubjectDetailScreen.kt`:
  - Thêm nút/icon Edit để vào chế độ chỉnh sửa.
  - Khi ở chế độ chỉnh sửa: cho phép thay đổi tên, trọng số, xóa cột điểm, thêm cột điểm mới.
  - Áp dụng cảnh báo màu sắc cho phần "cần đạt":
    - Xanh lá: Điểm cần đạt <= 5 (dễ).
    - Vàng: Điểm cần đạt từ 5 - 8 (trung bình).
    - Đỏ: Điểm cần đạt > 8 (khó).
- Cập nhật `StudyFlowNavGraph.kt`: Cập nhật lại luồng điều hướng: Home -> Semesters -> Grades (của 1 semester) -> Subject Detail.

## Verification Plan

### Automated Tests
- Cập nhật lại unit test trong `GradeCalculatorTest.kt` nếu cần thiết (kiểm tra hàm tính GPA toàn kỳ).

### Manual Verification
1. Cài đè app mới lên app cũ để test Database Migration (đảm bảo môn học cũ không mất và được gán vào học kỳ mặc định).
2. Tạo học kỳ mới, tạo môn học trong học kỳ đó.
3. Nhập điểm và kiểm tra màu sắc cảnh báo.
4. Thử tính năng chỉnh sửa (thêm/xóa/sửa trọng số) của cột điểm trên môn học đã tạo.
5. Kiểm tra GPA hiển thị trên màn hình học kỳ có chính xác dựa trên điểm và tín chỉ không.