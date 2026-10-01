package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Video
import com.example.ui.theme.VivaCyan

@Composable
fun VideoBottomInfo(
    video: Video,
    onUsernameClick: () -> Unit,
    onHashtagClick: (String) -> Unit,
    onSoundClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 84.dp, bottom = 18.dp)
    ) {
        // Creator Username Row with Verified Badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onUsernameClick() }
                .testTag("creator_username_row")
        ) {
            Text(
                text = "@${video.creatorUsername}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            VivaVerifiedBadge(size = 15.dp)
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Caption with clickable hashtags
        val annotatedCaption = buildAnnotatedString {
            val words = video.caption.split(" ")
            words.forEachIndexed { index, word ->
                if (word.startsWith("#")) {
                    pushStringAnnotation(tag = "HASHTAG", annotation = word)
                    withStyle(style = SpanStyle(color = VivaCyan, fontWeight = FontWeight.Bold)) {
                        append(word)
                    }
                    pop()
                } else {
                    withStyle(style = SpanStyle(color = Color.White.copy(alpha = 0.95f))) {
                        append(word)
                    }
                }
                if (index < words.size - 1) append(" ")
            }
        }

        Text(
            text = annotatedCaption,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            maxLines = if (isExpanded) 10 else 2,
            modifier = Modifier
                .clickable { isExpanded = !isExpanded }
                .testTag("video_caption_text")
        )

        // Sound / Audio Row
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onSoundClick() }
                .testTag("sound_info_row")
        ) {
            Icon(
                imageVector = Icons.Default.GraphicEq,
                contentDescription = "Audio track",
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${video.soundTitle} • ${video.soundCreator}",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}
