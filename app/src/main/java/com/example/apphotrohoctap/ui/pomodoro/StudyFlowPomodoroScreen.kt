package com.example.apphotrohoctap.ui.pomodoro

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apphotrohoctap.ui.tasks.TasksViewModel
import com.example.apphotrohoctap.ui.tasks.TasksViewModelFactory
import kotlinx.coroutines.flow.collectLatest

@Composable
fun StudyFlowPomodoroScreen(
    pomodoroViewModel: PomodoroViewModel,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    
    // Cấp Context cho ViewModel để đặt lịch WorkManager
    LaunchedEffect(Unit) {
        pomodoroViewModel.setContext(context)
    }

    val tasksViewModel: TasksViewModel = viewModel(factory = TasksViewModelFactory(context))

    val uiState by pomodoroViewModel.uiState.collectAsStateWithLifecycle()
    val tasks by tasksViewModel.tasks.collectAsStateWithLifecycle()
    val incompleteTasks = remember(tasks) { tasks.filter { !it.completed } }

    var showFinishedDialog by remember { mutableStateOf<PomodoroMode?>(null) }

    // Giữ màn hình luôn sáng khi đồng hồ đang chạy
    DisposableEffect(uiState.status) {
        if (uiState.status == PomodoroStatus.RUNNING) {
            view.keepScreenOn = true
        } else {
            view.keepScreenOn = false
        }
        onDispose {
            view.keepScreenOn = false
        }
    }

    // Lắng nghe sự kiện hết giờ
    LaunchedEffect(pomodoroViewModel) {
        pomodoroViewModel.sessionFinishedEvent.collectLatest { mode ->
            // Rung nhẹ khi hết giờ
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (vibrator != null && vibrator.hasVibrator()) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    val effect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
                    vibrator.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(500)
                }
            }
            showFinishedDialog = mode
        }
    }

    BackHandler(onBack = onClose)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFCF8FF))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onClose,
                    modifier = Modifier.size(40.dp),
                    color = Color.Transparent,
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("×", fontSize = 28.sp, color = Color(0xFF1A1A2A))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ModeSelector(
                    currentMode = uiState.mode,
                    onModeSelect = { pomodoroViewModel.selectMode(it) }
                )
                
                Spacer(Modifier.height(48.dp))

                PomodoroTimerArc(
                    remainingMillis = uiState.remainingMillis,
                    totalMillis = uiState.totalMillis,
                    mode = uiState.mode,
                    modifier = Modifier.size(280.dp)
                )

                Spacer(Modifier.height(48.dp))

                TaskSelector(
                    linkedTaskTitle = uiState.linkedTaskTitle,
                    incompleteTasks = incompleteTasks,
                    onTaskSelect = { pomodoroViewModel.setLinkedTask(it) }
                )

                Spacer(Modifier.height(16.dp))
                CycleDots(completedCount = uiState.completedFocusSessions)
            }

            Spacer(Modifier.height(32.dp))

            PomodoroControls(
                status = uiState.status,
                onStart = { pomodoroViewModel.start() },
                onPause = { pomodoroViewModel.pause() },
                onSkip = { pomodoroViewModel.skip() },
                onReset = { pomodoroViewModel.reset() }
            )

            Spacer(Modifier.height(48.dp))
        }

        // Dialog khi hết giờ
        if (showFinishedDialog != null) {
            AlertDialog(
                onDismissRequest = { showFinishedDialog = null },
                title = { 
                    Text(
                        if (showFinishedDialog == PomodoroMode.FOCUS) "Hoàn thành phiên tập trung!" 
                        else "Hết giờ nghỉ ngơi!",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                text = {
                    Text(
                        if (showFinishedDialog == PomodoroMode.FOCUS) 
                            "Tuyệt vời! Bạn đã hoàn thành một phiên tập trung. Hãy nghỉ ngơi một chút nhé."
                        else 
                            "Đã hết giờ nghỉ ngơi. Sẵn sàng cho phiên tập trung tiếp theo chưa?",
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showFinishedDialog = null }) {
                        Text("Để sau", color = Color(0xFF767586))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { 
                            showFinishedDialog = null
                            pomodoroViewModel.start() 
                        }
                    ) {
                        Text(
                            if (showFinishedDialog == PomodoroMode.FOCUS) "Bắt đầu nghỉ" 
                            else "Bắt đầu tập trung",
                            color = Color(0xFF4143D5),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StudyFlowPomodoroScreenPreview() {
    StudyFlowPomodoroScreen(
        pomodoroViewModel = PomodoroViewModel(),
        onClose = {}
    )
}
