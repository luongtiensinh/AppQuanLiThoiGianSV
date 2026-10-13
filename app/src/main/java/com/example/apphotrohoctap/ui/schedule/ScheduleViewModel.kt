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
            onError("Tiết kết thúc phải lớn hơn hoặc bằng tiết bắt đầu")
            return
        }

        viewModelScope.launch {
            repository.addSchedule(schedule)
            onSuccess()
        }
    }

    fun deleteSchedule(schedule: ScheduleEntity) {
        viewModelScope.launch {
            repository.deleteSchedule(schedule)
        }
    }
}
