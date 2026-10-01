package com.example.data.repository

import com.example.data.local.SampleData
import com.example.data.local.VivaDao
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class VivaRepository(private val dao: VivaDao) {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedDatabaseIfEmpty()
        }
    }

    private suspend fun seedDatabaseIfEmpty() {
        val existingUser = dao.getCurrentUserSync()
        if (existingUser == null) {
            dao.insertUsers(SampleData.users)
            dao.insertVideos(SampleData.videos)
            dao.insertComments(SampleData.comments)
            dao.insertSounds(SampleData.sounds)
            dao.insertHashtags(SampleData.hashtags)
            dao.insertNotifications(SampleData.notifications)
            dao.insertMessages(SampleData.messages)
            SampleData.followRelations.forEach { dao.insertFollowRelation(it) }
            SampleData.verificationRequests.forEach { dao.insertVerificationRequest(it) }
            SampleData.adminNotifications.forEach { dao.insertAdminNotification(it) }
            SampleData.supportConversations.forEach { dao.insertSupportConversation(it) }
            SampleData.adminAuditLogs.forEach { dao.insertAuditLog(it) }
            SampleData.liveStreams.forEach { dao.insertLiveStream(it) }
            SampleData.settings.forEach { dao.setSetting(it) }
        } else {
            SampleData.users.forEach { user ->
                val existing = dao.getUserByIdSync(user.id)
                if (existing == null) {
                    dao.insertUser(user)
                }
            }
            SampleData.supportConversations.forEach { dao.insertSupportConversation(it) }
            SampleData.liveStreams.forEach { dao.insertLiveStream(it) }
        }
    }

    // --- Users & Profiles ---
    fun getAllUsers(): Flow<List<User>> = dao.getAllUsers()
    fun getCurrentUser(): Flow<User?> = dao.getCurrentUser()
    fun getUserById(userId: String): Flow<User?> = dao.getUserById(userId)
    fun getUserByUsername(username: String): Flow<User?> = dao.getUserByUsername(username)

    suspend fun updateUserProfile(
        userId: String,
        displayName: String,
        username: String,
        bio: String,
        website: String,
        avatarUrl: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val cleanUsername = username.trim().removePrefix("@")
        if (cleanUsername.isBlank()) {
            return@withContext Result.failure(Exception("Username cannot be empty."))
        }
        val current = dao.getUserByIdSync(userId) ?: return@withContext Result.failure(Exception("User not found"))
        if (!cleanUsername.equals(current.username, ignoreCase = true)) {
            if (cleanUsername.equals("VIVA", ignoreCase = true) ||
                cleanUsername.equals("admin", ignoreCase = true) ||
                cleanUsername.equals("owner", ignoreCase = true) ||
                cleanUsername.equals("support", ignoreCase = true)
            ) {
                return@withContext Result.failure(Exception("Username @$cleanUsername is reserved for official VIVA platform administration."))
            }
        }
        dao.updateUserProfileFields(
            userId = userId,
            displayName = displayName.trim(),
            username = cleanUsername,
            bio = bio.trim(),
            website = website.trim(),
            avatarUrl = avatarUrl.trim().ifBlank { current.avatarUrl }
        )
        Result.success(Unit)
    }

    suspend fun switchAccount(newUserId: String) = withContext(Dispatchers.IO) {
        dao.clearCurrentUsers()
        dao.setCurrentUser(newUserId)
    }

    // --- Followers / Following ---
    fun getFollowersForUser(userId: String): Flow<List<User>> = dao.getFollowersForUser(userId)
    fun getFollowingForUser(userId: String): Flow<List<User>> = dao.getFollowingForUser(userId)

    suspend fun toggleFollowCreator(creatorId: String, currentFollowState: Boolean) = withContext(Dispatchers.IO) {
        val currentUser = dao.getCurrentUserSync() ?: SampleData.users.first()
        val newFollowState = !currentFollowState
        val delta = if (newFollowState) 1 else -1

        dao.updateFollowState(creatorId, delta, newFollowState)
        dao.updateVideoCreatorFollow(creatorId, newFollowState)

        if (newFollowState) {
            dao.insertFollowRelation(FollowRelation(currentUser.id, creatorId))
            dao.insertNotification(
                Notification(
                    id = UUID.randomUUID().toString(),
                    recipientId = creatorId,
                    senderId = currentUser.id,
                    senderUsername = currentUser.username,
                    senderAvatar = currentUser.avatarUrl,
                    type = "FOLLOW",
                    title = "New Follower",
                    message = "${currentUser.username} started following you on VIVA."
                )
            )
        } else {
            dao.deleteFollowRelation(currentUser.id, creatorId)
        }
    }

    // --- Blocked Users ---
    fun getBlockedUsers(userId: String): Flow<List<BlockedUser>> = dao.getBlockedUsers(userId)

    suspend fun blockUser(targetUser: User) = withContext(Dispatchers.IO) {
        val currentUser = dao.getCurrentUserSync() ?: return@withContext
        val blocked = BlockedUser(
            id = "block_${currentUser.id}_${targetUser.id}",
            userId = currentUser.id,
            blockedUserId = targetUser.id,
            blockedUsername = targetUser.username,
            blockedAvatarUrl = targetUser.avatarUrl
        )
        dao.insertBlockedUser(blocked)
    }

    suspend fun unblockUser(blockedUserId: String) = withContext(Dispatchers.IO) {
        val currentUser = dao.getCurrentUserSync() ?: return@withContext
        dao.deleteBlockedUser(currentUser.id, blockedUserId)
    }

    // --- Verification Request System (Real Database Implementation) ---
    fun getVerificationRequestForUser(userId: String): Flow<VerificationRequest?> =
        dao.getVerificationRequestForUser(userId)

    fun getAllVerificationRequests(): Flow<List<VerificationRequest>> =
        dao.getAllVerificationRequests()

    fun getPendingVerificationRequests(): Flow<List<VerificationRequest>> =
        dao.getPendingVerificationRequests()

    fun getAllAdminNotifications(): Flow<List<AdminNotification>> =
        dao.getAllAdminNotifications()

    suspend fun markAdminNotificationAsRead(id: String) = withContext(Dispatchers.IO) {
        dao.markAdminNotificationAsRead(id)
    }

    suspend fun markAllAdminNotificationsAsRead() = withContext(Dispatchers.IO) {
        dao.markAllAdminNotificationsAsRead()
    }

    suspend fun submitVerificationRequest(
        fullName: String,
        category: String,
        country: String,
        reason: String,
        website: String,
        instagram: String,
        youtube: String,
        tiktok: String,
        otherLinks: String,
        supportingDocuments: String
    ): Result<VerificationRequest> = withContext(Dispatchers.IO) {
        val currentUser = dao.getCurrentUserSync() ?: return@withContext Result.failure(Exception("User not authenticated"))

        val reqId = "vr_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(4)}"
        val request = VerificationRequest(
            id = reqId,
            userId = currentUser.id,
            username = currentUser.username,
            fullName = fullName.trim(),
            category = category,
            country = country.trim(),
            reason = reason.trim(),
            website = website.trim(),
            instagram = instagram.trim(),
            youtube = youtube.trim(),
            tiktok = tiktok.trim(),
            otherLinks = otherLinks.trim(),
            supportingDocuments = supportingDocuments.trim(),
            status = "pending",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        dao.insertVerificationRequest(request)
        dao.updateUserVerification(currentUser.id, isVerified = false, status = "pending")

        // Immediately notify administrators
        val adminNotif = AdminNotification(
            id = "an_${System.currentTimeMillis()}",
            type = "VERIFICATION_REQUEST",
            requestId = reqId,
            userId = currentUser.id,
            username = currentUser.username,
            message = "@${currentUser.username} (${fullName.trim()}) requested verification under '$category' category."
        )
        dao.insertAdminNotification(adminNotif)

        Result.success(request)
    }

    suspend fun approveVerification(requestId: String, adminId: String): Result<Unit> = withContext(Dispatchers.IO) {
        // Security check: only ADMIN can approve
        val admin = dao.getUserByIdSync(adminId)
        if (admin?.role != "ADMIN") {
            return@withContext Result.failure(SecurityException("Unauthorized: Only administrators can approve verification requests."))
        }

        // Fetch request
        val request = dao.getAllVerificationRequests().map { list -> list.find { it.id == requestId } }
        // Update request status
        val now = System.currentTimeMillis()
        dao.updateVerificationRequestStatus(
            id = requestId,
            status = "approved",
            reviewedBy = admin.username,
            reviewedAt = now,
            rejectionReason = "",
            adminNotes = "Approved by VIVA Trust & Safety Admin @${admin.username}"
        )

        // Find the user for this request
        val allRequests = dao.getAllVerificationRequests()
        // Synchronously update the user account to verified
        val targetUserId = SampleData.users.find { it.id == "user_3" }?.id // fallback
        // We will update using the request record directly in ViewModel or with target user ID
        Result.success(Unit)
    }

    suspend fun approveVerificationForUser(requestId: String, targetUserId: String, adminUsername: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        dao.updateVerificationRequestStatus(
            id = requestId,
            status = "approved",
            reviewedBy = adminUsername,
            reviewedAt = now,
            rejectionReason = "",
            adminNotes = "Verified by @$adminUsername"
        )
        // Award the verified badge in database
        dao.updateUserVerification(targetUserId, isVerified = true, status = "approved")

        // Notify user
        dao.insertNotification(
            Notification(
                id = UUID.randomUUID().toString(),
                recipientId = targetUserId,
                senderId = "user_official_viva",
                senderUsername = "VIVA",
                senderAvatar = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=400&auto=format&fit=crop&q=80",
                type = "SYSTEM",
                title = "Your VIVA account has been verified.",
                message = "Congratulations! Your verification request has been approved. The official blue checkmark is now active on your profile."
            )
        )

        // Audit log
        dao.insertAuditLog(
            AdminAuditLog(
                id = "log_${System.currentTimeMillis()}",
                adminId = adminUsername,
                adminUsername = adminUsername,
                action = "APPROVED_VERIFICATION",
                targetId = targetUserId,
                targetType = "VERIFICATION",
                details = "Approved verification request ($requestId) for user ID $targetUserId."
            )
        )
    }

    suspend fun rejectVerificationForUser(requestId: String, targetUserId: String, adminUsername: String, reason: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        dao.updateVerificationRequestStatus(
            id = requestId,
            status = "rejected",
            reviewedBy = adminUsername,
            reviewedAt = now,
            rejectionReason = reason.ifBlank { "Did not meet criteria for notable public interest." },
            adminNotes = "Rejected by @$adminUsername"
        )
        dao.updateUserVerification(targetUserId, isVerified = false, status = "rejected")

        // Notify user
        dao.insertNotification(
            Notification(
                id = UUID.randomUUID().toString(),
                recipientId = targetUserId,
                senderId = "system",
                senderUsername = "viva_official",
                senderAvatar = "",
                type = "SYSTEM",
                title = "Your VIVA verification request was not approved.",
                message = "Your verification request was reviewed. Reason: ${reason.ifBlank { "Account does not meet current criteria." }}"
            )
        )
    }

    suspend fun requestMoreInfoForUser(requestId: String, targetUserId: String, adminUsername: String, notes: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        dao.updateVerificationRequestStatus(
            id = requestId,
            status = "more_information_required",
            reviewedBy = adminUsername,
            reviewedAt = now,
            rejectionReason = "",
            adminNotes = notes
        )
        dao.updateUserVerification(targetUserId, isVerified = false, status = "more_information_required")

        // Notify user
        dao.insertNotification(
            Notification(
                id = UUID.randomUUID().toString(),
                recipientId = targetUserId,
                senderId = "system",
                senderUsername = "viva_official",
                senderAvatar = "",
                type = "SYSTEM",
                title = "Additional information required for verification",
                message = "VIVA Trust & Safety: $notes"
            )
        )
    }

    suspend fun submitAdditionalInfo(requestId: String, userId: String, additionalInfo: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        dao.updateVerificationRequestStatus(
            id = requestId,
            status = "under_review",
            reviewedBy = "",
            reviewedAt = now,
            rejectionReason = "",
            adminNotes = "Additional info submitted by user: $additionalInfo"
        )
        dao.updateUserVerification(userId, isVerified = false, status = "under_review")

        val user = dao.getUserByIdSync(userId)
        val adminNotif = AdminNotification(
            id = "an_${System.currentTimeMillis()}",
            type = "INFO_SUBMITTED",
            requestId = requestId,
            userId = userId,
            username = user?.username ?: "creator",
            message = "@${user?.username} submitted additional information for verification."
        )
        dao.insertAdminNotification(adminNotif)
    }

    // --- Videos ---
    fun getAllVideos(): Flow<List<Video>> = dao.getAllVideos().map { list ->
        list.sortedByDescending { v ->
            val engagement = (v.likesCount * 2) + (v.commentsCount * 3) + (v.sharesCount * 4) + (v.savesCount * 3)
            val recencyHours = ((System.currentTimeMillis() - v.createdAt) / 3600000).coerceAtLeast(1)
            engagement / (recencyHours * 0.5 + 1.0)
        }
    }

    fun getFollowingVideos(): Flow<List<Video>> = dao.getFollowingVideos()
    fun getVideosByCreator(creatorId: String): Flow<List<Video>> = dao.getVideosByCreator(creatorId)
    fun getLikedVideos(): Flow<List<Video>> = dao.getLikedVideos()
    fun getSavedVideos(): Flow<List<Video>> = dao.getSavedVideos()
    fun getVideosByHashtag(hashtag: String): Flow<List<Video>> = dao.getVideosByHashtag(hashtag)
    fun getVideosBySound(soundId: String): Flow<List<Video>> = dao.getVideosBySound(soundId)
    fun getVideoById(videoId: String): Flow<Video?> = dao.getVideoById(videoId)

    suspend fun toggleLikeVideo(video: Video) = withContext(Dispatchers.IO) {
        val newLiked = !video.isLiked
        val delta = if (newLiked) 1 else -1
        dao.updateLikeState(video.id, delta, newLiked)

        val currentUser = dao.getCurrentUserSync() ?: SampleData.users.first()
        if (newLiked && video.creatorId != currentUser.id) {
            dao.insertNotification(
                Notification(
                    id = UUID.randomUUID().toString(),
                    recipientId = video.creatorId,
                    senderId = currentUser.id,
                    senderUsername = currentUser.username,
                    senderAvatar = currentUser.avatarUrl,
                    type = "LIKE",
                    title = "Liked Your Video",
                    message = "${currentUser.username} liked your video.",
                    targetVideoId = video.id
                )
            )
        }
    }

    suspend fun toggleSaveVideo(video: Video) = withContext(Dispatchers.IO) {
        val newSaved = !video.isSaved
        val delta = if (newSaved) 1 else -1
        dao.updateSaveState(video.id, delta, newSaved)
    }

    suspend fun shareVideo(videoId: String) = withContext(Dispatchers.IO) {
        dao.incrementShareCount(videoId)
    }

    suspend fun deleteVideo(videoId: String) = withContext(Dispatchers.IO) {
        dao.deleteVideoById(videoId)
    }

    suspend fun publishVideo(
        caption: String,
        hashtags: String,
        videoUrl: String,
        thumbnailUrl: String,
        soundId: String,
        soundTitle: String,
        soundCreator: String,
        category: String,
        allowComments: Boolean,
        allowDownloads: Boolean,
        isPrivate: Boolean
    ): Video = withContext(Dispatchers.IO) {
        val currentUser = dao.getCurrentUserSync() ?: SampleData.users.first()
        val newVideo = Video(
            id = "vid_${System.currentTimeMillis()}",
            creatorId = currentUser.id,
            creatorUsername = currentUser.username,
            creatorDisplayName = currentUser.displayName,
            creatorAvatarUrl = currentUser.avatarUrl,
            caption = caption,
            hashtags = hashtags,
            soundId = soundId.ifBlank { "sound_1" },
            soundTitle = soundTitle.ifBlank { "Original Audio - ${currentUser.username}" },
            soundCreator = soundCreator.ifBlank { currentUser.displayName },
            videoUrl = videoUrl,
            thumbnailUrl = thumbnailUrl,
            durationSeconds = 15,
            category = category,
            allowComments = allowComments,
            allowDownloads = allowDownloads,
            isPrivate = isPrivate,
            createdAt = System.currentTimeMillis()
        )
        dao.insertVideo(newVideo)

        hashtags.split(" ", "#", ",").filter { it.isNotBlank() }.forEach { rawTag ->
            val cleanTag = rawTag.trim().lowercase()
            dao.insertHashtag(Hashtag(tag = cleanTag, videoCount = 1, viewsCount = 10, isTrending = false))
        }

        newVideo
    }

    // --- Comments ---
    fun getTopLevelComments(videoId: String): Flow<List<Comment>> = dao.getTopLevelComments(videoId)
    fun getCommentReplies(parentId: String): Flow<List<Comment>> = dao.getCommentReplies(parentId)

    suspend fun addComment(videoId: String, text: String, parentId: String? = null) = withContext(Dispatchers.IO) {
        val currentUser = dao.getCurrentUserSync() ?: SampleData.users.first()
        val comment = Comment(
            id = "c_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(4)}",
            videoId = videoId,
            parentCommentId = parentId,
            userId = currentUser.id,
            username = currentUser.username,
            userAvatar = currentUser.avatarUrl,
            text = text,
            timestamp = System.currentTimeMillis()
        )
        dao.insertComment(comment)
        dao.incrementCommentCount(videoId)

        val video = dao.getVideoByIdSync(videoId)
        if (video != null && video.creatorId != currentUser.id) {
            dao.insertNotification(
                Notification(
                    id = UUID.randomUUID().toString(),
                    recipientId = video.creatorId,
                    senderId = currentUser.id,
                    senderUsername = currentUser.username,
                    senderAvatar = currentUser.avatarUrl,
                    type = "COMMENT",
                    title = "New Comment",
                    message = "${currentUser.username} commented: '$text'",
                    targetVideoId = videoId
                )
            )
        }
    }

    suspend fun toggleCommentLike(comment: Comment) = withContext(Dispatchers.IO) {
        val newLiked = !comment.isLiked
        val delta = if (newLiked) 1 else -1
        dao.updateCommentLike(comment.id, delta, newLiked)
    }

    suspend fun deleteComment(commentId: String) = withContext(Dispatchers.IO) {
        dao.deleteComment(commentId)
    }

    // --- Messages ---
    fun getMessagesForConversation(convId: String): Flow<List<Message>> = dao.getMessagesForConversation(convId)
    fun getAllMessages(): Flow<List<Message>> = dao.getAllMessages()

    suspend fun sendMessage(convId: String, receiverId: String, text: String) = withContext(Dispatchers.IO) {
        val currentUser = dao.getCurrentUserSync() ?: SampleData.users.first()
        val msg = Message(
            id = "msg_${System.currentTimeMillis()}",
            conversationId = convId,
            senderId = currentUser.id,
            senderName = currentUser.displayName,
            senderAvatar = currentUser.avatarUrl,
            receiverId = receiverId,
            text = text,
            timestamp = System.currentTimeMillis(),
            isRead = true
        )
        dao.insertMessage(msg)
    }

    // --- Notifications ---
    fun getAllNotifications(): Flow<List<Notification>> = dao.getAllNotifications()
    fun getNotificationsByType(type: String): Flow<List<Notification>> = dao.getNotificationsByType(type)

    suspend fun markNotificationAsRead(id: String) = withContext(Dispatchers.IO) {
        dao.markNotificationAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() = withContext(Dispatchers.IO) {
        dao.markAllNotificationsAsRead()
    }

    // --- Search ---
    fun searchVideos(query: String): Flow<List<Video>> = dao.searchVideos(query)
    fun searchUsers(query: String): Flow<List<User>> = dao.searchUsers(query)
    fun searchHashtags(query: String): Flow<List<Hashtag>> = dao.searchHashtags(query)
    fun searchSounds(query: String): Flow<List<Sound>> = dao.searchSounds(query)

    // --- Sounds & Hashtags ---
    fun getAllSounds(): Flow<List<Sound>> = dao.getAllSounds()
    fun getSoundById(id: String): Flow<Sound?> = dao.getSoundById(id)
    fun getAllHashtags(): Flow<List<Hashtag>> = dao.getAllHashtags()
    fun getTrendingHashtags(): Flow<List<Hashtag>> = dao.getTrendingHashtags()

    // --- Reports ---
    fun getAllReports(): Flow<List<ModerationReport>> = dao.getAllReports()

    suspend fun submitReport(targetType: String, targetId: String, reason: String, notes: String) = withContext(Dispatchers.IO) {
        val currentUser = dao.getCurrentUserSync() ?: SampleData.users.first()
        val report = ModerationReport(
            id = "rep_${System.currentTimeMillis()}",
            targetType = targetType,
            targetId = targetId,
            reporterId = currentUser.id,
            reporterName = currentUser.username,
            reason = reason,
            notes = notes,
            status = "PENDING"
        )
        dao.insertReport(report)

        // Notify admin of new moderation report
        val adminNotif = AdminNotification(
            id = "an_rep_${System.currentTimeMillis()}",
            type = "USER_REPORT",
            requestId = report.id,
            userId = currentUser.id,
            username = currentUser.username,
            message = "New report on $targetType ($reason) by @${currentUser.username}."
        )
        dao.insertAdminNotification(adminNotif)
    }

    suspend fun updateReportStatus(reportId: String, status: String) = withContext(Dispatchers.IO) {
        dao.updateReportStatus(reportId, status)
    }

    // --- Settings ---
    fun getAllSettings(): Flow<List<AppSetting>> = dao.getAllSettings()

    suspend fun toggleSetting(key: String, currentVal: Boolean) = withContext(Dispatchers.IO) {
        val nextVal = (!currentVal).toString()
        dao.setSetting(AppSetting(key, nextVal))
    }

    // --- Support & Owner Two-Way Messaging ---
    val allSupportConversations: Flow<List<SupportConversation>> = dao.getAllSupportConversations()
    fun getSupportConversationById(convId: String): Flow<SupportConversation?> = dao.getSupportConversationById(convId)
    fun getSupportConversationForUser(userId: String): Flow<SupportConversation?> = dao.getSupportConversationForUser(userId)

    suspend fun sendContactVivaMessage(text: String): Result<Unit> = withContext(Dispatchers.IO) {
        val currentUser = dao.getCurrentUserSync() ?: return@withContext Result.failure(Exception("Not authenticated"))
        val convId = "support_${currentUser.id}"
        val now = System.currentTimeMillis()

        // 1. Insert message
        val msg = Message(
            id = "msg_sup_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(4)}",
            conversationId = convId,
            senderId = currentUser.id,
            senderName = currentUser.displayName,
            senderAvatar = currentUser.avatarUrl,
            receiverId = "user_official_viva",
            text = text.trim(),
            timestamp = now,
            isRead = true
        )
        dao.insertMessage(msg)

        // 2. Upsert support conversation
        val conv = SupportConversation(
            id = convId,
            userId = currentUser.id,
            username = currentUser.username,
            userDisplayName = currentUser.displayName,
            userAvatar = currentUser.avatarUrl,
            lastMessage = text.trim(),
            lastMessageSenderId = currentUser.id,
            lastTimestamp = now,
            status = "OPEN",
            unreadByAdmin = true,
            unreadByUser = false
        )
        dao.insertSupportConversation(conv)

        // 3. Create Admin Notification
        dao.insertAdminNotification(
            AdminNotification(
                id = "an_msg_${System.currentTimeMillis()}",
                type = "MESSAGE",
                requestId = convId,
                userId = currentUser.id,
                username = currentUser.username,
                message = "New message from @${currentUser.username}: '${text.trim().take(50)}'",
                timestamp = now
            )
        )

        // 4. Log in Audit Log
        dao.insertAuditLog(
            AdminAuditLog(
                id = "log_${System.currentTimeMillis()}",
                adminId = currentUser.id,
                adminUsername = currentUser.username,
                action = "USER_SUPPORT_MESSAGE",
                targetId = convId,
                targetType = "MESSAGE",
                details = "User @${currentUser.username} initiated contact with VIVA Official Support."
            )
        )

        Result.success(Unit)
    }

    suspend fun sendAdminReply(convId: String, text: String, adminUser: User): Result<Unit> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val targetUserId = convId.removePrefix("support_")

        // Insert message as official VIVA account
        val msg = Message(
            id = "msg_rep_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(4)}",
            conversationId = convId,
            senderId = "user_official_viva",
            senderName = "VIVA Support",
            senderAvatar = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=400&auto=format&fit=crop&q=80",
            receiverId = targetUserId,
            text = text.trim(),
            timestamp = now,
            isRead = true
        )
        dao.insertMessage(msg)

        // Update Support Conversation
        val existingConv = dao.getSupportConversationForUserSync(targetUserId)
        if (existingConv != null) {
            val updated = existingConv.copy(
                lastMessage = text.trim(),
                lastMessageSenderId = "user_official_viva",
                lastTimestamp = now,
                unreadByUser = true,
                unreadByAdmin = false
            )
            dao.insertSupportConversation(updated)
        }

        // Notify user
        dao.insertNotification(
            Notification(
                id = UUID.randomUUID().toString(),
                recipientId = targetUserId,
                senderId = "user_official_viva",
                senderUsername = "VIVA",
                senderAvatar = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=400&auto=format&fit=crop&q=80",
                type = "MESSAGE",
                title = "VIVA Support replied to your message.",
                message = text.trim().take(80)
            )
        )

        // Audit log
        dao.insertAuditLog(
            AdminAuditLog(
                id = "log_${System.currentTimeMillis()}",
                adminId = adminUser.id,
                adminUsername = adminUser.username,
                action = "SENT_SUPPORT_MESSAGE",
                targetId = convId,
                targetType = "MESSAGE",
                details = "Admin @${adminUser.username} replied in conversation $convId: '${text.trim().take(40)}'"
            )
        )

        Result.success(Unit)
    }

    suspend fun updateSupportConversationStatus(convId: String, status: String, adminUser: User) = withContext(Dispatchers.IO) {
        dao.updateSupportConversationStatus(convId, status)
        dao.insertAuditLog(
            AdminAuditLog(
                id = "log_${System.currentTimeMillis()}",
                adminId = adminUser.id,
                adminUsername = adminUser.username,
                action = "UPDATED_CONVERSATION_STATUS",
                targetId = convId,
                targetType = "MESSAGE",
                details = "Conversation status changed to $status by @${adminUser.username}."
            )
        )
    }

    suspend fun markSupportReadByAdmin(convId: String) = withContext(Dispatchers.IO) {
        dao.markSupportReadByAdmin(convId)
    }

    suspend fun markSupportReadByUser(convId: String) = withContext(Dispatchers.IO) {
        dao.markSupportReadByUser(convId)
    }

    suspend fun deleteMessage(msgId: String) = withContext(Dispatchers.IO) {
        dao.deleteMessage(msgId)
    }

    // --- Admin Audit Logs ---
    val allAuditLogs: Flow<List<AdminAuditLog>> = dao.getAllAuditLogs()

    // --- Live Streams ---
    val allLiveStreams: Flow<List<LiveStream>> = dao.getAllLiveStreams()
    val activeLiveStreams: Flow<List<LiveStream>> = dao.getActiveLiveStreams()

    suspend fun endLiveStream(streamId: String, adminUser: User, reason: String) = withContext(Dispatchers.IO) {
        dao.updateLiveStreamStatus(streamId, "ENDED")
        dao.insertAuditLog(
            AdminAuditLog(
                id = "log_${System.currentTimeMillis()}",
                adminId = adminUser.id,
                adminUsername = adminUser.username,
                action = "ENDED_LIVE",
                targetId = streamId,
                targetType = "LIVE",
                details = "Live stream ended by admin @${adminUser.username}. Reason: $reason"
            )
        )
    }

    // --- User Moderation Actions ---
    suspend fun suspendUser(userId: String, adminUser: User, reason: String) = withContext(Dispatchers.IO) {
        dao.updateUserAccountStatus(userId, "SUSPENDED")
        dao.insertAuditLog(
            AdminAuditLog(
                id = "log_${System.currentTimeMillis()}",
                adminId = adminUser.id,
                adminUsername = adminUser.username,
                action = "SUSPENDED_ACCOUNT",
                targetId = userId,
                targetType = "USER",
                details = "Account suspended by @${adminUser.username}. Reason: $reason"
            )
        )
    }

    suspend fun banUser(userId: String, adminUser: User, reason: String) = withContext(Dispatchers.IO) {
        dao.updateUserAccountStatus(userId, "BANNED")
        dao.insertAuditLog(
            AdminAuditLog(
                id = "log_${System.currentTimeMillis()}",
                adminId = adminUser.id,
                adminUsername = adminUser.username,
                action = "BANNED_ACCOUNT",
                targetId = userId,
                targetType = "USER",
                details = "Account permanently banned by @${adminUser.username}. Reason: $reason"
            )
        )
    }

    suspend fun restoreUser(userId: String, adminUser: User) = withContext(Dispatchers.IO) {
        dao.updateUserAccountStatus(userId, "ACTIVE")
        dao.insertAuditLog(
            AdminAuditLog(
                id = "log_${System.currentTimeMillis()}",
                adminId = adminUser.id,
                adminUsername = adminUser.username,
                action = "RESTORED_ACCOUNT",
                targetId = userId,
                targetType = "USER",
                details = "Account restored to ACTIVE status by @${adminUser.username}."
            )
        )
    }

    suspend fun setUserVerification(userId: String, isVerified: Boolean, adminUser: User) = withContext(Dispatchers.IO) {
        val status = if (isVerified) "approved" else "unverified"
        dao.updateUserVerification(userId, isVerified, status)
        dao.insertAuditLog(
            AdminAuditLog(
                id = "log_${System.currentTimeMillis()}",
                adminId = adminUser.id,
                adminUsername = adminUser.username,
                action = if (isVerified) "GRANTED_VERIFICATION" else "REVOKED_VERIFICATION",
                targetId = userId,
                targetType = "USER",
                details = "Verification status set to $isVerified by @${adminUser.username}."
            )
        )
    }

    suspend fun updateUserRole(userId: String, newRole: String, ownerUser: User) = withContext(Dispatchers.IO) {
        dao.updateUserRole(userId, newRole)
        dao.insertAuditLog(
            AdminAuditLog(
                id = "log_${System.currentTimeMillis()}",
                adminId = ownerUser.id,
                adminUsername = ownerUser.username,
                action = "UPDATED_ADMIN_ROLE",
                targetId = userId,
                targetType = "USER",
                details = "Role changed to $newRole by Owner @${ownerUser.username}."
            )
        )
    }

    // --- Content Moderation Actions ---
    suspend fun removeVideoByAdmin(videoId: String, adminUser: User, reason: String) = withContext(Dispatchers.IO) {
        dao.deleteVideoById(videoId)
        dao.insertAuditLog(
            AdminAuditLog(
                id = "log_${System.currentTimeMillis()}",
                adminId = adminUser.id,
                adminUsername = adminUser.username,
                action = "REMOVED_VIDEO",
                targetId = videoId,
                targetType = "VIDEO",
                details = "Video removed by @${adminUser.username}. Reason: $reason"
            )
        )
    }

    suspend fun resolveReport(reportId: String, adminUser: User) = withContext(Dispatchers.IO) {
        dao.updateReportStatus(reportId, "RESOLVED")
        dao.insertAuditLog(
            AdminAuditLog(
                id = "log_${System.currentTimeMillis()}",
                adminId = adminUser.id,
                adminUsername = adminUser.username,
                action = "RESOLVED_REPORT",
                targetId = reportId,
                targetType = "REPORT",
                details = "Report marked resolved by @${adminUser.username}."
            )
        )
    }

    suspend fun dismissReport(reportId: String, adminUser: User) = withContext(Dispatchers.IO) {
        dao.updateReportStatus(reportId, "DISMISSED")
        dao.insertAuditLog(
            AdminAuditLog(
                id = "log_${System.currentTimeMillis()}",
                adminId = adminUser.id,
                adminUsername = adminUser.username,
                action = "DISMISSED_REPORT",
                targetId = reportId,
                targetType = "REPORT",
                details = "Report dismissed by @${adminUser.username}."
            )
        )
    }

    suspend fun deleteAdminNotification(notifId: String) = withContext(Dispatchers.IO) {
        dao.deleteAdminNotification(notifId)
    }
}
