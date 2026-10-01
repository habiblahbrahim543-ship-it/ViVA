package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.ActiveReportTarget
import com.example.ui.viewmodel.VivaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDialog(
    target: ActiveReportTarget,
    viewModel: VivaViewModel,
    onDismiss: () -> Unit
) {
    val reportReasons = listOf(
        "Spam",
        "Harassment",
        "Violence",
        "Illegal content",
        "Copyright",
        "Other"
    )
    var selectedReason by remember { mutableStateOf(reportReasons.first()) }
    var notes by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = VivaSurfaceDark,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = VivaBorder)
        },
        modifier = Modifier.testTag("report_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Report ${target.type.lowercase().replaceFirstChar { it.uppercase() }}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = VivaTextSecondary
                    )
                }
            }

            Text(
                text = "Target: ${target.title}",
                color = VivaCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = "Please select the reason that best describes this issue:",
                color = VivaTextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Reason chips/rows
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                reportReasons.forEach { reason ->
                    val isSelected = selectedReason == reason
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) VivaSurfaceElevated else VivaSurfaceVariant)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) VivaPink else VivaBorder,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedReason = reason }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = reason,
                            color = if (isSelected) Color.White else VivaTextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedReason = reason },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = VivaPink,
                                unselectedColor = VivaBorder
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Additional details (optional)") },
                placeholder = { Text("Explain why this content violates community guidelines...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VivaPink,
                    unfocusedBorderColor = VivaBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.submitReport(reason = selectedReason, notes = notes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = VivaPink),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("submit_report_button")
            ) {
                Text(
                    text = "Submit Report",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
