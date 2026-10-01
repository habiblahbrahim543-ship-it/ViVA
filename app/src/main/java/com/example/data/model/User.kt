package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class User(
    @PrimaryKey
    val id: String,
    val username: String,
    val displayName: String,
    val bio: String = "",
    val website: String = "",
    val avatarUrl: String,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val likesCount: Int = 0,
    val videosCount: Int = 0,
    val isVerified: Boolean = false,
    val verificationStatus: String = "unverified", // "unverified", "pending", "under_review", "more_information_required", "approved", "rejected"
    val isFollowing: Boolean = false,
    val isCurrentUser: Boolean = false,
    val role: String = "USER", // "USER", "ADMIN", or "OWNER"
    val accountStatus: String = "ACTIVE", // "ACTIVE", "SUSPENDED", "BANNED"
    val email: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
