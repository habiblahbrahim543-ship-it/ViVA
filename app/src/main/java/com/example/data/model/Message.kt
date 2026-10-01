package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String,
    val receiverId: String,
    val text: String,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
