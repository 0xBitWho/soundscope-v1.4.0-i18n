package com.soundscope.app.ai

import android.content.Context
import android.util.Log
import com.soundscope.app.R
import com.soundscope.app.data.DecibelLevel
import com.soundscope.app.data.DecibelRecord
import com.soundscope.app.data.SceneMode
import com.soundscope.app.data.UserPreferences
import com.soundscope.app.util.LocaleManager
import org.json.JSONObject

/**
 * AI 仓库 — 业务封装层
 *
 * 策略（PRD 2.4.3 混合模式）：
 * 1. 若用户启用了 AI 且配置了 API Key → 调用云端大模型
 * 2. 否则 → 本地规则兜底，给出基础分析
 *
 * 这样无论是否联网/配置，用户都能获得分析结果，差异在于丰富度。
 *
 * v1.3.0 国际化：所有方法增加 context 参数，文案通过资源 ID 读取
 */
class AIRepository {

    private val service = AIService()

    /**
     * 分析一条测量记录
     *
     * @param record  测量记录
     * @param prefs   用户偏好
     * @param context 上下文（v1.3.0 用于国际化）
     */
    suspend fun analyze(record: DecibelRecord, prefs: UserPreferences, context: Context): AIAnalysisReport {
        if (!prefs.aiEnabled || prefs.apiKey.isBlank()) {
            // 本地兜底
            return localAnalysis(record, context)
        }

        return try {
            val (systemPrompt, userPrompt) = buildAnalysisPrompt(record, context)
            val messages = listOf(
                ChatMessage("system", systemPrompt),
                ChatMessage("user", userPrompt)
            )
            val raw = service.chat(prefs.apiBaseUrl, prefs.apiKey, prefs.modelName, messages, context)
            parseAnalysisResponse(raw, record.id, context)
        } catch (e: Exception) {
            Log.e("AIRepository", "Cloud analysis failed, fallback to local", e)
            // 云端失败也降级本地，保证用户始终有结果
            localAnalysis(record, context).copy(
                trendAnalysis = context.getString(R.string.local_cloud_fail) + e.message.orEmpty()
            )
        }
    }

    /**
     * 对话追问 — 用户基于已分析记录继续提问
     *
     * @param question 用户问题
     * @param record   测量记录
     * @param history  对话历史
     * @param prefs    用户偏好
     * @param context  上下文（v1.3.0 用于国际化）
     */
    suspend fun ask(
        question: String,
        record: DecibelRecord,
        history: List<ChatMessage>,
        prefs: UserPreferences,
        context: Context
    ): String {
        if (!prefs.aiEnabled || prefs.apiKey.isBlank()) {
            return localAnswer(question, record, context)
        }

        return try {
            val (systemPrompt, _) = buildAnalysisPrompt(record, context)
            val messages = mutableListOf<ChatMessage>().apply {
                add(ChatMessage("system", systemPrompt))
                add(ChatMessage("system", buildDataSummary(record, context)))
                addAll(history)
                add(ChatMessage("user", question))
            }
            service.chat(prefs.apiBaseUrl, prefs.apiKey, prefs.modelName, messages, context)
        } catch (e: Exception) {
            Log.e("AIRepository", "Cloud ask failed, fallback to local", e)
            context.getString(R.string.local_cloud_ask_fail) + localAnswer(question, record, context)
        }
    }

    /**
     * 解析大模型 JSON 响应为 AIAnalysisReport
     */
    private fun parseAnalysisResponse(raw: String, recordId: Long, context: Context): AIAnalysisReport {
        // 兼容模型可能输出 ```json ... ``` 包裹
        val cleaned = raw.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val json = try {
            JSONObject(cleaned)
        } catch (e: Exception) {
            // 解析失败则把整段文本作为 environmentLevel 返回，避免崩溃
            return AIAnalysisReport(
                recordId = recordId,
                environmentLevel = cleaned.take(500),
                healthAdvice = context.getString(R.string.local_parse_fail),
                trendAnalysis = "",
                actionAdvice = "",
                funFact = ""
            )
        }

        return AIAnalysisReport(
            recordId = recordId,
            environmentLevel = json.optString("environmentLevel", "—"),
            healthAdvice = json.optString("healthAdvice", "—"),
            trendAnalysis = json.optString("trendAnalysis", "—"),
            actionAdvice = json.optString("actionAdvice", "—"),
            funFact = json.optString("funFact", "—")
        )
    }

    // ============ 本地规则兜底 ============

    private fun localAnalysis(record: DecibelRecord, context: Context): AIAnalysisReport {
        val level = DecibelLevel.fromDb(record.avgDb)
        val scene = SceneMode.fromTag(record.scene)
        val diff = record.avgDb - scene.recommendedMaxDb
        val isEnglish = LocaleManager.isEnglish(context)

        val levelLabel = context.getString(level.labelRes)
        val levelDesc = context.getString(level.descriptionRes)
        val sceneLabel = context.getString(scene.labelRes)

        val environmentLevel = context.getString(
            R.string.local_env_level,
            levelLabel,
            levelDesc,
            String.format("%.1f", record.avgDb)
        )

        val healthAdvice = when {
            record.avgDb >= 85 ->
                context.getString(R.string.local_health_danger)
            record.avgDb >= 70 ->
                context.getString(R.string.local_health_warning)
            record.avgDb > scene.recommendedMaxDb ->
                context.getString(
                    R.string.local_health_over_limit,
                    sceneLabel,
                    scene.recommendedMaxDb,
                    String.format("%.1f", diff)
                )
            else ->
                context.getString(
                    R.string.local_health_ok,
                    sceneLabel,
                    scene.recommendedMaxDb
                )
        }

        val trendAnalysis = buildString {
            append(context.getString(
                R.string.local_trend_template,
                record.duration,
                String.format("%.1f", record.minDb),
                String.format("%.1f", record.maxDb)
            ))
            val range = record.maxDb - record.minDb
            if (range < 5) {
                append(context.getString(R.string.local_trend_stable))
            } else if (range < 15) {
                append(context.getString(R.string.local_trend_moderate))
            } else {
                append(context.getString(R.string.local_trend_unstable))
            }
        }

        val actionAdvice = when {
            record.avgDb >= 85 ->
                context.getString(R.string.local_action_danger)
            record.avgDb > scene.recommendedMaxDb ->
                context.getString(R.string.local_action_over_limit)
            else ->
                context.getString(R.string.local_action_ok, sceneLabel)
        }

        val funFact = buildString {
            val analogyRes = when (record.avgDb.toInt()) {
                in 0..20 -> R.string.local_fun_anechoic
                in 21..30 -> R.string.local_fun_whisper
                in 31..40 -> R.string.local_fun_library
                in 41..50 -> R.string.local_fun_fridge
                in 51..60 -> R.string.local_fun_conversation
                in 61..70 -> R.string.local_fun_office
                in 71..80 -> R.string.local_fun_street
                in 81..90 -> R.string.local_fun_subway
                in 91..100 -> R.string.local_fun_saw
                else -> R.string.local_fun_plane
            }
            append(context.getString(analogyRes))
            val ducks = (record.avgDb / 15).toInt().coerceAtLeast(1)
            append(context.getString(R.string.local_fun_ducks, ducks))
        }

        return AIAnalysisReport(
            recordId = record.id,
            environmentLevel = environmentLevel,
            healthAdvice = healthAdvice,
            trendAnalysis = trendAnalysis,
            actionAdvice = actionAdvice,
            funFact = funFact
        )
    }

    private fun localAnswer(question: String, record: DecibelRecord, context: Context): String {
        val q = question.lowercase()
        val isEnglish = LocaleManager.isEnglish(context)

        // 英文关键词匹配
        if (isEnglish) {
            return when {
                q.contains("hearing") || q.contains("damage") || q.contains("ear") -> {
                    if (record.avgDb >= 85) context.getString(R.string.local_q_hearing_danger, record.avgDb)
                    else context.getString(R.string.local_q_hearing_safe, record.avgDb)
                }
                q.contains("reduce") || q.contains("lower") || q.contains("how") -> {
                    context.getString(R.string.local_q_reduce)
                }
                q.contains("sleep") || q.contains("bed") -> {
                    if (record.avgDb > 30) context.getString(R.string.local_q_sleep_over, record.avgDb)
                    else context.getString(R.string.local_q_sleep_ok, record.avgDb)
                }
                else -> context.getString(R.string.local_q_default, record.avgDb)
            }
        }

        // 中文关键词匹配
        return when {
            q.contains("听力") || q.contains("损伤") -> {
                if (record.avgDb >= 85) context.getString(R.string.local_q_hearing_danger, record.avgDb)
                else context.getString(R.string.local_q_hearing_safe, record.avgDb)
            }
            q.contains("降低") || q.contains("减少") || q.contains("怎么办") -> {
                context.getString(R.string.local_q_reduce)
            }
            q.contains("睡眠") || q.contains("睡觉") -> {
                if (record.avgDb > 30) context.getString(R.string.local_q_sleep_over, record.avgDb)
                else context.getString(R.string.local_q_sleep_ok, record.avgDb)
            }
            else -> context.getString(R.string.local_q_default, record.avgDb)
        }
    }
}
