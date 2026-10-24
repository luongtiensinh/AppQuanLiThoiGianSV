package com.example.apphotrohoctap.ui.pomodoro

/**
 * Các chế độ hoạt động của đồng hồ Pomodoro.
 */
enum class PomodoroMode(val defaultDurationMillis: Long) {
    FOCUS(25 * 60 * 1000L),       // 25 phút
    SHORT_BREAK(5 * 60 * 1000L),  // 5 phút
    LONG_BREAK(15 * 60 * 1000L)   // 15 phút
}

/**
 * Logic thuần điều phối trạng thái và tính toán thời gian cho Pomodoro.
 * Tách biệt hoàn toàn khỏi ViewModel và vòng đời Android (Không dính dáng Context, Coroutines, v.v...)
 * Lý do: Code thuần (pure function) cho phép Unit Test siêu nhanh, đảm bảo State Machine 
 * của Pomodoro không bị ràng buộc bởi các yếu tố bất đồng bộ.
 */
object PomodoroLogic {

    /**
     * Xác định chế độ kế tiếp theo luật của Pomodoro:
     * - Cứ học xong (FOCUS) -> Nghỉ
     * - Nếu vừa hoàn thành đủ 4 phiên học (completedFocusSessions % 4 == 0) -> Nghỉ dài (LONG_BREAK)
     * - Nếu chưa đủ 4 phiên -> Nghỉ ngắn (SHORT_BREAK)
     * - Nếu đang nghỉ (SHORT_BREAK / LONG_BREAK) -> Quay lại học (FOCUS)
     */
    fun nextMode(current: PomodoroMode, completedFocusSessions: Int): PomodoroMode {
        return when (current) {
            PomodoroMode.FOCUS -> {
                if (completedFocusSessions > 0 && completedFocusSessions % 4 == 0) {
                    PomodoroMode.LONG_BREAK
                } else {
                    PomodoroMode.SHORT_BREAK
                }
            }
            PomodoroMode.SHORT_BREAK, PomodoroMode.LONG_BREAK -> PomodoroMode.FOCUS
        }
    }

    /**
     * Tính toán thời gian còn lại (tuyệt đối không để giá trị âm).
     * @param endTime Mốc thời gian (millis) kết thúc dự kiến.
     * @param now Thời gian hiện tại.
     */
    fun remainingMillis(endTime: Long, now: Long): Long {
        val diff = endTime - now
        return if (diff < 0) 0L else diff
    }
}
