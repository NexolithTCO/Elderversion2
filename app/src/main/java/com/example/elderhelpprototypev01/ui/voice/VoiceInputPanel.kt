package com.example.elderhelpprototypev01.ui.voice

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elderhelpprototypev01.model.VoiceState
import com.example.elderhelpprototypev01.ui.theme.*

/**
 * VoiceInputPanel
 *
 * The large microphone button + listening state display.
 * Shows different states: Idle, Listening (animated), Processing, Error.
 */
@Composable
fun VoiceInputPanel(
    voiceState: VoiceState,
    transcript: String,
    currentLanguage: String = "English (India)",
    onMicClick: () -> Unit,
    onStopClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isHindi = currentLanguage.contains("Hindi") || currentLanguage.contains("हिंदी")
    val isListening = voiceState is VoiceState.Listening || voiceState is VoiceState.PartialResult
    val isProcessing = voiceState is VoiceState.Processing

    // Pulsing animation for listening state
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseAnim.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ---- State Label ----
        val stateText = when (voiceState) {
            is VoiceState.Idle -> if (isHindi) "बोलने के लिए माइक दबाएं" else "Tap the microphone to speak"
            is VoiceState.RequestingPermission -> if (isHindi) "माइक्रोफ़ोन अनुमति आवश्यक है" else "Microphone permission needed"
            is VoiceState.Listening -> if (isHindi) "मैं सुन रहा हूँ..." else "I'm listening..."
            is VoiceState.PartialResult -> if (isHindi) "मैं सुन रहा हूँ..." else "I'm listening..."
            is VoiceState.Processing -> if (isHindi) "विचार कर रहा हूँ..." else "Thinking..."
            is VoiceState.Done -> if (isHindi) "हो गया! दोबारा बोलने के लिए टैप करें" else "Done! Tap again to speak"
            is VoiceState.Error -> voiceState.message
        }

        val labelColor = when (voiceState) {
            is VoiceState.Error -> Color(0xFFFF3B30)
            is VoiceState.Listening, is VoiceState.PartialResult -> AppleBlue
            is VoiceState.Processing -> Color(0xFFFF9500)
            else -> AppleTextMuted
        }

        Text(
            text = stateText,
            style = Typography.bodyLarge.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = labelColor,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ---- Microphone Button ----
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(140.dp)
        ) {
            // Outer pulse ring (only when listening)
            if (isListening) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(AppleBlue.copy(alpha = 0.15f))
                )
            }

            // Middle ring
            Box(
                modifier = Modifier
                    .size(116.dp)
                    .clip(CircleShape)
                    .background(
                        if (isListening) AppleBlue.copy(alpha = 0.12f)
                        else AppleBorderSubtle.copy(alpha = 0.5f)
                    )
            )

            // Main button
            if (isListening || isProcessing) {
                // Stop button
                Button(
                    onClick = onStopClick,
                    modifier = Modifier.size(90.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isListening) Color(0xFFFF3B30) else Color(0xFFFF9500)
                    ),
                    elevation = ButtonDefaults.buttonElevation(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop listening",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            } else {
                // Mic button
                Button(
                    onClick = onMicClick,
                    modifier = Modifier.size(90.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    elevation = ButtonDefaults.buttonElevation(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        MicAppleGradientStart,
                                        MicAppleGradientEnd
                                    )
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Start listening",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ---- Transcript Display ----
        if (transcript.isNotBlank()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                color = AppleBlueLight,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "You said:",
                        style = Typography.labelMedium.copy(
                            color = AppleBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\u201C$transcript\u201D",
                        style = Typography.bodyLarge.copy(
                            color = AppleTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}
