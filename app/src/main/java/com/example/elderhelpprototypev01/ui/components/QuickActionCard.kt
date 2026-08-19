package com.example.elderhelpprototypev01.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elderhelpprototypev01.ui.theme.*

/**
 * Editorial-Style Quick Action Row for Elder Help.
 *
 * Clean list item:
 * [ (Icon Tile)   Title & Subtitle description             (Chevron →) ]
 */
@Composable
fun QuickActionRowItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    iconBgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    emoji: String? = null,
    isEmergency: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "rowScale"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .semantics {
                contentDescription = "$title, $subtitle. Tap to open."
            }
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Leading Icon Tile (42px)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isEmergency) SosRedBg else iconBgColor)
        ) {
            if (emoji != null) {
                Text(text = emoji, fontSize = 20.sp)
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isEmergency) SosRedIcon else iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = Typography.titleMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isEmergency) SosRedIcon else AppTextPrimary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = Typography.bodySmall.copy(
                    fontSize = 12.5.sp,
                    color = AppTextSecondary,
                    fontWeight = FontWeight.Normal
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Trailing Chevron
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = if (isEmergency) SosRedIcon.copy(alpha = 0.7f) else AppTextMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}

/**
 * Backward compatibility wrapper for QuickActionCard
 */
@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    actionButtonText: String,
    icon: ImageVector,
    iconTint: Color,
    iconBgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    emoji: String? = null
) {
    QuickActionRowItem(
        title = title,
        subtitle = subtitle,
        icon = icon,
        iconTint = iconTint,
        iconBgColor = iconBgColor,
        onClick = onClick,
        modifier = modifier,
        emoji = emoji,
        isEmergency = title.contains("Emergency", ignoreCase = true) || title.contains("आपातकाल")
    )
}
