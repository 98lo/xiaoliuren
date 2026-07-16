package com.example.xiaoliuren

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.nlf.calendar.Lunar
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.Date

data class LiuShen(
    val name: String,
    val info: String,
    val bgStart: Color,
    val bgEnd: Color,
    val textColor: Color
)

val liuShenList = listOf(
    LiuShen("大安", "身不动，安稳守成，出行平安，求财稳定", Color(0xFFE6F0FF), Color(0xFFC7E0FF), Color(0xFF0F4C99)),
    LiuShen("留连", "事拖延，纠缠阻滞，不宜急办，静待时机", Color(0xFFEDF5FF), Color(0xFFB8D6F5), Color(0xFF1A568C)),
    LiuShen("速喜", "喜事至，求财顺利，寻人即见，办事称心", Color(0xFFFFF0F0), Color(0xFFFFC8C8), Color(0xFFC41E3A)),
    LiuShen("赤口", "多口舌，防争执损耗，出行谨慎，少与人争辩", Color(0xFFFFF2F2), Color(0xFFFFBABA), Color(0xFFA81628)),
    LiuShen("小吉", "小利可得，往来走动皆吉，小事顺遂", Color(0xFFFFF6E6), Color(0xFFFFD8A8), Color(0xFFC26400)),
    LiuShen("空亡", "谋事落空，徒劳无功，损耗破财，宜静不宜动", Color(0xFFF0F7FF), Color(0xFFB4D3F7), Color(0xFF144B80))
)

val fontList = listOf(FontFamily.Serif, FontFamily.Cursive, FontFamily.SansSerif)

fun getShiNum(hour: Int): Int {
    return when (hour) {
        23, 0 -> 1
        1, 2 -> 2
        3, 4 -> 3
        5, 6 -> 4
        7, 8 -> 5
        9, 10 -> 6
        11, 12 -> 1
        13, 14 -> 2
        15, 16 -> 3
        17, 18 -> 4
        19, 20 -> 5
        21, 22 -> 6
        else -> 1
    }
}

fun calcXiaoLiuRen(lunarMonth: Int, lunarDay: Int, hour: Int): LiuShen {
    val shi = getShiNum(hour)
    val monthIdx = ((lunarMonth - 1) % 6 + 6) % 6
    val dayIdx = ((monthIdx + lunarDay - 1) % 6 + 6) % 6
    val finalIdx = ((dayIdx + shi - 1) % 6 + 6) % 6
    return liuShenList[finalIdx]
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                XiaoLiuRenScreen()
            }
        }
    }
}

@Composable
fun XiaoLiuRenScreen() {
    var timeStr by remember { mutableStateOf("") }
    var lunarStr by remember { mutableStateOf("") }
    var result by remember { mutableStateOf(liuShenList[0]) }
    var lastRefresh by remember { mutableStateOf("") }
    var fontIndex by remember { mutableStateOf(0) }
    var menuExpanded by remember { mutableStateOf(false) }

    fun refresh() {
        val now = Calendar.getInstance()
        val h = now.get(Calendar.HOUR_OF_DAY)
        val m = now.get(Calendar.MINUTE)
        val s = now.get(Calendar.SECOND)
        val year = now.get(Calendar.YEAR)
        val month = now.get(Calendar.MONTH) + 1
        val day = now.get(Calendar.DAY_OF_MONTH)
        timeStr = "%d年%d月%d日 %02d:%02d".format(year, month, day, h, m)

        val lunar = Lunar.fromDate(Date())
        val lm = lunar.month
        val ld = lunar.day
        lunarStr = "农历${lm}月${ld}日"

        result = calcXiaoLiuRen(lm, ld, h)
        lastRefresh = "上次刷新：%02d:%02d:%02d".format(h, m, s)
    }

    LaunchedEffect(Unit) {
        refresh()
        while (true) {
            delay(60000)
            refresh()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(result.bgStart, result.bgEnd)))
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp)
                .fillMaxWidth(0.85f),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(timeStr, fontSize = 16.sp, color = Color(0xFF222222))
                Spacer(modifier = Modifier.height(6.dp))
                Text(lunarStr, fontSize = 14.sp, color = Color(0xFF333333))
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    result.name,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = result.textColor,
                    fontFamily = fontList[fontIndex],
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                   
