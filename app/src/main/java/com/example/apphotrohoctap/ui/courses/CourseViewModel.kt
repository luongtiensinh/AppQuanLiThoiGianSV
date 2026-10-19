package com.example.apphotrohoctap.ui.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphotrohoctap.data.NoteRepository
import com.example.apphotrohoctap.data.ScheduleRepository
import com.example.apphotrohoctap.data.TaskRepository
import com.example.apphotrohoctap.data.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class CourseViewModel(
    private val taskRepository: TaskRepository,
    private val scheduleRepository: ScheduleRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val courseNameFlow = MutableStateFlow("")

    fun setCourseName(name: String) {
        courseNameFlow.value = name.trim()
    }

    // Dùng combine để gộp 3 luồng dữ liệu (Schedules, Tasks, Notes) dựa trên tên môn học
    // Giải thích cho báo cáo: Thay vì phải fetch dữ liệu độc lập cho 3 tab (Lịch học, Bài tập, Ghi chú) gây 
    // lãng phí tài nguyên và rủi ro bất đồng bộ, việc dùng combine() giúp quy tụ tất cả về 1 State duy nhất (CourseUiState).
    // Bất kỳ sự thay đổi nào ở 1 trong 3 bảng cũng sẽ tự động trigger Compose render lại đúng dữ liệu mới nhất.
    // * Hạn chế: Việc lấy tên môn (String) làm khoá phụ (Pseudo-foreign key) để liên kết 3 bảng giúp tái sử dụng DB có sẵn
    // mà không cần tạo bảng Courses. Tuy nhiên, nếu user nhập sai chính tả (vd: "CSDL Nâng cao" vs "CSDL nâng cao"), 
    // chúng sẽ bị coi là 2 môn khác nhau. Để triệt để, sau này nên tạo bảng Course riêng biệt.

    val uiState: StateFlow<CourseUiState> = courseNameFlow.flatMapLatest { name ->
        combine(
            taskRepository.getTasksBySubject(name),
            scheduleRepository.getSchedulesBySubject(name),
            noteRepository.getNotesByCourse(name)
        ) { tasksList, scheduleList, noteList ->
            val now = System.currentTimeMillis()
            
            val mappedTasks = tasksList.map { it.toUiModel(now) }
            val mappedSchedules = scheduleList.map { it.toUiModel(now) }

            val firstSchedule = scheduleList.firstOrNull()

            CourseUiState(
                courseName = name,
                courseCode = firstSchedule?.courseCode?.takeIf { it.isNotBlank() } ?: "Chưa có mã",
                lecturer = firstSchedule?.lecturer?.takeIf { it.isNotBlank() } ?: "Chưa phân công",
                room = firstSchedule?.room?.takeIf { it.isNotBlank() } ?: "Chưa xếp phòng",
                tasksTotal = mappedTasks.size,
                tasksDone = mappedTasks.count { it.completed },
                schedules = mappedSchedules,
                tasks = mappedTasks,
                notes = noteList
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CourseUiState()
    )

    fun toggleTaskCompletion(taskIdStr: String) {
        viewModelScope.launch {
            val taskId = taskIdStr.toLongOrNull() ?: return@launch
            val taskWithSubtasks = taskRepository.getTaskById(taskId)
            if (taskWithSubtasks != null) {
                taskRepository.updateTask(taskWithSubtasks.task.copy(isCompleted = !taskWithSubtasks.task.isCompleted))
            }
        }
    }
}
