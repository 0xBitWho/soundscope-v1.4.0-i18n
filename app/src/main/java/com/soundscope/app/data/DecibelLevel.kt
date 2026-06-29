package com.soundscope.app.data

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.soundscope.app.R

/**
 * 分贝等级分类 — 基于 PRD 2.6 分贝参考标准
 *
 * v1.3.0 国际化：label/description 改为 @StringRes 资源 ID
 */
enum class DecibelLevel(
    val minDb: Int,
    val maxDb: Int,
    @StringRes val labelRes: Int,
    @StringRes val descriptionRes: Int,
    val color: Color
) {
    SILENCE(0, 20, R.string.db_level_silence_label, R.string.db_level_silence_desc, Color(0xFF1A237E)),
    QUIET(20, 30, R.string.db_level_quiet_label, R.string.db_level_quiet_desc, Color(0xFF1B5E20)),
    FAIRLY_QUIET(30, 40, R.string.db_level_fairly_quiet_label, R.string.db_level_fairly_quiet_desc, Color(0xFF4CAF50)),
    MODERATE(40, 50, R.string.db_level_moderate_label, R.string.db_level_moderate_desc, Color(0xFF8BC34A)),
    AVERAGE(50, 60, R.string.db_level_average_label, R.string.db_level_average_desc, Color(0xFFFFEB3B)),
    LOUD(60, 70, R.string.db_level_loud_label, R.string.db_level_loud_desc, Color(0xFFFFC107)),
    VERY_LOUD(70, 80, R.string.db_level_very_loud_label, R.string.db_level_very_loud_desc, Color(0xFFFF9800)),
    NOISY(80, 90, R.string.db_level_noisy_label, R.string.db_level_noisy_desc, Color(0xFFFF5722)),
    EXTREMELY_LOUD(90, 100, R.string.db_level_extremely_loud_label, R.string.db_level_extremely_loud_desc, Color(0xFFF44336)),
    DANGEROUS(100, 200, R.string.db_level_dangerous_label, R.string.db_level_dangerous_desc, Color(0xFFB71C1C));

    companion object {
        fun fromDb(db: Float): DecibelLevel {
            val dbInt = db.toInt()
            return entries.find { dbInt >= it.minDb && dbInt < it.maxDb } ?: DANGEROUS
        }
    }
}

/**
 * 场景模式 — 基于 PRD F08
 *
 * 每个场景有独立的：
 * - recommendedMaxDb 推荐最大分贝（用于超标判定与仪表盘阈值线）
 * - standard   引用的标准来源
 * - scenario   适用场景描述
 * - tip        场景化提示文案
 * - themeColor 场景主题色（影响 UI 强调色）
 *
 * v1.3.0 国际化：label/standard/scenario/tip 改为 @StringRes 资源 ID
 */
enum class SceneMode(
    val tag: String,
    @StringRes val labelRes: Int,
    val recommendedMaxDb: Int,
    val icon: String,
    @StringRes val standardRes: Int,
    @StringRes val scenarioRes: Int,
    @StringRes val tipRes: Int,
    val themeColor: Color
) {
    GENERAL(
        "general", R.string.scene_general_label, 60, "🏠",
        R.string.scene_general_standard,
        R.string.scene_general_scenario,
        R.string.scene_general_tip,
        Color(0xFF2196F3)
    ),
    SLEEP(
        "sleep", R.string.scene_sleep_label, 30, "😴",
        R.string.scene_sleep_standard,
        R.string.scene_sleep_scenario,
        R.string.scene_sleep_tip,
        Color(0xFF5C6BC0)
    ),
    STUDY(
        "study", R.string.scene_study_label, 35, "📚",
        R.string.scene_study_standard,
        R.string.scene_study_scenario,
        R.string.scene_study_tip,
        Color(0xFF26A69A)
    ),
    OFFICE(
        "office", R.string.scene_office_label, 50, "💼",
        R.string.scene_office_standard,
        R.string.scene_office_scenario,
        R.string.scene_office_tip,
        Color(0xFF7E57C2)
    ),
    CONSTRUCTION(
        "construction", R.string.scene_construction_label, 85, "🏗️",
        R.string.scene_construction_standard,
        R.string.scene_construction_scenario,
        R.string.scene_construction_tip,
        Color(0xFFEF6C00)
    );

    companion object {
        fun fromTag(tag: String): SceneMode = entries.find { it.tag == tag } ?: GENERAL
    }
}
