package com.example.elderhelpprototypev01.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elderhelpprototypev01.model.UserProfile
import com.example.elderhelpprototypev01.ui.localization.Localization
import com.example.elderhelpprototypev01.ui.theme.*

/**
 * 11. SETTINGS (Page 9, 10)
 *
 * iOS/Modern system-inspired grouped settings architecture:
 * - Header: "Settings / Preferences & Emergency Setup" (Not in a card)
 * - ACCESSIBILITY & VOICE
 * - PERSONAL
 * - EMERGENCY (subtle red accent)
 * - ACCOUNT
 * - SUPPORT & ABOUT
 *
 * Consistent 40-44px icon containers, subtle separators, uppercase section labels.
 */
@Composable
fun SettingsScreen(
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    userProfile: UserProfile = UserProfile(),
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
            .background(AppBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 40.dp)
        ) {
            // Header (Not in a card)
            Text(
                text = strings.settingsTitle,
                style = Typography.headlineLarge.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTextPrimary
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (isHindi) "प्राथमिकताएं और आपातकालीन सेटअप" else "Preferences & Emergency Setup",
                style = Typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    color = AppTextSecondary
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section 1: ACCESSIBILITY & VOICE
            SettingsSectionHeader(title = if (isHindi) "सुगमता एवं आवाज़" else "ACCESSIBILITY & VOICE")

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = AppSurface,
                border = BorderStroke(1.dp, AppBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
                    SettingsToggleRow(
                        title = "Large Readability",
                        subtitle = "Increased font contrast",
                        icon = Icons.Default.FormatSize,
                        iconBg = AppleBlueLight,
                        iconTint = AppPrimary,
                        checked = highContrastText,
                        onCheckedChange = {
                            highContrastText = it
                            Toast.makeText(context, "Readability updated", Toast.LENGTH_SHORT).show()
                        }
                    )
                    HorizontalDivider(color = AppBorder, thickness = 0.8.dp, modifier = Modifier.padding(start = 54.dp))
                    SettingsToggleRow(
                        title = "Voice Speech Feedback",
                        subtitle = "Speak confirmations",
                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                        iconBg = TintMobileBg,
                        iconTint = TintMobileIcon,
                        checked = voiceFeedback,
                        onCheckedChange = {
                            voiceFeedback = it
                            Toast.makeText(context, "Voice feedback updated", Toast.LENGTH_SHORT).show()
                        }
                    )
                    HorizontalDivider(color = AppBorder, thickness = 0.8.dp, modifier = Modifier.padding(start = 54.dp))
                    SettingsToggleRow(
                        title = "Simplified Easy Mode",
                        subtitle = "Reduce interface complexity",
                        icon = Icons.Default.TouchApp,
                        iconBg = Color(0xFFDCFCE7),
                        iconTint = AppSuccess,
                        checked = simpleMode,
                        onCheckedChange = {
                            simpleMode = it
                            Toast.makeText(context, "Simple mode toggled", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 2: PERSONAL
            SettingsSectionHeader(title = if (isHindi) "व्यक्तिगत" else "PERSONAL")

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = AppSurface,
                border = BorderStroke(1.dp, AppBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
                    SettingsNavRow(
                        title = strings.prefLanguageTitle,
                        subtitle = currentLanguage,
                        icon = Icons.Default.Language,
                        iconBg = AppleBlueLight,
                        iconTint = AppPrimary,
                        onClick = { showLanguageDialog = true }
                    )
                    HorizontalDivider(color = AppBorder, thickness = 0.8.dp, modifier = Modifier.padding(start = 54.dp))
                    SettingsNavRow(
                        title = "Personal Profile",
                        subtitle = if (userProfile.fullName.isNotBlank()) userProfile.fullName else "Tap to edit details",
                        icon = Icons.Default.Person,
                        iconBg = AppleBlueLight,
                        iconTint = AppPrimary,
                        onClick = { onEditProfileClick?.invoke() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 3: EMERGENCY (Subtle red accent)
            SettingsSectionHeader(title = if (isHindi) "आपातकालीन संपर्क" else "EMERGENCY")

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = AppSurface,
                border = BorderStroke(1.dp, AppBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
                    val emergencyDisplay = if (userProfile.emergencyContactPhone.isNotBlank()) {
                        "${userProfile.emergencyContactDisplayName} • ${userProfile.emergencyContactPhone}"
                    } else {
                        if (isHindi) "कोई संपर्क सेट नहीं" else "No emergency contact set"
                    }
                    SettingsNavRow(
                        title = "Emergency Contact",
                        subtitle = emergencyDisplay,
                        icon = Icons.Default.ContactPhone,
                        iconBg = SosRedBg,
                        iconTint = AppEmergency,
                        onClick = {
                            if (onEditProfileClick != null) {
                                onEditProfileClick()
                            } else {
                                Toast.makeText(context, "Emergency Contact: $emergencyDisplay", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 4: ACCOUNT
            SettingsSectionHeader(title = if (isHindi) "खाता एवं ऑनबोर्डिंग" else "ACCOUNT")

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = AppSurface,
                border = BorderStroke(1.dp, AppBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
                    SettingsNavRow(
                        title = "Relive Login Page",
                        subtitle = "Re-open login & signup",
                        icon = Icons.Default.LockReset,
                        iconBg = TintGasBg,
                        iconTint = TintGasIcon,
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

            Spacer(modifier = Modifier.height(24.dp))

            // Section 5: SUPPORT & ABOUT
            SettingsSectionHeader(title = if (isHindi) "सहायता एवं परिचय" else "SUPPORT & ABOUT")

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = AppSurface,
                border = BorderStroke(1.dp, AppBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
                    SettingsNavRow(
                        title = "How to Use",
                        subtitle = "1-min audio walkthrough guide",
                        icon = Icons.AutoMirrored.Filled.Help,
                        iconBg = TintMobileBg,
                        iconTint = TintMobileIcon,
                        onClick = {
                            Toast.makeText(context, "Audio guide clicked", Toast.LENGTH_SHORT).show()
                        }
                    )
                    HorizontalDivider(color = AppBorder, thickness = 0.8.dp, modifier = Modifier.padding(start = 54.dp))
                    SettingsNavRow(
                        title = "About Sahaay",
                        subtitle = "V0.1.0 • Hackathon Prototype",
                        icon = Icons.Default.Info,
                        iconBg = Color(0xFFDCFCE7),
                        iconTint = AppSuccess,
                        onClick = {
                            Toast.makeText(context, "Sahaay Elder Care V0.1.0 Prototype", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // Language Dialog
        if (showLanguageDialog) {
            AlertDialog(
                onDismissRequest = { showLanguageDialog = false },
                title = {
                    Text(
                        text = "Select Language",
                        style = Typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = AppTextPrimary
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
                                    colors = RadioButtonDefaults.colors(selectedColor = AppPrimary)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = language,
                                    style = Typography.bodyLarge.copy(
                                        fontSize = 16.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) AppPrimary else AppTextPrimary
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
                            color = AppPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = AppSurface
            )
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = Typography.bodySmall.copy(
            color = AppTextMuted,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            fontSize = 11.5.sp
        ),
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = Typography.titleMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTextPrimary
                )
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = subtitle,
                style = Typography.bodySmall.copy(
                    fontSize = 12.5.sp,
                    color = AppTextSecondary
                )
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AppPrimary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFCBD5E1)
            )
        )
    }
}

@Composable
fun SettingsNavRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = Typography.titleMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTextPrimary
                )
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = subtitle,
                style = Typography.bodySmall.copy(
                    fontSize = 12.5.sp,
                    color = AppTextSecondary
                ),
                maxLines = 1
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = AppTextMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}
