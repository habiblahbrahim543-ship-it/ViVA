package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Video
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.FeedType
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.VivaViewModel

@Composable
fun FeedScreen(
    viewModel: VivaViewModel,
    modifier: Modifier = Modifier
) {
    val feedType by viewModel.feedType.collectAsStateWithLifecycle()
    val forYouVideos by viewModel.forYouVideos.collectAsStateWithLifecycle()
    val followingVideos by viewModel.followingVideos.collectAsStateWithLifecycle()
    val isMuted by viewModel.isGlobalMuted.collectAsStateWithLifecycle()

    val currentVideoList = if (feedType == FeedType.FOR_YOU) forYouVideos else followingVideos

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VivaBlack)
            .testTag("feed_screen")
    ) {
        if (currentVideoList.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Whatshot,
                        contentDescription = "Feed empty",
                        tint = VivaPink,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (feedType == FeedType.FOLLOWING) "No videos from followed creators" else "No videos yet",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (feedType == FeedType.FOLLOWING) "Follow some creators on VIVA to see their latest videos here!" else "Be the first creator to upload a video!",
                        color = VivaTextSecondary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            if (feedType == FeedType.FOLLOWING) {
                                viewModel.setFeedType(FeedType.FOR_YOU)
                            } else {
                                viewModel.setMainTab(MainTab.CREATE)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VivaPink),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (feedType == FeedType.FOLLOWING) "Explore For You" else "Create Video",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            val pagerState = rememberPagerState(pageCount = { currentVideoList.size })

            VerticalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("vertical_video_pager")
            ) { page ->
                val video = currentVideoList.getOrNull(page)
                if (video != null) {
                    val isActive = pagerState.currentPage == page
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Full Screen Video Player
                        VivaVideoPlayer(
                            video = video,
                            isActive = isActive,
                            isMuted = isMuted,
                            onToggleMute = { viewModel.toggleGlobalMute() },
                            onDoubleTapLike = {
                                if (!video.isLiked) {
                                    viewModel.toggleLike(video)
                                }
                            }
                        )

                        // Right Vertical Action Sidebar
                        VideoSidebar(
                            video = video,
                            onAvatarClick = { viewModel.openCreatorProfile(video.creatorId) },
                            onFollowClick = { viewModel.toggleFollowCreator(video.creatorId, video.isFollowingCreator) },
                            onLikeClick = { viewModel.toggleLike(video) },
                            onCommentClick = { viewModel.openComments(video.id) },
                            onSaveClick = { viewModel.toggleSave(video) },
                            onShareClick = { viewModel.openShare(video) },
                            onSoundClick = { viewModel.openSound(video.soundId) },
                            modifier = Modifier.align(Alignment.BottomEnd)
                        )

                        // Bottom Info & Clickable Metadata
                        VideoBottomInfo(
                            video = video,
                            onUsernameClick = { viewModel.openCreatorProfile(video.creatorId) },
                            onHashtagClick = { tag -> viewModel.openHashtag(tag) },
                            onSoundClick = { viewModel.openSound(video.soundId) },
                            modifier = Modifier.align(Alignment.BottomStart)
                        )
                    }
                }
            }
        }

        // Top Navigation Bar (Following | For You tabs & Search action)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Live / Sound icon
            Icon(
                imageVector = Icons.Default.Whatshot,
                contentDescription = "Trending",
                tint = VivaPink,
                modifier = Modifier.size(26.dp)
            )

            // Feed Tabs
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Following Tab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { viewModel.setFeedType(FeedType.FOLLOWING) }
                        .padding(horizontal = 4.dp)
                        .testTag("feed_following_tab")
                ) {
                    Text(
                        text = "Following",
                        fontSize = 17.sp,
                        fontWeight = if (feedType == FeedType.FOLLOWING) FontWeight.Bold else FontWeight.Normal,
                        color = if (feedType == FeedType.FOLLOWING) Color.White else Color.White.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .height(3.dp)
                            .background(if (feedType == FeedType.FOLLOWING) Color.White else Color.Transparent, RoundedCornerShape(2.dp))
                    )
                }

                // For You Tab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { viewModel.setFeedType(FeedType.FOR_YOU) }
                        .padding(horizontal = 4.dp)
                        .testTag("feed_for_you_tab")
                ) {
                    Text(
                        text = "For You",
                        fontSize = 17.sp,
                        fontWeight = if (feedType == FeedType.FOR_YOU) FontWeight.Bold else FontWeight.Normal,
                        color = if (feedType == FeedType.FOR_YOU) Color.White else Color.White.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .height(3.dp)
                            .background(if (feedType == FeedType.FOR_YOU) Color.White else Color.Transparent, RoundedCornerShape(2.dp))
                    )
                }
            }

            // Search Icon (takes to Discover tab)
            IconButton(
                onClick = { viewModel.setMainTab(MainTab.DISCOVER) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search VIVA",
                    tint = Color.White
                )
            }
        }
    }
}
