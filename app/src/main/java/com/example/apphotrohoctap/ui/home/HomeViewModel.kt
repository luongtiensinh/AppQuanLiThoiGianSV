package com.example.apphotrohoctap.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphotrohoctap.data.ScheduleRepository
import com.example.apphotrohoctap.data.TaskRepository
import com.example.apphotrohoctap.data.toUiModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

internal class HomeViewModel(
    private val taskRepository: TaskRepository,
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        taskRepository.getTasks(),
        scheduleRepository.getAllSchedules()
    ) { tasksList, scheduleList ->
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance().apply { timeInMillis = now }

        val currentDayInt = if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) 7 else cal.get(Calendar.DAY_OF_WEEK) - 1
        val currentWeek = cal.get(Calendar.WEEK_OF_YEAR)

        var tasksToday = 0
        var tasksUrgent = 0
        var deadlinesThisWeek = 0
        var tasksDone = 0
        var tasksTotalThisWeek = 0

        val upcomingDeadlines = mutableListOf<DeadlineTask>()

        // Analyze Tasks
        tasksList.forEach { taskWithSub ->
            val uiModel = taskWithSub.toUiModel(now)
            val isDone = uiModel.completed
            
            val taskCal = Calendar.getInstance().apply { timeInMillis = taskWithSub.task.dueAtMillis }
            if (taskCal.get(Calendar.WEEK_OF_YEAR) == currentWeek) {
                tasksTotalThisWeek++
                if (isDone) tasksDone++
                else deadlinesThisWeek++
            }
            
            if (!isDone) {
                if (uiModel.group == "Hôm nay") {
                    tasksToday++
                    if (uiModel.urgency?.contains("Gấp") == true) tasksUrgent++
                }

                if (taskWithSub.task.category == "deadline") {
                    val progressFloat = if (uiModel.subtasks.isEmpty()) 0f else uiModel.subtasks.count { it.isCompleted }.toFloat() / uiModel.subtasks.size
                    upcomingDeadlines.add(
                        DeadlineTask(
                            id = uiModel.id,
                            subject = uiModel.subject,
                            urgency = uiModel.urgency ?: "Sắp đến",
                            title = uiModel.title,
                            dueDate = uiModel.due,
                            progress = progressFloat,
                            progressLabel = "${(progressFloat * 100).toInt()}%",
                            accent = uiModel.accent,
                            urgencyBackground = androidx.compose.ui.graphics.Color(0xFFFFDAD6),
                            urgencyColor = androidx.compose.ui.graphics.Color(0xFFBA1A1A),
                            timeColor = androidx.compose.ui.graphics.Color(0xFF464555)
                        )
                    )
                }
            }
        }

        // Format Schedules
        val todaySchedules = scheduleList
            .filter { it.dayOfWeek == currentDayInt }
            .map { it.toUiModel(now) }
            .sortedBy { it.time }

        HomeUiState(
            tasksToday = tasksToday,
            tasksUrgent = tasksUrgent,
            deadlinesThisWeek = deadlinesThisWeek,
            tasksDone = tasksDone,
            tasksTotalThisWeek = tasksTotalThisWeek,
            todaySchedules = todaySchedules,
            upcomingDeadlines = upcomingDeadlines.sortedBy { it.dueDate }.take(10) // Mocking sort
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )
}
