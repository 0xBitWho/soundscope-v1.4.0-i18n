package com.soundscope.app.ai

import android.content.Context
import android.util.Log
import com.soundscope.app.R
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * AI 服务 — 调用 OpenAI 兼容的 /chat/completions 接口
 *
 * 支持通义千问 / 文心 / 智谱 / DeepSeek / OpenAI 等，
 * 只要 BaseUrl + Key + Model 正确即可。
 *
 * 无网络或未配置时，AIRepository 会走本地规则兜底。
 *
 * v1.3.0 国际化：异常消息通过 Context 读取资源
 */
class AIService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    /**
     * 调用大模型，返回文本响应
     *
     * @param baseUrl  提供商 BaseUrl，如 https://dashscope.aliyuncs.com/compatible-mode/v1
     * @param apiKey   API Key
     * @param model    模型名，如 qwen-turbo
     * @param messages 消息列表（system + user）
     * @param context  上下文（v1.3.0 用于国际化异常消息）
     * @return 文本响应；失败抛异常
     */
    fun chat(
        baseUrl: String,
        apiKey: String,
        model: String,
        messages: List<ChatMessage>,
        context: Context
    ): String {
        val url = baseUrl.trimEnd('/') + "/chat/completions"

        val messagesArray = JSONArray()
        messages.forEach { msg ->
            messagesArray.put(JSONObject().apply {
                put("role", msg.role)
                put("content", msg.content)
            })
        }

        val body = JSONObject().apply {
            put("model", model)
            put("messages", messagesArray)
            put("temperature", 0.7)
            put("max_tokens", 1024)
        }.toString()

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()

        Log.d("AIService", "POST $url model=$model")

        client.newCall(request).execute().use { response ->
            val respBody = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                Log.e("AIService", "HTTP ${response.code}: $respBody")
                throw Exception(context.getString(R.string.error_http, response.code, response.message))
            }

            val json = JSONObject(respBody)
            // OpenAI 兼容结构：choices[0].message.content
            val choices = json.optJSONArray("choices")
                ?: throw Exception(context.getString(R.string.error_no_choices))
            val content = choices.getJSONObject(0)
                .optJSONObject("message")
                ?.optString("content")
                ?: throw Exception(context.getString(R.string.error_no_content))

            return content
        }
    }
}
