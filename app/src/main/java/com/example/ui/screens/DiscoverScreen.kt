package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.Hashtag
import com.example.data.model.Sound
import com.example.data.model.User
import com.example.data.model.Video
import com.example.ui.components.VivaVerifiedBadge
import com.example.ui.components.formatCount
import com.example.ui.theme.*
import com.example.ui.viewmodel.SearchCategory
import com.example.ui.viewmodel.VivaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    viewModel: VivaViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchCategory by viewModel.searchCategory.collectAsStateWithLifecycle()
    val allVideos by viewModel.forYouVideos.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val trendingHashtags by viewModel.trendingHashtags.collectAsStateWithLifecycle()
    val allSounds by viewModel.allSounds.collectAsStateWithLifecycle()

    val filteredVideos = remember(allVideos, searchQuery) {
        if (searchQuery.isBlank()) allVideos
        else allVideos.filter {
            it.caption.contains(searchQuery, ignoreCase = true) ||
            it.hashtags.contains(searchQuery, ignoreCase = true) ||
            it.creatorUsername.contains(searchQuery, ignoreCase = true)
        }
    }

    val filteredUsers = remember(allUsers, searchQuery) {
        if (searchQuery.isBlank()) allUsers
        else allUsers.filter {
            it.username.contains(searchQuery, ignoreCase = true) ||
            it.displayName.contains(searchQuery, ignoreCase = true)
        }
    }

    val filteredHashtags = remember(trendingHashtags, searchQuery) {
        if (searchQuery.isBlank()) trendingHashtags
        else trendingHashtags.filter { it.tag.contains(searchQuery.removePrefix("#"), ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VivaBlack)
            .statusBarsPadding()
            .testTag("discover_screen")
    ) {
        // Search Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = {
                    Text(text = "Search users, videos, sounds...", color = VivaTextTertiary, fontSize = 14.sp)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = VivaTextSecondary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = VivaTextSecondary
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = VivaSurfaceDark,
                    unfocusedContainerColor = VivaSurfaceDark,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("search_input_field")
            )
        }

        // Category Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val categories = SearchCategory.values()
            items(categories) { category ->
                val isSelected = searchCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setSearchCategory(category) },
                    label = {
                        Text(
                            text = category.name.lowercase().replaceFirstChar { it.uppercase() },
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

        Divider(color = VivaBorder, thickness = 0.5.dp)

        if (searchQuery.isNotBlank()) {
            // Search Results Content based on selected Category
            when (searchCategory) {
                SearchCategory.USERS -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        items(filteredUsers, key = { it.id }) { user ->
                            UserSearchRow(user = user, onUserClick = { viewModel.openCreatorProfile(user.id) }, viewModel = viewModel)
                        }
                    }
                }
                SearchCategory.HASHTAGS -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        items(filteredHashtags, key = { it.tag }) { tag ->
                            HashtagRow(hashtag = tag, onClick = { viewModel.openHashtag(tag.tag) })
                        }
                    }
                }
                SearchCategory.SOUNDS -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        items(allSounds, key = { it.id }) { sound ->
                            SoundRow(sound = sound, onClick = { viewModel.openSound(sound.id) })
                        }
                    }
                }
                else -> {
                    // Videos & Top Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(filteredVideos, key = { it.id }) { video ->
                            VideoGridThumbnail(
                                video = video,
                                onClick = { viewModel.openCreatorProfile(video.creatorId) }
                            )
                        }
                    }
                }
            }
        } else {
            // Discovery Feed (Explore, Trending Tags, Popular Creators)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 70.dp)
            ) {
                // Featured Spotlight Banner
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(130.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(VivaGradientStart, VivaGradientEnd)
                                )
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "VIVA TRENDING CHALLENGE",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "#VivaFutureBeat 🎵",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Create with original audio & win creator badges",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Trending Hashtags
                item {
                    Text(
                        text = "Trending Hashtags",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(trendingHashtags, key = { it.tag }) { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(VivaSurfaceDark)
                                    .border(1.dp, VivaBorder, RoundedCornerShape(12.dp))
                                    .clickable { viewModel.openHashtag(tag.tag) }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "#${tag.tag}",
                                        color = VivaCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${formatCount(tag.viewsCount)} views",
                                        color = VivaTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Popular Creators
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Popular Creators",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(allUsers.filter { !it.isCurrentUser }, key = { it.id }) { creator ->
                            CreatorCard(
                                user = creator,
                                onProfileClick = { viewModel.openCreatorProfile(creator.id) },
                                onFollowClick = { viewModel.toggleFollowCreator(creator.id, creator.isFollowing) }
                            )
                        }
                    }
                }

                // Explore Video Grid Header
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Explore Videos",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Video Grid items inside column
                item {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(480.dp)
                            .padding(horizontal = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        userScrollEnabled = false
                    ) {
                        items(allVideos.take(6), key = { it.id }) { video ->
                            VideoGridThumbnail(
                                video = video,
                                onClick = { viewModel.openCreatorProfile(video.creatorId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VideoGridThumbnail(
    video: Video,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(0.75f)
            .background(VivaSurfaceDark)
            .clickable { onClick() }
            .testTag("video_grid_thumb_${video.id}")
    ) {
        AsyncImage(
            model = video.thumbnailUrl,
            contentDescription = video.caption,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Bottom view count gradient overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                    )
                )
                .padding(horizontal = 6.dp, vertical = 4.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Views",
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = formatCount(video.viewsCount),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun CreatorCard(
    user: User,
    onProfileClick: () -> Unit,
    onFollowClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(130.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(VivaSurfaceDark)
            .border(1.dp, VivaBorder, RoundedCornerShape(14.dp))
            .clickable { onProfileClick() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = user.displayName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = user.displayName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1
            )
            if (user.isVerified) {
                Spacer(modifier = Modifier.width(4.dp))
                VivaVerifiedBadge(size = 12.dp)
            }
        }
        Text(
            text = "@${user.username}",
            color = VivaTextSecondary,
            fontSize = 11.sp,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "${formatCount(user.followersCount.toLong())} followers",
            color = VivaTextTertiary,
            fontSize = 10.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onFollowClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (user.isFollowing) VivaSurfaceVariant else VivaPink
            ),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            modifier = Modifier.height(30.dp)
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

@Composable
fun UserSearchRow(
    user: User,
    onUserClick: () -> Unit,
    viewModel: VivaViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VivaSurfaceDark)
            .clickable { onUserClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = user.displayName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(46.dp)
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
                    containerColor = if (user.isFollowing) VivaSurfaceVariant else VivaPink
                ),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = if (user.isFollowing) "Following" else "Follow",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun HashtagRow(
    hashtag: Hashtag,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VivaSurfaceDark)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(VivaSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "#",
                color = VivaCyan,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "#${hashtag.tag}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                text = "${formatCount(hashtag.viewsCount)} views • ${formatCount(hashtag.videoCount)} videos",
                color = VivaTextSecondary,
                fontSize = 12.sp
            )
        }
        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = "View hashtag",
            tint = VivaTextTertiary,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
fun SoundRow(
    sound: Sound,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VivaSurfaceDark)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(VivaSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.GraphicEq,
                contentDescription = "Sound",
                tint = VivaPink,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = sound.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1
            )
            Text(
                text = "${sound.creator} • ${formatCount(sound.videoCount.toLong())} videos",
                color = VivaTextSecondary,
                fontSize = 12.sp
            )
        }
        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = "View sound",
            tint = VivaTextTertiary,
            modifier = Modifier.size(14.dp)
        )
    }
}
