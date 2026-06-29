package com.soundscope.app.ui.meter

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.soundscope.app.R
import com.soundscope.app.data.DecibelLevel
import com.soundscope.app.data.SceneMode
import com.soundscope.app.viewmodel.DecibelViewModel
import com.soundscope.app.viewmodel.MeasurementState
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MainScreen(
    viewModel: DecibelViewModel,
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    onExit: () -> Unit = {}
) {
    val state by viewModel.measurementState.collectAsState()
    val scene = state.sceneMode
    val scrollState = rememberScrollState()
    // 是否超标（测量中且当前分贝超过场景推荐阈值）
    val isOverLimit = state.isMeasuring && !state.isPaused && state.currentDb > scene.recommendedMaxDb

    // ✅ v1.2.1 修复：改用 verticalScroll，解决小屏设备内容超出屏幕、开始按钮看不到的问题
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ✅ v1.2.0 顶部标题栏：slogan + 退出按钮
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.meter_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.app_slogan),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
            // 退出按钮
            IconButton(onClick = onExit) {
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = stringResource(R.string.meter_exit_desc),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 场景模式选择器
        SceneSelector(
            selectedMode = scene,
            onModeSelected = { viewModel.setSceneMode(it) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ✅ 场景信息卡片（切换场景立刻可见差异）
        SceneInfoCard(scene = scene)

        Spacer(modifier = Modifier.height(12.dp))

        // 仪表盘（带场景推荐阈值标记线）— v1.2.1 缩小尺寸适配小屏
        DecibelGauge(
            db = state.currentDb,
            isMeasuring = state.isMeasuring,
            thresholdDb = scene.recommendedMaxDb,
            modifier = Modifier.size(220.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 分贝等级描述 + 超标警告
        if (state.isMeasuring || state.currentDb > 0) {
            val level = DecibelLevel.fromDb(state.currentDb)
            Text(
                text = "${stringResource(level.labelRes)} · ${stringResource(level.descriptionRes)}",
                style = MaterialTheme.typography.bodyLarge,
                color = level.color,
                fontWeight = FontWeight.SemiBold
            )
            // 超标警告条
            if (isOverLimit) {
                Spacer(modifier = Modifier.height(6.dp))
                OverLimitWarning(
                    currentDb = state.currentDb,
                    limitDb = scene.recommendedMaxDb,
                    themeColor = scene.themeColor
                )
            } else if (state.isMeasuring) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.meter_compliant, stringResource(scene.labelRes), scene.recommendedMaxDb),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            Text(
                text = stringResource(R.string.meter_start_hint),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 统计数据卡片
        StatsRow(state = state)

        // 暂停状态提示
        if (state.isPaused) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.meter_paused_hint),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // ✅ v1.2.1 修复：去掉 weight(1f)（滚动容器中无效），改为固定间距
        Spacer(modifier = Modifier.height(16.dp))

        // ✅ v1.2.0 三态按钮组：未测量→开始；测量中→暂停+停止；暂停中→继续+停止
        when {
            !state.isMeasuring -> {
                // 开始测量
                Button(
                    onClick = {
                        if (hasPermission) {
                            viewModel.startMeasurement()
                        } else {
                            onRequestPermission()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = scene.themeColor
                    )
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.meter_start),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            state.isPaused -> {
                // 继续 + 停止
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { viewModel.resumeMeasurement() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = scene.themeColor
                        )
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.meter_resume), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { viewModel.stopMeasurement() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.meter_stop),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            else -> {
                // 暂停 + 停止
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { viewModel.pauseMeasurement() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.meter_pause), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { viewModel.stopMeasurement() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.meter_stop),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

/**
 * 场景信息卡片 — 切换场景时立刻可见的差异
 */
@Composable
private fun SceneInfoCard(scene: SceneMode) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = scene.themeColor.copy(alpha = 0.12f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 场景图标
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(scene.themeColor.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = scene.icon, fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            // 场景信息
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.meter_scene_mode, stringResource(scene.labelRes)),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = scene.themeColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = scene.themeColor),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "≤ ${scene.recommendedMaxDb} dB",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(scene.scenarioRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 12.sp
                )
                Text(
                    text = stringResource(R.string.meter_standard_label, stringResource(scene.standardRes)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

/**
 * 超标警告条
 */
@Composable
private fun OverLimitWarning(currentDb: Float, limitDb: Int, themeColor: Color) {
    val over = currentDb - limitDb
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⚠️", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.meter_over_limit, limitDb, String.format("%.1f", over)),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFC62828),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(R.string.meter_suggest_denoise),
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFC62828)
            )
        }
    }
}

@Composable
private fun SceneSelector(
    selectedMode: SceneMode,
    onModeSelected: (SceneMode) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(SceneMode.entries) { mode ->
            // ✅ 选中时用场景主题色，让切换有视觉反馈
            FilterChip(
                selected = selectedMode == mode,
                onClick = { onModeSelected(mode) },
                label = { Text(stringResource(mode.labelRes), fontWeight = if (selectedMode == mode) FontWeight.Bold else FontWeight.Normal) },
                leadingIcon = { Text(mode.icon) },
                colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                    selectedContainerColor = mode.themeColor.copy(alpha = 0.85f),
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun DecibelGauge(
    db: Float,
    isMeasuring: Boolean,
    thresholdDb: Int = 60,   // ✅ 场景推荐阈值，用于画标记线
    modifier: Modifier = Modifier
) {
    val animatedDb by animateFloatAsState(
        targetValue = db,
        animationSpec = tween(durationMillis = 100),
        label = "db"
    )

    val level = DecibelLevel.fromDb(db)
    // 超过阈值用红色，未超过用等级色，让阈值判定一目了然
    val isOver = db > thresholdDb
    val animatedColor by animateColorAsState(
        targetValue = when {
            !isMeasuring -> MaterialTheme.colorScheme.surfaceVariant
            isOver -> Color(0xFFE53935)   // 超标红
            else -> level.color
        },
        animationSpec = tween(300),
        label = "color"
    )

    val sweepAngle = (animatedDb / 120f) * 270f // 0-120 dB maps to 0-270 degrees
    // 阈值标记角度（0-120 dB → 0-270 度）
    val thresholdSweep = (thresholdDb / 120f) * 270f

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val canvasSize = size.minDimension
            val center = Offset(size.width / 2, size.height / 2)
            val radius = canvasSize / 2 - 20f
            val strokeWidth = 24f

            // 背景圆弧
            drawArc(
                color = Color(0xFF2A2A3E),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // 分贝圆弧
            if (sweepAngle > 0) {
                drawArc(
                    color = animatedColor,
                    startAngle = 135f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // ✅ 场景推荐阈值标记线（红色短粗线，标在圆弧外侧）
            val thresholdAngleDeg = 135f + thresholdSweep
            val thresholdAngleRad = Math.toRadians(thresholdAngleDeg.toDouble())
            val tickInner = radius + 2f
            val tickOuter = radius + 14f
            drawLine(
                color = Color(0xFFE53935),
                start = Offset(
                    (center.x + tickInner * cos(thresholdAngleRad)).toFloat(),
                    (center.y + tickInner * sin(thresholdAngleRad)).toFloat()
                ),
                end = Offset(
                    (center.x + tickOuter * cos(thresholdAngleRad)).toFloat(),
                    (center.y + tickOuter * sin(thresholdAngleRad)).toFloat()
                ),
                strokeWidth = 5f,
                cap = StrokeCap.Round
            )

            // 刻度线
            for (i in 0..12) {
                val angle = Math.toRadians((135.0 + i * 22.5))
                val innerR = radius - strokeWidth / 2 - 4f
                val outerR = radius - strokeWidth / 2 + 4f
                val startX = center.x + (innerR * cos(angle)).toFloat()
                val startY = center.y + (innerR * sin(angle)).toFloat()
                val endX = center.x + (outerR * cos(angle)).toFloat()
                val endY = center.y + (outerR * sin(angle)).toFloat()
                drawLine(
                    color = Color(0xFF4A4A5E),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 2f
                )
            }

            // 指针
            if (isMeasuring) {
                val pointerAngle = 135f + sweepAngle
                rotate(pointerAngle, pivot = center) {
                    drawLine(
                        color = animatedColor,
                        start = center,
                        end = Offset(center.x + radius - 30f, center.y),
                        strokeWidth = 4f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        // 中心数字显示
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = String.format("%.1f", animatedDb),
                fontSize = 60.sp,
                fontWeight = FontWeight.Bold,
                color = if (isMeasuring) animatedColor else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "dB",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Medium
            )
            // ✅ 仪表盘中心显示阈值提示
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.meter_threshold_label, thresholdDb),
                fontSize = 11.sp,
                color = Color(0xFFE53935),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun StatsRow(state: MeasurementState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = stringResource(R.string.stat_max),
            value = if (state.maxDb > 0) String.format("%.1f", state.maxDb) else "--",
            color = Color(0xFFFF5722),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = stringResource(R.string.stat_avg),
            value = if (state.avgDb > 0) String.format("%.1f", state.avgDb) else "--",
            color = Color(0xFF4CAF50),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = stringResource(R.string.stat_min),
            value = if (state.minDb > 0) String.format("%.1f", state.minDb) else "--",
            color = Color(0xFF2196F3),
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // 时长显示
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.stat_duration) + ": ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = formatDuration(state.durationSeconds),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "dB",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

private fun formatDuration(seconds: Long): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%02d:%02d", m, s)
}
