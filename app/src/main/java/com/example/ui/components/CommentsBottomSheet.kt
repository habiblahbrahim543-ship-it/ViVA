package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Send
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
import com.example.data.model.Comment
import com.example.ui.theme.*
import com.example.ui.viewmodel.ActiveReportTarget
import com.example.ui.viewmodel.VivaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
    videoId: String,
    viewModel: VivaViewModel,
    onDismiss: () -> Unit
) {
    val comments by viewModel.getCommentsForVideo(videoId).collectAsStateWithLifecycle(emptyList())
    var commentInput by remember { mutableStateOf("") }
    var replyingToComment by remember { mutableStateOf<Comment?>(null) }
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = VivaSurfaceDark,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = VivaBorder)
        },
        modifier = Modifier.testTag("comments_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.72f)
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${comments.size} comments",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close comments",
                        tint = VivaTextSecondary
                    )
                }
            }

            Divider(color = VivaBorder, thickness = 0.5.dp)

            // Comments List
            if (comments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No comments yet",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Be the first to share what you think!",
                            color = VivaTextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    items(comments, key = { it.id }) { comment ->
                        CommentItemRow(
                            comment = comment,
                            currentUserId = currentUser?.id ?: "user_me",
                            onLike = { viewModel.toggleCommentLike(comment) },
                            onReply = { replyingToComment = comment },
                            onDelete = { viewModel.deleteComment(comment.id) },
                            onReport = {
                                viewModel.openReport(
                                    ActiveReportTarget(
                                        type = "COMMENT",
                                        id = comment.id,
                                        title = "Comment by @${comment.username}"
                                    )
                                )
                            },
                            viewModel = viewModel
                        )
                    }
                }
            }

            // Replying To Banner
            if (replyingToComment != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VivaSurfaceVariant)
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Replying to @${replyingToComment?.username}",
                        color = VivaCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    IconButton(
                        onClick = { replyingToComment = null },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel reply",
                            tint = VivaTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Bottom Input Bar
            Divider(color = VivaBorder, thickness = 0.5.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
                    contentDescription = "My avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                TextField(
                    value = commentInput,
                    onValueChange = { commentInput = it },
                    placeholder = {
                        Text(
                            text = if (replyingToComment != null) "Reply to @${replyingToComment?.username}..." else "Add comment...",
                            color = VivaTextTertiary,
                            fontSize = 14.sp
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = VivaSurfaceVariant,
                        unfocusedContainerColor = VivaSurfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 44.dp, max = 100.dp)
                        .testTag("comment_input_field")
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (commentInput.isNotBlank()) {
                            viewModel.addComment(
                                videoId = videoId,
                                text = commentInput,
                                parentId = replyingToComment?.id
                            )
                            commentInput = ""
                            replyingToComment = null
                        }
                    },
                    enabled = commentInput.isNotBlank(),
                    modifier = Modifier
                        .size(40.dp)
                        .background(if (commentInput.isNotBlank()) VivaPink else VivaSurfaceVariant, CircleShape)
                        .testTag("send_comment_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send comment",
                        tint = if (commentInput.isNotBlank()) Color.White else VivaTextTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CommentItemRow(
    comment: Comment,
    currentUserId: String,
    onLike: () -> Unit,
    onReply: () -> Unit,
    onDelete: () -> Unit,
    onReport: () -> Unit,
    viewModel: VivaViewModel
) {
    val replies by viewModel.getRepliesForComment(comment.id).collectAsStateWithLifecycle(emptyList())
    var showReplies by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = comment.userAvatar,
                contentDescription = "${comment.username} avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = comment.username,
                        color = VivaTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    VivaVerifiedBadge(size = 13.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formatTimestamp(comment.timestamp),
                        color = VivaTextTertiary,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = comment.text,
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Reply",
                        color = VivaTextTertiary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onReply() }
                    )

                    if (comment.userId == currentUserId) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete comment",
                            tint = VivaTextTertiary,
                            modifier = Modifier
                                .size(14.dp)
                                .clickable { onDelete() }
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = "Report comment",
                            tint = VivaTextTertiary,
                            modifier = Modifier
                                .size(14.dp)
                                .clickable { onReport() }
                        )
                    }
                }
            }

            // Comment Like Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable { onLike() }
            ) {
                Icon(
                    imageVector = if (comment.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like comment",
                    tint = if (comment.isLiked) VivaPink else VivaTextTertiary,
                    modifier = Modifier.size(16.dp)
                )
                if (comment.likesCount > 0) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = comment.likesCount.toString(),
                        color = VivaTextTertiary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Nested Replies Section
        if (replies.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .padding(start = 48.dp)
                    .clickable { showReplies = !showReplies },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height(1.dp)
                        .background(VivaTextTertiary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (showReplies) "Hide replies" else "View ${replies.size} replies",
                    color = VivaCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (showReplies) {
                Column(
                    modifier = Modifier
                        .padding(start = 48.dp, top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    replies.forEach { reply ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            AsyncImage(
                                model = reply.userAvatar,
                                contentDescription = "${reply.username} avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = reply.username,
                                    color = VivaTextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = reply.text,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "just now"
        minutes < 60 -> "${minutes}m"
        hours < 24 -> "${hours}h"
        days < 7 -> "${days}d"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
    }
}
