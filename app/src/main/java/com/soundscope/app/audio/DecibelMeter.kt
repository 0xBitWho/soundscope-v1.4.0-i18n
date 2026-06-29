package com.soundscope.app.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

/**
 * 分贝测量器 — 使用 MediaRecorder.maxAmplitude 获取环境噪音
 * 基于 PRD F01: 实时分贝检测
 */
class DecibelMeter(private val context: Context) {

    private var recorder: MediaRecorder? = null
    private var isRecording = false
    private var isPaused = false
    private var outputFile: File? = null

    /**
     * 将 MediaRecorder.maxAmplitude (0~32767) 转换为分贝值
     * 公式: dB = 20 * log10(amplitude / reference) + calibration
     */
    private fun amplitudeToDb(amplitude: Int): Float {
        if (amplitude <= 0) return 0f
        val db = 20.0 * kotlin.math.log10(amplitude.toDouble() / 32767.0) + 90.0
        return db.toFloat().coerceIn(0f, 120f)
    }

    fun start() {
        if (isRecording) return
        try {
            // ✅ 使用应用缓存目录的临时文件，而非 /dev/null。
            // 部分设备/ROM 限制写入 /dev/null，会导致 prepare()/start() 失败。
            outputFile = File(context.cacheDir, "decibel_temp.3gp").apply {
                if (exists()) delete()
            }
            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.also { rec ->
                rec.setAudioSource(MediaRecorder.AudioSource.MIC)
                rec.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                rec.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                rec.setOutputFile(outputFile!!.absolutePath)
                rec.prepare()
                rec.start()
            }
            isRecording = true
            Log.d("DecibelMeter", "Recorder started successfully")
        } catch (e: Exception) {
            Log.e("DecibelMeter", "Failed to start recorder", e)
            isRecording = false
            // 清理可能已创建的 recorder，避免资源泄漏
            try {
                recorder?.release()
            } catch (ignored: Exception) {
            }
            recorder = null
            throw e
        }
    }

    /**
     * 获取当前分贝值
     */
    fun getDecibel(): Float {
        if (!isRecording) return 0f
        return try {
            val amplitude = recorder?.maxAmplitude ?: 0
            amplitudeToDb(amplitude)
        } catch (e: Exception) {
            Log.e("DecibelMeter", "Failed to get amplitude", e)
            0f
        }
    }

    /**
     * 实时分贝数据流 — 每 100ms 采样一次
     * 暂停时不发射新值（由 DecibelViewModel 控制），但流本身仍存活
     */
    fun decibelFlow(): Flow<Float> = flow {
        while (isRecording) {
            if (!isPaused) {
                emit(getDecibel())
            }
            delay(100)
        }
    }

    /**
     * 暂停采集 — 不释放 recorder，仅停止发射数据
     */
    fun pause() {
        if (!isRecording) return
        isPaused = true
        Log.d("DecibelMeter", "Recorder paused")
    }

    /**
     * 继续采集
     */
    fun resume() {
        if (!isRecording) return
        isPaused = false
        Log.d("DecibelMeter", "Recorder resumed")
    }

    fun isPaused(): Boolean = isPaused

    fun stop() {
        if (!isRecording) return
        isRecording = false
        isPaused = false
        try {
            recorder?.stop()
        } catch (e: Exception) {
            Log.e("DecibelMeter", "Failed to stop recorder", e)
        }
        try {
            recorder?.release()
        } catch (e: Exception) {
            Log.e("DecibelMeter", "Failed to release recorder", e)
        }
        recorder = null
        // 清理临时输出文件
        try {
            outputFile?.takeIf { it.exists() }?.delete()
        } catch (ignored: Exception) {
        }
        outputFile = null
    }

    fun isRunning(): Boolean = isRecording
}
