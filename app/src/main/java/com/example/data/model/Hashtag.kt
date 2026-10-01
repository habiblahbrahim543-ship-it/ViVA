package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hashtags")
data class Hashtag(
    @PrimaryKey
    val tag: String, // e.g. "dance"
    val videoCount: Long = 0,
    val viewsCount: Long = 0,
    val isTrending: Boolean = false
)
