package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "verification_requests")
data class VerificationRequest(
    @PrimaryKey
    val id: String,
    val userId: String,
    val username: String,
    val fullName: String,
    val category: String, // "Creator", "Artist", "Musician", "Athlete", "Public Figure", "Business", "Organization", "Media", "Other"
    val country: String,
    val reason: String,
    val website: String = "",
    val instagram: String = "",
    val youtube: String = "",
    val tiktok: String = "",
    val otherLinks: String = "",
    val supportingDocuments: String = "",
    val status: String = "pending", // "pending", "under_review", "more_information_required", "approved", "rejected"
    val rejectionReason: String = "",
    val adminNotes: String = "",
    val userAdditionalInfo: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val reviewedAt: Long? = null,
    val reviewedBy: String = ""
)
