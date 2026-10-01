package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocked_users")
data class BlockedUser(
    @PrimaryKey
    val id: String,
    val userId: String,
    val blockedUserId: String,
    val blockedUsername: String,
    val blockedAvatarUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
