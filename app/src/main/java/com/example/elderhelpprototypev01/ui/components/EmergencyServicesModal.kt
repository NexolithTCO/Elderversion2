package com.example.elderhelpprototypev01.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.elderhelpprototypev01.ui.localization.Localization
import com.example.elderhelpprototypev01.ui.theme.Typography

/**
 * Emergency Services Modal
 *
 * Senior-friendly emergency choice screen with 3 instant dialing options:
 * 1. Call Saved Emergency Contact — immediately opens dialer
 * 2. Call Police Emergency (112) — immediately opens dialer with 112
 * 3. Call Fire Department (101) — immediately opens dialer with 101
 *
 * Matches the app's dark high-contrast modal design system.
 */
@Composable
fun EmergencyServicesModal(
    onDismiss: () -> Unit,
    contactName: String = "Rahul",
    contactNumber: String = "+91 98765 43210",
    currentLanguage: String = "English (India)"
) {
    val context = LocalContext.current
    val strings = Localization.getStrings(currentLanguage)
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        // Full-screen dark overlay backdrop
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .border(2.dp, Color(0xFFFF3B30), RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                color = Color(0xFF1C1C1E),
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Header icon
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF0000).copy(alpha = 0.15f))
                            .border(2.dp, Color(0xFFFF0000), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Emergency",
                            tint = Color(0xFFFF0000),
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    Text(
                        text = strings.emergencyServicesTitle.uppercase(),
                        style = Typography.headlineSmall.copy(
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Option 1: Call Personal Emergency Contact
                    EmergencyActionButton(
                        icon = Icons.Default.PhoneInTalk,
                        title = strings.callEmergencyContact,
                        subtitle = "$contactName • $contactNumber",
                        accentColor = Color(0xFF34C759),
                        feedbackText = feedbackMessage?.takeIf { it == strings.callingContact },
                        onClick = {
                            feedbackMessage = strings.callingContact
                            triggerCall(context, contactNumber)
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Option 2: Call Police Control Room (112)
                    EmergencyActionButton(
                        icon = Icons.Default.LocalPolice,
                        title = strings.callPoliceHelpline,
                        subtitle = strings.policeHelplineSubtitle,
                        accentColor = Color(0xFFFF3B30),
                        feedbackText = feedbackMessage?.takeIf { it == strings.callingPolice },
                        onClick = {
                            feedbackMessage = strings.callingPolice
                            triggerCall(context, "112")
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Option 3: Call Fire Department (101)
                    EmergencyActionButton(
                        icon = Icons.Default.LocalFireDepartment,
                        title = strings.callFireDepartment,
                        subtitle = strings.fireHelplineSubtitle,
                        accentColor = Color(0xFFFF9500),
                        feedbackText = feedbackMessage?.takeIf { it == strings.callingFire },
                        onClick = {
                            feedbackMessage = strings.callingFire
                            triggerCall(context, "101")
                        }
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Close button
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3A3A3C),
                            contentColor = Color.White
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            Color.White.copy(alpha = 0.6f)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strings.close.uppercase(),
                                style = Typography.titleMedium.copy(
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Large, accessible emergency action button with icon, title, subtitle,
 * and spring-physics press animation matching the app's design system.
 */
@Composable
private fun EmergencyActionButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    feedbackText: String? = null,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "emergencyBtnScale"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(20.dp),
        color = accentColor.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, accentColor.copy(alpha = 0.5f)),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f))
                    .border(1.5.dp, accentColor, CircleShape)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = Typography.titleMedium.copy(
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = feedbackText ?: subtitle,
                    style = Typography.bodyMedium.copy(
                        color = if (feedbackText != null) accentColor else Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        fontWeight = if (feedbackText != null) FontWeight.SemiBold else FontWeight.Normal
                    )
                )
            }
        }
    }
}

/**
 * Triggers phone call intent to dial the emergency contact.
 */
private fun triggerCall(context: Context, phoneNumber: String) {
    try {
        val cleanNumber = phoneNumber.replace(" ", "").replace("-", "")
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$cleanNumber")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        Toast.makeText(context, "Dialing ($cleanNumber)...", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to launch dialer: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
