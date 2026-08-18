package com.example.elderhelpprototypev01.ui.voice

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elderhelpprototypev01.model.AssistantResponse
import com.example.elderhelpprototypev01.ui.theme.*

/**
 * ResponseCard
 *
 * Displays the latest Sahaay response in a structured card:
 * - Main response text
 * - Intent badge
 * - "Next step" section
 * - "💡 Helpful tip" section
 * - 🔊 Play / 🔄 Retry buttons
 * - TTS speed control
 */
@Composable
fun ResponseCard(
    response: AssistantResponse,
    isSpeaking: Boolean,
    ttsEnabled: Boolean,
    speechRate: Float,
    onPlayClick: () -> Unit,
    onStopClick: () -> Unit,
    onRetryClick: () -> Unit,
    onToggleTts: () -> Unit,
    onSpeechRateChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 6.dp,
        border = BorderStroke(1.dp, AppleBorderSubtle)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // ---- Header: Sahaay bot indicator ----
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppleBlueLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🤖", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Sahaay",
                        style = Typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppleTextPrimary,
                            fontSize = 18.sp
                        )
                    )
                    if (response.intent.isNotBlank() && response.intent != "GENERAL" &&
                        response.intent != "ERROR" && response.intent != "LOADING") {
                        IntentBadge(response.intent)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = AppleBorderSubtle, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(14.dp))

            // ---- Main Response Text ----
            Text(
                text = response.response,
                style = Typography.bodyLarge.copy(
                    color = AppleTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal
                )
            )

            // ---- Clarifying Question ----
            if (response.needsClarification && response.clarifyingQuestion != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Color(0xFFFFF9E6),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("❓", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = response.clarifyingQuestion,
                            style = Typography.bodyLarge.copy(
                                color = AppleTextPrimary,
                                fontSize = 17.sp
                            )
                        )
                    }
                }
            }

            // ---- Suggested Next Step ----
            if (!response.needsClarification && response.suggestedNextStep != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = AppleBlueLight,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Next step",
                            style = Typography.labelMedium.copy(
                                color = AppleBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = response.suggestedNextStep,
                            style = Typography.bodyLarge.copy(
                                color = AppleTextPrimary,
                                fontSize = 17.sp
                            )
                        )
                    }
                }
            }

            // ---- Helpful Tip ----
            if (response.helpfulTip != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Color(0xFFEAF9EC),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("💡", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Helpful tip",
                                style = Typography.labelMedium.copy(
                                    color = BillsGreenIcon,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = response.helpfulTip,
                                style = Typography.bodyMedium.copy(
                                    color = AppleTextPrimary,
                                    fontSize = 16.sp
                                )
                            )
                        }
                    }
                }
            }

            // ---- Emergency Action Button (for EMERGENCY_HELP intent) ----
            if (response.intent == "EMERGENCY_HELP") {
                val context = androidx.compose.ui.platform.LocalContext.current
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = {
                        val digits = response.response.filter { it.isDigit() || it == '+' }
                        val phoneToCall = if (digits.length >= 10) digits else "+919876543210"
                        val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                            data = android.net.Uri.parse("tel:$phoneToCall")
                            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // ignore
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B30)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhoneInTalk,
                        contentDescription = "Call",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Call Emergency Contact Now 📞",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = AppleBorderSubtle, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // ---- Action Buttons Row ----
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Play/Stop TTS
                if (ttsEnabled) {
                    Button(
                        onClick = if (isSpeaking) onStopClick else onPlayClick,
                        modifier = Modifier.height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSpeaking) Color(0xFFFF3B30) else AppleBlue
                        )
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                            contentDescription = if (isSpeaking) "Stop" else "Play",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isSpeaking) "Stop" else "Play",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Retry
                OutlinedButton(
                    onClick = onRetryClick,
                    modifier = Modifier.height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, AppleBorderSubtle)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Retry",
                        modifier = Modifier.size(18.dp),
                        tint = AppleTextSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Retry",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppleTextSecondary
                    )
                }

                // TTS Toggle
                IconButton(
                    onClick = onToggleTts,
                    modifier = Modifier
                        .height(48.dp)
                        .width(48.dp)
                ) {
                    Icon(
                        imageVector = if (ttsEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Toggle voice",
                        tint = if (ttsEnabled) AppleBlue else AppleTextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // ---- Speech Speed Slider (shown when TTS enabled) ----
            if (ttsEnabled) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.SlowMotionVideo,
                        contentDescription = "Speed",
                        tint = AppleTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Slider(
                        value = speechRate,
                        onValueChange = onSpeechRateChange,
                        valueRange = 0.5f..1.5f,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = AppleBlue,
                            activeTrackColor = AppleBlue,
                            inactiveTrackColor = AppleBorderSubtle
                        )
                    )
                    Text(
                        text = "%.1fx".format(speechRate),
                        style = Typography.labelMedium.copy(
                            color = AppleTextMuted,
                            fontSize = 12.sp
                        ),
                        modifier = Modifier.width(36.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun IntentBadge(intent: String) {
    val (label, color) = when (intent) {
        "BOOK_APPOINTMENT" -> "🏥 Appointment" to Color(0xFF007AFF)
        "PAY_BILL" -> "💳 Bill Payment" to Color(0xFF34C759)
        "FILL_FORM" -> "📝 Form" to Color(0xFFFF9500)
        "EXPLAIN_TERM" -> "💡 Explanation" to Color(0xFFAF52DE)
        "EMERGENCY_HELP" -> "🆘 Emergency" to Color(0xFFFF3B30)
        "ASK_QUESTION" -> "❓ Question" to Color(0xFF5856D6)
        else -> return
    }

    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = label,
            style = Typography.labelMedium.copy(
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
