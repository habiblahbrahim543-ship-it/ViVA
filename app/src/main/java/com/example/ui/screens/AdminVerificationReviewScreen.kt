package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.data.model.VerificationRequest
import com.example.ui.components.VivaVerifiedBadge
import com.example.ui.components.formatTimestamp
import com.example.ui.theme.*
import com.example.ui.viewmodel.VivaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVerificationReviewScreen(
    request: VerificationRequest,
    viewModel: VivaViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val targetUser = remember(request.userId, allUsers) { allUsers.find { it.id == request.userId } }

    var showRejectDialog by remember { mutableStateOf(false) }
    var rejectionReasonInput by remember { mutableStateOf("") }

    var showMoreInfoDialog by remember { mutableStateOf(false) }
    var moreInfoNotesInput by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VivaBlack)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .testTag("admin_verification_review_screen")
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "Verification Request Review",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Creator Identity Header Card
        Card(
            colors = CardDefaults.cardColors(containerColor = VivaSurfaceDark),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = targetUser?.avatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
                    contentDescription = targetUser?.displayName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = request.fullName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        if (targetUser?.isVerified == true) {
                            Spacer(modifier = Modifier.width(6.dp))
                            VivaVerifiedBadge()
                        }
                    }
                    Text(
                        text = "@${request.username} • User ID: ${request.userId}",
                        color = VivaTextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(VivaPink.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Category: ${request.category} • Country: ${request.country}",
                            color = VivaPink,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Detailed Application Data Card
        Card(
            colors = CardDefaults.cardColors(containerColor = VivaSurfaceDark),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ReviewSectionItem(title = "Reason for Verification", content = request.reason)

                Divider(color = VivaBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                if (request.website.isNotBlank()) {
                    ReviewSectionItem(title = "Official Website", content = request.website)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (request.instagram.isNotBlank() || request.youtube.isNotBlank() || request.tiktok.isNotBlank()) {
                    Text(text = "Social Media Profiles:", color = VivaTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    if (request.instagram.isNotBlank()) Text(text = "• Instagram: ${request.instagram}", color = Color.White, fontSize = 13.sp)
                    if (request.youtube.isNotBlank()) Text(text = "• YouTube: ${request.youtube}", color = Color.White, fontSize = 13.sp)
                    if (request.tiktok.isNotBlank()) Text(text = "• TikTok: ${request.tiktok}", color = Color.White, fontSize = 13.sp)
                    Divider(color = VivaBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))
                }

                if (request.supportingDocuments.isNotBlank()) {
                    ReviewSectionItem(title = "Supporting Documents / Identity Verification", content = request.supportingDocuments)
                    Divider(color = VivaBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))
                }

                if (request.userAdditionalInfo.isNotBlank()) {
                    ReviewSectionItem(title = "User Additional Details (Re-submission)", content = request.userAdditionalInfo)
                    Divider(color = VivaBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))
                }

                // Audit Trail
                Text(text = "Audit Trail", color = VivaCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "• Submitted on: ${formatTimestamp(request.createdAt)}", color = VivaTextTertiary, fontSize = 11.sp)
                if (request.reviewedAt != null) {
                    Text(text = "• Last reviewed: ${formatTimestamp(request.reviewedAt)} by @${request.reviewedBy}", color = VivaTextTertiary, fontSize = 11.sp)
                }
                Text(text = "• Current status: ${request.status.uppercase()}", color = VivaTextTertiary, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons Row (Approve / Reject / Request More Info)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Approve Button
            Button(
                onClick = {
                    viewModel.approveVerification(request.id, request.userId)
                },
                colors = ButtonDefaults.buttonColors(containerColor = VivaCyan),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("admin_approve_verification_button")
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Approve Verification (Grant Badge ✓)",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            // Request More Info Button
            OutlinedButton(
                onClick = { showMoreInfoDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = VivaYellow),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("admin_request_more_info_button")
            ) {
                Icon(imageVector = Icons.Default.HelpOutline, contentDescription = null, tint = VivaYellow)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Request More Information", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            // Reject Button
            Button(
                onClick = { showRejectDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = VivaRed),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("admin_reject_verification_button")
            ) {
                Icon(imageVector = Icons.Default.Cancel, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Reject Verification", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    // Reject Dialog
    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text(text = "Reject Verification Request", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Specify an optional reason for the rejection (will be sent in notification):",
                        color = VivaTextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rejectionReasonInput,
                        onValueChange = { rejectionReasonInput = it },
                        placeholder = { Text("e.g. Insufficient public notability evidence or inauthentic documents") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VivaRed,
                            unfocusedBorderColor = VivaBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().height(80.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.rejectVerification(request.id, request.userId, rejectionReasonInput)
                        showRejectDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VivaRed)
                ) {
                    Text("Confirm Rejection", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = false }) {
                    Text("Cancel", color = VivaTextSecondary)
                }
            },
            containerColor = VivaSurfaceDark
        )
    }

    // Request More Information Dialog
    if (showMoreInfoDialog) {
        AlertDialog(
            onDismissRequest = { showMoreInfoDialog = false },
            title = { Text(text = "Request Additional Information", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Specify what evidence the creator must submit:",
                        color = VivaTextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = moreInfoNotesInput,
                        onValueChange = { moreInfoNotesInput = it },
                        placeholder = { Text("e.g. Please provide a link to secondary press coverage or official business registration.") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VivaYellow,
                            unfocusedBorderColor = VivaBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().height(80.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.requestMoreInfo(request.id, request.userId, moreInfoNotesInput)
                        showMoreInfoDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VivaYellow)
                ) {
                    Text("Send Request", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showMoreInfoDialog = false }) {
                    Text("Cancel", color = VivaTextSecondary)
                }
            },
            containerColor = VivaSurfaceDark
        )
    }
}

@Composable
private fun ReviewSectionItem(title: String, content: String) {
    Column {
        Text(text = title, color = VivaTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = content, color = Color.White, fontSize = 13.sp, lineHeight = 18.sp)
    }
}
