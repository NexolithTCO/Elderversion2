package com.example.elderhelpprototypev01.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.elderhelpprototypev01.model.UserProfile
import com.example.elderhelpprototypev01.ui.theme.*

/**
 * PensionFormScreen
 *
 * Full-screen Pension Application Form pre-filled with data from UserProfile.
 * Auto-populates: Full Name, Phone Number, Address.
 * Additional pension-specific fields: Pensioner ID / Aadhaar, Pension Scheme Type.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PensionFormScreen(
    userProfile: UserProfile,
    currentLanguage: String = "English (India)",
    onDismiss: () -> Unit
) {
    val isHindi = currentLanguage.contains("Hindi") || currentLanguage.contains("हिंदी")

    // Pre-filled from UserProfile
    var fullName by remember { mutableStateOf(userProfile.fullName) }
    var phoneNumber by remember { mutableStateOf(userProfile.contactNumber) }
    var address by remember { mutableStateOf(userProfile.address) }

    // Pension-specific fields
    var aadhaarNumber by remember { mutableStateOf("") }
    var selectedScheme by remember { mutableStateOf<String?>(null) }

    // Submission state
    var isSubmitting by remember { mutableStateOf(false) }
    var isSubmitted by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val pensionSchemes = if (isHindi) listOf(
        "वृद्धावस्था पेंशन (Vriddha)",
        "विधवा पेंशन (Vidhwa)",
        "दिव्यांग पेंशन (Divyang)",
        "राष्ट्रीय सामाजिक सहायता"
    ) else listOf(
        "Old Age Pension (Vriddha)",
        "Widow Pension (Vidhwa)",
        "Disability Pension (Divyang)",
        "National Social Assistance"
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
            color = AppBackground
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
                                .background(TintGasBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📋", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isHindi) "पेंशन आवेदन पत्र" else "Pension Application",
                                style = Typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AppTextPrimary,
                                    fontSize = 19.sp
                                )
                            )
                            Text(
                                text = if (isHindi) "सरकारी कल्याणकारी योजना" else "Government Welfare Scheme",
                                style = Typography.bodySmall.copy(color = AppTextSecondary)
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

                Spacer(modifier = Modifier.height(18.dp))

                // Scrollable Form Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Success State
                    AnimatedVisibility(
                        visible = isSubmitted,
                        enter = fadeIn() + scaleIn(initialScale = 0.9f),
                        exit = fadeOut()
                    ) {
                        SubmissionSuccessCard(
                            applicantName = fullName,
                            scheme = selectedScheme ?: "Pension Scheme",
                            onDone = onDismiss
                        )
                    }

                    if (!isSubmitted) {
                        // Auto-fill notice banner
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFE8F5E9),
                            border = BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Fields pre-filled from your profile. Edit if needed.",
                                    style = Typography.bodySmall.copy(
                                        color = Color(0xFF2E7D32),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }

                        // Section: Personal Information (Pre-filled)
                        FormSection(title = "PERSONAL DETAILS", emoji = "👤") {
                            FormField(
                                label = "Full Name",
                                value = fullName,
                                onValueChange = { fullName = it },
                                placeholder = "e.g. Ramesh Sharma",
                                isAutoFilled = true
                            )

                            FormField(
                                label = "Phone Number",
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                placeholder = "e.g. +91 98765 12345",
                                isAutoFilled = true
                            )

                            FormField(
                                label = "Residential Address",
                                value = address,
                                onValueChange = { address = it },
                                placeholder = "e.g. Bandra West, Mumbai",
                                isAutoFilled = true,
                                maxLines = 3
                            )
                        }

                        // Section: Pension Details
                        FormSection(title = "PENSION DETAILS", emoji = "📄") {
                            FormField(
                                label = "Pensioner ID / Aadhaar Number",
                                value = aadhaarNumber,
                                onValueChange = { aadhaarNumber = it },
                                placeholder = "Enter 12-digit Aadhaar number",
                                isAutoFilled = false
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Pension Scheme Type Selector
                            Text(
                                text = "Pension Scheme Type",
                                style = Typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AppleTextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                pensionSchemes.forEach { scheme ->
                                    val isSelected = selectedScheme == scheme
                                    Surface(
                                        onClick = { selectedScheme = scheme },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) AppleBlue.copy(alpha = 0.08f) else Color.White,
                                        border = BorderStroke(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) AppleBlue else AppleBorderSubtle
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 14.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = { selectedScheme = scheme },
                                                colors = RadioButtonDefaults.colors(
                                                    selectedColor = AppleBlue
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = scheme,
                                                style = Typography.bodyMedium.copy(
                                                    color = if (isSelected) AppleBlue else AppleTextPrimary,
                                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                                    fontSize = 14.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                isSubmitting = true
                                isSubmitted = true
                                isSubmitting = false
                            },
                            enabled = !isSubmitting
                                && fullName.isNotBlank()
                                && aadhaarNumber.length >= 12
                                && selectedScheme != null,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Submitting...", fontWeight = FontWeight.Bold)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Submit Application",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Helper Composables
// ─────────────────────────────────────────────────────────────────

@Composable
private fun FormSection(
    title: String,
    emoji: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, AppleBorderSubtle)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(emoji, fontSize = 15.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = Typography.labelMedium.copy(
                        color = AppleBlue,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 12.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                content = content
            )
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isAutoFilled: Boolean,
    maxLines: Int = 1
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = Typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = AppleTextPrimary
                )
            )
            if (isAutoFilled) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF34C759).copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "Auto-filled",
                        style = Typography.labelSmall.copy(
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = AppleTextMuted) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            maxLines = maxLines,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = AppleTextPrimary,
                unfocusedTextColor = AppleTextPrimary,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = if (isAutoFilled) Color(0xFF34C759) else AppleBlue,
                unfocusedBorderColor = if (isAutoFilled) Color(0xFF34C759).copy(alpha = 0.5f) else AppleBorderSubtle
            )
        )
    }
}

@Composable
private fun SubmissionSuccessCard(
    applicantName: String,
    scheme: String,
    onDone: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFF34C759).copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Application Submitted!",
                style = Typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    fontSize = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Your pension application for\n\"$scheme\"\nhas been submitted for $applicantName.",
                style = Typography.bodyMedium.copy(
                    color = AppleTextSecondary,
                    fontSize = 14.sp
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF3E5F5)
            ) {
                Text(
                    text = "Ref No: PEN-${System.currentTimeMillis() % 100000}",
                    style = Typography.labelMedium.copy(
                        color = Color(0xFF6A1B9A),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDone,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Done", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
