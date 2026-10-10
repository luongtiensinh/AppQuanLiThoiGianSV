package com.example.apphotrohoctap.ui.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest

private val NotesBackground = Color(0xFFFCF8FF)
private val NotesInk = Color(0xFF1A1A2A)
private val NotesText = Color(0xFF464555)
private val NotesMuted = Color(0xFF767586)
private val NotesBrand = Color(0xFF4143D5)
private val NotesBright = Color(0xFF5B5FEF)
private val NotesChip = Color(0xFFEFECFF)

private data class NoteItem(
    val subject: String,
    val title: String,
    val preview: String,
    val date: String,
    val tag: String,
    val accent: Color,
    val subjectBackground: Color
)

private val sampleNotes = listOf(
    NoteItem("Lập trình Di động", "Jetpack Compose: State Hoisting & ViewModel", "Nguyên tắc: Đẩy state lên component cha và truyền event xuống component con.", "Hôm qua, 16:45", "Đồ án", Color(0xFFF97316), Color(0xFFFFF7ED)),
    NoteItem("Điện toán đám mây", "AWS Lambda vs EC2: So sánh kiến trúc Serverless", "Lambda tính tiền theo thời gian thực thi (ms) và số request. Thích hợp cho tác vụ ngắn.", "22 Th04", "Ôn thi", Color(0xFF0284C7), Color(0xFFF0F9FF)),
    NoteItem("Tiếng Anh CNTT", "Từ vựng Unit 5: Cloud Networking & Security", "Latency (độ trễ), Throughput (thông lượng), Firewall (tường lửa).", "19 Th04", "Flashcard", Color(0xFF7C3AED), Color(0xFFF5F3FF)),
    NoteItem("CSDL Nâng cao", "Tối ưu câu lệnh SQL với B-Tree Index", "Tránh dùng SELECT *, tận dụng Covering Index. Phân tích truy vấn bằng Execution Plan.", "15 Th04", "Bài tập 8", NotesBrand, NotesChip),
    NoteItem("Lập trình Di động", "Ghi chú họp nhóm đồ án Sprint 3", "Phân công: Minh làm UI màn hình thanh toán, Nam kết nối Retrofit API, Anh viết Unit test cho Repository.", "12 Th04", "Nhóm 4", Color(0xFFD97706), Color(0xFFFEF3C7))
)

private data class SubjectFilter(val name: String, val count: Int, val color: Color? = null)

@Composable
fun StudyFlowNotesScreen(onTabSelected: (String) -> Unit = {}, onCreateNote: () -> Unit = {}, onEditNote: () -> Unit = {}) {
    val filters = listOf(
        SubjectFilter("Tất cả", 16),
        SubjectFilter("CSDL Nâng cao", 5, NotesBrand),
        SubjectFilter("Lập trình Di động", 4, Color(0xFFF97316)),
        SubjectFilter("Điện toán đám mây", 3, Color(0xFF0284C7)),
        SubjectFilter("Tiếng Anh CNTT", 2, Color(0xFF7C3AED)),
        SubjectFilter("Khác", 2)
    )
    var selectedSubject by remember { mutableStateOf("Tất cả") }
    var searchQuery by remember { mutableStateOf("") }
    var isGrid by remember { mutableStateOf(true) }
    var newestFirst by remember { mutableStateOf(true) }

    Box(Modifier.fillMaxSize().background(NotesBackground)) {
        Column(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                    .statusBarsPadding().padding(top = 12.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                NotesHeader(isGrid = isGrid, onToggleGrid = { isGrid = !isGrid }, onSort = { newestFirst = !newestFirst })
                SearchField(value = searchQuery, onValueChange = { searchQuery = it })
                SubjectFilters(filters, selectedSubject) { selectedSubject = it }
                PinnedNotesCard()
                val visibleNotes = sampleNotes
                    .filter { selectedSubject == "Tất cả" || it.subject == selectedSubject }
                    .filter { searchQuery.isBlank() || (it.title + it.preview + it.subject).contains(searchQuery, ignoreCase = true) }
                    .let { if (newestFirst) it else it.reversed() }
                RecentNotesSection(visibleNotes, isGrid, onEditNote)
                StudyTipCard()
            }
            NotesBottomNavigation(onTabSelected)
        }
        Surface(
            onClick = onCreateNote,
            modifier = Modifier.align(Alignment.BottomEnd).navigationBarsPadding()
                .padding(end = 16.dp, bottom = 96.dp).size(56.dp)
                .shadow(8.dp, RoundedCornerShape(12.dp), spotColor = Color(0x595B5FEF)),
            color = NotesBright,
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(contentAlignment = Alignment.Center) { Text("+", fontSize = 28.sp, color = Color.White, lineHeight = 28.sp) }
        }
    }
}

@Composable
private fun NotesHeader(isGrid: Boolean, onToggleGrid: () -> Unit, onSort: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Ghi chú & Tài\nliệu", fontSize = 24.sp, lineHeight = 32.sp, letterSpacing = (-0.6).sp, fontWeight = FontWeight.Bold, color = NotesInk)
            Surface(color = Color(0xFFE8E6FC), shape = CircleShape) {
                Text("16 ghi\nchú", modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp), fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold, color = NotesBrand)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            HeaderAction(if (isGrid) "▦" else "☰", "Đổi chế độ xem", onToggleGrid)
            HeaderAction("↕", "Sắp xếp ghi chú", onSort)
        }
    }
}

@Composable
private fun HeaderAction(icon: String, description: String, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = Modifier.size(40.dp), color = Color(0xFFF5F2FF), shape = RoundedCornerShape(12.dp)) {
        Box(contentAlignment = Alignment.Center) { Text(icon, fontSize = 18.sp, color = NotesText) }
    }
}

@Composable
private fun SearchField(value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(48.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        placeholder = { Text("Tìm kiếm ghi chú, nội dung, môn học...", fontSize = 12.sp, color = NotesMuted, maxLines = 1) },
        leadingIcon = { Text("⌕", fontSize = 24.sp, color = NotesText) },
        trailingIcon = { Text("☷", modifier = Modifier.padding(end = 12.dp), fontSize = 18.sp, color = NotesText) },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = NotesBrand
        )
    )
}

@Composable
private fun SubjectFilters(filters: List<SubjectFilter>, selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            val active = selected == filter.name
            Surface(
                onClick = { onSelect(filter.name) },
                color = if (active) NotesBright else NotesChip,
                shape = CircleShape
            ) {
                Row(Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    filter.color?.let { Box(Modifier.size(8.dp).clip(CircleShape).background(it)) }
                    Text("${filter.name} (${filter.count})", fontSize = 11.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold, color = if (active) Color.White else NotesText, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun PinnedNotesCard() {
    Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("▮ ĐÃ GHIM", fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.7.sp, fontWeight = FontWeight.SemiBold, color = NotesBrand)
            Spacer(Modifier.weight(1f))
            Text("1 ghi chú quan trọng", fontSize = 11.sp, color = NotesText)
        }
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
            Row(Modifier.fillMaxWidth()) {
                Box(Modifier.width(6.dp).fillMaxHeight().background(NotesBrand))
                Column(Modifier.weight(1f).padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        NoteSubjectBadge("CSDL Nâng cao (IT3020)", NotesChip, NotesBrand)
                        Text("▮", fontSize = 15.sp, color = NotesBrand)
                    }
                    Text("Công thức phân mảnh CSDL ngang", fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold, color = NotesInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("Quy tắc completeness: Mọi tuple thuộc R phải xuất hiện ở ít nhất một mảnh. Quy tắc…", fontSize = 13.sp, lineHeight = 20.sp, color = NotesText, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        AttachmentPill("▧  2 ảnh sơ đồ")
                        AttachmentPill("▣  Slide_Chap3.pdf")
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("◷  Đã sửa 2 giờ trước", fontSize = 10.sp, color = NotesMuted)
                        Text("Chi tiết →", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = NotesBrand)
                    }
                }
            }
        }
    }
}

@Composable
private fun AttachmentPill(text: String) {
    Surface(color = NotesChip, shape = RoundedCornerShape(8.dp)) {
        Text(text, modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp), fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = NotesText, maxLines = 1)
    }
}

@Composable
private fun RecentNotesSection(notes: List<NoteItem>, isGrid: Boolean, onEditNote: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Gần đây", fontSize = 18.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold, color = NotesInk)
            Spacer(Modifier.weight(1f))
            Text("Sắp xếp: ", fontSize = 10.sp, color = NotesText)
            Text("Mới cập nhật", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = NotesBrand)
        }
        if (notes.isEmpty()) {
            Text("Không tìm thấy ghi chú phù hợp.", modifier = Modifier.padding(vertical = 20.dp), fontSize = 13.sp, color = NotesMuted)
        } else if (!isGrid) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { notes.forEach { NoteCard(it, Modifier.fillMaxWidth(), onEditNote) } }
        } else {
            notes.chunked(2).forEach { rowNotes ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
                    rowNotes.forEach { note -> NoteCard(note, Modifier.weight(1f), onEditNote) }
                    if (rowNotes.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun NoteCard(note: NoteItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = modifier.heightIn(min = 182.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.width(5.dp).fillMaxHeight().background(note.accent))
            Column(Modifier.weight(1f).padding(10.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    NoteSubjectBadge(note.subject, note.subjectBackground, note.accent)
                    Text("⋮", fontSize = 17.sp, color = NotesMuted)
                }
                Text(note.title, fontSize = 13.sp, lineHeight = 17.sp, fontWeight = FontWeight.Medium, color = NotesInk, maxLines = 3, overflow = TextOverflow.Ellipsis)
                Text(note.preview, fontSize = 11.sp, lineHeight = 16.sp, color = NotesText, maxLines = 3, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.weight(1f))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(note.date, fontSize = 9.sp, color = NotesMuted, maxLines = 1)
                    Text(note.tag, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = note.accent, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun NoteSubjectBadge(subject: String, background: Color, foreground: Color) {
    Surface(color = background, shape = RoundedCornerShape(6.dp)) {
        Text(subject, modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp), fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.SemiBold, color = foreground, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun StudyTipCard() {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 24.dp).background(Color(0xFFF5F2FF), RoundedCornerShape(16.dp)).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(color = NotesBright, shape = RoundedCornerShape(12.dp)) { Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) { Text("✦", fontSize = 20.sp, color = Color.White) } }
        Column(Modifier.weight(1f)) {
            Text("Mẹo ôn tập hiệu quả", fontSize = 11.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold, color = NotesInk)
            Text("Xem lại các ghi chú sau 24h giúp tăng 70% khả năng ghi nhớ dài hạn.", fontSize = 10.sp, lineHeight = 14.sp, color = NotesMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun NotesIcon(assetName: String, modifier: Modifier, contentDescription: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data("file:///android_asset/studyflow/$assetName")
            .decoderFactory(SvgDecoder.Factory())
            .build(),
        contentDescription = contentDescription.takeIf(String::isNotBlank),
        modifier = modifier
    )
}

@Composable
private fun NotesBottomNavigation(onTabSelected: (String) -> Unit) {
    val items = listOf(
        "Trang chủ" to "home_nav_home.svg",
        "Lịch biểu" to "home_nav_schedule.svg",
        "Nhiệm vụ" to "home_nav_tasks.svg",
        "Ghi chú" to "home_nav_notes.svg",
        "Cài đặt" to "home_nav_settings.svg"
    )
    Surface(color = NotesBackground.copy(alpha = .96f), shadowElevation = 4.dp) {
        Row(Modifier.fillMaxWidth().navigationBarsPadding().height(80.dp).padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
            items.forEach { (title, icon) ->
                val selected = title == "Ghi chú"
                Column(Modifier.weight(1f).height(56.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Surface(onClick = { if (!selected) onTabSelected(title) }, modifier = Modifier.width(56.dp).height(32.dp), color = if (selected) Color(0xFFE1E0FF) else Color.Transparent, shape = CircleShape) {
                        Box(contentAlignment = Alignment.Center) {
                            NotesIcon(icon, Modifier.size(18.dp), "")
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(title, fontSize = 10.sp, lineHeight = 14.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold, color = if (selected) NotesBrand else NotesText, maxLines = 1)
                }
            }
        }
    }
}
