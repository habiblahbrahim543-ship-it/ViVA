package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey
    val id: String,
    val recipientId: String,
    val senderId: String,
    val senderUsername: String,
    val senderAvatar: String,
    val type: String, // "LIKE", "COMMENT", "FOLLOW", "MENTION", "SYSTEM"
    val title: String,
    val message: String,
    val targetVideoId: String? = null,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
