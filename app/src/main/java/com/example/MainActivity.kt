package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.User
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.ReportDialog
import com.example.ui.components.ShareDialog
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.VivaViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: VivaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                VivaApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun VivaApp(viewModel: VivaViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()

    val selectedCreatorId by viewModel.selectedCreatorId.collectAsStateWithLifecycle()
    val selectedHashtag by viewModel.selectedHashtag.collectAsStateWithLifecycle()
    val selectedSoundId by viewModel.selectedSoundId.collectAsStateWithLifecycle()
    val activeChatPartner by viewModel.activeChatPartner.collectAsStateWithLifecycle()
    val showSettings by viewModel.showSettings.collectAsStateWithLifecycle()
    val showAdminDashboard by viewModel.showAdminDashboard.collectAsStateWithLifecycle()
    val showContactVivaScreen by viewModel.showContactVivaScreen.collectAsStateWithLifecycle()

    val showVerificationRequestScreen by viewModel.showVerificationRequestScreen.collectAsStateWithLifecycle()
    val selectedAdminVerificationRequest by viewModel.selectedAdminVerificationRequest.collectAsStateWithLifecycle()
    val showFollowersDialogUserId by viewModel.showFollowersDialogUserId.collectAsStateWithLifecycle()
    val showFollowingDialogUserId by viewModel.showFollowingDialogUserId.collectAsStateWithLifecycle()
    val showBlockedUsersScreen by viewModel.showBlockedUsersScreen.collectAsStateWithLifecycle()

    val activeCommentsVideoId by viewModel.activeCommentsVideoId.collectAsStateWithLifecycle()
    val activeShareVideo by viewModel.activeShareVideo.collectAsStateWithLifecycle()
    val activeReportTarget by viewModel.activeReportTarget.collectAsStateWithLifecycle()
    val showEditProfileDialog by viewModel.showEditProfileDialog.collectAsStateWithLifecycle()
    val showAuthDialog by viewModel.showAuthDialog.collectAsStateWithLifecycle()

    val uiMessage by viewModel.uiMessage.collectAsStateWithLifecycle()
    val allNotifications by viewModel.allNotifications.collectAsStateWithLifecycle()
    val unreadNotifsCount = remember(allNotifications) { allNotifications.count { !it.isRead } }

    val isFullScreenSubScreenActive = activeChatPartner != null ||
            showAdminDashboard ||
            showContactVivaScreen ||
            selectedCreatorId != null ||
            selectedHashtag != null ||
            selectedSoundId != null ||
            showSettings ||
            showVerificationRequestScreen ||
            selectedAdminVerificationRequest != null ||
            showBlockedUsersScreen

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiMessage) {
        uiMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 60.dp)
            ) { data ->
                Snackbar(
                    containerColor = VivaSurfaceElevated,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = data.visuals.message, fontWeight = FontWeight.Medium)
                }
            }
        },
        bottomBar = {
            if (!isFullScreenSubScreenActive) {
                VivaBottomNavigationBar(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.setMainTab(it) },
                    unreadCount = unreadNotifsCount,
                    isFeedScreen = currentTab == MainTab.HOME
                )
            }
        },
        containerColor = VivaBlack,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (!isFullScreenSubScreenActive) 56.dp else 0.dp)
        ) {
            // Main Tabs
            when (currentTab) {
                MainTab.HOME -> FeedScreen(viewModel = viewModel)
                MainTab.DISCOVER -> DiscoverScreen(viewModel = viewModel)
                MainTab.CREATE -> CreateScreen(viewModel = viewModel)
                MainTab.INBOX -> InboxScreen(viewModel = viewModel)
                MainTab.PROFILE -> {
                    currentUser?.let { user ->
                        ProfileScreen(
                            user = user,
                            isCurrentUser = true,
                            viewModel = viewModel
                        )
                    } ?: Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = VivaPink)
                    }
                }
            }

            // Sub-screen: Creator Profile
            selectedCreatorId?.let { creatorId ->
                val creator = allUsers.find { it.id == creatorId }
                if (creator != null) {
                    BackHandler { viewModel.closeCreatorProfile() }
                    ProfileScreen(
                        user = creator,
                        isCurrentUser = creator.isCurrentUser,
                        viewModel = viewModel,
                        onBack = { viewModel.closeCreatorProfile() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Sub-screen: Hashtag Detail
            selectedHashtag?.let { tag ->
                BackHandler { viewModel.closeHashtag() }
                HashtagDetailScreen(
                    tag = tag,
                    viewModel = viewModel,
                    onBack = { viewModel.closeHashtag() },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Sub-screen: Sound Detail
            selectedSoundId?.let { soundId ->
                BackHandler { viewModel.closeSound() }
                SoundDetailScreen(
                    soundId = soundId,
                    viewModel = viewModel,
                    onBack = { viewModel.closeSound() },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Sub-screen: 1-on-1 Direct Messaging
            activeChatPartner?.let { partner ->
                BackHandler { viewModel.closeChat() }
                ChatDetailScreen(
                    partner = partner,
                    viewModel = viewModel,
                    onBack = { viewModel.closeChat() },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Sub-screen: Settings & Privacy
            if (showSettings) {
                BackHandler { viewModel.closeSettings() }
                SettingsScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.closeSettings() },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Sub-screen: Request Verification Screen (Requirement 3)
            if (showVerificationRequestScreen) {
                BackHandler { viewModel.closeVerificationRequestScreen() }
                VerificationRequestScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.closeVerificationRequestScreen() },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Sub-screen: Admin Review of a Verification Request (Requirements 8, 9, 10, 11)
            selectedAdminVerificationRequest?.let { req ->
                BackHandler { viewModel.closeAdminVerificationDetail() }
                AdminVerificationReviewScreen(
                    request = req,
                    viewModel = viewModel,
                    onBack = { viewModel.closeAdminVerificationDetail() },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Sub-screen: Blocked Users
            if (showBlockedUsersScreen) {
                BackHandler { viewModel.closeBlockedUsersScreen() }
                BlockedUsersScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.closeBlockedUsersScreen() },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Sub-screen: Contact VIVA Official Support
            if (showContactVivaScreen) {
                BackHandler { viewModel.closeContactViva() }
                SupportChatScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.closeContactViva() },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Sub-screen: Admin Moderation Portal
            if (showAdminDashboard) {
                BackHandler { viewModel.closeAdminDashboard() }
                AdminScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.closeAdminDashboard() },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Modals & Bottom Sheets
        activeCommentsVideoId?.let { videoId ->
            CommentsBottomSheet(
                videoId = videoId,
                viewModel = viewModel,
                onDismiss = { viewModel.closeComments() }
            )
        }

        activeShareVideo?.let { video ->
            ShareDialog(
                video = video,
                viewModel = viewModel,
                onDismiss = { viewModel.closeShare() }
            )
        }

        activeReportTarget?.let { target ->
            ReportDialog(
                target = target,
                viewModel = viewModel,
                onDismiss = { viewModel.closeReport() }
            )
        }

        if (showEditProfileDialog) {
            EditProfileDialog(
                viewModel = viewModel,
                onDismiss = { viewModel.closeEditProfile() }
            )
        }

        if (showAuthDialog) {
            AuthDialog(
                viewModel = viewModel,
                onDismiss = { viewModel.closeAuth() }
            )
        }

        // Followers / Following sheet
        showFollowersDialogUserId?.let { fUserId ->
            FollowersFollowingDialog(
                userId = fUserId,
                isFollowers = true,
                viewModel = viewModel,
                onDismiss = { viewModel.closeFollowers() }
            )
        }

        showFollowingDialogUserId?.let { fUserId ->
            FollowersFollowingDialog(
                userId = fUserId,
                isFollowers = false,
                viewModel = viewModel,
                onDismiss = { viewModel.closeFollowing() }
            )
        }
    }
}

@Composable
fun VivaBottomNavigationBar(
    currentTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    unreadCount: Int,
    isFeedScreen: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isFeedScreen) Color.Black.copy(alpha = 0.9f) else VivaBlack)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .height(56.dp)
            .testTag("viva_bottom_navigation")
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Home
            NavTabItem(
                label = "Home",
                icon = if (currentTab == MainTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                isSelected = currentTab == MainTab.HOME,
                testTag = "nav_home",
                onClick = { onTabSelected(MainTab.HOME) }
            )

            // Discover
            NavTabItem(
                label = "Discover",
                icon = if (currentTab == MainTab.DISCOVER) Icons.Filled.Search else Icons.Outlined.Search,
                isSelected = currentTab == MainTab.DISCOVER,
                testTag = "nav_discover",
                onClick = { onTabSelected(MainTab.DISCOVER) }
            )

            // Central (+) Create Button
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(VivaPink, VivaCyan)
                        )
                    )
                    .clickable { onTabSelected(MainTab.CREATE) }
                    .testTag("nav_create_button"),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(VivaBlack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create Video",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Inbox
            NavTabItem(
                label = "Inbox",
                icon = if (currentTab == MainTab.INBOX) Icons.Filled.ChatBubble else Icons.Outlined.ChatBubbleOutline,
                isSelected = currentTab == MainTab.INBOX,
                badgeCount = if (unreadCount > 0) unreadCount else null,
                testTag = "nav_inbox",
                onClick = { onTabSelected(MainTab.INBOX) }
            )

            // Profile
            NavTabItem(
                label = "Profile",
                icon = if (currentTab == MainTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                isSelected = currentTab == MainTab.PROFILE,
                testTag = "nav_profile",
                onClick = { onTabSelected(MainTab.PROFILE) }
            )
        }
    }
}

@Composable
private fun NavTabItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit,
    badgeCount: Int? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable { onClick() }
            .testTag(testTag)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color.White else VivaTextSecondary,
                modifier = Modifier.size(24.dp)
            )
            if (badgeCount != null && badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .offset(x = 6.dp, y = (-4).dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(VivaPink),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (badgeCount > 9) "9+" else badgeCount.toString(),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) Color.White else VivaTextSecondary,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
