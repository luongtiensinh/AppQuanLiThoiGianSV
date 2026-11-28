package com.example.apphotrohoctap.ui.common

internal object NameUtils {

    private val commonVietnameseSurnames = setOf(
        "nguyen", "tran", "le", "pham", "hoang", "huynh", "vu", "vo", 
        "dang", "bui", "do", "ho", "ngo", "duong", "ly", "dinh", "trinh"
    )

    /**
     * Hàm tách Tên (First Name) thông minh từ chuỗi Họ và Tên nhập vào hoặc từ Google Account.
     * Xử lý cả tên dạng Việt Nam (Nguyễn Lương Tiến -> Tiến), dạng Western Google (Tien Nguyen -> Tien),
     * và dạng địa chỉ Email (tiennguyen@gmail.com -> Tiennguyen).
     */
    fun extractFirstName(rawName: String?): String {
        if (rawName.isNullOrBlank()) return "Sinh viên"

        val name = if (rawName.contains("@")) {
            rawName.substringBefore("@")
        } else {
            rawName.trim()
        }

        val parts = name.split(" ").map { it.trim() }.filter { it.isNotBlank() }
        if (parts.isEmpty()) return "Sinh viên"

        if (parts.size == 1) {
            return parts[0].replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }

        val firstWordLower = parts.first().lowercase().replace(".", "")
        val lastWordLower = parts.last().lowercase().replace(".", "")

        // Nếu từ cuối cùng là Họ Việt Nam (VD: Nguyen) nhưng từ đầu tiên KHÔNG PHẢI là Họ
        // Nghĩa là Google Account đang lưu theo định dạng Western "Tien Nguyen"
        return if (commonVietnameseSurnames.contains(lastWordLower) && !commonVietnameseSurnames.contains(firstWordLower)) {
            parts.first().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        } else {
            // Định dạng Tiếng Việt chuẩn "Nguyễn Lương Tiến" -> Lấy từ cuối cùng
            parts.last().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }
}
