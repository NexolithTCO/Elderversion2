package com.example.elderhelpprototypev01.ui.voice

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elderhelpprototypev01.ai.DoctorBookingManager
import com.example.elderhelpprototypev01.model.AssistantResponse
import com.example.elderhelpprototypev01.ui.theme.*

/**
 * ResponseCard
 *
 * Renders the assistant response strictly adhering to the Sahaay Voice Assistant specification:
 * 1. [Assistant Spoken Text]
 * 2. [Interactive Doctor Selection Cards - Rendered only when selecting a doctor]
 * 3. Horizontal divider
 * 4. Dedicated Action Controls: ONLY [ 🎙️ Speak ] and [ 🔊 Repeat Response ]
 */
@Composable
fun ResponseCard(
    response: AssistantResponse,
    isSpeaking: Boolean,
    onMicClick: () -> Unit,
    onRepeatClick: () -> Unit,
    onDoctorSelected: (String) -> Unit = {},
    userLanguage: String = "English",
    modifier: Modifier = Modifier
) {
    val isHindi = userLanguage.contains("Hindi") || userLanguage.contains("हिंदी")

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, AppleBorderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // ---- 1. Assistant Spoken Text ----
            Text(
                text = response.response,
                style = Typography.bodyLarge.copy(
                    color = AppleTextPrimary,
                    fontSize = 18.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.Normal
                )
            )

            // ---- 2. Interactive Doctor Selection Cards (Rendered only when selecting a doctor) ----
            val isDoctorSelection = response.intent == "BOOK_APPOINTMENT" && (
                response.goal.contains("Select doctor", ignoreCase = true) ||
                response.goal.contains("डॉक्टर चुनें", ignoreCase = true) ||
                response.response.contains("available", ignoreCase = true) ||
                response.response.contains("उपलब्ध हैं", ignoreCase = true) ||
                response.response.contains("Which doctor would you prefer", ignoreCase = true) ||
                response.response.contains("किसे चुनना चाहेंगे", ignoreCase = true)
            )

            if (isDoctorSelection) {
                val specialtyKey = when {
                    response.response.contains("Cardiologist", ignoreCase = true) || response.response.contains("हृदय", ignoreCase = true) -> "Cardiologist"
                    response.response.contains("Orthopedic", ignoreCase = true) || response.response.contains("हड्डी", ignoreCase = true) -> "Orthopedic"
                    response.response.contains("Eye", ignoreCase = true) || response.response.contains("Ophthalmologist", ignoreCase = true) || response.response.contains("नेत्र", ignoreCase = true) -> "Ophthalmologist"
                    response.response.contains("Neurologist", ignoreCase = true) || response.response.contains("न्यूरो", ignoreCase = true) -> "Neurologist"
                    else -> "General Physician"
                }

                val specialtyEntry = DoctorBookingManager.DOCTOR_DATABASE.find { it.key.equals(specialtyKey, ignoreCase = true) }
                    ?: DoctorBookingManager.DOCTOR_DATABASE.first()

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    specialtyEntry.doctors.forEachIndexed { index, doctor ->
                        val docName = if (isHindi) doctor.nameHi else doctor.nameEn
                        val docQual = if (isHindi) doctor.qualificationsHi else doctor.qualificationsEn

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFF7F9FC),
                            border = BorderStroke(1.dp, Color(0xFFD6E4FF)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onDoctorSelected(doctor.nameEn) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(AppleBlue.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = AppleBlue
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = docName,
                                        style = Typography.titleMedium.copy(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AppleTextPrimary
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = docQual,
                                        style = Typography.bodyMedium.copy(
                                            fontSize = 13.sp,
                                            color = AppleTextSecondary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ---- 3. Divider ----
            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = AppleBorderSubtle, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(14.dp))

            // ---- 4. Dedicated Action Controls: icon-only 🎙️ and 🔊 (no text labels per spec) ----
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 🎙️ icon-only mic button
                IconButton(
                    onClick = onMicClick,
                    modifier = Modifier
                        .size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Speak",
                        modifier = Modifier.size(28.dp),
                        tint = AppleBlue
                    )
                }

                Spacer(modifier = Modifier.width(32.dp))

                // 🔊 icon-only repeat button
                IconButton(
                    onClick = onRepeatClick,
                    modifier = Modifier
                        .size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Repeat Response",
                        modifier = Modifier.size(28.dp),
                        tint = AppleTextPrimary
                    )
                }
            }
        }
    }
}
