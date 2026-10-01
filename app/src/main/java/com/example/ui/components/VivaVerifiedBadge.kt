package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Official VIVA Verification Blue
val VivaVerifiedBlue = Color(0xFF1D9BF0)

@Composable
fun VivaVerifiedBadge(
    modifier: Modifier = Modifier,
    size: Dp = 15.dp,
    contentDescription: String = "Verified Account"
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(VivaVerifiedBlue)
            .testTag("viva_verified_badge"),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(size * 0.72f)
        )
    }
}
