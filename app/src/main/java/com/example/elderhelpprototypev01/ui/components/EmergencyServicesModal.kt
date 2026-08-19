package com.example.elderhelpprototypev01.ui.components

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
 * Senior-friendly emergency choice screen with instant direct calling options:
 * 1. Call Saved Emergency Contact (Family)
 * 2. Call Police / National Helpline (112)
 * 3. Call Medical Ambulance (102)
 * 4. Call Senior Citizen Helpline (14567)
 * 5. Call Fire Department (101)
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
    val isHindi = currentLanguage.contains("Hindi") || currentLanguage.contains("हिंदी")
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var pendingNumberToCall by remember { mutableStateOf<String?>(null) }

    val callPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        pendingNumberToCall?.let { number ->
            EmergencyCallHelper.makeCall(context, number)
            pendingNumberToCall = null
        }
    }

    fun initiateCall(number: String, msg: String) {
        feedbackMessage = msg
        pendingNumberToCall = number
        if (EmergencyCallHelper.hasCallPermission(context)) {
            EmergencyCallHelper.makeCall(context, number)
        } else {
            callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
        }
    }

    val scrollState = rememberScrollState()

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
                .padding(horizontal = 20.dp, vertical = 24.dp),
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
                        .verticalScroll(scrollState)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header icon
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF0000).copy(alpha = 0.15f))
                            .border(2.dp, Color(0xFFFF0000), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Emergency",
                            tint = Color(0xFFFF0000),
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Title
                    Text(
                        text = if (isHindi) "आपातकालीन सेवाएं" else strings.emergencyServicesTitle.uppercase(),
                        style = Typography.headlineSmall.copy(
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isHindi) "तत्काल सीधे कॉल करने के लिए चुनें" else "Tap to place a direct emergency call",
                        style = Typography.bodySmall.copy(color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Option 1: Call Personal Emergency Contact (Family)
                    EmergencyActionButton(
                        icon = Icons.Default.PhoneInTalk,
                        title = if (isHindi) "आपातकालीन संपर्क (परिवार)" else strings.callEmergencyContact,
                        subtitle = "$contactName • $contactNumber",
                        accentColor = Color(0xFF34C759),
                        feedbackText = feedbackMessage?.takeIf { it.contains(contactName) || it.contains("Contact") },
                        onClick = {
                            initiateCall(contactNumber, "Calling $contactName ($contactNumber)...")
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Option 2: Call Police (112)
                    EmergencyActionButton(
                        icon = Icons.Default.LocalPolice,
                        title = if (isHindi) "पुलिस / राष्ट्रीय आपातकाल (112)" else strings.callPoliceHelpline,
                        subtitle = if (isHindi) "तत्काल पुलिस सहायता" else strings.policeHelplineSubtitle,
                        accentColor = Color(0xFFFF3B30),
                        feedbackText = feedbackMessage?.takeIf { it.contains("112") },
                        onClick = {
                            initiateCall("112", "Calling Police Helpline (112)...")
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Option 3: Call Ambulance (102)
                    EmergencyActionButton(
                        icon = Icons.Default.LocalHospital,
                        title = if (isHindi) "एम्बुलेंस सेवा (102)" else "Ambulance Emergency (102)",
                        subtitle = if (isHindi) "चिकित्सा आपातकाल सहायता" else "Direct medical ambulance dispatch",
                        accentColor = Color(0xFF007AFF),
                        feedbackText = feedbackMessage?.takeIf { it.contains("102") },
                        onClick = {
                            initiateCall("102", "Calling Ambulance (102)...")
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Option 4: Senior Citizen Helpline (14567)
                    EmergencyActionButton(
                        icon = Icons.Default.SupportAgent,
                        title = if (isHindi) "वरिष्ठ नागरिक हेल्पलाइन (14567)" else "Senior Citizen Helpline (14567)",
                        subtitle = if (isHindi) "सरकारी सहायता एवं मार्गदर्शन" else "National elder line support",
                        accentColor = Color(0xFFAF52DE),
                        feedbackText = feedbackMessage?.takeIf { it.contains("14567") },
                        onClick = {
                            initiateCall("14567", "Calling Elderline (14567)...")
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Option 5: Call Fire Department (101)
                    EmergencyActionButton(
                        icon = Icons.Default.LocalFireDepartment,
                        title = if (isHindi) "दमकल केंद्र (101)" else strings.callFireDepartment,
                        subtitle = if (isHindi) "आग व बचाव नियंत्रण कक्ष" else strings.fireHelplineSubtitle,
                        accentColor = Color(0xFFFF9500),
                        feedbackText = feedbackMessage?.takeIf { it.contains("101") },
                        onClick = {
                            initiateCall("101", "Calling Fire Control (101)...")
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Close button
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
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
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strings.close.uppercase(),
                                style = Typography.titleMedium.copy(
                                    color = Color.White,
                                    fontSize = 16.sp,
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
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "btnScale"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(18.dp),
        color = if (feedbackText != null) accentColor.copy(alpha = 0.2f) else Color(0xFF2C2C2E),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (feedbackText != null) accentColor else accentColor.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = Typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = feedbackText ?: subtitle,
                    style = Typography.bodyMedium.copy(
                        color = if (feedbackText != null) accentColor else Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        fontWeight = if (feedbackText != null) FontWeight.SemiBold else FontWeight.Normal
                    )
                )
            }
        }
    }
}
