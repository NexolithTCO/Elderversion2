package com.example.elderhelpprototypev01.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.elderhelpprototypev01.model.BillType
import com.example.elderhelpprototypev01.ui.theme.*

/**
 * BillSelectionModal
 *
 * Intermediate selection screen displayed when tapping "Pay" in Quick Tasks.
 * Shows all three payment options as uniform cards. Tapping one navigates
 * directly to that type's BillPaymentModal.
 */
@Composable
fun BillSelectionModal(
    onDismiss: () -> Unit,
    onSelectBillType: (BillType) -> Unit
) {
    val billOptions = listOf(
        BillOption(
            type = BillType.ELECTRICITY,
            emoji = "⚡",
            label = "Electricity Bill",
            hint = "Consumer ID",
            bgColor = Color(0xFFFFF8E1),
            accentColor = Color(0xFFFFB300)
        ),
        BillOption(
            type = BillType.WATER,
            emoji = "💧",
            label = "Water Bill",
            hint = "Meter ID",
            bgColor = Color(0xFFE1F5FE),
            accentColor = Color(0xFF0288D1)
        ),
        BillOption(
            type = BillType.MOBILE,
            emoji = "📱",
            label = "Mobile Recharge",
            hint = "Mobile Number",
            bgColor = Color(0xFFF3E5F5),
            accentColor = Color(0xFF8E24AA)
        )
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = AppleCanvasBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8F5E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💳", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Pay Bills & Utilities",
                                style = Typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AppleTextPrimary,
                                    fontSize = 19.sp
                                )
                            )
                            Text(
                                text = "Select a category to continue",
                                style = Typography.bodySmall.copy(color = AppleTextMuted)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AppleBorderSubtle.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AppleTextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // 3 Uniform Bill Option Cards
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    billOptions.forEach { option ->
                        BillSelectionCard(
                            option = option,
                            onClick = {
                                onSelectBillType(option.type)
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Safe & Instant badge
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF0F9FF),
                    border = BorderStroke(1.dp, Color(0xFF0288D1).copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("🔒", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "All payments are encrypted & instant",
                            style = Typography.bodySmall.copy(
                                color = Color(0xFF0277BD),
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Internal Data Model
// ─────────────────────────────────────────────────────────────────

private data class BillOption(
    val type: BillType,
    val emoji: String,
    val label: String,
    val hint: String,
    val bgColor: Color,
    val accentColor: Color
)

// ─────────────────────────────────────────────────────────────────
// Uniform Bill Selection Card
// Identical dimensions: fillMaxWidth, height(96.dp), padding(16.dp)
// ─────────────────────────────────────────────────────────────────

@Composable
private fun BillSelectionCard(
    option: BillOption,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "billCardScale"
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .scale(scale)
            .height(96.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, option.accentColor.copy(alpha = 0.3f)),
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Uniform icon area
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(option.bgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(option.emoji, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.label,
                    style = Typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppleTextPrimary,
                        fontSize = 17.sp
                    )
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Enter ${option.hint} to pay",
                    style = Typography.bodySmall.copy(
                        color = AppleTextMuted,
                        fontSize = 13.sp
                    )
                )
            }

            // Arrow indicator
            Surface(
                shape = CircleShape,
                color = option.accentColor.copy(alpha = 0.1f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "›",
                        style = Typography.titleLarge.copy(
                            color = option.accentColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    )
                }
            }
        }
    }
}
