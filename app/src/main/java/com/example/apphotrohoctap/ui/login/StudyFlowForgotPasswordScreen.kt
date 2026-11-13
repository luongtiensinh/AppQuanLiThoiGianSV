package com.example.apphotrohoctap.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.apphotrohoctap.data.auth.AuthRepository
import com.example.apphotrohoctap.data.auth.AuthResult

@Composable
fun StudyFlowForgotPasswordScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFCF8FF))
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = onBack,
                modifier = Modifier.size(44.dp),
                color = Color.Transparent,
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data("file:///android_asset/studyflow/back.svg")
                            .decoderFactory(SvgDecoder.Factory())
                            .build(),
                        contentDescription = "Quay lại",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Quên Mật Khẩu",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A2A)
            )
        }
        
        Spacer(Modifier.height(32.dp))
        
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Khôi phục tài khoản",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2A)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Nhập email đã đăng ký của bạn. Chúng tôi sẽ gửi một liên kết để đặt lại mật khẩu.",
                fontSize = 14.sp,
                color = Color(0xFF464555),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(Modifier.height(32.dp))
            
            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    message = null
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Email của bạn", fontSize = 14.sp) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF4143D5),
                    unfocusedBorderColor = Color.Transparent
                )
            )
            
            if (message != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = message!!,
                    fontSize = 13.sp,
                    color = if (isSuccess) Color(0xFF10B981) else Color(0xFFBA1A1A)
                )
            }
            
            Spacer(Modifier.height(24.dp))
            
            Button(
                onClick = {
                    if (email.isBlank()) {
                        message = "Email không được để trống"
                        isSuccess = false
                        return@Button
                    }
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        message = "Định dạng email không hợp lệ"
                        isSuccess = false
                        return@Button
                    }
                    
                    if (isLoading) return@Button
                    isLoading = true
                    
                    coroutineScope.launch {
                        val repo = AuthRepository(context)
                        when (val result = repo.sendPasswordResetEmail(email.trim())) {
                            is AuthResult.Success -> {
                                isSuccess = true
                                message = "Liên kết đặt lại mật khẩu đã được gửi đến email của bạn!"
                            }
                            is AuthResult.Error -> {
                                isSuccess = false
                                message = result.message
                            }
                            else -> {}
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4143D5))
            ) {
                Text(
                    text = if (isLoading) "Đang gửi..." else "Gửi liên kết",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}