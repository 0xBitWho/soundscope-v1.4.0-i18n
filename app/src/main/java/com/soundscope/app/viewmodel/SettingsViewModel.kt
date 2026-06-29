package com.soundscope.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.soundscope.app.data.AiProvider
import com.soundscope.app.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * 设置视图模型 — v1.1.0 新增
 *
 * 管理：
 * - 用户昵称
 * - 大模型配置（提供商/Key/BaseUrl/Model/启用）
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserPreferencesRepository(application)

    val userPreferences: StateFlow<com.soundscope.app.data.UserPreferences> =
        repository.userPreferences.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = com.soundscope.app.data.UserPreferences()
        )

    fun updateNickname(nickname: String) = viewModelScope.launch {
        repository.updateNickname(nickname)
    }

    fun updateAiEnabled(enabled: Boolean) = viewModelScope.launch {
        repository.updateAiEnabled(enabled)
    }

    fun updateAiProvider(provider: AiProvider) = viewModelScope.launch {
        repository.resetToProviderDefaults(provider.id)
    }

    fun updateApiKey(key: String) = viewModelScope.launch {
        repository.updateApiKey(key)
    }

    fun updateApiBaseUrl(url: String) = viewModelScope.launch {
        repository.updateApiBaseUrl(url)
    }

    fun updateModelName(name: String) = viewModelScope.launch {
        repository.updateModelName(name)
    }

    /** v1.2.0 主题模式：0=跟随系统 1=白天 2=黑夜 */
    fun updateThemeMode(mode: Int) = viewModelScope.launch {
        repository.updateThemeMode(mode)
    }

    /** v1.3.0 语言模式："system"|"zh"|"en"|... */
    fun updateLanguage(mode: String) = viewModelScope.launch {
        repository.updateLanguageMode(mode)
    }
}
