package com.example.elderhelpprototypev01.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
    val isHindi = currentLanguage.contains("Hindi")

    // Smooth rhythmic breathing animation
    val infiniteTransition = rememberInfiniteTransition(label = "voicePulse")
    val pulseScale1 by infiniteTransition.animateFloat(
        initialValue = if (isListening) 1.0f else 0.98f,
        targetValue = if (isListening) 1.25f else 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 700 else 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale1"
    )
    val pulseScale2 by infiniteTransition.animateFloat(
        initialValue = if (isListening) 1.0f else 0.95f,
        targetValue = if (isListening) 1.15f else 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 700 else 2200, easing = FastOutSlowInEasing, delayMillis = 300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale2"
    )

    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else if (isListening) 1.04f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "buttonScale"
    )

    val gradientColors = if (isListening) {
        listOf(MicAppleListeningStart, MicAppleListeningEnd)
    } else {
        listOf(MicAppleGradientStart, MicAppleGradientEnd)
    }

    val auraColor by animateColorAsState(
        targetValue = if (isListening) MicListeningGlow else AppleBlueGlow,
        animationSpec = tween(400),
        label = "auraColor"
    )

    // Hero Voice Companion Card
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = if (isListening) strings.listeningText else strings.tapToSpeak
            },
        shape = RoundedCornerShape(24.dp),
        color = AppleSurfaceWhite,
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, AppleBorderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Top Badge Pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isListening) SosRedBg else AppleBlueLight,
                border = BorderStroke(1.dp, if (isListening) SosRedBorder else AppleBlueSubtle)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isListening) "🔴" else "✨",
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isListening) {
                            if (isHindi) "सहााय सुन रहा है..." else "Sahaay is listening..."
                        } else {
                            if (isHindi) "सहााय वॉयस असिस्टेंट" else "Sahaay AI Voice Companion"
                        },
                        style = Typography.labelSmall.copy(
                            color = if (isListening) SosRedIcon else AppleBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            letterSpacing = 0.3.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Central Pulsing Microphone Trigger (180dp canvas)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(176.dp)
            ) {
                // Outer Aura Ring
                Box(
                    modifier = Modifier
                        .size(168.dp)
                        .scale(pulseScale1)
                        .clip(CircleShape)
                        .background(auraColor)
                )

                // Mid Soundwave Ring
                Box(
                    modifier = Modifier
                        .size(142.dp)
                        .scale(pulseScale2)
                        .clip(CircleShape)
                        .background(auraColor.copy(alpha = 0.6f))
                )

                // Core Tactile Mic Button
                Surface(
                    modifier = Modifier
                        .size(118.dp)
                        .scale(buttonScale)
                        .clip(CircleShape)
                        .shadow(elevation = 8.dp, shape = CircleShape)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            role = Role.Button,
                            onClick = onClick
                        ),
                    shape = CircleShape,
                    color = Color.Transparent
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(gradientColors))
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                            contentDescription = "Voice Assistant Trigger",
                            tint = Color.White,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Clear Action Typography
            Text(
                text = if (isListening) strings.tapToStop else strings.tapToSpeak,
                style = Typography.titleLarge.copy(
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppleTextPrimary,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isListening) {
                    if (isHindi) "अपनी बात कहें, मैं समझ रहा हूँ..." else "Speak now, I am listening to you..."
                } else {
                    if (isHindi) "डॉक्टर बुक करें, बिल भरें या मदद मांगें" else "Ask to book doctors, pay bills, or get help"
                },
                style = Typography.bodyMedium.copy(
                    color = AppleTextMuted,
                    fontSize = 13.5.sp,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Prompt Hints Pill
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AppleCanvasBg,
                border = BorderStroke(1.dp, AppleBorderSubtle)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💬", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi)
                            "\"डॉक्टर से मिलना है\"  •  \"बिजली का बिल भरो\""
                        else
                            "\"Book doctor visit\"  •  \"Pay electricity bill\"",
                        style = Typography.bodySmall.copy(
                            color = AppleTextSecondary,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}
