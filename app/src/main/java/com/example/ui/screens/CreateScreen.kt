package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.VivaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(
    viewModel: VivaViewModel,
    modifier: Modifier = Modifier
) {
    var selectedVideoUri by remember { mutableStateOf<String>("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4") }
    var selectedThumbnailUri by remember { mutableStateOf("https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80") }
    var caption by remember { mutableStateOf("") }
    var soundTitle by remember { mutableStateOf("VIVA Pulse - Original Beat") }
    var soundCreator by remember { mutableStateOf("VIVA Sound Studio") }
    var category by remember { mutableStateOf("Trending") }
    var allowComments by remember { mutableStateOf(true) }
    var allowDownloads by remember { mutableStateOf(true) }
    var audience by remember { mutableStateOf("Public") } // Public, Followers, Private

    val uploadProgress by viewModel.uploadProgress.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    // File Picker Launcher for Videos
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedVideoUri = it.toString()
            viewModel.showToast("Video selected from device!")
        }
    }

    // Photo/Thumbnail Picker Launcher
    val coverPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedThumbnailUri = it.toString()
            viewModel.showToast("Cover thumbnail updated!")
        }
    }

    val sampleTemplates = listOf(
        Triple("Big Buck Bunny", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800&auto=format&fit=crop&q=80"),
        Triple("Tears of Steel", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4", "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?w=800&auto=format&fit=crop&q=80"),
        Triple("For Bigger Blazes", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4", "https://images.unsplash.com/photo-1547153760-18fc86324498?w=800&auto=format&fit=crop&q=80"),
        Triple("For Bigger Escapes", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4", "https://images.unsplash.com/photo-1544025162-d76694265947?w=800&auto=format&fit=crop&q=80")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VivaBlack)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("create_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Create Video",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            IconButton(onClick = { viewModel.setMainTab(MainTab.HOME) }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel create",
                    tint = VivaTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Video Preview & Media Source Card
        Card(
            colors = CardDefaults.cardColors(containerColor = VivaSurfaceDark),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(VivaSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = selectedThumbnailUri,
                        contentDescription = "Video Preview Cover",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Preview Play",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Cover selector badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.7f))
                            .clickable { coverPickerLauncher.launch("image/*") }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Change Cover",
                            color = VivaCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Media Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { videoPickerLauncher.launch("video/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = VivaSurfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = "Upload",
                            tint = VivaPink,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Choose Device Video", color = Color.White, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Or choose a high-res video clip:",
                    color = VivaTextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    sampleTemplates.forEach { (name, videoUrl, thumbUrl) ->
                        val isSelected = selectedVideoUri == videoUrl
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) VivaPink else VivaSurfaceVariant)
                                .clickable {
                                    selectedVideoUri = videoUrl
                                    selectedThumbnailUri = thumbUrl
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = name.take(10),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Caption Input Field
        Text(
            text = "Caption & Hashtags",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = caption,
            onValueChange = { caption = it },
            placeholder = { Text("Describe your video... add #tags to reach audiences") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VivaPink,
                unfocusedBorderColor = VivaBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .testTag("caption_input_field")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Hashtags Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("#viva", "#dance", "#trending", "#viral", "#creators").forEach { tag ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(VivaSurfaceDark)
                        .border(1.dp, VivaBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            if (!caption.contains(tag)) {
                                caption = if (caption.isBlank()) tag else "$caption $tag"
                            }
                        }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(text = tag, color = VivaCyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sound Selection Row
        Card(
            colors = CardDefaults.cardColors(containerColor = VivaSurfaceDark),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Sound track",
                        tint = VivaPink,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = soundTitle, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = soundCreator, color = VivaTextSecondary, fontSize = 12.sp)
                    }
                }
                TextButton(onClick = {
                    soundTitle = "VIVA Original Beats #2"
                    soundCreator = "VIVA Creator Studio"
                }) {
                    Text(text = "Change", color = VivaCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Privacy and Settings Options
        Card(
            colors = CardDefaults.cardColors(containerColor = VivaSurfaceDark),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Audience selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Who can watch", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Public", "Followers", "Private").forEach { opt ->
                            val isSel = audience == opt
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) VivaPink else VivaSurfaceVariant)
                                    .clickable { audience = opt }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = opt,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Divider(color = VivaBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                // Allow Comments switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Allow comments", color = Color.White, fontSize = 14.sp)
                    Switch(
                        checked = allowComments,
                        onCheckedChange = { allowComments = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = VivaPink)
                    )
                }

                // Allow Downloads switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Allow video downloads", color = Color.White, fontSize = 14.sp)
                    Switch(
                        checked = allowDownloads,
                        onCheckedChange = { allowDownloads = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = VivaPink)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Upload progress bar if publishing
        if (uploadProgress != null) {
            Column(modifier = Modifier.fillMaxWidth()) {
                LinearProgressIndicator(
                    progress = { uploadProgress!! },
                    color = VivaPink,
                    trackColor = VivaSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Publishing to VIVA cloud network... ${(uploadProgress!! * 100).toInt()}%",
                    color = VivaTextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Publish Button
        Button(
            onClick = {
                viewModel.publishNewVideo(
                    caption = caption.ifBlank { "Check out my new video on VIVA! #viva" },
                    hashtags = caption.split(" ").filter { it.startsWith("#") }.joinToString(" "),
                    videoUri = selectedVideoUri,
                    thumbnailUri = selectedThumbnailUri,
                    soundTitle = soundTitle,
                    soundCreator = soundCreator,
                    category = category,
                    allowComments = allowComments,
                    allowDownloads = allowDownloads,
                    isPrivate = audience == "Private",
                    onSuccess = {
                        // handled inside viewModel
                    }
                )
            },
            enabled = uploadProgress == null,
            colors = ButtonDefaults.buttonColors(containerColor = VivaPink),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("publish_video_button")
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Publish",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Post Video to VIVA",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(60.dp))
    }
}
