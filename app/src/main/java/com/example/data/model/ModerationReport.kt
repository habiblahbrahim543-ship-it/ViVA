package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "moderation_reports")
data class ModerationReport(
    @PrimaryKey
    val id: String,
    val targetType: String, // "VIDEO", "USER", "COMMENT"
    val targetId: String,
    val reporterId: String,
    val reporterName: String,
    val reason: String, // "Spam", "Harassment", "Violence", "Illegal content", "Copyright", "Other"
    val notes: String = "",
    val status: String = "PENDING", // "PENDING", "RESOLVED", "DISMISSED"
    val timestamp: Long = System.currentTimeMillis()
)
