package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class Video(
    @PrimaryKey
    val id: String,
    val creatorId: String,
    val creatorUsername: String,
    val creatorDisplayName: String,
    val creatorAvatarUrl: String,
    val caption: String,
    val hashtags: String, // comma or space separated, e.g. "#viva #dance #trending"
    val soundId: String,
    val soundTitle: String,
    val soundCreator: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val durationSeconds: Int = 15,
    val viewsCount: Long = 0,
    val likesCount: Long = 0,
    val commentsCount: Long = 0,
    val sharesCount: Long = 0,
    val savesCount: Long = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val isFollowingCreator: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val category: String = "Trending",
    val allowComments: Boolean = true,
    val allowDownloads: Boolean = true,
    val isPrivate: Boolean = false
)
