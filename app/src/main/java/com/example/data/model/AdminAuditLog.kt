package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admin_audit_logs")
data class AdminAuditLog(
    @PrimaryKey
    val id: String,
    val adminId: String,
    val adminUsername: String,
    val action: String, // "APPROVED_VERIFICATION", "REJECTED_VERIFICATION", "SUSPENDED_ACCOUNT", "BANNED_ACCOUNT", "RESTORED_ACCOUNT", "REMOVED_VIDEO", "RESOLVED_REPORT", "DISMISSED_REPORT", "SENT_SUPPORT_MESSAGE", "ENDED_LIVE", "ADDED_ADMIN", "REMOVED_ADMIN"
    val targetId: String,
    val targetType: String, // "USER", "VIDEO", "VERIFICATION", "REPORT", "MESSAGE", "LIVE"
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)
