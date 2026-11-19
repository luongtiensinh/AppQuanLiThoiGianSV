package com.example.apphotrohoctap.ui.settings

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphotrohoctap.notification.NotificationHelper
import com.example.apphotrohoctap.worker.ReminderManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val _isDeadlineReminderEnabled = MutableStateFlow(true)
    val isDeadlineReminderEnabled: StateFlow<Boolean> = _isDeadlineReminderEnabled.asStateFlow()

    private val _useAlarmSound = MutableStateFlow(true)
    val useAlarmSound: StateFlow<Boolean> = _useAlarmSound.asStateFlow()

    init {
        _isDeadlineReminderEnabled.value = prefs.getBoolean("deadlineReminder", true)
        _useAlarmSound.value = prefs.getBoolean("useAlarmSound", true)
    }

    fun toggleDeadlineReminder(enabled: Boolean) {
        _isDeadlineReminderEnabled.value = enabled
        prefs.edit().putBoolean("deadlineReminder", enabled).apply()
        val app = getApplication<Application>()
        if (enabled) {
            ReminderManager.startPeriodicReminder(app)
        } else {
            ReminderManager.stopPeriodicReminder(app)
        }
    }

    fun toggleUseAlarmSound(enabled: Boolean) {
        _useAlarmSound.value = enabled
        prefs.edit().putBoolean("useAlarmSound", enabled).apply()
    }

    fun triggerTestReminder(context: Context): Boolean {
        if (!_isDeadlineReminderEnabled.value) {
            toggleDeadlineReminder(true)
        }
        val useAlarm = _useAlarmSound.value
        val posted = NotificationHelper.sendNotification(
            context = context,
            title = "StudyFlow - Nhắc nhở Thử nghiệm!",
            message = "Đây là thông báo thử nghiệm! Kiểm tra âm thanh báo thức và chuông rung.",
            useAlarmSound = useAlarm
        )
        ReminderManager.testOneTimeReminder(getApplication())
        return posted
    }

    sealed class SyncState {
        object Idle : SyncState()
        object Loading : SyncState()
        data class Success(val message: String) : SyncState()
        data class Error(val message: String) : SyncState()
    }

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val syncRepository by lazy {
        val database = com.example.apphotrohoctap.data.AppDatabase.getDatabase(getApplication())
        com.example.apphotrohoctap.data.sync.SyncRepository(database, getApplication())
    }

    fun syncDataWithCloud() {
        _syncState.value = SyncState.Loading
        viewModelScope.launch {
            when (val result = syncRepository.syncWithCloud()) {
                is com.example.apphotrohoctap.data.sync.SyncResult.Success -> {
                    _syncState.value = SyncState.Success("Đồng bộ dữ liệu thành công!")
                }
                is com.example.apphotrohoctap.data.sync.SyncResult.NotLoggedIn -> {
                    _syncState.value = SyncState.Error("Chưa đăng nhập tài khoản Google.")
                }
                is com.example.apphotrohoctap.data.sync.SyncResult.Error -> {
                    // Filter raw exception messages for a better UX
                    val errorMsg = result.message
                    val friendlyMessage = when {
                        errorMsg.contains("Lỗi kết nối mạng") || errorMsg.contains("network", ignoreCase = true) || errorMsg.contains("host", ignoreCase = true) -> 
                            "Lỗi kết nối mạng. Vui lòng kiểm tra lại Internet."
                        errorMsg.contains("permission", ignoreCase = true) -> 
                            "Phiên đăng nhập hết hạn hoặc bị từ chối quyền truy cập."
                        else -> errorMsg
                    }
                    _syncState.value = SyncState.Error(friendlyMessage)
                }
            }
        }
    }

    fun resetSyncState() {
        _syncState.value = SyncState.Idle
    }

    fun clearAllUserData(onComplete: () -> Unit) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                // 1. Xóa toàn bộ CSDL cục bộ (Room)
                val database = com.example.apphotrohoctap.data.AppDatabase.getDatabase(getApplication())
                database.clearAllTables()
                
                // 2. Xóa mốc thời gian đồng bộ (Đảm bảo Full Sync ở lần đăng nhập tới)
                val application = getApplication<Application>()
                application.getSharedPreferences("studyflow_sync_prefs", Context.MODE_PRIVATE).edit().clear().apply()
                
                // 3. Xóa các dữ liệu lịch sử cục bộ của phiên người dùng trước đó
                application.getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE).edit().clear().apply()
                application.getSharedPreferences("pomodoro_stats", Context.MODE_PRIVATE).edit().clear().apply()
                
                // 4. Đăng xuất khỏi hệ thống mây của Google
                com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    onComplete()
                }
            }
        }
    }
}
