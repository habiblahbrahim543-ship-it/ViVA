package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey
    val id: String,
    val videoId: String,
    val parentCommentId: String? = null,
    val userId: String,
    val username: String,
    val userAvatar: String,
    val text: String,
    val likesCount: Int = 0,
    val replyCount: Int = 0,
    val isLiked: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
