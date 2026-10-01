package com.example.data.model

import androidx.room.Entity

@Entity(
    tableName = "followers_relations",
    primaryKeys = ["followerId", "followingId"]
)
data class FollowRelation(
    val followerId: String,
    val followingId: String,
    val timestamp: Long = System.currentTimeMillis()
)
