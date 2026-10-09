package com.example.apphotrohoctap.ui.tasks

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest

@Composable
internal fun AddTaskHeader(onBack: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().statusBarsPadding(), color = AddTaskBackground.copy(alpha = 0.94f), shadowElevation = 1.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(onClick = onBack, modifier = Modifier.size(40.dp), color = Color.Transparent, shape = CircleShape) {
                Box(contentAlignment = Alignment.Center) { AddTaskIcon("back.svg", Modifier.size(14.dp), "Đóng") }
            }
            Text(
                "Thêm Việc Mới",
                modifier = Modifier.weight(1f).padding(end = 40.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = AddTaskInk
            )
        }
    }
}

@Composable
internal fun GuidanceBanner() {
    Surface(color = Color(0xFFE8E6FC), shape = RoundedCornerShape(20.dp), shadowElevation = 2.dp) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(color = AddTaskBright, shape = RoundedCornerShape(12.dp)) {
                Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) { AddTaskIcon("home_goal.svg", Modifier.size(20.dp), "") }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Tạo nhiệm vụ học kỳ mới", fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold, color = AddTaskInk)
                Text(
                    "Lên kế hoạch thông minh để không bỏ lỡ hạn nộp bài hay buổi thảo luận quan trọng nhé!",
                    modifier = Modifier.padding(top = 2.dp).widthIn(max = 250.dp),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = AddTaskSecondary
                )
            }
        }
    }
}


@Composable
internal fun SaveFooter(enabled: Boolean, onSave: () -> Unit) {
    Surface(color = AddTaskBackground.copy(alpha = 0.97f), shadowElevation = 4.dp) {
        Box(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp)) {
            Surface(
                onClick = onSave,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                color = AddTaskBrand,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 4.dp
            ) {
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text("✓", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Lưu", fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
    }
}

@Composable
internal fun FormCard(spacing: androidx.compose.ui.unit.Dp = 4.dp, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(spacing), content = { content() })
    }
}

@Composable
internal fun FieldLabel(text: String) {
    Text(text, fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold, color = AddTaskInk)
}

@Composable
internal fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    trailingText: String? = null,
    onTrailingClick: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, fontSize = 13.sp, color = AddTaskMuted) },
        singleLine = true,
        trailingIcon = if (trailingText != null && onTrailingClick != null) {
            { Surface(onClick = onTrailingClick, color = Color.Transparent, shape = CircleShape) { Text(trailingText, Modifier.padding(8.dp), color = AddTaskSecondary) } }
        } else null,
        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, color = AddTaskInk),
        colors = formFieldColors(),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
internal fun formFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color.Transparent,
    unfocusedBorderColor = Color.Transparent,
    focusedContainerColor = AddTaskField,
    unfocusedContainerColor = AddTaskField,
    cursorColor = AddTaskBrand
)

@Composable
internal fun ChoicePill(label: String, selected: Boolean, onClick: () -> Unit, compact: Boolean = false) {
    Surface(
        onClick = onClick,
        color = if (selected) AddTaskBright else AddTaskChip,
        shape = CircleShape
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = if (compact) 12.dp else 14.dp, vertical = if (compact) 7.dp else 8.dp),
            fontSize = if (compact) 10.sp else 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) Color.White else AddTaskSecondary,
            maxLines = 1
        )
    }
}

@Composable
internal fun ActionChip(label: String, icon: String, onClick: () -> Unit) {
    Surface(onClick = onClick, color = AddTaskChip, shape = RoundedCornerShape(8.dp)) {
        Row(Modifier.padding(horizontal = 10.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            AddTaskIcon(icon, Modifier.size(12.dp), "")
            Text(label, fontSize = 9.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = AddTaskSecondary)
        }
    }
}

@Composable
internal fun ChoiceDialog(title: String, choices: List<String>, onDismiss: () -> Unit, onChoice: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.SemiBold) },
        text = {
            Column {
                choices.forEach { choice ->
                    TextButton(onClick = { onChoice(choice) }, modifier = Modifier.fillMaxWidth()) {
                        Text(choice, modifier = Modifier.fillMaxWidth(), color = AddTaskInk)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Đóng") } }
    )
}

@Composable
internal fun AddTaskIcon(assetName: String, modifier: Modifier, contentDescription: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data("file:///android_asset/studyflow/$assetName").decoderFactory(SvgDecoder.Factory()).build(),
        contentDescription = contentDescription.takeIf(String::isNotBlank),
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}
