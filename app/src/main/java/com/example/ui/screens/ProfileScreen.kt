package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.User
import com.example.data.model.Video
import com.example.ui.components.VivaVerifiedBadge
import com.example.ui.components.formatCount
import com.example.ui.theme.*
import com.example.ui.viewmodel.ActiveReportTarget
import com.example.ui.viewmodel.VivaViewModel

enum class ProfileTab {
    VIDEOS, LIKED, SAVED, REPOSTS
}

@Composable
fun ProfileScreen(
    user: User,
    isCurrentUser: Boolean,
    viewModel: VivaViewModel,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(ProfileTab.VIDEOS) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showSwitchAccountDialog by remember { mutableStateOf(false) }

    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val creatorVideos by viewModel.getVideosForCreator(user.id).collectAsStateWithLifecycle(emptyList())
    val likedVideos by viewModel.getLikedVideos().collectAsStateWithLifecycle(emptyList())
    val savedVideos by viewModel.getSavedVideos().collectAsStateWithLifecycle(emptyList())
    val myVerificationReq by viewModel.myVerificationRequest.collectAsStateWithLifecycle()

    val displayedVideos: List<Video> = when (selectedTab) {
        ProfileTab.VIDEOS -> creatorVideos
        ProfileTab.LIKED -> likedVideos
        ProfileTab.SAVED -> savedVideos
        ProfileTab.REPOSTS -> creatorVideos.take(2)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VivaBlack)
            .statusBarsPadding()
            .testTag("profile_screen")
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            } else {
                // Switch Account quick button
                IconButton(onClick = { showSwitchAccountDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.SwitchAccount,
                        contentDescription = "Switch Account",
                        tint = VivaCyan
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "@${user.username}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                if (user.isVerified) {
                    Spacer(modifier = Modifier.width(6.dp))
                    VivaVerifiedBadge(size = 16.dp)
                }
            }

            Row {
                if (isCurrentUser) {
                    IconButton(onClick = { viewModel.openSettings() }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                } else {
                    Box {
                        IconButton(onClick = { showMoreMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More",
                                tint = Color.White
                            )
                        }
                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false },
                            modifier = Modifier.background(VivaSurfaceDark)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Report @${user.username}", color = VivaRed) },
                                onClick = {
                                    showMoreMenu = false
                                    viewModel.openReport(
                                        ActiveReportTarget(
                                            type = "USER",
                                            id = user.id,
                                            title = "@${user.username}"
                                        )
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Block @${user.username}", color = Color.White) },
                                onClick = {
                                    showMoreMenu = false
                                    viewModel.blockUser(user)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Profile Avatar & Verified Badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = user.displayName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .border(2.dp, if (user.isVerified) VivaVerifiedBlue else VivaBorder, CircleShape)
                )
                if (user.isVerified) {
                    VivaVerifiedBadge(size = 22.dp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Display Name & Bio
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.displayName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                if (user.isVerified) {
                    Spacer(modifier = Modifier.width(6.dp))
                    VivaVerifiedBadge(size = 18.dp)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = user.bio,
                color = VivaTextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            if (user.website.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = "Website",
                        tint = VivaCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = user.website.removePrefix("https://").removePrefix("http://"),
                        color = VivaCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Verification Request Shortcut Badge for Current User
            if (isCurrentUser) {
                Spacer(modifier = Modifier.height(8.dp))
                val (badgeText, badgeBg, badgeTextColor) = when {
                    user.isVerified -> Triple("Official Verified Creator ✓", VivaVerifiedBlue.copy(alpha = 0.2f), VivaCyan)
                    myVerificationReq?.status == "pending" -> Triple("Verification: Pending Review ⏳", VivaYellow.copy(alpha = 0.2f), VivaYellow)
                    myVerificationReq?.status == "more_information_required" -> Triple("Verification: More Info Needed ⚠️", VivaPink.copy(alpha = 0.2f), VivaPink)
                    else -> Triple("Request Verification ✓", VivaSurfaceVariant, VivaTextSecondary)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(badgeBg)
                        .clickable { viewModel.openVerificationRequestScreen() }
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = badgeTextColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Stats Row (Following, Followers, Likes) - Clickable!
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.clickable { viewModel.openFollowing(user.id) }) {
                ProfileStatItem(count = formatCount(user.followingCount.toLong()), label = "Following")
            }
            Box(modifier = Modifier.width(1.dp).height(24.dp).background(VivaBorder))
            Box(modifier = Modifier.clickable { viewModel.openFollowers(user.id) }) {
                ProfileStatItem(count = formatCount(user.followersCount.toLong()), label = "Followers")
            }
            Box(modifier = Modifier.width(1.dp).height(24.dp).background(VivaBorder))
            ProfileStatItem(count = formatCount(user.likesCount.toLong()), label = "Likes")
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Profile Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isCurrentUser) {
                Button(
                    onClick = { viewModel.openEditProfile() },
                    colors = ButtonDefaults.buttonColors(containerColor = VivaSurfaceVariant),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text(text = "Edit Profile", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = {
                        val shareUrl = "https://viva.social/@${user.username}"
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("VIVA Profile", shareUrl)
                        clipboard.setPrimaryClip(clip)
                        viewModel.showToast("Profile link copied to clipboard!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VivaSurfaceVariant),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text(text = "Share Profile", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                if (user.role == "ADMIN" || user.role == "OWNER") {
                    Button(
                        onClick = { viewModel.openAdminDashboard() },
                        colors = ButtonDefaults.buttonColors(containerColor = VivaPink),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = "Admin", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (user.role == "OWNER") "Owner" else "Admin", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                IconButton(
                    onClick = { viewModel.openContactViva() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(VivaSurfaceVariant)
                ) {
                    Icon(imageVector = Icons.Default.SupportAgent, contentDescription = "Contact VIVA", tint = VivaCyan, modifier = Modifier.size(20.dp))
                }
            } else {
                if (user.username == "VIVA" || user.id == "user_official_viva") {
                    Button(
                        onClick = { viewModel.openContactViva() },
                        colors = ButtonDefaults.buttonColors(containerColor = VivaCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(40.dp)
                    ) {
                        Icon(Icons.Default.SupportAgent, contentDescription = null, tint = VivaBlack, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Contact VIVA Official Support", color = VivaBlack, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                } else {
                    Button(
                        onClick = { viewModel.toggleFollowCreator(user.id, user.isFollowing) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (user.isFollowing) VivaSurfaceVariant else VivaPink
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Text(
                            text = if (user.isFollowing) "Following" else "Follow",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = { viewModel.openChat(user) },
                        colors = ButtonDefaults.buttonColors(containerColor = VivaSurfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Text(text = "Message", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs Row (Videos, Liked, Saved, Reposts)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ProfileTabButton(
                icon = Icons.Outlined.GridOn,
                isSelected = selectedTab == ProfileTab.VIDEOS,
                onClick = { selectedTab = ProfileTab.VIDEOS }
            )
            ProfileTabButton(
                icon = Icons.Outlined.FavoriteBorder,
                isSelected = selectedTab == ProfileTab.LIKED,
                onClick = { selectedTab = ProfileTab.LIKED }
            )
            if (isCurrentUser) {
                ProfileTabButton(
                    icon = Icons.Outlined.BookmarkBorder,
                    isSelected = selectedTab == ProfileTab.SAVED,
                    onClick = { selectedTab = ProfileTab.SAVED }
                )
            }
            ProfileTabButton(
                icon = Icons.Outlined.Repeat,
                isSelected = selectedTab == ProfileTab.REPOSTS,
                onClick = { selectedTab = ProfileTab.REPOSTS }
            )
        }

        Divider(color = VivaBorder, thickness = 0.5.dp)

        // Videos Grid
        if (displayedVideos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = when (selectedTab) {
                            ProfileTab.LIKED -> Icons.Default.FavoriteBorder
                            ProfileTab.SAVED -> Icons.Default.BookmarkBorder
                            else -> Icons.Default.VideoLibrary
                        },
                        contentDescription = "Empty",
                        tint = VivaTextTertiary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = when (selectedTab) {
                            ProfileTab.LIKED -> "No liked videos yet"
                            ProfileTab.SAVED -> "No saved bookmarks"
                            ProfileTab.REPOSTS -> "No reposted videos"
                            else -> "No videos posted yet"
                        },
                        color = VivaTextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(displayedVideos, key = { it.id }) { video ->
                    VideoGridThumbnail(
                        video = video,
                        onClick = { viewModel.openCreatorProfile(video.creatorId) }
                    )
                }
            }
        }
    }

    // Switch Account Dialog
    if (showSwitchAccountDialog) {
        AlertDialog(
            onDismissRequest = { showSwitchAccountDialog = false },
            title = { Text(text = "Switch VIVA Account", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Switch between regular creator and administrator to test the complete verification lifecycle:",
                        color = VivaTextSecondary,
                        fontSize = 12.sp
                    )
                    allUsers.forEach { u ->
                        val isCurrent = u.id == user.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isCurrent) VivaPink.copy(alpha = 0.2f) else VivaSurfaceDark)
                                .clickable {
                                    viewModel.switchAccount(u.id)
                                    showSwitchAccountDialog = false
                                }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = u.avatarUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(36.dp).clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = u.displayName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    if (u.isVerified) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        VivaVerifiedBadge(size = 12.dp)
                                    }
                                }
                                Text(text = "@${u.username} • Role: ${u.role}", color = VivaTextSecondary, fontSize = 11.sp)
                            }
                            if (isCurrent) {
                                Text(text = "Active", color = VivaPink, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSwitchAccountDialog = false }) {
                    Text("Close", color = VivaCyan)
                }
            },
            containerColor = VivaSurfaceVariant
        )
    }
}

@Composable
private fun ProfileStatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
        )
        Text(
            text = label,
            color = VivaTextSecondary,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun ProfileTabButton(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) Color.White else VivaTextTertiary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .width(28.dp)
                .height(2.dp)
                .background(if (isSelected) Color.White else Color.Transparent)
        )
    }
}
