package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "support_conversations")
data class SupportConversation(
    @PrimaryKey
    val id: String, // e.g. "support_user_me"
    val userId: String,
    val username: String,
    val userDisplayName: String,
    val userAvatar: String,
    val lastMessage: String,
    val lastMessageSenderId: String,
    val lastTimestamp: Long = System.currentTimeMillis(),
    val status: String = "OPEN", // "OPEN", "CLOSED"
    val unreadByAdmin: Boolean = true,
    val unreadByUser: Boolean = false
)
