package com.example.apphotrohoctap.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Trình quản lý phiên cục bộ bằng SharedPreferences.
 * Do ứng dụng đã nâng cấp lên Firebase Authentication, Class này chỉ còn
 * đóng vai trò là một "Cờ đánh dấu trạng thái Cache", giúp ứng dụng
 * biết người dùng có nhấn nút "Ghi nhớ đăng nhập" hay không để tự động Bypass màn Login.
 */
class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("studyflow_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_FULLNAME = "user_fullname"
    }

    /**
     * Lưu trạng thái Cache của Firebase vào Local.
     */
    fun saveLoginSession(email: String, fullName: String) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_FULLNAME, fullName)
            .apply()
    }

    /**
     * Xóa cache phiên làm việc.
     */
    fun clearSession() {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .remove(KEY_USER_EMAIL)
            .remove(KEY_USER_FULLNAME)
            .apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    fun getRegisteredFullName(): String? {
        return prefs.getString(KEY_USER_FULLNAME, "Sinh viên")
    }
}
