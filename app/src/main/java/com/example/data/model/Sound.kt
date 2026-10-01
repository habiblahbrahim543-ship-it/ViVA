package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sounds")
data class Sound(
    @PrimaryKey
    val id: String,
    val title: String,
    val creator: String,
    val durationSeconds: Int = 30,
    val videoCount: Int = 1,
    val audioUrl: String = "",
    val coverUrl: String = ""
)
