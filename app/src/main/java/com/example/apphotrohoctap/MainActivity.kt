package com.example.apphotrohoctap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.apphotrohoctap.ui.navigation.Screen
import androidx.navigation.compose.rememberNavController
import com.example.apphotrohoctap.ui.navigation.StudyFlowNavGraph
import com.example.apphotrohoctap.ui.navigation.BottomNavTab
import com.example.apphotrohoctap.ui.theme.AppHoTroHocTapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val sessionManager = com.example.apphotrohoctap.data.SessionManager(this)
        val startDest = if (sessionManager.isLoggedIn()) Screen.Home.route else Screen.Login.route
        
        setContent {
            AppHoTroHocTapTheme {
                val navController = rememberNavController()

                val navToTab: (com.example.apphotrohoctap.ui.navigation.BottomNavTab) -> Unit = { tab ->
                    val route = when (tab) {
                        com.example.apphotrohoctap.ui.navigation.BottomNavTab.HOME -> Screen.Home.route
                        com.example.apphotrohoctap.ui.navigation.BottomNavTab.SCHEDULE -> Screen.Schedule.route
                        com.example.apphotrohoctap.ui.navigation.BottomNavTab.TASKS -> Screen.Tasks.route
                        com.example.apphotrohoctap.ui.navigation.BottomNavTab.NOTES -> Screen.Notes.route
                        com.example.apphotrohoctap.ui.navigation.BottomNavTab.GRADES -> Screen.Semesters.route
                    }
                    navController.navigate(route) {
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
                
                val pomodoroViewModel: com.example.apphotrohoctap.ui.pomodoro.PomodoroViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

                StudyFlowNavGraph(
                    navController = navController,
                    startDestination = startDest,
                    pomodoroViewModel = pomodoroViewModel,
                    navToTab = navToTab
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppHoTroHocTapTheme {
        Greeting("Android")
    }
}
