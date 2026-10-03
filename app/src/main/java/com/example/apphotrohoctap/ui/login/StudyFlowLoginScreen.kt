package com.example.apphotrohoctap.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.apphotrohoctap.R
import coil.compose.AsyncImage

private val PageBackground = Color(0xFFFCF8FF)
private val BrandBlue = Color(0xFF4143D5)
private val Ink = Color(0xFF1A1A2A)
private val SecondaryInk = Color(0xFF464555)
private val MutedInk = Color(0xFF767586)
private val FieldBackground = Color(0xFFF5F2FF)
private val DividerColor = Color(0xFFE3E0F7)

@Composable
fun StudyFlowLoginScreen(onRegister: () -> Unit = {}) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberLogin by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
    ) {
        AmbientGlows()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 179.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BrandHeader()
            Spacer(Modifier.height(24.dp))
            LoginCard(
                email = email,
                onEmailChange = { email = it },
                password = password,
                onPasswordChange = { password = it },
                passwordVisible = passwordVisible,
                onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
                rememberLogin = rememberLogin,
                onRememberLoginChange = { rememberLogin = !rememberLogin }
            )
            Spacer(Modifier.height(24.dp))
            RegistrationPrompt(onRegister)
        }
    }
}

@Composable
private fun BoxScope.AmbientGlows() {
    Box(
        modifier = Modifier
            .offset(x = 35.dp, y = (-40).dp)
            .size(256.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x66E1E0FF), Color.Transparent)
                ),
                shape = CircleShape
            )
    )
    Box(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(x = 48.dp, y = 80.dp)
            .size(176.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x99E8E6FC), Color.Transparent)
                ),
                shape = CircleShape
            )
    )
}

@Composable
private fun BrandHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(64.dp)) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(R.drawable.studyflow_logo),
                    contentDescription = "StudyFlow",
                    modifier = Modifier.padding(8.dp).clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 4.dp, y = 4.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(BrandBlue),
                contentAlignment = Alignment.Center
            ) {
                FigmaIcon("brand_badge.svg", Modifier.width(11.dp).height(9.dp), "")
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = "StudyFlow",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 24.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.6).sp
            ),
            color = Ink
        )
        Text(
            text = "Đăng nhập vào tài khoản của bạn",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
            color = SecondaryInk
        )
    }
}

@Composable
private fun LoginCard(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
    rememberLogin: Boolean,
    onRememberLoginChange: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LoginInput(
                label = "Email sinh viên / Mã số SV",
                hint = "Email hoặc MSSV",
                icon = "email.svg",
                iconModifier = Modifier.size(13.333.dp),
                value = email,
                onValueChange = onEmailChange,
                keyboardType = KeyboardType.Email
            )
            LoginInput(
                label = "Mật khẩu",
                hint = "Mật khẩu",
                icon = "lock.svg",
                iconModifier = Modifier.width(10.667.dp).height(14.dp),
                value = password,
                onValueChange = onPasswordChange,
                keyboardType = KeyboardType.Password,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingContent = {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable(onClick = onPasswordVisibilityChange),
                        contentAlignment = Alignment.Center
                    ) {
                        FigmaIcon("eye.svg", Modifier.width(18.333.dp).height(12.5.dp), "Hiển thị mật khẩu")
                    }
                }
            )
            RememberAndForgot(
                checked = rememberLogin,
                onCheckedChange = onRememberLoginChange
            )
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
            ) {
                Text("Đăng nhập", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                Spacer(Modifier.width(8.dp))
                FigmaIcon("arrow.svg", Modifier.size(13.333.dp), "")
            }
            SocialOptions()
        }
    }
}

@Composable
private fun LoginInput(
    label: String,
    hint: String,
    icon: String,
    iconModifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FigmaIcon(icon, iconModifier, "")
            Spacer(Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp, lineHeight = 16.sp),
                color = SecondaryInk
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(FieldBackground),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, end = if (trailingContent == null) 16.dp else 0.dp),
                singleLine = true,
                textStyle = TextStyle(fontSize = 14.sp, color = SecondaryInk),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                visualTransformation = visualTransformation,
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(hint, fontSize = 14.sp, color = MutedInk)
                        }
                        innerTextField()
                    }
                }
            )
            trailingContent?.invoke()
        }
    }
}

@Composable
private fun RememberAndForgot(checked: Boolean, onCheckedChange: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(42.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.clickable(onClick = onCheckedChange),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (checked) BrandBlue else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                if (checked) FigmaIcon("check.svg", Modifier.width(10.442.dp).height(7.963.dp), "")
            }
            Spacer(Modifier.width(8.dp))
            Text("Ghi nhớ đăng nhập", fontSize = 14.sp, color = SecondaryInk)
        }
        Text(
            text = "Quên mật khẩu?",
            modifier = Modifier.clickable { },
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
            color = BrandBlue
        )
    }
}

@Composable
private fun SocialDivider() {
    Row(
        modifier = Modifier.fillMaxWidth().height(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.weight(1f).height(1.dp).background(DividerColor))
        Text("Hoặc", modifier = Modifier.padding(horizontal = 12.dp), fontSize = 12.sp, color = Color(0xFF555962))
        Box(Modifier.weight(1f).height(1.dp).background(DividerColor))
    }
}

@Composable
private fun SocialOptions() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(Modifier.height(12.dp))
        SocialDivider()
        Spacer(Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SocialButton("google.svg", "Tiếp tục với Google")
            SocialButton("university.svg", "Cổng trường Đại học / SSO")
        }
    }
}

@Composable
private fun SocialButton(icon: String, label: String) {
    Surface(
        onClick = {},
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(12.dp),
        color = FieldBackground
    ) {
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            FigmaIcon(icon, Modifier.size(if (icon == "university.svg") 15.dp else 16.dp), "")
            Spacer(Modifier.width(10.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Ink)
        }
    }
}

@Composable
private fun RegistrationPrompt(onRegister: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Chưa có tài khoản?", fontSize = 14.sp, color = SecondaryInk)
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Đăng ký ngay",
            modifier = Modifier.clickable(onClick = onRegister),
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
            color = BrandBlue
        )
    }
}

@Composable
private fun FigmaIcon(assetName: String, modifier: Modifier, contentDescription: String) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data("file:///android_asset/studyflow/$assetName")
            .decoderFactory(SvgDecoder.Factory())
            .build(),
        contentDescription = contentDescription.takeIf { it.isNotBlank() },
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

@Preview(showBackground = true)
@Composable
private fun StudyFlowLoginScreenPreview() {
    StudyFlowLoginScreen()
}
