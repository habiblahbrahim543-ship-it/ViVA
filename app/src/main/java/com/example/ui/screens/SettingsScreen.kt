package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.VivaVerifiedBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.VivaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: VivaViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()
    val myVerificationReq by viewModel.myVerificationRequest.collectAsStateWithLifecycle()
    val blockedUsers by viewModel.blockedUsers.collectAsStateWithLifecycle()

    var privateAccount by remember { mutableStateOf(false) }
    var allowComments by remember { mutableStateOf(true) }
    var allowMessages by remember { mutableStateOf(true) }
    var allowDownloads by remember { mutableStateOf(true) }
    var showActivityStatus by remember { mutableStateOf(true) }
    var pushNotifications by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VivaBlack)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .testTag("settings_screen")
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
                text = "Settings & Privacy",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Account Section
        SettingsSectionHeader(title = "Account")
        SettingsCard {
            SettingsItemRow(
                icon = Icons.Outlined.Person,
                title = "Account Information",
                subtitle = "@${currentUser?.username ?: "user"} • ${if (currentUser?.isVerified == true) "Verified ✓" else "Standard Account"}",
                onClick = { viewModel.openEditProfile() }
            )
            Divider(color = VivaBorder, thickness = 0.5.dp)

            // Request Verification Row (Requirement 3: Profile -> Settings -> Account -> Request Verification)
            val verificationSubtitle = when {
                currentUser?.isVerified == true -> "Account Verified ✓"
                myVerificationReq?.status == "pending" -> "Status: Pending Review ⏳"
                myVerificationReq?.status == "under_review" -> "Status: Under Review 🔍"
                myVerificationReq?.status == "more_information_required" -> "Status: Action Required ⚠️"
                myVerificationReq?.status == "rejected" -> "Status: Not Approved ❌"
                else -> "Apply for official VIVA verified checkmark"
            }
            SettingsItemRow(
                icon = Icons.Default.Verified,
                title = "Request Verification",
                subtitle = verificationSubtitle,
                iconTint = if (currentUser?.isVerified == true) VivaVerifiedBlue else VivaCyan,
                onClick = {
                    onBack()
                    viewModel.openVerificationRequestScreen()
                }
            )
            Divider(color = VivaBorder, thickness = 0.5.dp)

            SettingsItemRow(
                icon = Icons.Outlined.Lock,
                title = "Password & Security",
                subtitle = "Two-Factor Auth Active",
                onClick = { viewModel.showToast("Security settings are configured.") }
            )
        }

        // Privacy Section
        Spacer(modifier = Modifier.height(16.dp))
        SettingsSectionHeader(title = "Privacy & Safety")
        SettingsCard {
            SettingsSwitchRow(
                icon = Icons.Outlined.VisibilityOff,
                title = "Private Account",
                subtitle = "Only approved followers can see your videos",
                checked = privateAccount,
                onCheckedChange = {
                    privateAccount = it
                    viewModel.showToast(if (it) "Account set to Private" else "Account set to Public")
                }
            )
            Divider(color = VivaBorder, thickness = 0.5.dp)
            SettingsSwitchRow(
                icon = Icons.Outlined.ChatBubbleOutline,
                title = "Allow Comments",
                subtitle = "Who can comment on your posts",
                checked = allowComments,
                onCheckedChange = { allowComments = it }
            )
            Divider(color = VivaBorder, thickness = 0.5.dp)
            SettingsSwitchRow(
                icon = Icons.Outlined.Send,
                title = "Direct Messaging",
                subtitle = "Receive messages from other creators",
                checked = allowMessages,
                onCheckedChange = { allowMessages = it }
            )
            Divider(color = VivaBorder, thickness = 0.5.dp)
            SettingsSwitchRow(
                icon = Icons.Outlined.Download,
                title = "Video Downloads",
                subtitle = "Allow others to download your videos",
                checked = allowDownloads,
                onCheckedChange = { allowDownloads = it }
            )
            Divider(color = VivaBorder, thickness = 0.5.dp)
            SettingsSwitchRow(
                icon = Icons.Outlined.Circle,
                title = "Activity Status",
                subtitle = "Show when you are active on VIVA",
                checked = showActivityStatus,
                onCheckedChange = { showActivityStatus = it }
            )
            Divider(color = VivaBorder, thickness = 0.5.dp)
            // Blocked accounts
            SettingsItemRow(
                icon = Icons.Outlined.Block,
                title = "Blocked Accounts",
                subtitle = "${blockedUsers.size} accounts blocked",
                iconTint = VivaRed,
                onClick = {
                    onBack()
                    viewModel.openBlockedUsersScreen()
                }
            )
        }

        // Notifications
        Spacer(modifier = Modifier.height(16.dp))
        SettingsSectionHeader(title = "Notifications")
        SettingsCard {
            SettingsSwitchRow(
                icon = Icons.Outlined.Notifications,
                title = "Push Notifications",
                subtitle = "Likes, comments, mentions, and follows",
                checked = pushNotifications,
                onCheckedChange = { pushNotifications = it }
            )
        }

        // Admin Portal
        Spacer(modifier = Modifier.height(16.dp))
        SettingsSectionHeader(title = "Administration")
        SettingsCard {
            val isAdmin = currentUser?.role == "ADMIN" || currentUser?.role == "OWNER"
            SettingsItemRow(
                icon = Icons.Default.Shield,
                title = if (isAdmin) "VIVA Private Control Panel" else "Administrator Portal",
                subtitle = if (isAdmin) "Manage verifications, users, reports & live" else "Restricted access (Owner & Staff only)",
                iconTint = if (isAdmin) VivaCyan else Color.White.copy(alpha = 0.5f),
                onClick = {
                    onBack()
                    viewModel.openAdminDashboard()
                }
            )
        }

        // About & Support
        Spacer(modifier = Modifier.height(16.dp))
        SettingsSectionHeader(title = "Support & Help")
        SettingsCard {
            SettingsItemRow(
                icon = Icons.Default.SupportAgent,
                title = "Contact VIVA",
                subtitle = "Official 24/7 Support, Trust & Safety Desk",
                iconTint = VivaCyan,
                onClick = {
                    onBack()
                    viewModel.openContactViva()
                }
            )
            Divider(color = VivaBorder, thickness = 0.5.dp)
            SettingsItemRow(
                icon = Icons.Outlined.HelpOutline,
                title = "Community Guidelines",
                subtitle = "Safety standards and verification criteria",
                onClick = { viewModel.showToast("VIVA Guidelines: Authentic, respectful, original.") }
            )
            Divider(color = VivaBorder, thickness = 0.5.dp)
            SettingsItemRow(
                icon = Icons.Outlined.Info,
                title = "About VIVA",
                subtitle = "Version 1.0 (Production Release)",
                onClick = { viewModel.showToast("VIVA Short Video Network v1.0") }
            )
        }

        // Log out
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(VivaSurfaceDark)
                .clickable {
                    viewModel.showToast("Signed out of session")
                    onBack()
                }
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Log Out of VIVA",
                color = VivaRed,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = VivaTextSecondary,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = VivaSurfaceDark),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(content = content)
    }
}

@Composable
private fun SettingsItemRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    iconTint: Color = VivaCyan
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = subtitle, color = VivaTextSecondary, fontSize = 12.sp)
        }
        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = null,
            tint = VivaTextTertiary,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = VivaCyan,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = subtitle, color = VivaTextSecondary, fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = VivaPink
            )
        )
    }
}
