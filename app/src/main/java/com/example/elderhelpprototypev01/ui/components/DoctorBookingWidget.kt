package com.example.elderhelpprototypev01.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.elderhelpprototypev01.ai.DoctorBookingManager
import com.example.elderhelpprototypev01.model.UserProfile
import com.example.elderhelpprototypev01.ui.localization.Localization
import com.example.elderhelpprototypev01.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// ------------------------------------------------------------------
// Data Models for Visual Doctor Booking
// ------------------------------------------------------------------

data class SpecialtyItem(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val emoji: String,
    val subtitleEn: String,
    val subtitleHi: String,
    val color: Color
)

data class DoctorInfo(
    val id: String,
    val name: String,
    val specialtyId: String,
    val qualifications: String,
    val experience: String,
    val clinicLocation: String,
    val fee: String,
    val rating: String,
    val avatarEmoji: String
)

// ------------------------------------------------------------------
// Doctor Booking Catalog
// ------------------------------------------------------------------

val SPECIALTIES_CATALOG = listOf(
    SpecialtyItem(
        id = "gp",
        nameEn = "General Physician",
        nameHi = "सामान्य चिकित्सक",
        emoji = "👨‍⚕️",
        subtitleEn = "Fever, cough, cold, general health",
        subtitleHi = "बुखार, सर्दी, सामान्य स्वास्थ्य",
        color = Color(0xFF007AFF)
    ),
    SpecialtyItem(
        id = "cardio",
        nameEn = "Cardiologist",
        nameHi = "हृदय रोग विशेषज्ञ",
        emoji = "❤️",
        subtitleEn = "Heart health, BP, chest checkup",
        subtitleHi = "दिल, ब्लड प्रेशर, छाती की जांच",
        color = Color(0xFFFF3B30)
    ),
    SpecialtyItem(
        id = "ortho",
        nameEn = "Orthopedic",
        nameHi = "हड्डी रोग विशेषज्ञ",
        emoji = "🦴",
        subtitleEn = "Joints, knee pain, bone fracture",
        subtitleHi = "जोड़ों का दर्द, हड्डी की जांच",
        color = Color(0xFFFF9500)
    ),
    SpecialtyItem(
        id = "eye",
        nameEn = "Ophthalmologist",
        nameHi = "नेत्र विशेषज्ञ",
        emoji = "👁️",
        subtitleEn = "Eye vision, glasses, cataract",
        subtitleHi = "आँखों की जांच, चश्मा, मोतियाबिंद",
        color = Color(0xFF5856D6)
    ),
    SpecialtyItem(
        id = "neuro",
        nameEn = "Neurologist",
        nameHi = "तंत्रिका रोग विशेषज्ञ",
        emoji = "🧠",
        subtitleEn = "Headache, nerve care, memory",
        subtitleHi = "सिरदर्द, नसें, याददाश्त",
        color = Color(0xFFAF52DE)
    ),
    SpecialtyItem(
        id = "dentist",
        nameEn = "Dentist",
        nameHi = "दंत चिकित्सक",
        emoji = "🦷",
        subtitleEn = "Teeth cleaning, toothache, gums",
        subtitleHi = "दांत का दर्द, मसूड़े, सफाई",
        color = Color(0xFF34C759)
    ),
    SpecialtyItem(
        id = "derma",
        nameEn = "Dermatologist",
        nameHi = "त्वचा विशेषज्ञ",
        emoji = "🧴",
        subtitleEn = "Skin allergy, itching, hair fall",
        subtitleHi = "त्वचा की एलर्जी, खुजली, बाल",
        color = Color(0xFFFF2D55)
    ),
    SpecialtyItem(
        id = "pediatric",
        nameEn = "Pediatrician",
        nameHi = "बाल रोग विशेषज्ञ",
        emoji = "👶",
        subtitleEn = "Children care & vaccination",
        subtitleHi = "बच्चों की देखभाल व टीकाकरण",
        color = Color(0xFF00C7BE)
    )
)

val DOCTORS_CATALOG = listOf(
    // General Physician
    DoctorInfo("doc_gp_1", "Dr. Sunita Rao", "gp", "MBBS, MD (Medicine)", "15 Yrs Exp", "Bandra Medical Center", "₹500", "4.9", "👩‍⚕️"),
    DoctorInfo("doc_gp_2", "Dr. Rajesh Gupta", "gp", "MBBS", "12 Yrs Exp", "City Healthcare Clinic", "₹400", "4.8", "👨‍⚕️"),
    DoctorInfo("doc_gp_3", "Dr. Priya Verma", "gp", "MBBS, DNB", "8 Yrs Exp", "Apex Polyclinic", "₹450", "4.7", "👩‍⚕️"),

    // Cardiologist
    DoctorInfo("doc_cardio_1", "Dr. Amit Joshi", "cardio", "MD, DM (Cardiology)", "18 Yrs Exp", "Heart Care Institute", "₹800", "4.9", "👨‍⚕️"),
    DoctorInfo("doc_cardio_2", "Dr. Neha Kapoor", "cardio", "MD Cardiology", "14 Yrs Exp", "Bandra West Hospital", "₹750", "4.8", "👩‍⚕️"),

    // Orthopedic
    DoctorInfo("doc_ortho_1", "Dr. Vikram Malhotra", "ortho", "MS (Orthopedics)", "16 Yrs Exp", "Joint & Bone Center", "₹600", "4.9", "👨‍⚕️"),
    DoctorInfo("doc_ortho_2", "Dr. Sanjay Mehta", "ortho", "D.Ortho, MS", "11 Yrs Exp", "Metro Spine Clinic", "₹550", "4.7", "👨‍⚕️"),

    // Eye
    DoctorInfo("doc_eye_1", "Dr. Anjali Deshmukh", "eye", "MS (Ophthalmology)", "12 Yrs Exp", "Netra Eye Hospital", "₹500", "4.9", "👩‍⚕️"),
    DoctorInfo("doc_eye_2", "Dr. Rohan Nair", "eye", "DO, DNB (Eye)", "9 Yrs Exp", "Vision Care Clinic", "₹450", "4.8", "👨‍⚕️"),

    // Neuro
    DoctorInfo("doc_neuro_1", "Dr. Arvind Swamy", "neuro", "MD, DM (Neurology)", "20 Yrs Exp", "Neuro Health Care", "₹900", "5.0", "👨‍⚕️"),
    DoctorInfo("doc_neuro_2", "Dr. Meera Nambiar", "neuro", "MD Neurology", "13 Yrs Exp", "City Brain & Nerve Clinic", "₹850", "4.8", "👩‍⚕️"),

    // Dentist
    DoctorInfo("doc_dent_1", "Dr. Pooja Agarwal", "dentist", "BDS, MDS (Dental)", "10 Yrs Exp", "Smile Dental Clinic", "₹400", "4.9", "👩‍⚕️"),
    DoctorInfo("doc_dent_2", "Dr. Karan Shah", "dentist", "BDS (Dental Surgeon)", "7 Yrs Exp", "Bandra Dental Studio", "₹350", "4.7", "👨‍⚕️"),

    // Derma
    DoctorInfo("doc_derma_1", "Dr. Ritu Sharma", "derma", "MD (Dermatology)", "13 Yrs Exp", "Skin & Glow Clinic", "₹600", "4.9", "👩‍⚕️"),
    DoctorInfo("doc_derma_2", "Dr. Manish Jain", "derma", "DVD, DNB (Skin)", "8 Yrs Exp", "Aesthetic Skin Center", "₹500", "4.7", "👨‍⚕️"),

    // Pediatric
    DoctorInfo("doc_ped_1", "Dr. Shalini Patil", "pediatric", "MD (Pediatrics)", "14 Yrs Exp", "Shishu Child Clinic", "₹500", "4.9", "👩‍⚕️"),
    DoctorInfo("doc_ped_2", "Dr. Deepak Saxena", "pediatric", "DCH, MD", "11 Yrs Exp", "Little Care Hospital", "₹450", "4.8", "👨‍⚕️")
)

/**
 * DoctorBookingWidget
 *
 * Clickable Quick Task Widget displayed on the Home dashboard.
 * Shows active appointment details if present, or an invite to book.
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
        shape = RoundedCornerShape(22.dp),
        color = AppleSurfaceWhite,
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
                // Left Title & Subtitle Info Row
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(DoctorBlueBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👨‍⚕️", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Doctor Appointments",
                            style = Typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AppleTextPrimary,
                                fontSize = 16.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = if (appointment?.specialty != null) "Confirmed Visit • Ready" else "Verified Specialists",
                            style = Typography.bodySmall.copy(
                                color = if (appointment?.specialty != null) Color(0xFF059669) else AppleTextMuted,
                                fontSize = 12.sp,
                                fontWeight = if (appointment?.specialty != null) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Right Unbreakable CTA Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (appointment?.specialty != null) Color(0xFFECFDF5) else AppleBlueLight,
                    border = BorderStroke(1.dp, if (appointment?.specialty != null) Color(0xFFA7F3D0) else AppleBlueSubtle)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (appointment?.specialty != null) "Details" else "Book Now",
                            style = Typography.labelMedium.copy(
                                color = if (appointment?.specialty != null) Color(0xFF059669) else AppleBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp
                            ),
                            maxLines = 1,
                            softWrap = false
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            tint = if (appointment?.specialty != null) Color(0xFF059669) else AppleBlue,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = AppleBorderSubtle, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Details Content
            if (appointment?.specialty != null) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF0FDF4),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🩺", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = appointment.specialty ?: "General Physician",
                                style = Typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF166534),
                                    fontSize = 14.5.sp
                                )
                            )
                        }
                    }

                    if (!appointment.location.isNullOrBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📍", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = appointment.location ?: "",
                                style = Typography.bodyMedium.copy(
                                    color = AppleTextSecondary,
                                    fontSize = 13.5.sp
                                ),
                                maxLines = 1
                            )
                        }
                    }

                    if (!appointment.dateTime.isNullOrBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🕒", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = appointment.dateTime ?: "",
                                style = Typography.bodyMedium.copy(
                                    color = AppleBlue,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.5.sp
                                )
                            )
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Specialty Preview Avatar Group
                        Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                            listOf("👨‍⚕️", "👩‍⚕️", "❤️", "🦴").forEach { em ->
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(AppleBlueLight)
                                        .border(1.5.dp, Color.White, CircleShape)
                                ) {
                                    Text(text = em, fontSize = 13.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Find general physicians, cardiologists, orthopedics & more",
                            style = Typography.bodySmall.copy(
                                color = AppleTextSecondary,
                                fontSize = 12.5.sp,
                                lineHeight = 16.sp
                            ),
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}

// ------------------------------------------------------------------
// Redesigned Doctor Booking Modal (4-Step Visual Selection Flow)
// ------------------------------------------------------------------

/**
 * 4-Step Visual Doctor Booking Modal for Elderly & Low-Literacy Users:
 *
 * Flow:
 * Step 1: Choose Specialty (Large visual cards with emoji/icons)
 * Step 2: Choose Doctor (List of doctors for selected specialty)
 * Step 3: Appointment Details (Auto-filled profile data + large date/time chips)
 * Step 4: Confirmation Screen (Summary card + large Done button)
 */
@Composable
fun DoctorAppointmentModal(
    appointment: DoctorBookingManager.BookingState?,
    userProfile: UserProfile = UserProfile(),
    initialTab: Int? = null,
    currentLanguage: String = "English (India)",
    onDismiss: () -> Unit,
    onBookManual: (DoctorBookingManager.BookingState) -> Unit,
    onCancelAppointment: () -> Unit
) {
    val isHindi = currentLanguage.contains("Hindi") || currentLanguage.contains("हिंदी")
    val strings = Localization.getStrings(currentLanguage)

    // Flow Step State: 1 = Choose Specialty, 2 = Choose Doctor, 3 = Appointment Details, 4 = Confirmation
    // If user already has an active confirmed appointment, show step 0 (Active Booking Overview)
    var currentStep by remember {
        mutableIntStateOf(
            if (initialTab == 1) 1
            else if (appointment?.specialty != null && appointment.isConfirmed) 0
            else 1
        )
    }

    // Selected state across steps
    var selectedSpecialty by remember { mutableStateOf<SpecialtyItem?>(null) }
    var selectedDoctor by remember { mutableStateOf<DoctorInfo?>(null) }

    // Pre-populated patient details from userProfile
    var patientName by remember { mutableStateOf(userProfile.fullName) }
    var patientPhone by remember { mutableStateOf(userProfile.contactNumber) }
    var patientAge by remember { mutableStateOf(userProfile.age) }

    // Date & Time selection states
    val calendar = Calendar.getInstance()
    val dateSdf = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())

    val todayStr = "Today (${dateSdf.format(calendar.time)})"
    calendar.add(Calendar.DAY_OF_YEAR, 1)
    val tomorrowStr = "Tomorrow (${dateSdf.format(calendar.time)})"
    calendar.add(Calendar.DAY_OF_YEAR, 1)
    val in2DaysStr = dateSdf.format(calendar.time)
    calendar.add(Calendar.DAY_OF_YEAR, 1)
    val in3DaysStr = dateSdf.format(calendar.time)

    val dateOptions = listOf(tomorrowStr, todayStr, in2DaysStr, in3DaysStr)
    var selectedDate by remember { mutableStateOf(tomorrowStr) }

    val timeSlots = listOf("10:00 AM", "11:30 AM", "04:30 PM", "06:00 PM", "07:30 PM")
    var selectedTime by remember { mutableStateOf("10:00 AM") }

    var selectedMode by remember { mutableStateOf("In-Person Clinic Visit") }

    // Last confirmed booking summary (for step 4)
    var lastConfirmedBooking by remember { mutableStateOf<DoctorBookingManager.BookingState?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 36.dp),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = AppleCanvasBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (currentStep in 2..3) {
                            IconButton(
                                onClick = { currentStep -= 1 },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(AppleBorderSubtle.copy(alpha = 0.5f))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = AppleTextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(DoctorBlueBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👨‍⚕️", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                        }

                        Column {
                            val headerTitle = when (currentStep) {
                                0 -> if (isHindi) "सक्रिय अपॉइंटमेंट" else "Active Booking"
                                1 -> if (isHindi) "विशेषज्ञता चुनें" else "Choose Specialty"
                                2 -> if (isHindi) "डॉक्टर चुनें" else "Choose Doctor"
                                3 -> if (isHindi) "अपॉइंटमेंट विवरण" else "Appointment Details"
                                4 -> if (isHindi) "अपॉइंटमेंट कन्फर्म!" else "Booking Confirmed!"
                                else -> strings.bookDoctor
                            }
                            Text(
                                text = headerTitle,
                                style = Typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AppleTextPrimary,
                                    fontSize = 20.sp
                                )
                            )
                            val stepSubtitle = when (currentStep) {
                                0 -> if (isHindi) "आपका वर्तमान निर्धारित दौरा" else "Your scheduled healthcare visit"
                                1 -> if (isHindi) "चरण 1/4: डॉक्टर का प्रकार चुनें" else "Step 1 of 4: Select doctor type"
                                2 -> if (isHindi) "चरण 2/4: अनुभवी डॉक्टर चुनें" else "Step 2 of 4: Select your doctor"
                                3 -> if (isHindi) "चरण 3/4: समय और तारीख चुनें" else "Step 3 of 4: Select time & verify details"
                                4 -> if (isHindi) "चरण 4/4: आपकी बुकिंग सफल रही" else "Step 4 of 4: Successfully scheduled"
                                else -> ""
                            }
                            Text(
                                text = stepSubtitle,
                                style = Typography.bodySmall.copy(color = AppleTextMuted, fontSize = 12.sp)
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

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Bar indicator for steps 1..4
                if (currentStep in 1..4) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (i in 1..4) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        if (i <= currentStep) DoctorBlueIcon else AppleBorderSubtle
                                    )
                            )
                        }
                    }
                }

                // Step Content
                Box(modifier = Modifier.weight(1f)) {
                    when (currentStep) {
                        0 -> {
                            // --------------------------------------------------
                            // STEP 0: Active Booking Overview
                            // --------------------------------------------------
                            ActiveBookingView(
                                appointment = appointment,
                                isHindi = isHindi,
                                onNewBooking = { currentStep = 1 },
                                onCancel = {
                                    onCancelAppointment()
                                    currentStep = 1
                                }
                            )
                        }

                        1 -> {
                            // --------------------------------------------------
                            // STEP 1: Choose Specialty (Large Visual Cards)
                            // --------------------------------------------------
                            SpecialtySelectionView(
                                specialties = SPECIALTIES_CATALOG,
                                isHindi = isHindi,
                                onSelect = { specialty ->
                                    selectedSpecialty = specialty
                                    currentStep = 2
                                }
                            )
                        }

                        2 -> {
                            // --------------------------------------------------
                            // STEP 2: Choose Doctor (Cards with Name, Exp, Fee)
                            // --------------------------------------------------
                            val specialtyId = selectedSpecialty?.id ?: "gp"
                            val filteredDoctors = DOCTORS_CATALOG.filter { it.specialtyId == specialtyId }
                                .ifEmpty { DOCTORS_CATALOG.take(3) }

                            DoctorSelectionView(
                                specialty = selectedSpecialty,
                                doctors = filteredDoctors,
                                isHindi = isHindi,
                                onSelectDoctor = { doc ->
                                    selectedDoctor = doc
                                    currentStep = 3
                                }
                            )
                        }

                        3 -> {
                            // --------------------------------------------------
                            // STEP 3: Appointment Details (Auto-filled User Data)
                            // --------------------------------------------------
                            AppointmentDetailsView(
                                doctor = selectedDoctor,
                                specialty = selectedSpecialty,
                                isHindi = isHindi,
                                patientName = patientName,
                                onPatientNameChange = { patientName = it },
                                patientPhone = patientPhone,
                                onPatientPhoneChange = { patientPhone = it },
                                patientAge = patientAge,
                                onPatientAgeChange = { patientAge = it },
                                dateOptions = dateOptions,
                                selectedDate = selectedDate,
                                onSelectDate = { selectedDate = it },
                                timeSlots = timeSlots,
                                selectedTime = selectedTime,
                                onSelectTime = { selectedTime = it },
                                selectedMode = selectedMode,
                                onSelectMode = { selectedMode = it },
                                onConfirmBooking = {
                                    val booking = DoctorBookingManager.BookingState(
                                        specialty = selectedSpecialty?.let { if (isHindi) it.nameHi else it.nameEn } ?: "General Physician",
                                        location = "${selectedDoctor?.clinicLocation ?: "Bandra Medical Clinic"} (${selectedDoctor?.name ?: "Dr. Sunita Rao"})",
                                        dateTime = "$selectedDate, $selectedTime",
                                        mode = selectedMode,
                                        isConfirmed = true
                                    )
                                    lastConfirmedBooking = booking
                                    onBookManual(booking)
                                    currentStep = 4
                                }
                            )
                        }

                        4 -> {
                            // --------------------------------------------------
                            // STEP 4: Confirmation Screen (Summary + Done Button)
                            // --------------------------------------------------
                            BookingConfirmationView(
                                booking = lastConfirmedBooking ?: appointment,
                                doctor = selectedDoctor,
                                patientName = patientName,
                                patientPhone = patientPhone,
                                patientAge = patientAge,
                                isHindi = isHindi,
                                onDone = onDismiss
                            )
                        }
                    }
                }
            }
        }
    }
}

// ------------------------------------------------------------------
// Sub-Views for Each Step
// ------------------------------------------------------------------

/**
 * Step 1: Visual Specialty Selection Grid
 */
@Composable
private fun SpecialtySelectionView(
    specialties: List<SpecialtyItem>,
    isHindi: Boolean,
    onSelect: (SpecialtyItem) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text(
            text = if (isHindi) "कृपया वह विशेषज्ञता चुनें जो आपकी ज़रूरत के अनुसार हो:" else "Tap a specialty card below to choose your doctor:",
            style = Typography.bodyMedium.copy(
                color = AppleTextSecondary,
                fontSize = 14.sp
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 2-Column Grid of Large Visual Specialty Cards
        specialties.chunked(2).forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { item ->
                    SpecialtyCard(
                        item = item,
                        isHindi = isHindi,
                        onClick = { onSelect(item) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecialtyCard(
    item: SpecialtyItem,
    isHindi: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.2.dp, item.color.copy(alpha = 0.3f)),
        modifier = modifier
            .semantics {
                contentDescription = "${if (isHindi) item.nameHi else item.nameEn}. Tap to select."
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Large Visual Emoji Icon Box
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(item.color.copy(alpha = 0.12f))
                    .border(1.5.dp, item.color.copy(alpha = 0.4f), CircleShape)
            ) {
                Text(text = item.emoji, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Specialty Name
            Text(
                text = if (isHindi) item.nameHi else item.nameEn,
                style = Typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = AppleTextPrimary,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle Description
            Text(
                text = if (isHindi) item.subtitleHi else item.subtitleEn,
                style = Typography.bodySmall.copy(
                    color = AppleTextMuted,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                ),
                maxLines = 2,
                minLines = 2
            )
        }
    }
}

/**
 * Step 2: Choose Doctor List
 */
@Composable
private fun DoctorSelectionView(
    specialty: SpecialtyItem?,
    doctors: List<DoctorInfo>,
    isHindi: Boolean,
    onSelectDoctor: (DoctorInfo) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Specialty summary banner
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = (specialty?.color ?: AppleBlue).copy(alpha = 0.1f),
                border = BorderStroke(1.dp, (specialty?.color ?: AppleBlue).copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = specialty?.emoji ?: "👨‍⚕️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isHindi)
                            "विशेषज्ञता: ${specialty?.nameHi ?: "सामान्य चिकित्सक"}"
                        else
                            "Specialty: ${specialty?.nameEn ?: "General Physician"}",
                        style = Typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = specialty?.color ?: AppleBlue,
                            fontSize = 15.sp
                        )
                    )
                }
            }
        }

        items(doctors) { doc ->
            DoctorCard(
                doctor = doc,
                isHindi = isHindi,
                onSelect = { onSelectDoctor(doc) }
            )
        }
    }
}

@Composable
private fun DoctorCard(
    doctor: DoctorInfo,
    isHindi: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, AppleBorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(DoctorBlueBg)
                    .border(1.5.dp, DoctorBlueIcon.copy(alpha = 0.4f), CircleShape)
            ) {
                Text(text = doctor.avatarEmoji, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = doctor.name,
                    style = Typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppleTextPrimary,
                        fontSize = 17.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${doctor.qualifications} • ${doctor.experience}",
                    style = Typography.bodySmall.copy(color = AppleTextSecondary, fontSize = 12.sp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "📍 ${doctor.clinicLocation}",
                    style = Typography.bodySmall.copy(color = AppleTextMuted, fontSize = 12.sp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⭐ ${doctor.rating}",
                        style = Typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFFFF9500))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${doctor.fee}",
                        style = Typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF34C759))
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Select button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AppleBlueLight)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Select",
                    tint = AppleBlue,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Step 3: Appointment Details (Auto-filled Profile + Large Selectors)
 */
@Composable
private fun AppointmentDetailsView(
    doctor: DoctorInfo?,
    specialty: SpecialtyItem?,
    isHindi: Boolean,
    patientName: String,
    onPatientNameChange: (String) -> Unit,
    patientPhone: String,
    onPatientPhoneChange: (String) -> Unit,
    patientAge: String,
    onPatientAgeChange: (String) -> Unit,
    dateOptions: List<String>,
    selectedDate: String,
    onSelectDate: (String) -> Unit,
    timeSlots: List<String>,
    selectedTime: String,
    onSelectTime: (String) -> Unit,
    selectedMode: String,
    onSelectMode: (String) -> Unit,
    onConfirmBooking: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Selected Doctor Summary Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, AppleBlue.copy(alpha = 0.3f)),
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = doctor?.avatarEmoji ?: "👨‍⚕️", fontSize = 30.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = doctor?.name ?: "Dr. Sunita Rao",
                        style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    )
                    Text(
                        text = "${specialty?.let { if (isHindi) it.nameHi else it.nameEn } ?: "General Physician"} • ${doctor?.clinicLocation ?: "Bandra Center"}",
                        style = Typography.bodySmall.copy(color = AppleTextSecondary, fontSize = 12.sp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Patient Details (Auto-filled from Profile)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, AppleBorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("👤", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "मरीज़ का विवरण (प्रोफ़ाइल से भरा हुआ)" else "PATIENT DETAILS (PRE-FILLED)",
                        style = Typography.labelMedium.copy(
                            color = AppleBlue,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 11.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Patient Name
                Text(
                    text = if (isHindi) "मरीज़ का नाम" else "Patient Name",
                    style = Typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary)
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = patientName,
                    onValueChange = onPatientNameChange,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Phone Number
                    Column(modifier = Modifier.weight(1.3f)) {
                        Text(
                            text = if (isHindi) "फोन नंबर" else "Phone",
                            style = Typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = patientPhone,
                            onValueChange = onPatientPhoneChange,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    // Age
                    Column(modifier = Modifier.weight(0.7f)) {
                        Text(
                            text = if (isHindi) "उम्र" else "Age",
                            style = Typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = patientAge,
                            onValueChange = onPatientAgeChange,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Select Date (Large visual chips)
        Text(
            text = if (isHindi) "तारीख चुनें" else "Select Date",
            style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary, fontSize = 16.sp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(dateOptions) { d ->
                val isSelected = selectedDate == d
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectDate(d) },
                    label = { Text(d, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AppleBlue,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.height(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Select Time (Large visual chips)
        Text(
            text = if (isHindi) "समय चुनें" else "Select Time Slot",
            style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary, fontSize = 16.sp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(timeSlots) { t ->
                val isSelected = selectedTime == t
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectTime(t) },
                    label = { Text(t, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AppleBlue,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.height(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Consultation Mode
        Text(
            text = if (isHindi) "परामर्श का तरीका" else "Consultation Mode",
            style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary, fontSize = 16.sp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ModeSelectionCard(
                title = if (isHindi) "🏥 क्लिनिक विज़िट" else "🏥 In-Person",
                subtitle = if (isHindi) "क्लिनिक जाएं" else "Visit clinic",
                isSelected = selectedMode.contains("In-Person", ignoreCase = true),
                onClick = { onSelectMode("In-Person Clinic Visit") },
                modifier = Modifier.weight(1f)
            )
            ModeSelectionCard(
                title = if (isHindi) "💻 ऑनलाइन कॉल" else "💻 Online",
                subtitle = if (isHindi) "वीडियो / ऑडियो कॉल" else "Video/audio call",
                isSelected = selectedMode.contains("Online", ignoreCase = true),
                onClick = { onSelectMode("Online Consultation") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Confirm Button
        Button(
            onClick = onConfirmBooking,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text(
                text = if (isHindi) "कन्फर्म करें और अपॉइंटमेंट बुक करें →" else "Confirm & Book Appointment →",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun ModeSelectionCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) AppleBlueLight else Color.White,
        border = BorderStroke(1.5.dp, if (isSelected) AppleBlue else AppleBorderSubtle),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = Typography.titleMedium.copy(
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
                    fontSize = 11.sp
                )
            )
        }
    }
}

/**
 * Step 4: Confirmation Screen
 */
@Composable
private fun BookingConfirmationView(
    booking: DoctorBookingManager.BookingState?,
    doctor: DoctorInfo?,
    patientName: String,
    patientPhone: String,
    patientAge: String,
    isHindi: Boolean,
    onDone: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Success Checkmark Badge
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F5E9))
                .border(2.5.dp, Color(0xFF34C759), CircleShape)
        ) {
            Text("✅", fontSize = 38.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = if (isHindi) "अपॉइंटमेंट कन्फर्म हो गया!" else "Appointment Confirmed!",
            style = Typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32),
                fontSize = 22.sp
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = if (isHindi) "SMS विवरण आपके मोबाइल पर भेज दिया गया है" else "Confirmation & SMS sent to your phone",
            style = Typography.bodyMedium.copy(color = AppleTextMuted, fontSize = 13.sp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Summary Card
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 3.dp,
            border = BorderStroke(1.2.dp, Color(0xFF34C759).copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
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
                        Text(
                            text = if (isHindi) "कन्फर्म" else "CONFIRMED",
                            style = Typography.labelSmall.copy(
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = "ID: #SAH-${(System.currentTimeMillis() % 10000).toString().padStart(4, '0')}",
                        style = Typography.bodySmall.copy(color = AppleTextMuted, fontSize = 12.sp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                DetailItemRow(
                    label = if (isHindi) "डॉक्टर" else "Doctor",
                    value = doctor?.name ?: "Dr. Sunita Rao",
                    emoji = doctor?.avatarEmoji ?: "👨‍⚕️"
                )
                HorizontalDivider(color = AppleBorderSubtle, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                DetailItemRow(
                    label = if (isHindi) "विशेषज्ञता" else "Specialty",
                    value = booking?.specialty ?: "General Physician",
                    emoji = "🩺"
                )
                HorizontalDivider(color = AppleBorderSubtle, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                DetailItemRow(
                    label = if (isHindi) "मरीज़" else "Patient",
                    value = "$patientName ($patientAge yrs) • $patientPhone",
                    emoji = "👤"
                )
                HorizontalDivider(color = AppleBorderSubtle, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                DetailItemRow(
                    label = if (isHindi) "तारीख और समय" else "Date & Time",
                    value = booking?.dateTime ?: "Tomorrow at 10:00 AM",
                    emoji = "🕒"
                )
                HorizontalDivider(color = AppleBorderSubtle, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                DetailItemRow(
                    label = if (isHindi) "स्थान / तरीका" else "Location & Mode",
                    value = "${booking?.location ?: "Bandra Center"} (${booking?.mode ?: "In-Person"})",
                    emoji = "📍"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Large Done Button
        Button(
            onClick = onDone,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text(
                text = if (isHindi) "हो गया (होम पर जाएं)" else "Done (Return to Home)",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

/**
 * Step 0: Active Booking Detail View
 */
@Composable
private fun ActiveBookingView(
    appointment: DoctorBookingManager.BookingState?,
    isHindi: Boolean,
    onNewBooking: () -> Unit,
    onCancel: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
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
                            Text(
                                text = if (appointment.isConfirmed) "CONFIRMED" else "IN PROGRESS",
                                style = Typography.labelSmall.copy(
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = "Booking ID: #SAH-8821",
                            style = Typography.bodySmall.copy(color = AppleTextMuted, fontSize = 12.sp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    DetailItemRow(label = if (isHindi) "विशेषज्ञता" else "Specialty", value = appointment.specialty ?: "General Physician", emoji = "🩺")
                    HorizontalDivider(color = AppleBorderSubtle, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                    DetailItemRow(label = if (isHindi) "स्थान / क्लिनिक" else "Location / Clinic", value = appointment.location ?: "Central Medical Clinic", emoji = "📍")
                    HorizontalDivider(color = AppleBorderSubtle, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                    DetailItemRow(label = if (isHindi) "तारीख और समय" else "Preferred Date & Time", value = appointment.dateTime ?: "Tomorrow at 5:00 PM", emoji = "🕒")
                    HorizontalDivider(color = AppleBorderSubtle, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                    DetailItemRow(
                        label = if (isHindi) "परामर्श का तरीका" else "Consultation Mode",
                        value = if (appointment.mode?.contains("online", ignoreCase = true) == true) "Online Consultation" else "In-Person Clinic Visit",
                        emoji = if (appointment.mode?.contains("online", ignoreCase = true) == true) "💻" else "🏥"
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Buttons: Book New & Cancel
                    Button(
                        onClick = onNewBooking,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isHindi) "नया अपॉइंटमेंट बुक करें" else "Book Another Appointment", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onCancel,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF3B30)),
                        border = BorderStroke(1.dp, Color(0xFFFF3B30).copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isHindi) "यह अपॉइंटमेंट रद्द करें" else "Cancel This Appointment", fontWeight = FontWeight.Bold)
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
                        text = if (isHindi) "कोई सक्रिय अपॉइंटमेंट नहीं" else "No Active Appointment",
                        style = Typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppleTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isHindi) "नया अपॉइंटमेंट बुक करने के लिए नीचे दिए गए बटन पर टैप करें।" else "Tap below to start visual doctor booking.",
                        style = Typography.bodyMedium.copy(
                            color = AppleTextSecondary,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = onNewBooking,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppleBlue)
                    ) {
                        Text(if (isHindi) "डॉक्टर बुक करें" else "Book Doctor Now", fontWeight = FontWeight.Bold)
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
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = Typography.bodySmall.copy(color = AppleTextMuted, fontSize = 12.sp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, style = Typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary, fontSize = 15.sp))
        }
        Text(text = emoji, fontSize = 20.sp)
    }
}
