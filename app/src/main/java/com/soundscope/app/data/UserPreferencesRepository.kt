package com.soundscope.app.data

import android.content.Context
import androidx.annotation.StringRes
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.soundscope.app.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 用户偏好存储 — 基于 DataStore
 *
 * v1.1.0 新增，承载：
 * - 用户管理：昵称
 * - 大模型设置：提供商、API Key、Base URL、模型名、是否启用
 * - v1.2.0 新增：主题模式（0=跟随系统 1=白天 2=黑夜）
 * - v1.3.0 新增：语言模式（"system"|"zh"|"en"|...）
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sound_scope_prefs")

data class UserPreferences(
    val nickname: String = "",
    val aiEnabled: Boolean = false,
    val aiProvider: String = AiProvider.QWEN.id,        // 默认通义千问
    val apiKey: String = "",
    val apiBaseUrl: String = AiProvider.QWEN.defaultBaseUrl,
    val modelName: String = AiProvider.QWEN.defaultModel,
    val themeMode: Int = 0,                              // 0=跟随系统 1=白天 2=黑夜
    val languageMode: String = "system"                  // "system"|"zh"|"en"|... v1.3.0
)

/**
 * 支持的大模型提供商 — OpenAI 兼容接口
 * 所有提供商均走 /v1/chat/completions，只需切换 BaseUrl + Key + Model
 *
 * v1.3.0：displayName 改为 @StringRes 资源 ID，支持国际化
 */
enum class AiProvider(
    val id: String,
    @StringRes val displayNameRes: Int,
    val defaultBaseUrl: String,
    val defaultModel: String,
    val officialSite: String
) {
    QWEN(
        "qwen",
        R.string.provider_qwen,
        "https://dashscope.aliyuncs.com/compatible-mode/v1",
        "qwen-turbo",
        "https://dashscope.console.aliyun.com"
    ),
    ERNIE(
        "ernie",
        R.string.provider_ernie,
        "https://qianfan.baidubce.com/v2",
        "ernie-4.0-8k-latest",
        "https://console.bce.baidu.com/qianfan"
    ),
    ZHIPU(
        "zhipu",
        R.string.provider_zhipu,
        "https://open.bigmodel.cn/api/paas/v4",
        "glm-4-flash",
        "https://open.bigmodel.cn"
    ),
    DEEPSEEK(
        "deepseek",
        R.string.provider_deepseek,
        "https://api.deepseek.com/v1",
        "deepseek-chat",
        "https://platform.deepseek.com"
    ),
    OPENAI(
        "openai",
        R.string.provider_openai,
        "https://api.openai.com/v1",
        "gpt-4o-mini",
        "https://platform.openai.com"
    );

    companion object {
        fun fromId(id: String): AiProvider =
            entries.firstOrNull { it.id == id } ?: QWEN
    }
}

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val NICKNAME = stringPreferencesKey("nickname")
        val AI_ENABLED = booleanPreferencesKey("ai_enabled")
        val AI_PROVIDER = stringPreferencesKey("ai_provider")
        val API_KEY = stringPreferencesKey("api_key")
        val API_BASE_URL = stringPreferencesKey("api_base_url")
        val MODEL_NAME = stringPreferencesKey("model_name")
        val THEME_MODE = intPreferencesKey("theme_mode")
        val LANGUAGE_MODE = stringPreferencesKey("language_mode")  // v1.3.0
    }

    val userPreferences: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            nickname = prefs[Keys.NICKNAME] ?: "",
            aiEnabled = prefs[Keys.AI_ENABLED] ?: false,
            aiProvider = prefs[Keys.AI_PROVIDER] ?: AiProvider.QWEN.id,
            apiKey = prefs[Keys.API_KEY] ?: "",
            apiBaseUrl = prefs[Keys.API_BASE_URL] ?: AiProvider.QWEN.defaultBaseUrl,
            modelName = prefs[Keys.MODEL_NAME] ?: AiProvider.QWEN.defaultModel,
            themeMode = prefs[Keys.THEME_MODE] ?: 0,
            languageMode = prefs[Keys.LANGUAGE_MODE] ?: "system"
        )
    }

    suspend fun updateNickname(nickname: String) {
        context.dataStore.edit { it[Keys.NICKNAME] = nickname }
    }

    suspend fun updateAiEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AI_ENABLED] = enabled }
    }

    suspend fun updateAiProvider(providerId: String) {
        val provider = AiProvider.fromId(providerId)
        context.dataStore.edit { prefs ->
            prefs[Keys.AI_PROVIDER] = provider.id
            // 切换提供商时同步默认 BaseUrl + Model，若用户已自定义过则用用户值
            if (prefs[Keys.API_BASE_URL].isNullOrBlank()) {
                prefs[Keys.API_BASE_URL] = provider.defaultBaseUrl
            }
            if (prefs[Keys.MODEL_NAME].isNullOrBlank()) {
                prefs[Keys.MODEL_NAME] = provider.defaultModel
            }
        }
    }

    suspend fun updateApiKey(key: String) {
        context.dataStore.edit { it[Keys.API_KEY] = key }
    }

    suspend fun updateApiBaseUrl(url: String) {
        context.dataStore.edit { it[Keys.API_BASE_URL] = url }
    }

    suspend fun updateModelName(name: String) {
        context.dataStore.edit { it[Keys.MODEL_NAME] = name }
    }

    /** v1.2.0 主题模式：0=跟随系统 1=白天 2=黑夜 */
    suspend fun updateThemeMode(mode: Int) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode }
    }

    /** v1.3.0 语言模式："system"|"zh"|"en"|... */
    suspend fun updateLanguageMode(mode: String) {
        context.dataStore.edit { it[Keys.LANGUAGE_MODE] = mode }
    }

    /**
     * 切换提供商时重置 BaseUrl + Model 为该提供商默认值
     */
    suspend fun resetToProviderDefaults(providerId: String) {
        val provider = AiProvider.fromId(providerId)
        context.dataStore.edit { prefs ->
            prefs[Keys.AI_PROVIDER] = provider.id
            prefs[Keys.API_BASE_URL] = provider.defaultBaseUrl
            prefs[Keys.MODEL_NAME] = provider.defaultModel
        }
    }
}
