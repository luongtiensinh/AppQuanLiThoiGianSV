package com.example.apphotrohoctap.data

import android.content.Context
import android.content.SharedPreferences

import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SessionManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "studyflow_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_EMAIL = "user_email"
        
        private const val KEY_REG_EMAIL = "reg_email"
        private const val KEY_REG_PASSWORD = "reg_password"
        private const val KEY_REG_FULLNAME = "reg_fullname"
    }

    fun saveLoginSession(email: String, fullName: String = "") {
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
