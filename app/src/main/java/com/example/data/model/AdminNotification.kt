package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admin_notifications")
data class AdminNotification(
    @PrimaryKey
    val id: String,
    val type: String = "VERIFICATION_REQUEST", // "VERIFICATION_REQUEST", "USER_REPORT", "INFO_SUBMITTED"
    val requestId: String = "",
    val userId: String,
    val username: String,
    val message: String,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
