package com.example.elderhelpprototypev01.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elderhelpprototypev01.ui.localization.Localization
import com.example.elderhelpprototypev01.ui.theme.*

/**
 * 6. VOICE HERO — MOST IMPORTANT COMPONENT (Page 5, 6)
 *
 * Dedicated Voice Hero Container (24px radius, white surface with very subtle tint, no heavy border).
 *
 * Layered radial rings:
 * - Outer ring (180-200px) -> soft blue translucent ring
 * - Medium blue ring
 * - Primary blue circle (132px)
 * - White microphone icon
 *
 * Animated idle pulse (1.8-2.5s), press scale 0.96.
 */
@Composable
fun VoiceHero(
    isListening: Boolean = false,
    currentLanguage: String = "English (India)",
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val strings = Localization.getStrings(currentLanguage)
    val isHindi = currentLanguage.contains("Hindi")

    // Idle breathing pulse (1.8 - 2.5s)
    val infiniteTransition = rememberInfiniteTransition(label = "heroPulse")
    val pulseScaleOuter by infiniteTransition.animateFloat(
        initialValue = if (isListening) 1.0f else 0.98f,
        targetValue = if (isListening) 1.20f else 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 700 else 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScaleOuter"
    )

    val pulseScaleMedium by infiniteTransition.animateFloat(
        initialValue = if (isListening) 1.0f else 0.96f,
        targetValue = if (isListening) 1.12f else 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 700 else 2200, easing = FastOutSlowInEasing, delayMillis = 200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScaleMedium"
    )

    // Press scale 0.96 with 100-150ms transition
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing),
        label = "buttonScale"
    )

    val outerRingColor by animateColorAsState(
        targetValue = if (isListening) HeroMicListeningRingOuter else HeroMicRingOuter,
        animationSpec = tween(150),
        label = "outerRingColor"
    )

    val mediumRingColor by animateColorAsState(
        targetValue = if (isListening) HeroMicListeningRingMedium else HeroMicRingMedium,
        animationSpec = tween(150),
        label = "mediumRingColor"
    )

    val coreButtonColor by animateColorAsState(
        targetValue = if (isListening) AppEmergency else AppPrimary,
        animationSpec = tween(150),
        label = "coreButtonColor"
    )

    // Hero Container Surface (24px radius, white surface, subtle elevation)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = if (isListening) strings.listeningText else strings.tapToSpeak
            },
        shape = RoundedCornerShape(24.dp),
        color = AppSurface,
        shadowElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, AppBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Label: ✦ SAHAAY VOICE COMPANION
            Text(
                text = if (isListening) "✦ SAHAAY IS LISTENING" else "✦ SAHAAY VOICE COMPANION",
                style = Typography.bodySmall.copy(
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isListening) AppEmergency else AppPrimary,
                    letterSpacing = 1.2.sp
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Microphone Radial Interaction Area (188px)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(188.dp)
            ) {
                // Layer 1: Outer soft translucent ring
                Box(
                    modifier = Modifier
                        .size(184.dp)
                        .scale(pulseScaleOuter)
                        .clip(CircleShape)
                        .background(outerRingColor)
                )

                // Layer 2: Medium ring
                Box(
                    modifier = Modifier
                        .size(156.dp)
                        .scale(pulseScaleMedium)
                        .clip(CircleShape)
                        .background(mediumRingColor)
                )

                // Layer 3 & 4: Primary Blue Circle (132px) + White Microphone
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(132.dp)
                        .scale(buttonScale)
                        .clip(CircleShape)
                        .background(coreButtonColor)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            role = Role.Button,
                            onClick = onClick
                        )
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                        contentDescription = "Voice Assistant Trigger",
                        tint = Color.White,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hero Title: 24px / 30px / 750
            Text(
                text = if (isListening) strings.tapToStop else strings.tapToSpeak,
                style = Typography.headlineMedium.copy(
                    fontSize = 24.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTextPrimary,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Body Supporting Text: 14px / 20px / 450
            Text(
                text = if (isListening) {
                    if (isHindi) "अपनी बात कहें, मैं सुन रहा हूँ..." else "Listening... ask anything now"
                } else {
                    if (isHindi) "मुझसे मदद मांगें..." else "Ask me to help with..."
                },
                style = Typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = AppTextSecondary,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Prompt hints: "Book doctor" "Pay bill"
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppleBlueLight)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = if (isHindi) "\"डॉक्टर बुक करो\"" else "\"Book doctor\"",
                        style = Typography.bodySmall.copy(
                            color = AppPrimary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppleBlueLight)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = if (isHindi) "\"बिल भरो\"" else "\"Pay bill\"",
                        style = Typography.bodySmall.copy(
                            color = AppPrimary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * Backward compatibility alias for MicrophoneButton
 */
@Composable
fun MicrophoneButton(
    isListening: Boolean = false,
    currentLanguage: String = "English (India)",
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    VoiceHero(
        isListening = isListening,
        currentLanguage = currentLanguage,
        onClick = onClick,
        modifier = modifier
    )
}
