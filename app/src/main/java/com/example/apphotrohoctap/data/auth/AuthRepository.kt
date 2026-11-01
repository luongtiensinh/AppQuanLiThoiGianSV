package com.example.apphotrohoctap.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.example.apphotrohoctap.R
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

class AuthRepository(private val context: Context) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val credentialManager = CredentialManager.create(context)
    
    val currentUser get() = auth.currentUser
    
    fun signOut() {
        auth.signOut()
    }

    suspend fun signInWithGoogle(): AuthResult {
        return try {
            // 1. Tạo Nonce ngẫu nhiên để chống lại các cuộc tấn công phát lại (Replay Attacks)
            val rawNonce = UUID.randomUUID().toString()
            val bytes = rawNonce.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }
            
            // 2. Định nghĩa cấu hình Google ID
            // Lưu ý: Lấy web_client_id từ file strings.xml mà google-services.json tự sinh ra
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false) // Cho phép chọn các tài khoản mới
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setNonce(hashedNonce)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            // 3. Hiển thị UI chọn tài khoản Google (Bằng Credential Manager)
            val result = credentialManager.getCredential(
                context = context,
                request = request
            )
            
            val credential = result.credential
            
            // Xử lý CustomCredential trả về từ Credential Manager
            if (credential is androidx.credentials.CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                
                // Lấy Token gửi về Firebase Auth
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()
                AuthResult.Success(authResult.user)
            } else {
                AuthResult.Error("Loại chứng chỉ không hợp lệ: ${credential.javaClass.simpleName}")
            }
        } catch (e: GetCredentialCancellationException) {
            AuthResult.Error("Người dùng đã hủy đăng nhập.")
        } catch (e: GetCredentialException) {
            Log.e("AuthRepository", "Lỗi Credential", e)
            AuthResult.Error("Lỗi đăng nhập Credential: ${e.message}")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Lỗi Firebase Auth", e)
            AuthResult.Error("Lỗi hệ thống: ${e.message}")
        }
    }

    suspend fun registerWithEmailAndPassword(email: String, password: String, fullName: String): AuthResult {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user != null) {
                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(fullName)
                    .build()
                user.updateProfile(profileUpdates).await()
            }
            AuthResult.Success(user)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Lỗi tạo tài khoản", e)
            AuthResult.Error("Lỗi tạo tài khoản: ${e.message}")
        }
    }
}
