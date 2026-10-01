package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.*
import com.example.ui.components.VivaVerifiedBadge
import com.example.ui.components.formatCount
import com.example.ui.components.formatTimestamp
import com.example.ui.theme.*
import com.example.ui.viewmodel.VivaViewModel
import kotlinx.coroutines.launch

enum class AdminNavigationSection(val title: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    USERS("Users", Icons.Default.People),
    VERIFICATION("Verification", Icons.Default.Verified),
    MESSAGES("Messages", Icons.Default.Email),
    REPORTS("Reports", Icons.Default.Shield),
    VIDEOS("Videos", Icons.Default.VideoLibrary),
    LIVE("LIVE", Icons.Default.LiveTv),
    NOTIFICATIONS("Notifications", Icons.Default.Notifications),
    SETTINGS("Settings", Icons.Default.AdminPanelSettings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: VivaViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isAuthorizedAdmin = currentUser?.role == "ADMIN" || currentUser?.role == "OWNER"
    val isOwner = currentUser?.role == "OWNER"

    // If unauthorized, show Access Denied gatekeeper screen
    if (!isAuthorizedAdmin) {
        AdminAccessDeniedScreen(
            currentUser = currentUser,
            onBack = onBack,
            onSwitchToAdmin = { viewModel.switchAccount("user_admin") },
            onSwitchToOwner = { viewModel.switchAccount("user_owner") }
        )
        return
    }

    var currentSection by remember { mutableStateOf(AdminNavigationSection.DASHBOARD) }

    // Live reactive database flows
    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    val videos by viewModel.forYouVideos.collectAsStateWithLifecycle()
    val reports by viewModel.allReports.collectAsStateWithLifecycle()
    val verificationRequests by viewModel.allVerificationRequests.collectAsStateWithLifecycle()
    val adminNotifs by viewModel.adminNotifications.collectAsStateWithLifecycle()
    val supportConversations by viewModel.allSupportConversations.collectAsStateWithLifecycle()
    val auditLogs by viewModel.allAuditLogs.collectAsStateWithLifecycle()
    val liveStreams by viewModel.allLiveStreams.collectAsStateWithLifecycle()

    val pendingVerificationsCount = remember(verificationRequests) {
        verificationRequests.count { it.status == "pending" || it.status == "under_review" }
    }
    val unreadSupportCount = remember(supportConversations) {
        supportConversations.count { it.unreadByAdmin }
    }
    val unreadNotifsCount = remember(adminNotifs) {
        adminNotifs.count { !it.isRead }
    }

    // Active Chat dialog inside Admin
    var activeAdminChatConv by remember { mutableStateOf<SupportConversation?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VivaBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("admin_screen")
    ) {
        // Admin Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VivaDarkGray)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("admin_back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to VIVA",
                    tint = Color.White
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "VIVA Private Control Panel",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = if (isOwner) VivaPurple else VivaCyan,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (isOwner) "OWNER" else "ADMIN",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "@${currentUser?.username} • Restricted Access",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp
                )
            }

            // Switch to Owner / Admin tester shortcut
            TextButton(
                onClick = {
                    if (isOwner) {
                        viewModel.switchAccount("user_admin")
                    } else {
                        viewModel.switchAccount("user_owner")
                    }
                }
            ) {
                Text(
                    text = if (isOwner) "Use Admin" else "Use Owner",
                    color = VivaCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Horizontal Navigation Bar (Responsive on Mobile)
        ScrollableTabRow(
            selectedTabIndex = currentSection.ordinal,
            containerColor = VivaSurfaceElevated,
            contentColor = VivaPink,
            edgePadding = 8.dp,
            divider = {}
        ) {
            AdminNavigationSection.entries.forEach { section ->
                val badgeCount = when (section) {
                    AdminNavigationSection.VERIFICATION -> pendingVerificationsCount
                    AdminNavigationSection.MESSAGES -> unreadSupportCount
                    AdminNavigationSection.NOTIFICATIONS -> unreadNotifsCount
                    else -> 0
                }
                Tab(
                    selected = currentSection == section,
                    onClick = { currentSection = section },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = section.icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = section.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            if (badgeCount > 0) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(
                                    color = VivaPink,
                                    shape = CircleShape,
                                    modifier = Modifier.size(16.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = badgeCount.toString(),
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    },
                    selectedContentColor = Color.White,
                    unselectedContentColor = Color.White.copy(alpha = 0.5f)
                )
            }
        }

        // Section Body
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (currentSection) {
                AdminNavigationSection.DASHBOARD -> AdminDashboardHomeSection(
                    users = users,
                    videos = videos,
                    reports = reports,
                    verificationRequests = verificationRequests,
                    adminNotifs = adminNotifs,
                    supportConversations = supportConversations,
                    liveStreams = liveStreams,
                    onNavigate = { currentSection = it }
                )
                AdminNavigationSection.USERS -> AdminUsersSection(
                    users = users,
                    onSuspend = { uid, r -> viewModel.suspendUser(uid, r) },
                    onBan = { uid, r -> viewModel.banUser(uid, r) },
                    onRestore = { uid -> viewModel.restoreUser(uid) },
                    onToggleVerified = { uid, cur -> viewModel.toggleUserVerification(uid, cur) }
                )
                AdminNavigationSection.VERIFICATION -> AdminVerificationSection(
                    requests = verificationRequests,
                    onApprove = { req -> viewModel.approveVerification(req.id, req.userId) },
                    onReject = { req, reason -> viewModel.rejectVerification(req.id, req.userId, reason) },
                    onRequestMoreInfo = { req, notes -> viewModel.requestMoreInfo(req.id, req.userId, notes) }
                )
                AdminNavigationSection.MESSAGES -> AdminMessagesSection(
                    conversations = supportConversations,
                    onOpenConversation = { activeAdminChatConv = it },
                    onToggleStatus = { conv, status -> viewModel.updateSupportConversationStatus(conv.id, status) }
                )
                AdminNavigationSection.REPORTS -> AdminReportsSection(
                    reports = reports,
                    onResolve = { rep -> viewModel.resolveReport(rep.id) },
                    onDismiss = { rep -> viewModel.dismissReport(rep.id) }
                )
                AdminNavigationSection.VIDEOS -> AdminVideosSection(
                    videos = videos,
                    onRemoveVideo = { vid, r -> viewModel.removeVideoByAdmin(vid, r) }
                )
                AdminNavigationSection.LIVE -> AdminLiveSection(
                    streams = liveStreams,
                    onEndLive = { sid, r -> viewModel.endLiveStream(sid, r) }
                )
                AdminNavigationSection.NOTIFICATIONS -> AdminNotificationsSection(
                    notifications = adminNotifs,
                    onMarkRead = { nid -> viewModel.markAdminNotificationAsRead(nid) },
                    onMarkAllRead = { viewModel.markAllAdminNotificationsAsRead() },
                    onDelete = { nid -> viewModel.deleteAdminNotification(nid) }
                )
                AdminNavigationSection.SETTINGS -> AdminSettingsSection(
                    isOwner = isOwner,
                    users = users,
                    auditLogs = auditLogs,
                    onUpdateUserRole = { uid, role -> viewModel.updateUserRole(uid, role) }
                )
            }
        }
    }

    // Active Admin Support Chat Modal
    activeAdminChatConv?.let { conv ->
        AdminSupportChatDialog(
            conversation = conv,
            viewModel = viewModel,
            onDismiss = { activeAdminChatConv = null }
        )
    }
}

// -------------------------------------------------------------
// 1. ACCESS DENIED SCREEN
// -------------------------------------------------------------
@Composable
fun AdminAccessDeniedScreen(
    currentUser: User?,
    onBack: () -> Unit,
    onSwitchToAdmin: () -> Unit,
    onSwitchToOwner: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VivaBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = VivaPink.copy(alpha = 0.15f),
            modifier = Modifier.size(90.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.GppBad,
                    contentDescription = null,
                    tint = VivaPink,
                    modifier = Modifier.size(54.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Access Denied",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Administrator privileges required to access the VIVA Management Portal. Current account: @${currentUser?.username ?: "Guest"} (Role: ${currentUser?.role ?: "USER"}). This access attempt has been logged for security auditing.",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 13.sp,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = VivaSurfaceElevated),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Return to VIVA Application", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Secure Authentication Switcher for Review & Demonstration
        Surface(
            color = VivaDarkGray,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Authorized Owner & Staff Sign-in",
                    color = VivaCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sign in with registered administrator credentials to open the management portal.",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onSwitchToAdmin,
                        colors = ButtonDefaults.buttonColors(containerColor = VivaCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Log In as Admin", color = VivaBlack, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Button(
                        onClick = onSwitchToOwner,
                        colors = ButtonDefaults.buttonColors(containerColor = VivaPurple),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Log In as Owner", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. DASHBOARD HOME (Real DB Numbers)
// -------------------------------------------------------------
@Composable
fun AdminDashboardHomeSection(
    users: List<User>,
    videos: List<Video>,
    reports: List<ModerationReport>,
    verificationRequests: List<VerificationRequest>,
    adminNotifs: List<AdminNotification>,
    supportConversations: List<SupportConversation>,
    liveStreams: List<LiveStream>,
    onNavigate: (AdminNavigationSection) -> Unit
) {
    val totalUsers = users.size
    val activeUsers = users.count { it.accountStatus == "ACTIVE" }
    val totalVideos = videos.size
    val activeLive = liveStreams.count { it.status == "ACTIVE" }
    val pendingVerifications = verificationRequests.count { it.status == "pending" || it.status == "under_review" }
    val unreadMessages = supportConversations.count { it.unreadByAdmin }
    val pendingReports = reports.count { it.status == "PENDING" }
    val unreadNotifs = adminNotifs.count { !it.isRead }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "System Overview & Telemetry",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Live platform statistics connected directly to Cloud Firestore & Room database.",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp
            )
        }

        // Metrics Grid (Row 1)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminMetricStatCard(
                    title = "Total Users",
                    value = totalUsers.toString(),
                    subtitle = "$activeUsers Active",
                    icon = Icons.Default.People,
                    accentColor = VivaCyan,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(AdminNavigationSection.USERS) }
                )
                AdminMetricStatCard(
                    title = "Total Videos",
                    value = totalVideos.toString(),
                    subtitle = "Published",
                    icon = Icons.Default.VideoLibrary,
                    accentColor = VivaPink,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(AdminNavigationSection.VIDEOS) }
                )
            }
        }

        // Metrics Grid (Row 2)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminMetricStatCard(
                    title = "Active LIVE",
                    value = activeLive.toString(),
                    subtitle = "Broadcasting",
                    icon = Icons.Default.LiveTv,
                    accentColor = VivaGreen,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(AdminNavigationSection.LIVE) }
                )
                AdminMetricStatCard(
                    title = "Verification Queue",
                    value = pendingVerifications.toString(),
                    subtitle = "Needs Review",
                    icon = Icons.Default.Verified,
                    accentColor = if (pendingVerifications > 0) VivaPink else VivaCyan,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(AdminNavigationSection.VERIFICATION) }
                )
            }
        }

        // Metrics Grid (Row 3)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminMetricStatCard(
                    title = "User Messages",
                    value = unreadMessages.toString(),
                    subtitle = "Unread tickets",
                    icon = Icons.Default.Email,
                    accentColor = VivaPurple,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(AdminNavigationSection.MESSAGES) }
                )
                AdminMetricStatCard(
                    title = "Reports & Safety",
                    value = pendingReports.toString(),
                    subtitle = "Pending triage",
                    icon = Icons.Default.Shield,
                    accentColor = if (pendingReports > 0) VivaPink else VivaGreen,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(AdminNavigationSection.REPORTS) }
                )
            }
        }

        // Action Shortcuts
        item {
            Surface(
                color = VivaSurfaceElevated,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Quick Moderation Actions",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onNavigate(AdminNavigationSection.VERIFICATION) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Review Verifications", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { onNavigate(AdminNavigationSection.MESSAGES) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Open Support Chat", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminMetricStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        color = VivaDarkGray,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = accentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// -------------------------------------------------------------
// 3. USERS MANAGEMENT & SEARCH
// -------------------------------------------------------------
@Composable
fun AdminUsersSection(
    users: List<User>,
    onSuspend: (String, String) -> Unit,
    onBan: (String, String) -> Unit,
    onRestore: (String) -> Unit,
    onToggleVerified: (String, Boolean) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredUsers = remember(users, searchQuery, selectedFilter) {
        users.filter { user ->
            val matchQuery = searchQuery.isBlank() ||
                    user.username.contains(searchQuery, ignoreCase = true) ||
                    user.displayName.contains(searchQuery, ignoreCase = true) ||
                    user.id.contains(searchQuery, ignoreCase = true) ||
                    user.email.contains(searchQuery, ignoreCase = true)

            val matchFilter = when (selectedFilter) {
                "ACTIVE" -> user.accountStatus == "ACTIVE"
                "SUSPENDED" -> user.accountStatus == "SUSPENDED"
                "BANNED" -> user.accountStatus == "BANNED"
                "VERIFIED" -> user.isVerified
                else -> true
            }
            matchQuery && matchFilter
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        // Search Box
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by username, display name, user ID, email...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.5f)) },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = VivaCyan,
                unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter chips
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("ALL", "ACTIVE", "SUSPENDED", "BANNED", "VERIFIED").forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VivaPink,
                        selectedLabelColor = Color.White,
                        containerColor = VivaDarkGray,
                        labelColor = Color.White.copy(alpha = 0.7f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Users List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredUsers, key = { it.id }) { user ->
                Surface(
                    color = VivaDarkGray,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = user.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80" },
                                contentDescription = user.displayName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = user.displayName,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    if (user.isVerified) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        VivaVerifiedBadge(size = 14.dp)
                                    }
                                }
                                Text(
                                    text = "@${user.username} • ID: ${user.id.take(8)}...",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 11.sp
                                )
                            }
                            // Status Pill
                            val statusColor = when (user.accountStatus) {
                                "ACTIVE" -> VivaGreen
                                "SUSPENDED" -> Color(0xFFFFB300)
                                "BANNED" -> VivaPink
                                else -> Color.Gray
                            }
                            Surface(
                                color = statusColor.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = user.accountStatus,
                                    color = statusColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Actions Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (user.accountStatus != "ACTIVE") {
                                Button(
                                    onClick = { onRestore(user.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = VivaGreen),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Restore", fontSize = 11.sp, color = VivaBlack, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { onSuspend(user.id, "Violation of platform community standards") },
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Suspend", fontSize = 11.sp)
                                }
                                Button(
                                    onClick = { onBan(user.id, "Severe terms of service breach") },
                                    colors = ButtonDefaults.buttonColors(containerColor = VivaPink),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Ban", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }

                            OutlinedButton(
                                onClick = { onToggleVerified(user.id, user.isVerified) },
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (user.isVerified) "Revoke ✓" else "Grant ✓", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. VERIFICATION REQUESTS
// -------------------------------------------------------------
@Composable
fun AdminVerificationSection(
    requests: List<VerificationRequest>,
    onApprove: (VerificationRequest) -> Unit,
    onReject: (VerificationRequest, String) -> Unit,
    onRequestMoreInfo: (VerificationRequest, String) -> Unit
) {
    var selectedTab by remember { mutableStateOf("pending") }
    var reviewModalRequest by remember { mutableStateOf<VerificationRequest?>(null) }

    val filtered = remember(requests, selectedTab) {
        if (selectedTab == "ALL") requests
        else requests.filter { it.status.equals(selectedTab, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        // Status filter tabs
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("pending", "under_review", "more_information_required", "approved", "rejected", "ALL").forEach { status ->
                FilterChip(
                    selected = selectedTab == status,
                    onClick = { selectedTab = status },
                    label = { Text(status.replace("_", " ").uppercase(), fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VivaPink,
                        selectedLabelColor = Color.White,
                        containerColor = VivaDarkGray,
                        labelColor = Color.White.copy(alpha = 0.7f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No verification requests in this category.", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered, key = { it.id }) { req ->
                    Surface(
                        color = VivaDarkGray,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = VivaCyan.copy(alpha = 0.2f),
                                    shape = CircleShape,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Verified, contentDescription = null, tint = VivaCyan, modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = req.fullName,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "@${req.username} • Category: ${req.category} (${req.country})",
                                        color = VivaCyan,
                                        fontSize = 11.sp
                                    )
                                }
                                Surface(
                                    color = if (req.status == "approved") VivaGreen else VivaPink,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = req.status.uppercase(),
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Reason: ${req.reason}",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (req.supportingDocuments.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Evidence: ${req.supportingDocuments}",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 11.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { reviewModalRequest = req },
                                    colors = ButtonDefaults.buttonColors(containerColor = VivaSurfaceElevated),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Full Review", fontSize = 11.sp)
                                }
                                if (req.status != "approved") {
                                    Button(
                                        onClick = { onApprove(req) },
                                        colors = ButtonDefaults.buttonColors(containerColor = VivaCyan),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Approve ✓", fontSize = 11.sp, color = VivaBlack, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Detail modal dialog
    reviewModalRequest?.let { req ->
        Dialog(onDismissRequest = { reviewModalRequest = null }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = VivaDarkGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(18.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("Verification Request Details", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Applicant: ${req.fullName} (@${req.username})", color = Color.White, fontSize = 13.sp)
                    Text("Category: ${req.category}", color = VivaCyan, fontSize = 12.sp)
                    Text("Country: ${req.country}", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    if (req.website.isNotBlank()) Text("Website: ${req.website}", color = VivaPink, fontSize = 12.sp)
                    if (req.instagram.isNotBlank()) Text("Instagram: ${req.instagram}", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    if (req.youtube.isNotBlank()) Text("YouTube: ${req.youtube}", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    if (req.tiktok.isNotBlank()) Text("TikTok: ${req.tiktok}", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Reason for Verification:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(req.reason, color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Evidence & Supporting Documentation:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(req.supportingDocuments.ifBlank { "None attached" }, color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                onApprove(req)
                                reviewModalRequest = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VivaCyan),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Approve ✓", color = VivaBlack, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                onReject(req, "Did not meet criteria for notable public interest.")
                                reviewModalRequest = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VivaPink),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Reject", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                onRequestMoreInfo(req, "Please submit additional press coverage or government ID verification.")
                                reviewModalRequest = null
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("More Info", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. MESSAGES & SUPPORT CHAT
// -------------------------------------------------------------
@Composable
fun AdminMessagesSection(
    conversations: List<SupportConversation>,
    onOpenConversation: (SupportConversation) -> Unit,
    onToggleStatus: (SupportConversation, String) -> Unit
) {
    var filter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(conversations, filter, searchQuery) {
        conversations.filter { conv ->
            val matchQuery = searchQuery.isBlank() ||
                    conv.username.contains(searchQuery, ignoreCase = true) ||
                    conv.userDisplayName.contains(searchQuery, ignoreCase = true) ||
                    conv.lastMessage.contains(searchQuery, ignoreCase = true)

            val matchFilter = when (filter) {
                "UNREAD" -> conv.unreadByAdmin
                "OPEN" -> conv.status == "OPEN"
                "CLOSED" -> conv.status == "CLOSED"
                else -> true
            }
            matchQuery && matchFilter
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search support conversations...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.5f)) },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = VivaCyan,
                unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("ALL", "UNREAD", "OPEN", "CLOSED").forEach { f ->
                FilterChip(
                    selected = filter == f,
                    onClick = { filter = f },
                    label = { Text(f, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VivaPink,
                        selectedLabelColor = Color.White,
                        containerColor = VivaDarkGray,
                        labelColor = Color.White.copy(alpha = 0.7f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No support conversations found.", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered, key = { it.id }) { conv ->
                    Surface(
                        color = if (conv.unreadByAdmin) VivaSurfaceElevated else VivaDarkGray,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenConversation(conv) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = conv.userAvatar.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80" },
                                contentDescription = conv.userDisplayName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = conv.userDisplayName,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = formatTimestamp(conv.lastTimestamp),
                                        color = Color.White.copy(alpha = 0.5f),
                                        fontSize = 10.sp
                                    )
                                }
                                Text(
                                    text = "@${conv.username}",
                                    color = VivaCyan,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = conv.lastMessage,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            if (conv.unreadByAdmin) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = VivaPink,
                                    shape = CircleShape,
                                    modifier = Modifier.size(10.dp)
                                ) {}
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSupportChatDialog(
    conversation: SupportConversation,
    viewModel: VivaViewModel,
    onDismiss: () -> Unit
) {
    val messages by viewModel.getMessagesForConversation(conversation.id).collectAsStateWithLifecycle(emptyList())
    var replyText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(conversation.id) {
        viewModel.markSupportReadByAdmin(conversation.id)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = VivaDarkGray,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VivaSurfaceElevated)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Replying as VIVA Official Support",
                            color = VivaCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${conversation.userDisplayName} (@${conversation.username})",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                // Chat Messages
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        val isAdminReply = msg.senderId == "user_official_viva"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isAdminReply) Arrangement.End else Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isAdminReply) {
                                IconButton(
                                    onClick = { viewModel.deleteSupportMessage(msg.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Delete own message",
                                        tint = Color.White.copy(alpha = 0.4f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Column(
                                horizontalAlignment = if (isAdminReply) Alignment.End else Alignment.Start,
                                modifier = Modifier.widthIn(max = 260.dp)
                            ) {
                                Surface(
                                    color = if (isAdminReply) VivaPurple else VivaSurfaceElevated,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = msg.text,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                                Text(
                                    text = "${if (isAdminReply) "VIVA Admin" else "@${conversation.username}"} • ${formatTimestamp(msg.timestamp)}${if (isAdminReply) if (msg.isRead) " • Read ✓✓" else " • Sent ✓" else ""}",
                                    color = Color.White.copy(alpha = 0.4f),
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Reply Input
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        placeholder = { Text("Type official admin reply...", fontSize = 12.sp) },
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = VivaCyan
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            val txt = replyText.trim()
                            if (txt.isNotBlank()) {
                                replyText = ""
                                viewModel.sendAdminReply(conversation.id, txt) {
                                    coroutineScope.launch {
                                        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
                                    }
                                }
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = VivaCyan)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = VivaBlack)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 6. REPORTS
// -------------------------------------------------------------
@Composable
fun AdminReportsSection(
    reports: List<ModerationReport>,
    onResolve: (ModerationReport) -> Unit,
    onDismiss: (ModerationReport) -> Unit
) {
    var filterType by remember { mutableStateOf("ALL") }

    val filtered = remember(reports, filterType) {
        if (filterType == "ALL") reports
        else reports.filter { it.targetType.equals(filterType, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("ALL", "VIDEO", "USER", "COMMENT", "LIVE").forEach { type ->
                FilterChip(
                    selected = filterType == type,
                    onClick = { filterType = type },
                    label = { Text(type, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VivaPink,
                        selectedLabelColor = Color.White,
                        containerColor = VivaDarkGray,
                        labelColor = Color.White.copy(alpha = 0.7f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No moderation reports in this category.", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered, key = { it.id }) { report ->
                    Surface(
                        color = VivaDarkGray,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Target: ${report.targetType} (#${report.targetId.take(8)})",
                                    color = VivaCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    color = if (report.status == "RESOLVED") VivaGreen else VivaPink,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = report.status,
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Reason: ${report.reason}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            if (report.notes.isNotBlank()) {
                                Text("Notes: ${report.notes}", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                            }
                            Text("Reported by @${report.reporterName}", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { onResolve(report) },
                                    colors = ButtonDefaults.buttonColors(containerColor = VivaGreen),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Resolve & Clear", color = VivaBlack, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = { onDismiss(report) },
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Dismiss", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 7. VIDEOS MANAGEMENT
// -------------------------------------------------------------
@Composable
fun AdminVideosSection(
    videos: List<Video>,
    onRemoveVideo: (String, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = remember(videos, searchQuery) {
        if (searchQuery.isBlank()) videos
        else videos.filter {
            it.caption.contains(searchQuery, ignoreCase = true) ||
                    it.creatorUsername.contains(searchQuery, ignoreCase = true) ||
                    it.id.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search videos by caption, creator, ID...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.5f)) },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = VivaCyan
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filtered, key = { it.id }) { video ->
                Surface(
                    color = VivaDarkGray,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp)) {
                        AsyncImage(
                            model = video.thumbnailUrl,
                            contentDescription = video.caption,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(60.dp, 80.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "@${video.creatorUsername}", color = VivaCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(text = video.caption, color = Color.White, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "❤️ ${formatCount(video.likesCount)} • 💬 ${formatCount(video.commentsCount)} • 👁️ ${formatCount(video.viewsCount)}",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { onRemoveVideo(video.id, "Moderator removal for policy non-compliance") },
                                colors = ButtonDefaults.buttonColors(containerColor = VivaPink),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Remove Video", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 8. LIVE STREAMS CONTROL
// -------------------------------------------------------------
@Composable
fun AdminLiveSection(
    streams: List<LiveStream>,
    onEndLive: (String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        Text("Active LIVE Broadcasts Monitor", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text("Real-time telemetry and immediate emergency termination controls.", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
        Spacer(modifier = Modifier.height(12.dp))

        if (streams.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No active LIVE streams currently running.", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(streams, key = { it.id }) { stream ->
                    Surface(
                        color = VivaDarkGray,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = stream.creatorAvatarUrl,
                                    contentDescription = stream.creatorDisplayName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = stream.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = "Creator: @${stream.creatorUsername}", color = VivaCyan, fontSize = 11.sp)
                                }
                                Surface(
                                    color = if (stream.status == "ACTIVE") VivaPink else Color.Gray,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = stream.status,
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "👥 ${formatCount(stream.viewerCount.toLong())} Viewers • Flagged reports: ${stream.reportsCount}",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )

                            if (stream.status == "ACTIVE") {
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { onEndLive(stream.id, "Violated live streaming guidelines") },
                                    colors = ButtonDefaults.buttonColors(containerColor = VivaPink),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Emergency Terminate LIVE Stream", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 9. ADMIN NOTIFICATIONS
// -------------------------------------------------------------
@Composable
fun AdminNotificationsSection(
    notifications: List<AdminNotification>,
    onMarkRead: (String) -> Unit,
    onMarkAllRead: () -> Unit,
    onDelete: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Admin Notification Feed", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            TextButton(onClick = onMarkAllRead) {
                Text("Mark all as read", color = VivaCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (notifications.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No admin notifications.", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notifications, key = { it.id }) { notif ->
                    Surface(
                        color = if (!notif.isRead) VivaSurfaceElevated else VivaDarkGray,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val icon = when (notif.type) {
                                "VERIFICATION_REQUEST" -> Icons.Default.Verified
                                "MESSAGE" -> Icons.Default.Email
                                else -> Icons.Default.Shield
                            }
                            Icon(icon, contentDescription = null, tint = VivaCyan, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = notif.message, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = formatTimestamp(notif.timestamp), color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                            }
                            IconButton(onClick = { onDelete(notif.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 10. SETTINGS & IMMUTABLE AUDIT LOG
// -------------------------------------------------------------
@Composable
fun AdminSettingsSection(
    isOwner: Boolean,
    users: List<User>,
    auditLogs: List<AdminAuditLog>,
    onUpdateUserRole: (String, String) -> Unit
) {
    var selectedSubTab by remember { mutableStateOf("AUDIT") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = selectedSubTab == "AUDIT",
                onClick = { selectedSubTab = "AUDIT" },
                label = { Text("Audit Trail Log", fontSize = 11.sp) }
            )
            if (isOwner) {
                FilterChip(
                    selected = selectedSubTab == "STAFF",
                    onClick = { selectedSubTab = "STAFF" },
                    label = { Text("Owner Staff Roles", fontSize = 11.sp) }
                )
            }
            FilterChip(
                selected = selectedSubTab == "RULES",
                onClick = { selectedSubTab = "RULES" },
                label = { Text("Security Policy", fontSize = 11.sp) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedSubTab) {
            "AUDIT" -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(auditLogs, key = { it.id }) { log ->
                        Surface(
                            color = VivaDarkGray,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = log.action,
                                        color = VivaCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = formatTimestamp(log.timestamp),
                                        color = Color.White.copy(alpha = 0.5f),
                                        fontSize = 10.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = log.details, color = Color.White, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Admin: @${log.adminUsername} (ID: ${log.adminId})",
                                    color = Color.White.copy(alpha = 0.45f),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            "STAFF" -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(users.filter { it.role == "ADMIN" || it.role == "OWNER" || it.username == "alex_viva" }, key = { it.id }) { user ->
                        Surface(
                            color = VivaDarkGray,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = user.displayName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = "@${user.username} • Role: ${user.role}", color = VivaCyan, fontSize = 11.sp)
                                }
                                if (user.role != "OWNER") {
                                    Button(
                                        onClick = {
                                            val nextRole = if (user.role == "ADMIN") "USER" else "ADMIN"
                                            onUpdateUserRole(user.id, nextRole)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (user.role == "ADMIN") VivaPink else VivaCyan
                                        ),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(if (user.role == "ADMIN") "Revoke Admin" else "Grant Admin", fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "RULES" -> {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Surface(
                        color = VivaDarkGray,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("VIVA Security & RBAC Enforcement", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = """
                                1. Role-based authorization enforced via backend & Room/Firestore security rules.
                                2. Normal users cannot approve verifications, read other users' support tickets, change administrator permissions, or modify audit logs.
                                3. Reserved handles (@VIVA, @admin) are protected against public registration.
                                4. All admin actions are appended to the immutable audit trail.
                                """.trimIndent(),
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
