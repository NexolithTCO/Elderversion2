package com.example.elderhelpprototypev01.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.elderhelpprototypev01.SahaayViewModel
import com.example.elderhelpprototypev01.model.BillPayment
import com.example.elderhelpprototypev01.model.BillType
import com.example.elderhelpprototypev01.ui.components.*
import com.example.elderhelpprototypev01.ui.localization.Localization
import com.example.elderhelpprototypev01.ui.theme.*
import com.example.elderhelpprototypev01.ui.voice.VoiceScreen

@Composable
fun SahaayHomeScreen(
    modifier: Modifier = Modifier,
    overlayRefreshTick: Int = 0,
    viewModel: SahaayViewModel? = null,
    initialTab: Int = 0
) {
    val context = LocalContext.current

    // Hoisted persistent state across tab switches and recompositions
    var currentLanguage by rememberSaveable { mutableStateOf("English (India)") }
    var selectedTab by rememberSaveable { mutableIntStateOf(initialTab) }
    var activeMessage by remember { mutableStateOf<String?>(null) }

    // Modals
    var showProfileModal by remember { mutableStateOf(false) }
    var showDoctorModal by remember { mutableStateOf(false) }
    var showPensionForm by remember { mutableStateOf(false) }
    var showBillSelection by remember { mutableStateOf(false) }
    var selectedBillType by remember { mutableStateOf<BillType?>(null) }
    var isSosModalOpen by remember { mutableStateOf(false) }
    var showEmergencyServicesModal by remember { mutableStateOf(false) }
    var showSearchModal by remember { mutableStateOf(false) }

    // Reactive State from ViewModel
    val bookedAppointment by (viewModel?.bookedAppointment?.collectAsStateWithLifecycle()
        ?: remember { mutableStateOf(null) })
    val userProfile by (viewModel?.userProfile?.collectAsStateWithLifecycle()
        ?: remember { mutableStateOf(com.example.elderhelpprototypev01.model.UserProfile()) })
    val vmLanguage by (viewModel?.currentLanguage?.collectAsStateWithLifecycle()
        ?: remember { mutableStateOf("English (India)") })

    // Sync currentLanguage FROM ViewModel on first composition (picks up onboarding choice)
    LaunchedEffect(vmLanguage) {
        if (vmLanguage != currentLanguage) {
            currentLanguage = vmLanguage
        }
    }

    // Sync language preference to ViewModel whenever user toggles it from the Home header
    LaunchedEffect(currentLanguage) {
        viewModel?.setLanguage(currentLanguage)
    }

    val scrollState = rememberScrollState()
    val strings = Localization.getStrings(currentLanguage)

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                currentLanguage = currentLanguage,
                onTabSelected = { index ->
                    selectedTab = index
                }
            )
        },
        containerColor = AppleCanvasBg,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Crossfade(
            targetState = selectedTab,
            label = "tabCrossfade",
            modifier = Modifier.padding(innerPadding)
        ) { tabIndex ->
            when (tabIndex) {
                1 -> {
                    // Tab Index 1: Voice Assistant Screen
                    if (viewModel != null) {
                        VoiceScreen(
                            viewModel = viewModel,
                            onNavigateBack = { selectedTab = 0 },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🎙️ Voice Assistant",
                                style = Typography.headlineMedium.copy(color = AppleTextMuted)
                            )
                        }
                    }
                }
                2 -> {
                    // Tab Index 2: Transactions Screen
                    if (viewModel != null) {
                        TransactionsScreen(
                            viewModel = viewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🧾 Transactions",
                                style = Typography.headlineMedium.copy(color = AppleTextMuted)
                            )
                        }
                    }
                }
                3 -> {
                    // Tab Index 3: Settings Screen
                    SettingsScreen(
                        currentLanguage = currentLanguage,
                        userProfile = userProfile,
                        onEditProfileClick = { showProfileModal = true },
                        onLanguageChange = { newLang ->
                            currentLanguage = newLang
                        }
                    )
                }
                else -> {
                    // Tab Index 0: Main Dashboard Screen
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AppleCanvasBg)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(horizontal = 18.dp)
                                .padding(top = 16.dp, bottom = 28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            // 1. Top Header Row with Clickable Profile Button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Profile Button & Greeting
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { showProfileModal = true }
                                        .padding(4.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(AppleBlueLight)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Profile",
                                            tint = AppleBlue,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (userProfile.fullName.isNotBlank()) {
                                                    "${strings.greetingPrefix} ${userProfile.fullName}"
                                                } else {
                                                    strings.welcomePill
                                                },
                                                style = Typography.titleLarge.copy(
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = AppleTextPrimary
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit Profile",
                                                tint = AppleBlue,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                        Text(
                                            text = "Sahaay Voice Assistant",
                                            style = Typography.bodyMedium.copy(
                                                fontSize = 13.sp,
                                                color = AppleTextMuted
                                            )
                                        )
                                    }
                                }

                                // Quick Action Icons + Language Toggle
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Language Toggle Pill: taps between English and Hindi
                                    LanguageTogglePill(
                                        currentLanguage = currentLanguage,
                                        onToggle = {
                                            currentLanguage = if (currentLanguage.contains("Hindi")) {
                                                "English (India)"
                                            } else {
                                                "Hindi (हिंदी)"
                                            }
                                        }
                                    )
                                    IconButton(onClick = {
                                        showSearchModal = true
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Search",
                                            tint = AppleTextPrimary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    IconButton(onClick = {
                                        Toast.makeText(context, "Notifications clicked", Toast.LENGTH_SHORT).show()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Notifications,
                                            contentDescription = "Notifications",
                                            tint = AppleTextPrimary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 2. Featured Quick Task: Doctor Booking Widget
                            DoctorBookingWidget(
                                appointment = bookedAppointment,
                                onClick = { showDoctorModal = true },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // 4. Hero Voice Assistant Banner (Direct Voice Access)
                            MicrophoneButton(
                                isListening = false,
                                currentLanguage = currentLanguage,
                                onClick = {
                                    selectedTab = 1 // Switch to Voice Screen directly
                                    viewModel?.startListening()
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // 5. Expanded Pay Bills Section (Electricity, Water, Mobile Recharge)
                            PayBillsSection(
                                onCategoryClick = { type ->
                                    selectedBillType = type
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // 6. Quick Services Grid
                            QuickActionsSection(
                                currentLanguage = currentLanguage,
                                onActionClick = { action ->
                                    when (action.id) {
                                        "doctor" -> showDoctorModal = true
                                        "bills" -> showBillSelection = true
                                        "forms" -> showPensionForm = true
                                        "emergency" -> showEmergencyServicesModal = true
                                        "sos" -> isSosModalOpen = true
                                        else -> {
                                            activeMessage = "${action.title} selected"
                                            Toast.makeText(context, "${action.title} clicked", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Bottom Toast Feedback Banner
                        AnimatedVisibility(
                            visible = activeMessage != null,
                            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp, start = 20.dp, end = 20.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = AppleTextPrimary,
                                shadowElevation = 8.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = activeMessage ?: "",
                                        style = Typography.bodyLarge.copy(
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    TextButton(onClick = { activeMessage = null }) {
                                        Text(
                                            text = "OK",
                                            color = Color(0xFF64D2FF),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Modal Dialogs
                        if (showProfileModal) {
                            ProfileModal(
                                profile = userProfile,
                                onDismiss = { showProfileModal = false },
                                onSaveProfile = { updated ->
                                    viewModel?.updateUserProfile(updated)
                                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                        if (showDoctorModal) {
                            DoctorAppointmentModal(
                                appointment = bookedAppointment,
                                userProfile = userProfile,
                                currentLanguage = currentLanguage,
                                onDismiss = { showDoctorModal = false },
                                onBookManual = { newBooking ->
                                    viewModel?.updateAppointment(newBooking)
                                    Toast.makeText(context, "Doctor appointment scheduled!", Toast.LENGTH_SHORT).show()
                                },
                                onCancelAppointment = {
                                    viewModel?.cancelAppointment()
                                    Toast.makeText(context, "Appointment cancelled.", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                        if (showPensionForm) {
                            PensionFormScreen(
                                userProfile = userProfile,
                                onDismiss = { showPensionForm = false }
                            )
                        }

                        if (showBillSelection) {
                            BillSelectionModal(
                                onDismiss = { showBillSelection = false },
                                onSelectBillType = { type ->
                                    selectedBillType = type
                                }
                            )
                        }

                        selectedBillType?.let { type ->
                            BillPaymentModal(
                                billType = type,
                                userProfile = userProfile,
                                onDismiss = { selectedBillType = null },
                                onPaymentSuccess = { payment ->
                                    viewModel?.recordBillPayment(payment)
                                    Toast.makeText(context, "₹${payment.amount} paid successfully!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                        if (showSearchModal) {
                            GlobalSearchModal(
                                onDismiss = { showSearchModal = false },
                                currentLanguage = currentLanguage,
                                onNavigateAction = { result ->
                                    when (result.actionType) {
                                        SearchActionType.OPEN_DOCTOR -> showDoctorModal = true
                                        SearchActionType.OPEN_BILL_TYPE -> result.billType?.let { selectedBillType = it }
                                        SearchActionType.OPEN_BILL_SELECTION -> showBillSelection = true
                                        SearchActionType.OPEN_PENSION_FORM -> showPensionForm = true
                                        SearchActionType.OPEN_EMERGENCY -> showEmergencyServicesModal = true
                                        SearchActionType.OPEN_PROFILE -> showProfileModal = true
                                        SearchActionType.SWITCH_TAB -> result.tabIndex?.let { selectedTab = it }
                                    }
                                }
                            )
                        }

                        if (showEmergencyServicesModal) {
                            EmergencyServicesModal(
                                onDismiss = { showEmergencyServicesModal = false },
                                contactName = userProfile.emergencyContactDisplayName,
                                contactNumber = userProfile.emergencyContactPhone,
                                currentLanguage = currentLanguage
                            )
                        }

                        if (isSosModalOpen) {
                            EmergencySosModal(
                                onDismiss = { isSosModalOpen = false },
                                onEmergencyTriggered = {
                                    activeMessage = "Emergency call initiated to ${userProfile.emergencyContactDisplayName} (${userProfile.emergencyContactPhone})"
                                },
                                contactName = userProfile.emergencyContactDisplayName,
                                contactNumber = userProfile.emergencyContactPhone,
                                currentLanguage = currentLanguage
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SahaayHomeScreenPreview() {
    ElderHelpPrototypeV01Theme {
        SahaayHomeScreen()
    }
}

/**
 * LanguageTogglePill
 *
 * A compact, accessible pill button that lives in the Home screen top header.
 * Displays "A" when the app is in English and "अ" when the app is in Hindi.
 * Tapping it calls [onToggle] to switch the global language state.
 *
 * Accessibility: A `contentDescription` is attached so TalkBack announces
 * "Switch to Hindi" / "Switch to English" appropriately.
 */
@Composable
fun LanguageTogglePill(
    currentLanguage: String,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isHindi = currentLanguage.contains("Hindi")
    val label = if (isHindi) "अ" else "A"
    val accessibilityLabel = if (isHindi) "Switch to English" else "हिंदी में बदलें"

    Surface(
        shape = RoundedCornerShape(50),
        color = if (isHindi) AppleBlue else Color.Transparent,
        border = if (!isHindi) {
            androidx.compose.foundation.BorderStroke(1.5.dp, AppleBlue.copy(alpha = 0.6f))
        } else null,
        modifier = modifier
            .semantics { contentDescription = accessibilityLabel }
            .clickable(onClick = onToggle)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 5.dp)
                .defaultMinSize(minWidth = 36.dp)
        ) {
            Text(
                text = label,
                style = Typography.labelLarge.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isHindi) Color.White else AppleBlue
                )
            )
        }
    }
}
