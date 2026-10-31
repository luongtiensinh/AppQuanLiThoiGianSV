package com.example.apphotrohoctap.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphotrohoctap.data.GradeRepository
import com.example.apphotrohoctap.data.ScheduleRepository
import com.example.apphotrohoctap.data.TaskRepository
import com.example.apphotrohoctap.data.toUiModel
import com.example.apphotrohoctap.ui.analytics.GamificationEngine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

internal class HomeViewModel(
    private val taskRepository: TaskRepository,
    private val scheduleRepository: ScheduleRepository,
    private val gradeRepository: GradeRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        taskRepository.getTasks(),
        scheduleRepository.getAllSchedules(),
        gradeRepository.getAllSubjectsWithGrades()
    ) { tasksList, scheduleList, gradeList ->
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance().apply { timeInMillis = now }

        val currentYear = cal.get(Calendar.YEAR)
        val currentDayInt = if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) 7 else cal.get(Calendar.DAY_OF_WEEK) - 1
        val currentWeek = cal.get(Calendar.WEEK_OF_YEAR)

        var tasksToday = 0
        var tasksUrgent = 0
        var deadlinesThisWeek = 0
        var tasksDone = 0
        var tasksTotalThisWeek = 0
        var hasLateNightTask = false
        var hasEarlyBirdTask = false

        val upcomingDeadlines = mutableListOf<DeadlineTask>()

        // Analyze Tasks
        tasksList.forEach { taskWithSub ->
            val uiModel = taskWithSub.toUiModel(now)
            val isDone = uiModel.completed
            
            val taskCal = Calendar.getInstance().apply { timeInMillis = taskWithSub.task.dueAtMillis }
            val isSameWeek = taskCal.get(Calendar.YEAR) == currentYear && taskCal.get(Calendar.WEEK_OF_YEAR) == currentWeek
            
            if (isSameWeek) {
                tasksTotalThisWeek++
                if (isDone) tasksDone++
                else deadlinesThisWeek++
            }
            
            if (isDone) {
                val hour = taskCal.get(Calendar.HOUR_OF_DAY)
                if (GamificationEngine.isLateNightHour(hour)) {
                    hasLateNightTask = true
                }
                if (GamificationEngine.isEarlyBirdHour(hour)) {
                    hasEarlyBirdTask = true
                }
            } else {
                if (uiModel.group == "Hôm nay") {
                    tasksToday++
                    if (uiModel.urgency?.contains("Gấp") == true) tasksUrgent++
                }

                if (taskWithSub.task.category == "deadline") {
                    val progressFloat = if (uiModel.subtasks.isEmpty()) 0f else uiModel.subtasks.count { it.isCompleted }.toFloat() / uiModel.subtasks.size
                    val isUrgent = uiModel.urgency?.contains("Gấp") == true || uiModel.urgency == "Quá hạn"
                    
                    upcomingDeadlines.add(
                        DeadlineTask(
                            id = uiModel.id,
                            subject = uiModel.subject,
                            urgency = uiModel.urgency ?: "Sắp đến",
                            title = uiModel.title,
                            dueDate = uiModel.due,
                            dueAtMillis = taskWithSub.task.dueAtMillis,
                            progress = progressFloat,
                            progressLabel = "${(progressFloat * 100).toInt()}%",
                            accent = uiModel.accent,
                            isUrgent = isUrgent
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

        val hasFullSchedule = scheduleList.map { it.dayOfWeek }.distinct().size >= 5

        HomeUiState(
            tasksToday = tasksToday,
            tasksUrgent = tasksUrgent,
            deadlinesThisWeek = deadlinesThisWeek,
            tasksDone = tasksDone,
            tasksTotalThisWeek = tasksTotalThisWeek,
            todaySchedules = todaySchedules,
            upcomingDeadlines = upcomingDeadlines.sortedBy { it.dueAtMillis }.take(10),
            hasLateNightTask = hasLateNightTask,
            hasEarlyBirdTask = hasEarlyBirdTask,
            subjectCount = gradeList.size,
            hasFullSchedule = hasFullSchedule
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )
}
