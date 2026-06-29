package com.soundscope.app.ui.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soundscope.app.R
import com.soundscope.app.ai.AIResult
import com.soundscope.app.ai.ChatMessage
import com.soundscope.app.ai.ChatResult
import com.soundscope.app.data.DecibelLevel
import com.soundscope.app.data.DecibelRecord
import com.soundscope.app.data.SceneMode
import com.soundscope.app.viewmodel.AIViewModel
import com.soundscope.app.viewmodel.DecibelViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * AI 分析界面 — v1.1.0 新增
 *
 * 对应 PRD F07 AI 噪音分析：
 * - 顶部展示当前/最近测量数据摘要
 * - 「AI 分析」按钮触发分析
 * - 报告卡片展示 5 大维度（环境评估/健康建议/趋势/行动/趣味）
 * - 底部对话追问输入框
 * - 未配置 API 时引导去设置
 *
 * v1.3.0 国际化：所有硬编码中文替换为 stringResource()
 */
@Composable
fun AIScreen(
    aiViewModel: AIViewModel,
    decibelViewModel: DecibelViewModel,
    onNavigateToSettings: () -> Unit
) {
    val records by decibelViewModel.allRecords.collectAsState(initial = emptyList())
    val currentRecord by aiViewModel.currentRecord.collectAsState()
    val aiResult by aiViewModel.aiResult.collectAsState()
    val chatHistory by aiViewModel.chatHistory.collectAsState()
    val chatResult by aiViewModel.chatResult.collectAsState()
    val prefs by aiViewModel.userPreferences.collectAsState(initial = com.soundscope.app.data.UserPreferences())

    // 默认取最近一条记录
    LaunchedEffect(records) {
        if (currentRecord == null && records.isNotEmpty()) {
            aiViewModel.setRecord(records.first())
        }
    }

    val scrollState = rememberLazyListState()
    // 新消息时滚动到底部
    LaunchedEffect(chatHistory.size, chatResult) {
        if (chatHistory.isNotEmpty()) {
            scrollState.animateScrollToItem(chatHistory.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 顶部摘要
        RecordSummary(
            record = currentRecord,
            onPickRecord = {
                // 简化：循环切换最近 5 条记录
                val idx = records.indexOfFirst { it.id == currentRecord?.id }
                val nextIdx = if (idx + 1 < records.size.coerceAtMost(5)) idx + 1 else 0
                if (records.isNotEmpty()) aiViewModel.setRecord(records[nextIdx])
            }
        )

        // AI 状态提示条
        if (!prefs.aiEnabled || prefs.apiKey.isBlank()) {
            LocalModeBanner(onNavigateToSettings = onNavigateToSettings)
        }

        // 主内容区：可滚动
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // AI 分析按钮
                Button(
                    onClick = { aiViewModel.analyze() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    enabled = currentRecord != null && aiResult !is AIResult.Loading
                ) {
                    if (aiResult is AIResult.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.ai_analyzing))
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.ai_analyze_btn), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 分析报告
            when (val result = aiResult) {
                is AIResult.Success -> {
                    item { AnalysisReportCard(result.report) }
                }
                is AIResult.Error -> {
                    item {
                        ErrorCard(result.message)
                    }
                }
                AIResult.Idle, AIResult.Loading -> {
                    if (aiResult is AIResult.Idle) {
                        item { EmptyHint() }
                    }
                }
            }

            // 对话历史
            items(chatHistory) { msg ->
                ChatBubble(msg)
            }

            // 对话加载中
            if (chatResult is ChatResult.Loading) {
                item {
                    ChatBubble(ChatMessage("assistant", stringResource(R.string.ai_thinking)), isLoading = true)
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        // 底部输入框
        ChatInput(
            enabled = aiResult is AIResult.Success,
            onSend = { aiViewModel.ask(it) }
        )
    }
}

@Composable
private fun RecordSummary(
    record: DecibelRecord?,
    onPickRecord: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp),
        onClick = onPickRecord
    ) {
        if (record == null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.ai_no_record), fontWeight = FontWeight.Medium)
                Text(
                    text = stringResource(R.string.ai_no_record_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        } else {
            Column(modifier = Modifier.padding(16.dp)) {
                val level = DecibelLevel.fromDb(record.avgDb)
                val scene = SceneMode.fromTag(record.scene)
                val date = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
                    .format(Date(record.timestamp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.ai_current_data),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = stringResource(R.string.ai_switch_record),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    SummaryItem(stringResource(R.string.ai_summary_time), date, modifier = Modifier.weight(1f))
                    SummaryItem(stringResource(R.string.ai_summary_scene), stringResource(scene.labelRes), modifier = Modifier.weight(1f))
                    SummaryItem(stringResource(R.string.ai_summary_duration), "${record.duration}s", modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    SummaryItem(stringResource(R.string.stat_avg_short), String.format("%.1f dB", record.avgDb), level.color, modifier = Modifier.weight(1f))
                    SummaryItem(stringResource(R.string.stat_max_short), String.format("%.1f dB", record.maxDb), Color(0xFFFF5722), modifier = Modifier.weight(1f))
                    SummaryItem(stringResource(R.string.stat_min_short), String.format("%.1f dB", record.minDb), Color(0xFF2196F3), modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.onBackground,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            fontSize = 11.sp
        )
    }
}

@Composable
private fun LocalModeBanner(onNavigateToSettings: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3CD)  // 淡黄色提示
        ),
        shape = RoundedCornerShape(8.dp),
        onClick = onNavigateToSettings
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF856404))
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.ai_local_mode),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF856404)
                )
                Text(
                    text = stringResource(R.string.ai_local_mode_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF856404)
                )
            }
        }
    }
}

@Composable
private fun AnalysisReportCard(report: com.soundscope.app.ai.AIAnalysisReport) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.ai_report_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            ReportSection(stringResource(R.string.ai_section_env), report.environmentLevel)
            ReportSection(stringResource(R.string.ai_section_health), report.healthAdvice)
            ReportSection(stringResource(R.string.ai_section_trend), report.trendAnalysis)
            ReportSection(stringResource(R.string.ai_section_action), report.actionAdvice)
            ReportSection(stringResource(R.string.ai_section_fun), report.funFact)
        }
    }
}

@Composable
private fun ReportSection(title: String, content: String) {
    if (content.isBlank()) return
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.ai_error_title),
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC62828)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFC62828)
            )
        }
    }
}

@Composable
private fun EmptyHint() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringResource(R.string.ai_empty_hint), fontSize = 48.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.ai_empty_hint_text),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage, isLoading: Boolean = false) {
    val isUser = message.role == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.width(280.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (isLoading) stringResource(R.string.ai_thinking) else message.content,
                modifier = Modifier.padding(12.dp),
                color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ChatInput(
    enabled: Boolean,
    onSend: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val sendAction = {
        if (text.isNotBlank() && enabled) {
            onSend(text)
            text = ""
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    if (enabled) stringResource(R.string.ai_chat_placeholder_on)
                    else stringResource(R.string.ai_chat_placeholder_off)
                )
            },
            enabled = enabled,
            shape = RoundedCornerShape(24.dp),
            singleLine = false,
            maxLines = 3
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = { sendAction() },
            enabled = enabled && text.isNotBlank()
        ) {
            Icon(
                Icons.Default.Send,
                contentDescription = stringResource(R.string.ai_send),
                tint = if (enabled && text.isNotBlank()) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            )
        }
    }
}
