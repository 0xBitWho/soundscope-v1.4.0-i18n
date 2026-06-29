package com.soundscope.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 测量记录 — 基于 PRD 2.7 数据模型
 */
@Entity(tableName = "decibel_records")
data class DecibelRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,           // 测量时间
    val maxDb: Float,              // 最大分贝
    val minDb: Float,              // 最小分贝
    val avgDb: Float,              // 平均分贝
    val duration: Long,            // 测量时长（秒）
    val scene: String = "general", // 场景标签
    val timeSeries: String = "[]"  // 时间序列数据（JSON）
)
