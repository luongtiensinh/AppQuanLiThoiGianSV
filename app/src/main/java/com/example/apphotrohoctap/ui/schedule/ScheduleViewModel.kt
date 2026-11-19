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
        if (schedule.endPeriod < schedule.startPeriod) {
            onError("Thời gian kết thúc phải lớn hơn hoặc bằng thời gian bắt đầu")
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
