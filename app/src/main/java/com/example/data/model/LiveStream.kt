package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "live_streams")
data class LiveStream(
    @PrimaryKey
    val id: String,
    val creatorId: String,
    val creatorUsername: String,
    val creatorDisplayName: String,
    val creatorAvatarUrl: String,
    val title: String,
    val viewerCount: Int = 0,
    val reportsCount: Int = 0,
    val status: String = "ACTIVE", // "ACTIVE", "ENDED", "SUSPENDED"
    val startedAt: Long = System.currentTimeMillis()
)
