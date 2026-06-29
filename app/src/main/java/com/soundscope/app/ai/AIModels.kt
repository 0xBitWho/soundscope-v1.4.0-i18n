package com.soundscope.app.ai

import android.content.Context
import com.soundscope.app.R
import com.soundscope.app.data.DecibelRecord
import com.soundscope.app.data.SceneMode
import com.soundscope.app.util.LocaleManager

/**
 * AI 分析报告 — 对应 PRD 2.4.1 / 2.7 AIAnalysisReport
 *
 * 字段：
 * - environmentLevel  环境评估
 * - healthAdvice      健康建议
 * - trendAnalysis     趋势分析（单次测量时基于本次波动）
 * - actionAdvice      行动建议
 * - funFact           趣味解读
 */
data class AIAnalysisReport(
    val recordId: Long,
    val environmentLevel: String,
    val healthAdvice: String,
    val trendAnalysis: String,
    val actionAdvice: String,
    val funFact: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 对话消息
 */
data class ChatMessage(
    val role: String,        // "user" / "assistant" / "system"
    val content: String
)

/**
 * AI 分析结果封装
 */
sealed class AIResult {
    data object Idle : AIResult()
    data object Loading : AIResult()
    data class Success(val report: AIAnalysisReport) : AIResult()
    data class Error(val message: String) : AIResult()
}

/**
 * 对话结果封装
 */
sealed class ChatResult {
    data object Loading : ChatResult()
    data class Success(val answer: String) : ChatResult()
    data class Error(val message: String) : ChatResult()
}

/**
 * 构造分析提示词（system + user）
 * 参考 PRD 2.4.1，引导大模型输出结构化 JSON
 *
 * v1.3.0 国际化：根据当前语言选择中英文提示词模板
 *
 * @param record  测量记录
 * @param context 上下文（用于读取字符串资源和判断语言）
 */
internal fun buildAnalysisPrompt(record: DecibelRecord, context: Context): Pair<String, String> {
    val scene = SceneMode.fromTag(record.scene)
    val isEnglish = LocaleManager.isEnglish(context)

    val systemPrompt = if (isEnglish) {
        context.getString(R.string.prompt_system_en)
    } else {
        context.getString(R.string.prompt_system_zh)
    }

    val sceneLabel = context.getString(scene.labelRes)
    val userPrompt = if (isEnglish) {
        context.getString(
            R.string.prompt_user_en,
            sceneLabel,
            scene.recommendedMaxDb,
            String.format("%.1f", record.avgDb),
            String.format("%.1f", record.maxDb),
            String.format("%.1f", record.minDb),
            record.duration,
            record.timeSeries
        )
    } else {
        context.getString(
            R.string.prompt_user_zh,
            sceneLabel,
            scene.recommendedMaxDb,
            String.format("%.1f", record.avgDb),
            String.format("%.1f", record.maxDb),
            String.format("%.1f", record.minDb),
            record.duration,
            record.timeSeries
        )
    }

    return systemPrompt to userPrompt
}

/**
 * 构造数据摘要（用于对话追问的 system 上下文）
 */
internal fun buildDataSummary(record: DecibelRecord, context: Context): String {
    val scene = SceneMode.fromTag(record.scene)
    val isEnglish = LocaleManager.isEnglish(context)
    val sceneLabel = context.getString(scene.labelRes)

    val summary = if (isEnglish) {
        "Scene: $sceneLabel, Average: ${String.format("%.1f", record.avgDb)} dB, " +
            "Max: ${String.format("%.1f", record.maxDb)} dB, Min: ${String.format("%.1f", record.minDb)} dB, " +
            "Duration: ${record.duration} seconds"
    } else {
        context.getString(
            R.string.local_data_summary,
            sceneLabel,
            String.format("%.1f", record.avgDb),
            String.format("%.1f", record.maxDb),
            String.format("%.1f", record.minDb),
            record.duration
        )
    }

    val template = if (isEnglish) R.string.prompt_data_summary_en else R.string.prompt_data_summary_zh
    return context.getString(template, summary)
}
