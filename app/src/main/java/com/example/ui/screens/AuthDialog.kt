package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.VivaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthDialog(
    viewModel: VivaViewModel,
    onDismiss: () -> Unit
) {
    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = VivaSurfaceDark,
        dragHandle = { BottomSheetDefaults.DragHandle(color = VivaBorder) },
        modifier = Modifier.testTag("auth_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isSignUp) "Join VIVA Network" else "Log In to VIVA",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = VivaTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isSignUp) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Unique Username") },
                    placeholder = { Text("e.g. creator_99") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VivaPink,
                        unfocusedBorderColor = VivaBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = { Icon(imageVector = Icons.Default.Mail, contentDescription = null, tint = VivaTextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VivaPink,
                    unfocusedBorderColor = VivaBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = VivaTextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VivaPink,
                    unfocusedBorderColor = VivaBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (isSignUp) {
                        val clean = username.trim().removePrefix("@")
                        if (clean.equals("VIVA", ignoreCase = true) ||
                            clean.equals("admin", ignoreCase = true) ||
                            clean.equals("owner", ignoreCase = true) ||
                            clean.equals("support", ignoreCase = true)
                        ) {
                            viewModel.showToast("Username @$clean is reserved for official VIVA platform administration.")
                            return@Button
                        }
                    }
                    viewModel.showToast(if (isSignUp) "Welcome to VIVA! Account created." else "Welcome back!")
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = VivaPink),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("auth_submit_button")
            ) {
                Text(
                    text = if (isSignUp) "Create Account" else "Log In",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Google Sign In button
            OutlinedButton(
                onClick = {
                    viewModel.showToast("Signed in with Google account")
                    onDismiss()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Text(text = "Continue with Google", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isSignUp) "Already have an account? " else "Don't have an account? ",
                    color = VivaTextSecondary,
                    fontSize = 13.sp
                )
                Text(
                    text = if (isSignUp) "Log in" else "Sign up",
                    color = VivaPink,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { isSignUp = !isSignUp }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
