package com.example.apphotrohoctap.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.apphotrohoctap.R
import com.example.apphotrohoctap.data.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun StudyFlowSplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        // Tạo khoảng trễ nhỏ (500ms) để người dùng kịp ngắm logo ứng dụng (Branding effect)
        kotlinx.coroutines.delay(500)
        
        // Chuyển việc giải mã SharedPreferences (isLoggedIn) sang luồng I/O
        val isLoggedIn = withContext(Dispatchers.IO) {
            val sessionManager = SessionManager(context)
            sessionManager.isLoggedIn()
        }
        
        // Điều hướng (LaunchedEffect mặc định chạy trên Main Dispatcher nên không cần switch về Main)
        if (isLoggedIn) {
            onNavigateToHome()
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFCF8FF)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Image(
                painter = painterResource(R.drawable.studyflow_logo),
                contentDescription = "StudyFlow Logo",
                modifier = Modifier.fillMaxSize().padding(16.dp).clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}
