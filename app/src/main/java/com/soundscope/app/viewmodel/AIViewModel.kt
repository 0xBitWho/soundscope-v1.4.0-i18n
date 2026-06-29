package com.soundscope.app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.soundscope.app.R
import com.soundscope.app.ai.AIAnalysisReport
import com.soundscope.app.ai.AIRepository
import com.soundscope.app.ai.AIResult
import com.soundscope.app.ai.ChatMessage
import com.soundscope.app.ai.ChatResult
import com.soundscope.app.data.DecibelRecord
import com.soundscope.app.data.UserPreferences
import com.soundscope.app.data.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * AI 视图模型 — 管理 AI 分析与对话状态
 *
 * v1.1.0 新增，对应 PRD F07 AI 噪音分析
 * v1.3.0 国际化：传入 Application context 给 AIRepository，错误兜底文案国际化
 */
class AIViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AIRepository()
    private val prefsRepository = UserPreferencesRepository(application)
    private val appContext = application.applicationContext

    // 当前分析的记录
    private val _currentRecord = MutableStateFlow<DecibelRecord?>(null)
    val currentRecord: StateFlow<DecibelRecord?> = _currentRecord.asStateFlow()

    // AI 分析结果
    private val _aiResult = MutableStateFlow<AIResult>(AIResult.Idle)
    val aiResult: StateFlow<AIResult> = _aiResult.asStateFlow()

    // 对话历史（不持久化，会话内有效）
    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()

    // 对话结果（最新一条 assistant 回复的加载状态）
    private val _chatResult = MutableStateFlow<ChatResult>(ChatResult.Success(""))
    val chatResult: StateFlow<ChatResult> = _chatResult.asStateFlow()

    // 用户偏好（供 UI 判断是否已配置）
    val userPreferences = prefsRepository.userPreferences

    /**
     * 设置要分析的记录（从历史/测量结果进入 AI 页面时调用）
     */
    fun setRecord(record: DecibelRecord) {
        _currentRecord.value = record
        _aiResult.value = AIResult.Idle
        _chatHistory.value = emptyList()
        _chatResult.value = ChatResult.Success("")
    }

    /**
     * 触发 AI 分析
     */
    fun analyze() {
        val record = _currentRecord.value ?: return
        _aiResult.value = AIResult.Loading

        viewModelScope.launch {
            try {
                val prefs = prefsRepository.userPreferences.first()
                val report = withContext(Dispatchers.IO) {
                    repository.analyze(record, prefs, appContext)
                }
                _aiResult.value = AIResult.Success(report)
            } catch (e: Exception) {
                Log.e("AIViewModel", "Analysis failed", e)
                _aiResult.value = AIResult.Error(e.message ?: appContext.getString(R.string.error_analyze_fail))
            }
        }
    }

    /**
     * 用户追问
     */
    fun ask(question: String) {
        val record = _currentRecord.value ?: return
        if (question.isBlank()) return

        val newHistory = _chatHistory.value + ChatMessage("user", question)
        _chatHistory.value = newHistory
        _chatResult.value = ChatResult.Loading

        viewModelScope.launch {
            try {
                val prefs = prefsRepository.userPreferences.first()
                val answer = withContext(Dispatchers.IO) {
                    repository.ask(question, record, _chatHistory.value, prefs, appContext)
                }
                _chatHistory.value = _chatHistory.value + ChatMessage("assistant", answer)
                _chatResult.value = ChatResult.Success(answer)
            } catch (e: Exception) {
                Log.e("AIViewModel", "Ask failed", e)
                _chatResult.value = ChatResult.Error(e.message ?: appContext.getString(R.string.error_request_fail))
            }
        }
    }

    /**
     * 重置 AI 状态
     */
    fun reset() {
        _currentRecord.value = null
        _aiResult.value = AIResult.Idle
        _chatHistory.value = emptyList()
        _chatResult.value = ChatResult.Success("")
    }
}
