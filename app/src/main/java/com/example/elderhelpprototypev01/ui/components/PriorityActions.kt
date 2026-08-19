package com.example.elderhelpprototypev01.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elderhelpprototypev01.ai.DoctorBookingManager
import com.example.elderhelpprototypev01.ui.localization.Localization
import com.example.elderhelpprototypev01.ui.theme.*

/**
 * Segregated Doctor Appointments & Current Appointments Section
 *
 * Clearly displays the user's active confirmed appointment if one exists,
 * or provides a spacious, accessible booking card.
 */
@Composable
fun DoctorAppointmentsSection(
    appointment: DoctorBookingManager.BookingState? = null,
    currentLanguage: String = "English (India)",
    onDoctorClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isHindi = currentLanguage.contains("Hindi") || currentLanguage.contains("हिंदी")
    val hasAppointment = appointment?.specialty != null && appointment.isConfirmed

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1.0f,
        animationSpec = tween(120),
        label = "doctorCardScale"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (hasAppointment) {
                        if (isHindi) "मौजूदा अपॉइंटमेंट" else "Current Appointments"
                    } else {
                        if (isHindi) "डॉक्टर परामर्श" else "Doctor Consultations"
                    },
                    style = Typography.titleLarge.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (hasAppointment) {
                        if (isHindi) "आपकी आगामी क्लिनिक विज़िट" else "Your upcoming scheduled visit"
                    } else {
                        if (isHindi) "सत्यापित डॉक्टरों से परामर्श बुक करें" else "Book visits with verified specialists"
                    },
                    style = Typography.bodySmall.copy(
                        color = AppTextMuted,
                        fontSize = 13.sp
                    )
                )
            }

            if (hasAppointment) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFDCFCE7)
                ) {
                    Text(
                        text = if (isHindi) "✓ कन्फर्म" else "✓ Confirmed",
                        style = Typography.bodySmall.copy(
                            color = AppSuccess,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Main Card
        Surface(
            onClick = onDoctorClick,
            interactionSource = interactionSource,
            shape = RoundedCornerShape(16.dp),
            color = AppSurface,
            border = BorderStroke(1.dp, if (hasAppointment) Color(0xFFBBF7D0) else AppBorder),
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale)
                .semantics {
                    contentDescription = if (hasAppointment) {
                        "Current appointment for ${appointment?.specialty} at ${appointment?.location}, ${appointment?.dateTime}. Tap to view details."
                    } else {
                        "Book Doctor consultation. Tap to open doctor directory."
                    }
                }
        ) {
            if (hasAppointment) {
                // ACTIVE APPOINTMENT LISTING
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Top Row: Specialty + Clinic Location
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AppleBlueLight)
                        ) {
                            Text(text = "👨‍⚕️", fontSize = 24.sp)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = appointment?.specialty ?: "General Physician",
                                style = Typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AppTextPrimary,
                                    fontSize = 16.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = AppTextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = appointment?.location ?: "Bandra Medical Clinic",
                                    style = Typography.bodySmall.copy(
                                        color = AppTextSecondary,
                                        fontSize = 12.5.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = AppBorder, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Date & Time + Action Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f, fill = false),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = AppPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = appointment?.dateTime ?: "Tomorrow at 5:00 PM",
                                style = Typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppPrimary,
                                    fontSize = 13.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AppleBlueLight,
                            modifier = Modifier.clickable(onClick = onDoctorClick)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isHindi) "विवरण →" else "View Details →",
                                    style = Typography.labelSmall.copy(
                                        color = AppPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    ),
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
            } else {
                // NO APPOINTMENT — INVITATION TO BOOK CARD
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AppleBlueLight)
                    ) {
                        Text(text = "🩺", fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHindi) "डॉक्टर अपॉइंटमेंट बुक करें" else "Book Doctor Consultation",
                            style = Typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = AppTextPrimary,
                                fontSize = 15.5.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isHindi) "क्लिनिक विज़िट और विशेषज्ञ परामर्श" else "Cardiologist, Physician, Ortho & more",
                            style = Typography.bodySmall.copy(
                                color = AppTextSecondary,
                                fontSize = 12.5.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AppleBlueLight,
                        modifier = Modifier.clickable(onClick = onDoctorClick)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isHindi) "बुक करें →" else "Book →",
                                style = Typography.labelSmall.copy(
                                    color = AppPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp
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
 * Segregated Dedicated 24/7 Emergency Safety Banner
 */
@Composable
fun EmergencyQuickSection(
    currentLanguage: String = "English (India)",
    onEmergencyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isHindi = currentLanguage.contains("Hindi") || currentLanguage.contains("हिंदी")

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1.0f,
        animationSpec = tween(120),
        label = "emergencyCardScale"
    )

    Surface(
        onClick = onEmergencyClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(16.dp),
        color = SosRedBg,
        border = BorderStroke(1.dp, Color(0xFFFFCDD2)),
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .semantics {
                contentDescription = if (isHindi) "आपातकालीन सहायता। मदद पाने के लिए टैप करें।" else "24/7 Emergency Assistance. Tap for instant SOS call."
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFFCDD2))
            ) {
                Text(text = "🚨", fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isHindi) "24/7 आपातकालीन सहायता" else "24/7 Emergency Assistance",
                        style = Typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppEmergency,
                            fontSize = 15.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = if (isHindi) "परिवार एवं 112 पुलिस को तत्काल कॉल" else "Instant SOS call to family & services",
                    style = Typography.bodySmall.copy(
                        color = Color(0xFFB71C1C),
                        fontSize = 12.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AppEmergency
            ) {
                Text(
                    text = if (isHindi) "मदद लें →" else "SOS Help →",
                    style = Typography.labelSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * Backward compatibility alias for PriorityActions
 */
@Composable
fun PriorityActions(
    appointment: DoctorBookingManager.BookingState? = null,
    currentLanguage: String = "English (India)",
    onDoctorClick: () -> Unit,
    onEmergencyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DoctorAppointmentsSection(
            appointment = appointment,
            currentLanguage = currentLanguage,
            onDoctorClick = onDoctorClick,
            modifier = Modifier.fillMaxWidth()
        )

        EmergencyQuickSection(
            currentLanguage = currentLanguage,
            onEmergencyClick = onEmergencyClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
