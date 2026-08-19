package com.example.elderhelpprototypev01.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elderhelpprototypev01.ui.localization.Localization
import com.example.elderhelpprototypev01.ui.theme.*

@Composable
fun SettingsScreen(
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    userProfile: com.example.elderhelpprototypev01.model.UserProfile = com.example.elderhelpprototypev01.model.UserProfile(),
    onEditProfileClick: (() -> Unit)? = null,
    onReliveLoginClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val strings = Localization.getStrings(currentLanguage)
    val isHindi = currentLanguage.contains("Hindi")

    var highContrastText by remember { mutableStateOf(true) }
    var voiceFeedback by remember { mutableStateOf(true) }
    var simpleMode by remember { mutableStateOf(false) }

    var showLanguageDialog by remember { mutableStateOf(false) }

    val languages = listOf(
        "English (India)",
        "Hindi (हिंदी)",
        "Marathi (मराठी)",
        "Tamil (தமிழ்)",
        "Telugu (తెలుగు)",
        "Bengali (বাংলা)"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppleCanvasBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp)
                .padding(top = 20.dp, bottom = 36.dp)
        ) {
            // Header
            Text(
                text = strings.settingsTitle,
                style = Typography.headlineLarge.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppleTextPrimary
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (isHindi) "सरल नियंत्रण, सुरक्षा और भाषा सेटिंग्स" else "Accessibility, safety & language preferences",
                style = Typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    color = AppleTextMuted
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // User Identity Profile Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEditProfileClick?.invoke() },
                shape = RoundedCornerShape(22.dp),
                color = AppleSurfaceWhite,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, AppleBorderSubtle)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(AppleBlueLight)
                            .border(1.5.dp, AppleBlueSubtle, CircleShape)
                    ) {
                        Text(
                            text = if (userProfile.fullName.isNotBlank()) userProfile.fullName.take(1).uppercase() else "👤",
                            style = Typography.titleLarge.copy(
                                color = AppleBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (userProfile.fullName.isNotBlank()) userProfile.fullName else "Senior User",
                            style = Typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AppleTextPrimary,
                                fontSize = 17.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (userProfile.contactNumber.isNotBlank()) userProfile.contactNumber else "Tap to complete profile",
                            style = Typography.bodySmall.copy(
                                color = AppleTextMuted,
                                fontSize = 13.sp
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AppleBlueLight
                    ) {
                        Text(
                            text = "Edit",
                            style = Typography.labelMedium.copy(
                                color = AppleBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Section 1: Accessibility & Voice
            SettingsSectionTitle(title = if (isHindi) "सुगमता एवं आवाज़" else "ACCESSIBILITY & VOICE")

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = AppleSurfaceWhite,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, AppleBorderSubtle)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    SettingsSwitchRow(
                        title = "Large Clear Fonts",
                        subtitle = "Crisp contrast for comfortable reading",
                        icon = Icons.Default.FormatSize,
                        iconTint = Color(0xFF0284C7),
                        iconBg = Color(0xFFE0F2FE),
                        checked = highContrastText,
                        onCheckedChange = {
                            highContrastText = it
                            Toast.makeText(context, "Readability updated", Toast.LENGTH_SHORT).show()
                        }
                    )
                    HorizontalDivider(color = AppleBorderSubtle, thickness = 0.8.dp)
                    SettingsSwitchRow(
                        title = "Voice Speech Feedback",
                        subtitle = "Read aloud button actions & confirmations",
                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                        iconTint = Color(0xFF7C3AED),
                        iconBg = Color(0xFFF5F3FF),
                        checked = voiceFeedback,
                        onCheckedChange = {
                            voiceFeedback = it
                            Toast.makeText(context, "Voice feedback updated", Toast.LENGTH_SHORT).show()
                        }
                    )
                    HorizontalDivider(color = AppleBorderSubtle, thickness = 0.8.dp)
                    SettingsSwitchRow(
                        title = "Simplified Easy Mode",
                        subtitle = "Enlarge touch targets & reduce options",
                        icon = Icons.Default.TouchApp,
                        iconTint = Color(0xFF059669),
                        iconBg = Color(0xFFECFDF5),
                        checked = simpleMode,
                        onCheckedChange = {
                            simpleMode = it
                            Toast.makeText(context, "Simple mode toggled", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Section 2: Personal & Safety Contacts
            SettingsSectionTitle(title = if (isHindi) "व्यक्तिगत एवं आपातकालीन सुरक्षा" else "PERSONAL & SAFETY")

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = AppleSurfaceWhite,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, AppleBorderSubtle)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    val emergencyDisplay = if (userProfile.emergencyContactPhone.isNotBlank()) {
                        "${userProfile.emergencyContactDisplayName} • ${userProfile.emergencyContactPhone}"
                    } else {
                        if (isHindi) "कोई संपर्क सेट नहीं (जोड़ें)" else "No contact set (tap to add)"
                    }
                    SettingsNavigationRow(
                        title = if (isHindi) "आपातकालीन संपर्क" else "Emergency Safety Contact",
                        value = emergencyDisplay,
                        icon = Icons.Default.ContactPhone,
                        iconTint = SosRedIcon,
                        iconBg = SosRedBg,
                        onClick = {
                            if (onEditProfileClick != null) {
                                onEditProfileClick()
                            } else {
                                Toast.makeText(context, "Emergency Contact: $emergencyDisplay", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    HorizontalDivider(color = AppleBorderSubtle, thickness = 0.8.dp)
                    SettingsNavigationRow(
                        title = strings.prefLanguageTitle,
                        value = currentLanguage,
                        icon = Icons.Default.Language,
                        iconTint = AppleBlue,
                        iconBg = AppleBlueLight,
                        onClick = {
                            showLanguageDialog = true
                        }
                    )
                    HorizontalDivider(color = AppleBorderSubtle, thickness = 0.8.dp)
                    SettingsNavigationRow(
                        title = if (isHindi) "शुरुआती सेटअप दोबारा देखें" else "Relive Onboarding Flow",
                        value = if (isHindi) "भाषा व विवरण बदलें" else "Re-open language & login setup",
                        icon = Icons.Default.LockReset,
                        iconTint = Color(0xFFD97706),
                        iconBg = Color(0xFFFFFBEB),
                        onClick = {
                            if (onReliveLoginClick != null) {
                                onReliveLoginClick()
                            } else {
                                Toast.makeText(context, "Opening Login Page...", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Section 3: App Information & Support
            SettingsSectionTitle(title = if (isHindi) "सहायता एवं परिचय" else "HELP & ABOUT")

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = AppleSurfaceWhite,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, AppleBorderSubtle)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    SettingsNavigationRow(
                        title = "How to Use (1-Min Audio Guide)",
                        value = "Listen to quick walkthrough",
                        icon = Icons.AutoMirrored.Filled.Help,
                        iconTint = Color(0xFF7C3AED),
                        iconBg = Color(0xFFF5F3FF),
                        onClick = {
                            Toast.makeText(context, "Audio guide clicked", Toast.LENGTH_SHORT).show()
                        }
                    )
                    HorizontalDivider(color = AppleBorderSubtle, thickness = 0.8.dp)
                    SettingsNavigationRow(
                        title = "About Sahaay Elder Care",
                        value = "V0.1.0 • Hackathon Prototype",
                        icon = Icons.Default.Info,
                        iconTint = Color(0xFF059669),
                        iconBg = Color(0xFFECFDF5),
                        onClick = {
                            Toast.makeText(context, "Sahaay Elder Care V0.1.0 Prototype", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // Language Selection Dialog
        if (showLanguageDialog) {
            AlertDialog(
                onDismissRequest = { showLanguageDialog = false },
                title = {
                    Text(
                        text = "Select Language",
                        style = Typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = AppleTextPrimary
                        )
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .selectableGroup()
                            .fillMaxWidth()
                    ) {
                        languages.forEach { language ->
                            val isSelected = language == currentLanguage
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .selectable(
                                        selected = isSelected,
                                        onClick = {
                                            onLanguageChange(language)
                                            showLanguageDialog = false
                                            Toast.makeText(
                                                context,
                                                "Language set to $language",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        role = Role.RadioButton
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors(selectedColor = AppleBlue)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = language,
                                    style = Typography.bodyLarge.copy(
                                        fontSize = 16.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) AppleBlue else AppleTextPrimary
                                    )
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLanguageDialog = false }) {
                        Text(
                            text = "Cancel",
                            color = AppleBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                shape = RoundedCornerShape(24.dp),
                containerColor = AppleSurfaceWhite
            )
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = Typography.labelMedium.copy(
            color = AppleTextMuted,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            fontSize = 12.sp
        ),
        modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color = AppleBlue,
    iconBg: Color = AppleBlueLight,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = Typography.titleMedium.copy(
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppleTextPrimary
                )
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = subtitle,
                style = Typography.bodySmall.copy(
                    fontSize = 12.5.sp,
                    color = AppleTextMuted
                )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AppleBlue,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFCBD5E1)
            )
        )
    }
}

@Composable
fun SettingsNavigationRow(
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color = iconTint.copy(alpha = 0.12f),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = Typography.titleMedium.copy(
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppleTextPrimary
                )
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = value,
                style = Typography.bodySmall.copy(
                    fontSize = 12.5.sp,
                    color = AppleTextMuted
                ),
                maxLines = 1
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = AppleTextMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    ElderHelpPrototypeV01Theme {
        SettingsScreen(
            currentLanguage = "Hindi (हिंदी)",
            onLanguageChange = {}
        )
    }
}
