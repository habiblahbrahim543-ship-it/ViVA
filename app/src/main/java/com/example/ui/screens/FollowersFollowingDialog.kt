package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.components.VivaVerifiedBadge
import com.example.ui.components.formatCount
import com.example.ui.theme.*
import com.example.ui.viewmodel.VivaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowersFollowingDialog(
    userId: String,
    isFollowers: Boolean,
    viewModel: VivaViewModel,
    onDismiss: () -> Unit
) {
    val usersList by (if (isFollowers) viewModel.getFollowersForUser(userId) else viewModel.getFollowingForUser(userId))
        .collectAsStateWithLifecycle(emptyList())

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = VivaSurfaceDark,
        dragHandle = { BottomSheetDefaults.DragHandle(color = VivaBorder) },
        modifier = Modifier.testTag("followers_following_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isFollowers) "Followers (${usersList.size})" else "Following (${usersList.size})",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = VivaTextSecondary)
                }
            }

            Divider(color = VivaBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))

            if (usersList.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isFollowers) "No followers yet" else "Not following anyone yet",
                        color = VivaTextSecondary,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(usersList, key = { it.id }) { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(VivaSurfaceVariant)
                                .clickable {
                                    onDismiss()
                                    viewModel.openCreatorProfile(user.id)
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = user.avatarUrl,
                                contentDescription = user.displayName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
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
                                    text = "@${user.username} • ${formatCount(user.followersCount.toLong())} followers",
                                    color = VivaTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                            if (!user.isCurrentUser) {
                                Button(
                                    onClick = { viewModel.toggleFollowCreator(user.id, user.isFollowing) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (user.isFollowing) VivaSurfaceElevated else VivaPink
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text(
                                        text = if (user.isFollowing) "Following" else "Follow",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
