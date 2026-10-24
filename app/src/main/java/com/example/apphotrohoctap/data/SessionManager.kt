package com.example.apphotrohoctap.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("studyflow_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_EMAIL = "user_email"
        
        // Mock Auth Keys for demo purposes (vì không dùng Room UserEntity)
        private const val KEY_REG_EMAIL = "reg_email"
        private const val KEY_REG_PASSWORD = "reg_password"
        private const val KEY_REG_FULLNAME = "reg_fullname"
    }

    fun saveLoginSession(email: String) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_EMAIL, email)
            .apply()
    }

    fun clearSession() {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, false).remove(KEY_USER_EMAIL).apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    fun registerUser(email: String, password: String, fullName: String) {
        prefs.edit()
            .putString(KEY_REG_EMAIL, email)
            .putString(KEY_REG_PASSWORD, password)
            .putString(KEY_REG_FULLNAME, fullName)
            .apply()
    }

    fun checkLogin(email: String, password: String): Boolean {
        val regEmail = prefs.getString(KEY_REG_EMAIL, null)
        val regPassword = prefs.getString(KEY_REG_PASSWORD, null)
        
        return email == regEmail && password == regPassword
    }

    fun getRegisteredFullName(): String? {
        return prefs.getString(KEY_REG_FULLNAME, "Sinh viên")
    }
}
