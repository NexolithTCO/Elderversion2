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
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elderhelpprototypev01.ui.localization.Localization
import com.example.elderhelpprototypev01.ui.theme.*

@Composable
fun MicrophoneButton(
    isListening: Boolean = false,
    currentLanguage: String = "English (India)",
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val strings = Localization.getStrings(currentLanguage)

    // Smooth pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulseRing")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = if (isListening) 1.0f else 0.97f,
        targetValue = if (isListening) 1.22f else 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 750 else 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else if (isListening) 1.05f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "buttonScale"
    )

    val gradientColors = if (isListening) {
        listOf(MicAppleListeningStart, MicAppleListeningEnd)
    } else {
        listOf(MicAppleGradientStart, MicAppleGradientEnd)
    }

    val glowColor by animateColorAsState(
        targetValue = if (isListening) MicAppleListeningStart.copy(alpha = 0.25f) else AppleBlueGlow,
        animationSpec = tween(400),
        label = "glowColor"
    )

    // Featured Banner Container
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        color = AppleSurfaceWhite,
        shadowElevation = 4.dp,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Hero Voice Circular Trigger (140dp)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(190.dp)
            ) {
                // Pulsing Aura Ring
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(glowColor)
                )

                // Secondary Accent Layer
                Box(
                    modifier = Modifier
                        .size(154.dp)
                        .clip(CircleShape)
                        .background(glowColor.copy(alpha = 0.5f))
                )

                // Core Microphone Surface Circle (132dp)
                Surface(
                    modifier = Modifier
                        .size(132.dp)
                        .scale(buttonScale)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onClick
                        ),
                    shape = CircleShape,
                    shadowElevation = 8.dp,
                    tonalElevation = 6.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(gradientColors))
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                            contentDescription = "Microphone Trigger",
                            tint = Color.White,
                            modifier = Modifier.size(58.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Action Title: "Tap to speak" / localized
            Text(
                text = if (isListening) strings.tapToStop else strings.tapToSpeak,
                style = Typography.labelLarge.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppleTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isListening) strings.listeningText else strings.micSubtitle,
                style = Typography.bodyMedium.copy(
                    color = AppleTextMuted,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }
}
