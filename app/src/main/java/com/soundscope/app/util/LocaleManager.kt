package com.soundscope.app.util

import android.content.Context
import androidx.core.os.LocaleListCompat
import java.util.Locale

/**
 * 语言管理器 — v1.3.0 国际化新增，v1.4.0 扩展至 9 种语言
 *
 * 职责：
 * 1. 应用用户选择的语言到 App
 * 2. 判断当前语言（供 AI 提示词等模块使用）
 * 3. RTL 语言检测（阿拉伯语）
 *
 * v1.4.0 支持的语言代码：
 * - "system"  跟随系统
 * - "zh"      简体中文
 * - "en"      English
 * - "zh-TW"   繁體中文
 * - "fr"      Français
 * - "pt"      Português
 * - "es"      Español
 * - "ar"      العربية（RTL）
 * - "ru"      Русский
 * - "hi"      हिन्दी
 */
object LocaleManager {

    /** 所有支持的语言代码（用于设置页展示与校验） */
    val supportedLanguages = listOf(
        "system", "zh", "en",
        "zh-TW", "fr", "pt", "es", "ar", "ru", "hi"
    )

    /**
     * 应用语言设置
     *
     * @param languageCode  语言代码，"system" 表示跟随系统
     *
     * 使用 AppCompatDelegate.setApplicationLocales 实现，
     * 该 API 会自动重建 Activity 并持久化语言选择（API 13+）。
     */
    fun applyLanguage(languageCode: String) {
        val locales = if (languageCode == "system") {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            val locale = parseLocale(languageCode)
            LocaleListCompat.create(locale)
        }
        androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(locales)
    }

    /**
     * 判断当前是否为英文环境
     * 供 AI 提示词模块使用
     */
    fun isEnglish(context: Context): Boolean {
        val lang = getCurrentLanguage(context)
        return lang == "en"
    }

    /**
     * 判断当前是否为中文环境（简体或繁体）
     */
    fun isChinese(context: Context): Boolean {
        val lang = getCurrentLanguage(context)
        return lang == "zh" || lang.startsWith("zh-")
    }

    /**
     * 判断当前是否为 RTL（从右到左）语言
     * 阿拉伯语等语言需要 RTL 布局
     */
    fun isRtl(context: Context): Boolean {
        val lang = getCurrentLanguage(context)
        return lang == "ar" || lang == "he" || lang == "fa"
    }

    /**
     * 获取当前生效的语言代码
     *
     * 优先级：
     * 1. AppCompatDelegate 设置的应用语言
     * 2. 系统语言
     */
    fun getCurrentLanguage(context: Context): String {
        // 优先取 AppCompatDelegate 设置的语言
        val appLocales = androidx.appcompat.app.AppCompatDelegate.getApplicationLocales()
        if (!appLocales.isEmpty) {
            val locale = appLocales.get(0)
            return locale?.let { toLanguageCode(it) } ?: "system"
        }
        // 降级取系统语言
        val config = context.resources.configuration
        val locale = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            config.locales[0]
        } else {
            @Suppress("DEPRECATION")
            config.locale
        }
        return toLanguageCode(locale)
    }

    /**
     * 将 Locale 转为语言代码
     * zh-CN → "zh", zh-TW → "zh-TW", en-US → "en"
     */
    private fun toLanguageCode(locale: Locale): String {
        val lang = locale.language
        val country = locale.country
        return if (country.isNotEmpty() && lang == "zh") {
            "$lang-$country"
        } else {
            lang
        }
    }

    /**
     * 解析语言代码为 Locale
     * "zh-TW" → Locale("zh", "TW")
     */
    private fun parseLocale(code: String): Locale {
        return if (code.contains("-")) {
            val parts = code.split("-")
            Locale(parts[0], parts[1])
        } else {
            Locale(code)
        }
    }
}
