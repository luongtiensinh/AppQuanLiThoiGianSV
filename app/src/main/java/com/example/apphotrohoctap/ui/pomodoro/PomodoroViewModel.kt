package com.example.apphotrohoctap.ui.pomodoro

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Quản lý trạng thái và bộ đếm thời gian cho màn hình Pomodoro.
 *
 * @param clock: Hàm giả lập đồng hồ (Dependency Injection) giúp dễ dàng Unit Test mà không
 * bị phụ thuộc vào Thread/System thời gian thực của máy.
 */
class PomodoroViewModel(
    private val clock: () -> Long = { SystemClock.elapsedRealtime() }
) : ViewModel() {

    private val _uiState = MutableStateFlow(PomodoroUiState())
    val uiState: StateFlow<PomodoroUiState> = _uiState.asStateFlow()

    private val _sessionFinishedEvent = MutableSharedFlow<PomodoroMode>()
    val sessionFinishedEvent: SharedFlow<PomodoroMode> = _sessionFinishedEvent.asSharedFlow()

    private var timerJob: Job? = null
    private var endTimeMillis: Long = 0L

    private var appContext: android.content.Context? = null

    fun setContext(ctx: android.content.Context) {
        appContext = ctx.applicationContext
    }

    fun start() {
        if (_uiState.value.status == PomodoroStatus.RUNNING) return

        _uiState.update { it.copy(status = PomodoroStatus.RUNNING) }
        endTimeMillis = clock() + _uiState.value.remainingMillis

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.status == PomodoroStatus.RUNNING) {
                val now = clock()
                val rem = PomodoroLogic.remainingMillis(endTimeMillis, now)
                
                _uiState.update { it.copy(remainingMillis = rem) }

                if (rem <= 0) {
                    handleSessionComplete()
                    break
                }
                delay(250) // Cập nhật UI nhanh, mịn hơn
            }
        }
        
        // Đặt lịch thông báo nền (WorkManager) khi hẹn giờ hoàn tất
        appContext?.let { 
            com.example.apphotrohoctap.worker.PomodoroReminderManager.schedule(
                it, 
                _uiState.value.remainingMillis, 
                _uiState.value.mode
            )
        }
    }

    /**
     * Tạm dừng bộ đếm. Lưu lại thời gian còn lại (remaining) và hủy Job vòng lặp.
     */
    fun pause() {
        if (_uiState.value.status != PomodoroStatus.RUNNING) return
        
        timerJob?.cancel()
        val now = clock()
        val rem = PomodoroLogic.remainingMillis(endTimeMillis, now)
        _uiState.update { 
            it.copy(
                status = PomodoroStatus.PAUSED,
                remainingMillis = rem
            ) 
        }
        appContext?.let { com.example.apphotrohoctap.worker.PomodoroReminderManager.cancel(it) }
    }

    /**
     * Đặt lại bộ đếm về ban đầu (hoặc chuyển chế độ mà không ghi nhận phiên).
     */
    fun reset() {
        timerJob?.cancel()
        appContext?.let { com.example.apphotrohoctap.worker.PomodoroReminderManager.cancel(it) }
        _uiState.update { state ->
            state.copy(
                status = PomodoroStatus.IDLE,
                remainingMillis = state.mode.defaultDurationMillis,
                totalMillis = state.mode.defaultDurationMillis
            )
        }
    }

    /**
     * Đổi Mode tùy chỉnh (chỉ khi không chạy).
     */
    fun selectMode(mode: PomodoroMode) {
        if (_uiState.value.status == PomodoroStatus.RUNNING) return
        timerJob?.cancel()
        appContext?.let { com.example.apphotrohoctap.worker.PomodoroReminderManager.cancel(it) }
        _uiState.update { state ->
            state.copy(
                mode = mode,
                status = PomodoroStatus.IDLE,
                remainingMillis = mode.defaultDurationMillis,
                totalMillis = mode.defaultDurationMillis
            )
        }
    }

    /**
     * Bỏ qua (Skip) phiên hiện tại, nhảy tới chế độ tiếp theo.
     */
    fun skip() {
        val nextMode = PomodoroLogic.nextMode(_uiState.value.mode, _uiState.value.completedFocusSessions)
        selectMode(nextMode)
    }

    /**
     * Gắn liên kết một bài tập (Task) vào để làm mục tiêu học tập.
     */
    fun setLinkedTask(title: String?) {
        _uiState.update { it.copy(linkedTaskTitle = title) }
    }

    private suspend fun handleSessionComplete() {
        timerJob?.cancel()
        
        val currentState = _uiState.value
        val completedMode = currentState.mode
        
        // Tăng đếm nếu vừa hoàn thành phiên FOCUS
        val newCount = if (completedMode == PomodoroMode.FOCUS) {
            currentState.completedFocusSessions + 1
        } else {
            currentState.completedFocusSessions
        }
        
        val nextMode = PomodoroLogic.nextMode(completedMode, newCount)

        _uiState.update { state ->
            state.copy(
                mode = nextMode,
                status = PomodoroStatus.IDLE,
                remainingMillis = nextMode.defaultDurationMillis,
                totalMillis = nextMode.defaultDurationMillis,
                completedFocusSessions = newCount
            )
        }
        
        // Phát sự kiện thông báo cho UI (vd: bật chuông, hiển thị dialog chúc mừng)
        _sessionFinishedEvent.emit(completedMode)
    }
}
