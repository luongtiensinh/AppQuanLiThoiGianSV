package com.example.apphotrohoctap.ui.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.selection.toggleable
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest

private val RegisterBackground = Color(0xFFFCF8FF)
private val RegisterBrandBlue = Color(0xFF4143D5)
private val RegisterInk = Color(0xFF1A1A2A)
private val RegisterSecondaryInk = Color(0xFF464555)
private val RegisterMutedInk = Color(0xFF767586)
private val RegisterDivider = Color(0xFFE8E6FC)

@Composable
fun StudyFlowRegisterScreen(
    onBack: () -> Unit,
    onSignIn: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var agreedToTerms by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RegisterBackground)
            .statusBarsPadding()
    ) {
        RegisterTopBar(onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 24.dp)
        ) {
            RegisterHeading()
            Spacer(Modifier.height(20.dp))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                RegisterInput(
                    label = "Họ và tên",
                    hint = "Nguyễn Văn A",
                    icon = "register_person.svg",
                    iconModifier = Modifier.size(13.333.dp),
                    value = fullName,
                    onValueChange = { fullName = it },
                    keyboardType = KeyboardType.Text
                )
                RegisterInput(
                    label = "Email",
                    hint = "name@school.edu.vn",
                    icon = "register_email.svg",
                    iconModifier = Modifier.width(16.667.dp).height(13.333.dp),
                    value = email,
                    onValueChange = { email = it },
                    keyboardType = KeyboardType.Email
                )
                RegisterInput(
                    label = "Mật khẩu",
                    hint = "Tối thiểu 8 ký tự",
                    icon = "register_lock.svg",
                    iconModifier = Modifier.width(13.333.dp).height(17.5.dp),
                    value = password,
                    onValueChange = { password = it },
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingContent = {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { passwordVisible = !passwordVisible },
                            contentAlignment = Alignment.Center
                        ) {
                            RegisterIcon(
                                "password_hidden.svg",
                                Modifier.width(18.333.dp).height(16.5.dp),
                                "Ẩn hoặc hiện mật khẩu"
                            )
                        }
                    }
                )
                AgreementRow(
                    checked = agreedToTerms,
                    onCheckedChange = { agreedToTerms = it }
                )
                Button(
                    onClick = {
                        if (agreedToTerms && fullName.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                            onSignIn()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp).padding(top = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RegisterBrandBlue),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Tạo tài khoản",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    RegisterIcon("arrow.svg", Modifier.size(13.333.dp), "")
                }
            }

            Spacer(Modifier.height(16.dp))
            RegisterDivider()
            Spacer(Modifier.height(16.dp))
            GoogleRegisterButton()
            Spacer(Modifier.height(24.dp))
            SignInPrompt(onSignIn)
        }
    }
}

@Composable
private fun RegisterTopBar(onBack: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(64.dp),
        color = Color(0xD9FCF8FF),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                RegisterIcon("back.svg", Modifier.size(16.dp), "Quay lại")
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Đăng Ký",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    letterSpacing = (-0.45).sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = RegisterInk
            )
        }
    }
}

@Composable
private fun RegisterHeading() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Tạo tài khoản mới",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 24.sp,
                lineHeight = 32.sp,
                letterSpacing = (-0.6).sp,
                fontWeight = FontWeight.Bold
            ),
            color = RegisterInk
        )
        Text(
            text = "Bắt đầu quản lý việc học và kiểm soát deadline\nthông minh.",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
            color = RegisterSecondaryInk
        )
    }
}

@Composable
private fun RegisterInput(
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
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp, lineHeight = 16.sp),
            color = RegisterInk
        )
        Surface(
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RegisterIcon(icon, iconModifier, "")
                Spacer(Modifier.width(12.dp))
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = if (trailingContent == null) 16.dp else 0.dp),
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 14.sp, color = RegisterSecondaryInk),
                    cursorBrush = SolidColor(RegisterBrandBlue),
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    visualTransformation = visualTransformation,
                    decorationBox = { innerTextField ->
                        Box {
                            if (value.isEmpty()) {
                                Text(hint, fontSize = 14.sp, color = RegisterMutedInk)
                            }
                            innerTextField()
                        }
                    }
                )
                trailingContent?.invoke()
            }
        }
    }
}

@Composable
private fun AgreementRow(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val checkboxShape = RoundedCornerShape(2.5.dp)
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(checkboxShape)
                .background(if (checked) RegisterBrandBlue else Color.White)
                .border(1.dp, if (checked) RegisterBrandBlue else RegisterMutedInk, checkboxShape)
                .toggleable(checked, role = Role.Checkbox, onValueChange = onCheckedChange),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Canvas(Modifier.size(10.dp)) {
                    val check = Path().apply {
                        moveTo(size.width * 0.12f, size.height * 0.52f)
                        lineTo(size.width * 0.4f, size.height * 0.8f)
                        lineTo(size.width * 0.9f, size.height * 0.2f)
                    }
                    drawPath(
                        path = check,
                        color = Color.White,
                        style = Stroke(
                            width = 1.5.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
        }
        Spacer(Modifier.width(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Tôi đồng ý với ",
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = RegisterSecondaryInk
            )
            Text(
                text = "Điều khoản & Chính sách",
                modifier = Modifier.clickable { },
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = RegisterBrandBlue
            )
        }
    }
}

@Composable
private fun RegisterDivider() {
    Row(
        modifier = Modifier.fillMaxWidth().height(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.weight(1f).height(1.dp).background(RegisterDivider))
        Text(
            text = "Hoặc",
            modifier = Modifier.padding(horizontal = 12.dp),
            fontSize = 10.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF555962)
        )
        Box(Modifier.weight(1f).height(1.dp).background(RegisterDivider))
    }
}

@Composable
private fun GoogleRegisterButton() {
    Surface(
        onClick = {},
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RegisterIcon("register_google.svg", Modifier.size(20.dp), "")
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Đăng ký bằng Google",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = RegisterInk
            )
        }
    }
}

@Composable
private fun SignInPrompt(onSignIn: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Đã có tài khoản?", fontSize = 14.sp, lineHeight = 20.sp, color = RegisterSecondaryInk)
        Spacer(Modifier.width(4.dp))
        Text(
            text = "Đăng nhập",
            modifier = Modifier.clickable(onClick = onSignIn),
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = RegisterBrandBlue
        )
    }
}

@Composable
private fun RegisterIcon(assetName: String, modifier: Modifier, contentDescription: String) {
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
private fun StudyFlowRegisterScreenPreview() {
    StudyFlowRegisterScreen(onBack = {}, onSignIn = {})
}
