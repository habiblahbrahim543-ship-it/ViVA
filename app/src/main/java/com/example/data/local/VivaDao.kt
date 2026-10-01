package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VivaDao {

    // --- Users ---
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE isCurrentUser = 1 LIMIT 1")
    fun getCurrentUser(): Flow<User?>

    @Query("SELECT * FROM users WHERE isCurrentUser = 1 LIMIT 1")
    suspend fun getCurrentUserSync(): User?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserById(userId: String): Flow<User?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserByIdSync(userId: String): User?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    fun getUserByUsername(username: String): Flow<User?>

    @Query("SELECT * FROM users WHERE username LIKE '%' || :query || '%' OR displayName LIKE '%' || :query || '%'")
    fun searchUsers(query: String): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET followersCount = followersCount + :delta, isFollowing = :isFollowing WHERE id = :userId")
    suspend fun updateFollowState(userId: String, delta: Int, isFollowing: Boolean)

    @Query("UPDATE users SET isVerified = :isVerified, verificationStatus = :status WHERE id = :userId")
    suspend fun updateUserVerification(userId: String, isVerified: Boolean, status: String)

    @Query("UPDATE users SET displayName = :displayName, username = :username, bio = :bio, website = :website, avatarUrl = :avatarUrl WHERE id = :userId")
    suspend fun updateUserProfileFields(userId: String, displayName: String, username: String, bio: String, website: String, avatarUrl: String)

    @Query("UPDATE users SET isCurrentUser = 0")
    suspend fun clearCurrentUsers()

    @Query("UPDATE users SET isCurrentUser = 1 WHERE id = :userId")
    suspend fun setCurrentUser(userId: String)

    // --- Followers Relations ---
    @Query("""
        SELECT u.* FROM users u 
        INNER JOIN followers_relations fr ON u.id = fr.followerId 
        WHERE fr.followingId = :userId
    """)
    fun getFollowersForUser(userId: String): Flow<List<User>>

    @Query("""
        SELECT u.* FROM users u 
        INNER JOIN followers_relations fr ON u.id = fr.followingId 
        WHERE fr.followerId = :userId
    """)
    fun getFollowingForUser(userId: String): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollowRelation(relation: FollowRelation)

    @Query("DELETE FROM followers_relations WHERE followerId = :followerId AND followingId = :followingId")
    suspend fun deleteFollowRelation(followerId: String, followingId: String)

    // --- Blocked Users ---
    @Query("SELECT * FROM blocked_users WHERE userId = :userId ORDER BY timestamp DESC")
    fun getBlockedUsers(userId: String): Flow<List<BlockedUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedUser(blocked: BlockedUser)

    @Query("DELETE FROM blocked_users WHERE userId = :userId AND blockedUserId = :blockedUserId")
    suspend fun deleteBlockedUser(userId: String, blockedUserId: String)

    // --- Verification Requests ---
    @Query("SELECT * FROM verification_requests WHERE userId = :userId ORDER BY createdAt DESC LIMIT 1")
    fun getVerificationRequestForUser(userId: String): Flow<VerificationRequest?>

    @Query("SELECT * FROM verification_requests WHERE id = :requestId LIMIT 1")
    fun getVerificationRequestById(requestId: String): Flow<VerificationRequest?>

    @Query("SELECT * FROM verification_requests ORDER BY createdAt DESC")
    fun getAllVerificationRequests(): Flow<List<VerificationRequest>>

    @Query("SELECT * FROM verification_requests WHERE status = 'pending' OR status = 'under_review' ORDER BY createdAt DESC")
    fun getPendingVerificationRequests(): Flow<List<VerificationRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerificationRequest(request: VerificationRequest)

    @Update
    suspend fun updateVerificationRequest(request: VerificationRequest)

    @Query("""
        UPDATE verification_requests 
        SET status = :status, reviewedBy = :reviewedBy, reviewedAt = :reviewedAt, rejectionReason = :rejectionReason, adminNotes = :adminNotes, updatedAt = :reviewedAt
        WHERE id = :id
    """)
    suspend fun updateVerificationRequestStatus(
        id: String,
        status: String,
        reviewedBy: String,
        reviewedAt: Long,
        rejectionReason: String,
        adminNotes: String
    )

    // --- Admin Notifications ---
    @Query("SELECT * FROM admin_notifications ORDER BY timestamp DESC")
    fun getAllAdminNotifications(): Flow<List<AdminNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdminNotification(notification: AdminNotification)

    @Query("UPDATE admin_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAdminNotificationAsRead(id: String)

    @Query("UPDATE admin_notifications SET isRead = 1")
    suspend fun markAllAdminNotificationsAsRead()

    // --- Videos ---
    @Query("SELECT * FROM videos WHERE isPrivate = 0 ORDER BY createdAt DESC")
    fun getAllVideos(): Flow<List<Video>>

    @Query("SELECT * FROM videos WHERE isFollowingCreator = 1 AND isPrivate = 0 ORDER BY createdAt DESC")
    fun getFollowingVideos(): Flow<List<Video>>

    @Query("SELECT * FROM videos WHERE creatorId = :creatorId ORDER BY createdAt DESC")
    fun getVideosByCreator(creatorId: String): Flow<List<Video>>

    @Query("SELECT * FROM videos WHERE isLiked = 1 ORDER BY createdAt DESC")
    fun getLikedVideos(): Flow<List<Video>>

    @Query("SELECT * FROM videos WHERE isSaved = 1 ORDER BY createdAt DESC")
    fun getSavedVideos(): Flow<List<Video>>

    @Query("SELECT * FROM videos WHERE hashtags LIKE '%' || :hashtag || '%' ORDER BY viewsCount DESC")
    fun getVideosByHashtag(hashtag: String): Flow<List<Video>>

    @Query("SELECT * FROM videos WHERE soundId = :soundId ORDER BY likesCount DESC")
    fun getVideosBySound(soundId: String): Flow<List<Video>>

    @Query("SELECT * FROM videos WHERE caption LIKE '%' || :query || '%' OR hashtags LIKE '%' || :query || '%'")
    fun searchVideos(query: String): Flow<List<Video>>

    @Query("SELECT * FROM videos WHERE id = :videoId LIMIT 1")
    fun getVideoById(videoId: String): Flow<Video?>

    @Query("SELECT * FROM videos WHERE id = :videoId LIMIT 1")
    suspend fun getVideoByIdSync(videoId: String): Video?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: Video)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<Video>)

    @Update
    suspend fun updateVideo(video: Video)

    @Query("DELETE FROM videos WHERE id = :videoId")
    suspend fun deleteVideoById(videoId: String)

    @Query("UPDATE videos SET likesCount = likesCount + :delta, isLiked = :isLiked WHERE id = :videoId")
    suspend fun updateLikeState(videoId: String, delta: Int, isLiked: Boolean)

    @Query("UPDATE videos SET savesCount = savesCount + :delta, isSaved = :isSaved WHERE id = :videoId")
    suspend fun updateSaveState(videoId: String, delta: Int, isSaved: Boolean)

    @Query("UPDATE videos SET sharesCount = sharesCount + 1 WHERE id = :videoId")
    suspend fun incrementShareCount(videoId: String)

    @Query("UPDATE videos SET isFollowingCreator = :isFollowing WHERE creatorId = :creatorId")
    suspend fun updateVideoCreatorFollow(creatorId: String, isFollowing: Boolean)

    // --- Comments ---
    @Query("SELECT * FROM comments WHERE videoId = :videoId AND parentCommentId IS NULL ORDER BY timestamp DESC")
    fun getTopLevelComments(videoId: String): Flow<List<Comment>>

    @Query("SELECT * FROM comments WHERE parentCommentId = :parentId ORDER BY timestamp ASC")
    fun getCommentReplies(parentId: String): Flow<List<Comment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: Comment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<Comment>)

    @Query("UPDATE comments SET likesCount = likesCount + :delta, isLiked = :isLiked WHERE id = :commentId")
    suspend fun updateCommentLike(commentId: String, delta: Int, isLiked: Boolean)

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteComment(commentId: String)

    @Query("UPDATE videos SET commentsCount = commentsCount + 1 WHERE id = :videoId")
    suspend fun incrementCommentCount(videoId: String)

    // --- Messages ---
    @Query("SELECT * FROM messages WHERE conversationId = :convId ORDER BY timestamp ASC")
    fun getMessagesForConversation(convId: String): Flow<List<Message>>

    @Query("SELECT * FROM messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<Message>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<Message>)

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: String)

    // --- Notifications ---
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<Notification>>

    @Query("SELECT * FROM notifications WHERE type = :type ORDER BY timestamp DESC")
    fun getNotificationsByType(type: String): Flow<List<Notification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<Notification>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markNotificationAsRead(notificationId: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    // --- Sounds ---
    @Query("SELECT * FROM sounds ORDER BY videoCount DESC")
    fun getAllSounds(): Flow<List<Sound>>

    @Query("SELECT * FROM sounds WHERE id = :soundId LIMIT 1")
    fun getSoundById(soundId: String): Flow<Sound?>

    @Query("SELECT * FROM sounds WHERE title LIKE '%' || :query || '%' OR creator LIKE '%' || :query || '%'")
    fun searchSounds(query: String): Flow<List<Sound>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSound(sound: Sound)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSounds(sounds: List<Sound>)

    // --- Hashtags ---
    @Query("SELECT * FROM hashtags ORDER BY videoCount DESC")
    fun getAllHashtags(): Flow<List<Hashtag>>

    @Query("SELECT * FROM hashtags WHERE isTrending = 1 ORDER BY videoCount DESC")
    fun getTrendingHashtags(): Flow<List<Hashtag>>

    @Query("SELECT * FROM hashtags WHERE tag LIKE '%' || :query || '%'")
    fun searchHashtags(query: String): Flow<List<Hashtag>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHashtag(hashtag: Hashtag)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHashtags(hashtags: List<Hashtag>)

    // --- Moderation Reports ---
    @Query("SELECT * FROM moderation_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<ModerationReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ModerationReport)

    @Query("UPDATE moderation_reports SET status = :status WHERE id = :reportId")
    suspend fun updateReportStatus(reportId: String, status: String)

    @Query("DELETE FROM moderation_reports WHERE id = :reportId")
    suspend fun deleteReport(reportId: String)

    // --- Support Conversations ---
    @Query("SELECT * FROM support_conversations ORDER BY lastTimestamp DESC")
    fun getAllSupportConversations(): Flow<List<SupportConversation>>

    @Query("SELECT * FROM support_conversations WHERE id = :convId LIMIT 1")
    fun getSupportConversationById(convId: String): Flow<SupportConversation?>

    @Query("SELECT * FROM support_conversations WHERE userId = :userId LIMIT 1")
    fun getSupportConversationForUser(userId: String): Flow<SupportConversation?>

    @Query("SELECT * FROM support_conversations WHERE userId = :userId LIMIT 1")
    suspend fun getSupportConversationForUserSync(userId: String): SupportConversation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupportConversation(conv: SupportConversation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupportConversations(convs: List<SupportConversation>)

    @Query("UPDATE support_conversations SET status = :status WHERE id = :convId")
    suspend fun updateSupportConversationStatus(convId: String, status: String)

    @Query("UPDATE support_conversations SET unreadByAdmin = 0 WHERE id = :convId")
    suspend fun markSupportReadByAdmin(convId: String)

    @Query("UPDATE support_conversations SET unreadByUser = 0 WHERE id = :convId")
    suspend fun markSupportReadByUser(convId: String)

    // --- Admin Audit Logs ---
    @Query("SELECT * FROM admin_audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AdminAuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AdminAuditLog)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLogs(logs: List<AdminAuditLog>)

    // --- Live Streams ---
    @Query("SELECT * FROM live_streams ORDER BY startedAt DESC")
    fun getAllLiveStreams(): Flow<List<LiveStream>>

    @Query("SELECT * FROM live_streams WHERE status = 'ACTIVE' ORDER BY viewerCount DESC")
    fun getActiveLiveStreams(): Flow<List<LiveStream>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLiveStream(stream: LiveStream)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLiveStreams(streams: List<LiveStream>)

    @Query("UPDATE live_streams SET status = :status WHERE id = :streamId")
    suspend fun updateLiveStreamStatus(streamId: String, status: String)

    // --- Admin Notification Deletion ---
    @Query("DELETE FROM admin_notifications WHERE id = :notifId")
    suspend fun deleteAdminNotification(notifId: String)

    // --- User Management Extras ---
    @Query("UPDATE users SET accountStatus = :status WHERE id = :userId")
    suspend fun updateUserAccountStatus(userId: String, status: String)

    @Query("UPDATE users SET role = :role WHERE id = :userId")
    suspend fun updateUserRole(userId: String, role: String)

    // --- Settings ---
    @Query("SELECT * FROM app_settings")
    fun getAllSettings(): Flow<List<AppSetting>>

    @Query("SELECT value FROM app_settings WHERE `key` = :key LIMIT 1")
    suspend fun getSettingValue(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: AppSetting)
}
