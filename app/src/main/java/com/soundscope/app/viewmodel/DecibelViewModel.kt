package com.soundscope.app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.soundscope.app.audio.DecibelMeter
import com.soundscope.app.data.AppDatabase
import com.soundscope.app.data.DecibelRecord
import com.soundscope.app.data.SceneMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONArray

data class MeasurementState(
    val isMeasuring: Boolean = false,
    val isPaused: Boolean = false,        // v1.2.0 暂停状态
    val currentDb: Float = 0f,
    val maxDb: Float = 0f,
    val minDb: Float = 0f,
    val avgDb: Float = 0f,
    val durationSeconds: Long = 0L,
    val startTime: Long = 0L,
    val sceneMode: SceneMode = SceneMode.GENERAL
)

class DecibelViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private var decibelMeter: DecibelMeter = DecibelMeter(application)
    private var measureJob: Job? = null
    private val timeSeriesData = mutableListOf<Float>()
    private var dbSum = 0.0
    private var dbCount = 0

    private val _measurementState = MutableStateFlow(MeasurementState())
    val measurementState: StateFlow<MeasurementState> = _measurementState.asStateFlow()

    val allRecords = database.decibelDao().getAllRecords()

    fun setSceneMode(mode: SceneMode) {
        _measurementState.value = _measurementState.value.copy(sceneMode = mode)
    }

    fun startMeasurement() {
        if (_measurementState.value.isMeasuring) return

        try {
            decibelMeter.start()
        } catch (e: Exception) {
            // ✅ 记录日志，便于排查；不再静默吞掉，否则用户看到「点了没反应」
            Log.e("DecibelViewModel", "启动录音失败，请检查麦克风权限是否已授予", e)
            return
        }

        timeSeriesData.clear()
        dbSum = 0.0
        dbCount = 0
        val startTime = System.currentTimeMillis()

        _measurementState.value = _measurementState.value.copy(
            isMeasuring = true,
            currentDb = 0f,
            maxDb = 0f,
            minDb = 0f,
            avgDb = 0f,
            durationSeconds = 0L,
            startTime = startTime
        )

        measureJob = viewModelScope.launch(Dispatchers.IO) {
            decibelMeter.decibelFlow().collectLatest { db ->
                if (!_measurementState.value.isMeasuring) return@collectLatest
                // 暂停时不更新数据（DecibelMeter 已不发射，但这里二次保险）
                if (_measurementState.value.isPaused) return@collectLatest

                timeSeriesData.add(db)
                if (timeSeriesData.size > 600) {
                    // 限制时间序列数据量，最多保存 60 秒的数据
                    timeSeriesData.removeAt(0)
                }

                dbSum += db
                dbCount++

                val currentMax = if (db > _measurementState.value.maxDb) db else _measurementState.value.maxDb
                val currentMin = if (_measurementState.value.minDb == 0f) db else minOf(db, _measurementState.value.minDb)
                val currentAvg = (dbSum / dbCount).toFloat()
                val elapsed = (System.currentTimeMillis() - startTime) / 1000

                _measurementState.value = _measurementState.value.copy(
                    currentDb = db,
                    maxDb = currentMax,
                    minDb = currentMin,
                    avgDb = currentAvg,
                    durationSeconds = elapsed
                )
            }
        }
    }

    fun stopMeasurement() {
        if (!_measurementState.value.isMeasuring) return

        measureJob?.cancel()
        decibelMeter.stop()

        val state = _measurementState.value
        val duration = (System.currentTimeMillis() - state.startTime) / 1000

        // 保存记录到数据库
        if (duration > 0 && dbCount > 0) {
            val record = DecibelRecord(
                timestamp = state.startTime,
                maxDb = state.maxDb,
                minDb = state.minDb,
                avgDb = state.avgDb,
                duration = duration,
                scene = state.sceneMode.tag,
                timeSeries = timeSeriesToJson(timeSeriesData.toList())
            )
            viewModelScope.launch(Dispatchers.IO) {
                database.decibelDao().insertRecord(record)
            }
        }

        _measurementState.value = _measurementState.value.copy(
            isMeasuring = false,
            isPaused = false,
            currentDb = 0f
        )
    }

    /**
     * v1.2.0 暂停测量 — recorder 不释放，仅停止数据更新
     */
    fun pauseMeasurement() {
        if (!_measurementState.value.isMeasuring || _measurementState.value.isPaused) return
        decibelMeter.pause()
        _measurementState.value = _measurementState.value.copy(isPaused = true)
    }

    /**
     * v1.2.0 继续测量
     */
    fun resumeMeasurement() {
        if (!_measurementState.value.isMeasuring || !_measurementState.value.isPaused) return
        decibelMeter.resume()
        _measurementState.value = _measurementState.value.copy(isPaused = false)
    }

    fun deleteRecord(record: DecibelRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            database.decibelDao().deleteRecord(record)
        }
    }

    fun deleteAllRecords() {
        viewModelScope.launch(Dispatchers.IO) {
            database.decibelDao().deleteAllRecords()
        }
    }

    private fun timeSeriesToJson(data: List<Float>): String {
        val jsonArray = JSONArray()
        // 降采样：最多保存 100 个数据点
        val step = if (data.size > 100) data.size / 100 else 1
        var i = 0
        while (i < data.size) {
            jsonArray.put(data[i])
            i += step
        }
        return jsonArray.toString()
    }

    override fun onCleared() {
        super.onCleared()
        decibelMeter.stop()
    }
}
