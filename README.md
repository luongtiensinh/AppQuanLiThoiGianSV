# Readme.md

## Project

StudyFlow là ứng dụng Android quản lý thời gian sinh viên.

## Technology

- Kotlin
- Jetpack Compose
- Material 3
- Gradle Kotlin DSL
- Android SDK

## Architecture

Ưu tiên:
- MVVM
- Repository pattern khi cần
- StateFlow
- ViewModel
- Unidirectional Data Flow

## UI

- Sử dụng Jetpack Compose
- Sử dụng Material 3
- Không hard-code màu sắc nếu đã có Theme
- Không hard-code typography nếu đã có Typography
- UI phải responsive với nhiều kích thước màn hình

## Code quality

Codex phải kiểm tra:

1. Logic bug
2. Runtime crash
3. Null safety
4. State management
5. Lifecycle
6. Memory leak
7. Coroutine misuse
8. Compose recomposition
9. Performance
10. Accessibility
11. Security
12. Gradle/dependency issues
13. Duplicate code
14. Maintainability
15. Missing tests

## Review rules

Ưu tiên phát hiện lỗi thực sự có khả năng gây:

- Crash
- Sai dữ liệu
- Sai logic
- Regression
- Performance degradation
- Security vulnerability

Không báo lỗi chỉ vì khác style nếu không ảnh hưởng đến chất lượng hoặc quy ước của project.

Không tự ý sửa code khi chỉ được yêu cầu review.

Mỗi finding phải có:

- Mức độ nghiêm trọng
- File
- Dòng code
- Giải thích vấn đề
- Vì sao vấn đề xảy ra
- Cách sửa đề xuất

## Testing

Sau khi thay đổi code, ưu tiên chạy:

./gradlew assembleDebug

và các test liên quan nếu có.
