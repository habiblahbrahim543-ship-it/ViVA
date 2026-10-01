package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.VivaVerifiedBadge
import com.example.ui.components.formatTimestamp
import com.example.ui.theme.*
import com.example.ui.viewmodel.VivaViewModel

val VERIFICATION_CATEGORIES = listOf(
    "Creator",
    "Artist",
    "Musician",
    "Athlete",
    "Public Figure",
    "Business",
    "Organization",
    "Media",
    "Other"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationRequestScreen(
    viewModel: VivaViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val request by viewModel.myVerificationRequest.collectAsStateWithLifecycle()

    var fullName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(VERIFICATION_CATEGORIES.first()) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var country by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }
    var instagram by remember { mutableStateOf("") }
    var youtube by remember { mutableStateOf("") }
    var tiktok by remember { mutableStateOf("") }
    var otherLinks by remember { mutableStateOf("") }
    var supportingDocuments by remember { mutableStateOf("") }

    var additionalInfoInput by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VivaBlack)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .testTag("verification_request_screen")
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
                text = "Request VIVA Verification",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Existing Request Status Banner (If user has an active or past request)
        if (request != null || currentUser?.isVerified == true) {
            val status = if (currentUser?.isVerified == true) "approved" else (request?.status ?: "unverified")

            Card(
                colors = CardDefaults.cardColors(containerColor = VivaSurfaceDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Verification Status",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )

                        val (statusLabel, statusColor) = when (status) {
                            "approved" -> "Verified ✓" to VivaCyan
                            "pending" -> "Pending Review" to VivaYellow
                            "under_review" -> "Under Review" to VivaCyan
                            "more_information_required" -> "More Info Needed" to VivaPink
                            "rejected" -> "Not Approved" to VivaRed
                            else -> "Unverified" to VivaTextSecondary
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(statusColor.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = statusLabel,
                                color = statusColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    when (status) {
                        "approved" -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                VivaVerifiedBadge(size = 22.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Congratulations! Your VIVA account is verified.",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "The official VIVA verification checkmark is visible to all users.",
                                        color = VivaTextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                        "pending", "under_review" -> {
                            Text(
                                text = "Verification request submitted.\nStatus: Pending review by VIVA Trust & Safety.",
                                color = Color.White,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                            if (request != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Submitted: ${formatTimestamp(request!!.createdAt)} • Category: ${request!!.category}",
                                    color = VivaTextTertiary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        "more_information_required" -> {
                            Text(
                                text = "Additional information is required for your verification request.",
                                color = VivaPink,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            if (!request?.adminNotes.isNullOrBlank()) {
                                Text(
                                    text = "Administrator Note: \"${request!!.adminNotes}\"",
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = additionalInfoInput,
                                onValueChange = { additionalInfoInput = it },
                                label = { Text("Provide additional evidence / links") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VivaPink,
                                    unfocusedBorderColor = VivaBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(90.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    if (additionalInfoInput.isNotBlank() && request != null) {
                                        viewModel.submitAdditionalInfo(request!!.id, additionalInfoInput)
                                        additionalInfoInput = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = VivaPink),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(42.dp)
                            ) {
                                Text(text = "Re-submit for Review", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        "rejected" -> {
                            Text(
                                text = "Your verification request was not approved.",
                                color = VivaRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            if (!request?.rejectionReason.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Reason: ${request!!.rejectionReason}",
                                    color = VivaTextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "You may update your profile presence and submit a new request below.",
                                color = VivaTextTertiary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Verification Form (only show if not verified or pending)
        if (currentUser?.isVerified != true && request?.status != "pending" && request?.status != "under_review" && request?.status != "more_information_required") {
            // Notice card
            Card(
                colors = CardDefaults.cardColors(containerColor = VivaSurfaceDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = VivaCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Submitting a request does not guarantee verification. Verified badges are granted to authentic public figures, notable creators, and registered organizations on VIVA.",
                        color = VivaTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Form Fields
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Full Legal Name
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Legal / Official Name *") },
                    placeholder = { Text("e.g. John Doe or Acme Corp") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VivaPink,
                        unfocusedBorderColor = VivaBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("verification_full_name")
                )

                // Category Selection
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category *") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select category",
                                tint = VivaTextSecondary,
                                modifier = Modifier.clickable { categoryDropdownExpanded = !categoryDropdownExpanded }
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VivaPink,
                            unfocusedBorderColor = VivaBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { categoryDropdownExpanded = true }
                            .testTag("verification_category")
                    )

                    DropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false },
                        modifier = Modifier.background(VivaSurfaceDark)
                    ) {
                        VERIFICATION_CATEGORIES.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat, color = Color.White) },
                                onClick = {
                                    selectedCategory = cat
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Country
                OutlinedTextField(
                    value = country,
                    onValueChange = { country = it },
                    label = { Text("Country / Region *") },
                    placeholder = { Text("e.g. United States, France, Japan") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VivaPink,
                        unfocusedBorderColor = VivaBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("verification_country")
                )

                // Reason for verification
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason for Verification *") },
                    placeholder = { Text("Explain notability, audience size, media presence, or public interest...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VivaPink,
                        unfocusedBorderColor = VivaBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(100.dp).testTag("verification_reason")
                )

                // Official Website
                OutlinedTextField(
                    value = website,
                    onValueChange = { website = it },
                    label = { Text("Official Website / Press Portfolio") },
                    placeholder = { Text("https://...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VivaPink,
                        unfocusedBorderColor = VivaBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Social Links
                Text(
                    text = "External Social Profiles (Evidence of Notability)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                OutlinedTextField(
                    value = instagram,
                    onValueChange = { instagram = it },
                    label = { Text("Instagram Profile / Handle") },
                    placeholder = { Text("@handle") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VivaPink,
                        unfocusedBorderColor = VivaBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = youtube,
                    onValueChange = { youtube = it },
                    label = { Text("YouTube Channel") },
                    placeholder = { Text("Channel URL or handle") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VivaPink,
                        unfocusedBorderColor = VivaBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = tiktok,
                    onValueChange = { tiktok = it },
                    label = { Text("TikTok / Other Network") },
                    placeholder = { Text("@handle") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VivaPink,
                        unfocusedBorderColor = VivaBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Supporting Documents
                OutlinedTextField(
                    value = supportingDocuments,
                    onValueChange = { supportingDocuments = it },
                    label = { Text("Supporting Documents / Identification") },
                    placeholder = { Text("e.g. Government ID number, business registration, or press coverage articles") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VivaPink,
                        unfocusedBorderColor = VivaBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(90.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (fullName.isBlank() || country.isBlank() || reason.isBlank()) {
                            viewModel.showToast("Please fill all required fields (*).")
                        } else {
                            viewModel.submitVerificationRequest(
                                fullName = fullName,
                                category = selectedCategory,
                                country = country,
                                reason = reason,
                                website = website,
                                instagram = instagram,
                                youtube = youtube,
                                tiktok = tiktok,
                                otherLinks = otherLinks,
                                supportingDocuments = supportingDocuments
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VivaPink),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_verification_button")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Submit", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Submit Verification Request",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
