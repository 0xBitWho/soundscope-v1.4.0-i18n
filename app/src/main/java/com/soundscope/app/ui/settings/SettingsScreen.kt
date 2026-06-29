package com.soundscope.app.ui.settings

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soundscope.app.R
import com.soundscope.app.data.AiProvider
import com.soundscope.app.util.LocaleManager
import com.soundscope.app.viewmodel.SettingsViewModel

/**
 * 设置界面 — v1.1.0 新增
 *
 * 包含五个分区：
 * 1. 用户管理：昵称
 * 2. 外观：主题模式
 * 3. 语言：语言切换（v1.3.0 新增）
 * 4. AI 大模型设置：提供商 / API Key / BaseUrl / 模型 / 启用开关
 * 5. 关于：版本号
 *
 * v1.3.0 国际化：所有硬编码中文替换为 stringResource()，新增语言选择分区
 */
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onExit: () -> Unit = {}) {
    val prefs by viewModel.userPreferences.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // ✅ v1.2.0 顶部 Slogan
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.settings_about_app_value),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.app_slogan),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ===== 1. 用户管理 =====
        SectionHeader(icon = Icons.Default.AccountCircle, title = stringResource(R.string.settings_section_user))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                var nickname by remember(prefs.nickname) { mutableStateOf(prefs.nickname) }
                Text(
                    text = stringResource(R.string.settings_nickname),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = nickname,
                    onValueChange = {
                        nickname = it
                        viewModel.updateNickname(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.settings_nickname_placeholder)) },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.settings_nickname_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ===== 2. 外观（白天/黑夜模式） =====  v1.2.0 新增
        SectionHeader(icon = Icons.Default.DarkMode, title = stringResource(R.string.settings_section_appearance))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.settings_theme_mode),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                // 三选项：跟随系统 / 白天 / 黑夜
                ThemeOptionRow(
                    label = stringResource(R.string.settings_theme_system),
                    icon = Icons.Default.Build,
                    selected = prefs.themeMode == 0,
                    onClick = { viewModel.updateThemeMode(0) }
                )
                ThemeOptionRow(
                    label = stringResource(R.string.settings_theme_light),
                    icon = Icons.Default.WbSunny,
                    selected = prefs.themeMode == 1,
                    onClick = { viewModel.updateThemeMode(1) }
                )
                ThemeOptionRow(
                    label = stringResource(R.string.settings_theme_dark),
                    icon = Icons.Default.DarkMode,
                    selected = prefs.themeMode == 2,
                    onClick = { viewModel.updateThemeMode(2) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ===== 3. 语言（v1.3.0 新增，v1.4.0 扩展至 9 种语言） =====
        SectionHeader(icon = Icons.Default.Language, title = stringResource(R.string.settings_section_language))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // v1.4.0 支持全部 9 种语言 + 跟随系统
                LanguageOptionRow(
                    label = stringResource(R.string.lang_system),
                    selected = prefs.languageMode == "system",
                    onClick = {
                        viewModel.updateLanguage("system")
                        LocaleManager.applyLanguage("system")
                    }
                )
                LanguageOptionRow(
                    label = stringResource(R.string.lang_chinese),
                    selected = prefs.languageMode == "zh",
                    onClick = {
                        viewModel.updateLanguage("zh")
                        LocaleManager.applyLanguage("zh")
                    }
                )
                LanguageOptionRow(
                    label = stringResource(R.string.lang_english),
                    selected = prefs.languageMode == "en",
                    onClick = {
                        viewModel.updateLanguage("en")
                        LocaleManager.applyLanguage("en")
                    }
                )
                LanguageOptionRow(
                    label = stringResource(R.string.lang_traditional_chinese),
                    selected = prefs.languageMode == "zh-TW",
                    onClick = {
                        viewModel.updateLanguage("zh-TW")
                        LocaleManager.applyLanguage("zh-TW")
                    }
                )
                LanguageOptionRow(
                    label = stringResource(R.string.lang_french),
                    selected = prefs.languageMode == "fr",
                    onClick = {
                        viewModel.updateLanguage("fr")
                        LocaleManager.applyLanguage("fr")
                    }
                )
                LanguageOptionRow(
                    label = stringResource(R.string.lang_portuguese),
                    selected = prefs.languageMode == "pt",
                    onClick = {
                        viewModel.updateLanguage("pt")
                        LocaleManager.applyLanguage("pt")
                    }
                )
                LanguageOptionRow(
                    label = stringResource(R.string.lang_spanish),
                    selected = prefs.languageMode == "es",
                    onClick = {
                        viewModel.updateLanguage("es")
                        LocaleManager.applyLanguage("es")
                    }
                )
                LanguageOptionRow(
                    label = stringResource(R.string.lang_arabic),
                    selected = prefs.languageMode == "ar",
                    onClick = {
                        viewModel.updateLanguage("ar")
                        LocaleManager.applyLanguage("ar")
                    }
                )
                LanguageOptionRow(
                    label = stringResource(R.string.lang_russian),
                    selected = prefs.languageMode == "ru",
                    onClick = {
                        viewModel.updateLanguage("ru")
                        LocaleManager.applyLanguage("ru")
                    }
                )
                LanguageOptionRow(
                    label = stringResource(R.string.lang_hindi),
                    selected = prefs.languageMode == "hi",
                    onClick = {
                        viewModel.updateLanguage("hi")
                        LocaleManager.applyLanguage("hi")
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ===== 4. AI 大模型设置 =====
        SectionHeader(icon = Icons.Default.Psychology, title = stringResource(R.string.settings_section_ai))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // 启用开关
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.settings_ai_enable),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = stringResource(R.string.settings_ai_enable_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = prefs.aiEnabled,
                        onCheckedChange = { viewModel.updateAiEnabled(it) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // 提供商选择
                Text(
                    text = stringResource(R.string.settings_ai_provider),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                AiProvider.entries.forEach { provider ->
                    ProviderOption(
                        provider = provider,
                        selected = prefs.aiProvider == provider.id,
                        onClick = { viewModel.updateAiProvider(provider) }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // API Key
                var showKey by remember { mutableStateOf(false) }
                var apiKey by remember(prefs.apiKey) { mutableStateOf(prefs.apiKey) }
                Text(
                    text = stringResource(R.string.settings_api_key),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = {
                        apiKey = it
                        viewModel.updateApiKey(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.settings_api_key_placeholder)) },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        IconButton(onClick = { showKey = !showKey }) {
                            Icon(
                                if (showKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showKey) stringResource(R.string.settings_hide) else stringResource(R.string.settings_show)
                            )
                        }
                    },
                    visualTransformation = if (showKey) VisualTransformation.None
                    else PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Base URL
                var baseUrl by remember(prefs.apiBaseUrl) { mutableStateOf(prefs.apiBaseUrl) }
                Text(
                    text = stringResource(R.string.settings_api_base_url),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = baseUrl,
                    onValueChange = {
                        baseUrl = it
                        viewModel.updateApiBaseUrl(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.settings_api_base_url_placeholder)) },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    leadingIcon = { Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 模型名
                var modelName by remember(prefs.modelName) { mutableStateOf(prefs.modelName) }
                Text(
                    text = stringResource(R.string.settings_model_name),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = modelName,
                    onValueChange = {
                        modelName = it
                        viewModel.updateModelName(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.settings_model_name_placeholder)) },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    leadingIcon = { Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.settings_ai_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ===== 5. 关于 =====
        SectionHeader(icon = Icons.Default.Build, title = stringResource(R.string.settings_section_about))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow(label = stringResource(R.string.settings_about_app_name), value = stringResource(R.string.settings_about_app_value))
                InfoRow(label = stringResource(R.string.settings_about_version), value = stringResource(R.string.settings_about_version_value))
                InfoRow(label = stringResource(R.string.settings_about_slogan), value = stringResource(R.string.app_slogan_short))
                InfoRow(label = stringResource(R.string.settings_about_developer), value = stringResource(R.string.settings_about_developer_value))
                InfoRow(label = stringResource(R.string.settings_about_stack), value = stringResource(R.string.settings_about_stack_value))
                InfoRow(label = stringResource(R.string.settings_about_license), value = stringResource(R.string.settings_about_license_value))
                InfoRow(label = stringResource(R.string.settings_about_min_sdk), value = stringResource(R.string.settings_about_min_sdk_value))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ===== 6. 退出应用 =====
        OutlinedButton(
            onClick = onExit,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                Icons.Default.ExitToApp,
                contentDescription = null,
                tint = Color(0xFFD32F2F),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.settings_exit),
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFD32F2F),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.app_thanks),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun SectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun ProviderOption(
    provider: AiProvider,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            else MaterialTheme.colorScheme.surface
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(provider.displayNameRes),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(R.string.settings_default_model, provider.defaultModel),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

/**
 * v1.2.0 主题模式选项行
 */
@Composable
private fun ThemeOptionRow(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onBackground
            )
            if (selected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * v1.3.0 语言选项行
 */
@Composable
private fun LanguageOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Language,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onBackground
            )
            if (selected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
