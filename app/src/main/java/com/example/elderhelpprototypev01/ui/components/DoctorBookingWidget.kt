package com.example.elderhelpprototypev01.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
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
import com.example.elderhelpprototypev01.ai.DoctorBookingManager
import com.example.elderhelpprototypev01.ui.theme.*

/**
 * DoctorBookingWidget
 *
 * Clickable Quick Task Widget displayed on the Home dashboard.
 * Shows active / booked appointment details if present, or an invite to book.
 * Clicking anywhere on the card opens the full [DoctorAppointmentModal].
 */
@Composable
fun DoctorBookingWidget(
    appointment: DoctorBookingManager.BookingState?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, AppleBorderSubtle)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DoctorBlueBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = "Doctor Booking",
                            tint = DoctorBlueIcon,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Doctor Booking",
                            style = Typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AppleTextPrimary,
                                fontSize = 17.sp
                            )
                        )
                        Text(
                            text = if (appointment?.specialty != null) "Active Appointment" else "Book clinical & online visits",
                            style = Typography.bodySmall.copy(
                                color = if (appointment?.specialty != null) Color(0xFF34C759) else AppleTextMuted,
                                fontSize = 13.sp,
                                fontWeight = if (appointment?.specialty != null) FontWeight.SemiBold else FontWeight.Normal
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AppleBlueLight
                ) {
                    Text(
                        text = if (appointment?.specialty != null) "View Details" else "Book Now",
                        style = Typography.labelMedium.copy(
                            color = AppleBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = AppleBorderSubtle, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Details Content
            if (appointment?.specialty != null) {
                // Active Appointment Preview
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("👨‍⚕️", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = appointment.specialty ?: "General Physician",
                            style = Typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AppleTextPrimary,
                                fontSize = 15.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = CircleShape,
                            color = if (appointment.mode?.contains("online", ignoreCase = true) == true) Color(0xFFE8F5E9) else Color(0xFFEDE7F6)
                        ) {
                            Text(
                                text = if (appointment.mode?.contains("online", ignoreCase = true) == true) "Online" else "In-Person",
                                style = Typography.labelSmall.copy(
                                    color = if (appointment.mode?.contains("online", ignoreCase = true) == true) Color(0xFF2E7D32) else Color(0xFF512DA8),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    if (!appointment.location.isNullOrBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📍", fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = appointment.location ?: "",
                                style = Typography.bodyMedium.copy(
                                    color = AppleTextSecondary,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }

                    if (!appointment.dateTime.isNullOrBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🕒", fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = appointment.dateTime ?: "",
                                style = Typography.bodyMedium.copy(
                                    color = AppleBlue,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }
                }
            } else {
                // Empty state prompt
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "No active booking. Tap to schedule a visit or ask via voice.",
                        style = Typography.bodyMedium.copy(
                            color = AppleTextSecondary,
                            fontSize = 14.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Open",
                        tint = AppleTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

/**
 * DoctorAppointmentModal
 *
 * Full detail modal displaying:
 * 1. Active / Booked appointment details captured during voice or manual flow.
 * 2. Form to "Book Appointment Manually" without voice.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorAppointmentModal(
    appointment: DoctorBookingManager.BookingState?,
    initialTab: Int? = null,
    onDismiss: () -> Unit,
    onBookManual: (DoctorBookingManager.BookingState) -> Unit,
    onCancelAppointment: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(initialTab ?: if (appointment?.specialty != null) 0 else 1) }

    // Manual Form State
    var specialty by remember { mutableStateOf(appointment?.specialty ?: "General Physician") }
    var location by remember { mutableStateOf(appointment?.location ?: "Bandra West, Mumbai") }
    var dateTime by remember { mutableStateOf(appointment?.dateTime ?: "Tomorrow at 5:00 PM") }
    var mode by remember { mutableStateOf(appointment?.mode ?: "in-person clinic visit") }
    var showSuccessToast by remember { mutableStateOf(false) }

    val specialties = listOf(
        "General Physician", "Dermatologist", "Cardiologist",
        "Pediatrician", "Dentist", "Orthopedic", "Eye Specialist", "ENT Specialist"
    )

    val quickTimes = listOf(
        "Today at 4:00 PM", "Tomorrow at 10:00 AM", "Tomorrow at 5:00 PM", "Next Monday 11:00 AM"
    )

    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = AppleCanvasBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Modal Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Doctor Appointments",
                            style = Typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = AppleTextPrimary,
                                fontSize = 22.sp
                            )
                        )
                        Text(
                            text = "Manage or schedule healthcare visits",
                            style = Typography.bodySmall.copy(color = AppleTextMuted)
                        )
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

                Spacer(modifier = Modifier.height(16.dp))

                // Navigation Tabs
                PrimaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = AppleSurfaceWhite,
                    contentColor = AppleBlue,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, AppleBorderSubtle, RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "Active Booking",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "Book Manually",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                    if (selectedTab == 0) {
                        // TAB 0: Active / Booked Appointment Details
                        if (appointment?.specialty != null) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White,
                                shadowElevation = 2.dp,
                                border = BorderStroke(1.dp, AppleBorderSubtle)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFFE8F5E9)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("✅", fontSize = 12.sp)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = if (appointment.isConfirmed) "CONFIRMED" else "IN PROGRESS",
                                                    style = Typography.labelSmall.copy(
                                                        color = Color(0xFF2E7D32),
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp
                                                    )
                                                )
                                            }
                                        }

                                        Text(
                                            text = "Booking ID: #SAH-8821",
                                            style = Typography.bodySmall.copy(
                                                color = AppleTextMuted,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    DetailItemRow(label = "Doctor Specialty", value = appointment.specialty ?: "General Physician", emoji = "🩺")
                                    HorizontalDivider(color = AppleBorderSubtle, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                                    DetailItemRow(label = "Location / Clinic", value = appointment.location ?: "Central Medical Clinic", emoji = "📍")
                                    HorizontalDivider(color = AppleBorderSubtle, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                                    DetailItemRow(label = "Preferred Date & Time", value = appointment.dateTime ?: "Tomorrow at 5:00 PM", emoji = "🕒")
                                    HorizontalDivider(color = AppleBorderSubtle, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                                    DetailItemRow(
                                        label = "Consultation Mode",
                                        value = if (appointment.mode?.contains("online", ignoreCase = true) == true) "Online Consultation (Video/Call)" else "In-Person Clinic Visit",
                                        emoji = if (appointment.mode?.contains("online", ignoreCase = true) == true) "💻" else "🏥"
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    // Cancel Button
                                    OutlinedButton(
                                        onClick = {
                                            onCancelAppointment()
                                            selectedTab = 1
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF3B30)),
                                        border = BorderStroke(1.dp, Color(0xFFFF3B30).copy(alpha = 0.5f)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Cancel This Appointment", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        } else {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, AppleBorderSubtle)
                            ) {
                                Column(
                                    modifier = Modifier.padding(28.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("📋", fontSize = 42.sp)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "No Active Appointment",
                                        style = Typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = AppleTextPrimary
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "You do not have any upcoming doctor appointments. Use the Manual form or speak to Sahaay to book one.",
                                        style = Typography.bodyMedium.copy(
                                            color = AppleTextSecondary,
                                            fontSize = 14.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(18.dp))
                                    Button(
                                        onClick = { selectedTab = 1 },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = AppleBlue)
                                    ) {
                                        Text("Book Manually Now", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    } else {
                        // TAB 1: Book Appointment Manually Form
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White,
                            shadowElevation = 2.dp,
                            border = BorderStroke(1.dp, AppleBorderSubtle)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Book Appointment Manually",
                                    style = Typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AppleTextPrimary,
                                        fontSize = 18.sp
                                    )
                                )
                                Text(
                                    text = "Fill in the details below to schedule your visit directly.",
                                    style = Typography.bodySmall.copy(color = AppleTextMuted)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // 1. Doctor Specialty
                                Text(
                                    text = "1. Doctor Specialty / Type",
                                    style = Typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(specialties) { spec ->
                                        val isSelected = specialty == spec
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { specialty = spec },
                                            label = { Text(spec, fontSize = 13.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = AppleBlue,
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = specialty,
                                    onValueChange = { specialty = it },
                                    label = { Text("Or Type Specialty") },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // 2. Location / Place
                                Text(
                                    text = "2. Area / City / Clinic Location",
                                    style = Typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = location,
                                    onValueChange = { location = it },
                                    placeholder = { Text("e.g. Bandra West, Mumbai or City Hospital") },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // 3. Preferred Date & Time
                                Text(
                                    text = "3. Preferred Date & Time",
                                    style = Typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(quickTimes) { t ->
                                        val isSelected = dateTime == t
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { dateTime = t },
                                            label = { Text(t, fontSize = 13.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = AppleBlue,
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = dateTime,
                                    onValueChange = { dateTime = it },
                                    placeholder = { Text("e.g. Tomorrow at 5:00 PM") },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // 4. Consultation Mode
                                Text(
                                    text = "4. Consultation Mode",
                                    style = Typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    ModeCard(
                                        title = "🏥 In-Person",
                                        subtitle = "Clinic visit",
                                        isSelected = mode.contains("in-person", ignoreCase = true),
                                        onClick = { mode = "in-person clinic visit" },
                                        modifier = Modifier.weight(1f)
                                    )
                                    ModeCard(
                                        title = "💻 Online",
                                        subtitle = "Video / Audio call",
                                        isSelected = mode.contains("online", ignoreCase = true),
                                        onClick = { mode = "online consultation" },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Submit Button
                                Button(
                                    onClick = {
                                        val newBooking = DoctorBookingManager.BookingState(
                                            specialty = specialty.trim(),
                                            location = location.trim(),
                                            dateTime = dateTime.trim(),
                                            mode = mode.trim(),
                                            isConfirmed = true
                                        )
                                        onBookManual(newBooking)
                                        showSuccessToast = true
                                        selectedTab = 0
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                ) {
                                    Text(
                                        text = "Confirm & Book Appointment",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailItemRow(label: String, value: String, emoji: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = label, style = Typography.bodySmall.copy(color = AppleTextMuted, fontSize = 13.sp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, style = Typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary, fontSize = 16.sp))
        }
        Text(text = emoji, fontSize = 20.sp)
    }
}

@Composable
private fun ModeCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) AppleBlueLight else AppleSurfaceWhite,
        border = BorderStroke(1.5.dp, if (isSelected) AppleBlue else AppleBorderSubtle),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = Typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) AppleBlue else AppleTextPrimary,
                    fontSize = 14.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = Typography.bodySmall.copy(
                    color = if (isSelected) AppleBlue.copy(alpha = 0.8f) else AppleTextMuted,
                    fontSize = 12.sp
                )
            )
        }
    }
}
