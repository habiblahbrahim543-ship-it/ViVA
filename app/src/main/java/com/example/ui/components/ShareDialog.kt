package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Video
import com.example.ui.theme.*
import com.example.ui.viewmodel.ActiveReportTarget
import com.example.ui.viewmodel.VivaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareDialog(
    video: Video,
    viewModel: VivaViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val videoShareUrl = "https://viva.social/video/${video.id}"

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = VivaSurfaceDark,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = VivaBorder)
        },
        modifier = Modifier.testTag("share_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = "Share to",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Primary Share Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ShareActionItem(
                    icon = Icons.Default.Share,
                    label = "System Share",
                    backgroundColor = VivaPink,
                    onClick = {
                        viewModel.shareVideo(video)
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "Watch @${video.creatorUsername} on VIVA: $videoShareUrl\n\n${video.caption}")
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Share VIVA Video")
                        context.startActivity(shareIntent)
                        onDismiss()
                    }
                )

                ShareActionItem(
                    icon = Icons.Default.ContentCopy,
                    label = "Copy Link",
                    backgroundColor = VivaCyan,
                    onClick = {
                        viewModel.shareVideo(video)
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("VIVA Video Link", videoShareUrl)
                        clipboard.setPrimaryClip(clip)
                        viewModel.showToast("Link copied to clipboard!")
                        onDismiss()
                    }
                )

                ShareActionItem(
                    icon = Icons.Default.Bookmark,
                    label = if (video.isSaved) "Saved" else "Save Video",
                    backgroundColor = VivaSurfaceVariant,
                    onClick = {
                        viewModel.toggleSave(video)
                        onDismiss()
                    }
                )

                ShareActionItem(
                    icon = Icons.Default.Flag,
                    label = "Report",
                    backgroundColor = VivaRed,
                    onClick = {
                        onDismiss()
                        viewModel.openReport(
                            ActiveReportTarget(
                                type = "VIDEO",
                                id = video.id,
                                title = "Video by @${video.creatorUsername}"
                            )
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // URL preview box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(VivaSurfaceVariant)
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = videoShareUrl,
                        color = VivaTextSecondary,
                        fontSize = 13.sp,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "VIVA Video",
                        color = VivaCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ShareActionItem(
    icon: ImageVector,
    label: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            color = VivaTextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
