package com.example.ui.components

import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.data.model.Video
import com.example.ui.theme.VivaBlack
import com.example.ui.theme.VivaCyan
import com.example.ui.theme.VivaPink
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun VivaVideoPlayer(
    video: Video,
    isActive: Boolean,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onDoubleTapLike: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }
    var isVideoReady by remember { mutableStateOf(false) }
    var hasPlaybackError by remember { mutableStateOf(false) }
    var showPlayPauseIndicator by remember { mutableStateOf(false) }
    var heartExplosionOffset by remember { mutableStateOf<Offset?>(null) }
    var heartExplosionTrigger by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }
    var mediaPlayerRef by remember { mutableStateOf<MediaPlayer?>(null) }
    var progress by remember { mutableFloatStateOf(0f) }

    // Ken Burns ambient video motion effect for smooth visual experience
    val infiniteTransition = rememberInfiniteTransition(label = "video_ambient")
    val ambientScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambient_scale"
    )

    // Synchronize play/pause with active page state safely
    LaunchedEffect(isActive, isVideoReady, hasPlaybackError) {
        if (!hasPlaybackError && isVideoReady) {
            try {
                videoViewRef?.let { vv ->
                    if (isActive) {
                        vv.start()
                        isPlaying = true
                    } else {
                        vv.pause()
                        isPlaying = false
                    }
                }
            } catch (e: Exception) {
                Log.w("VivaVideoPlayer", "Safe player sync: ${e.message}")
            }
        }
    }

    // Audio volume sync
    LaunchedEffect(isMuted, mediaPlayerRef) {
        try {
            mediaPlayerRef?.setVolume(if (isMuted) 0f else 1f, if (isMuted) 0f else 1f)
        } catch (_: Exception) {}
    }

    // Smooth playback progress loop (advances natively if MediaPlayer ready, or smoothly simulates if network offline)
    LaunchedEffect(isActive, isPlaying, isVideoReady, hasPlaybackError) {
        while (isActive && isPlaying) {
            if (isVideoReady && !hasPlaybackError && videoViewRef != null) {
                try {
                    val vv = videoViewRef!!
                    val duration = vv.duration
                    if (duration > 0) {
                        progress = vv.currentPosition.toFloat() / duration.toFloat()
                    }
                } catch (_: Exception) {}
            } else {
                // Smooth fallback progress based on durationSeconds (default 15s)
                val totalSteps = (video.durationSeconds * 10).coerceAtLeast(50)
                val stepIncrement = 1f / totalSteps
                progress = (progress + stepIncrement) % 1.0f
            }
            delay(100)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VivaBlack)
            .pointerInput(video.id) {
                detectTapGestures(
                    onTap = {
                        val nextPlaying = !isPlaying
                        isPlaying = nextPlaying
                        if (isVideoReady && !hasPlaybackError) {
                            try {
                                videoViewRef?.let { vv ->
                                    if (nextPlaying) vv.start() else vv.pause()
                                }
                            } catch (_: Exception) {}
                        }
                        showPlayPauseIndicator = true
                        coroutineScope.launch {
                            delay(600)
                            showPlayPauseIndicator = false
                        }
                    },
                    onDoubleTap = { offset ->
                        heartExplosionOffset = offset
                        heartExplosionTrigger = true
                        onDoubleTapLike()
                        coroutineScope.launch {
                            delay(800)
                            heartExplosionTrigger = false
                        }
                    }
                )
            }
            .testTag("video_player_container_${video.id}")
    ) {
        // High-fidelity background visual with ambient Ken Burns motion
        AsyncImage(
            model = video.thumbnailUrl,
            contentDescription = video.caption,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .scale(if (isPlaying && isActive) ambientScale else 1.0f)
                .alpha(if (isVideoReady && !hasPlaybackError) 0f else 1f)
        )

        // Native Android VideoView (only initialized when active and valid)
        val parsedUri = remember(video.videoUrl) { resolveVideoUri(context, video.videoUrl) }
        if (isActive && !hasPlaybackError && parsedUri != null) {
            AndroidView(
                factory = { ctx ->
                    VideoView(ctx).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setOnPreparedListener { mp ->
                            mediaPlayerRef = mp
                            try {
                                mp.isLooping = true
                                mp.setVolume(if (isMuted) 0f else 1f, if (isMuted) 0f else 1f)
                                isVideoReady = true
                                hasPlaybackError = false
                                if (isActive && isPlaying) {
                                    start()
                                }
                            } catch (e: Exception) {
                                Log.w("VivaVideoPlayer", "Prepared listener handled: ${e.message}")
                            }
                        }
                        setOnErrorListener { _, what, extra ->
                            // Consume error to suppress Android's modal dialog
                            Log.w("VivaVideoPlayer", "Suppressed MediaPlayer error ($what, $extra), switching to ambient player")
                            isVideoReady = false
                            hasPlaybackError = true
                            try {
                                stopPlayback()
                            } catch (_: Exception) {}
                            true
                        }
                        try {
                            setVideoURI(parsedUri)
                        } catch (e: Exception) {
                            hasPlaybackError = true
                        }
                        videoViewRef = this
                    }
                },
                update = { vv ->
                    videoViewRef = vv
                },
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(if (isVideoReady && !hasPlaybackError) 1f else 0f)
            )
        }

        // Top gradient scrim for status bar readability
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                    )
                )
        )

        // Bottom gradient scrim for video info and controls readability
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
        )

        // Mute / Unmute Quick Button (Top End)
        IconButton(
            onClick = onToggleMute,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 16.dp)
                .size(40.dp)
                .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                .testTag("mute_toggle_button")
        ) {
            Icon(
                imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                contentDescription = if (isMuted) "Unmute audio" else "Mute audio",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        // Ambient Sound Wave indicator when playing
        if (isActive && isPlaying) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 54.dp, start = 16.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = VivaCyan,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "VIVA HD",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Play / Pause central indicator overlay
        AnimatedVisibility(
            visible = showPlayPauseIndicator && !isPlaying,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Paused",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        // Double-Tap Neon Heart Animation
        if (heartExplosionTrigger && heartExplosionOffset != null) {
            val scale by animateFloatAsState(
                targetValue = if (heartExplosionTrigger) 1.4f else 0.4f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                label = "heart_scale"
            )
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Liked",
                tint = VivaPink,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (heartExplosionOffset!!.x - 40.dp.toPx()).roundToInt(),
                            (heartExplosionOffset!!.y - 40.dp.toPx()).roundToInt()
                        )
                    }
                    .size(80.dp)
                    .scale(scale)
            )
        }

        // Video playback progress bar along the very bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(3.dp)
                .background(Color.White.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .background(VivaPink)
            )
        }
    }
}

fun resolveVideoUri(context: android.content.Context, rawUriString: String): Uri? {
    if (rawUriString.isBlank()) return null
    return try {
        if (rawUriString.startsWith("raw://")) {
            val resourceName = rawUriString.removePrefix("raw://")
            val resId = context.resources.getIdentifier(resourceName, "raw", context.packageName)
            if (resId != 0) {
                Uri.parse("android.resource://${context.packageName}/$resId")
            } else null
        } else if (rawUriString.contains("viva_clip_")) {
            val resourceName = rawUriString.substringAfterLast("/").substringBeforeLast(".")
            val resId = context.resources.getIdentifier(resourceName, "raw", context.packageName)
            if (resId != 0) {
                Uri.parse("android.resource://${context.packageName}/$resId")
            } else Uri.parse(rawUriString)
        } else {
            Uri.parse(rawUriString)
        }
    } catch (_: Exception) {
        null
    }
}
