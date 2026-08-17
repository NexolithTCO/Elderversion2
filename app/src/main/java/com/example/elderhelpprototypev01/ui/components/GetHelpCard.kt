package com.example.elderhelpprototypev01.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elderhelpprototypev01.ui.theme.*

/**
 * GetHelpCard
 *
 * Prominent "Get Help" dashboard banner with a compact, high-visibility Emergency SOS button.
 * Redesigned to integrate urgent assistance cleanly without overwhelming the screen.
 */
@Composable
fun GetHelpCard(
    onEmergencyClick: () -> Unit,
    emergencyContactName: String = "Rahul",
    emergencyContactPhone: String = "+91 98765 43210",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFFFF5F5),
        border = BorderStroke(1.2.dp, Color(0xFFFF3B30).copy(alpha = 0.35f)),
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left details
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF3B30).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🚨", fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Need Urgent Help?",
                        style = Typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F),
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Instant 3s dial to $emergencyContactName ($emergencyContactPhone)",
                        style = Typography.bodySmall.copy(
                            color = AppleTextSecondary,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Compact Emergency SOS Button
            CompactSosButton(onClick = onEmergencyClick)
        }
    }
}

/**
 * Compact SOS Button
 *
 * Sleek, high-contrast, compact emergency trigger.
 */
@Composable
fun CompactSosButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .height(44.dp)
            .shadow(elevation = 6.dp, shape = CircleShape),
        shape = CircleShape,
        color = Color(0xFFFF0000), // Vibrant high-contrast emergency red
        contentColor = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "SOS",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "SOS",
                style = Typography.labelLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    letterSpacing = 1.sp
                )
            )
        }
    }
}
