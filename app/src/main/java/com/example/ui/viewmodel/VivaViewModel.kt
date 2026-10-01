package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.VivaDatabase
import com.example.data.model.*
import com.example.data.repository.VivaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class MainTab {
    HOME, DISCOVER, CREATE, INBOX, PROFILE
}

enum class FeedType {
    FOR_YOU, FOLLOWING
}

enum class SearchCategory {
    TOP, USERS, VIDEOS, SOUNDS, HASHTAGS
}

enum class InboxFilter {
    ALL, LIKES, COMMENTS, FOLLOWS, MESSAGES
}

data class ActiveReportTarget(
    val type: String, // "VIDEO", "USER", "COMMENT"
    val id: String,
    val title: String
)

class VivaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VivaRepository

    init {
        val db = VivaDatabase.getDatabase(application)
        repository = VivaRepository(db.vivaDao())
    }

    // Navigation & Tabs
    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _feedType = MutableStateFlow(FeedType.FOR_YOU)
    val feedType: StateFlow<FeedType> = _feedType.asStateFlow()

    // Sub-screens & Modals
    private val _showContactVivaScreen = MutableStateFlow(false)
    val showContactVivaScreen: StateFlow<Boolean> = _showContactVivaScreen.asStateFlow()

    private val _selectedCreatorId = MutableStateFlow<String?>(null)
    val selectedCreatorId: StateFlow<String?> = _selectedCreatorId.asStateFlow()

    private val _selectedHashtag = MutableStateFlow<String?>(null)
    val selectedHashtag: StateFlow<String?> = _selectedHashtag.asStateFlow()

    private val _selectedSoundId = MutableStateFlow<String?>(null)
    val selectedSoundId: StateFlow<String?> = _selectedSoundId.asStateFlow()

    private val _activeChatPartner = MutableStateFlow<User?>(null)
    val activeChatPartner: StateFlow<User?> = _activeChatPartner.asStateFlow()

    private val _activeCommentsVideoId = MutableStateFlow<String?>(null)
    val activeCommentsVideoId: StateFlow<String?> = _activeCommentsVideoId.asStateFlow()

    private val _activeShareVideo = MutableStateFlow<Video?>(null)
    val activeShareVideo: StateFlow<Video?> = _activeShareVideo.asStateFlow()

    private val _activeReportTarget = MutableStateFlow<ActiveReportTarget?>(null)
    val activeReportTarget: StateFlow<ActiveReportTarget?> = _activeReportTarget.asStateFlow()

    private val _showSettings = MutableStateFlow(false)
    val showSettings: StateFlow<Boolean> = _showSettings.asStateFlow()

    private val _showAdminDashboard = MutableStateFlow(false)
    val showAdminDashboard: StateFlow<Boolean> = _showAdminDashboard.asStateFlow()

    private val _showAuthDialog = MutableStateFlow(false)
    val showAuthDialog: StateFlow<Boolean> = _showAuthDialog.asStateFlow()

    private val _showEditProfileDialog = MutableStateFlow(false)
    val showEditProfileDialog: StateFlow<Boolean> = _showEditProfileDialog.asStateFlow()

    private val _showVerificationRequestScreen = MutableStateFlow(false)
    val showVerificationRequestScreen: StateFlow<Boolean> = _showVerificationRequestScreen.asStateFlow()

    private val _selectedAdminVerificationRequest = MutableStateFlow<VerificationRequest?>(null)
    val selectedAdminVerificationRequest: StateFlow<VerificationRequest?> = _selectedAdminVerificationRequest.asStateFlow()

    private val _showFollowersDialogUserId = MutableStateFlow<String?>(null)
    val showFollowersDialogUserId: StateFlow<String?> = _showFollowersDialogUserId.asStateFlow()

    private val _showFollowingDialogUserId = MutableStateFlow<String?>(null)
    val showFollowingDialogUserId: StateFlow<String?> = _showFollowingDialogUserId.asStateFlow()

    private val _showBlockedUsersScreen = MutableStateFlow(false)
    val showBlockedUsersScreen: StateFlow<Boolean> = _showBlockedUsersScreen.asStateFlow()

    // Global Audio Mute
    private val _isGlobalMuted = MutableStateFlow(false)
    val isGlobalMuted: StateFlow<Boolean> = _isGlobalMuted.asStateFlow()

    // Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchCategory = MutableStateFlow(SearchCategory.TOP)
    val searchCategory: StateFlow<SearchCategory> = _searchCategory.asStateFlow()

    // Inbox
    private val _inboxFilter = MutableStateFlow(InboxFilter.ALL)
    val inboxFilter: StateFlow<InboxFilter> = _inboxFilter.asStateFlow()

    // Upload status
    private val _uploadProgress = MutableStateFlow<Float?>(null)
    val uploadProgress: StateFlow<Float?> = _uploadProgress.asStateFlow()

    // UI feedback message
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    // Base flows
    val currentUser: StateFlow<User?> = repository.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val forYouVideos: StateFlow<List<Video>> = repository.getAllVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val followingVideos: StateFlow<List<Video>> = repository.getFollowingVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<User>> = repository.searchUsers("")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trendingHashtags: StateFlow<List<Hashtag>> = repository.getTrendingHashtags()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSounds: StateFlow<List<Sound>> = repository.getAllSounds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<Notification>> = repository.getAllNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMessages: StateFlow<List<Message>> = repository.getAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReports: StateFlow<List<ModerationReport>> = repository.getAllReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appSettings: StateFlow<List<AppSetting>> = repository.getAllSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Verification flows
    @OptIn(ExperimentalCoroutinesApi::class)
    val myVerificationRequest: StateFlow<VerificationRequest?> = currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getVerificationRequestForUser(user.id)
            else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allVerificationRequests: StateFlow<List<VerificationRequest>> = repository.getAllVerificationRequests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingVerificationRequests: StateFlow<List<VerificationRequest>> = repository.getPendingVerificationRequests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminNotifications: StateFlow<List<AdminNotification>> = repository.getAllAdminNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSupportConversations: StateFlow<List<SupportConversation>> = repository.allSupportConversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAuditLogs: StateFlow<List<AdminAuditLog>> = repository.allAuditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allLiveStreams: StateFlow<List<LiveStream>> = repository.allLiveStreams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeLiveStreams: StateFlow<List<LiveStream>> = repository.activeLiveStreams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val mySupportConversation: StateFlow<SupportConversation?> = currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getSupportConversationForUser(user.id)
            else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val blockedUsers: StateFlow<List<BlockedUser>> = currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getBlockedUsers(user.id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions & Nav
    fun setMainTab(tab: MainTab) { _currentTab.value = tab }
    fun setFeedType(type: FeedType) { _feedType.value = type }
    fun toggleGlobalMute() { _isGlobalMuted.value = !_isGlobalMuted.value }

    fun openCreatorProfile(creatorId: String) { _selectedCreatorId.value = creatorId }
    fun closeCreatorProfile() { _selectedCreatorId.value = null }

    fun openHashtag(tag: String) { _selectedHashtag.value = tag.trim().removePrefix("#") }
    fun closeHashtag() { _selectedHashtag.value = null }

    fun openSound(soundId: String) { _selectedSoundId.value = soundId }
    fun closeSound() { _selectedSoundId.value = null }

    fun openComments(videoId: String) { _activeCommentsVideoId.value = videoId }
    fun closeComments() { _activeCommentsVideoId.value = null }

    fun openShare(video: Video) { _activeShareVideo.value = video }
    fun closeShare() { _activeShareVideo.value = null }

    fun openReport(target: ActiveReportTarget) { _activeReportTarget.value = target }
    fun closeReport() { _activeReportTarget.value = null }

    fun openChat(user: User) { _activeChatPartner.value = user }
    fun closeChat() { _activeChatPartner.value = null }

    fun openSettings() { _showSettings.value = true }
    fun closeSettings() { _showSettings.value = false }

    fun openAdminDashboard() { _showAdminDashboard.value = true }
    fun closeAdminDashboard() { _showAdminDashboard.value = false }

    fun openEditProfile() { _showEditProfileDialog.value = true }
    fun closeEditProfile() { _showEditProfileDialog.value = false }

    fun openAuth() { _showAuthDialog.value = true }
    fun closeAuth() { _showAuthDialog.value = false }

    fun openVerificationRequestScreen() { _showVerificationRequestScreen.value = true }
    fun closeVerificationRequestScreen() { _showVerificationRequestScreen.value = false }

    fun openAdminVerificationDetail(request: VerificationRequest) { _selectedAdminVerificationRequest.value = request }
    fun closeAdminVerificationDetail() { _selectedAdminVerificationRequest.value = null }

    fun openFollowers(userId: String) { _showFollowersDialogUserId.value = userId }
    fun closeFollowers() { _showFollowersDialogUserId.value = null }

    fun openFollowing(userId: String) { _showFollowingDialogUserId.value = userId }
    fun closeFollowing() { _showFollowingDialogUserId.value = null }

    fun openBlockedUsersScreen() { _showBlockedUsersScreen.value = true }
    fun closeBlockedUsersScreen() { _showBlockedUsersScreen.value = false }

    fun openContactViva() { _showContactVivaScreen.value = true }
    fun closeContactViva() { _showContactVivaScreen.value = false }

    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setSearchCategory(category: SearchCategory) { _searchCategory.value = category }
    fun setInboxFilter(filter: InboxFilter) { _inboxFilter.value = filter }

    fun showToast(msg: String) { _uiMessage.value = msg }
    fun clearToast() { _uiMessage.value = null }

    // Business Logic
    fun toggleLike(video: Video) {
        viewModelScope.launch { repository.toggleLikeVideo(video) }
    }

    fun toggleSave(video: Video) {
        viewModelScope.launch {
            repository.toggleSaveVideo(video)
            showToast(if (!video.isSaved) "Added to Saved Videos" else "Removed from Saved")
        }
    }

    fun shareVideo(video: Video) {
        viewModelScope.launch { repository.shareVideo(video.id) }
    }

    fun toggleFollowCreator(creatorId: String, currentState: Boolean) {
        viewModelScope.launch {
            repository.toggleFollowCreator(creatorId, currentState)
            showToast(if (!currentState) "Followed creator" else "Unfollowed creator")
        }
    }

    fun blockUser(targetUser: User) {
        viewModelScope.launch {
            repository.blockUser(targetUser)
            showToast("Blocked @${targetUser.username}")
        }
    }

    fun unblockUser(blockedUserId: String, username: String) {
        viewModelScope.launch {
            repository.unblockUser(blockedUserId)
            showToast("Unblocked @$username")
        }
    }

    fun switchAccount(userId: String) {
        viewModelScope.launch {
            repository.switchAccount(userId)
            val user = repository.getUserById(userId).firstOrNull()
            showToast("Switched account to @${user?.username ?: userId}")
        }
    }

    fun addComment(videoId: String, text: String, parentId: String? = null) {
        if (text.isBlank()) return
        viewModelScope.launch { repository.addComment(videoId, text.trim(), parentId) }
    }

    fun toggleCommentLike(comment: Comment) {
        viewModelScope.launch { repository.toggleCommentLike(comment) }
    }

    fun deleteComment(commentId: String) {
        viewModelScope.launch {
            repository.deleteComment(commentId)
            showToast("Comment deleted")
        }
    }

    fun sendDirectMessage(receiverId: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val myId = currentUser.value?.id ?: "user_me"
            val convId = "conv_${listOf(myId, receiverId).sorted().joinToString("_")}"
            repository.sendMessage(convId, receiverId, text.trim())
        }
    }

    fun markNotificationRead(notifId: String) {
        viewModelScope.launch { repository.markNotificationAsRead(notifId) }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
            showToast("All notifications marked as read")
        }
    }

    fun markAdminNotificationRead(id: String) {
        viewModelScope.launch { repository.markAdminNotificationAsRead(id) }
    }

    fun markAllAdminNotificationsRead() {
        viewModelScope.launch {
            repository.markAllAdminNotificationsAsRead()
            showToast("All admin notifications marked as read")
        }
    }

    fun submitReport(reason: String, notes: String) {
        val target = _activeReportTarget.value ?: return
        viewModelScope.launch {
            repository.submitReport(target.type, target.id, reason, notes)
            closeReport()
            showToast("Report submitted. Thank you for keeping VIVA safe.")
        }
    }

    fun updateReportStatus(reportId: String, status: String) {
        viewModelScope.launch {
            repository.updateReportStatus(reportId, status)
            showToast("Report status updated to $status")
        }
    }

    fun deleteVideo(videoId: String) {
        viewModelScope.launch {
            repository.deleteVideo(videoId)
            showToast("Video deleted")
        }
    }

    fun updateProfile(displayName: String, username: String, bio: String, website: String, avatarUrl: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.updateUserProfile(
                userId = user.id,
                displayName = displayName,
                username = username,
                bio = bio,
                website = website,
                avatarUrl = avatarUrl
            )
            if (result.isSuccess) {
                closeEditProfile()
                showToast("Profile updated successfully!")
            } else {
                showToast("Failed to update profile: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    // Verification Workflow
    fun submitVerificationRequest(
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
    ) {
        viewModelScope.launch {
            val result = repository.submitVerificationRequest(
                fullName = fullName,
                category = category,
                country = country,
                reason = reason,
                website = website,
                instagram = instagram,
                youtube = youtube,
                tiktok = tiktok,
                otherLinks = otherLinks,
                supportingDocuments = supportingDocuments
            )
            if (result.isSuccess) {
                showToast("Verification request submitted. Status: Pending review.")
            } else {
                showToast("Error: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun approveVerification(requestId: String, targetUserId: String) {
        val admin = currentUser.value
        if (admin?.role != "ADMIN" && admin?.role != "OWNER") {
            showToast("Unauthorized: Administrator permissions required to approve verification.")
            return
        }
        viewModelScope.launch {
            repository.approveVerificationForUser(
                requestId = requestId,
                targetUserId = targetUserId,
                adminUsername = admin.username
            )
            closeAdminVerificationDetail()
            showToast("Verification APPROVED for user. Blue badge granted! ✓")
        }
    }

    fun rejectVerification(requestId: String, targetUserId: String, reason: String) {
        val admin = currentUser.value
        if (admin?.role != "ADMIN" && admin?.role != "OWNER") {
            showToast("Unauthorized: Administrator permissions required to reject verification.")
            return
        }
        viewModelScope.launch {
            repository.rejectVerificationForUser(
                requestId = requestId,
                targetUserId = targetUserId,
                adminUsername = admin.username,
                reason = reason
            )
            closeAdminVerificationDetail()
            showToast("Verification request rejected.")
        }
    }

    fun requestMoreInfo(requestId: String, targetUserId: String, notes: String) {
        val admin = currentUser.value
        if (admin?.role != "ADMIN" && admin?.role != "OWNER") {
            showToast("Unauthorized: Administrator permissions required to request more info.")
            return
        }
        viewModelScope.launch {
            repository.requestMoreInfoForUser(
                requestId = requestId,
                targetUserId = targetUserId,
                adminUsername = admin.username,
                notes = notes
            )
            closeAdminVerificationDetail()
            showToast("Requested more information from creator.")
        }
    }

    // Support & Admin Messaging
    fun sendUserSupportMessage(text: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val result = repository.sendContactVivaMessage(text)
            if (result.isSuccess) {
                showToast("Message sent to VIVA Official Support.")
                onSuccess()
            } else {
                showToast("Error sending message: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun sendAdminReply(convId: String, text: String, onSuccess: () -> Unit = {}) {
        val admin = currentUser.value
        if (admin?.role != "ADMIN" && admin?.role != "OWNER") {
            showToast("Unauthorized: Only administrators can reply as VIVA.")
            return
        }
        viewModelScope.launch {
            val result = repository.sendAdminReply(convId, text, admin)
            if (result.isSuccess) {
                showToast("Reply sent to user from VIVA Official Support.")
                onSuccess()
            } else {
                showToast("Error sending reply: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun updateSupportConversationStatus(convId: String, status: String) {
        val admin = currentUser.value ?: return
        viewModelScope.launch {
            repository.updateSupportConversationStatus(convId, status, admin)
            showToast("Conversation status set to $status.")
        }
    }

    fun markSupportReadByAdmin(convId: String) {
        viewModelScope.launch { repository.markSupportReadByAdmin(convId) }
    }

    fun markSupportReadByUser(convId: String) {
        viewModelScope.launch { repository.markSupportReadByUser(convId) }
    }

    fun deleteSupportMessage(msgId: String) {
        viewModelScope.launch {
            repository.deleteMessage(msgId)
            showToast("Message deleted.")
        }
    }

    fun markAdminNotificationAsRead(id: String) {
        viewModelScope.launch {
            repository.markAdminNotificationAsRead(id)
        }
    }

    fun markAllAdminNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllAdminNotificationsAsRead()
            showToast("All admin notifications marked as read.")
        }
    }

    fun deleteAdminNotification(id: String) {
        viewModelScope.launch {
            repository.deleteAdminNotification(id)
            showToast("Admin notification removed.")
        }
    }

    // Admin User & Content Moderation Actions
    fun suspendUser(userId: String, reason: String) {
        val admin = currentUser.value
        if (admin?.role != "ADMIN" && admin?.role != "OWNER") {
            showToast("Unauthorized: Administrator permissions required.")
            return
        }
        viewModelScope.launch {
            repository.suspendUser(userId, admin, reason)
            showToast("User account has been SUSPENDED.")
        }
    }

    fun banUser(userId: String, reason: String) {
        val admin = currentUser.value
        if (admin?.role != "ADMIN" && admin?.role != "OWNER") {
            showToast("Unauthorized: Administrator permissions required.")
            return
        }
        viewModelScope.launch {
            repository.banUser(userId, admin, reason)
            showToast("User account has been PERMANENTLY BANNED.")
        }
    }

    fun restoreUser(userId: String) {
        val admin = currentUser.value
        if (admin?.role != "ADMIN" && admin?.role != "OWNER") {
            showToast("Unauthorized: Administrator permissions required.")
            return
        }
        viewModelScope.launch {
            repository.restoreUser(userId, admin)
            showToast("User account restored to ACTIVE.")
        }
    }

    fun toggleUserVerification(userId: String, currentVerified: Boolean) {
        val admin = currentUser.value
        if (admin?.role != "ADMIN" && admin?.role != "OWNER") {
            showToast("Unauthorized: Administrator permissions required.")
            return
        }
        viewModelScope.launch {
            repository.setUserVerification(userId, !currentVerified, admin)
            showToast(if (!currentVerified) "Verified badge granted! ✓" else "Verified badge revoked.")
        }
    }

    fun updateUserRole(userId: String, newRole: String) {
        val owner = currentUser.value
        if (owner?.role != "OWNER") {
            showToast("Unauthorized: Only the Platform Owner can manage administrator roles.")
            return
        }
        viewModelScope.launch {
            repository.updateUserRole(userId, newRole, owner)
            showToast("Role updated to $newRole.")
        }
    }

    fun removeVideoByAdmin(videoId: String, reason: String) {
        val admin = currentUser.value
        if (admin?.role != "ADMIN" && admin?.role != "OWNER") {
            showToast("Unauthorized: Administrator permissions required.")
            return
        }
        viewModelScope.launch {
            repository.removeVideoByAdmin(videoId, admin, reason)
            showToast("Video has been removed by administration.")
        }
    }

    fun resolveReport(reportId: String) {
        val admin = currentUser.value
        if (admin?.role != "ADMIN" && admin?.role != "OWNER") {
            showToast("Unauthorized: Administrator permissions required.")
            return
        }
        viewModelScope.launch {
            repository.resolveReport(reportId, admin)
            showToast("Report marked as RESOLVED.")
        }
    }

    fun dismissReport(reportId: String) {
        val admin = currentUser.value
        if (admin?.role != "ADMIN" && admin?.role != "OWNER") {
            showToast("Unauthorized: Administrator permissions required.")
            return
        }
        viewModelScope.launch {
            repository.dismissReport(reportId, admin)
            showToast("Report DISMISSED.")
        }
    }

    fun endLiveStream(streamId: String, reason: String) {
        val admin = currentUser.value
        if (admin?.role != "ADMIN" && admin?.role != "OWNER") {
            showToast("Unauthorized: Administrator permissions required.")
            return
        }
        viewModelScope.launch {
            repository.endLiveStream(streamId, admin, reason)
            showToast("Live stream terminated by administration.")
        }
    }

    fun submitAdditionalInfo(requestId: String, info: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.submitAdditionalInfo(requestId, user.id, info)
            showToast("Additional information submitted to VIVA Trust & Safety.")
        }
    }

    fun toggleSetting(key: String, currentVal: Boolean) {
        viewModelScope.launch { repository.toggleSetting(key, currentVal) }
    }

    fun publishNewVideo(
        caption: String,
        hashtags: String,
        videoUri: String,
        thumbnailUri: String,
        soundTitle: String,
        soundCreator: String,
        category: String,
        allowComments: Boolean,
        allowDownloads: Boolean,
        isPrivate: Boolean,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uploadProgress.value = 0.2f
            kotlinx.coroutines.delay(200)
            _uploadProgress.value = 0.6f
            kotlinx.coroutines.delay(250)
            _uploadProgress.value = 1.0f

            repository.publishVideo(
                caption = caption,
                hashtags = hashtags,
                videoUrl = videoUri,
                thumbnailUrl = thumbnailUri,
                soundId = "sound_custom_${System.currentTimeMillis()}",
                soundTitle = soundTitle,
                soundCreator = soundCreator,
                category = category,
                allowComments = allowComments,
                allowDownloads = allowDownloads,
                isPrivate = isPrivate
            )

            kotlinx.coroutines.delay(100)
            _uploadProgress.value = null
            showToast("Video published to VIVA!")
            setMainTab(MainTab.HOME)
            onSuccess()
        }
    }

    // Helper queries
    fun getCommentsForVideo(videoId: String): Flow<List<Comment>> = repository.getTopLevelComments(videoId)
    fun getRepliesForComment(parentId: String): Flow<List<Comment>> = repository.getCommentReplies(parentId)
    fun getVideosForCreator(creatorId: String): Flow<List<Video>> = repository.getVideosByCreator(creatorId)
    fun getLikedVideos(): Flow<List<Video>> = repository.getLikedVideos()
    fun getSavedVideos(): Flow<List<Video>> = repository.getSavedVideos()
    fun getVideosByHashtag(tag: String): Flow<List<Video>> = repository.getVideosByHashtag(tag)
    fun getVideosBySound(soundId: String): Flow<List<Video>> = repository.getVideosBySound(soundId)
    fun getMessagesForConversation(convId: String): Flow<List<Message>> = repository.getMessagesForConversation(convId)
    fun getFollowersForUser(userId: String): Flow<List<User>> = repository.getFollowersForUser(userId)
    fun getFollowingForUser(userId: String): Flow<List<User>> = repository.getFollowingForUser(userId)
}
