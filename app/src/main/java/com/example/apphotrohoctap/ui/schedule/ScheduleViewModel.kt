package com.example.apphotrohoctap.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphotrohoctap.data.ScheduleEntity
import com.example.apphotrohoctap.data.ScheduleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class ScheduleViewModel(private val repository: ScheduleRepository) : ViewModel() {

    val schedules: StateFlow<List<ScheduleEntity>> = repository.getAllSchedules()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addSchedule(schedule: ScheduleEntity, onError: (String) -> Unit, onSuccess: () -> Unit) {
        if (schedule.subject.isBlank()) {
            onError("Tên môn học không được để trống")
            return
        }
        
        // Vì hệ thống lưu trữ sử dụng kỹ thuật Packed Time (Gói thời gian):
        // Giá trị = (Tiết * 10000) + (Giờ * 100) + Phút.
        // Dữ liệu từ giao diện luôn đảm bảo cả startPeriod và endPeriod đều được đóng gói cùng 1 định dạng.
        // Do đó, thuật toán so sánh số nguyên (Int) thuần túy dưới đây hoàn toàn chính xác tuyệt đối.
        if (schedule.endPeriod < schedule.startPeriod) {
            onError("Thời gian kết thúc phải lớn hơn hoặc bằng thời gian bắt đầu")
            return
        }

        // Kiểm tra trùng lịch (Overlap check)
        val isOverlapping = checkOverlap(schedule, schedules.value)

        if (isOverlapping) {
            onError("Trùng lịch! Đã có môn học khác diễn ra vào khung giờ này.")
            return
        }

        viewModelScope.launch {
            try {
                repository.addSchedule(schedule)
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                e.printStackTrace()
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) { onError("Lỗi lưu cơ sở dữ liệu.") }
            }
        }
    }

    fun deleteSchedule(schedule: ScheduleEntity) {
        viewModelScope.launch {
            repository.deleteSchedule(schedule.id)
        }
    }

    fun updateSchedule(schedule: ScheduleEntity, onError: (String) -> Unit, onSuccess: () -> Unit) {
        if (schedule.subject.isBlank()) {
            onError("Tên môn học không được để trống")
            return
        }
        
        // Vì giờ đây startPeriod và endPeriod có thể là dạng Packed (ví dụ: 10730)
        // Nên so sánh vẫn đúng vì (endP * 10000 + endHour * 100 + endMinute)
        // sẽ luôn lớn hơn nếu endHour/Minute lớn hơn.
        if (schedule.endPeriod < schedule.startPeriod) {
            onError("Thời gian kết thúc phải lớn hơn hoặc bằng thời gian bắt đầu")
            return
        }

        // Kiểm tra trùng lịch (Overlap check)
        val isOverlapping = checkOverlap(schedule, schedules.value)

        if (isOverlapping) {
            onError("Trùng lịch! Đã có môn học khác diễn ra vào khung giờ này.")
            return
        }

        viewModelScope.launch {
            try {
                repository.updateSchedule(schedule)
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                e.printStackTrace()
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) { onError("Lỗi lưu cơ sở dữ liệu.") }
            }
        }
    }
}

internal fun checkOverlap(newSchedule: ScheduleEntity, existingSchedules: List<ScheduleEntity>): Boolean {
    // Nếu môn học không lặp lại và đã bị xóa, bỏ qua (Mặc dù dữ liệu UI vốn đã lọc isDeleted = 0)
    return existingSchedules.any { existing ->
        if (existing.id == newSchedule.id) return@any false // Bỏ qua chính nó khi cập nhật
        if (existing.isDeleted) return@any false // Bỏ qua bản ghi đã xóa mềm

        val sameDay = existing.dayOfWeek == newSchedule.dayOfWeek
        if (!sameDay) return@any false // Khác thứ -> Không trùng

        // Kiểm tra khoảng ngày (Date Range Intersection)
        val dateOverlap = newSchedule.startDateMillis <= existing.endDateMillis && newSchedule.endDateMillis >= existing.startDateMillis
        if (!dateOverlap) return@any false // Khác giai đoạn -> Không trùng

        // Chuyển đổi linh hoạt giữa Packed Time (10730) và Tiết thuần (1) sang số Phút (Minutes of Day)
        val start1 = convertToMinutes(newSchedule.startPeriod, isStart = true)
        val end1 = convertToMinutes(newSchedule.endPeriod, isStart = false)
        
        val start2 = convertToMinutes(existing.startPeriod, isStart = true)
        val end2 = convertToMinutes(existing.endPeriod, isStart = false)

        // Công thức kiểm tra giao nhau (Overlap chặt chẽ: Không tính liền kề)
        // start1 < end2 AND start2 < end1
        start1 < end2 && start2 < end1
    }
}

/**
 * Quy đổi PackedTime hoặc Tiết thuần về Tổng số Phút kể từ nửa đêm.
 * Mảng giờ cứng: 1=06:45, 2=07:30, 3=08:25, 4=09:20, 5=10:05, 6=10:50, 7=12:30, 8=13:15, 9=14:10, 10=15:05, 11=15:50, 12=16:35, 13=18:00
 * Mảng giờ kết thúc: 1=07:30, 2=08:15, 3=09:10, 4=10:05, 5=10:50, 6=11:35, 7=13:15, 8=14:00, 9=14:55, 10=15:50, 11=16:35, 12=17:20, 13=20:15
 */
internal fun convertToMinutes(periodValue: Int, isStart: Boolean): Int {
    if (periodValue >= 10000) {
        // Đây là dạng Packed Time: (Tiết * 10000) + (Giờ * 100) + Phút
        val hour = (periodValue % 10000) / 100
        val minute = periodValue % 100
        return hour * 60 + minute
    } else {
        // Đây là dạng Tiết thuần (Ví dụ: Tiết 1, 2, 3) từ dữ liệu cũ
        val idx = (periodValue - 1).coerceIn(0, 12)
        if (isStart) {
            val startTimes = listOf("06:45", "07:30", "08:25", "09:20", "10:05", "10:50", "12:30", "13:15", "14:10", "15:05", "15:50", "16:35", "18:00")
            val parts = startTimes[idx].split(":")
            return parts[0].toInt() * 60 + parts[1].toInt()
        } else {
            val endTimes = listOf("07:30", "08:15", "09:10", "10:05", "10:50", "11:35", "13:15", "14:00", "14:55", "15:50", "16:35", "17:20", "20:15")
            val parts = endTimes[idx].split(":")
            return parts[0].toInt() * 60 + parts[1].toInt()
        }
    }
}
