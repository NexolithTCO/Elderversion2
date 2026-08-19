package com.example.elderhelpprototypev01.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elderhelpprototypev01.model.EmergencyContact
import com.example.elderhelpprototypev01.model.UserProfile
import com.example.elderhelpprototypev01.overlay.SahaayPreferences
import com.example.elderhelpprototypev01.ui.localization.Localization
import com.example.elderhelpprototypev01.ui.theme.*

/**
 * LoginSignupScreen
 *
 * Collects a full user profile on first launch:
 *  - Full Name, Phone Number, Primary Address
 *  - Emergency Contact: Name, Relationship, Phone Number
 *
 * On "Start Using Sahaay":
 *  1. Saves all fields to SharedPreferences (persistence across process death)
 *  2. Calls [onProfileSaved] with the constructed [UserProfile] for immediate in-memory use
 *  3. Marks onboarding complete so this screen is never shown again
 */
@Composable
fun LoginSignupScreen(
    language: String = "English (India)",
    onProfileSaved: (UserProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strings = Localization.getStrings(language)
    val isHindi = language.contains("Hindi") || language.contains("हिंदी")
    val scrollState = rememberScrollState()

    // Form state
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var emergencyName by remember { mutableStateOf("") }
    var emergencyRelation by remember { mutableStateOf("") }
    var emergencyPhone by remember { mutableStateOf("") }

    val quickRelationships = if (isHindi) {
        listOf("बेटा", "बेटी", "पति", "पत्नी", "भाई", "बहन", "केयरगिवर")
    } else {
        listOf("Son", "Daughter", "Husband", "Wife", "Brother", "Sister", "Caregiver")
    }

    val isFormValid = fullName.isNotBlank() && phone.isNotBlank()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppleCanvasBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 120.dp)
        ) {
            // ── Header ────────────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(AppleBlueLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🙏", fontSize = 26.sp)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = strings.signupTitle,
                        style = Typography.headlineMedium.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppleTextPrimary
                        )
                    )
                    Text(
                        text = strings.signupSubtitle,
                        style = Typography.bodySmall.copy(
                            color = AppleTextMuted,
                            fontSize = 13.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Personal Information ───────────────────────────────────────────
            SignupSectionCard(
                emoji = "👤",
                title = strings.personalInfoSection.uppercase(),
                action = {
                    Surface(
                        onClick = {
                            fullName = "Melith"
                            phone = "9372552738"
                            address = "Earth"
                            emergencyName = "6383165097"
                            emergencyRelation = "Son"
                            emergencyPhone = "99523672234"
                            Toast.makeText(context, "Filled demo details into text boxes!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = AppleBlue,
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⚡", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "EXD",
                                style = Typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            ) {
                SignupField(
                    label = strings.fieldFullName,
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = strings.fieldFullNamePlaceholder,
                    leadingIcon = Icons.Default.Person,
                    isRequired = true
                )
                Spacer(modifier = Modifier.height(14.dp))
                SignupField(
                    label = strings.fieldPhone,
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = strings.fieldPhonePlaceholder,
                    leadingIcon = Icons.Default.Phone,
                    isRequired = true
                )
                Spacer(modifier = Modifier.height(14.dp))
                SignupField(
                    label = strings.fieldAddress,
                    value = address,
                    onValueChange = { address = it },
                    placeholder = strings.fieldAddressPlaceholder,
                    leadingIcon = Icons.Default.Home,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Emergency Contact ─────────────────────────────────────────────
            SignupSectionCard(
                emoji = "🚨",
                title = strings.emergencyContactSection.uppercase(),
                accentColor = Color(0xFFFF3B30)
            ) {
                // Info banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFF3B30).copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, Color(0xFFFF3B30).copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFFF3B30),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi)
                                "SOS और वॉयस इमरजेंसी के लिए उपयोग किया जाएगा"
                            else
                                "Used for SOS alerts and voice emergency calls",
                            style = Typography.bodySmall.copy(
                                color = Color(0xFFFF3B30),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                SignupField(
                    label = strings.fieldEmergencyName,
                    value = emergencyName,
                    onValueChange = { emergencyName = it },
                    placeholder = strings.fieldEmergencyNamePlaceholder,
                    leadingIcon = Icons.Default.PersonPin
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Relationship quick chips
                Text(
                    text = strings.fieldEmergencyRelation,
                    style = Typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppleTextPrimary,
                        fontSize = 14.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    quickRelationships.take(4).forEach { rel ->
                        FilterChip(
                            selected = emergencyRelation == rel,
                            onClick = { emergencyRelation = rel },
                            label = { Text(rel, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFF3B30).copy(alpha = 0.12f),
                                selectedLabelColor = Color(0xFFFF3B30)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = emergencyRelation,
                    onValueChange = { emergencyRelation = it },
                    placeholder = {
                        Text(
                            strings.fieldEmergencyRelationPlaceholder,
                            color = AppleTextMuted,
                            fontSize = 14.sp
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Default.FamilyRestroom,
                            contentDescription = null,
                            tint = AppleTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                SignupField(
                    label = strings.fieldEmergencyPhone,
                    value = emergencyPhone,
                    onValueChange = { emergencyPhone = it },
                    placeholder = strings.fieldEmergencyPhonePlaceholder,
                    leadingIcon = Icons.Default.PhoneInTalk
                )
            }
        }

        // ── Fixed bottom CTA ─────────────────────────────────────────────────
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = AppleCanvasBg,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Button(
                    onClick = {
                        val contact = EmergencyContact(
                            name = emergencyName.trim(),
                            relationship = emergencyRelation.trim(),
                            phone = emergencyPhone.trim()
                        )
                        val profile = UserProfile(
                            fullName = fullName.trim(),
                            age = "",
                            contactNumber = phone.trim(),
                            emergencyContactName = emergencyName.trim(),
                            emergencyContactRelationship = emergencyRelation.trim(),
                            emergencyContactPhone = emergencyPhone.trim(),
                            address = address.trim(),
                            emergencyContacts = if (emergencyName.isNotBlank()) listOf(contact) else emptyList()
                        )
                        // Persist to SharedPreferences
                        SahaayPreferences.saveProfile(
                            context = context,
                            fullName = profile.fullName,
                            age = profile.age,
                            contactNumber = profile.contactNumber,
                            address = profile.address,
                            emergencyName = profile.emergencyContactName,
                            emergencyRelationship = profile.emergencyContactRelationship,
                            emergencyPhone = profile.emergencyContactPhone
                        )
                        SahaayPreferences.markOnboardingComplete(context)
                        onProfileSaved(profile)
                    },
                    enabled = isFormValid,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppleBlue,
                        disabledContainerColor = AppleBorderSubtle
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = strings.createAccountBtn,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
                if (!isFormValid) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isHindi) "* पूरा नाम और फ़ोन नंबर ज़रूरी है" else "* Full Name and Phone Number are required",
                        style = Typography.bodySmall.copy(
                            color = Color(0xFFFF3B30),
                            fontSize = 12.sp
                        ),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

// ── Helper composables ────────────────────────────────────────────────────────

@Composable
private fun SignupSectionCard(
    emoji: String,
    title: String,
    accentColor: Color = AppleBlue,
    action: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, AppleBorderSubtle)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(emoji, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = Typography.labelMedium.copy(
                            color = accentColor,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 12.sp
                        )
                    )
                }
                if (action != null) {
                    action()
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun SignupField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isRequired: Boolean = false,
    maxLines: Int = 1
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = Typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = AppleTextPrimary,
                    fontSize = 14.sp
                )
            )
            if (isRequired) {
                Text(
                    text = " *",
                    style = Typography.labelLarge.copy(
                        color = Color(0xFFFF3B30),
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = AppleTextMuted, fontSize = 14.sp) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            maxLines = maxLines,
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = AppleTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        )
    }
}
