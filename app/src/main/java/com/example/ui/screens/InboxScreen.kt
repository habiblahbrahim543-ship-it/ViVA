package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.Notification
import com.example.data.model.User
import com.example.ui.components.formatTimestamp
import com.example.ui.theme.*
import com.example.ui.viewmodel.InboxFilter
import com.example.ui.viewmodel.VivaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
    viewModel: VivaViewModel,
    modifier: Modifier = Modifier
) {
    val inboxFilter by viewModel.inboxFilter.collectAsStateWithLifecycle()
    val allNotifications by viewModel.allNotifications.collectAsStateWithLifecycle()
    val allMessages by viewModel.allMessages.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()

    val filteredNotifications = remember(allNotifications, inboxFilter) {
        when (inboxFilter) {
            InboxFilter.LIKES -> allNotifications.filter { it.type == "LIKE" }
            InboxFilter.COMMENTS -> allNotifications.filter { it.type == "COMMENT" }
            InboxFilter.FOLLOWS -> allNotifications.filter { it.type == "FOLLOW" }
            else -> allNotifications
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VivaBlack)
            .statusBarsPadding()
            .testTag("inbox_screen")
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Inbox",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )

            if (inboxFilter != InboxFilter.MESSAGES) {
                TextButton(onClick = { viewModel.markAllNotificationsRead() }) {
                    Text(
                        text = "Mark all read",
                        color = VivaCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Filter Tabs Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = InboxFilter.values()
            items(filters) { filter ->
                val isSelected = inboxFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setInboxFilter(filter) },
                    label = {
                        Text(
                            text = when (filter) {
                                InboxFilter.ALL -> "All Activity"
                                InboxFilter.LIKES -> "Likes"
                                InboxFilter.COMMENTS -> "Comments"
                                InboxFilter.FOLLOWS -> "Followers"
                                InboxFilter.MESSAGES -> "Direct Messages"
                            },
                            color = if (isSelected) Color.White else VivaTextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VivaPink,
                        containerColor = VivaSurfaceDark
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (isSelected) VivaPink else VivaBorder,
                        enabled = true,
                        selected = isSelected
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        Divider(color = VivaBorder, thickness = 0.5.dp, modifier = Modifier.padding(top = 8.dp))

        // Content
        if (inboxFilter == InboxFilter.MESSAGES) {
            // Direct Messages Conversations List
            val conversationPartners = remember(allUsers) {
                allUsers.filter { !it.isCurrentUser }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(conversationPartners, key = { it.id }) { partner ->
                    val lastMsg = allMessages.find {
                        (it.senderId == partner.id && it.receiverId == "user_me") ||
                        (it.senderId == "user_me" && it.receiverId == partner.id)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(VivaSurfaceDark)
                            .clickable { viewModel.openChat(partner) }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = partner.avatarUrl,
                            contentDescription = partner.displayName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = partner.displayName,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                if (partner.isVerified) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified",
                                        tint = VivaCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = lastMsg?.text ?: "Start chatting with @${partner.username}",
                                color = VivaTextSecondary,
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                        }
                        if (lastMsg != null) {
                            Text(
                                text = formatTimestamp(lastMsg.timestamp),
                                color = VivaTextTertiary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        } else {
            // Notifications List
            if (filteredNotifications.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = "No notifications",
                            tint = VivaTextTertiary,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No notifications in this filter",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredNotifications, key = { it.id }) { notif ->
                        NotificationItemRow(
                            notification = notif,
                            onClick = {
                                viewModel.markNotificationRead(notif.id)
                                if (notif.senderId != "system") {
                                    viewModel.openCreatorProfile(notif.senderId)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItemRow(
    notification: Notification,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (!notification.isRead) VivaSurfaceElevated else VivaSurfaceDark)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon / Avatar with Type Badge
        Box(contentAlignment = Alignment.BottomEnd) {
            if (notification.senderAvatar.isNotBlank()) {
                AsyncImage(
                    model = notification.senderAvatar,
                    contentDescription = notification.senderUsername,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(VivaPink),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "System",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Notification type tiny badge
            val badgeColor = when (notification.type) {
                "LIKE" -> VivaPink
                "COMMENT" -> VivaCyan
                "FOLLOW" -> VivaPurple
                else -> VivaGreen
            }
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(badgeColor),
                contentAlignment = Alignment.Center
            ) {
                val icon = when (notification.type) {
                    "LIKE" -> Icons.Default.Favorite
                    "COMMENT" -> Icons.Default.ChatBubble
                    "FOLLOW" -> Icons.Default.PersonAdd
                    else -> Icons.Default.Notifications
                }
                Icon(
                    imageVector = icon,
                    contentDescription = notification.type,
                    tint = Color.White,
                    modifier = Modifier.size(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = notification.message,
                color = VivaTextSecondary,
                fontSize = 13.sp,
                lineHeight = 17.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = formatTimestamp(notification.timestamp),
                color = VivaTextTertiary,
                fontSize = 11.sp
            )
        }

        if (!notification.isRead) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(VivaPink)
            )
        }
    }
}
