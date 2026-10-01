package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Video
import com.example.ui.theme.*

@Composable
fun VideoSidebar(
    video: Video,
    onAvatarClick: () -> Unit,
    onFollowClick: () -> Unit,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
    onSoundClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Rotating vinyl sound animation
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_rotate")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "vinyl_angle"
    )

    // Animated heart bounce
    val heartScale by animateFloatAsState(
        targetValue = if (video.isLiked) 1.25f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "heart_bounce"
    )

    Column(
        modifier = modifier
            .padding(end = 12.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Creator Avatar with Follow Badge
        Box(
            modifier = Modifier.padding(bottom = 6.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            AsyncImage(
                model = video.creatorAvatarUrl,
                contentDescription = "${video.creatorUsername} profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape)
                    .clickable { onAvatarClick() }
                    .testTag("creator_avatar_${video.creatorId}")
            )

            if (!video.isFollowingCreator && video.creatorId != "user_me") {
                Box(
                    modifier = Modifier
                        .offset(y = 10.dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(VivaPink)
                        .clickable { onFollowClick() }
                        .testTag("follow_creator_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Follow creator",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Like Button
        SidebarActionButton(
            icon = if (video.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            label = formatCount(video.likesCount),
            iconTint = if (video.isLiked) VivaPink else Color.White,
            scale = heartScale,
            testTag = "like_video_button",
            onClick = onLikeClick
        )

        // Comment Button
        SidebarActionButton(
            icon = Icons.Outlined.ChatBubbleOutline,
            label = formatCount(video.commentsCount),
            iconTint = Color.White,
            testTag = "comments_button",
            onClick = onCommentClick
        )

        // Save / Bookmark Button
        SidebarActionButton(
            icon = if (video.isSaved) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
            label = formatCount(video.savesCount),
            iconTint = if (video.isSaved) VivaYellow else Color.White,
            testTag = "save_video_button",
            onClick = onSaveClick
        )

        // Share Button
        SidebarActionButton(
            icon = Icons.Outlined.Share,
            label = formatCount(video.sharesCount),
            iconTint = Color.White,
            testTag = "share_video_button",
            onClick = onShareClick
        )

        // Vinyl Disc Audio Track
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(46.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(VivaSurfaceVariant, VivaBlack, VivaBlack)
                    )
                )
                .border(2.dp, VivaBorder, CircleShape)
                .rotate(rotationAngle)
                .clickable { onSoundClick() }
                .testTag("sound_vinyl_disc"),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = video.creatorAvatarUrl,
                contentDescription = "Sound track",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
private fun SidebarActionButton(
    icon: ImageVector,
    label: String,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    scale: Float = 1.0f,
    testTag: String = ""
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable { onClick() }
            .testTag(testTag)
            .padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconTint,
            modifier = Modifier
                .size(36.dp)
                .scale(scale)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

fun formatCount(count: Long): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}
